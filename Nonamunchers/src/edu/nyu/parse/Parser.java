package edu.nyu.parse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.nyu.entity.Node;

public class Parser {

	public static List<Node> parse(String path) {
		List<Node> nodes = new ArrayList<Node>();
		FileReader fr = null;
		try {
			fr = new FileReader(path);
			String line;
			BufferedReader br = new BufferedReader(fr);
			boolean isNode = false;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				if (line.contains("xloc")) {
					isNode = true;
					continue;
				} else if (line.contains("nodeid1")) {
					isNode = false;
					continue;
				}
				String[] parts = line.split(",");
				if (isNode) {
					Node node = new Node(Integer.parseInt(parts[0]),
							             Integer.parseInt(parts[1]),
							             Integer.parseInt(parts[2]));
					nodes.add(node);
				} else {
					int nodeId1 = Integer.parseInt(parts[0]);
					int nodeId2 = Integer.parseInt(parts[1]);
					connect(nodes.get(nodeId1), nodes.get(nodeId2));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nodes;
	}

	public static void connect(Node node1, Node node2) {
		if (node1.x != node2.x) {
			if (node1.x < node2.x) {
				node1.right = node2;
				node2.left = node1;
			} else {
				node1.left = node2;
				node2.right = node1;
			}
		} else if (node1.y != node2.y) {
			if (node1.y < node2.y) {
				node1.up = node2;
				node2.down = node1;
			} else {
				node1.down = node2;
				node2.up = node1;
			}
		}
	}
	
	public static void main(String[] args) {
		Parser.parse(args[0]);

	}

}
