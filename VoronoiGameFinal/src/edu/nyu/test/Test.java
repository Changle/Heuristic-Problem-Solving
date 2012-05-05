package edu.nyu.test;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.nyu.entity.Point;
import edu.nyu.entity.VoronoiStrategy;
import edu.nyu.voronoi.BiggestPoly;

/**
 * Test if the getPointsOccupiedNum method and choosePoint method is right.
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VoronoiStrategy strategy = new BiggestPoly();
		Set<Point> myPoints = new TreeSet<Point>();
		myPoints.add(new Point(5, 5));
		Set<Point> oppPoints = new TreeSet<Point>();
		oppPoints.add(new Point(3, 4));
		int arenaWidth = 10;
		int arenaHeight = 10;
		Set<Point> points = new TreeSet<Point>();
		points.addAll(myPoints);
		points.addAll(oppPoints);
		VoronoiStrategy.getPointsOccupiedNum(points,
				arenaWidth,
				arenaHeight);
		Map<Point, Integer> map = VoronoiStrategy.pointArea;
		
		if (map.get(myPoints.iterator().next()) != 54) {
			System.err.println("my point wrong");
		}
		if (map.get(oppPoints.iterator().next()) != 44) {
			System.err.println("opp point wrong");
		}
		System.out.println(map);

		Point chosenPoint = strategy.choosePoint(myPoints, oppPoints, arenaWidth, arenaHeight);
		if (!chosenPoint.equals(new Point(2, 4))) {
			System.err.println("wrong step" + chosenPoint);
		}

	}

}
