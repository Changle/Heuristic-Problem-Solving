package edu.nyu.ant;

import java.util.LinkedList;
import java.util.List;

import edu.nyu.ant.City.cityType;

public class Ant {

	int id;

	int currentCity;

	int nextCity;

	int tourIndex;

	int tour[];

	int nearestHospital;

	double tourLen;

	double timePast;

	boolean isStop;

	List<Trip> trips = new LinkedList<Trip>();

	public Ant(int id, int ambCapacity, int currentCity) {
		this.id = id;
		tour= new int[ambCapacity + 2];
		this.currentCity = currentCity;
		tourIndex = 0;
		tour[tourIndex] = currentCity;
		timePast = 0;
		nearestHospital = currentCity;
		isStop = false;
	}

	public void restart() {
		tourIndex = 0;
		tour[tourIndex] = currentCity;
		nearestHospital = currentCity;
		isStop = false;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(id + ": ");
		int countTrips = 0;
		for (Trip trip : trips) {
			countTrips++;
			for (int i = 0; i <= trip.lastValidIndex; i++) {
				if (countTrips > 1 && i == 0) {
					continue;
				}
				City city = AntColony.cities.get(trip.tour[i]);
				if (city.type.equals(cityType.HOSPITAL)) {
					sb.append("H" + (trip.tour[i] + 1 - AntColony.numPatients) + "(" + city.X + "," + city.Y + ") ");
				} else if (city.type.equals(cityType.PATIENT)) {
					sb.append("P" + (trip.tour[i] + 1) + "(" + city.X + "," + city.Y + "," + ((Patient)city).rescureTime + ") ");
				}
			}
		}
		sb.append("\n");
		return sb.toString();
	}

}
