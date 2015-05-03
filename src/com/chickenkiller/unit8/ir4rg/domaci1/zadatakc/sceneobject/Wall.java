package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.sceneobject;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Collision;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.OrientedArea;
import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Vector;

public class Wall implements SceneObject, Collidable {
	private static final int BRICKS_PER_ROW = 13;
	public static final Vector STARTING_POSITION = new Vector(
			1.0 / (Wall.BRICKS_PER_ROW + 2), 1.0 / (Wall.BRICKS_PER_ROW + 2)
	);
	public static final double WALL_WIDTH = 1 - 2 * Wall.STARTING_POSITION.getX();
	private static final int MAX_ROWS = 25;
	private static final Vector BRICK_SCALE = new Vector(
			Wall.WALL_WIDTH / Wall.BRICKS_PER_ROW, 1.0 / (Wall.MAX_ROWS + 5)
	);
	private static final String BRICK_ALLOWED_VALUES = "012345678";
	
	private final ArrayList<Brick> bricks;
	/**
	 * Temporary area used to check collision with ball.
	 */
	private final Area intersection;
	
	/**
	 * Total unbroken bricks in the wall.
	 */
	private int durability;
	
	/**
	 * Parses the lines of bricks and adds them to the brick list
	 * @param rows Lines of bricks read from the level file
	 */
	public Wall(final ArrayList<String> rows) {
		this.bricks = new ArrayList<Brick>();
		this.intersection = new Area();
		for (int i = 0; (i < rows.size()) && (i < Wall.MAX_ROWS); i += 1) {
			String row = rows.get(i);
			for (int j = 0; (j < row.length() / 2) && (j < Wall.BRICKS_PER_ROW); j += 1) {
				// Hash means start of comment, so skip the rest of the line
				if (row.charAt(j) == '#' || row.charAt(j + 1) == '#') {
					break;
				}
				Vector position = new Vector(
						Wall.BRICK_SCALE.getX() * j,
						Wall.BRICK_SCALE.getY() * i
				).getSum(Wall.STARTING_POSITION);
				/*
				 * First number in a pair is durability, second is color
				 * Durability is a number (1..8) of hits to break block, or 0 for invincible brick
				 * Color is a number (0..7) 3-bit representation of RGB color, or 8 for random color
				 */
				char durabilityChar = row.charAt(2 * j),
						colorChar = row.charAt(2 * j + 1);
				// If the durability or color is invalid, just skip the current brick
				if ((Wall.BRICK_ALLOWED_VALUES.indexOf(durabilityChar) == -1) || 
						(Wall.BRICK_ALLOWED_VALUES.indexOf(colorChar) == -1)) {
					continue;
				}
				int durability = Character.getNumericValue(durabilityChar),
						colorNum = Character.getNumericValue(colorChar);
				this.bricks.add(new Brick(position, Wall.BRICK_SCALE, durability, colorNum));
			}
		}
		this.updateDurability();
	}
	
	public void update(final double deltaTime) {
		for (Brick b: this.bricks) {
			b.update(deltaTime);
		}
	}
	
	public void draw(final Graphics2D g2d) {
		RenderingHints oldHints = g2d.getRenderingHints();
		g2d.setRenderingHint(
        	    RenderingHints.KEY_ANTIALIASING,
        	    RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (Brick b: this.bricks) {
			b.draw(g2d);
		}
		
		g2d.setRenderingHints(oldHints);
	}

	/**
	 * Checks collision with passed area of all bricks that are unbroken
	 * @param ball Collision area of the ball
	 */
	public void testCollision(final Area ball) throws Collision {
		Vector nVector = Vector.ZERO;
		for (Brick b: this.bricks) {
			// First checks collision with whole brick area
			if (b.isBroken()) {
				continue;
			}
			this.intersection.reset();
			this.intersection.add(ball);
			this.intersection.intersect(b.getArea());
			if (this.intersection.isEmpty()) {
				continue;
			}
			// If brick collision passes, collide the brick and reduce wall durability...
			b.collide();
			this.updateDurability();
			OrientedArea[] edgeAreas = b.getEdgeAreas();
			for (OrientedArea edgeArea: edgeAreas) {
				// ... and only then check collision with individual brick edges...
				this.intersection.reset();
				this.intersection.add(ball);
				this.intersection.intersect(edgeArea.area);
				if (this.intersection.isEmpty()) {
					continue;
				}
				// ... and sum it all up in the total normal vector.
				nVector = nVector.getSum(edgeArea.nVector);
			}
		}
		if (nVector.getLengthSquared() > 0) {
			throw new Collision(nVector.getNormalized());
		}
	}
	
	public int getDurability() {
		return this.durability;
	}
	
	private void updateDurability() {
		this.durability = 0;
		for (Brick brick: this.bricks) {
			this.durability += (brick.isBroken()) ? 0 : 1;
		}
	}
}
