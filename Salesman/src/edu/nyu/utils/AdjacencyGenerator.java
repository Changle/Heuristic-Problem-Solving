package edu.nyu.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import edu.nyu.salesman.Point;

public class AdjacencyGenerator {

	public static double[][] generate(String path) {
		FileReader fr = null;
		List<Point> cities = new LinkedList<Point>();
		try {
			fr = new FileReader(path);
			String line;
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				String[] elems = line.trim().split(" ");
				Point point = new Point(Double.parseDouble(elems[0]) - 1,
						                Double.parseDouble(elems[1]),
						                Double.parseDouble(elems[2]),
						                Double.parseDouble(elems[3]));
				cities.add(point);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fillAdjacency(cities);
	}

	public static double getDistance(Point p1, Point p2) {
		return Math.sqrt(Math.pow(p1.x - p2.x, 2)
				         + Math.pow(p1.y - p2.y, 2)
				         + Math.pow(p1.z - p2.z, 2));
		
	}

	public static double[][] fillAdjacency(List<Point> cities) {
		int dimension = cities.size();
		double[][] adjacency = new double[dimension][dimension];
		for (int i = 0; i < adjacency.length; i++) {
			for (int j = 0; j < adjacency[i].length; j++) {
				adjacency[i][j] = getDistance(cities.get(i), cities.get(j));
			}
		}
		return adjacency;
	}
}
