package edu.nyu.ambplan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.nyu.ant.AntColony;
import edu.nyu.ant.Hospital;
import edu.nyu.ant.Patient;
import edu.nyu.ant.City.cityType;
import edu.nyu.parse.Extractor;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AntColony antColony = new AntColony(10000, new Extractor("input"));
		antColony.start();
		
//		Hospital h1 = new Hospital(1, 1, 6, 4);
//		Hospital h2 = new Hospital(2, 2, 4, 4);
		
		//System.out.println(cityType.PATIENT.equals(cityType.PATIENT));
//		Patient p1 = new Patient(1, 0, 0, 0);
//		List<Patient> list = new ArrayList<Patient>();
//		list.add(p1);
//		List<Patient> list2 = new ArrayList<Patient>();
//		list2.add(p1);
//		List<Patient> list3 = (ArrayList<Patient>)((ArrayList<Patient>)list2).clone();
//		list.get(0).id = 2;
//		System.out.println(list2.get(0).id);
//		System.out.println(list3.get(0).id);
	}

}
