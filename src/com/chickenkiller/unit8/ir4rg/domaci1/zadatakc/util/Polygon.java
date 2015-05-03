package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util;

import java.awt.geom.Path2D;

public class Polygon extends Path2D.Double {
	private static final long serialVersionUID = 278382492205000915L;

	public Polygon(final double[] pointsX, final double[] pointsY) {
		super();
		this.moveTo(pointsX[0], pointsY[0]);
		for (int i = 1; i < pointsX.length; i += 1) {
		   this.lineTo(pointsX[i], pointsY[i]);
		}
		this.closePath();
	}
	
	public Polygon(final Vector[] points) {
		super();
		this.moveTo(points[0].getX(), points[0].getY());
		for (int i = 1; i < points.length; i += 1) {
			this.lineTo(points[i].getX(), points[i].getY());
		}
		this.closePath();
	}
}
