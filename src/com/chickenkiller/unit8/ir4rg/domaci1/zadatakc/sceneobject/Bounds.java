package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.sceneobject;

import java.awt.*;
import java.awt.geom.*;

import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.SoundManager;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Collision;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Vector;

public class Bounds implements SceneObject, Collidable {
	private static final Stroke HAIRLINE_STROKE = new BasicStroke(0);
	/**
	 * Area used to detect if other object is out-of-bounds
	 */
	private static final Area BOUNDS_AREA = new Area(new Rectangle2D.Double(0, 0, 1, 1));
	private static final GradientPaint DIAGONAL_GRADIENT = new GradientPaint(-0.1f, -0.1f, Color.black, 0.35f, 0.35f, Color.white, true);
	private static final GradientPaint VERTICAL_GRADIENT = new GradientPaint(0, -0.1f, Color.black, 0, 0.4f, Color.lightGray, true);
	private static final RoundRectangle2D CEILING_SHAPE = new RoundRectangle2D.Double(0, 0, 1, 1, 0.1, 0.5);
	private static final Ellipse2D CORNER_ORB = new Ellipse2D.Double(0, 0, 1, 1);
	private static final Rectangle2D BOTTOM_RECTANGLE = new Rectangle2D.Double(0, 0, 1, 1);
	
	/**
	 * Three areas used to detect collision between ball and edges
	 * 0 - right edge,
	 * 1 - ceiling,
	 * 2 - left edge
	 */
	private final Area[] edgeAreas;
	/**
	 * Temporary area used for detecting collisions
	 */
	private final Area intersection;
	/**
	 * Four lightning walls, two used for each edge:
	 * 0 - far left,
	 * 1 - mid left,
	 * 2 - mid right,
	 * 3 - far right
	 */
	private final LightningWall[] lightningWalls;
	
	private GradientPaint bottomGradient;
	
	/**
	 * Initializes all fields, instantiates all edge areas, instantiates all lightning walls, and calls setColor
	 * @param startingPosition Expressed compared to window scale (0..1, 0..1)
	 * @param baseColor Affects lightning wall and out-of-bounds area colors
	 */
	public Bounds(final double startingPosition, final Color baseColor) {
		/*
		 * 0 - right edge,
		 * 1 - ceiling,
		 * 2 - left edge
		 */
		Rectangle2D[] edgeRectangles = new Rectangle2D[3];
		edgeRectangles[0] = new Rectangle2D.Double(
				1 - startingPosition,
				startingPosition,
				startingPosition,
				1 - startingPosition
		);
		edgeRectangles[1] = new Rectangle2D.Double(
				0,
				0,
				1,
				startingPosition
		);
		edgeRectangles[2] = new Rectangle2D.Double(
				0,
				startingPosition,
				startingPosition,
				1 - startingPosition
		);
		this.edgeAreas = new Area[3];
		for (int i = 0; i < 3; i += 1) {
			this.edgeAreas[i] = new Area(edgeRectangles[i]);
		}
		
		this.intersection = new Area();
		
		/*
		 * 0 - far left,
		 * 1 - mid left,
		 * 2 - mid right,
		 * 3 - far right
		 */
		this.lightningWalls = new LightningWall[4];
		this.lightningWalls[0] = new LightningWall(
				new Vector(0, startingPosition),
				new Vector(startingPosition / 2, 1 - 2 * startingPosition),
				1
		);
		this.lightningWalls[1] = new LightningWall(
				new Vector(startingPosition / 2, startingPosition),
				new Vector(startingPosition / 2, 1 - 2 * startingPosition),
				0
		);
		this.lightningWalls[2] = new LightningWall(
				new Vector(1 - startingPosition, startingPosition),
				new Vector(startingPosition / 2, 1 - 2 * startingPosition),
				1
		);
		this.lightningWalls[3] = new LightningWall(
				new Vector(1 - startingPosition / 2, startingPosition),
				new Vector(startingPosition / 2, 1 - 2 * startingPosition),
				0
		);
		
		this.setColor(baseColor);
	}
	
	/**
	 * Updates the lightning walls
	 */
	public void update(final double deltaTime) {
		for (LightningWall lightningWall: this.lightningWalls) {
			lightningWall.update(deltaTime);
		}
	}
	
	/**
	 * Draws out-of-bounds area, ceiling, sets anti-aliasing, draws the bulbs, and draws the lightning walls
	 * @param g2d
	 */
	public void draw(final Graphics2D g2d) {
		g2d.setStroke(Bounds.HAIRLINE_STROKE);
		
		AffineTransform oldTransform = g2d.getTransform();
		
		Vector startingPosition = Wall.STARTING_POSITION;
		
		g2d.translate(0, 1 - startingPosition.getY());
		g2d.scale(1, startingPosition.getY());
		g2d.setPaint(this.bottomGradient);
		g2d.fill(Bounds.BOTTOM_RECTANGLE);
		
		g2d.setTransform(oldTransform);
		
		g2d.translate(startingPosition.getX(), 0);
		g2d.scale(1 - 2 * startingPosition.getX(), startingPosition.getY());
		g2d.setPaint(Bounds.VERTICAL_GRADIENT);
		g2d.fill(Bounds.CEILING_SHAPE);
		
		g2d.setTransform(oldTransform);
		
		RenderingHints oldHints = g2d.getRenderingHints();
		g2d.setRenderingHint(
        	    RenderingHints.KEY_ANTIALIASING,
        	    RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Top left bulb
		g2d.translate(0, 0);
		g2d.scale(startingPosition.getX(), startingPosition.getY());
		g2d.setPaint(Bounds.DIAGONAL_GRADIENT);
		g2d.fill(Bounds.CORNER_ORB);

		g2d.setTransform(oldTransform);
		
		// Top right bulb
		g2d.translate(1 - startingPosition.getX(), 0);
		g2d.scale(startingPosition.getX(), startingPosition.getY());
		g2d.setPaint(Bounds.DIAGONAL_GRADIENT);
		g2d.fill(Bounds.CORNER_ORB);
		
		g2d.setTransform(oldTransform);
		
		// Bottom left bulb
		g2d.translate(0, 1 - startingPosition.getY());
		g2d.scale(startingPosition.getX(), startingPosition.getY());
		g2d.setPaint(Bounds.DIAGONAL_GRADIENT);
		g2d.fill(Bounds.CORNER_ORB);
		
		g2d.setTransform(oldTransform);
		
		// Bottom right bulb
		g2d.translate(1 - startingPosition.getX(), 1 - startingPosition.getY());
		g2d.scale(startingPosition.getX(), startingPosition.getY());
		g2d.setPaint(Bounds.DIAGONAL_GRADIENT);
		g2d.fill(Bounds.CORNER_ORB);
		
		g2d.setTransform(oldTransform);
		
		g2d.setRenderingHints(oldHints);

		for (LightningWall lightningWall: this.lightningWalls) {
			lightningWall.draw(g2d);
		}
	
		g2d.setTransform(oldTransform);
	}
	
	/**
	 * Check if ball is colliding with the ceiling or an edge
	 * @param ball
	 */
	public void testCollision(final Area ball) throws Collision {
		for (int i = 0; i < this.edgeAreas.length; i += 1) {
			this.intersection.reset();
			this.intersection.add(ball);
			this.intersection.intersect(this.edgeAreas[i]);
			if (this.intersection.isEmpty()) {
				continue;
			}
			Vector nVector = null;
			switch (i) {
			case 0:
				// Right edge
				SoundManager.getInstance().play("lightning.wav");
				nVector = Vector.MINUS_X_UNIT;
				break;
			case 1:
				// Ceiling
				SoundManager.getInstance().play("ceiling.wav");
				nVector = Vector.Y_UNIT;
				break;
			case 2:
				// Left edge
				SoundManager.getInstance().play("lightning.wav");
				nVector = Vector.X_UNIT;
				break;
			}
			if (nVector != null) {
				throw new Collision(nVector);
			}
		}
	}
	
	/**
	 * Sets out-of-bounds area color and lightning wall color based on baseColor
	 * @param baseColor
	 */
	public void setColor(final Color baseColor) {
		this.bottomGradient = new GradientPaint(0, 0, baseColor.darker().darker().darker(), 0, 1, Color.black);
		
		for (LightningWall lightningWall: this.lightningWalls) {
			lightningWall.setColor(baseColor);
		}
	}
	
	/**
	 * Check if ball is out-of-bounds
	 * @param ball
	 * @return True if ball is out-of-bounds, otherwise false
	 */
	public boolean isOutOfBounds(final Area ball) {
		this.intersection.reset();
		this.intersection.add(ball);
		this.intersection.intersect(Bounds.BOUNDS_AREA);
		if (this.intersection.isEmpty()) {
			return true;
		}
		return false;
	}
}
