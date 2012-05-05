package edu.nyu.entity;

/**
 * The point in the arena.
 */
public class Point implements Comparable<Point> {

	public int x;

	public int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public double distanceTo(int otherX, int otherY) {
		
		//return Math.abs(x - otherX) + Math.abs(y - otherY);
		return Math.sqrt(Math.pow(x - otherX, 2) + Math.pow(y - otherY, 2));
	}

	public double distanceTo(Point otherPoint) {
		//return Math.abs(x - otherPoint.x) + Math.abs(y - otherPoint.y);
		return Math.sqrt(Math.pow(x - otherPoint.x, 2) + Math.pow(y - otherPoint.y, 2));

	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public int compareTo(Point o) {
		if (x < o.x) {
			return -1;
		} else if (x == o.x) {
			if (y < o.y) {
				return -1;
			} else if (y == o.y) {
				return 0;
			}
		}
		return 1;
	}

}
