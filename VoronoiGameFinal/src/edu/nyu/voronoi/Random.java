package edu.nyu.voronoi;

import java.util.Set;

import edu.nyu.entity.Point;
import edu.nyu.entity.VoronoiStrategy;

/**
 * The random strategy.
 * Choose a point randomly in the arena.
 */
public class Random extends VoronoiStrategy {

	@Override
	public Point choosePoint(Set<Point> myPoints,
			                 Set<Point> oppPoints,
			                 int arenaWidth,
			                 int arenaHeight) {
		int x = (int)(Math.random() * arenaWidth);
		int y = (int)(Math.random() * arenaHeight);
		Point point = new Point(x, y);
		return point;
	}

}
