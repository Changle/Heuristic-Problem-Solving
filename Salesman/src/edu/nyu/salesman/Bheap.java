package edu.nyu.salesman;

import java.util.Arrays;

public class Bheap {
	  HeapElement Heap[];
	  int heapSize;
	  int city_num;
	  int watch[];
	  /*
	     initialize the watch array wich keeps truck of the place
	     of every point inside the heep tree.
	     Every point is represented by it's index in the xarray.
	  */

	  void watch_init() {
		  watch  = new int[city_num];
	    for (int i=0; i<city_num; i++) {
	      watch[i]=i+1;
	    }
	  }

	  /* computing the indices of parent and childs of node i */
	  int Parent(int i) {
	    return ((int)i/2);       // [i/2]
	  }

	  int Left(int i) {
	    return (i*2);     
	  }

	  int Right(int i) {
	    return (i*2 + 1);
	  }

	  /*
	     the constuctor of the Heap -
	     creates a new structure of binary Heap (an array) and 
	     intializes it.
	  */
	  public Bheap(int num_of_elem, double key0, double keyall, int city_num) {
		this.city_num = city_num;
	    Heap = new HeapElement[num_of_elem+1];
	    Heap[1]=new HeapElement(0,key0);
	    for (int i=2;i<=num_of_elem;i++)
	       Heap[i]=new HeapElement(i-1,keyall);

	    heapSize=num_of_elem;
	    watch_init();    // initialize the watch array
	  }

	  void Heapify(int i) {
	   int smallest = i;
	   int l = Left(i);
	   int r = Right(i);
	   if ( (l <= heapSize) && (Heap[l].key < Heap[i].key) )
	      smallest=l;
	   if ( (r <= heapSize) && (Heap[r].key < Heap[smallest].key) )
	      smallest=r;
	   if (smallest!=i)
	     {
	     Exchange(i,smallest);
	     Heapify(smallest);
	     }
	  }

	  // performs Heap[ind1] <-> Heap[ind2]
	  void Exchange(int ind1,int ind2) {
	    watch[Heap[ind1].index]=ind2;
	    watch[Heap[ind2].index]=ind1;
	    HeapElement temp = new HeapElement(Heap[ind1].index,Heap[ind1].key);
	    Heap[ind1].index=Heap[ind2].index;
	    Heap[ind1].key=Heap[ind2].key;
	    Heap[ind2].index=temp.index;
	    Heap[ind2].key=temp.key;
	  }

	  void DoMove(int dest, int source) {
	   Heap[dest].index= Heap[source].index;
	   Heap[dest].key=Heap[source].key;
	   watch[Heap[source].index]=dest;
	  }


	  // returns the index of point with smallest value of key
	  int ExtractMin() {
	   int ind = Heap[1].index;
	   DoMove(1,heapSize);
	   heapSize--;
	   Heapify(1);
	   return ind;
	  }

	  // changes key value of specified node to smaller one
	  void DecreaseKey(int ind, double key) {
	    int i=watch[ind];  // get position of node in the heap
	    int p=Parent(i);
	    while ( (i > 1) && (Heap[p].key > key) )
	      {
	       DoMove(i,p);
	       i=p;
	       p=Parent(i);
	      }
	    Heap[i].key=key;
	    Heap[i].index=ind;
	    watch[ind]=i;
	  }

	  boolean NotEmpty()
	   {
	   if (heapSize==0)
	     return false;
	   else
	     return true;
	   }

	}
