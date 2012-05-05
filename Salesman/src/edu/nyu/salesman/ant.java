package edu.nyu.salesman;

import java.math.BigDecimal;

import edu.nyu.utils.AdjacencyGenerator;

public class ant {



int num_cities;
int num_ants;


int edgeLen = 99999; // max edge length when creating city graph.

class Ant
{
int current_city;
int next_city;
int tabu[];
int tour_index;
int tour[];
double tour_length;

Ant(){
 tabu = new int[num_cities];
 tour = new int[num_cities];
}
}

//ACO-equation variables
//keep alpha,beta and rho within [0,1]
float alpha_value = 1.0f; // higher value -> more scouting behaviour
float beta_value = 1.0f; // higher value -> higher importance of distance cost
float rho = 0.9f; // decay rate
float qval = 500; // amount of pherom to spread on tour.

Point cities[];
Ant ants[];
double precomp_distance[][]; // edge length, store distances between cities
double pherom[][]; // pherom levels on each edge
double base_pherom; // minimum pherom level

double best_tour; // length of best tour found
int best_index = -1; // index of ants[] with best tour
int best_tour_history[]; // save ant tour history here

int iterations; // no of iterations used on ACO

float iterationTimer = 0; // remember time
float iterationTimeLength = 0.05f; // how fast to iterate in seconds
int grabbedNode = -1;

void initACO()
{
iterations = 0;
num_cities = 1000;
num_ants = 25;

best_tour = 9999999999.0;
base_pherom = (float) (1.0 / num_cities);

cities = new Point[num_cities];
ants = new Ant[num_ants];
for(int i=0; i < num_ants; i++){
  ants[i] = new Ant();
}

precomp_distance = new double[num_cities][num_cities];
pherom = new double[num_cities][num_cities]; 
best_tour_history = new int[num_cities];

initCities();
initAnts();
}

void setup()
{
                    
initACO();

}

public static void main(String[] args) {
//background(127,127,127);

//float time = (float) millis() / 1000.0f;
//if(time > iterationTimer)
//{
 ant app = new ant();
// app.num_cities=50;
// app.num_ants = 50;
 
 app.setup();
 
 for(int i=0; i<100; i++)
   //  if( app.moveAnts() == 0) // are all ants finished moving?
     {
    	 app.moveAnts();
         app.evaporatePheromoneTrails();
         app.intensifyPheromoneTrails();
         app.findBestTour();
         app.initAnts(); // reset ant position and tour
         app.drawBestTour();

     }
/*
	// app.initAnts();
	 	app.moveAnts();
	   app.evaporatePheromoneTrails();
       app.intensifyPheromoneTrails();
       app.findBestTour();
      // app.initAnts(); // reset ant position and tour*/
 
// iterationTimer = time + iterationTimeLength;  
//}

//if(app.best_index >= 0) // found a good tour?
//{
//}

}



void drawBestTour()
{

	
for(int i=0; i < num_cities; i++)
{
 System.out.print(best_tour_history[i]+ " ");
}
System.out.println(best_tour_history[num_cities-1]);
BigDecimal big= new BigDecimal(best_tour);

System.out.println(big);

}


void initCities()
{
	
	precomp_distance=AdjacencyGenerator.generate("map");
	/*
	for(int i=0;i<1000;i++)
		System.out.print(precomp_distance[0][i]+" ");
	System.out.println();
*/
	resetPherom();

// precompute the distances between cities
//computeCityDistances();
}


void resetPherom()
{
for(int from=0; from < num_cities; from++){
 for(int to=0; to < num_cities; to++){
   pherom[from][to] = base_pherom;
   pherom[to][from] = base_pherom;
 }
}
}

void findBestTour()
{
for(int i=0; i < num_ants; i++){
 if( ants[i].tour_length < best_tour){
   best_tour = ants[i].tour_length;
   best_index = i;
    
   for(int j=0; j < num_cities; j++) // remember tour
   {
     best_tour_history[j] = ants[i].tour[j];
   }
 }
}
}

void initAnts()
{
int city = 0;
// place ants throughout the cities (evenly if possible)
for(int i=0; i< num_ants; i++){
  
 for(int j=0; j< num_cities; j++){
   ants[i].tabu[j] = 0;
   ants[i].tour[j] = 0;
 }
 // place this ant in a city, and reflect it in the tabu
 ants[i].current_city = city; //(int)random(0,num_cities);//city;
 //ants[i].next_city = city; // will be set on choosenext
 //city++;
 //city %= num_cities;
 ants[i].tabu[ ants[i].current_city ] = 1;

 // update the tour, and current tour length given the current path
 ants[i].tour[0] = ants[i].current_city;
 ants[i].tour_index = 1;
 ants[i].tour_length = 0.0;
}
}

void chooseNextCity(Ant ant)
{

double d = 0.0;
double p = 0.0;

int from = ant.current_city;

//
int to=0;
for(to=0; to < num_cities; to++){ 
 if( ant.tabu[to] == 0){ // if city not yet visited
   d += Math.pow(pherom[from][to], (float)alpha_value) *
   	Math.pow( (1.0/precomp_distance[from][to]), (float)beta_value );
 }
}

// Probabilistically select the next city
to = 0;
int stuck = 0;
while(true){
 if( ant.tabu[to] == 0){ // if city not yet visited
 // equation 14.1
 p = Math.pow(pherom[from][to], (float)alpha_value) *
               Math.pow( (1.0/precomp_distance[from][to]), (float)beta_value ) / d;
 if( Math.random() <= p ) break; // roll dice and see if we choose to go to city
 }
 to++;
 to %= num_cities;
 // shouldnt need this:
 //stuck++;
 //if(stuck > 1000) break;
}

// we have our new destination, update for new city
ant.next_city = to;
ant.tabu[ant.next_city] = 1; // mark as visited
ant.tour[ant.tour_index] = ant.next_city; // update tour log
ant.tour_index++;
ant.tour_length += precomp_distance[ant.current_city][ant.next_city];



// visited all cities, add distance from start to end.
if(ant.tour_index == num_cities){
 ant.tour_length +=
   precomp_distance[ ant.tour[num_cities-1] ]  [ant.tour[0] ];
/*
 	if(ant.tour_length<best_tour)
 	{
 		best_tour = ant.tour_length;
 	}
 */
}


ant.current_city = ant.next_city; //!!!
}

int moveAnts()
{
int moved = 0;

for(int i=0; i < num_ants; i++){
	while( ants[i].tour_index < num_cities ){
		chooseNextCity( ants[i] );
   moved++;
 }
}
return moved; // if we couldnt move, we have visited all. We need to re-init. Return 0
}

void evaporatePheromoneTrails()
{
for(int from = 0; from < num_cities; from++){
 for(int to=0; to < num_cities; to++){
   // equation 14.4
   pherom[from][to] *= (1.0 - rho);
   if(pherom[from][to] < 0.0){
     pherom[from][to] = base_pherom;
   }
 }
}
}
void intensifyPheromoneTrails()
{
for(int i=0; i < num_ants; i++){
 for(int city = 0; city < num_cities; city++){
   int from = ants[i].tour[city];
   int to = ants[i].tour[ ((city+1) % num_cities) ];

   // eq 14.2 / 14.3
   pherom[from][to] += (qval/ants[i].tour_length) * rho;
   pherom[to][from] = pherom[from][to]; 
 }
}
}


}
