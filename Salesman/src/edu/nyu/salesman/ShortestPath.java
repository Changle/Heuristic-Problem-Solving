package edu.nyu.salesman;

import java.util.List;

public class ShortestPath {

	int pointId;

	List<Point> points;
	
	double cost;
	
	ShortestPath() {}
	
	ShortestPath(int pointId, List<Point> points) {
		this.pointId = pointId;
		this.points = points;
	}
	
	ShortestPath(int pointId, List<Point> points, double cost) {
		this.pointId = pointId;
		this.points = points;
		this.cost = cost;
	}


}
