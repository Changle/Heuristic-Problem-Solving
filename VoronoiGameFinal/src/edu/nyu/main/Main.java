package edu.nyu.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import edu.nyu.entity.Const;
import edu.nyu.entity.Point;
import edu.nyu.entity.VoronoiStrategy;
import edu.nyu.voronoi.BiggestPoly;
import edu.nyu.voronoi.BiggestPolyCenter;

public class Main {

	public static StringBuffer state = new StringBuffer();
	public static String host = "";
	public static int port = 0;
	public static int globalTurn = 10;
	public static int current_turn = 0;
	public static int myID = 0;
	public static int playerCount = 2;
	public static float[] score = { 0, 0 };
	public static HashMap<Integer, Set<Point>> map = new HashMap<Integer, Set<Point>>();
	public static VoronoiStrategy strategy = new BiggestPoly();
	//public static VoronoiStrategy largestStrategy = new BiggestPoly();


	public static void readAndUpdateStates() {
		String states = state.toString();
		Scanner scanner = new Scanner(states);
		scanner.useDelimiter("\n");
		String[] tmps;
		while (scanner.hasNext()) {
			String oneLine = scanner.next();
			if (oneLine.equals("GLOBALS")) {
				String turnLine = scanner.next();
				String playerLine = scanner.next();
				String IDLine = scanner.next();
				tmps = turnLine.split(": ");
				globalTurn = Integer.valueOf(tmps[1]);
				tmps = playerLine.split(": ");
				playerCount = Integer.valueOf(tmps[1]);
				tmps = IDLine.split(": ");
				myID = Integer.valueOf(tmps[1]);
			} else if (oneLine.equals("PLAYER SCORES")) {
				String scoreLine = scanner.next();
				tmps = scoreLine.split(": ");
				score[0] = Float.valueOf(tmps[1]);
				scoreLine = scanner.next();
				tmps = scoreLine.split(": ");
				score[1] = Float.valueOf(tmps[1]);

			} else if (oneLine.equals("BOARD STATE")) {
				String pointLine;
				while ((pointLine = scanner.next()) != null) {
					if (pointLine.length() == 0)
						break;
					tmps = pointLine.split(": ");
					int playerId = Integer.parseInt(tmps[0]);
					tmps = tmps[1].split(" ");
					Point newPoint = new Point(Integer.parseInt(tmps[0]),
							Integer.parseInt(tmps[1]));
					if (!map.containsKey(playerId)) {
						Set<Point> pointSet = new TreeSet<Point>();
						pointSet.add(newPoint);
						map.put(playerId, pointSet);
					} else {
						Set<Point> pointSet = map.get(playerId);
						if (!pointSet.contains(newPoint)) {
							pointSet.add(newPoint);
							map.put(playerId, pointSet);
						}

					}
				}
			}
		}

	}

	
	public static void reportResult() {
		
		Set<Point> myPoint;
		Set<Point> oppPoint;
		
		int oppenentID = (myID == 0? 1: 0);
		
		if (map.containsKey(myID)) {
			myPoint = map.get(myID);
		}else
		{
			myPoint = new TreeSet<Point>();
		}
		if (map.containsKey(oppenentID)) {
			oppPoint = map.get(oppenentID);
		}else
		{
			oppPoint = new TreeSet<Point>();
		}
		
		Set<Point> set = new TreeSet<Point>();
		set.addAll(myPoint);
		set.addAll(oppPoint);
		
		Map<Point, Integer> scoreMap = VoronoiStrategy.CalculateArea(set,
				1000, 1000);
		
		int robotCount = 0;
		int oppCount = 0;
		
		for (Point point : scoreMap.keySet()) {
			if (myPoint.contains(point)) {
				robotCount += scoreMap.get(point);
			} else if (oppPoint.contains(point)) {
				oppCount += scoreMap.get(point);
			}
		}
		
		System.out.println("Your Score: "+ robotCount);
		System.out.println("Opp Score: "+ oppCount);
		/*
        if (robotCount > oppCount) {
    		System.out.println("The winner is Robot");
		} else if (robotCount < oppCount) {
			System.out.println("The winner is your opponent");
		} else {
			System.out.println("Tie");
		}*/
	}
	
	public static String process() {
		/*
		 * int x = (int) (Math.random() * 999); int y = (int) (Math.random() *
		 * 999);
		 */

		Set<Point> myPoint;
		Set<Point> oppPoint;
		
		int oppenentID = (myID == 0? 1: 0);
		
		if (map.containsKey(myID)) {
			myPoint = map.get(myID);
		}else
		{
			myPoint = new TreeSet<Point>();
		}
		if (map.containsKey(oppenentID)) {
			oppPoint = map.get(oppenentID);
		}else
		{
			oppPoint = new TreeSet<Point>();
		}
		
		Point decdesion = strategy.choosePoint(myPoint, oppPoint, 1000, 1000);
		myPoint.add(decdesion);
		map.put(myID, myPoint);
		
		return decdesion.x + " " + decdesion.y;
		
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException,
			IOException {
		
		if (args.length > 0) {
		    try {
		    	host = args[0];
		        port = Integer.parseInt(args[1]);
		    } catch (NumberFormatException e) {
		        System.err.println("Argument must be an integer");
		        System.exit(1);
		    }
		}

		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

		} catch (IOException ev) {
			System.err.println(ev.getMessage());
		}

		int ret;

		while ((ret = in.read()) != -1) {
			char inChar = (char) ret;
			state.append(inChar);

			if (inChar == ':' && state.charAt(state.length() - 2) == '\"') {
				System.out.println(state.toString());
				readAndUpdateStates();
				reportResult();
				out.println(process());
				reportResult();
				state.delete(0, state.length());
				current_turn++;
				if (current_turn == globalTurn)
					break;
			}
		}
		out.close();
		try {
			in.close();
			socket.close();
		} catch (IOException io) {
			System.err.println(io.getMessage());
		}

	}

}
