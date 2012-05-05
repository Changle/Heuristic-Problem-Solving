package edu.nyu.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * the super class for every strategy.
 */
public abstract class VoronoiStrategy {

	public static HashMap<Point, Integer> pointArea = new HashMap<Point, Integer>();
	public static HashMap<Point, Point> pointCenter = new HashMap<Point, Point>();

	public abstract Point choosePoint(Set<Point> myPoints,
			Set<Point> oppPoints, int arenaWidth, int arenaHeight);

	/**
	 * @param points
	 *            all the occupied points in the arena
	 * @param arenaWidth
	 * @param arenaHeight
	 * @return a map, its key is the chosen point, its value is the number of
	 *         points belong to it.
	 */
	public static void getPointsOccupiedNum(Set<Point> points, int arenaWidth,
			int arenaHeight) {

		pointArea.clear();
		pointCenter.clear();

		for (Point point : points) {
			pointArea.put(point, 0);
			pointCenter.put(point, new Point(0, 0));
		}

		for (int i = 0; i < arenaWidth; i++) {
			for (int j = 0; j < arenaHeight; j++) {

				double minDistance = Double.MAX_VALUE;
				Point nearestPoint = null;
				for (Point p : points) {
					double d = p.distanceTo(i, j);
					if (d < minDistance) {
						nearestPoint = p;
						minDistance = d;
					}
				}

				pointArea.put(nearestPoint, pointArea.get(nearestPoint) + 1);
				Point center = pointCenter.get(nearestPoint);
				center.x += i;
				center.y += j;
				pointCenter.put(nearestPoint, center);
			}
		}

		for (Point point : points) {
			Integer count = pointArea.get(point);
			Point center = pointCenter.get(point);
			center.x /= count;
			center.y /= count;
			pointCenter.put(point, center);
		}

	}

	public static Map<Point, Integer> CalculateArea(Set<Point> points,
			int arenaWidth, int arenaHeight) {

		Map<Point, Integer> map = new HashMap<Point, Integer>();

		for (Point point : points) {
			map.put(point, 0);
		}
		for (int i = 0; i < arenaWidth; i++) {
			for (int j = 0; j < arenaHeight; j++) {
				double minDistance = Double.MAX_VALUE;
				Point nearestPoint = null;

				for (Point p : points) {
					double d = p.distanceTo(i, j);
					if (d < minDistance) {
						minDistance = d;
						nearestPoint = p;
					}
				} 
				if(nearestPoint!=null)
				map.put(nearestPoint, map.get(nearestPoint) + 1);
			}
		}
		return map;
	}

	public boolean isPointInArena(int x, int y, int arenaWidth, int arenaHeight) {
		if (x >= 0 && x < arenaWidth && y >= 0 && y < arenaHeight) {
			return true;
		}
		return false;
	}

}
