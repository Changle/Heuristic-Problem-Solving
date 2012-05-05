package edu.nyu.voronoi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.nyu.entity.Arena;
import edu.nyu.entity.Const;
import edu.nyu.entity.Point;
import edu.nyu.entity.Robot;
import edu.nyu.entity.VoronoiStrategy;

/**
 * The game controller to control the game flow.
 */
public class Controller {

	private Arena arena;

	private int maxNumOfRounds;

	private int currentRound;

	private Robot robot;

	/**
	 * The First player id.
	 */
	Integer whoFirst;

	public Controller(int arenaWidth,
			int arenaHeight,
			int maxNumOfRounds,
			Integer whoFirst) {
		arena = new Arena(arenaWidth, arenaHeight, Const.OPPOENT, Const.ROBOT);
		this.maxNumOfRounds = maxNumOfRounds;
		robot = new Robot(Const.ROBOT);
		currentRound = 0;
		this.whoFirst = whoFirst;
	}

	/**
	 * Two players chose new points in turn.
	 */
	public void run() {
		while (currentRound < maxNumOfRounds) {
			System.out.println("Round " + currentRound + ":");
			if (whoFirst.equals(Const.OPPOENT)) {
				opponentTurn();
				robotTurn();
			} else {
				robotTurn();
				opponentTurn();
			}
			currentRound++;
		}
		reportResult();
	}

	/**
	 * The opponent get a new point from the stdin.
	 */
	private void opponentTurn() {
		while (true) {
			try {
				Point oppPoint = getInput();
				System.out.println("Opponent put " + oppPoint);
				arena.addPoint(oppPoint, Const.OPPOENT);
				break;
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	/**
	 * Get all points chosen by the player.
	 * @param player id
	 * @return all points chosen by the player
	 */
	private Set<Point> makeSet(Integer who) {
		Iterator<Point> robotIt = arena.getIterator(who);
		Set<Point> set = new TreeSet<Point>();
		while (robotIt.hasNext()) {
			set.add(robotIt.next());
		}
		return set;
	}

	/**
	 * Robot chooses a new point according to the arena.
	 */
	private void robotTurn() {
		while (true) {
			Point robotPoint = robot.putToken(makeSet(Const.ROBOT),
					makeSet(Const.OPPOENT),
					arena.width,
					arena.height);
			System.out.println("Robot put " + robotPoint);
			try {
				arena.addPoint(robotPoint, Const.ROBOT);
				break;
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private void reportResult() {
		Set<Point> robotSet = makeSet(Const.ROBOT);
		Set<Point> oppSet = makeSet(Const.OPPOENT);
		Set<Point> set = new TreeSet<Point>();
		set.addAll(robotSet);
		set.addAll(oppSet);
		VoronoiStrategy.getPointsOccupiedNum(set,
					arena.width, arena.height);
		HashMap<Point,Integer>map = VoronoiStrategy.pointArea;
		int robotCount = 0;
		int oppCount = 0;
		for (Point point : map.keySet()) {
			if (robotSet.contains(point)) {
				robotCount += map.get(point);
			} else if (oppSet.contains(point)) {
				oppCount += map.get(point);
			}
		}
		
        if (robotCount > oppCount) {
    		System.out.println("The winner is Robot");
		} else if (robotCount < oppCount) {
			System.out.println("The winner is your opponent");
		} else {
			System.out.println("Tie");
		}
	}

	private Point getInput() {
		System.out.println("Opponent, please enter new point in the format " +
				"like 1,2,");
		InputStreamReader isr = new InputStreamReader(System.in);
		char[] a = new char[100];
		try {
			isr.read(a);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String pointStr = new String(a).replace("\n", "");
		String[] parts = pointStr.split(",");
		Point point = new Point(Integer.parseInt(parts[0]),
				Integer.parseInt(parts[1]));
		return point;
	}

}
