package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.sceneobject;

import java.awt.*;
import java.awt.geom.*; 
import java.util.Random;

import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.GameState;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.SoundManager;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.OrientedArea;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Polygon;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Vector;

public class Brick implements SceneObject {
	private static final BasicStroke MEDIUM_STROKE = new BasicStroke(0.01f);
	/**
	 * Brick fade time in milliseconds
	 */
	private static final int FADE_LENGTH = 400;
	private static final Rectangle2D BACK_RECTANGLE = new Rectangle2D.Double(0, 0, 1, 1);
	private static final Polygon TOP_TRIANGLE = new Polygon(new double[]{0, 0, 1}, new double[]{0, 1, 0});
	private static final Line2D DIAGONAL_LINE = new Line2D.Double(0.0, 0.0, 1.0, 1.0);
	private static final Rectangle2D CENTRAL_RECTANGLE = new Rectangle2D.Double(0.2, 0.2, 0.6, 0.6);
	
	private final Vector position;
	private final Vector scale;
	/**
	 * Area used to detect general collision
	 */
	private final Area area;
	/**
	 * Areas used to detect which edge(s) are having a collision:
	 * 0 - top,
	 * 1 - bottom,
	 * 2 - left,
	 * 3 - right
	 */
	private final OrientedArea[] edgeAreas;
	
	private Color baseColor;
	private Color lighterColor = Color.WHITE;
	private GradientPaint colorGradient;
	private boolean broken = false;
	private int durability;
	private int fadeTime = 0;
	/*
	 * Four fields used for holding source and destination colors for fading animation
	 */
	private Color prevBaseColor = null;
	private Color prevLighterColor = null;
	private Color nextBaseColor = null;
	private Color nextLighterColor = null;

	/**
	 * Initializes all fields, randomizes color if necessary,
	 * calls updateColors, and instantiates all edge areas.
	 * @param position Expressed compared to window scale (0..1, 0..1)
	 * @param scale Expressed compared to window scale (0..1, 0..1)
	 * @param durability Number (1..8) of hits to break block, or 0 for invincible brick
	 * @param colorNum Number (0..7) 3-bit representation of RGB color, or 8 for random color
	 */
	public Brick(final Vector position, final Vector scale, final int durability, int colorNum) {
		this.position = position;
		this.scale = new Vector(scale);
		
		this.durability = durability;
		
		if (colorNum == 8) {
			colorNum = (int)(new Random().nextDouble() * 8);
		}
		this.baseColor = new Color(
				(float) (colorNum >> 2),
				(float) ((colorNum >> 1) & 1),
				(float) (colorNum & 1),
				0.7f
		);
		this.updateColors();
		
		this.area = new Area(new Rectangle2D.Double(
				position.getX(),
				position.getY(),
				scale.getX(),
				scale.getY()
		));
		this.edgeAreas = new OrientedArea[4];
		this.edgeAreas[0] = new OrientedArea(
				new Area(
					new Rectangle2D.Double(
							position.getX(),
							position.getY(),
							scale.getX(),
							0.001
					)
				),
				Vector.MINUS_Y_UNIT
		);
		this.edgeAreas[1] = new OrientedArea(
				new Area(
					new Rectangle2D.Double(
							position.getX(),
							position.getY() + scale.getY(),
							scale.getX(),
							0.001
					)
				),
				Vector.Y_UNIT
		);
		this.edgeAreas[2] = new OrientedArea(
				new Area(
					new Rectangle2D.Double(
							position.getX(),
							position.getY(),
							0.001,
							scale.getY()
					)
				),
				Vector.MINUS_X_UNIT
		);
		this.edgeAreas[3] = new OrientedArea(
				new Area(
					new Rectangle2D.Double(
							position.getX() + scale.getX(),
							position.getY(),
							0.001,
							scale.getY()
					)
				),
				Vector.X_UNIT
		);
	}
	
	/**
	 * If brick fade has started, lowers the fadeTime counter and fades the colors
	 */
	public void update(final double deltaTime) {
		if (this.fadeTime <= 0) {
			return;
		}
		this.fadeTime = (int) Math.max(this.fadeTime - deltaTime, 0);
		double fadeBlend = 1 - 1.0 * this.fadeTime / Brick.FADE_LENGTH;
		this.baseColor = this.blendColors(this.prevBaseColor, this.nextBaseColor, fadeBlend);
		this.lighterColor = this.blendColors(this.prevLighterColor, this.nextLighterColor, fadeBlend);
		this.updateColors();
	}
	
	/**
	 * If brick is broken and faded, return.
	 * Sets transformation matrix, draws top and bottom triangle,
	 * diagonal line, and central rectangle.
	 * @param g2d
	 */
	public void draw(final Graphics2D g2d) {
		if (this.broken && this.fadeTime == 0) {
			return;
		}
		
		AffineTransform oldTransform = g2d.getTransform();
		
		g2d.translate(this.position.getX(), this.position.getY());
		g2d.scale(this.scale.getX(), this.scale.getY());
		g2d.setStroke(Brick.MEDIUM_STROKE);
		
		g2d.setPaint(this.baseColor);
		g2d.fill(Brick.BACK_RECTANGLE);
		g2d.setPaint(Color.black);
		g2d.draw(Brick.BACK_RECTANGLE);
		
		g2d.setPaint(this.lighterColor);
		g2d.fill(Brick.TOP_TRIANGLE);
		g2d.setPaint(Color.black);
		g2d.draw(Brick.TOP_TRIANGLE);
		
		g2d.setPaint(Color.black);
		g2d.draw(Brick.DIAGONAL_LINE);
		
		g2d.setPaint(this.colorGradient);
		g2d.fill(Brick.CENTRAL_RECTANGLE);
		g2d.setPaint(Color.black);
		g2d.draw(Brick.CENTRAL_RECTANGLE);
		
		g2d.setTransform(oldTransform);
	}
	
	/**
	 * Durability zero means brick is invincible so do nothing.
	 * Otherwise either break the brick, or weaken and darken it.
	 * If animation is turned off, fade should be instant.
	 */
	public void collide() {
		if (this.durability == 0) {
			SoundManager.getInstance().play("ceiling.wav");
			return;
		}
		
		SoundManager.getInstance().play("brick.wav");
		if (GameState.getInstance().isAnimationOff()) {
			this.fadeTime = 1;
		} else {
			this.fadeTime = Brick.FADE_LENGTH;
		}
		
		// Set the starting color to fully opaque, to get a nice reactive effect
		this.prevBaseColor = new Color(
			this.baseColor.getRed(),
			this.baseColor.getGreen(),
			this.baseColor.getBlue(),
			255
		);
		this.prevLighterColor = new Color(
			this.lighterColor.getRed(),
			this.lighterColor.getGreen(),
			this.lighterColor.getBlue(),
			255
		);
		
		if (this.durability == 1) {
			this.broken = true;
			// Set the ending color to fully transparent, to make it disappear
			this.nextBaseColor = new Color(
				this.baseColor.getRed(),
				this.baseColor.getGreen(),
				this.baseColor.getBlue(),
				0
			);
			this.nextLighterColor = new Color(
				this.lighterColor.getRed(),
				this.lighterColor.getGreen(),
				this.lighterColor.getBlue(),
				0
			);
		} else {
			this.durability -= 1;
			// Set the ending color to a darker one, to make it look damaged
			this.nextBaseColor = this.baseColor.darker();
			this.nextLighterColor = this.lighterColor.darker();
		}
	}
	
	public Area getArea() {
		return this.area;
	}
	
	public OrientedArea[] getEdgeAreas() {
		return this.edgeAreas;
	}
	
	public boolean isBroken() {
		return this.broken;
	}
	
	/**
	 * Blends between two colors based on blendLevel: 0 -> sourceColor, 1 -> destinationColor
	 * @param sourceColor Color to blend from
	 * @param destinationColor Color to blend to
	 * @param blendLevel Between 0 and 1
	 * @return The blended color
	 * @throws IllegalArgumentException If any of the input colors is null, or blendLevel is not between 0 and 1.
	 */
	private Color blendColors(final Color sourceColor, final Color destinationColor, final double blendLevel) {
		if (sourceColor == null || destinationColor == null || blendLevel < 0 || blendLevel > 1) {
			throw new IllegalArgumentException();
		}
		return new Color(
			(int) (sourceColor.getRed() * (1 - blendLevel) + destinationColor.getRed() * blendLevel),
			(int) (sourceColor.getGreen() * (1 - blendLevel) + destinationColor.getGreen() * blendLevel),
			(int) (sourceColor.getBlue() * (1 - blendLevel) + destinationColor.getBlue() * blendLevel),
			(int) (sourceColor.getAlpha() * (1 - blendLevel) + destinationColor.getAlpha() * blendLevel)
		);
	}
	
	/**
	 * Updates the colorGradient based on the current lighterColor and baseColor
	 */
	private void updateColors() {
		this.colorGradient = new GradientPaint(
				0,
				0,
				this.lighterColor,
				1,
				1,
				this.baseColor
		);
	}
}
