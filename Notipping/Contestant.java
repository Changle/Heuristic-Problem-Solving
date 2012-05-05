import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class Contestant extends NoTippingPlayer {
	private static BufferedReader br;
	private ArrayList<Weight> avWeights;
	private ArrayList<Integer> avSpots;
	private HashMap<Integer, Weight> myPos;
	private HashMap<Integer, String> criticalSec;
	private String mycolor;
	private String phase;
	private int left_torque;
	private int right_torque;

	Contestant(int port) {
		super(port);
	}

	private void init(List<String> command) {
		avWeights = new ArrayList<Weight>();
		avSpots = new ArrayList<Integer>();
		myPos = new HashMap<Integer, Weight>();
		criticalSec = new HashMap<Integer, String>();

		boolean allEmpty = true;
		for (String s : command) {
			List<String> myCommands = new ArrayList<String>(Arrays.asList((s
					.split(" "))));
			int position = Integer.valueOf(myCommands.get(1));
			String color = myCommands.get(2);
			if (color.equals("Red") || color.equals("Blue"))
				if (position != 0) {
					allEmpty = false;
					break;
				}
		}
		if (allEmpty) {
			mycolor = "Red";
		} else {
			mycolor = "Blue";
		}

		for (int i = -15; i <= 15; i++) {
			for (int j = 1; j <= 10; j++) {
				if (isOk(j, i)) {
					if (criticalSec.containsKey(i))
						criticalSec.put(i, criticalSec.get(i) + " " + j);
					else
						criticalSec.put(i, String.valueOf(j));
				}
			}
		}

	}

	private ArrayList<Integer> readCritical(Integer pos) {
		ArrayList<String> tmp = new ArrayList<String>(
				Arrays.asList((criticalSec.get(pos).split(" "))));
		ArrayList<Integer> avP = new ArrayList<Integer>();
		for (int i = 0; i < tmp.size(); i++)
			avP.add(Integer.valueOf(tmp.get(i)));

		return avP;

	}

	private void readState(String state) {
		List<String> myCommands = new LinkedList<String>(Arrays.asList((state
				.split("\n"))));
		phase = myCommands.get(0);
		myCommands.remove(0);
		if (mycolor == null) {
			init(myCommands);
		}
		avWeights.clear();
		avSpots.clear();
		myPos.clear();

		for (String s : myCommands) {

			List<String> oneline = new ArrayList<String>(Arrays.asList((s
					.split(" "))));
			int used = Integer.valueOf(oneline.get(0));
			int position = Integer.valueOf(oneline.get(1));
			String weightcolor = oneline.get(2);
			int weight = Integer.valueOf(oneline.get(3));

			if (phase.equals("ADDING")) {
				if (used == 0) {
					Weight newWeight = new Weight(weight, weightcolor, false,
							99);
					if (weightcolor.equals(mycolor))
						avWeights.add(newWeight);
				} else {
					Weight newWeight = new Weight(weight, weightcolor, true,
							position);
					myPos.put(position, newWeight);
				}
			} else {

				if (used == 1) {
					Weight newWeight = new Weight(weight, weightcolor, true,
							position);
					avWeights.add(newWeight);
					myPos.put(position, newWeight);
				}
			}
		}

		for (int i = -15; i <= 15; i++) {

			if (phase.equals("ADDING") && myPos.get(i) == null)
				avSpots.add(i);
			if (phase.equals("REMOVING") && myPos.get(i) != null)
				avSpots.add(i);
		}

	}

	private boolean isOver(int weight, int pos, boolean add) {
		left_torque = 0;
		right_torque = 0;
		for (Integer key : myPos.keySet()) {
			Weight w = (Weight) myPos.get(key);
			left_torque -= (w.position - (-3)) * w.weight;
			right_torque -= (w.position - (-1)) * w.weight;
		}

		if (add) {
			left_torque -= (pos - (-3)) * weight;
			right_torque -= (pos - (-1)) * weight;
		} else {
			left_torque += (pos - (-3)) * weight;
			right_torque += (pos - (-1)) * weight;
		}
		// for board
		left_torque -= 3 * 3;
		right_torque -= 3;

		if (left_torque > 0 || right_torque < 0)
			return true;
		else
			return false;
	}

	private String makeMove() {

		Random generator = new Random();

		if (mycolor.equals("Red")) {

			if (phase.equals("ADDING")) {

				int bestScore = -99;
				int bestWeight = 0;
				int bestPos = 0;

				ArrayList<Weight> avW = (ArrayList<Weight>) avWeights.clone();

				for (Integer key : criticalSec.keySet()) {
					if (myPos.containsKey(key))
						continue;

					ArrayList<Integer> avoidWeight = readCritical(key);

					for (int i = 0; i < avW.size(); i++) {
						Integer aw = avW.get(i).weight;

						if (isOver(aw, key, true))
							continue;

						if (avoidWeight.contains(aw))
							continue;
						else {

							int score = evaluate(aw, (int) key, true);
							if (score > bestScore) {
								bestWeight = aw;
								bestPos = key;
								bestScore = score;
							}
						}
					}
				}

				if (bestWeight == 0) {

					bestScore = -99;

					for (int i = 0; i < avW.size(); i++) {
						int weight = avW.get(i).weight;

						for (int j = 0; j < avSpots.size(); j++) {
							int pos = avSpots.get(j);
							if (!isOver(weight, pos, true)) {

								int score = evaluate(weight, pos, true);
								if (score > bestScore) {
									bestWeight = weight;
									bestPos = pos;
									bestScore = score;
								}

							}
						}
					}
				}

				if (bestWeight == 0) {
					bestWeight = avWeights.get(generator.nextInt(avWeights
							.size())).weight;
					bestPos = avSpots.get(generator.nextInt(avSpots.size()));
				}

				return bestPos + " " + bestWeight;

			} else {

				int best_weight = 0;
				int best_pos = 0;
				int best_score = 999999;

				for (Integer key : criticalSec.keySet()) {
					if (myPos.containsKey(key)) {
						int weight = myPos.get(key).weight;
						ArrayList<Integer> arrayweight = readCritical(key);

						if (arrayweight.contains(weight)) {
							if (isOver(weight, key, false))
								continue;
							int score = evaluate(weight, (int) key, false);
							
							if(nextAvailableMove(myPos.get(key),key)==1)
								continue;
							if (score < best_score) {
								best_weight = weight;
								best_pos = key;
								best_score = score;
							}
						}
					}
				}

				if (best_weight != 0)
					return best_pos + " " + best_weight;

				int position;
				int weight;
				int score;
				best_score = 99999999;

				for (int i = 0; i < avSpots.size(); i++) {
					position = avSpots.get(i);
					weight = myPos.get(position).weight;
					if (isOver(weight, position, false))
						continue;
					score = evaluate(weight, position, false);
					if(nextAvailableMove(myPos.get(position),position)==1)
						score += 10000;
					
					if (score < best_score) {
						best_weight = weight;
						best_pos = position;
						best_score = score;
					}
				}

				if (best_weight == 0) {
					best_pos = avSpots.get(generator.nextInt(avSpots.size()));
					best_weight = myPos.get(best_pos).weight;
				}

				return best_pos + " " + best_weight;

			}
		} else {
			
			
			if (phase.equals("ADDING")) {

				int bestScore = -99;
				int bestWeight = 0;
				int bestPos = 0;

				ArrayList<Weight> avW = (ArrayList<Weight>) avWeights.clone();

				for (Integer key : criticalSec.keySet()) {
					
					if (myPos.containsKey(key))
						continue;

					ArrayList<Integer> avoidWeight = readCritical(key);

					for (int i = 0; i < avW.size(); i++) {
						Integer aw = avW.get(i).weight;

						if (isOver(aw, key, true))
							continue;

						if (avoidWeight.contains(aw))
						{
							int score = evaluate(aw, (int) key, true);
							if (score > bestScore) {
								bestWeight = aw;
								bestPos = key;
								bestScore = score;
							}
						}
					}
				}

				if (bestWeight == 0) {

					bestScore = -99;

					for (int i = 0; i < avW.size(); i++) {
						int weight = avW.get(i).weight;

						for (int j = 0; j < avSpots.size(); j++) {
							int pos = avSpots.get(j);
							if (!isOver(weight, pos, true)) {

								int score = evaluate(weight, pos, true);
								if (score > bestScore) {
									bestWeight = weight;
									bestPos = pos;
									bestScore = score;
								}

							}
						}
					}
				}

				if (bestWeight == 0) {
					bestWeight = avWeights.get(generator.nextInt(avWeights
							.size())).weight;
					bestPos = avSpots.get(generator.nextInt(avSpots.size()));
				}

				return bestPos + " " + bestWeight;

			} else {

				int best_weight = 0;
				int best_pos = 0;
				int best_score = 999999;
				
				for(int i=0;i<avSpots.size();i++)
				{
					int pos = avSpots.get(i);
					int weight = myPos.get(pos).weight;
					if(criticalSec.keySet().contains(pos))
					{
						ArrayList<Integer> wlist = readCritical(pos);
						if(wlist.contains(weight))
							continue;
					}
					
					if (isOver(weight, pos, false))
						continue;
					
					int score = evaluate(weight, (int) pos, false);
					
					if(nextAvailableMove(myPos.get(pos),pos)==1)
						score += 10000;;
					
					if (score < best_score) {
						best_weight = weight;
						best_pos = pos;
						best_score = score;
					}	
					
				}
				
				if(best_weight!=0)
					return best_pos + " " + best_weight;

				best_score = 999999;
				
				for(Integer pos: criticalSec.keySet())
				{
					if(myPos.containsKey(pos))
					{
						int weight = myPos.get(pos).weight;
						if (isOver(weight, pos, false))
							continue;
						int score = evaluate(weight, (int) pos, false);
						if(nextAvailableMove(myPos.get(pos),weight)==1)
							score += 10000;;
						if (score < best_score) {
							best_weight = weight;
							best_pos = pos;
							best_score = score;
						}	

					}
				}
				
				if(best_weight!=0)
					
				return best_pos + " " + best_weight;


				if (best_weight == 0) {
					best_pos = avSpots.get(generator.nextInt(avSpots.size()));
					best_weight = myPos.get(best_pos).weight;
				}

				return best_pos + " " + best_weight;

			}
			
			
			
		}

	}

	int nextAvailableMove(Weight weight, int pos)
	{
		int count=0;
		myPos.remove(pos);
		
		for(Integer position:myPos.keySet())
		{
			int tmpWeight = myPos.get(position).weight;
			
			if(!isOver(tmpWeight,position,false))
				count++;		
		}
		myPos.put(pos, weight);
		return count;
		
	}

	private int evaluate(int weight, int pos, boolean flag) {

		int score = 0;

		if (flag == true) {
			if (isOver(weight, pos, true))
				return -1;

			for (Integer key : myPos.keySet()) {
				int tmpWeight = myPos.get(key).weight;

				if (criticalSec.containsKey(key)) {
					ArrayList<Integer> tmpList = readCritical(key);
					if (!tmpList.contains(tmpWeight)) {
						score = score
								+ tmpWeight
								* Math.min(Math.abs(key - (-3)),
										Math.abs(key - (-1)));
					}
				} else {
					score = score
							+ tmpWeight
							* Math.min(Math.abs(key - (-3)),
									Math.abs(key - (-1)));
				}
			}
			score = score + weight
					* Math.min(Math.abs(pos - (-3)), Math.abs(pos - (-1)));

		} else {

			if (isOver(weight, pos, false))
				return -1;

			for (Integer key : myPos.keySet()) {
				int tmpWeight = myPos.get(key).weight;

				if (criticalSec.containsKey(key)) {
					ArrayList<Integer> tmpList = readCritical(key);
					if (!tmpList.contains(tmpWeight)) {
						score = score
								+ tmpWeight
								* Math.min(Math.abs(key - (-3)),
										Math.abs(key - (-1)));
					}
				} else {
					score = score
							+ tmpWeight
							* Math.min(Math.abs(key - (-3)),
									Math.abs(key - (-1)));
				}
			}
			score = score - weight
					* Math.min(Math.abs(pos - (-3)), Math.abs(pos - (-1)));

		}
		return score;

	}

	private String randomPlay() {

		Random generator = new Random();
		if (phase.equals("ADDING")) {

			ArrayList<Weight> avW = (ArrayList<Weight>) avWeights.clone();

			do {

				int index = generator.nextInt(avW.size());
				int weight = avW.get(index).weight;
				Collections.sort(avSpots);

				for (int i = 0; i < avSpots.size(); i++) {
					int position = avSpots.get(i);
					if (isOver(weight, position, true) == false) {
						return position + " " + weight;
					}
				}
				avW.remove(index);
			} while (avW.size() != 0);

		} else {

			int position;
			int weight;
			ArrayList<Integer> spots = (ArrayList<Integer>) avSpots.clone();
			do {
				int index = generator.nextInt(spots.size());
				position = spots.get(index);
				weight = myPos.get(position).weight;
				spots.remove(index);
			} while (isOver(weight, position, false) && spots.size() != 0);

			return position + " " + weight;

		}

		int pos;
		int weight;

		if (phase.equals("ADDING")) {
			weight = avWeights.get(generator.nextInt(avWeights.size())).weight;
			pos = avSpots.get(generator.nextInt(avSpots.size()));

		} else {
			pos = avSpots.get(generator.nextInt(avSpots.size()));
			weight = myPos.get(pos).weight;
		}

		// System.out.println(pos+" "+weight);

		return pos + " " + weight;
	}

	protected String process(String command) {
		System.out.println(command);
		System.out.println("Enter move (position weight): ");
		readState(command);
		String move = makeMove();
		// String move = randomPlay();
		System.out.println(move);
		return move;
		/*
		 * try { return br.readLine(); } catch (Exception ev) {
		 * System.out.println(ev.getMessage()); } return "";
		 */
	}

	public static boolean isOk(int weight, int position) {
		int left_torque = 0;
		int right_torque = 0;

		left_torque -= (position - (-3)) * weight;
		right_torque -= (position - (-1)) * weight;

		left_torque -= 3 * 3;
		right_torque -= 3;

		if (left_torque > 0 || right_torque < 0)
			return false;
		else
			return true;
	}

	public static void main(String[] args) throws Exception {
		// br = new BufferedReader(new InputStreamReader(System.in));
		new Contestant(Integer.parseInt(args[0]));
	}
}

class Weight {

	public Weight(int weight, String color, boolean used, int position) {
		this.weight = weight;
		this.color = color;
		this.used = used;
		this.position = position;
	}

	public int weight;
	public String color;
	public boolean used;
	public int position;
}
