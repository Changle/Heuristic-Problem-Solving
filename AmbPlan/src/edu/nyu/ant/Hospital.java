package edu.nyu.ant;

public class Hospital extends City {

	int capacity;

	public Hospital(int id, int X, int Y, int capacity) {
		this.id = id;
		this.X = X;
		this.Y = Y;
		this.capacity = capacity;
		type = cityType.HOSPITAL;
	}

	@Override
	public String toString() {
		return "Hospital [capacity=" + capacity + ", X=" + X + ", Y=" + Y
				+ ", id=" + id + ", type=" + type + "]";
	}

	public String output() {
		return (id + 1 - AntColony.numPatients) + ":(" + X + "," + Y + ")\n"; 
	}

}
