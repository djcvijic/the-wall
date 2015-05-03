package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.sceneobject;

import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;

import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.GameState;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Vector;

public class GameMenu implements SceneObject {
	private static final BasicStroke HAIRLINE_STROKE = new BasicStroke(0);
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
	private static final Color BACKDROP_COLOR = new Color(0, 0, 0, 0.3f);
	private static final Color MENU_COLOR = new Color(0, 0, 0, 0.7f);
	private static final Rectangle2D RECTANGLE = new Rectangle2D.Double(0, 0, 1, 1);
	private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 26);
	private static final Font INTSTRUCTION_FONT = new Font("Monospaced", Font.BOLD, 12);
	private static final Font SCORE_FONT = new Font("Monospaced", Font.BOLD, 14);
	
	private final Vector position;
	private final Vector scale;
	
	/**
	 * Initializes all fields
	 * @param position Expressed compared to window scale (0..1, 0..1)
	 * @param scale Expressed compared to window scale (0..1, 0..1)
	 */
	public GameMenu(final Vector position, final Vector scale) {
		this.position = position;
		this.scale = scale;
	}
	
	public void update(final double deltaTime) {}
	
	/**
	 * Renders backdrop, sets transformation matrix, renders menu, enables antiAliasing,
	 * prints the instructions, and calls drawScore
	 * @param g2d
	 */
	public void draw(final Graphics2D g2d) {
		GameState gameState = GameState.getInstance();
		if (gameState.isPlaying()) {
			return;
		}
		
		AffineTransform oldTransform = g2d.getTransform();
		
		g2d.setColor(GameMenu.BACKDROP_COLOR);
		g2d.fill(GameMenu.RECTANGLE);
		
		g2d.translate(this.position.getX(), this.position.getY());
		g2d.scale(this.scale.getX(), this.scale.getY());
		
		g2d.setColor(GameMenu.MENU_COLOR);
		g2d.fill(GameMenu.RECTANGLE);
		
		g2d.setStroke(GameMenu.HAIRLINE_STROKE);
		g2d.setColor(Color.white);
		g2d.draw(GameMenu.RECTANGLE);
		
		RenderingHints oldHints = g2d.getRenderingHints();
		g2d.setRenderingHint(
        	    RenderingHints.KEY_ANTIALIASING,
        	    RenderingHints.VALUE_ANTIALIAS_ON);
		
		AffineTransform transformBeforeText = g2d.getTransform();
		
		g2d.scale(0.005, 0.005);
		g2d.setPaint(Color.white);
		
		String menuTitle;
		if (gameState.isVictory()) {
			menuTitle = "Victory!";
		} else if (gameState.isGameOver()) {
			menuTitle = "Defeat!";
		} else {
			menuTitle = "Paused";
		}
		g2d.setFont(GameMenu.TITLE_FONT);
		g2d.drawString(
				menuTitle,
				6,
				24
		);
		
		g2d.setFont(GameMenu.INTSTRUCTION_FONT);
		g2d.drawString("LCLICK  Launch Ball",        6, 48);
		g2d.drawString("WHEEL   Adjust Speed",       6, 60);
		g2d.drawString("P/ESC   Pause/Unpause",      6, 72);
		g2d.drawString("R       Restart",            6, 84);
		g2d.drawString("A       Toggle Animation",   6, 96);
		g2d.drawString("M       Mute/Unmute",        6, 108);
		g2d.drawString("F10     Exit Game",          6, 120);
		g2d.drawString("X       ???",                6, 132);
		
		g2d.setTransform(transformBeforeText);
		
		drawScore(g2d, 0, 0.8, 0.005, 0.005);
		
		g2d.setRenderingHints(oldHints);
		
		g2d.setTransform(oldTransform);
	}
	
	/**
	 * Sets transformation matrix, enables anti-aliasing, and prints out the score based on GameState singleton
	 * @param g2d
	 * @param positionX Expressed compared to window scale (0..1, 0..1)
	 * @param positionY Expressed compared to window scale (0..1, 0..1)
	 * @param scaleX Expressed compared to window scale (0..1, 0..1)
	 * @param scaleY Expressed compared to window scale (0..1, 0..1)
	 */
	public void drawScore(final Graphics2D g2d, final double positionX, final double positionY, final double scaleX, final double scaleY) {
		GameState gameState = GameState.getInstance();
		
		AffineTransform oldTransform = g2d.getTransform();
		
		g2d.translate(positionX, positionY);
		g2d.scale(scaleX, scaleY);
		
		RenderingHints oldHints = g2d.getRenderingHints();
		g2d.setRenderingHint(
        	    RenderingHints.KEY_ANTIALIASING,
        	    RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setFont(GameMenu.SCORE_FONT);
		g2d.setPaint(Color.white);
		g2d.drawString(
				"Current Time: " + GameMenu.DECIMAL_FORMAT.format(gameState.gameTime),
				6,
				0
		);
		g2d.drawString(
				"Previous Time: " + GameMenu.DECIMAL_FORMAT.format(gameState.previousTime),
				6,
				12
		);
		g2d.drawString(
				"Previous Victory: " + GameMenu.DECIMAL_FORMAT.format(gameState.previousVictoryTime),
				6,
				24
		);
		
		g2d.setRenderingHints(oldHints);
		
		g2d.setTransform(oldTransform);
	}
}
