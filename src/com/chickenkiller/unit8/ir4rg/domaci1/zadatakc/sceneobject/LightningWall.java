package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.sceneobject;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Vector;

public class LightningWall implements SceneObject {
	private static final BasicStroke THIN_STROKE = new BasicStroke(0.005f);
	private static final Random RAND = new Random();
	private static final double ANIMATION_FRAME_LENGTH = 125;
	/**
	 * Total number of spark points including the two origin points (N + 2)
	 */
	private static final int SPARK_POINT_COUNT = 20;
	
	private final Vector position;
	private final Vector scale;
	private final ArrayList<Double> randomYs;
	/**
	 * Contains N + 2 vertexes for drawing the lightning
	 */
	private final double[] sparkPointsX;
	private final double[] sparkPointsY;
	/**
	 * Contains N + 1 lines drawn between adjacent spark points
	 */
	private final Line2D[] sparks;
	
	private double animationFrameTime;
	private Color color;
	
	/**
	 * Initializes all fields, instantiates sparkPoints and sparks, and calls updateAnimation
	 * @param position Expressed compared to window scale (0..1, 0..1)
	 * @param originX X component of origin point position, expressed in local scale (0..1)
	 * @param scale Expressed compared to window scale (0..1, 0..1)
	 */
	public LightningWall(final Vector position, final Vector scale, final double originX) {
		this.position = position;
		this.scale = scale;
		
		this.animationFrameTime = LightningWall.ANIMATION_FRAME_LENGTH;
		
		this.color = Color.white;
		
		this.randomYs = new ArrayList<Double>();
		
		this.sparkPointsX = new double[LightningWall.SPARK_POINT_COUNT];
		this.sparkPointsY = new double[LightningWall.SPARK_POINT_COUNT];
		this.sparkPointsX[0] = originX;
		this.sparkPointsY[0] = 0;
		this.sparkPointsX[LightningWall.SPARK_POINT_COUNT - 1] = originX;
		this.sparkPointsY[LightningWall.SPARK_POINT_COUNT - 1] = 1;
		
		this.sparks = new Line2D[LightningWall.SPARK_POINT_COUNT - 1];
		for (int i = 0; i < LightningWall.SPARK_POINT_COUNT - 1; i += 1) {
			this.sparks[i] = new Line2D.Double();
		}
		
		this.updateAnimation();
	}
	
	/**
	 * Decrements the animationFrame counter, and resets it and updates the animation if it reached zero
	 */
	public void update(final double deltaTime) {
		if ((this.animationFrameTime -= deltaTime) <= 0) {
			this.animationFrameTime = LightningWall.ANIMATION_FRAME_LENGTH;
			this.updateAnimation();
		}
	}
	
	/**
	 * Sets transformation matrix, enables anti-aliasing, and draws the sparks
	 * @param g2d
	 */
	public void draw(final Graphics2D g2d) {
		AffineTransform oldTransform = g2d.getTransform();
		
		g2d.translate(this.position.getX(), this.position.getY());
		g2d.scale(this.scale.getX(), this.scale.getY());
		g2d.setStroke(LightningWall.THIN_STROKE);
		
		RenderingHints oldHints = g2d.getRenderingHints();
		g2d.setRenderingHint(
        	    RenderingHints.KEY_ANTIALIASING,
        	    RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setPaint(this.color);
		for (Line2D spark: this.sparks) {
			g2d.draw(spark);
		}
		
		g2d.setRenderingHints(oldHints);
		
		g2d.setTransform(oldTransform);
	}
	
	/**
	 * Sets color based on Color c, making it brighter, and less saturated if necessary
	 * @param c
	 */
	public void setColor(Color c) {
		c = c.brighter().brighter().brighter();
		float red = Math.max(0.5f, (float) c.getRed() / 256),
				green = Math.max(0.5f, (float) c.getGreen() / 256),
				blue = Math.max(0.5f, (float) c.getBlue() / 256);
		this.color = new Color(red, green, blue);
	}
	
	/**
	 * First N random Y values (0..1) are generated and sorted.
	 * Then each spark point are given a random X value (0..1)
	 * and a Y value from the sorted sequence.
	 * Lastly the spark lines are given start and end coordinates based on the spark points.
	 */
	private void updateAnimation() {
		this.randomYs.clear();
		for (int i = 0; i < LightningWall.SPARK_POINT_COUNT - 2; i += 1) {
			this.randomYs.add(LightningWall.RAND.nextDouble());
		}
		Collections.sort(this.randomYs);
		
		for (int i = 1; i < LightningWall.SPARK_POINT_COUNT - 1; i += 1) {
			this.sparkPointsX[i] = LightningWall.RAND.nextDouble();
			this.sparkPointsY[i] = this.randomYs.get(i - 1);
		}
		
		for (int i = 0; i < LightningWall.SPARK_POINT_COUNT - 1; i += 1) {
			this.sparks[i].setLine(
					this.sparkPointsX[i],
					this.sparkPointsY[i],
					this.sparkPointsX[i + 1],
					this.sparkPointsY[i + 1]
			);
		}
	}
}
