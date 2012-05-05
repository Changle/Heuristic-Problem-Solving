package edu.nyu.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * <code>Arena</code> class represents the arena in the game.
 * All points occupied by each player is stored in the class.
 */
public class Arena {

	public int width;

	public int height;

	/**
	 * The key is player id, the Set is the point occupied by the player
	 */
	private Map<Integer, Set<Point>> map = new HashMap<Integer, Set<Point>>();

	public Arena(int width, int height, Integer...playerIds) {
		this.width = width;
		this.height = height;
		for (Integer id : playerIds) {
			Set<Point> playerSteps = new TreeSet<Point>();
			map.put(id, playerSteps);
		}
	}

	/**
	 * To decouple and hide the data structure used in the class from 
	 * the outside, return the iterator
	 * @param who  the player id
	 * @return the unmodifiable iterator of the set of the player's points.
	 */
	public Iterator<Point> getIterator(Integer who) {
		return Collections.unmodifiableSet(map.get(who)).iterator();
	}

	/**
	 * Add point by the point and player id
	 * @param point  the point
	 * @param who  player id
	 * @throws Exception  if the point is not in the arena or the point has been
	 *                    occupied, a exception is thrown out.
	 */
	public void addPoint(Point point, Integer who) throws Exception {
		Set<Point> whoseSet = map.get(who);
		if (isPointInArena(point)) {
			if (!whoseSet.contains(point)) {
				whoseSet.add(point);
			} else {
				throw new Exception("The point has been occupied, " +
	            "you can't occupy it!");
			}
		} else {
			throw new Exception("Please choose a point in arena, " +
					"arena width is " + width + ", height is " + height + ".");
		}
	}

	private boolean isPointInArena(Point point) throws Exception {
		if (point.x >= 0
				&& point.x < width
				&& point.y >= 0
				&& point.y < height) {
			return true;
		}
		return false;
	}

}
