package edu.nyu.salesman;

public class Couple  {
	  int node1;
	  int node2;
	  double distance;

	  public Couple(int ind1,int ind2) {
	    node1=ind1;
	    node2=ind2;
	    distance=Main.distMatr[ind1][ind2];
	  }
	  @Override
	public String toString() {
		  System.out.println("node1: " + node1 + " node2: " + node2 + " distance: " + distance);
		  return super.toString();
	}
	}
