package edu.nyu.ant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AntGroup {

	public Ant[] ants;

	List<Trip> trips = new ArrayList<Trip>();

	public double totalLen = 0;

	public int numSaved = 0;

	public int[] tabu;

	public AntGroup(List<Hospital> hospitals, int ambCapacity, int numCities) {
		LinkedList<Ant> list = new LinkedList<Ant>();
		int count = 0;
		for (Hospital hospital : hospitals) {
			for (int i = 0; i < hospital.capacity; i++) {
				count++;
				Ant ant = new Ant(count, ambCapacity, hospital.id);
				list.add(ant);
			}
		}
		tabu = new int[numCities];
		ants = (Ant[])list.toArray(new Ant[list.size()]);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Ant ant : ants) {
			sb.append(ant);
		}
		return sb.toString();
	}


}
