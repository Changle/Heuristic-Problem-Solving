package edu.nyu.salesman;

import java.util.Vector;

public class Node  {
	  int index;
	  int degree;
	  Vector edgeList;
	  int circleNum;

	  public Node(int ind)  {
	   index=ind;
	   degree=0;
	   edgeList = new Vector();
	   circleNum=-1;
	  }

	  void addEdge(int endpoint) {
	   edgeList.addElement(new Integer(endpoint));
	   degree++;
	  }

	  int getEdge()  {
	   if (degree!=0)
	    {
	     int size = edgeList.size();
	     Integer N = (Integer) (edgeList.firstElement());//get next neighbor
	     int n = N.intValue();

	     edgeList.removeElementAt(0); // remove the edge
	     degree--;
	     // remove same edge from the neighbor's list
	     Main.Graph[n].edgeList.removeElement(new Integer(index));
	     Main.Graph[n].degree--;

	     return n;
	    }
	   else return (-1);
	  }
	} // end Node class
