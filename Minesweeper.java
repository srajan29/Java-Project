import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Minesweeper extends JFrame {
    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int MINES = 10;
    private JButton[][] boardButtons;
    private boolean[][] mines;
    private boolean[][] revealed;
    private JLabel statusLabel;

    public Minesweeper() {
        setTitle("Minesweeper");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();

        startNewGame();

        setVisible(true);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(ROWS, COLS));

        boardButtons = new JButton[ROWS][COLS];
        ButtonClickListener listener = new ButtonClickListener();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].setFont(new Font("Arial", Font.PLAIN, 14));
                boardButtons[i][j].addActionListener(listener);
                boardPanel.add(boardButtons[i][j]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        statusLabel = new JLabel();
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void startNewGame() {
        mines = new boolean[ROWS][COLS];
        revealed = new boolean[ROWS][COLS];
        statusLabel.setText("Mines: " + MINES);

        // Place mines randomly
        Random random = new Random();
        int count = 0;
        while (count < MINES) {
            int row = random.nextInt(ROWS);
            int col = random.nextInt(COLS);
            if (!mines[row][col]) {
                mines[row][col] = true;
                count++;
            }
        }

        // Reset button text and enable all buttons
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                boardButtons[i][j].setText("");
                boardButtons[i][j].setEnabled(true);
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < ROWS && j >= 0 && j < COLS && mines[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void revealCell(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS || revealed[row][col]) {
            return;
        }
        revealed[row][col] = true;
        boardButtons[row][col].setEnabled(false);
        int adjacentMines = countAdjacentMines(row, col);
        if (adjacentMines > 0) {
            boardButtons[row][col].setText(Integer.toString(adjacentMines));
        } else {
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    revealCell(i, j);
                }
            }
        }
    }

    private void revealMines() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (mines[i][j]) {
                    boardButtons[i][j].setText("X");
                }
            }
        }
    }

    private void updateMinesLabel() {
        int flaggedMines = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (boardButtons[i][j].getText().equals("F")) {
                    flaggedMines++;
                }
            }
        }
        statusLabel.setText("Mines: " + (MINES - flaggedMines));
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (boardButtons[i][j] == clickedButton) {
                        if (mines[i][j]) {
                            clickedButton.setText("X");
                            revealMines();
                            JOptionPane.showMessageDialog(Minesweeper.this, "Game Over! You clicked on a mine.");
                            startNewGame();
                        } else {
                            revealCell(i, j);
                            updateMinesLabel(); // Update the label after revealing a cell
                            if (checkWinCondition()) {
                                JOptionPane.showMessageDialog(Minesweeper.this, "Congratulations! You've cleared all non-mine cells. You win!");
                                startNewGame();
                            }
                        }
                        return;
                    }
                }
            }
        }

        private boolean checkWinCondition() {
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (!mines[i][j] && !revealed[i][j]) {
                        return false; // Found a non-revealed non-mine cell, game is not won yet
                    }
                }
            }
            return true; // All non-mine cells are revealed, player wins
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Minesweeper::new);
    }
}