package edu.nyu.muhchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import edu.nyu.entity.CircularList;
import edu.nyu.entity.Node;
import edu.nyu.parse.Parser;

public class Munchers {

	List<Ant> confuseWaiting = new LinkedList<Ant>();

	List<Ant> betterWaiting = new LinkedList<Ant>();

	Map<Node, Integer> componentsWithSize;

	Map<Node, List<Node>> componentsWithLeaves = new HashMap<Node, List<Node>>();

	Map<Node, List<Node>> componentsWithNodes = new HashMap<Node, List<Node>>();

	Map<Node, List<Ant>> doneAntsResult = new HashMap<Node, List<Ant>>();

	List<Node> allNodes;

	List<Ant> allDoneAnts = new ArrayList<Ant>();

	int antCount = 0;

	List<Direction> horizontals = new ArrayList<Direction>();

	List<Direction> verticals = new ArrayList<Direction>();
	    
    //CircularList<Direction> defaultWalkPattern = null;

	public Munchers(String path) {
		List<Node> nodes = Parser.parse(path);
		allNodes = new ArrayList<Node>(nodes);
		componentsWithSize = getComponents(nodes);
//		for (Node node : componentsWithSize.keySet()) {
//			System.out.println(node + "size = " + componentsWithSize.get(node));
//		}
		for (Node node : componentsWithSize.keySet()) {
			List<Node> leaves = getLeaves(node);
			List<Node> nodesInComp = getNodes(node);
			componentsWithLeaves.put(node, leaves);
			componentsWithNodes.put(node, nodesInComp);
		}
		horizontals.add(Direction.LEFT);
		horizontals.add(Direction.RIGHT);
		verticals.add(Direction.UP);
		verticals.add(Direction.DOWN);
		List<Direction> dwp = new ArrayList<Direction>();
		dwp.add(Direction.LEFT);
		dwp.add(Direction.RIGHT);
		dwp.add(Direction.UP);
		dwp.add(Direction.DOWN);
		//defaultWalkPattern = new CircularList<Direction>(dwp);
	}

	public enum Direction {
		UP, LEFT, DOWN, RIGHT;
	};


	public static enum AntState {
		MOVED, CONFUSE_WAITING, BETTER_WAITING, DONE
	};
	
	int time = 0;
	
	boolean checkWalkPatternMatch(CircularList<Direction> p1,
			CircularList<Direction> p2) {
		if (p1 != null && p2 != null) {
			return p1.equals(p2);
		}
		return false;
	}
	
	boolean checkWalkPatternMatchWithRestriction(CircularList<Direction> p,
			CircularList<Direction> r) {
		if (p.size() != 4 || r.size() != 3) {
		}
		if (p.size() == 4 && r.size() == 3) {
			List<Direction> list = new LinkedList<Direction>(p.getList());
			CircularList<Direction> pl = new CircularList<Direction>(list);
			for (int i = 0; i < 4; i++) {
				Direction d = p.next();
				if (!r.contains(d)) {
					pl.remove(d);
				}
			}
			return pl.equals(r);		
		}
		return false;
	}
	
	Node getSubGraph(List<Node> nodes) {
		List<Node> nodesCopy = new ArrayList<Node>();
		for (Node node : nodes) {
			Node n = new Node(node);
			nodesCopy.add(n);
		}
		Node root = null;
		for (Node node : nodesCopy) {
			List<Node> conns = node.getConnections();
			if (root == null && conns.size() == 1) {
				root = node;
			}
			for (Node node2 : conns) {
				if (!nodesCopy.contains(node2)) {
					node.removeConn(node2);
				}
			}
		}
		return root;
	}
	public void antVisiteNode(Node node, Ant ant, AntState state) {
		node.ant = ant;
		node.timestamp = ant.timePast;
		ant.state = state;
	}
	
	public void putAnt(List<Node> leaves, boolean special, List<Node> componentVisitedNodes,
			boolean fromStart, int[] tabu, List<Ant> movingAnts, List<Node> componentNodes) {
		int minNumOfAnts = Math.round((float)leaves.size() / 2);
		//int minNumOfAnts = 1;
		int maxTimestamp = 0;
		if (special) {
			for (Node node : componentVisitedNodes) {
				if (node.timestamp > maxTimestamp) {
					maxTimestamp = node.timestamp;
				}
			}
		}
		int count = 0;
		while (count < minNumOfAnts) {
			List<Node> list = leaves;
			int random = (int)(Math.random() * list.size());
			Node node = list.get(random);
			assert(node.ant == null);
			Ant ant = new Ant(++antCount, node);
			if (ant.patterns == null) {
			}
			if (special) {
				ant.timeDropped = maxTimestamp;
			} else {
				if (fromStart) {
					ant.timeDropped = 0;
				} else {
					List<Node> conns = node.getConnections();
					int maxTime = 0;
					for (Node node2 : conns) {
						if (tabu[node2.id] == 1) {
							if (node2.timestamp > maxTime) {
								maxTime = node2.timestamp;
							}
						}
					}
					ant.timeDropped = maxTime;
				}
			}
			ant.timePast = ant.timeDropped;
			node.timestamp = ant.timeDropped;
			tabu[node.id] = 1;
			node.ant = ant;
			ant.state = AntState.MOVED;
			movingAnts.add(ant);
			list.remove(node);
			componentNodes.remove(node);
			componentVisitedNodes.add(node);
            count++;
		}

	}

	public void go() {
		int sum = 0;
		for (Integer integer : componentsWithSize.values()) {
			sum += integer;
		}
		int[] tabu = new int[allNodes.size()];
		for (Node root : componentsWithSize.keySet()) {
//			if (root.id != 3) {
//				continue;
//			}
			if (componentsWithSize.get(root).equals(1)) {
				antCount++;
				Ant ant = new Ant(antCount, root);
				if (ant.patterns == null) {
				}
				ant.state = AntState.DONE;
				ant.timePast = 0;
				root.ant = ant;
				root.timestamp = ant.timePast;
				tabu[root.id] = 1;
				List<Ant> doneAnts = new LinkedList<Ant>();
				doneAnts.add(ant);
				doneAntsResult.put(root, doneAnts);
			} else {
				int itCount = 0;
				int minAntsCount = Integer.MAX_VALUE;
				int iterationCount = 1;//50 * componentsWithSize.get(root);
				while (itCount < iterationCount) {
					itCount++;
					List<Node> componentLeaves = new LinkedList<Node>(componentsWithLeaves.get(root));
                    List<Node> componentNodes = new LinkedList<Node>(componentsWithNodes.get(root));
                    List<Node> componentVisitedNodes = new LinkedList<Node>();
                    List<Ant> movingAnts = new LinkedList<Ant>();
                    List<Ant> doneAnts = new LinkedList<Ant>();
                    Map<Node, Integer> holeComponentsWithSize = null;
                    Map<Node, List<Node>> holeComponentsWithNodes = new HashMap<Node, List<Node>>();
                    Map<Node, List<Node>> holeComponentsWithLeaves = new HashMap<Node, List<Node>>();
					boolean special = false;
					int turn = 0;
					int total = componentsWithSize.get(root);
					while (componentNodes.size() > 0 || movingAnts.size() > 0) {
						if (turn == 20000) {
							//.out.println();
						}
						if (movingAnts.size() == 0) {
							boolean fromStart = false;
							List<Node> leaves = null;
							if (componentNodes.size() == total) {
								leaves = componentLeaves;
								/*leaves = new ArrayList<Node>();
								for (Node node : allNodes) {
									if (node.x == 0 && node.y == 1) {
										leaves.add(node);
										
									}
									if (node.x == 3 && node.y == 0) {
										leaves.add(node);
									}
								}
								fromStart = true;
								for (Node node : leaves) {
									Ant ant = new Ant(++antCount, node);
									if (special) {
									} else {
										if (fromStart) {
											ant.timeDropped = 0;
										} else {
											List<Node> conns = node.getConnections();
											int maxTime = 0;
											for (Node node2 : conns) {
												if (tabu[node2.id] == 1) {
													if (node2.timestamp > maxTime) {
														maxTime = node2.timestamp;
													}
												}
											}
											ant.timeDropped = maxTime;
										}
									}
									ant.timePast = ant.timeDropped;
									node.timestamp = ant.timeDropped;
									tabu[node.id] = 1;
									node.ant = ant;
									ant.state = AntState.MOVED;
									movingAnts.add(ant);
									componentNodes.remove(node);
									System.out.println("remove: " + node.x + " " + node.y);
									componentVisitedNodes.add(node);

								}*/
								fromStart = true;
								putAnt(leaves, special, componentVisitedNodes,
										fromStart, tabu, movingAnts, componentNodes);
							} else {
//								List<Node> unvisitedNodes = componentNodes;
//								leaves = new ArrayList<Node>();
//								leaves.add(unvisitedNodes.get(0));
//								putAnt(leaves, special, componentVisitedNodes,
//										fromStart, tabu, movingAnts, componentNodes);
								List<Node> unvisitedNodes = (List<Node>) ((LinkedList<Node>)componentNodes).clone();
//								System.out.println(unvisitedNodes.size());
								int ssum = 0;
								for (int i : tabu) {
									ssum += i;
								}
								holeComponentsWithSize = getComponents(unvisitedNodes);
								int totalNumAnts = doneAnts.size();
								for (Ant a : doneAnts) {
								}
								for (Ant a : doneAnts) {
								}
								

								for (Node node : holeComponentsWithSize.keySet()) {
									holeComponentsWithLeaves.put(node, getHoleLeaves(node));
									holeComponentsWithNodes.put(node, getHoleNodes(node));
//									System.out.println("holeleaves size: " + holeComponentsWithLeaves.get(node).size());
//									System.out.println("holenodes size: " + holeComponentsWithNodes.get(node).size());

								}
								unvisitedNodes = (List<Node>) ((LinkedList<Node>)componentNodes).clone();

								leaves = new LinkedList<Node>();
								for (Node node : holeComponentsWithSize.keySet()) {
									List<Node> holeLeavesList = holeComponentsWithLeaves.get(node);
									for (Node node2 : holeLeavesList) {
										int size = node2.getConnections().size();
										if (size > 1 || size == 0) {
											leaves.add(node2);
										}
									}
									if (leaves.size() > 0) {
										putAnt(leaves, special, componentVisitedNodes,
												fromStart, tabu, movingAnts, componentNodes);
									}
								}
								if (movingAnts.size() == 0) {
									for (Node node : holeComponentsWithSize.keySet()) {
										List<Node> holeLeavesList = holeComponentsWithLeaves.get(node);
										special = true;
										for (Node node2 : holeLeavesList) {
										    leaves.add(node2);
										}
										if (leaves.size() > 0) {
											putAnt(leaves, special, componentVisitedNodes,
													fromStart, tabu, movingAnts, componentNodes);
										}
										special = false;

									}
									if (movingAnts.size() == 0) {
										leaves.add(unvisitedNodes.get(0));
										special = true;
										putAnt(leaves, special, componentVisitedNodes,
												fromStart, tabu, movingAnts, componentNodes);
										special = false;
									}
								}
							}			
						} else {
							List<Ant> completeAnts = new ArrayList<Ant>();
							Map<Node, List<Ant>> map = new HashMap<Node, List<Ant>>();
							for (Ant ant : movingAnts) {
								if (ant.patterns.size() == 0) {
								}
								Node to = ant.move(tabu);
								if (ant.state.equals(AntState.DONE)) {
								    completeAnts.add(ant);
								    if (ant.patterns.size() == 0) {
									}
								    //componentNodes.remove(to);
								    continue;
							    }
								if (to == null) {
									ant.move(tabu);
								}

								List<Ant> as = map.get(to);
								if (as == null) {
									as = new LinkedList<Ant>();
								}
								as.add(ant);
								map.put(to, as);
							}
							for (Node to: map.keySet()) {
								List<Ant> as = map.get(to);
								Ant winAnt = null;
								if (as.size() == 1) {
									winAnt = as.get(0);
								} else {
									List<Integer> order = new LinkedList<Integer>();
									for (Ant ant : as) {
										Direction d = ant.current.getRelativeLocation(to);
										if (d.equals(Direction.UP)) {
											order.add(1);
										} else if (d.equals(Direction.LEFT)) {
											order.add(2);
										} else if (d.equals(Direction.DOWN)) {
											order.add(3);
										} else {
											order.add(4);
										}
									}
								    int minIndex = 0;
								    int min = Integer.MAX_VALUE;
								    for (int i = 0; i < order.size(); i++) {
										if (order.get(i) < min) {
											min = order.get(i);
											minIndex = i;
										}
									}
								    winAnt = as.get(minIndex);
								}
								if (winAnt.patternsMayRemove.size() > 0) {
									if (winAnt.patterns.size() == winAnt.patternsMayRemove.size()) {
										winAnt.patternsMayRemove.clear();
										winAnt.move(tabu);
									}
									winAnt.patterns.removeAll(winAnt.patternsMayRemove);
									winAnt.patternsMayRemove.clear();
								}
								winAnt.state = AntState.MOVED;
								winAnt.lastGoDirection = winAnt.current.getRelativeLocation(to);
								tabu[to.id] = 1;
								winAnt.trip.add(to);
								winAnt.current = to;
								winAnt.timePast += 1;
								to.timestamp = winAnt.timePast;
								to.ant = winAnt;
								if (winAnt.trip.size() == 2) {
									winAnt.fistDoDirection = winAnt.lastGoDirection;
								}
								componentNodes.remove(to);
								componentVisitedNodes.add(to);
								as.remove(winAnt);
								for (Ant ant : as) {
									if (ant.patternsMayRemove.size() > 0) {
										if (ant.patterns.size() == ant.patternsMayRemove.size()) {
											ant.patternsMayRemove.clear();
											ant.move(tabu);
										}
										ant.patterns.removeAll(ant.patternsMayRemove);
										ant.patternsMayRemove.clear();
									}

									ant.state = AntState.DONE;
									ant.current = to;
									ant.trip.add(to);
									ant.timePast += 1;
									completeAnts.add(ant);
								}
							}
							if (completeAnts.size() > 0) {
								movingAnts.removeAll(completeAnts);
								doneAnts.addAll(completeAnts);
							}
						}
					}
					if (doneAnts.size() < minAntsCount) {
						minAntsCount = doneAnts.size();
						doneAntsResult.put(root, doneAnts);				
					}
				}
			}
//			System.out.println("*************RESULT**************");
//			System.out.println("Graph size=" + componentsWithSize.get(root));
//			System.out.println("ant number=" + doneAntsResult.get(root).size());
//			System.out.println(doneAntsResult.get(root));
//			System.out.println("*********************************");
		}
		int totalNumAnts = 0;
		for (List<Ant> ants : doneAntsResult.values()) {
			totalNumAnts += ants.size();
		}
		System.out.println(totalNumAnts);
		for (List<Ant> ants : doneAntsResult.values()) {
			totalNumAnts += ants.size();
			for (Ant ant : ants) {
				System.out.println(ant.timeDropped + " " + ant.origin.x + " "
						+ ant.origin.y + " " + ant.patterns.get(0));
			}
		}
		
	}

	boolean isVertical(Direction d1, Direction d2) {
		if (horizontals.contains(d1)) {
			if (verticals.contains(d2)) {
				return true;
			}
		} else if (verticals.contains(d1)) {
			if (horizontals.contains(d2)) {
				return true;
			}
		}
		return false;
	}

	Direction getOppsiteDirection(Direction d) {
		switch (d) {
		case LEFT:
			return Direction.RIGHT;
		case RIGHT:
			return Direction.LEFT;
		case UP:
			return Direction.DOWN;
		case DOWN:
			return Direction.UP;
		}
		return null;
	}

	public class Ant {

		public int id;

		public Node origin;

		public Node current;

		public Direction lastGoDirection = null;

		public Direction fistDoDirection = null;

		AntState state;

		int timeDropped = 0;
		
		int timePast = 0;

		List<Node> leaves = null;

		// save ids in the path
		List<Node> trip = new LinkedList<Node>();

		//CircularList<Direction> walkPattern = null;

		List<CircularList<Direction>> patterns;
		
		List<CircularList<Direction>> patternsMayRemove = new LinkedList<CircularList<Direction>>();
		
		void addPatterns() {
			patterns = new LinkedList<CircularList<Direction>>();
			List<Direction> list1 = new ArrayList<Direction>();
			list1.add(Direction.UP);
			list1.add(Direction.DOWN);
			list1.add(Direction.LEFT);
			list1.add(Direction.RIGHT);
			CircularList<Direction> udlr = new CircularList<Direction>(list1);
			patterns.add(udlr);
			
			List<Direction> list2 = new ArrayList<Direction>();
			list2.add(Direction.UP);
			list2.add(Direction.DOWN);
			list2.add(Direction.RIGHT);
			list2.add(Direction.LEFT);
			CircularList<Direction> udrl = new CircularList<Direction>(list2);
			patterns.add(udrl);

			List<Direction> list3 = new ArrayList<Direction>();
			list3.add(Direction.UP);
			list3.add(Direction.LEFT);
			list3.add(Direction.DOWN);
			list3.add(Direction.RIGHT);
			CircularList<Direction> uldr = new CircularList<Direction>(list3);
			patterns.add(uldr);
			
			List<Direction> list4 = new ArrayList<Direction>();
			list4.add(Direction.UP);
			list4.add(Direction.LEFT);
			list4.add(Direction.RIGHT);
			list4.add(Direction.DOWN);
			CircularList<Direction> ulrd = new CircularList<Direction>(list4);
			patterns.add(ulrd);
			
			List<Direction> list5 = new ArrayList<Direction>();
			list5.add(Direction.UP);
			list5.add(Direction.RIGHT);
			list5.add(Direction.LEFT);
			list5.add(Direction.DOWN);
			CircularList<Direction> urld = new CircularList<Direction>(list5);
			patterns.add(urld);
			
			List<Direction> list6 = new ArrayList<Direction>();
			list6.add(Direction.UP);
			list6.add(Direction.RIGHT);
			list6.add(Direction.DOWN);
			list6.add(Direction.LEFT);
			CircularList<Direction> urdl = new CircularList<Direction>(list6);
			patterns.add(urdl);
		}
		public Ant(int id, Node origin) {
			this.id = id;
			this.origin = origin;
			timeDropped = 0;
			current = origin;
			state = AntState.MOVED;
			trip.add(origin);
			addPatterns();
		}

		public Ant(int id, Node origin, int timeDropped) {
			this.id = id;
			this.origin = origin;
			this.timeDropped = timeDropped;
			current = origin;
			state = AntState.MOVED;
			trip.add(origin);
			addPatterns();
		}


		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("state=" + state + "\n");
			sb.append("timeDropped=" + timeDropped + "\n");
			sb.append("orgin=" + origin + "\n");
			sb.append("current=" + current + "\n");
			//sb.append("walkPattern=" + pat + "\n");
			sb.append("restrictions=" + patterns);
			return sb.toString();
		}

		Node testAndGoOnlyPath(List<Node> unvisitedConns) {
			Node to = null;
			if (unvisitedConns != null && unvisitedConns.size() == 1) {
				to = unvisitedConns.get(0);
			}
			return to;
		}

		Node testAndGoDirectionNotCameFrom(List<Node> unvisitedConns) {
			Node to = null;
			if (unvisitedConns != null && unvisitedConns.size() == 2) {
				Direction d0 =
					current.getRelativeLocation(unvisitedConns.get(0));
				Direction d1 =
					current.getRelativeLocation(unvisitedConns.get(1));
				if (d0.equals(lastGoDirection)) {
					to = unvisitedConns.get(1);
				} else if (d1.equals(lastGoDirection)) {
					to = unvisitedConns.get(0);
				}
			}
			return to;
		}

		Node testAntGoByRestrictions(List<Node> unvisitedConns) {
			Node to = null;
			if (patterns.size() == 1) {
				Direction toD = patterns.get(0).next(lastGoDirection);
				to = current.getConnByDirection(toD);
			}
//			if (unvisitedConns != null && unvisitedConns.size() == 2) {
//				if (walkPattern != null) {
//                    Direction before = lastGoDirection;
//					for (int i = 0; i < 4; i++) {
//						Direction currentDir = walkPattern.next(before);
//						if (unvisitedConns.contains(current)) {
//							to = current.getConnByDirection(currentDir);
//							break;
//						}
//						before = currentDir;
//					}
//				} else {
//					if (patterns.size() == 1) {
//						walkPattern = patterns.get(0);
//						testAntGoByRestrictions(unvisitedConns);
//					} else if (patterns.size() < 6) {
//						Direction d0 = current.getRelativeLocation(unvisitedConns.get(0));
//						Direction d1 = current.getRelativeLocation(unvisitedConns.get(1));
//						List<Direction> l1 = new ArrayList<Direction>();
//						l1.add(lastGoDirection);
//						l1.add(d0);
//						l1.add(d1);
//						List<Direction> l2 = new ArrayList<Direction>();
//						l2.add(lastGoDirection);
//						l2.add(d1);
//						l2.add(d0);
//						CircularList<Direction> c1 = new CircularList<Direction>(l1);
//						CircularList<Direction> c2 = new CircularList<Direction>(l2);
//						boolean check0 = false;
//						boolean check1 = false;
//						for (CircularList<Direction> c : patterns) {
//							if (checkWalkPatternMatchWithRestriction(c, c1)) {
//								check0 = true;
//							}
//							if (checkWalkPatternMatchWithRestriction(c, c2)) {
//								check1 = true;
//							}
//						}
//						if (check0 == true && check1 == true) {
//							return to;
//						}
//						if (check0 == true && check1 == false) {
//							for (CircularList<Direction> c : patterns) {
//								if (!checkWalkPatternMatchWithRestriction(c, c1)) {
//									patternsMayRemove.add(c);
//								}
//							}
//						}
//						if (check0 == false && check1 == true) {
//							for (CircularList<Direction> c : patterns) {
//								if (!checkWalkPatternMatchWithRestriction(c, c2)) {
//									patternsMayRemove.add(c);
//								}
//							}
//						}
//						if (check0 == false && check1 == false) {
//							System.out.println("Never");
//							System.exit(0);
//						}
//						/*for (CircularList<Direction> c : patterns) {
//							if (!checkWalkPatternMatchWithRestriction(c, c1)) {
//								if (to == null) {
//									to = unvisitedConns.get(1);
//								} else if (!to.equals(unvisitedConns.get(1))) {
//									to = null;
//									break;
//								}
//							}
//							if (!checkWalkPatternMatchWithRestriction(c, c2)) {
//								//to = unvisitedConns.get(0);
//								if (to == null) {
//									to = unvisitedConns.get(0);
//								} else if (!to.equals(unvisitedConns.get(0))) {
//									to = null;
//									break;
//								}
//							}
//							if (!checkWalkPatternMatchWithRestriction(c, c1) && !checkWalkPatternMatchWithRestriction(c, c2)){
//								System.out.println("wrong");
//								System.exit(0);
//							}
//						}
//						if (to != null) {
//							if (to.equals(unvisitedConns.get(1))) {
//								for (CircularList<Direction> c : patterns) {
//									if (!checkWalkPatternMatchWithRestriction(c, c2)) {
//										patternsMayRemove.remove(c);
//									}
//								}
//							} else if (to.equals(unvisitedConns.get(0))) {
//								for (CircularList<Direction> c : patterns) {
//									if (!checkWalkPatternMatchWithRestriction(c, c1)) {
//										patternsMayRemove.remove(c);
//									}
//								}
//							}
//						}*/
//					}
//				}
//			}
			return to;
		}

		Node testAntGoNodeWithFewerDegree(List<Node> unvisitedConns, int[] tabu) {
			Node to = null;
			if (unvisitedConns != null && unvisitedConns.size() == 2) {
				if (lastGoDirection != null) {
					Node node0 = unvisitedConns.get(0);
					Node node1 = unvisitedConns.get(1);
					int count0 = node0.getUnvisitedConns(tabu);
					int count1 = node1.getUnvisitedConns(tabu);
					Direction d0 = current.getRelativeLocation(node0);
					Direction d1 = current.getRelativeLocation(node1);
					if (unvisitedConns.size() == 2) {
						List<Direction> r = new ArrayList<Direction>();
						r.add(lastGoDirection);
						if (count0 < count1) {
							to = node0;
							r.add(d0);
							r.add(d1);
						} else {
							to = node1;
							r.add(d1);
							r.add(d0);
						}
						if (r.size() == 3) {
							CircularList<Direction> cr = new CircularList<Direction>(r);
							Iterator<CircularList<Direction>> iterator = patterns.iterator();
							while (iterator.hasNext()) {
								CircularList<Direction> c = iterator.next();
								if (!checkWalkPatternMatchWithRestriction(c, cr)) {
									//iterator.remove();
									patternsMayRemove.add(c);
								}
							}
						}
						if (patternsMayRemove.size() == patterns.size()) {
							patternsMayRemove.clear();
							List<Direction> rr = new ArrayList<Direction>();
							rr.add(lastGoDirection);
							if (to.equals(node0)) {
								to = node1;
								rr.add(d1);
								rr.add(d0);
							} else {
								to = node0;
								rr.add(d0);
								rr.add(d1);
							}
							CircularList<Direction> crr = new CircularList<Direction>(rr);
							for (CircularList<Direction> c : patterns) {
								if (!checkWalkPatternMatchWithRestriction(c, crr)) {
									patternsMayRemove.add(c);
								}
							}
							if (patternsMayRemove.size() == patterns.size()) {
								System.exit(0);
							}
						}
					}

				}
			}
			return to;
		}

		Node randomANodetoGoNode(List<Node> unvisitedConns) {
			Node to = null;
			if (unvisitedConns != null && unvisitedConns.size() > 1) {
				List<Node> copyUnvisitedConns = new ArrayList<Node>(unvisitedConns);
				while (copyUnvisitedConns.size() > 0) {
					int random = (int)(Math.random() * copyUnvisitedConns.size());
					to = copyUnvisitedConns.get(random);
					if (lastGoDirection != null) {
						Node nodeStraight = current.getConnByDirection(lastGoDirection);
						if (copyUnvisitedConns.contains(nodeStraight)) {
							copyUnvisitedConns.remove(nodeStraight);
						}
						if (copyUnvisitedConns.size() == 2) {
							Node node0 = copyUnvisitedConns.get(0);
							Node node1 = copyUnvisitedConns.get(1);
							Direction d0 =
								current.getRelativeLocation(node0);
							Direction d1 =
								current.getRelativeLocation(node1);
							List<Direction> r = new ArrayList<Direction>();
							r.add(lastGoDirection);
							Direction decision = current.getRelativeLocation(to);
							r.add(decision);
							if (decision.equals(d0)) {
								r.add(d1);
							} else {
								r.add(d0);
							}
							CircularList<Direction> cr = new CircularList<Direction>(r);
							for (CircularList<Direction> c : patterns) {
								if (!checkWalkPatternMatchWithRestriction(c, cr)) {
									patternsMayRemove.add(c);
								}
							}
							if (patterns.size() != patternsMayRemove.size()) {
								copyUnvisitedConns.remove(to);
								break;
							}
							
							
						}
					} else {
						to = copyUnvisitedConns.get(random);
						break;
					}
				}
//				if (copyUnvisitedConns.size() == 3) {
//					copyUnvisitedConns.remove(0);
//				} else if (copyUnvisitedConns.size() == 4) {
//					copyUnvisitedConns.remove(0);
//					copyUnvisitedConns.remove(1);
//				}
//				int random = (int)(Math.random() * copyUnvisitedConns.size());
//				to = copyUnvisitedConns.get(random);
//				Direction decision = current.getRelativeLocation(to);
//				Node node0 = copyUnvisitedConns.get(0);
//				Node node1 = copyUnvisitedConns.get(1);
//				Direction d0 =
//					current.getRelativeLocation(node0);
//				Direction d1 =
//					current.getRelativeLocation(node1);
//
//				List<Direction> r = new ArrayList<Direction>();
//				r.add(lastGoDirection);
//				r.add(decision);
//				if (to.equals(node0)) {
//					r.add(d1);
//				} else {
//					r.add(d0);
//				}
//				CircularList<Direction> cr = new CircularList<Direction>(r);
//				Iterator<CircularList<Direction>> iterator = patterns.iterator();
//				while (iterator.hasNext()) {
//					CircularList<Direction> c = iterator.next();
//					if (!checkWalkPatternMatchWithRestriction(c, cr)) {
//						//iterator.remove();
//						patternsMayRemove.add(c);
//					}
//				}
			}
			return to;
		}

		public Node move(int[] tabu) {
			int count = current.getUnvisitedConns(tabu);
			Node to = null;
			switch (count) {
			case 0:
				state = AntState.DONE;
				break;
			case 1:
				to = current.getUnvisitedConnections(tabu).get(0);
				//state = AntState.MOVED;
				break;
			case 2:
			case 3:
			case 4:
				List<Node> unvisitedConns =
					current.getUnvisitedConnections(tabu);
				to = testAndGoOnlyPath(unvisitedConns);
				if (to == null) {
					if (lastGoDirection != null) {
						Node nodeStraight = current.getConnByDirection(lastGoDirection);
						if (unvisitedConns.contains(nodeStraight)) {
							unvisitedConns.remove(nodeStraight);
						}
						to = testAndGoOnlyPath(unvisitedConns);
					}
					if (to == null && patterns.size() == 1) {
						to = testAntGoByRestrictions(unvisitedConns);
					}
					if (to == null && patterns.size() > 1) {
						to = testAntGoNodeWithFewerDegree(unvisitedConns, tabu);
					}
					if (to == null && patterns.size() > 1) {
						to = randomANodetoGoNode(unvisitedConns);
					}
//					state = AntState.MOVED;
				}
				break;
			}
//			if (state.equals(AntState.MOVED)) {
//				lastGoDirection = current.getRelativeLocation(to);
//				tabu[to.id] = 1;
//				trip.add(to);
//				current = to;
//				to.ant = this;
//				if (trip.size() == 2) {
//					fistDoDirection = lastGoDirection;
//				}
//			}
			return to;
		}		
	}

	public static Map<Node, Integer> getComponents(List<Node> nodes) {
		Queue<Node> queue = new LinkedList<Node>();
		Map<Node, Integer> mapWithCount = new HashMap<Node, Integer>();
		while (nodes.size() > 0) {
			Node root = nodes.get(0);
			queue.add(root);
			int count = 0;
			while (!queue.isEmpty()) {
				Node node = queue.poll();
				nodes.remove(node);
				count++;
				if (node.left != null
						&& nodes.contains(node.left)
						&& !queue.contains(node.left)) {
					queue.add(node.left);
				}
				if (node.right != null
						&& nodes.contains(node.right)
						&& !queue.contains(node.right)) {
					queue.add(node.right);
				}
				if (node.up != null
						&& nodes.contains(node.up)
						&& !queue.contains(node.up)) {
					queue.add(node.up);
				}
				if (node.down != null
						&& nodes.contains(node.down)
						&& !queue.contains(node.down)) {
					queue.add(node.down);
				}
			}
			mapWithCount.put(root, count);
		}
		return mapWithCount;
	}
	
	public static List<Node> getHoleLeaves(Node root) {
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(root);
		Set<Node> set = new TreeSet<Node>();
		List<Node> leaves = new ArrayList<Node>();
        while (!queue.isEmpty()) {
			Node node = queue.poll();
			set.add(node);
			int count = 0;
			if (node.left != null && node.left.ant == null) {
				count++;
				if (!set.contains(node.left)
					&& !queue.contains(node.left)) {
					queue.add(node.left);
				}
			}
			if (node.right != null && node.right.ant == null) {
				count++;
				if (!set.contains(node.right)
					&& !queue.contains(node.right)) {
					queue.add(node.right);
				}
			}
			if (node.up != null && node.up.ant == null) {
				count++;
				if (!set.contains(node.up)
					&& !queue.contains(node.up)) {
					queue.add(node.up);
				}
			}
			if (node.down != null && node.down.ant == null) {
				count++;
				if (!set.contains(node.down)
					&& !queue.contains(node.down)) {
					queue.add(node.down);
				}
			}
			if (count == 0 || count == 1) {
				leaves.add(node);
			}
		}
        return leaves;
	}

	public static List<Node> getLeaves(Node root) {
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(root);
		Set<Node> set = new TreeSet<Node>();
		List<Node> leaves = new ArrayList<Node>();
		while (!queue.isEmpty()) {
			Node node = queue.poll();
			//System.out.println(node);
			set.add(node);
			int count = 0;
			if (node.left != null) {
				count++;
				if (!set.contains(node.left)
						&& !queue.contains(node.left)) {
					queue.add(node.left);
				}
			}
			if (node.right != null) {
				count++;
				if (!set.contains(node.right)
						&& !queue.contains(node.right)) {
					queue.add(node.right);
				}
			}
			if (node.up != null) {
				count++;
				if (!set.contains(node.up)
						&& !queue.contains(node.up)) {
					queue.add(node.up);
				}
			}
			if (node.down != null) {
				count++;
				if (!set.contains(node.down)
						&& !queue.contains(node.down)) {
					queue.add(node.down);
				}
			}
			if (count == 1) {
				leaves.add(node);
			}
		}
		return leaves;
	}
	
	public static List<Node> getHoleNodes(Node root) {
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(root);
		Set<Node> set = new TreeSet<Node>();
		List<Node> nodes = new ArrayList<Node>();
		while (!queue.isEmpty()) {
			Node node = queue.poll();
			//System.out.println(node);
			set.add(node);
			nodes.add(node);
			if (node.left != null) {
				if (!set.contains(node.left)
						&& !queue.contains(node.left)
						&& node.left.ant == null) {
					
					queue.add(node.left);
				}
			}
			if (node.right != null) {
				if (!set.contains(node.right)
						&& !queue.contains(node.right)
						&& node.right.ant == null) {
					queue.add(node.right);
				}
			}
			if (node.up != null) {
				if (!set.contains(node.up)
						&& !queue.contains(node.up)
						&& node.up.ant == null) {
					queue.add(node.up);
				}
			}
			if (node.down != null) {
				if (!set.contains(node.down)
						&& !queue.contains(node.down)
						&& node.down.ant == null) {
					queue.add(node.down);
				}
			}
		}
		return nodes;

	}

	public static List<Node> getNodes(Node root) {
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(root);
		Set<Node> set = new TreeSet<Node>();
		List<Node> nodes = new ArrayList<Node>();
		while (!queue.isEmpty()) {
			Node node = queue.poll();
			//System.out.println(node);
			set.add(node);
			nodes.add(node);
			if (node.left != null) {
				if (!set.contains(node.left)
						&& !queue.contains(node.left)) {
					queue.add(node.left);
				}
			}
			if (node.right != null) {
				if (!set.contains(node.right)
						&& !queue.contains(node.right)) {
					queue.add(node.right);
				}
			}
			if (node.up != null) {
				if (!set.contains(node.up)
						&& !queue.contains(node.up)) {
					queue.add(node.up);
				}
			}
			if (node.down != null) {
				if (!set.contains(node.down)
						&& !queue.contains(node.down)) {
					queue.add(node.down);
				}
			}
		}
		return nodes;
	}



    public static void main(String[] args) {
    	Munchers m = new Munchers(args[0]);
    	m.go();
	}
}
