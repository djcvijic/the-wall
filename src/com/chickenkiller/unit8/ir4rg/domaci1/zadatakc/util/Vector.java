package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc.util;

public final class Vector {
	public static final Vector ZERO           = new Vector(0, 0, 0);
	public static final Vector IDENTITY       = new Vector(1, 1, 1);
	public static final Vector X_UNIT         = new Vector(1, 0, 0);
	public static final Vector Y_UNIT         = new Vector(0, 1, 0);
	public static final Vector Z_UNIT         = new Vector(0, 0, 1);
	public static final Vector MINUS_IDENTITY = new Vector(-1, -1, -1);
	public static final Vector MINUS_X_UNIT   = new Vector(-1, 0, 0);
	public static final Vector MINUS_Y_UNIT   = new Vector(0, -1, 0);
	public static final Vector MINUS_Z_UNIT   = new Vector(0, 0, -1);
	
	private final double x;
	private final double y;
	private final double z;
	
	public Vector(final double x, final double y, final double z) {
		this.x = x; this.y = y; this.z = z;
	}
	
	public Vector(final double x, final double y) {
		this(x, y, 0);
	}
	
	public Vector() {
		this(0, 0, 0);
	}
	
	public Vector(final Vector v) {
		this(v.x, v.y, v.z);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	public boolean equals(final Vector v) {
		return (this.x == v.getX()) && (this.y == v.getY()) && (this.z == v.getZ());
	}
	
	public Vector getSum(final Vector v) {
		return new Vector(this.x + v.x, this.y + v.y, this.z + v.z);
	}
	
	public Vector getNegated() {
		return this.getProduct(-1);
	}
	
	public Vector getDifference(final Vector v) {
		return this.getSum(v.getNegated());
	}
	
	public Vector getProduct(final double f) {
		return new Vector(this.x * f, this.y * f, this.z * f);
	}
	
	public double getX() { return this.x; }
	public double getY() { return this.y; }
	public double getZ() { return this.z; }
	
	public double getLengthSquared() {
		return this.getDotProduct(this);
	}
	
	public double getLength() {
		return Math.sqrt(this.getLengthSquared());
	}

	public double getDistanceSquared(final Vector v) {
		return Math.pow(this.x - v.x, 2) + Math.pow(this.y - v.y, 2) + Math.pow(this.z - v.z, 2);
	}
	
	public double getDistance(final Vector v) {
		return Math.sqrt(this.getDistanceSquared(v));
	}
	
	public double getDotProduct(final Vector v) {
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}
	
	public Vector getCrossProduct(final Vector v) {
		return new Vector(
				this.y * v.z - this.z * v.y,
				this.z * v.x - this.x * v.z,
				this.x * v.y - this.y * v.x
		);
	}
	
	/**
	 * Calculates the absolute angle between this and a second vector, in radians
	 * @param v The second vector
	 * @return The calculated angle, or zero if the length of either vector is zero
	 */
	public double getAngle(final Vector v) {
		double length = this.getLength(),
				vLength = v.getLength();
		if (length == 0 || vLength == 0) {
			return 0;
		}
		return Math.acos(this.getDotProduct(v) / (length * vLength));
	}
	
	/**
	 * Calculates the signed angle between this and a second vector, in radians.
	 * The referent direction is from X to Y axis.
	 * Only works in XY plane, so 3D vectors will first be projected onto it.
	 * @param v The second vector
	 * @return The calculated angle, or zero if the length of either vector is zero
	 */
	public double getAngleSigned(final Vector v) {
		Vector vectA = this,
				vectB = v;
		if (this.z != 0) {
			vectA = new Vector(this.x, this.y, 0);
		}
		if (v.getZ() != 0) {
			vectB = new Vector(v.x, v.y, 0);
		}
		double absoluteAngle = vectA.getAngle(vectB),
				crossProductZ = vectA.getCrossProduct(vectB).z;
		if (crossProductZ == 0) {
			// Angle between vectors may be PI. In that case result should be positive.
			crossProductZ = 1;
		}
		return absoluteAngle * Math.signum(crossProductZ);
	}
	
	public Vector getResized(final double f) {
		return this.getProduct(f / this.getLength());
	}
	
	public Vector getNormalized() {
		return this.getResized(1.0);
	}
}
