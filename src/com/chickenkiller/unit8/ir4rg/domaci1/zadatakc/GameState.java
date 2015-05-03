package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc;

public class GameState {
	public static final int FLAG_ALL_DEFAULTS = 0;
	public static final int FLAG_GAME_OVER = 1;
	public static final int FLAG_PAUSED = 2;
	public static final int FLAG_VICTORY = 4;
	public static final int FLAG_PLAYING = -8;
	public static final int FLAG_BALL_MOVING = 8;
	public static final int FLAG_BALL_STATIC = -9;
	public static final int FLAG_SOUND_OFF = 16;
	public static final int FLAG_SOUND_ON = -17;
	public static final int FLAG_MODE_RED = 32;
	public static final int FLAG_MODE_BLUE = -33;
	public static final int FLAG_ANIMATION_OFF = 64;
	public static final int FLAG_ANIMATION_ON = -65;
	
	/**
	 * Milestones are at 30 seconds.
	 * Used for periodic ball velocity increase.
	 */
	public double lastMilestone = 0;
	public double gameTime = 0;
	public double previousTime = 0;
	public double previousVictoryTime = 0;
	
	private static GameState instance = null;
	
	/**
	 * Bit representation of general game status info
	 * -----------
	 * bit  - state 1      / state 0
	 * -----------
	 * bit0 - game over    / playing
	 * bit1 - paused       / playing
	 * bit2 - victory      / playing
	 * bit3 - ball moving  / ball static
	 * bit4 - muted        / sound
	 * bit5 - red          / blue
	 * bit6 - no animation / animation
	 */
	private int gameState = 0;
	
	private GameState() {}
	
	public static GameState getInstance() {
		if (GameState.instance == null) {
			GameState.instance = new GameState();
		}
		return GameState.instance;
	}
	
	/**
	 * Sets or resets a one or more flags in the game state
	 * @param flag One of this class's constants prefixed with "FLAG_"
	 * @return This object
	 */
	public GameState set(final int flag) {
		if (flag < 0) {
			this.gameState &= flag;
		} else if (flag > 0) {
			this.gameState |= flag;
		} else {
			this.gameState = 0;
		}
		return this;
	}
	
	public void togglePause() {
		if (isPaused()) {
			set(GameState.FLAG_PLAYING);
		} else {
			set(GameState.FLAG_PAUSED);
		}
	}
	
	/**
	 * If a new milestone has been reached, remember it and return true, otherwise return false
	 * @return
	 */
	public boolean shouldIncreaseDifficulty() {
		if (this.gameTime - this.lastMilestone > 30) {
			this.lastMilestone = this.gameTime;
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if game is not in paused, victory, or game over mode
	 * @return
	 */
	public boolean isPlaying() {
		return 0 == (this.gameState & 7);
	}
	
	public boolean isGameOver() {
		return 1 == (this.gameState & 1);
	}
	
	public boolean isPaused() {
		return 1 == ((this.gameState >> 1) & 1);
	}
	
	public boolean isVictory() {
		return 1 == ((this.gameState >> 2) & 1);
	}
	
	public boolean isBallMoving() {
		return 1 == ((this.gameState >> 3) & 1);
	}
	
	public boolean isMuted() {
		return 1 == ((this.gameState >> 4) & 1);
	}
	
	public boolean isRedMode() {
		return 1 == ((this.gameState >> 5) & 1);
	}
	
	public boolean isAnimationOff() {
		return 1 == ((this.gameState >> 6) & 1);
	}
}
