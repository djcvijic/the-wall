package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.sceneobject.*;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Collision;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Vector;

public class Game extends JPanel implements Runnable {	
	private static final long serialVersionUID = -5949360252481685640L;
	private static final String GAME_TITLE = "The Wall";
	private static final String GAME_VERSION = "1.5";
	private static final double FRAME_BOUNDS_SCALE = 0.9;
	private static final double DEFAULT_FRAME_TIME = 1000.0 / 60;
	private static final double DEFAULT_PHYSICS_FRAME_TIME = 1000.0 / 300;
	private static final String LEVEL_FILE = "/levels/1.txt";
	private static final Color COLOR_SCHEME_RED = new Color(0.5f, 0f, 0f);
	private static final Color COLOR_SCHEME_BLUE = new Color(0f, 0f, 0.5f);
	
	private final InputListener inputListener;
	private final JFrame frame;
	
	private boolean initialized = false;
	private GameState gameState = null;
	private Color background = null;
	private Cursor defaultCursor = null;
	private Cursor blankCursor = null;
	private Cursor currentCursor = null;
	private Color colorScheme = null;
	private Bounds bounds = null;
	private Wall wall = null;
	private Paddle paddle = null;
	private Ball ball = null;
	private GameMenu menu = null;
	
	/**
	 * Initializes the game and panel, adds InputListener,
	 * initializes the frame, and calls initCursors.
	 */
	public Game() {
		super();
		this.inputListener = new InputListener(this);
		this.addMouseListener(this.inputListener);
		this.addMouseMotionListener(this.inputListener);
		this.setOpaque(true);
		this.frame = new JFrame(Game.GAME_TITLE + " " + Game.GAME_VERSION);
		this.frame.addWindowListener(new WindowAdapter() {
	        	public void windowClosing(WindowEvent e) { System.exit(0); }
		});
		this.frame.getContentPane().add(this);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int frameSize = (int) (Game.FRAME_BOUNDS_SCALE * Math.min(screenSize.width, screenSize.height));
		this.frame.setBounds(0, 0, frameSize, frameSize);
		this.frame.setResizable(false);
		this.frame.setVisible(true);
		this.frame.addKeyListener(this.inputListener);
		this.frame.addMouseWheelListener(this.inputListener);
	}
	
    public static void main(final String s[]) {
		new Thread(new Game()).start();
    }
    
    /**
     * If the game is initialized, paint the background, do initial scaling,
     * render all game elements, and render the menu if necessary.
     */
    public void paint(final Graphics g) {
        if (!this.initialized) {
        	return;
        }
        Graphics2D g2d = (Graphics2D) g;
        Dimension d = getSize();
        g2d.setPaint(this.background);
        g2d.fillRect(0, 0, d.width, d.height);
        g2d.scale(d.width, d.height);
        this.bounds.draw(g2d);
        this.wall.draw(g2d);
        this.paddle.draw(g2d);
        this.ball.draw(g2d);
        this.menu.draw(g2d);
        this.menu.drawScore(
        		g2d,
        		Wall.STARTING_POSITION.getX(),
        		Wall.STARTING_POSITION.getY() / 3,
        		0.0015,
        		0.0015
        );
    }
    
    /**
     * Initializes the game, and run the game loop infinitely, or until interrupted.
     * Update will be done on every frame, while repaint will be called often enough to maintain DEFAULT_FRAME_TIME.
     */
    public void run() {
		this.initCursors();
		this.initialize();
		double frameStartTime = 0,
				prevFrameStartTime = 0,
				prevGraphicsStartTime = 0;
		while (true) {
			prevFrameStartTime = frameStartTime;
			frameStartTime = System.currentTimeMillis();
			if (prevGraphicsStartTime == 0) {
				prevGraphicsStartTime = frameStartTime;
			}
			this.update(frameStartTime - prevFrameStartTime);
			if (frameStartTime - prevGraphicsStartTime >= Game.DEFAULT_FRAME_TIME) {
				prevGraphicsStartTime += Game.DEFAULT_FRAME_TIME;
				this.repaint();
			}
			try {
				Thread.sleep(Math.max(
						0,
						(long) (frameStartTime + Game.DEFAULT_PHYSICS_FRAME_TIME) - System.currentTimeMillis()
				));
			} catch (InterruptedException e) {
				break;
			}
		}
	}
    
	/**
	 * Toggles the color mode in the GameState, calls updateColorScheme,
	 * and sets the color of the paddle and bounds accordingly.
	 */
    public void toggleColorScheme() {
		if (this.gameState.isRedMode()) {
			this.gameState.set(GameState.FLAG_MODE_BLUE);
		} else {
			this.gameState.set(GameState.FLAG_MODE_RED);
		}
		this.updateColorScheme();
		this.paddle.setColor(this.colorScheme);
		this.bounds.setColor(this.colorScheme);
	}
    
    public boolean isInitialized() {
    	return this.initialized;
    }
    
    public void reinitialize() {
    	this.initialized = false;
    	this.initialize();
    }
    
    public Paddle getPaddle() {
    	return this.paddle;
    }
    
    public Ball getBall() {
    	return this.ball;
    }
    
    public Rectangle getFrameBounds() {
    	return this.frame.getBounds();
    }
    
    public Point getMousePositionInFrame() {
    	return this.frame.getMousePosition();
    }
    
    /**
     * Saves the default cursor and creates a custom blank one
     */
    private void initCursors() {
    	this.defaultCursor = this.frame.getContentPane().getCursor();
    	this.blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
		    new Point(0, 0),
		    "blank cursor"
		);
    }
    
    /**
     * If the game is already initialized, do nothing.
     * Initializes GameState, loads sounds, loads the level, updates the colorScheme,
     * initializes the bounds, initializes the paddle, initializes the ball, initializes the menu.
     */
	private void initialize() {
		if (this.initialized) {
			return;
		}
		
		this.gameState = GameState.getInstance();
		this.gameState.set(GameState.FLAG_PLAYING).set(GameState.FLAG_BALL_STATIC);
		this.gameState.gameTime = this.gameState.lastMilestone = 0;
		
		SoundManager.getInstance().loadSoundFx(new String[] {
				"brick.wav", "ceiling.wav", "lightning.wav", "paddle.wav"
		});
		this.loadLevel();
		
		this.updateColorScheme();
		this.bounds = new Bounds(Wall.STARTING_POSITION.getX(), this.colorScheme);
		this.paddle = new Paddle(
				new Vector(0.5, 28.0 / 30),
				new Vector(1.0 / 8, 1.0 / 8),
				this.colorScheme
		);
		double ballVelocityAngle = Math.PI * 0.25 + new Random().nextDouble() * Math.PI * 0.5;
		this.ball = new Ball(
				new Vector(this.paddle.getPosition().getX(), this.paddle.getPosition().getY() - 0.8 / 30),
				new Vector(Math.cos(ballVelocityAngle), -Math.sin(ballVelocityAngle)).getResized(0.0006),
				new Vector(1.0 / 50, 1.0 / 50)
		);
		this.menu = new GameMenu(new Vector(0.25, 0.25), new Vector(0.5, 0.5));
		
		this.initialized = true;
	}
    
    /**
     * If game is not initialized, do nothing.
     * Updates the bounds if necessary, updates the gameTime and all game objects and collisions if necessary,
     * updates the ball velocity if necessary, and sets victory state if the condition was reached.
     * Calls updateCursor.
     */
    private void update(final double deltaTime) {
		if (!this.initialized) {
			return;
		}
		if (this.gameState.isPlaying()) {
			if (!this.gameState.isAnimationOff()) {
				this.bounds.update(deltaTime);
			}
			if (this.gameState.isBallMoving()) {
				this.gameState.gameTime += deltaTime / 1000;
				this.wall.update(deltaTime);
				this.paddle.update(deltaTime);
				this.ball.update(deltaTime);
				this.updateCollisions();
				if (this.gameState.shouldIncreaseDifficulty()) {
					this.ball.modifyVelocity(1.1);
				}
				if (this.wall.getDurability() <= 0) {
					this.gameState.set(GameState.FLAG_VICTORY);
					this.gameState.previousVictoryTime = this.gameState.previousTime = this.gameState.gameTime;
				}
			}
		}
		this.updateCursor();
	}
    
    /**
     * Sets the colorScheme field based on GameState, and sets the background color
     */
    private void updateColorScheme() {
    	if (this.gameState.isRedMode()) {
    		this.colorScheme = Game.COLOR_SCHEME_RED;
		} else {
			this.colorScheme = Game.COLOR_SCHEME_BLUE;
		}
    	this.background = this.colorScheme.darker().darker().darker();
    }
    
    /**
     * Reads the LEVEL_FILE and generates a Wall based on it, saving it to the wall field
     */
    private void loadLevel() {
    	try {
			InputStream is = Game.class.getResourceAsStream(Game.LEVEL_FILE);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			ArrayList<String> rows = new ArrayList<String>();
			String line;
			while ((line = br.readLine()) != null) {
				rows.add(line);
			}
			this.wall = new Wall(rows);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Checks if ball is out-of-bounds, and if so sets the GameState to game over,
     * and updates the game timers.
	 * Checks collisions in order: out-of-bounds, paddle, brick wall, walls and ceiling
	 */
	private void updateCollisions() {
		Area ballArea = this.ball.getArea();
		if (this.bounds.isOutOfBounds(ballArea)) {
			this.gameState.set(GameState.FLAG_GAME_OVER);
			this.gameState.previousTime = this.gameState.gameTime;
			this.gameState.gameTime = 0;
			return;
		}
		try {
			this.paddle.testCollision(ballArea);
			this.wall.testCollision(ballArea);
			this.bounds.testCollision(ballArea);
		} catch (Collision c) {
			this.ball.collide(c.getNVector());
		}
	}
	
	/**
	 * Updates the cursor if necessary, based on GameState and currentCursor
	 */
	private void updateCursor() {
		if (this.gameState.isPlaying() && this.currentCursor != this.blankCursor) {
			this.currentCursor = this.blankCursor;
			this.frame.getContentPane().setCursor(this.currentCursor);
		} else if (!this.gameState.isPlaying() && this.currentCursor != this.defaultCursor) {
			this.currentCursor = this.defaultCursor;
			this.frame.getContentPane().setCursor(this.currentCursor);
		}
	}
}
