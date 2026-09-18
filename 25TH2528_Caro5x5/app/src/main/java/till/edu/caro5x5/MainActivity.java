package till.edu.caro5x5;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // =========================================================
    // CẤU HÌNH
    // =========================================================

    private static final int SIZE = 5;

    // 0 = trống
    // 1 = X = người
    // 2 = O = máy
    private final int[][] board = new int[SIZE][SIZE];

    private final Button[][] buttons =
            new Button[SIZE][SIZE];


    // =========================================================
    // TRẠNG THÁI GAME
    // =========================================================

    // 1 = người
    // 2 = máy
    private int currentPlayer = 1;

    private boolean gameOver = false;

    /*
        1 = Dễ
        2 = Trung bình
        3 = Khó
    */
    private int gameMode = 1;


    // =========================================================
    // GIAO DIỆN
    // =========================================================

    private LinearLayout modeLayout;

    private Button btnEasy;
    private Button btnMedium;
    private Button btnHard;

    private TextView tvMode;
    private TextView tvTurn;

    private GridLayout boardLayout;

    private Button btnBackMode;
    private Button btnRestart;


    // =========================================================
    // HỖ TRỢ AI
    // =========================================================

    private final Random random = new Random();

    private final Handler handler =
            new Handler(Looper.getMainLooper());


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // -----------------------------------------------------
        // Ánh xạ giao diện
        // -----------------------------------------------------

        modeLayout =
                findViewById(R.id.modeLayout);

        btnEasy =
                findViewById(R.id.btnEasy);

        btnMedium =
                findViewById(R.id.btnMedium);

        btnHard =
                findViewById(R.id.btnHard);

        tvMode =
                findViewById(R.id.tvMode);

        tvTurn =
                findViewById(R.id.tvTurn);

        boardLayout =
                findViewById(R.id.board);

        btnBackMode =
                findViewById(R.id.btnBackMode);

        btnRestart =
                findViewById(R.id.btnRestart);


        // -----------------------------------------------------
        // Tạo bàn cờ
        // -----------------------------------------------------

        createBoard();


        // -----------------------------------------------------
        // NÚT DỄ
        // -----------------------------------------------------

        btnEasy.setOnClickListener(v -> {

            gameMode = 1;

            startGame("DỄ");
        });


        // -----------------------------------------------------
        // NÚT TRUNG BÌNH
        // -----------------------------------------------------

        btnMedium.setOnClickListener(v -> {

            gameMode = 2;

            startGame("TRUNG BÌNH");
        });


        // -----------------------------------------------------
        // NÚT KHÓ
        // -----------------------------------------------------

        btnHard.setOnClickListener(v -> {

            gameMode = 3;

            startGame("KHÓ");
        });


        // -----------------------------------------------------
        // NÚT CHƠI LẠI
        // -----------------------------------------------------

        btnRestart.setOnClickListener(v -> {

            restartGame();
        });


        // -----------------------------------------------------
        // NÚT QUAY LẠI
        // -----------------------------------------------------

        btnBackMode.setOnClickListener(v -> {

            backToModeSelection();
        });
    }


    // =========================================================
    // BẮT ĐẦU GAME
    // =========================================================

    private void startGame(String mode) {

        modeLayout.setVisibility(View.GONE);

        tvMode.setVisibility(View.VISIBLE);

        tvMode.setText("Chế độ: " + mode);

        tvTurn.setVisibility(View.VISIBLE);

        boardLayout.setVisibility(View.VISIBLE);

        btnBackMode.setVisibility(View.VISIBLE);

        btnRestart.setVisibility(View.VISIBLE);

        restartGame();
    }


    // =========================================================
    // TẠO BÀN CỜ
    // =========================================================

    private void createBoard() {

        for (int row = 0; row < SIZE; row++) {

            for (int col = 0; col < SIZE; col++) {

                String name =
                        "b" + row + col;

                int id =
                        getResources().getIdentifier(
                                name,
                                "id",
                                getPackageName()
                        );

                buttons[row][col] =
                        findViewById(id);


                final int r = row;
                final int c = col;


                buttons[row][col]
                        .setOnClickListener(v -> {

                            playerMove(r, c);
                        });
            }
        }
    }


    // =========================================================
    // NGƯỜI CHƠI ĐÁNH
    // =========================================================

    private void playerMove(
            int row,
            int col) {

        // Không cho đánh nếu game kết thúc
        if (gameOver) {
            return;
        }


        // Không phải lượt người
        if (currentPlayer != 1) {
            return;
        }


        // Ô đã có quân
        if (board[row][col] != 0) {
            return;
        }


        // -----------------------------------------------------
        // Người đặt X
        // -----------------------------------------------------

        board[row][col] = 1;

        buttons[row][col].setText("X");

        buttons[row][col].setTextColor(
                Color.RED
        );


        // -----------------------------------------------------
        // Kiểm tra thắng
        // -----------------------------------------------------

        if (checkWin(row, col)) {

            gameOver = true;

            showResult("Bạn thắng!");

            return;
        }


        // -----------------------------------------------------
        // Kiểm tra hòa
        // -----------------------------------------------------

        if (isBoardFull()) {

            gameOver = true;

            showResult("Hai bên hòa!");

            return;
        }


        // -----------------------------------------------------
        // Chuyển lượt cho máy
        // -----------------------------------------------------

        currentPlayer = 2;

        updateTurn();


        // -----------------------------------------------------
        // Máy suy nghĩ 400ms
        // -----------------------------------------------------

        handler.postDelayed(
                () -> {

                    if (!gameOver) {

                        if (gameMode == 1) {

                            machineEasyMove();

                        } else if (gameMode == 2) {

                            machineMediumMove();

                        } else {

                            machineHardMove();
                        }
                    }

                },
                400
        );
    }


    // =========================================================
    // AI DỄ
    // =========================================================

    private void machineEasyMove() {

        List<int[]> empty =
                getEmptyCells();


        if (empty.isEmpty()) {

            gameOver = true;

            showResult("Hai bên hòa!");

            return;
        }


        // Chọn một ô ngẫu nhiên

        int index =
                random.nextInt(
                        empty.size()
                );


        int[] move =
                empty.get(index);


        makeMachineMove(
                move[0],
                move[1]
        );
    }


    // =========================================================
    // AI TRUNG BÌNH
    // =========================================================

    private void machineMediumMove() {

        List<int[]> empty =
                getEmptyCells();


        if (empty.isEmpty()) {

            gameOver = true;

            showResult("Hai bên hòa!");

            return;
        }


        // =====================================================
        // ƯU TIÊN 1:
        // MÁY CÓ THỂ THẮNG NGAY
        // =====================================================

        for (int[] move : empty) {

            int row = move[0];
            int col = move[1];


            board[row][col] = 2;


            if (checkWin(row, col)) {

                board[row][col] = 0;

                makeMachineMove(
                        row,
                        col
                );

                return;
            }


            board[row][col] = 0;
        }


        // =====================================================
        // ƯU TIÊN 2:
        // NGƯỜI CÓ THỂ THẮNG
        // → MÁY CHẶN
        // =====================================================

        for (int[] move : empty) {

            int row = move[0];
            int col = move[1];


            // Giả lập người đánh X

            board[row][col] = 1;


            if (checkWin(row, col)) {

                board[row][col] = 0;

                // Máy đặt O để chặn

                makeMachineMove(
                        row,
                        col
                );

                return;
            }


            board[row][col] = 0;
        }


        // =====================================================
        // ƯU TIÊN 3:
        // CHỌN NƯỚC TỐT
        // =====================================================

        int bestScore =
                Integer.MIN_VALUE;


        List<int[]> bestMoves =
                new ArrayList<>();


        for (int[] move : empty) {

            int row = move[0];
            int col = move[1];


            board[row][col] = 2;


            int score =
                    evaluatePosition(
                            row,
                            col
                    );


            board[row][col] = 0;


            if (score > bestScore) {

                bestScore = score;

                bestMoves.clear();

                bestMoves.add(move);

            } else if (score == bestScore) {

                bestMoves.add(move);
            }
        }


        int index =
                random.nextInt(
                        bestMoves.size()
                );


        int[] best =
                bestMoves.get(index);


        makeMachineMove(
                best[0],
                best[1]
        );
    }


    // =========================================================
    // ĐÁNH GIÁ NƯỚC ĐI - TRUNG BÌNH
    // =========================================================

    private int evaluatePosition(
            int row,
            int col) {

        int score = 0;


        // Trung tâm
        if (row == 2 && col == 2) {

            score += 20;
        }


        // Gần trung tâm
        if (Math.abs(row - 2) <= 1
                &&
                Math.abs(col - 2) <= 1) {

            score += 5;
        }


        // Đếm các quân liên tiếp
        score += countDirection(
                row,
                col,
                0,
                1
        );

        score += countDirection(
                row,
                col,
                1,
                0
        );

        score += countDirection(
                row,
                col,
                1,
                1
        );

        score += countDirection(
                row,
                col,
                1,
                -1
        );


        return score;
    }


    // =========================================================
    // AI KHÓ
    // =========================================================

    private void machineHardMove() {

        List<int[]> empty =
                getCandidateMoves();


        if (empty.isEmpty()) {

            gameOver = true;

            showResult("Hai bên hòa!");

            return;
        }


        // -----------------------------------------------------
        // Nếu máy thắng ngay → thắng
        // -----------------------------------------------------

        for (int[] move : empty) {

            int row = move[0];
            int col = move[1];


            board[row][col] = 2;


            if (checkWin(row, col)) {

                board[row][col] = 0;

                makeMachineMove(
                        row,
                        col
                );

                return;
            }


            board[row][col] = 0;
        }


        // -----------------------------------------------------
        // Nếu người sắp thắng → chặn
        // -----------------------------------------------------

        for (int[] move : empty) {

            int row = move[0];
            int col = move[1];


            board[row][col] = 1;


            if (checkWin(row, col)) {

                board[row][col] = 0;

                makeMachineMove(
                        row,
                        col
                );

                return;
            }


            board[row][col] = 0;
        }


        // -----------------------------------------------------
        // MINIMAX
        // -----------------------------------------------------

        int bestScore =
                Integer.MIN_VALUE;


        int bestRow = -1;

        int bestCol = -1;


        List<int[]> ordered =
                orderMoves(empty);


        for (int[] move : ordered) {

            int row = move[0];
            int col = move[1];


            board[row][col] = 2;


            int score =
                    minimax(
                            3,
                            false,
                            Integer.MIN_VALUE,
                            Integer.MAX_VALUE
                    );


            board[row][col] = 0;


            if (score > bestScore) {

                bestScore = score;

                bestRow = row;

                bestCol = col;
            }
        }


        // Fallback

        if (bestRow == -1) {

            int[] move =
                    empty.get(0);

            bestRow = move[0];

            bestCol = move[1];
        }


        makeMachineMove(
                bestRow,
                bestCol
        );
    }


    // =========================================================
    // MINIMAX + ALPHA-BETA
    // =========================================================

    private int minimax(
            int depth,
            boolean maximizing,
            int alpha,
            int beta) {


        // Máy thắng
        if (hasWon(2)) {

            return 100000 + depth;
        }


        // Người thắng
        if (hasWon(1)) {

            return -100000 - depth;
        }


        // Hết độ sâu
        if (depth == 0
                ||
                isBoardFull()) {

            return evaluateBoard();
        }


        List<int[]> moves =
                orderMoves(
                        getCandidateMoves()
                );


        if (moves.isEmpty()) {

            return evaluateBoard();
        }


        // =====================================================
        // LƯỢT MÁY
        // =====================================================

        if (maximizing) {

            int best =
                    Integer.MIN_VALUE;


            for (int[] move : moves) {

                int row = move[0];
                int col = move[1];


                board[row][col] = 2;


                int score =
                        minimax(
                                depth - 1,
                                false,
                                alpha,
                                beta
                        );


                board[row][col] = 0;


                best =
                        Math.max(
                                best,
                                score
                        );


                alpha =
                        Math.max(
                                alpha,
                                best
                        );


                if (beta <= alpha) {

                    break;
                }
            }


            return best;
        }


        // =====================================================
        // LƯỢT NGƯỜI
        // =====================================================

        int best =
                Integer.MAX_VALUE;


        for (int[] move : moves) {

            int row = move[0];
            int col = move[1];


            board[row][col] = 1;


            int score =
                    minimax(
                            depth - 1,
                            true,
                            alpha,
                            beta
                    );


            board[row][col] = 0;


            best =
                    Math.min(
                            best,
                            score
                    );


            beta =
                    Math.min(
                            beta,
                            best
                    );


            if (beta <= alpha) {

                break;
            }
        }


        return best;
    }


    // =========================================================
    // ĐÁNH GIÁ BÀN CỜ
    // =========================================================

    private int evaluateBoard() {

        int score = 0;


        // -----------------------------------------------------
        // Giá trị vị trí
        // -----------------------------------------------------

        for (int row = 0; row < SIZE; row++) {

            for (int col = 0; col < SIZE; col++) {

                if (board[row][col] == 2) {

                    score +=
                            positionValue(
                                    row,
                                    col
                            );

                } else if (
                        board[row][col] == 1) {

                    score -=
                            positionValue(
                                    row,
                                    col
                            );
                }
            }
        }


        // -----------------------------------------------------
        // Các hàng
        // -----------------------------------------------------

        for (int row = 0; row < SIZE; row++) {

            score += evaluateLine(
                    row,
                    0,
                    0,
                    1
            );
        }


        // -----------------------------------------------------
        // Các cột
        // -----------------------------------------------------

        for (int col = 0; col < SIZE; col++) {

            score += evaluateLine(
                    0,
                    col,
                    1,
                    0
            );
        }


        // -----------------------------------------------------
        // Chéo chính
        // -----------------------------------------------------

        score += evaluateLine(
                0,
                0,
                1,
                1
        );


        // -----------------------------------------------------
        // Chéo phụ
        // -----------------------------------------------------

        score += evaluateLine(
                0,
                SIZE - 1,
                1,
                -1
        );


        return score;
    }


    // =========================================================
    // GIÁ TRỊ VỊ TRÍ
    // =========================================================

    private int positionValue(
            int row,
            int col) {

        // Trung tâm
        if (row == 2 && col == 2) {

            return 10;
        }


        // Gần trung tâm
        if (Math.abs(row - 2) <= 1
                &&
                Math.abs(col - 2) <= 1) {

            return 4;
        }


        return 1;
    }


    // =========================================================
    // ĐÁNH GIÁ MỘT ĐƯỜNG 5 Ô
    // =========================================================

    private int evaluateLine(
            int startRow,
            int startCol,
            int rowDir,
            int colDir) {

        int machineCount = 0;

        int playerCount = 0;


        int row = startRow;

        int col = startCol;


        for (int i = 0; i < SIZE; i++) {

            if (row < 0
                    ||
                    row >= SIZE
                    ||
                    col < 0
                    ||
                    col >= SIZE) {

                break;
            }


            if (board[row][col] == 2) {

                machineCount++;

            } else if (
                    board[row][col] == 1) {

                playerCount++;
            }


            row += rowDir;

            col += colDir;
        }


        // Hai bên cùng xuất hiện
        if (machineCount > 0
                &&
                playerCount > 0) {

            return 0;
        }


        // Máy
        if (machineCount == 5) {
            return 100000;
        }

        if (machineCount == 4) {
            return 5000;
        }

        if (machineCount == 3) {
            return 500;
        }

        if (machineCount == 2) {
            return 50;
        }

        if (machineCount == 1) {
            return 5;
        }


        // Người
        if (playerCount == 5) {
            return -100000;
        }

        if (playerCount == 4) {
            return -7000;
        }

        if (playerCount == 3) {
            return -700;
        }

        if (playerCount == 2) {
            return -70;
        }

        if (playerCount == 1) {
            return -7;
        }


        return 0;
    }


    // =========================================================
    // TÌM CÁC NƯỚC ĐI CÓ KHẢ NĂNG
    // =========================================================

    private List<int[]> getCandidateMoves() {

        List<int[]> candidates =
                new ArrayList<>();


        // -----------------------------------------------------
        // Nếu bàn cờ hoàn toàn trống
        // → chọn trung tâm
        // -----------------------------------------------------

        if (isBoardEmpty()) {

            candidates.add(
                    new int[]{2, 2}
            );

            return candidates;
        }


        // -----------------------------------------------------
        // Chỉ xét các ô trống gần quân đã có
        // -----------------------------------------------------

        boolean[][] added =
                new boolean[SIZE][SIZE];


        for (int row = 0;
             row < SIZE;
             row++) {

            for (int col = 0;
                 col < SIZE;
                 col++) {

                if (board[row][col] != 0) {

                    for (int dr = -1;
                         dr <= 1;
                         dr++) {

                        for (int dc = -1;
                             dc <= 1;
                             dc++) {

                            int nr = row + dr;

                            int nc = col + dc;


                            if (nr >= 0
                                    &&
                                    nr < SIZE
                                    &&
                                    nc >= 0
                                    &&
                                    nc < SIZE
                                    &&
                                    board[nr][nc] == 0
                                    &&
                                    !added[nr][nc]) {

                                candidates.add(
                                        new int[]{
                                                nr,
                                                nc
                                        }
                                );

                                added[nr][nc] = true;
                            }
                        }
                    }
                }
            }
        }


        // -----------------------------------------------------
        // Fallback
        // -----------------------------------------------------

        if (candidates.isEmpty()) {

            return getEmptyCells();
        }


        return candidates;
    }


    // =========================================================
    // SẮP XẾP NƯỚC ĐI
    // =========================================================

    private List<int[]> orderMoves(
            List<int[]> moves) {

        List<int[]> result =
                new ArrayList<>(moves);


        result.sort(
                (a, b) -> {

                    int da =
                            Math.abs(a[0] - 2)
                                    +
                                    Math.abs(a[1] - 2);


                    int db =
                            Math.abs(b[0] - 2)
                                    +
                                    Math.abs(b[1] - 2);


                    return Integer.compare(
                            da,
                            db
                    );
                }
        );


        return result;
    }


    // =========================================================
    // KIỂM TRA MỘT NGƯỜI ĐÃ THẮNG
    // =========================================================

    private boolean hasWon(int player) {

        // -----------------------------------------------------
        // Hàng
        // -----------------------------------------------------

        for (int row = 0;
             row < SIZE;
             row++) {

            boolean win = true;


            for (int col = 0;
                 col < SIZE;
                 col++) {

                if (board[row][col] != player) {

                    win = false;

                    break;
                }
            }


            if (win) {

                return true;
            }
        }


        // -----------------------------------------------------
        // Cột
        // -----------------------------------------------------

        for (int col = 0;
             col < SIZE;
             col++) {

            boolean win = true;


            for (int row = 0;
                 row < SIZE;
                 row++) {

                if (board[row][col] != player) {

                    win = false;

                    break;
                }
            }


            if (win) {

                return true;
            }
        }


        // -----------------------------------------------------
        // Chéo chính
        // -----------------------------------------------------

        boolean mainDiagonal = true;


        for (int i = 0;
             i < SIZE;
             i++) {

            if (board[i][i] != player) {

                mainDiagonal = false;

                break;
            }
        }


        if (mainDiagonal) {

            return true;
        }


        // -----------------------------------------------------
        // Chéo phụ
        // -----------------------------------------------------

        boolean otherDiagonal = true;


        for (int i = 0;
             i < SIZE;
             i++) {

            if (
                    board[i][SIZE - 1 - i]
                            != player
            ) {

                otherDiagonal = false;

                break;
            }
        }


        return otherDiagonal;
    }


    // =========================================================
    // MÁY ĐẶT O
    // =========================================================

    private void makeMachineMove(
            int row,
            int col) {

        if (gameOver) {
            return;
        }


        board[row][col] = 2;


        buttons[row][col].setText("O");

        buttons[row][col].setTextColor(
                Color.BLUE
        );


        // -----------------------------------------------------
        // Máy thắng
        // -----------------------------------------------------

        if (checkWin(row, col)) {

            gameOver = true;

            showResult("Máy thắng!");

            return;
        }


        // -----------------------------------------------------
        // Hòa
        // -----------------------------------------------------

        if (isBoardFull()) {

            gameOver = true;

            showResult("Hai bên hòa!");

            return;
        }


        // -----------------------------------------------------
        // Trả lượt cho người
        // -----------------------------------------------------

        currentPlayer = 1;

        updateTurn();
    }


    // =========================================================
    // KIỂM TRA THẮNG TẠI MỘT Ô
    // =========================================================

    private boolean checkWin(
            int row,
            int col) {

        // -----------------------------------------------------
        // Ngang
        // -----------------------------------------------------

        if (
                countDirection(
                        row,
                        col,
                        0,
                        1
                )
                        +
                        countDirection(
                                row,
                                col,
                                0,
                                -1
                        )
                        - 1 >= SIZE
        ) {

            return true;
        }


        // -----------------------------------------------------
        // Dọc
        // -----------------------------------------------------

        if (
                countDirection(
                        row,
                        col,
                        1,
                        0
                )
                        +
                        countDirection(
                                row,
                                col,
                                -1,
                                0
                        )
                        - 1 >= SIZE
        ) {

            return true;
        }


        // -----------------------------------------------------
        // Chéo \
        // -----------------------------------------------------

        if (
                countDirection(
                        row,
                        col,
                        1,
                        1
                )
                        +
                        countDirection(
                                row,
                                col,
                                -1,
                                -1
                        )
                        - 1 >= SIZE
        ) {

            return true;
        }


        // -----------------------------------------------------
        // Chéo /
        // -----------------------------------------------------

        if (
                countDirection(
                        row,
                        col,
                        1,
                        -1
                )
                        +
                        countDirection(
                                row,
                                col,
                                -1,
                                1
                        )
                        - 1 >= SIZE
        ) {

            return true;
        }


        return false;
    }


    // =========================================================
    // ĐẾM QUÂN LIÊN TIẾP
    // =========================================================

    private int countDirection(
            int row,
            int col,
            int rowDir,
            int colDir) {

        int player =
                board[row][col];


        int count = 0;


        int r = row;

        int c = col;


        while (
                r >= 0
                        &&
                        r < SIZE
                        &&
                        c >= 0
                        &&
                        c < SIZE
                        &&
                        board[r][c] == player
        ) {

            count++;

            r += rowDir;

            c += colDir;
        }


        return count;
    }


    // =========================================================
    // LẤY Ô TRỐNG
    // =========================================================

    private List<int[]> getEmptyCells() {

        List<int[]> result =
                new ArrayList<>();


        for (int row = 0;
             row < SIZE;
             row++) {

            for (int col = 0;
                 col < SIZE;
                 col++) {

                if (board[row][col] == 0) {

                    result.add(
                            new int[]{
                                    row,
                                    col
                            }
                    );
                }
            }
        }


        return result;
    }


    // =========================================================
    // KIỂM TRA BÀN CỜ ĐẦY
    // =========================================================

    private boolean isBoardFull() {

        for (int row = 0;
             row < SIZE;
             row++) {

            for (int col = 0;
                 col < SIZE;
                 col++) {

                if (board[row][col] == 0) {

                    return false;
                }
            }
        }


        return true;
    }


    // =========================================================
    // KIỂM TRA BÀN CỜ TRỐNG
    // =========================================================

    private boolean isBoardEmpty() {

        for (int row = 0;
             row < SIZE;
             row++) {

            for (int col = 0;
                 col < SIZE;
                 col++) {

                if (board[row][col] != 0) {

                    return false;
                }
            }
        }


        return true;
    }


    // =========================================================
    // CẬP NHẬT LƯỢT
    // =========================================================

    private void updateTurn() {

        if (currentPlayer == 1) {

            tvTurn.setText(
                    "Lượt chơi: Bạn (X)"
            );

        } else {

            tvTurn.setText(
                    "Lượt chơi: Máy (O)"
            );
        }
    }


    // =========================================================
    // HIỂN THỊ KẾT QUẢ
    // =========================================================

    private void showResult(
            String message) {

        new AlertDialog.Builder(this)

                .setTitle("KẾT THÚC")

                .setMessage(message)

                .setPositiveButton(
                        "OK",

                        null
                )

                .show();
    }


    // =========================================================
    // QUAY LẠI CHỌN CHẾ ĐỘ
    // =========================================================

    private void backToModeSelection() {

        // Hủy máy đang suy nghĩ

        handler.removeCallbacksAndMessages(
                null
        );


        // Kết thúc game hiện tại

        gameOver = true;


        // Xóa bàn cờ

        clearBoard();


        // -----------------------------------------------------
        // Hiện màn hình chọn chế độ
        // -----------------------------------------------------

        modeLayout.setVisibility(
                View.VISIBLE
        );


        // -----------------------------------------------------
        // Ẩn giao diện chơi
        // -----------------------------------------------------

        tvMode.setVisibility(
                View.GONE
        );

        tvTurn.setVisibility(
                View.GONE
        );

        boardLayout.setVisibility(
                View.GONE
        );

        btnBackMode.setVisibility(
                View.GONE
        );

        btnRestart.setVisibility(
                View.GONE
        );
    }


    // =========================================================
    // XÓA BÀN CỜ
    // =========================================================

    private void clearBoard() {

        for (int row = 0;
             row < SIZE;
             row++) {

            for (int col = 0;
                 col < SIZE;
                 col++) {

                board[row][col] = 0;

                buttons[row][col].setText("");

                buttons[row][col].setTextColor(
                        Color.BLACK
                );
            }
        }
    }


    // =========================================================
    // CHƠI LẠI
    // =========================================================

    private void restartGame() {

        // Hủy máy đang suy nghĩ

        handler.removeCallbacksAndMessages(
                null
        );


        // Xóa bàn cờ

        clearBoard();


        // Người đi trước

        currentPlayer = 1;


        // Game hoạt động

        gameOver = false;


        // Cập nhật lượt

        updateTurn();
    }


    // =========================================================
    // HỦY ACTIVITY
    // =========================================================

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(
                null
        );

        super.onDestroy();
    }
}