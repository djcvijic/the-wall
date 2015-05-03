package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.sceneobject;

import java.awt.*;
import java.awt.geom.*;

import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Vector;

public class Ball implements SceneObject {
	private static final BasicStroke THICK_STROKE = new BasicStroke(0.05f);
	private static final double MAX_SPEED = 0.0012;
	private static final double MIN_SPEED = 0.00006;
	/**
	 * Circle used to render the ball
	 */
	private static final Ellipse2D CIRCLE = new Ellipse2D.Double(-0.5, -0.5, 1, 1);
	private static final GradientPaint COLOR_GRADIENT = new GradientPaint(-0.5f, -0.5f, Color.white, 0.5f, 0.5f, Color.black, true);
	
	private final Vector scale;
	/**
	 * Circle used to calculate the area of the ball.
	 * Always half a frame ahead of the visible ball.
	 */
	private final Ellipse2D areaCircle;
	
	private Vector position;
	private Vector velocity;
	/**
	 * Area used to collide the ball
	 */
	private Area area;
	
	/**
	 * Initializes all fields
	 * @param position Expressed compared to window scale (0..1, 0..1)
	 * @param velocity Expressed compared to window scale (0..1, 0..1), position shift per frame
	 * @param scale Expressed compared to window scale (0..1, 0..1)
	 */
	public Ball(final Vector position, final Vector velocity, final Vector scale) {
		this.position = position;
		this.velocity = velocity;
		this.scale = scale;
		
		this.areaCircle = new Ellipse2D.Double();
	}
	
	/**
	 * Moves the ball to its new location based on velocity,
	 * sets areaCircle to 1/2 frame ahead of the visible circle (converted to global coordinates),
	 * and sets area to that of areaCircle.
	 */
	public void update(final double deltaTime) {
		this.position = this.position.getSum(this.velocity.getProduct(deltaTime));
		this.areaCircle.setFrame(
				this.position.getX() + 0.5 * this.velocity.getX() * deltaTime - 0.5 * this.scale.getX(),
				this.position.getY() + 0.5 * this.velocity.getY() * deltaTime - 0.5 * this.scale.getY(),
				this.scale.getX(),
				this.scale.getY()
		);
		this.area = new Area(this.areaCircle);
	}
	
	/**
	 * Sets transformation matrix, enables anti-aliasing, and renders the ball with gradient fill and a stroke
	 * @param g2d
	 */
	public void draw(final Graphics2D g2d) {
		AffineTransform oldTransform = g2d.getTransform();
		
		g2d.translate(this.position.getX(), this.position.getY());
		g2d.scale(this.scale.getX(), this.scale.getY());
		g2d.setStroke(Ball.THICK_STROKE);
		
		RenderingHints oldHints = g2d.getRenderingHints();
		g2d.setRenderingHint(
        	    RenderingHints.KEY_ANTIALIASING,
        	    RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setPaint(Ball.COLOR_GRADIENT);
		g2d.fill(Ball.CIRCLE);
		g2d.setPaint(Color.white);
		g2d.draw(Ball.CIRCLE);
		
		g2d.setRenderingHints(oldHints);
		
		g2d.setTransform(oldTransform);
	}
	
	/**
	 * Used to move the ball along x axis while it is stuck to the paddle
	 * @param x Absolute x, expressed in local coordinates
	 */
	public void setX(final double x) {
		this.position = new Vector(x, this.position.getY(), this.position.getZ());
	}
	
	/**
	 * Modifies the intensity of the ball velocity by factor, clamped between MIN_SPEED and MAX_SPEED
	 * @param factor
	 */
	public void modifyVelocity(final double factor) {
		double oldSpeed = this.velocity.getLength();
		double newSpeed = factor * oldSpeed;
		if ((newSpeed > Ball.MAX_SPEED && factor > 1) ||
				(newSpeed <  Ball.MIN_SPEED && factor < 1)){
			return;
		}
		this.velocity = this.velocity.getProduct(newSpeed / this.velocity.getLength());
	}
	
	/**
	 * Used to calculate collisions with the ball
	 * @return java.awt.geom.Area
	 */
	public Area getArea() {
		return this.area;
	}
	
	/**
	 * Simple elastic collision, where X or Y or both or neither may be inverted
	 * @param nVector Sum of normal vectors of all colliding edges
	 */
	public void collide(final Vector nVector) {
		if (nVector.getX() * this.velocity.getX() < 0) {
			this.velocity = new Vector(
					-this.velocity.getX(),
					this.velocity.getY(),
					this.velocity.getZ()
			);
		}
		if (nVector.getY() * this.velocity.getY() < 0) {
			this.velocity = new Vector(
					this.velocity.getX(),
					-this.velocity.getY(),
					this.velocity.getZ()
			);
		}
	}
}
