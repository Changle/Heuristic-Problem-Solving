package edu.nyu.entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.nyu.muhchers.Munchers.Ant;
import edu.nyu.muhchers.Munchers.Direction;

public class Node implements Comparable<Node> {

	public int id;

	public int x;

	public int y;

	public Node left;

	public Node right;

	public Node up;

	public Node down;

	public Ant ant;

	public int timestamp;

	// if two ants both can take this node, they will wait and this node will
	// save the two ants.
	public List<Ant> antLookingList = new LinkedList<Ant>();

	@Override
	public String toString() {
		return "Node [id=" + id
		       + ", left=" + (left == null ? "" : left.id)
			  + ", right=" + (right == null ? "" : right.id)
			     + ", up=" + (up == null ? "" : up.id)
			   + ", down=" + (down == null ? "" : down.id)
			      + ", x=" + x
			      + ", y=" + y + "]";
	}

	public Node(int id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
	public Node(Node node) {
		this.id = node.id;
		this.x = node.x;
		this.y = node.y;
		this.left = node.left;
		this.right = node.right;
		this.up = node.up;
		this.down = node.down;
		this.ant = node.ant;
	}

	public boolean removeConn(Node node) {
		if (node == null) {
			return false;
		}
		
		if (node.equals(left)) {
			left = null;
			return true;
		} else if (node.equals(right)) {
			right = null;
			return true;
		} else if (node.equals(up)) {
			up = null;
			return true;
		} else if (node.equals(down)) {
			down = null;
			return true;
		}
		return false;
	}
 
	public int getUnvisitedConns(int[] tabu) {
		int count = 0;
		if (left != null && tabu[left.id] == 0) {
			count++;
		}
		if (right != null && tabu[right.id] == 0) {
			count++;
		}
		if (up != null && tabu[up.id] == 0) {
			count++;
		}
		if (down != null && tabu[down.id] == 0) {
			count++;
		}
		return count;
	}

	public List<Node> getUnvisitedConnections(int[] tabu) {
		List<Node> nodes = new ArrayList<Node>();
		if (left != null && tabu[left.id] == 0) {
			nodes.add(left);
		}
		if (right != null && tabu[right.id] == 0) {
			nodes.add(right);
		}
		if (up != null && tabu[up.id] == 0) {
			nodes.add(up);
		}
		if (down != null && tabu[down.id] == 0) {
			nodes.add(down);
		}
		return nodes;
	}

	public List<Node> getConnections() {
		List<Node> nodes = new ArrayList<Node>();
		if (left != null) {
			nodes.add(left);
		}
		if (right != null) {
			nodes.add(right);
		}
		if (up != null) {
			nodes.add(up);
		}
		if (down != null) {
			nodes.add(down);
		}
		return nodes;
	}



	public Direction getRelativeLocation(Node node) {
		if (node != null) {
			if (node.equals(left)) {
				return Direction.LEFT;
			}
	    	if (node.equals(right)) {
				return Direction.RIGHT;
			}
	    	if (node.equals(up)) {
	    		return Direction.UP;
	    	}
	    	if (node.equals(down)) {
				return Direction.DOWN;
			}
		}
      	return null;
    }

	public Node getConnByDirection(Direction d) {
		switch (d) {
		case LEFT:
			return left;
		case RIGHT:
			return right;
		case UP:
			return up;
		case DOWN:
			return down;
		}
		return null;
	}

	@Override
	public int compareTo(Node o) {
		if (this.x < o.x) {
			return -1;
		} else if (this.x == o.x) {
			if (this.y < o.y) {
				return -1;
			} else if (this.y == o.y) {
				return 0;
			}
		}
		return 1;
	}



}
