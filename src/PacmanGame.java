import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class PacmanGame extends JPanel implements ActionListener, KeyListener {

    private final int PANEL_WIDTH = 600;
    private final int PANEL_HEIGHT = 630;
    private final int GRID_SIZE = 30; //20x20 game panel grid = 600x600; extra line for status display
    private final int FLOWER_SCORE = 5; //1 flower = 5 score;
    private final int GHOST_SCORE = 50; //try to eat ghosts = 20 score;

    private int gameLevel ;

    private boolean gamePaused ;
    private boolean isGameOver ;
    private boolean nextLevel ;

    private int pacmanX ;
    private int pacmanY ;
    private Direction pacDirection ;
    private Direction pacNewDirection ;
    private int pacAnimation ; // animation speed
    private int pacLives ;
    private boolean isPacInvincible ;

    private SoundEffect sfx = new SoundEffect();

    Random random = new Random();

    //Ghost[] ghosts = new Ghost[4];
    ArrayList<Ghost> ghosts = new ArrayList<Ghost>();

    ArrayList<Flowers> flowers = new ArrayList<>();

    private int score ;

    private Timer walkTimer;
    private Timer ghostTimer;
    private Timer pacInvTimer; //sets how long the pacman is invincible for, after eating flower

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private int[][] maze;

    private int[][] food;

    public PacmanGame() {
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.BLACK);
        sfx.initBgm();
        //sfx.startBgm();

        initGame();

        ghosts = new ArrayList<>();
        flowers = new ArrayList<>();

        walkTimer = new Timer(100, this);
        walkTimer.setActionCommand("walk");
        walkTimer.start();
        ghostTimer = new Timer(150, this);
        ghostTimer.setActionCommand("ghost");
        ghostTimer.start();

        pacInvTimer = new Timer(6000, this);
        pacInvTimer.setActionCommand("pacInv");

        resetLevel();
        startPauseGame();

        setFocusable(true);
        addKeyListener(this);
    }

    private void initGame()
    {
         gameLevel = 1;

         gamePaused = false;
         isGameOver = false;

         pacmanX = 1;
         pacmanY = 1;
         pacDirection = Direction.RIGHT;
         pacNewDirection = Direction.RIGHT;
         pacAnimation = 0; // animation speed
         pacLives = 5; // how many lives in total
         isPacInvincible = false;
         nextLevel = false;
         score   = 0;
    }

    private void resetLevel() {
        //gameLevel = 2;
        switch(gameLevel) {
            case 1:
                //GPT prompt: please design 3 pacman levels using this format: Java code to initiate two 20x20 arrays, one for the walls and one for food; the 4 outmost lines are all walls, and fill most of the spaces with food. the output format should be an array, with 1 for walls or for food, and 0 for no walls/food.
                maze = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1},
                        {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                        {1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1},
                        {1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1},
                        {1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1},
                        {1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1},
                        {1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1},
                        {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                        {1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };

                food = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0},
                        {0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0},
                        {0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0},
                        {0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0},
                        {0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0},
                        {0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0},
                        {0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0},
                        {0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0},
                        {0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0},
                        {0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0},
                        {0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0},
                        {0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0},
                        {0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0},
                        {0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };

                ghosts.clear();
                ghosts.add(new Ghost(9, 7));
                ghosts.add(new Ghost(10, 12));

                flowers.clear();
                flowers.add(new Flowers(5, 5));
                flowers.add(new Flowers(14, 14));
                flowers.add(new Flowers(5, 14));
                flowers.add(new Flowers(14, 5));

                ghostTimer.setDelay(300); //slow
                break;

            case 2:
                maze = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1},
                        {1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1},
                        {1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1},
                        {1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1},
                        {1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1},
                        {1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1},
                        {1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };

                food = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0},
                        {0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0},
                        {0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0},
                        {0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0},
                        {0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0},
                        {0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                        {0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0},
                        {0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };

                ghosts.clear();
                ghosts.add(new Ghost(8, 11));
                ghosts.add(new Ghost(11, 11));

                flowers.clear();
                flowers.add(new Flowers(5, 4));
                flowers.add(new Flowers(5, 15));
                flowers.add(new Flowers(14, 4));
                flowers.add(new Flowers(14, 15));

                ghostTimer.setDelay(150); //fast

                break;
            case 3:
                maze = new int[][]{
                        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                        {1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
                        {1,0,1,0,1,0,1,1,1,1,1,1,1,1,0,1,0,1,0,1},
                        {1,0,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,0,1},
                        {1,1,1,0,1,1,1,1,0,1,1,0,1,1,1,1,0,1,1,1},
                        {1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
                        {1,0,1,1,1,0,1,1,1,0,0,1,1,1,0,1,1,1,0,1},
                        {1,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,1},
                        {1,0,1,1,1,0,1,0,1,1,1,1,0,1,0,1,1,1,0,1},
                        {1,0,0,0,1,0,0,0,1,0,0,1,0,0,0,1,0,0,0,1},
                        {1,0,0,0,1,0,0,0,1,0,0,1,0,0,0,1,0,0,0,1},
                        {1,0,1,1,1,0,1,0,1,1,0,1,0,1,0,1,1,1,0,1},
                        {1,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,1},
                        {1,0,1,1,1,0,1,1,1,0,0,1,1,1,0,1,1,1,0,1},
                        {1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
                        {1,1,1,0,1,1,1,1,0,1,1,0,1,1,1,1,0,1,1,1},
                        {1,0,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,0,1},
                        {1,0,1,0,1,0,1,1,1,1,1,1,1,1,0,1,0,1,0,1},
                        {1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
                        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
                };

                food = new int[][]{
                        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,1,1,0,1,1,1,1,1,1,1,1,1,1,0,1,1,1,0},
                        {0,1,0,1,0,1,0,0,0,0,0,0,0,0,1,0,1,0,1,0},
                        {0,1,0,1,1,1,1,1,1,0,0,1,1,1,1,1,1,0,1,0},
                        {0,0,0,1,0,0,0,0,1,0,0,1,0,0,0,0,1,0,0,0},
                        {0,1,1,1,0,1,1,1,1,1,1,1,1,1,1,0,1,1,1,0},
                        {0,1,0,0,0,1,0,0,0,1,1,0,0,0,1,0,0,0,1,0},
                        {0,1,1,1,1,1,0,1,1,1,1,1,1,0,1,1,1,1,1,0},
                        {0,1,0,0,0,1,0,1,0,0,0,0,1,0,1,0,0,0,1,0},
                        {0,1,1,1,0,1,1,1,0,1,1,0,1,1,1,0,1,1,1,0},
                        {0,1,1,1,0,1,1,1,0,1,1,0,1,1,1,0,1,1,1,0},
                        {0,1,0,0,0,1,0,1,0,0,1,0,1,0,1,0,0,0,1,0},
                        {0,1,1,1,1,1,0,1,1,1,1,1,1,0,1,1,1,1,1,0},
                        {0,1,0,0,0,1,0,0,0,1,1,0,0,0,1,0,0,0,1,0},
                        {0,1,1,1,0,1,1,1,1,1,1,1,1,1,1,0,1,1,1,0},
                        {0,0,0,1,0,0,0,0,1,0,0,1,0,0,0,0,1,0,0,0},
                        {0,1,0,1,1,1,1,1,1,0,0,1,1,1,1,1,1,0,1,0},
                        {0,1,0,1,0,1,0,0,0,0,0,0,0,0,1,0,1,0,1,0},
                        {0,1,1,1,0,1,1,1,1,1,1,1,1,1,1,0,1,1,1,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
                };

                ghosts.clear();
                ghosts.add(new Ghost(7, 12));
                ghosts.add(new Ghost(8, 12));
                ghosts.add(new Ghost(11, 12));
                ghosts.add(new Ghost(12, 12));

                flowers.clear();
                flowers.add( new Flowers(5, 5));
                flowers.add( new Flowers(5,14));
                flowers.add( new Flowers(14,5));
                flowers.add( new Flowers(14,14));

                ghostTimer.setDelay(120); //very fast

                break;
            default:
                break;
        }

        resetPacman();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw background
//        g.setColor(Color.pink);
//        g.fillOval(60,60,200,100);
//        g.fillRect(80,150,10,200);
//        g.fillRect(100,150,10,200);
//        g.fillRect(180,150,10,200);
//        g.fillRect(200,150,10,200);

        // draw maze
        for (int row = 0; row < maze.length; row++) {
            for (int col = 0; col < maze[row].length; col++) {
                if (maze[row][col] == 1) {
                    g.setColor(Color.decode("#7aab4a"));
                    g.fillRect(col * GRID_SIZE, row * GRID_SIZE, GRID_SIZE, GRID_SIZE);
                }
            }
        }

        //draw seeds
        for (int row = 0; row < food.length; row++) {
            for (int col = 0; col < food[row].length; col++) {
                if (food[row][col] == 1) {
                    //g.setColor(Color.RED);
                    g.setColor(Color.decode("#B59410")); //dark gold
                    g.fillOval(col * GRID_SIZE + 11, row * GRID_SIZE + 11, GRID_SIZE - 22, GRID_SIZE - 22);
                }
            }
        }


        //draw flower
        g.setColor(Color.GREEN);
        g.setColor(Color.decode("#B59410")); //dark gold
        for (int i = 0; i < flowers.size(); i++) {
            //g.fillOval(flowers.get(i).x * GRID_SIZE + 5, flowers.get(i).y * GRID_SIZE + 5, GRID_SIZE - 10, GRID_SIZE - 10);
            g.setColor(Color.decode("#ffa6b9")); //pink
            g.fillOval(flowers.get(i).x * GRID_SIZE + 8, flowers.get(i).y * GRID_SIZE , 14, 14);
            g.fillOval(flowers.get(i).x * GRID_SIZE + 0, flowers.get(i).y * GRID_SIZE+5 , 14, 14);
            g.fillOval(flowers.get(i).x * GRID_SIZE + 15, flowers.get(i).y * GRID_SIZE+5 , 14, 14);
            g.fillOval(flowers.get(i).x * GRID_SIZE + 3, flowers.get(i).y * GRID_SIZE+15 , 14, 14);
            g.fillOval(flowers.get(i).x * GRID_SIZE + 13, flowers.get(i).y * GRID_SIZE+15 , 14, 14);

            g.setColor(Color.yellow); //yellow
            g.fillOval(flowers.get(i).x * GRID_SIZE + 9, flowers.get(i).y * GRID_SIZE + 9, 12, 12);
        }


        drawPacMan(g);
        //g.setColor(Color.YELLOW);
        //g.fillArc(pacmanX, pacmanY, GRID_SIZE, GRID_SIZE, 45, 270);

        //draw ghosts
        //g.setColor(Color.CYAN);
        for (int i = 0; i < ghosts.size(); i++) {
            //g.fillRect(ghosts.get(i).x * GRID_SIZE + 5, ghosts.get(i).y * GRID_SIZE + 5, GRID_SIZE - 10, GRID_SIZE - 10);
            drawGhostShape(g, ghosts.get(i).x * GRID_SIZE + 5, ghosts.get(i).y * GRID_SIZE + 5, GRID_SIZE - 10, GRID_SIZE - 10);
        }


        //draw text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Lives: " , 5, 620);
        g.drawString("Score: " + score, 480, 620);
        g.drawString("Level "+ gameLevel, 270, 620);

        g.setColor(Color.yellow);
        if (nextLevel)
            g.drawString("Next level...", 200, 300);

        if (gamePaused)
        {
            g.drawString("Press enter to start", 210, 300);
        }

        if(isGameOver) {
            g.drawString("Game Over", 250, 270);
            g.drawString("Press enter to restart", 200, 300);
        }

        //paint pacman lives

        for (int i = 1; i <= pacLives; i++) {
            g.setColor(Color.decode("#ffdc44"));
            int arcStartAngle = 45;
            int arcAngle = 270;
            g.fillArc(i * GRID_SIZE+50, 20 * GRID_SIZE + GRID_SIZE / 4, GRID_SIZE / 2, GRID_SIZE / 2, arcStartAngle, arcAngle); // Draw Pacman
        }

        // test drawing
        //g.setColor(Color.pink);
        //g.fillOval(60,60,200,100);
    }

    private void drawGhostShape(Graphics g, int x, int y, int width, int height) {
        // Set the color for the ghost
        g.setColor(Color.decode("#4a94b1"));
        //width += 100;
        //height += 100;

        // Draw the ghost body (cape shape)
        g.fillArc(x, y, width, height, 0, 180); // Top half
        g.fillRect(x, y+height/2, width, height/4); // Bottom part
        g.fillArc(x-1, y+height/2, width/3+1, height/2, 180, 180); // skirt
        g.fillArc(x+width/3+1, y+height/2, width/3+1, height/2, 180, 180); // skirt
        g.fillArc(x+width*2/3+2, y+height/2, width/3, height/2, 180, 190); // skirt
        //numbers are trial and error

        // Draw the ghost's eyes
        g.setColor(Color.black);
        g.fillOval(x+(int)(width*0.2), y+(int) (height *0.25), (int)(width *0.2), (int) (height *0.3)); // Left eye
        g.fillOval(x+(int)(width*0.6), y+(int) (height *0.25), (int)(width *0.2), (int) (height *0.3)); // Right eye

        // Draw the ghost's mouth (smile)
        //g.setColor(Color.black);
        g.drawArc(x+(int)(width*0.2), y+(int) (height *0.1), (int)(width *0.6), (int) (height *0.6), -30, -120); // Mouth

    }

    public void drawPacMan(Graphics g) {
        if (isPacInvincible) {
            //g.setColor(Color.CYAN);
            //g.setColor(Color.decode("#C0C0C0"));
            g.setColor(Color.white);
        } else {
            g.setColor(Color.YELLOW);
        }

        int arcStartAngle = 0;
        int arcAngle = 270;

        // Set the arc based on direction
        switch (pacDirection) {
            case UP:
                arcStartAngle = 135; // Facing up
                break;
            case DOWN:
                arcStartAngle = 315;  // Facing down (corrected)
                break;
            case LEFT:
                arcStartAngle = 225;  // Facing left
                break;
            case RIGHT:
            default:
                arcStartAngle = 45;  // Facing right (corrected)
                break;
        }

        switch (pacAnimation) {
            case 0:
                arcStartAngle = arcStartAngle + 15;
                arcAngle = arcAngle - 30;
                break;
            case 1:
            case 7:
                break;
            case 2:
            case 6:
                arcStartAngle = arcStartAngle - 15;
                arcAngle = arcAngle + 30;
                break;
            case 3:
            case 5:
                arcStartAngle = arcStartAngle - 30;
                arcAngle = arcAngle + 60;
                break;
            case 4:
                arcStartAngle = arcStartAngle - 45;
                arcAngle = arcAngle + 90;
                break;
            default:
                break;
        }

        g.fillArc(pacmanX * GRID_SIZE, pacmanY * GRID_SIZE, GRID_SIZE, GRID_SIZE, arcStartAngle, arcAngle); // Draw Pacman

        pacAnimation = (pacAnimation+1) % 8;
    }

    //20241226
    @Override
    public void actionPerformed(ActionEvent e) {

        if ("walk".equals(e.getActionCommand())) {

            if (tryMove(pacNewDirection)) {
                pacDirection = pacNewDirection;
            } else {
                tryMove(pacDirection);
            }
            repaint();
            if (isFood(pacmanX, pacmanY)) {
                sfx.playEatSound();
                score++;
                removeFood(pacmanX, pacmanY);
            }
        } else if ("ghost".equals(e.getActionCommand())) {
            System.out.println("move ghosts ");
            moveGhosts();
        } else if ("pacInv".equals(e.getActionCommand())) {
            System.out.println("pacman inv expires");
            isPacInvincible = false;
        }

        repaint();

        int ghostIndex = isGhost(pacmanX, pacmanY);
        if (ghostIndex >= 0) {
            if (isPacInvincible) {
                sfx.playEatSound();
                score += GHOST_SCORE;
                ghosts.get(ghostIndex).resetGhost();
            } else {
                pacLives--;

                if (pacLives > 0) {
                    sfx.stopBgm();
                    pacmanDead();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    //slightly pause before resetting
                    resetPacman();
                    sfx.startBgm();
                } else if (!isGameOver) {
                    gameOver();
                }
            }
        }

        if (isFlower(pacmanX, pacmanY)) {
            score += FLOWER_SCORE;
            sfx.playInvincibleSound();
            isPacInvincible = true;
            pacInvTimer.stop();
            //pacInvTimer = new Timer(1200, this);
            //pacInvTimer.setActionCommand("ghost");
            pacInvTimer.start();
        }

        if (!foodExists()) {
            if (!isGameOver) {
                if(gameLevel <3 )
                {
                    //level up
                    if(!nextLevel) {
                        nextLevel = true;
                    walkTimer.stop();
                    ghostTimer.stop(); // Stop the game
                    sfx.playYouWinSound();
//                    repaint();

                    JOptionPane.showMessageDialog(this, "Level " + gameLevel + " cleared!", "Next Level", JOptionPane.INFORMATION_MESSAGE);

                    //repaint();
                    gameLevel++;
                    resetLevel();
                    nextLevel = false;
                    }
                }
                else {
                    youWin();
                }
            }
        }
    }

    private boolean tryMove(Direction direction) {
        int newX = pacmanX;
        int newY = pacmanY;

        switch (direction) {
            case UP:
                newX = pacmanX + 0;
                newY = pacmanY + -1;
                break;
            case DOWN:
                newX = pacmanX + 0;
                newY = pacmanY + 1;
                break;
            case LEFT:
                newX = pacmanX + -1;
                newY = pacmanY + 0;
                break;
            case RIGHT:
            default:
                newX = pacmanX + 1;
                newY = pacmanY + 0;
                break;
        }

        if (!isWall(newX, newY)) {
            pacmanX = newX;
            pacmanY = newY;
            return true;
        } else {
            return false;
            //walk in the same direction
        }
    }

    private void pacmanDead() {
        walkTimer.stop();
        ghostTimer.stop(); // Stop the game

        sfx.playDeadSound();
    }

    private void resetPacman() {
        //walkTimer.stop();
        //ghostTimer.stop(); // Stop the game
        pacmanX = 1;
        pacmanY = 1;
        pacDirection = Direction.RIGHT;
        pacNewDirection = Direction.RIGHT;
        // Show game over message
        // reset ghost location
        for (Ghost ghost : ghosts) {
            ghost.resetGhost();
        }

        walkTimer.start();
        ghostTimer.start();
    }

    private void startPauseGame() {
        if (isGameOver) {
            initGame();
            resetLevel();
            startPauseGame();
            return;
        }

        if (gamePaused) {
            gamePaused = false;
            walkTimer.start();
            ghostTimer.start(); // Start the game
            sfx.startBgm();
        } else {
            gamePaused = true;
            walkTimer.stop();
            ghostTimer.stop(); // Stop the game
            sfx.stopBgm();
            repaint(); //show the game paused message
        }
    }

    private void gameOver() {
        walkTimer.stop();
        ghostTimer.stop(); // Stop the game
        sfx.stopBgm();
        sfx.playGameOverSound();
        isGameOver = true;
        // Show game over message
        JOptionPane.showMessageDialog(this, "Game Over! Final Score: " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void youWin() {
        walkTimer.stop();
        ghostTimer.stop(); // Stop the game
        sfx.stopBgm();
        sfx.playYouWinSound();
        isGameOver = true;
        // Show game over message
        JOptionPane.showMessageDialog(this, "You Win! Final Score: " + score, "You Win", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isWall(int x, int y) {
        int row = y;
        int col = x;

        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length) {
            return true;
        }

        return maze[row][col] == 1;
    }

    private boolean isFood(int x, int y) {
        int row = y;
        int col = x;

        if (row < 0 || col < 0 || row >= food.length || col >= food[0].length) {
            //out of boundary
            return false;
        }

        return food[row][col] == 1;
    }

    private void removeFood(int x, int y) {
        int row = y;
        int col = x;

        if (row < 0 || col < 0 || row >= food.length || col >= food[0].length) {
            //out of boundary
            return;
        }

        food[row][col] = 0;
    }

    //returns the ghost index if true; otherwise -1
    private int isGhost(int x, int y) {
        int row = y;
        int col = x;

        for (int i = 0; i < ghosts.size(); i++) {
            Ghost ghost = ghosts.get(i);
            if (ghost.y == row && ghost.x == col) {
                return i;
            }
        }
        return -1;
    }

    private boolean isFlower(int x, int y) {
        int row = y;
        int col = x;

        for (Flowers flowers : this.flowers) {
            if (flowers.y == row && flowers.x == col) {
                this.flowers.remove(flowers);
                return true;
            }
        }
        return false;
    }

    private boolean foodExists() {
        // calculate max score
        for (int i = 0; i < food.length; i++) {
            for (int j = 0; j < food[i].length; j++) {
                if (food[i][j]==1)
                    return true; // Add each number to the sum
            }
        }
        return false;
    }

    class Ghost {
        int x; // Ghost's x position
        int y; // Ghost's y position
        int resetX;
        int resetY;
        Direction ghostDirection;
        int sleepTimer;

        Ghost(int startX, int startY) {
            resetX = startX;
            resetY = startY;
            resetGhost();
        }

        public void resetGhost() {
            resetGhost(resetX,resetY);
        }

        private void resetGhost(int startX, int startY) {
            this.x = startX;
            this.y = startY;
            this.sleepTimer = 10;
            ghostDirection = Direction.values()[random.nextInt(Direction.values().length)];
        }
    }


    class Flowers {
        int x; // Ghost's x position
        int y; // Ghost's y position

        Flowers(int startX, int startY) {
            this.x = startX;
            this.y = startY;
        }
    }

//    private void placeFood() {
//        int row, col;
//        do {
//            row = (int) (Math.random() * maze.length);
//            col = (int) (Math.random() * maze[0].length);
//        } while (maze[row][col] != 0);
//
//        foodX = col * GRID_SIZE;
//        foodY = row * GRID_SIZE;
//    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();


        if (key == KeyEvent.VK_ENTER) {
            //start and pause game≈
            startPauseGame();
        } else if (key == KeyEvent.VK_UP) {
            pacNewDirection = Direction.UP;
        } else if (key == KeyEvent.VK_DOWN) {
            pacNewDirection = Direction.DOWN;
        } else if (key == KeyEvent.VK_LEFT) {
            pacNewDirection = Direction.LEFT;
        } else if (key == KeyEvent.VK_RIGHT) {
            pacNewDirection = Direction.RIGHT;
        }
    }

    public void moveGhosts() {
        for (Ghost ghost : ghosts) {
            System.out.println("new ghost " + ghost.x + ", " + ghost.y);

            if (ghost.sleepTimer > 0)
            {
                //don't move ghost in the first x times
                ghost.sleepTimer--;
            }
            else {
                boolean moved = false;
                while (!moved) {
                    System.out.println(ghost.ghostDirection);

                    int newX, newY;
                    newX = ghost.x;
                    newY = ghost.y;

                    if (random.nextInt(3) == 0) {
                        //1/3 chance it will try to change direction
                        //otherwise it just stays in the same direction
                        Direction direction;

                            Direction preferX;
                            Direction preferY;
                            //little logic to try to move closer to pacman
                            if (ghost.x > pacmanX) {
                                preferX = Direction.LEFT;
                            } else {
                                preferX = Direction.RIGHT;
                            }
                            if (ghost.y > pacmanY) {
                                preferY = Direction.UP;
                            } else {
                                preferY = Direction.DOWN;
                            }

                            if (random.nextInt(100) < 70) {
                                //70% chance tries to go closer to pacman
                                if (random.nextInt(2) == 1) {
                                    direction = preferX;
                                } else {
                                    direction = preferY;
                                }
                            } else {
                                direction = Direction.values()[random.nextInt(Direction.values().length)];
                            }

                        ghost.ghostDirection = direction;
                    }

                    switch (ghost.ghostDirection) {
                        case UP:
                            newX = ghost.x;
                            newY = ghost.y - 1;
                            break;
                        case DOWN:
                            newX = ghost.x;
                            newY = ghost.y + 1;
                            break;
                        case LEFT:
                            newX = ghost.x - 1;
                            newY = ghost.y;
                            break;
                        case RIGHT:
                            newX = ghost.x + 1;
                            newY = ghost.y;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + ghost.ghostDirection);
                    }
                    System.out.println("new ghost " + newX + ", " + newY);
                    if (!isWall(newX, newY)) {
                        ghost.x = newX;
                        ghost.y = newY;
                        moved = true;
                    }
                    //keep going in one direction until hit the wall
                    else {
                        ghost.ghostDirection = Direction.values()[random.nextInt(Direction.values().length)];
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //keep walking
        //pacmanDX = 0;
        //pacmanDY = 0;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Helen's Pac-Man Game");
        PacmanGame game = new PacmanGame();

        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}