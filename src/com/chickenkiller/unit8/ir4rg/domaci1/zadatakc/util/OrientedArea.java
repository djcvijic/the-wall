package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util;

import java.awt.geom.Area;

public class OrientedArea {
	public final Area area;
	public final Vector nVector;
	
	public OrientedArea(final Area area, final Vector nVector) {
		this.area = area;
		this.nVector = nVector;
	}
}
