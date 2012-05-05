package edu.nyu.voronoi;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.nyu.entity.Point;
import edu.nyu.entity.VoronoiStrategy;

public class BiggestPolyCenter extends VoronoiStrategy {
	
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
			return center;
		} else {
			
			getPointsOccupiedNum(points, arenaWidth, arenaHeight);
			Point maxOppPoint = getMaxOppNumPoint(oppPoints);
			Point polyCenter = VoronoiStrategy.pointCenter.get(maxOppPoint);
			if(!points.contains(polyCenter))
				return polyCenter;
			else
			{
				int step = 1;
				Point newCenter=polyCenter;
				while(true)
				{
					newCenter = new Point(polyCenter.x+step,polyCenter.y+step);
					if(!points.contains(newCenter))
					{
						break;
					}
					step ++;
				}
				return newCenter;
			}
		}
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
