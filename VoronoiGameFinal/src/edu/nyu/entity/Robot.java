package edu.nyu.entity;

import java.util.Set;

import edu.nyu.voronoi.BiggestPoly;
import edu.nyu.voronoi.Random;

/**
 * The robot adopts a strategy to play with the player.
 */
public class Robot {

	public int id;

	private VoronoiStrategy strategy;

	public Robot(int id) {
        this.id = id;
	}

	/**
	 * According to the positions of all points, the robot use a strategy to
	 * choose a new point.
	 * @param myPoints  the points chosen by the robot
	 * @param oppPoints  the points chosen by the opponent
	 * @param arenaWidth
	 * @param arenaHeight
	 * @return
	 */
	public Point putToken(Set<Point> myPoints,
			              Set<Point> oppPoints,
			              int arenaWidth,
			              int arenaHeight) {
		if (strategy == null) {
			if ((oppPoints.size() == 0 && myPoints.size() == 0)
					|| !oppPoints.contains(new Point(arenaWidth / 2,
							                         arenaHeight / 2))) {
				strategy = new BiggestPoly();
			} else {
				strategy = new Random();
			}
		}
		return strategy.choosePoint(myPoints,
				                    oppPoints,
				                    arenaWidth,
				                    arenaHeight);
	}

}
