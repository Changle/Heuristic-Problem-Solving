package edu.nyu.salesman;


import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import edu.nyu.utils.AdjacencyGenerator;


public class Main {

	/**
	 * @param args
	 */
	
	public static final int CITY_NUM = 1000;
	
	static int  parent[];
	
	public static double distMatr[][];
	
	public static Couple Match[];

	static int odd[];
	
	static int oddNum=0;
	
	static int matchNum=0;
	
	static Node Graph[];
	
	static int edgesMatr[][];   // this matritz will indicate wether each edge is double or not
	
	static Vector Circles;  // holds arrays which represent circles in graph

    static int numOfEdges=0;  // initial number of edges in eulerian graph
    
    static int eulertour[];  //    TraverseCircle

    static int outerIndex;    //for
    
    static double best=Double.MAX_VALUE;
    static int tspPath[];
    static double MAX=100000;

    
    static int xfinal2[],yfinal2[]; //second Algorithm's output
//    static int xarr[]=new int[MAX];
//    static int yarr[]=new int[MAX];
    static double alg2Weight=MAX;
    static double Oldcost  =  0;
    static double Newcost = 0;
    static int Iterations 	= 1000;
    static   double alg2Wimproved=MAX;
    static int tsp2Improved[];





	public static void MSTcreate(String path) {
	    //creating binary heap with node 0 as a root
	    Bheap Q = new Bheap(CITY_NUM,0,CITY_NUM, 10 * CITY_NUM);
	    /*
	      Parent array holds definition of node's parent during construction of
	      the minimum spanning tree.
	    */
	    parent = new int[CITY_NUM];
	    parent[0]=-1;  // -1 for null as parent of root


	    /*
	      Initialize Key array.
	      The value of key is the distance of the node from the current
	      subtree of MST.
	      1000 equals to infinity in this case.
	    */
	    double Key[] = new double[CITY_NUM];
	    Key[0]=0;
	    
	    for (int i=1; i<CITY_NUM;Key[i++]=10 * CITY_NUM);


	    /*
	       bitvector array is used to indicate wich nodes are in Q
	       bitvector[i]=true <=> node i is in heap Q
	       all bitvector elements intialized to true
	    */
	    boolean bitvector[] = new boolean[CITY_NUM];
	    for (int i=0;i < CITY_NUM;bitvector[i++]=true);
	    if (distMatr==null) {
	    	distMatr = AdjacencyGenerator.generate(path);
	    	/*distMatr = new double[CITY_NUM][CITY_NUM];
	    	distMatr[0][0] = Integer.MAX_VALUE;
	    	distMatr[0][1] = 6;
	    	distMatr[0][2] = 1;
	    	distMatr[0][3] = 5;
	    	distMatr[0][4] = Integer.MAX_VALUE;
	    	distMatr[0][5] = Integer.MAX_VALUE;
	    	distMatr[1][0] = distMatr[0][1];
	    	distMatr[1][1] = Integer.MAX_VALUE;
	    	distMatr[1][2] = 5;
	    	distMatr[1][3] = Integer.MAX_VALUE;
	    	distMatr[1][4] = 3;
	    	distMatr[1][5] = Integer.MAX_VALUE;
	    	distMatr[2][0] = distMatr[0][2];
	    	distMatr[2][1] = distMatr[1][2];
	    	distMatr[2][2] = Integer.MAX_VALUE;
	    	distMatr[2][3] = 5;
	    	distMatr[2][4] = 6;
	    	distMatr[2][5] = 4;
	    	distMatr[3][0] = distMatr[0][3];
	    	distMatr[3][1] = distMatr[1][3];
	    	distMatr[3][2] = distMatr[2][3];
	    	distMatr[3][3] = Integer.MAX_VALUE;
	    	distMatr[3][4] = Integer.MAX_VALUE;
	    	distMatr[3][5] = 2;
	    	distMatr[4][0] = distMatr[0][4];
	    	distMatr[4][1] = distMatr[1][4];
	    	distMatr[4][2] = distMatr[2][4];
	    	distMatr[4][3] = distMatr[3][4];
	    	distMatr[4][4] = Integer.MAX_VALUE;
	    	distMatr[4][5] = 6;
	    	distMatr[5][0] = distMatr[0][5];
	    	distMatr[5][1] = distMatr[1][5];
	    	distMatr[5][2] = distMatr[2][5];
	    	distMatr[5][3] = distMatr[3][5];
	    	distMatr[5][4] = distMatr[4][5];
	    	distMatr[5][5] = Integer.MAX_VALUE;*/
	    }
	    // the body of Prim's algorithm for finding minimum spanning tree
	    int u;
	    while (Q.NotEmpty())
	     {
	      u=Q.ExtractMin();
	      bitvector[u]=false;  // u isn't in Q any more
	      for (int v=0;v < CITY_NUM;v++)
	       {
	        if (bitvector[v] && (distMatr[v][u] < Key[v]) )
	          {
	           parent[v]=u;
	           Key[v]=distMatr[v][u];
	           Q.DecreaseKey(v,Key[v]);
	          }
	       }
	     }
	  }
	
	static Couple[] randomMatch(int nodes[],int nodesNum ) {
	    boolean done[] = new boolean[nodesNum];
	    int num,num0,ind1,ind2;
	    int halfNum = (int) nodesNum/2;
	    Couple match[] = new Couple[halfNum];
	    for (int i=0;i<nodesNum;done[i++]=false);
	    for (int i=0;i<halfNum;i++)
	     {
	        num0=i;
	        while (done[num0]!=false)
	          num0++;
	        done[num0]=true;
	        num=(int) (Math.random()*(nodesNum));
	        while ((done[num]!=false) ||  (num==nodesNum))
	          num=(int) (Math.random()*(nodesNum));
	        done[num]=true;
	        ind1=nodes[num0];
	        ind2=nodes[num];
	        match[i] = new Couple(ind1,ind2);
	     }
	    return match; 
	  }
	
	static double improve(int numOfMatches,Couple match[]) {
	    double weight=0;
	    for (int i=0;i<numOfMatches;i++)
	      for(int j=i+1;j<numOfMatches;j++)
	        {
	         boolean chose1=false,chose2=false;
	         double sum = match[i].distance + match[j].distance;
	         int i1 = match[i].node1;
	         int i2 = match[i].node2;
	         int j1 = match[j].node1;
	         int j2 = match[j].node2;
	         double newsum1 = distMatr[i1][j1]+distMatr[i2][j2];
	         double newsum2 = distMatr[i1][j2]+distMatr[i2][j1];

	         if (newsum1<sum) {
	            if (newsum2 < newsum1)
	              chose2=true;
	            else
	              chose1=true; }
	         else if (newsum2<sum)
	              chose2=true;

	         if (chose1)
	           {
	            match[i].node2=j1;
	            match[i].distance=distMatr[i1][j1];
	            match[j].node1=i2;
	            match[j].distance=distMatr[i2][j2];
	           }
	         else if(chose2)
	            {
	            match[i].node2=j2;
	            match[i].distance=distMatr[i1][j2];
	            match[j].node2=i2;
	            match[j].distance=distMatr[i2][j1];
	            }
	        }
	    for (int i=0;i<numOfMatches;i++)
	       weight+=match[i].distance;
	    return weight;
	  }
	
	static void MatchHendler() {
	    double matchWeight,sum;
	    if (Match==null)
	       {
	        Match=randomMatch(odd,oddNum);
	        matchWeight=improve(matchNum,Match);
	        while (matchWeight!=(sum=improve(matchNum,Match)))
	           matchWeight=sum;
	       }
	  }
	
	static void createGraph() {
		   Graph = new Node[CITY_NUM];
		   edgesMatr = new int[CITY_NUM][CITY_NUM];
		   numOfEdges=0;
		   // initialize Graph structure
		   for (int i=0;i<CITY_NUM;i++)
		     Graph[i] = new Node(i);
		   // initialize Matriz representation
		   for (int i=0;i<CITY_NUM;i++)
		     for (int j=0;j<CITY_NUM;j++)
		        edgesMatr[i][j]=0;

		   for (int i=1;i<CITY_NUM;i++)
		      {
		       int j=parent[i];
		       Graph[i].addEdge(j);
		       Graph[j].addEdge(i);
		       edgesMatr[i][j]=1;
		       edgesMatr[j][i]=1;
		       numOfEdges++;
		      }
		  }

	static int findOdds()  {
	    int j=0;
	    odd = new int[CITY_NUM];
	    for (int i=0;i<CITY_NUM;i++)
	     if ( (Graph[i].degree % 2) != 0)  // check if node's degree is odd
	       {
	       odd[j]=i;
	       j++;
	       }
	    return j;
	  }

	
	static void oddsHendler() {
	     if ((Graph==null) || edgesMatr==null)
	        createGraph();  // => Graph!=null

	     if (odd==null)
	        {
	         oddNum=findOdds();
	         matchNum=(int) oddNum/2;
	        }
	  }

	static void EulerTour()  {
		 boolean stop = false;
		 int nextpoint,startpoint=0;
		 int circleIndex=0;
		 // number of edged in graph that are still not in circle
		 int restOfEdges=numOfEdges;
		 Circles = new Vector();

		 while (! stop)
		  {
			 //Point ps[] = new Point[restOfEdges+1];
		   circleElement circle[] = new circleElement[restOfEdges+1];
		   circle[0] = new circleElement(startpoint);
		   Graph[startpoint].circleNum=circleIndex; // mark node for some circle
		   nextpoint=startpoint;
		   int tempedges=restOfEdges;

		   // next loop creates a circle
		   for (int i=1; i<=restOfEdges; i++ )
		    {
		     int ind;
		     if ((ind=Graph[nextpoint].getEdge()) == -1)
		       {
		       if (nextpoint!=startpoint)     // just for check
		         //System.out.println("not a circle");
		       break;
		       }
		     circle[i] = new circleElement(ind);
		     tempedges--;
		     nextpoint=ind;
		     Graph[nextpoint].circleNum=circleIndex; // mark node for some circle
		    }
		   if (tempedges==0)  // no more circles to find.
		     {
		      Circles.addElement(circle);
		      stop=true;
		      continue;
		     }

		   int j,k;    // look for node in circle ,which can begin a new circle
		   for (j=0;
		        (j<restOfEdges) && (circle[j]!=null) && (Graph[circle[j].index].degree==0);
		        j++);
		   if (circle[j]!=null && j<restOfEdges)
		     {
		     startpoint=circle[j].index;
		     circle[j].another_circle=++circleIndex;
		     }
		   else
		     {
		     for (j=0;j<CITY_NUM &&
		         ((Graph[j].degree==0) || (Graph[j].circleNum==-1));j++);
		     int circlenum=Graph[j].circleNum;
		     circleElement oldcircle[]=(circleElement[]) Circles.elementAt(circlenum);
		     for (k=0;oldcircle[k]!=null && k<oldcircle.length && (oldcircle[k].index!=j);k++);
		     if (oldcircle[k].index==j && oldcircle[k].another_circle==-1)
		     {
		     oldcircle[k].another_circle=++circleIndex;
		     Circles.setElementAt(oldcircle,circlenum);
		     startpoint=j;
		     }
		    // else
		     // System.out.println("trouble in EulerTour");
		     }
		   Circles.addElement(circle);
		   restOfEdges=tempedges;

		  } //end ofwhile loop

		} //
	static void EulerHendler() {
	    if (Circles==null)
	       EulerTour();
	    if (eulertour==null)
	       {
	        eulertour = new int[numOfEdges+1];
	        outerIndex=0;
	        TraverseCircle(0);
	       }
	  }

   static void TraverseCircle(int circle_index)
   {
	   circleElement tempcircle[]=(circleElement[]) (Circles.elementAt(circle_index));
	   for (int i=0;i<tempcircle.length && tempcircle[i]!=null;i++)
	    {
	     if (tempcircle[i].another_circle!=-1 && tempcircle[i].another_circle!=circle_index)
	        TraverseCircle(tempcircle[i].another_circle);
	     else
	       {
	       eulertour[outerIndex]=tempcircle[i].index;
	       outerIndex++;
	       }
	    }
	   }

   static void tsp2Hendler() {
	    if (tspPath==null)
	       {
	        TSPbuild();
	        alg2Weight=calculateTotal(tspPath);
	       }

	  }
   static double calculateTotal(int arr[]) {
	    int ind1,ind2;
	    double weight=0;
	    ind1=arr[0];
	    for(int i=1;i<=CITY_NUM && i<arr.length;i++)
	     {
	       ind2=arr[i];
	       weight+=distMatr[ind1][ind2];
	       ind1=ind2;
	     }
	    return weight;
	}
   
//   static void TSPpath(int array[])  {
//	      xfinal2 = new int[CITY_NUM+1];
//	      yfinal2 = new int[CITY_NUM+1];
//	      for(int i=0;i <= CITY_NUM;i++)
//	       {
//	       xfinal2[i]=xarr[array[i]];
//	       yfinal2[i]=yarr[array[i]];
//	       }
//	  }

   
   static void TSPbuild() {
	    tspPath = new int[CITY_NUM+1];
	    boolean already[] = new boolean[CITY_NUM];
	    int ind=0,node;
	    for (int i=0;i<CITY_NUM;already[i++]=false);
	    for (int i=0;i<= numOfEdges;i++)
	      {
	       node = eulertour[i];
	       if (!already[node])
	         {
	          tspPath[ind]=node ;
	          already[node]=true;
	          ind++;
	         }
	      }
	    //TSPpath(tspPath);
	 }

   static public void solve() {
	   int i1,i2,temp;
	   int i11,i12,i21,i22;
	   boolean more;
	   int tempOrder[] = new int[CITY_NUM+1];
	   int improved[] = new int[CITY_NUM+1];

	   // improve over previos improve
	   Oldcost=alg2Weight;
	      Newcost=0;
	      for (int i=0;i<=CITY_NUM;i++)
	        improved[i]=tspPath[i];

	   for (int i=1;i<=Iterations;i++)
	     {
	      i1= (int)(CITY_NUM*Math.random());// Find two random cities
	      i2= (int)(CITY_NUM*Math.random());

	      if (i2<i1)
	        {temp=i2;i2=i1;i1=temp;}    	// i1 should be lowest;
	      if (i1==i2)
	        {
	         if (i2==(CITY_NUM-1))
	             i1--;
	         else i2++;
	        }		// check ranges


	      i11=improved[i1];
	      i12=improved[i1+1];	// Find the city after city i1
	      i21=improved[i2];	// Find the city after city i2
	      if (i2==(CITY_NUM-1))
	         i22=improved[0];
	      else
	         i22=improved[i2+1];

	      if ( (distMatr[i11][i12]+distMatr[i21][i22]) >
		          (distMatr[i11][i21]+distMatr[i12][i22]) )
		        {
			     for (int j=i1+1;j<=i2;j++)
			       tempOrder[j]=improved[j];
	  		     for (int j=i1+1;j<=i2;j++)
			       improved[j]=tempOrder[1+i2+i1-j];
	     	    }
	     }

	    more=true;
	    while (more)
	     {
	     more=false;   
	     for (int i=0;i<CITY_NUM;i++)
	      for (int j=0;j<CITY_NUM;j++)
	       {
	        i1=i;
	        i2=j;
	        if (i2<i1)
	          {temp=i2;i2=i1;i1=temp;}    	// i1 should be lowest;
	        if (i1==i2)
	          {
	           if (i2==(CITY_NUM-1))
	              i1--;
	           else i2++;
	          }		// check ranges

	        i11=improved[i1];
	        i12=improved[i1+1];	// Find the city after city i1
	        i21=improved[i2];	// Find the city after city i2
	        if (i2==(CITY_NUM-1))
	           i22=improved[0];
	        else
	           i22=improved[i2+1];

	        if ( (distMatr[i11][i12]+distMatr[i21][i22]) >
		          (distMatr[i11][i21]+distMatr[i12][i22]) )
		        {
			     more=true;
			     for (int k=i1+1;k<=i2;k++)
			       tempOrder[k]=improved[k];
	  		     for (int k=i1+1;k<=i2;k++)
			       improved[k]=tempOrder[1+i2+i1-k];
	     	    }
	       }
	     }            
	    
	       alg2Wimproved=calculateTotal(improved);
	       
	       if(alg2Wimproved<best)
	       {
	    	   tsp2Improved=improved;
	    	   best = alg2Wimproved;
	       }
	     
	  }

   public static void main(String[] args) {
		String  path=null;
		if (args.length == 1) {
			path = args[0];
		} else {
			System.out.println("Please type file path, try again. ");
			System.exit(0);
		}
	   
		MSTcreate(path);
		oddsHendler();
		MatchHendler();
		EulerHendler();
		tsp2Hendler();
		for(int i=0;i<100;i++)
		solve();
		
		/*
		System.out.println(Arrays.toString(parent));
		System.out.println(Arrays.toString(Match));
		System.out.println(Arrays.toString(tspPath));
		System.out.println(alg2Weight);
		
		System.out.println(alg2Wimproved);
		System.out.println(Arrays.toString(tsp2Improved));
		
		Hashtable<Integer, Integer> ht = new Hashtable<Integer, Integer>();
		
		for(int tmp:tsp2Improved)
		{
			if(!ht.containsKey(Integer.valueOf(tmp)))
				ht.put(Integer.valueOf(tmp), 1);
			else {
				ht.put(Integer.valueOf(tmp), ht.get(Integer.valueOf(tmp))+1);
			}
		}
		
		for(int i=0;i<1000;i++)
		{
			if(i==0)
			{
			if(ht.get(Integer.valueOf(i))!=2)
				{
				System.out.print("error");
				break;
				}
			}
			else if(ht.get(Integer.valueOf(i))!=1)
			{
				System.out.println(i+" : "+ ht.get(Integer.valueOf(i)));
				break;
			}
		}
		*/
		
		//System.out.println(calculateTotal(tsp2Improved));

		for(int i=0;i<tsp2Improved.length;i++)
			tsp2Improved[i]++;
		System.out.println(Arrays.toString(tsp2Improved));
		System.out.println(best);

		

	}

}
