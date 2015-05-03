package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.sceneobject;

import java.awt.*;
import java.awt.geom.*;

import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.SoundManager;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Collision;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Vector;

public class Paddle implements SceneObject, Collidable {
	private static final BasicStroke MEDIUM_STROKE = new BasicStroke(0.01f);
	/**
	 * Round rectangle used for drawing the paddle
	 */
	private static final RoundRectangle2D SHAPE = new RoundRectangle2D.Double(-0.5, -0.125, 1.0, 0.25, 0.25, 0.25);
	
	private final Vector scale;
	/**
	 * Rectangle used to calculate the paddleArea
	 */
	private final Rectangle2D areaRectangle;
	/**
	 * Temporary area used to store intersection results
	 */
	private final Area intersection;
	
	private Vector position;
	private GradientPaint colorGradient;
	/**
	 * Area used for collision. Spans whole X of paddle, but only narrow strip on top edge.
	 */
	private Area paddleArea;
	
	/**
	 * Initializes all fields, and calls updateArea
	 * @param position Expressed compared to window scale (0..1, 0..1)
	 * @param scale Expressed compared to window scale (0..1, 0..1)
	 * @param baseColor
	 */
	public Paddle(final Vector position, final Vector scale, final Color baseColor) {
		this.position = position;
		this.scale = scale;
		
		this.colorGradient = new GradientPaint(0, -0.025f, Color.white, 0, 0.1f, baseColor, true);
		
		this.areaRectangle = new Rectangle2D.Double();
		this.intersection = new Area();
		this.updateArea();
	}
	
	public void update(final double deltatTime) {
		this.updateArea();
	}
	
	/**
	 * Sets transformation matrix, enables anti-aliasing, and draws the paddle
	 * @param g2d
	 */
	public void draw(final Graphics2D g2d) {
		AffineTransform oldTransform = g2d.getTransform();
		
		g2d.translate(this.position.getX(), this.position.getY());
		g2d.scale(this.scale.getX(), this.scale.getY());
		g2d.setStroke(MEDIUM_STROKE);
		
		RenderingHints oldHints = g2d.getRenderingHints();
		g2d.setRenderingHint(
        	    RenderingHints.KEY_ANTIALIASING,
        	    RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setPaint(this.colorGradient);
		g2d.fill(Paddle.SHAPE);
		g2d.setPaint(Color.white);
		g2d.draw(Paddle.SHAPE);
		
		g2d.setRenderingHints(oldHints);
		
		g2d.setTransform(oldTransform);
	}
	
	/**
	 * Tests for collision of paddleArea with ball
	 * @param ball
	 */
	public void testCollision(final Area ball) throws Collision {
		this.intersection.reset();
		this.intersection.add(ball);
		this.intersection.intersect(this.paddleArea);
		if (!this.intersection.isEmpty()) {
			SoundManager.getInstance().play("paddle.wav");
			throw new Collision(Vector.MINUS_Y_UNIT);
		}
	}
	
	public Vector getPosition() {
		return this.position;
	}
	
	public Vector getScale() {
		return this.scale;
	}
	
	public void setX(final double x) {
		this.position = new Vector(x, this.position.getY(), this.position.getZ());
	}
	
	/**
	 * Sets the colorGradient based on c
	 * @param c
	 */
	public void setColor(final Color c) {
		this.colorGradient = new GradientPaint(0, -0.025f, Color.white, 0, 0.1f, c, true);
	}
	
	/**
	 * Updates the area rectangle based on position, and then updates paddleArea base on it
	 */
	private void updateArea() {
		this.areaRectangle.setFrame(
				this.position.getX() - 0.5 * this.scale.getX(),
				this.position.getY() - 0.125 * this.scale.getY(),
				1.0 * this.scale.getX(),
				0.0625 * this.scale.getY()
		);
		this.paddleArea = new Area(this.areaRectangle);
	}
}
