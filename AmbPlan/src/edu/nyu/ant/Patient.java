package edu.nyu.ant;

public class Patient extends City {

	public int rescureTime;

	public Patient(int id, int X, int Y, int rescureTime) {
		this.id = id;
		this.X = X;
		this.Y = Y;
		this.rescureTime = rescureTime;
		type = cityType.PATIENT;
	}

	@Override
	public String toString() {
		return "Patient [rescureTime=" + rescureTime + ", X=" + X + ", Y=" + Y
				+ ", id=" + id + ", type=" + type + "]";
	}

}
