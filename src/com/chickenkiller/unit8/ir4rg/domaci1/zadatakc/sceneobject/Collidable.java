package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.sceneobject;

import java.awt.geom.Area;

import com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util.Collision;

public interface Collidable {
	public void testCollision(final Area ballArea) throws Collision;
}
