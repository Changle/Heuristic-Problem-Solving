package edu.nyu.ant;

import java.util.Arrays;

public class Trip {

	int antIndex;

	int[] tour;

	double tourLen;

	int lastValidIndex;

	public Trip(int antIndex, int[] tour, double tourLen, int lastValidIndex) {
		this.antIndex = antIndex;
		this.tour = tour;
		this.tourLen = tourLen;
		this.lastValidIndex = lastValidIndex;
	}

	@Override
	public String toString() {
		return "Trip [antIndex=" + antIndex + ", tour=" + Arrays.toString(tour)
				+ ", tourLen=" + tourLen + "]";
	}
}
