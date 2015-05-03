package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc;

import java.awt.*;
import java.awt.event.*;

import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.sceneobject.Paddle;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.sceneobject.Wall;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Vector;

public class InputListener implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {
	private final Game game;
	private final GameState gameState;
	
	private Robot mouseRobot = null;
	
	public InputListener(final Game game) {
		this.game = game;
		this.gameState = GameState.getInstance();
	}
	
	/**
	 * KeyListener implementation
     * F10 key will close the game
     * ESC or P will pause the game
	 */
	public void keyPressed(final KeyEvent e) {
		if (!this.game.isInitialized()) {
			return;
		}
		switch(e.getKeyCode()) {
		case KeyEvent.VK_F10:
			System.exit(0);
			break;
		case KeyEvent.VK_ESCAPE: case KeyEvent.VK_P:
			this.gameState.togglePause();
			if (this.gameState.isPlaying()) {
				this.moveMouseToPaddle();
			}
			break;
	  	}
	}
	public void keyReleased(final KeyEvent e) {}
	/**
	 * Controls are:
	 * a - toggle animation,
	 * m - toggle sound,
	 * r - reset game,
	 * x - toggle color scheme.
	 */
	public void keyTyped(final KeyEvent e) {
		if (!this.game.isInitialized()) {
			return;
		}
	  	switch(e.getKeyChar()) {
	  	case 'a':
	  		if (this.gameState.isAnimationOff()) {
	  			this.gameState.set(GameState.FLAG_ANIMATION_ON);
	  		} else {
	  			this.gameState.set(GameState.FLAG_ANIMATION_OFF);
	  		}
	  		break;
	  	case 'm':
	  		if (this.gameState.isMuted()) {
	  			this.gameState.set(GameState.FLAG_SOUND_ON);
	  		} else {
	  			this.gameState.set(GameState.FLAG_SOUND_OFF);
	  		}
	  		break;
	  	case 'r':
	  		this.resetGame();
	  		break;
	  	case 'x':
	  		this.game.toggleColorScheme();
	  		break;
	  	}
	  	this.game.repaint();
	}

	public void mouseClicked(final MouseEvent e){}
	public void mouseEntered(final MouseEvent e){}
	public void mouseExited(final MouseEvent e){}
	public void mousePressed(final MouseEvent e){}
    /**
     * MouseListener implementation
	 * If game is playing, launch the ball
	 * Otherwise if the game is over or player has won, restart the game
     */
	public void mouseReleased(final MouseEvent e) {
		if (!this.game.isInitialized()) {
			return;
		}
		if (this.gameState.isPlaying()) {
			this.gameState.set(GameState.FLAG_BALL_MOVING);
		} else if (this.gameState.isGameOver() || this.gameState.isVictory()) {
			this.resetGame();
		}
	}

	/**
	 * MouseMotionListener implementation
	 * Moves the mouse pointer, but keeps X in between the left and right wall
	 * and keeps Y in the center of the frame.
	 */
	public void mouseMoved(final MouseEvent e) {
		Point mousePosition = this.game.getMousePositionInFrame();
		if (!this.game.isInitialized() || !this.gameState.isPlaying() || (mousePosition == null)) {
			return;
		}
		Rectangle frameBounds = this.game.getFrameBounds();
		double x = mousePosition.getX() / frameBounds.width;
		Vector paddleScale = this.game.getPaddle().getScale();
		if (x < Wall.STARTING_POSITION.getX() + paddleScale.getX() / 2) {
			x = Wall.STARTING_POSITION.getX() + paddleScale.getX() / 2;
		}
		if (x > Wall.STARTING_POSITION.getX() + Wall.WALL_WIDTH - paddleScale.getX() / 2) {
			x = Wall.STARTING_POSITION.getX() + Wall.WALL_WIDTH - paddleScale.getX() / 2;
		}
		this.game.getPaddle().setX(x);
		if (!this.gameState.isBallMoving()) {
			this.game.getBall().setX(x);
		}
		this.moveMouseToPaddle();
	}
	/**
	 * Does the same as mouseMoved
	 */
	public void mouseDragged(final MouseEvent e) { mouseMoved(e); }
		
	/**
	 * MouseWheelListener implementation
	 * Affects ball velocity
	 */
	public void mouseWheelMoved(final MouseWheelEvent e)  {
		if (!this.game.isInitialized() || !this.gameState.isPlaying() || e.getWheelRotation() == 0) {
			return;
		}
		if (e.getWheelRotation() > 0) {
			this.game.getBall().modifyVelocity(0.95);
		} else {
			this.game.getBall().modifyVelocity(1.0 / 0.95);
		}
	}
	
	/**
	 * Called when 'R' is pressed, or when clicking in the menu.
	 * Updates previousTime if necessary, and reinitializes the game.
	 */
	private void resetGame() {
		if (!this.gameState.isGameOver()) {
  			this.gameState.previousTime = this.gameState.gameTime;
  		}
  		this.game.reinitialize();
  		this.moveMouseToPaddle();
	}
	
	/**
	 * If mouseRobot is not yet initialized, first initializes it.
	 * Moves the mouse cursor to the position of the paddle horizontally, and the middle of the screen vertically.
	 */
	private void moveMouseToPaddle() {
		Paddle paddle = this.game.getPaddle();
		if (paddle == null) {
			return;
		}
		try {
			if (this.mouseRobot == null) {
				this.mouseRobot = new Robot();
			}
			Rectangle frameBounds = this.game.getFrameBounds();
			this.mouseRobot.mouseMove(
				(int) (paddle.getPosition().getX() * frameBounds.width) + frameBounds.x,
				frameBounds.height / 2 + frameBounds.y
			);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}
