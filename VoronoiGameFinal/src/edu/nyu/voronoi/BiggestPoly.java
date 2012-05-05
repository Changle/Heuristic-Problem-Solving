package edu.nyu.voronoi;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.nyu.entity.Point;
import edu.nyu.entity.VoronoiStrategy;
import edu.nyu.main.Main;

/**
 * A greedy algorithm to choose a new point in the arena.
 * Find the biggest polygon occupied by the opponent, and choose a point in
 * the polygon to separate the most of points from the polygon.
 */
public class BiggestPoly extends VoronoiStrategy {
	
	

	/**
	 * Choose a new point according to all the points in the arena.
	 */
	@Override
	public Point choosePoint(Set<Point> myPoints,
			Set<Point> oppPoints,
			int arenaWidth,
			int arenaHeight) {
		Point center = new Point(arenaWidth / 2, arenaHeight / 2);
		Set<Point> points = new TreeSet<Point>();
		points.addAll(myPoints);
		points.addAll(oppPoints);
		if (!oppPoints.contains(center) && !myPoints.contains(center)) {
		//if( Main.current_turn == 0)
			return center;
		} else {
			
			getPointsOccupiedNum(points, arenaWidth, arenaHeight);
			 
			Point maxOppPoint = getMaxOppNumPoint(oppPoints);
			Point polyCenter = VoronoiStrategy.pointCenter.get(maxOppPoint);

			Set<Point> aroundPoints = new TreeSet<Point>();
			for (int x = maxOppPoint.x - 1; x < maxOppPoint.x + 2; x++) {
				for (int y = maxOppPoint.y - 1; y < maxOppPoint.y + 2; y++) {
					if (!(maxOppPoint.x == x && maxOppPoint.y == y)
							&& isPointInArena(x, y, arenaWidth, arenaHeight)) {
						aroundPoints.add(new Point(x, y));
					}
				}
			}

			Point maxCandidatePoint = null;
			int maxNum = 0;
			for (Point aroundPoint : aroundPoints) {
				if (isPointInMainPointSet(aroundPoint, maxOppPoint, points)) {
					Set<Point> tempPoints = new TreeSet<Point>(points);
					tempPoints.add(aroundPoint);
					Map<Point, Integer> tempMap =
							CalculateArea(tempPoints,
								arenaWidth,
								arenaHeight);
					int size = tempMap.get(aroundPoint);
					if (size > maxNum) {
						maxCandidatePoint = aroundPoint;
						maxNum = size;
					}
				}
			}
			
			if(Main.myID == 1)
				return maxCandidatePoint;
			
			int x = (int) ((-maxCandidatePoint.x + polyCenter.x)*0.38+maxCandidatePoint.x);
			int y = (int) ((-maxCandidatePoint.y + polyCenter.y)*0.38+maxCandidatePoint.y);
			Point middlePoint = new Point(x,y);
			
			while(points.contains(middlePoint))
			{
				middlePoint.x++;
				middlePoint.y++;
			}
			
			return middlePoint;
			//return maxCandidatePoint;
		}
	}

	/**
	 * 
	 * @param point  an arbitrary point in the arena
	 * @param mainPoint a point chosen by a player
	 * @param mainPoints all the points chosen by the all players
	 * @return  a boolean value which represents if the point is belong to
	 *          the mainPoint.
	 */
	private boolean isPointInMainPointSet(Point point,
			Point mainPoint, Set<Point> mainPoints) {
		double minDistance = point.distanceTo(mainPoint);
		for (Point p : mainPoints) {
			double d = point.distanceTo(p);
			if (d < minDistance) {
				return false;
			} else if (d == minDistance && !p.equals(mainPoint)) {
				return false;
			}
		}
		return true;
	}

	private Point getMaxOppNumPoint(Set<Point> oppPoints) {
		
		Map<Point,Integer> map = VoronoiStrategy.pointArea;
		Point maxPoint = null;
		int maxPolygon = 0;
		for (Point p : oppPoints) {
			if (map.get(p) > maxPolygon) {
				maxPoint = p;
				maxPolygon = map.get(p);
			}
		}
		return maxPoint;
	}

}
