package edu.nyu.ant;

public abstract class City {

	public enum cityType {
		HOSPITAL,
		PATIENT
	};

	public int id;

	public int X;

	public int Y;

	public cityType type;
}
