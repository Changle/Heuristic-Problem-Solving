package edu.nyu.ant;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import edu.nyu.ant.City.cityType;
import edu.nyu.parse.Extractor;

public class AntColony {

	public static final float alpha = 1.0f;

	public static final float beta = 1.0f;

	public static final float gama = 0.7f;

	public static final float rho = .8f; // decay rate

	public static final float qval = 100; // amount of pherom to spread on tour

	public static final int ambCapacity = 4;

	public static final int loadTimePerPatient = 1;

	public static final int driveSpeed = 1;

	public static final int maxSearchRadius = 300;

	public static int numPatients;

	public static int numHospital;

	boolean stillAlive = true;

	int timeCount = 0;

	int[][] precompDistance;

	double[][] pherom;

	double basePherom;

	List<Hospital> hospitals;

	public static List<City> cities; // patients + hospitals

	List<City> cities1; // sorted list, by X

	AntGroup[] antGroups;

	public AntColony(int numAntGroups, Extractor extractor) {
		precompDistance = extractor.precompDistance;
		hospitals = extractor.hospitals;
		cities = extractor.cities;
		cities1 = extractor.cities1;
		numHospital = hospitals.size();
		numPatients = cities.size() - numHospital;
		antGroups = new AntGroup[numAntGroups];
		for (int i = 0; i < numAntGroups; i++) {
			antGroups[i] = new AntGroup(hospitals, ambCapacity, cities.size());
		}
		basePherom = 1.0 / cities.size();
		pherom = new double[cities.size()][cities.size()];
		resetPherom();
	}

	public void start() {
		int saved = 0;
		int groupId = 0;
		
		for (int i = 0; i < 200; i++) {
			
		moveAnts(antGroups[i]);
		evaporatePheromoneTrails();
		intensifyPheromoneTrails();
		findBestTour();
		if (antGroups[i].numSaved > saved) {
				saved = antGroups[i].numSaved;
				groupId = i;
				
			}
		
		//System.out.println(i+" : "+antGroups[i].numSaved);
		}
		System.out.println(toString());
		System.out.println(antGroups[groupId]);
		System.out.println("save num: " + antGroups[groupId].numSaved);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Hospital\n");
		for (Hospital hospital : hospitals) {
			sb.append(hospital.output());
		}
		sb.append("\nAmbulance");
		return sb.toString();
	}

	private void findBestTour() {
		// TODO Auto-generated method stub
		
	}

	private void intensifyPheromoneTrails() {
		for (AntGroup ag : antGroups) {
			for (int i = 0; i < ag.trips.size(); i++) {
				Trip trip = ag.trips.get(i);
				for (int j = 0; j < trip.tour.length - 2; j++) {
					int from = trip.tour[j];
					int to = trip.tour[j + 1];
					pherom[from][to] += (qval/ag.totalLen) * rho;
				}
			}
		}
	}

	private void evaporatePheromoneTrails() {
		for (int from = 0; from < cities.size(); from++) {
			for (int to = 0; to < cities.size(); to++) {
				pherom[from][to] *= (1.0 - rho);
				if (pherom[from][to] < 0.0) {
					pherom[from][to] = basePherom;
				}
			}
		}
	}

	private void resetPherom() {
		for (int i = 0; i < cities.size(); i++) {
			for (int j = 0; j < cities.size(); j++) {
				pherom[i][j] = basePherom;
			}
		}
	}

	public void moveAnts(AntGroup antGroups) {

			while (stillAlive) {
				chooseNextCities(antGroups);
			}
			for (Trip trip : antGroups.trips) {
				antGroups.totalLen += trip.tourLen;
			}
			stillAlive = true;
		
	}

	private Set<Patient> getCandidatePatients(AntGroup antGroup, Ant ant) {
		//System.out.println("  [INFO]: getCandidatePatients");
		int from = ant.currentCity;
		// find out the patients which in the circle with its center is from
		// and radius is maxSearchRadius, and the patients is not visited
		// if there is no patients in that circle, enlarge the radius.
		Set<Patient> set = new TreeSet<Patient>(new Comparator2());

		int lowerSearchBound = cities1.indexOf(cities.get(from)) - 1;
		int upperSearchBound = lowerSearchBound + 2;

		int times = 1;
		while (set.size() == 0 && (lowerSearchBound >= 0 || upperSearchBound < cities1.size())) {
			double lowerDisX = cities.get(from).X - times * maxSearchRadius;
			double upperDisX = cities.get(from).X + times * maxSearchRadius;
			double lowerDisY = cities.get(from).Y - times * maxSearchRadius;
			double upperDisY = cities.get(from).Y + times * maxSearchRadius;
			while (lowerSearchBound >= 0 && cities1.get(lowerSearchBound).X > lowerDisX) {
				City city = cities1.get(lowerSearchBound);
				if (city.type.equals(cityType.PATIENT)
						&& antGroup.tabu[city.id] == 0
						&& city.Y < upperDisY
						&& city.Y > lowerDisY
						&& isSaveToPick(ant.currentCity, city.id, ant)) {
					set.add((Patient)city);
				}
				lowerSearchBound--;
			}
			while (upperSearchBound < cities1.size() && cities1.get(upperSearchBound).X < upperDisX) {
				City city = cities1.get(upperSearchBound);
				if (city.type.equals(cityType.PATIENT)
						&& antGroup.tabu[city.id] == 0
						&& city.Y < upperDisY
						&& city.Y > lowerDisY
						&& isSaveToPick(ant.currentCity, city.id, ant)) {
					set.add((Patient)city);
				}
				upperSearchBound++;
			}
			times++;
		}
		return set;
	}

	private boolean isSaveToPick(int from, int to, Ant ant) {
		// calculate the distance to the "to"
		double time = precompDistance[from][to] * driveSpeed;
		// calculate distance from "to" to the nearest hospital
		double nearestHospitalDis = Double.MAX_VALUE;
		int nearestHospitalId = 0;
		for (Hospital hospital : hospitals) {
			if (precompDistance[to][hospital.id] < nearestHospitalDis) {
				nearestHospitalDis = precompDistance[to][hospital.id];
				nearestHospitalId = hospital.id;
			}
		}
		ant.nearestHospital = nearestHospitalId;
		time += nearestHospitalDis * driveSpeed;
		time += ant.timePast;
		time += 2;
		// check if all the patients on the amb survive
		boolean isSafe = true;
		if (((Patient)(cities.get(to))).rescureTime > time) {
			for (int i = 1; i <= ant.tourIndex; i++) {
				if (((Patient)(cities.get(ant.tour[i]))).rescureTime < time) {
					isSafe = false;
					break;
				}
			}
		} else {
			isSafe = false;
		}
		return isSafe;
	}

	private int getNearestHospital(int from) {
		double nearestHospitalDis = Double.MAX_VALUE;
		int nearestHospitalId = 0;
		for (Hospital hospital : hospitals) {
			if (precompDistance[from][hospital.id] < nearestHospitalDis) {
				nearestHospitalDis = precompDistance[from][hospital.id];
				nearestHospitalId = hospital.id;
			}
		}
		return nearestHospitalId;
	}
	private boolean chooseNextCityPerAnt(AntGroup antGroup, int antIdx) {
		//System.out.println("  [INFO]: chooseNextCityPerAnt");
		Ant ant = antGroup.ants[antIdx];
		// every ant has its own search range
		Set<Patient> set = getCandidatePatients(antGroup, ant);
		//System.out.println("  [INFO]: got candidates");
		int from = ant.currentCity;
		boolean shouldSentToHospital = false;
		int to = 0;
		if (ant.tourIndex < ambCapacity) {
			if (set.size() > 0) {
				double d = 0;
				int dis;
				for (Patient patient : set) {
					dis = precompDistance[from][patient.id] == 0 ? 1 : precompDistance[from][patient.id];
					d += Math.pow(pherom[from][patient.id], alpha) *
					Math.pow(1.0 / dis, beta) * Math.pow(patient.rescureTime, gama);
				}

				Iterator<Patient> it = set.iterator();
				while (it.hasNext()) {
					//System.out.println("  [INFO]: in random while");
					Patient pIt = it.next();
					to = pIt.id;
					dis = precompDistance[from][to] == 0 ? 1 : precompDistance[from][to];
					double p = Math.pow(pherom[from][to], alpha) *
					Math.pow((1.0 / dis), beta) * Math.pow(pIt.rescureTime, gama) / d;
					if (Math.random() <= p) {
						System.out.println("  [INFO]: chooseNextCityPerAnt: random choose a city");
						break;
					}
					if (!it.hasNext()) {
						it = set.iterator();
					}
				}
			} else {
				if (ant.tourIndex > 0) {
					//System.out.println("  [INFO]: chooseNextCityPerAnt: return to a hospital");
					to = getNearestHospital(from);
					shouldSentToHospital = true;
				} else {
					//System.out.println("  [INFO]: chooseNextCityPerAnt: no way to go ant stop");
					ant.isStop = true;
				}
			}
		} else {
			to = getNearestHospital(from);
			shouldSentToHospital = true;
		}
		
        if (!stillAlive)
        	return false;
        
		ant.nextCity = to;
		antGroup.tabu[ant.nextCity] = 1;
		ant.timePast += driveSpeed * precompDistance[ant.currentCity][ant.nextCity] + 1;
		ant.tourIndex++;
		ant.tour[ant.tourIndex] = ant.nextCity;
		ant.tourLen += precompDistance[ant.currentCity][ant.nextCity];
		ant.currentCity = ant.nextCity;
		System.out.println("  ant " + antIdx + " chose id " + ant.currentCity);
		if (shouldSentToHospital && ant.tourIndex > 1) {
			antGroup.numSaved += (ant.tourIndex - 1);
			int[] array = new int[ant.tour.length];
			for (int i = 0; i <= ant.tourIndex; i++) {
				array[i] = ant.tour[i];
			}
			Trip trip = new Trip(antIdx, array, ant.tourLen, ant.tourIndex);
			antGroup.trips.add(trip);
			ant.trips.add(trip);
			ant.restart();
		}
		return shouldSentToHospital;
	}

	private void chooseNextCities(AntGroup antGroup) {
		int count = 0;
		for (int i = 0; i < antGroup.ants.length; i++) {
			if (!antGroup.ants[i].isStop) {
				//System.out.println("  ant" + " " + i + " begin to move");
				chooseNextCityPerAnt(antGroup, i);
				//System.out.println("  ant" + " " + i + " moved");
			} else {
				count++;
			}
			if (count == antGroup.ants.length) {
				stillAlive = false;
			}
		}
	}

}
