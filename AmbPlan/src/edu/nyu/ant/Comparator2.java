package edu.nyu.ant;

import java.util.Comparator;

public class Comparator2 implements Comparator<City> {

	@Override
	public int compare(City c1, City c2) {
		if (c1.Y < c2.Y) {
			return -1;
		} else if (c1.Y == c2.Y) {
			if (c1.X < c2.X) {
				return -1;
			} else if (c1.X == c2.X) {
				return 0;
			}
		}
		return 1;
	}

}
