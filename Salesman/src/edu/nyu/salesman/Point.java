package edu.nyu.salesman;

public class Point {

	public double id;

	public double x;

	public double y;

	public double z;

	public Point() {}

	public Point(double id, double x, double y, double z) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString() {
		return id + ": x = " + x + ", y = " + y + ", z = " + z;
	}

}
