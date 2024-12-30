import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LevelDesigner extends JFrame {
    private final int SIZE = 20;
    private final String WALL = "W";
    private final String FOOD = "F";
    private String[][] levelArray = new String[SIZE][SIZE];
    private int currentRow = 0;
    private int currentCol = 0;

    public LevelDesigner() {
        setTitle("Game Level Designer");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //initialize level array
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                levelArray[i][j] = "."; // Empty space
            }
        }

        //create the grid panel
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(SIZE, SIZE));
        JButton[][] buttons = new JButton[SIZE][SIZE];

        //create buttons for the grid
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setText(levelArray[i][j]);
                buttons[i][j].setPreferredSize(new Dimension(30, 30));

                final int x = i; //row index
                final int y = j; //column index

                //mouse listener for each button to set it as the current cell
                buttons[i][j].addActionListener(e -> {
                    currentRow = x;
                    currentCol = y;
                    gridPanel.requestFocusInWindow(); //request focus for key events
                });

                gridPanel.add(buttons[i][j]);
            }
        }

        //add grid panel to frame
        add(gridPanel, BorderLayout.CENTER);

        //key listener for placing walls and food
        gridPanel.setFocusable(true);
        gridPanel.requestFocusInWindow();
        gridPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (currentRow < SIZE && currentCol < SIZE) {
                    if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W') {
                        placeItem(buttons, currentRow, currentCol, WALL);
                        moveToNextCell(); //move to the next cell after placing
                    } else if (e.getKeyChar() == 'f' || e.getKeyChar() == 'F') {
                        placeItem(buttons, currentRow, currentCol, FOOD);
                        moveToNextCell(); //move to the next cell after placing
                    }
                }
            }
        });

        //create brush selection panel
        JPanel brushPanel = new JPanel();
        JButton saveButton = new JButton("Save Level");

        saveButton.addActionListener(e -> saveLevel());

        brushPanel.add(saveButton);

        add(brushPanel, BorderLayout.SOUTH);
    }

    private void placeItem(JButton[][] buttons, int row, int col, String item) {
        levelArray[row][col] = item; //set the current item
        buttons[row][col].setText(item); //update button text
    }

    private void moveToNextCell() {
        currentCol++;
        if (currentCol >= SIZE) {
            currentCol = 0;
            currentRow++;
        }
        if (currentRow >= SIZE) {
            currentRow = SIZE - 1; //prevent going out of bounds
            currentCol = SIZE - 1; //stay in the last cell
        }
    }

    private void saveLevel() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("level.txt"))) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    writer.write(levelArray[i][j]);
                }
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Level saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving level: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LevelDesigner designer = new LevelDesigner();
            designer.setVisible(true);
        });
    }
}