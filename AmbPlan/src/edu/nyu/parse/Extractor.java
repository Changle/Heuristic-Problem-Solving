package edu.nyu.parse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.nyu.ant.City;
import edu.nyu.ant.Comparator1;
import edu.nyu.ant.Hospital;
import edu.nyu.ant.Patient;


public class Extractor {

	public List<Hospital> hospitals = new LinkedList<Hospital>();

	public List<City> cities;

	public List<City> cities1;

	public int[][] precompDistance;

	public Extractor(String path) {
		extract(path);
	}

	@SuppressWarnings("unchecked")
	private void extract(String path) {
		FileReader fr = null;
		boolean isPerson = false;
		boolean isHospital = false;
		int count = 0;
		cities = new LinkedList<City>();
		try {
			fr = new FileReader(path);
			String line;
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					if (line.contains("person")) {
						isPerson = true;
	                    isHospital = false;
						continue;
					}
					if (line.contains("hospital")) {
						isPerson = false;
						isHospital = true;
						continue;
					}
					String[] parts = line.split(",");
					if (isPerson) {
						Patient p =
							new Patient(count,
									    Integer.parseInt(parts[0]),
									    Integer.parseInt(parts[1]),
									    Integer.parseInt(parts[2]));
						cities.add(p);
						count++;
					}
					if (isHospital) {
						Hospital h = new Hospital(count, 
							    				  Integer.parseInt(parts[0]),
							    				  Integer.parseInt(parts[1]),
							                      Integer.parseInt(parts[2]));
						hospitals.add(h);
					    cities.add(h);
						count++;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fillAdjacency(cities);
		cities1 = (LinkedList<City>)((LinkedList<City>)cities).clone();

		Collections.sort(cities1, new Comparator1());
		
//		int idx = 1;
//		for (City c : cities) {
//			System.out.println("idx " + idx + ": "+ c);
//			idx++;
//		}
	}

	private void fillAdjacency(List<City> cities) {
		int dimension = cities.size();
		precompDistance = new int[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				precompDistance[i][j] = getDistance(cities.get(i), cities.get(j));
			}
		}
	}

	private int getDistance(City p1, City p2) {
		return Math.abs(p1.X - p2.X) + Math.abs(p1.Y - p2.Y); 		
	}
}
