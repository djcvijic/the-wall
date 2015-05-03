package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util;

public class Collision extends Throwable {
	private static final long serialVersionUID = 4156612417936861027L;

	private final Vector nVector;
	
	public Collision(final Vector nVector) {
		super();
		this.nVector = nVector;
	}
	
	public Vector getNVector() {
		return this.nVector;
	}
}
