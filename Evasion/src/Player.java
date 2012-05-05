import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player {

	public String host;
	public int port;
	public Socket socket = null;
	public PrintWriter out = null;
	public BufferedReader in = null;
	public boolean isHunter = false;
	public Point hunterPos;
	public Point hunterDir;
	public Point preyPos;
	public int interval;
	public int maxWall;
	public int count;
	public List<Wall> walls = new ArrayList<Wall>();
	public int wallGrid[][] = new int[500][500];
	public boolean hor = true;

	Player() {
		host = "localhost";
		port = 0;
		count = 0;
	}

	Player(String host, int port, int M, int N) {
		this.host = host;
		this.port = port;
		this.maxWall = M;
		this.interval = N;
		this.count = 0;
		hunterPos = new Point(0, 0);
		hunterDir = new Point(1, 1);
		preyPos = new Point(330, 200);

	}

	public void readState(String state) {

		List<String> states = new LinkedList<String>(Arrays.asList((state
				.split("\n"))));

		String oneLine = states.get(0);
		if (oneLine.contains("Hunter")) {
			isHunter = true;
		} else {
			isHunter = false;
		}

		oneLine = states.get(1);
		String[] strs = oneLine.split(":");
		// System.out.println(strs[1]);
		Scanner scanner = new Scanner(strs[1]);
		hunterPos.x = scanner.nextInt();
		hunterPos.y = scanner.nextInt();
		hunterDir.x = scanner.nextInt();
		hunterDir.y = scanner.nextInt();

		oneLine = states.get(2);
		strs = oneLine.split(":");
		scanner = new Scanner(strs[1]);
		preyPos.x = scanner.nextInt();
		preyPos.y = scanner.nextInt();

		oneLine = states.get(3);
		strs = oneLine.split(": ");
		String wallstr = strs[1];

		walls.clear();

		for (int i = 0; i < 500; i++)
			for (int j = 0; j < 500; j++)
				wallGrid[i][j] = 0;

		if (!wallstr.equals("[]")) {

			wallstr = wallstr.substring(1, wallstr.length() - 2);
			strs = wallstr.split("\\), ");
			String RE = "(\\d+) \\((\\d+), (\\d+), (\\d+), (\\d+)";
			Pattern pattern = Pattern.compile(RE);
			for (String tmp : strs) {
				Matcher matcher = pattern.matcher(tmp);
				if (matcher.find()) {
					int no = Integer.parseInt(matcher.group(1));
					int x = Integer.parseInt(matcher.group(2));
					int y = Integer.parseInt(matcher.group(3));
					int i = Integer.parseInt(matcher.group(4));
					int j = Integer.parseInt(matcher.group(5));
					Wall newWall = new Wall(no, x, y, i, j);
					walls.add(newWall);
					for (int k = x; k <= i; k++)
						for (int p = y; p <= j; p++) {
							if (y == j)
								wallGrid[k][p] = 1;
							if (x == i)
								wallGrid[k][p] = -1;
						}
				}
			}
		}

	}

	public String buildHorWall() {
		int minX = 0;
		int maxX = 499;
		for (int i = hunterPos.x; i >= 0; i--) {
			if (wallGrid[i][hunterPos.y] == 0)
				minX = i;
			else
				break;
		}
		for (int i = hunterPos.x; i < 500; i++) {
			if (wallGrid[i][hunterPos.y] == 0)
				maxX = i;
			else
				break;
		}
		int largestNo;
		if (walls.size() == 0)
			largestNo = 0;
		else {
			largestNo = walls.get(walls.size() - 1).no + 1;
		}
		Wall newWall = new Wall(largestNo, minX, hunterPos.y, maxX, hunterPos.y);
		walls.add(newWall);

		return "0 " + minX + " " + maxX;
	}

	public String buildVerWall() {
		int minY = 0;
		int maxY = 499;
		for (int i = hunterPos.y; i >= 0; i--) {
			if (wallGrid[hunterPos.x][i] == 0)
				minY = i;
			else
				break;
		}
		for (int i = hunterPos.y; i < 500; i++) {
			if (wallGrid[hunterPos.x][i] == 0)
				maxY = i;
			else
				break;
		}
		hor = true;
		int largestNo;
		if (walls.size() == 0)
			largestNo = 0;
		else {
			largestNo = walls.get(walls.size() - 1).no + 1;
		}
		Wall newWall = new Wall(largestNo, hunterPos.x, minY, hunterPos.x, maxY);
		walls.add(newWall);
		return "1 " + minY + " " + maxY;
	}

	public String buildWall() {

		if (hunterDir.x == 1 && hunterDir.y == 1) {

			if (hunterPos.x < preyPos.x && hunterPos.y < preyPos.y) {

				int gapX = preyPos.x;
				int gapY = preyPos.y;
				for (int i = preyPos.x; i >= 0; i--) {
					if (wallGrid[i][preyPos.y] == -1) {
						gapX = preyPos.x - i;
						break;
					}
				}

				for (int i = preyPos.y; i >= 0; i--) {
					if (wallGrid[preyPos.x][i] == 1) {
						gapY = preyPos.y - i;
						break;
					}
				}

				boolean isHor;
				if (gapX < gapY) {
					isHor = true;
				} else {
					isHor = false;
				}

				if (isHor) {
					return buildHorWall();
				} else {
					return buildVerWall();
				}

			} else if (hunterPos.x < preyPos.x && hunterPos.y > preyPos.y) {
				return buildVerWall();
			} else if (hunterPos.x > preyPos.x && hunterPos.y < preyPos.y) {
				return buildHorWall();
			}
			return "";

		} else if (hunterDir.x == 1 && hunterDir.y == -1) {

			if (hunterPos.x < preyPos.x && hunterPos.y > preyPos.y) {

				int gapX = preyPos.x;
				int gapY = 499 - preyPos.y;
				for (int i = preyPos.x; i >= 0; i--) {
					if (wallGrid[i][preyPos.y] == -1) {
						gapX = preyPos.x - i;
						break;
					}
				}

				for (int i = preyPos.y; i < 500; i++) {

					if (wallGrid[preyPos.x][i] == 1) {
						gapY = i - preyPos.y;
						break;
					}
				}

				boolean isHor;
				if (gapX < gapY) {
					isHor = true;
				} else {
					isHor = false;
				}

				if (isHor) {
					return buildHorWall();
				} else {
					return buildVerWall();
				}

			} else if (hunterPos.x < preyPos.x && hunterPos.y < preyPos.y) {
				return buildVerWall();
			} else if (hunterPos.x > preyPos.x && hunterPos.y > preyPos.y) {
				return buildHorWall();
			}
			return "";
		} else if (hunterDir.x == -1 && hunterDir.y == 1) {

			if (hunterPos.x > preyPos.x && hunterPos.y < preyPos.y) {

				int gapX = 499 - preyPos.x;
				int gapY = preyPos.y;
				for (int i = preyPos.x; i < 500; i++) {
					if (wallGrid[i][preyPos.y] == -1) {
						gapX = i - preyPos.x;
						break;
					}
				}

				for (int i = preyPos.y; i >= 0; i--) {

					if (wallGrid[preyPos.x][i] == 1) {
						gapY = preyPos.y - i;
						break;
					}
				}

				boolean isHor;
				if (gapX < gapY) {
					isHor = true;
				} else {
					isHor = false;
				}

				if (isHor) {
					return buildHorWall();
				} else {
					return buildVerWall();
				}

			} else if (hunterPos.x > preyPos.x && hunterPos.y > preyPos.y) {
				return buildVerWall();
			} else if (hunterPos.x < preyPos.x && hunterPos.y < preyPos.y) {
				return buildHorWall();
			}
			return "";

		} else if (hunterDir.x == -1 && hunterDir.y == -1) {

			if (hunterPos.x > preyPos.x && hunterPos.y > preyPos.y) {

				int gapX = 499 - preyPos.x;
				int gapY = 499 - preyPos.y;
				for (int i = preyPos.x; i < 500; i++) {
					if (wallGrid[i][preyPos.y] == -1) {
						gapX = i - preyPos.x;
						break;
					}
				}

				for (int i = preyPos.y; i < 500; i++) {

					if (wallGrid[preyPos.x][i] == 1) {
						gapY = i - preyPos.y;
						break;
					}
				}

				boolean isHor;
				if (gapX < gapY) {
					isHor = true;
				} else {
					isHor = false;
				}

				if (isHor) {
					return buildHorWall();
				} else {
					return buildVerWall();
				}

			} else if (hunterPos.x > preyPos.x && hunterPos.y < preyPos.y) {
				return buildVerWall();
			} else if (hunterPos.x < preyPos.x && hunterPos.y > preyPos.y) {
				return buildHorWall();
			}
			return "";

		}

		return "";

	}

	public int removeWall() {

		int removeId = 0;

		
		int countLeft = 0;
		int countRight = 0;
		int countUp = 0;
		int countDown = 0;
		
		int minX = 0;
		int maxX = 499;
		int minY = 0;
		int maxY = 499;
		
		for (int i = hunterPos.x; i >= 0; i--) {
			if (wallGrid[i][hunterPos.y] == -1) {
				minX = i;
				countLeft++;
			}
		}
		
		for (int i = hunterPos.y; i >= 0; i--) {
			if (wallGrid[hunterPos.x][i] == 1) {
				minY = i;
				countUp++;
			}
		}
		
		for (int i = hunterPos.x; i < 500; i++) {
			if (wallGrid[i][hunterPos.y] == -1) {
				maxX = i;
				countRight++;
			}
		}
		
		for (int i = hunterPos.y; i < 500; i++) {
			if (wallGrid[hunterPos.x][i] == 1) {
				maxY = i;
				countDown++;
			}
		}
		
		if(countLeft >= countRight && countLeft >= countUp && countLeft >= countDown)
		{
			removeId = getWallNo(minX, hunterPos.y);
		}
		
		if(countRight >= countLeft && countRight >= countUp && countRight >= countDown)
		{
			removeId = getWallNo(maxX, hunterPos.y);
		}
		
		if(countUp >= countLeft && countUp >= countRight && countUp >= countDown)
		{
			removeId = getWallNo(hunterPos.x, minY);
		}
		
		if(countDown >= countLeft && countDown >= countRight && countDown >= countUp)
		{
			removeId = getWallNo(hunterPos.x, maxY);
		}
		
		/*
		
		if (hunterDir.x == 1 && hunterDir.y == 1) {
			int minX = 500;
			int minY = 500;
			int countX = 0;
			int countY = 0;
			for (int i = hunterPos.x; i >= 0; i--) {
				if (wallGrid[i][hunterPos.y] == -1) {
					minX = i;
					countX++;
				}
			}

			for (int i = hunterPos.y; i >= 0; i--) {
				if (wallGrid[hunterPos.x][i] == 1) {
					minY = i;
					countY++;
				}
			}

			if (countX > countY) {
				removeId = getWallNo(minX, hunterPos.y);
			} else{
				removeId = getWallNo(hunterPos.x, minY);
			}
			
		} else if (hunterDir.x == 1 && hunterDir.y == -1) {
			int minX = 500;
			int minY = 0;
			int countX = 0;
			int countY = 0;
			for (int i = hunterPos.x; i >= 0; i--) {
				if (wallGrid[i][hunterPos.y] == -1) {
					minX = i;
					countX++;
				}
			}

			for (int i = hunterPos.y; i < 500; i++) {
				if (wallGrid[hunterPos.x][i] == 1) {
					minY = i;
					countY++;
				}
			}

			if (countX > countY) {
				removeId = getWallNo(minX, hunterPos.y);
			} else{
				removeId = getWallNo(hunterPos.x, minY);
			}

		} else if (hunterDir.x == -1 && hunterDir.y == 1) {
			int minX = 0;
			int minY = 0;
			int countX = 0;
			int countY = 0;

			for (int i = hunterPos.x; i < 500; i++) {
				if (wallGrid[i][hunterPos.y] == -1) {
					minX = i;
					countX++;
				}
			}

			for (int i = hunterPos.y; i >= 0; i--) {
				if (wallGrid[hunterPos.x][i] == 1) {
					minY = i;
					countY++;
				}
			}

			if (countX > countY) {
				removeId = getWallNo(minX, hunterPos.y);
			} else {
				removeId = getWallNo(hunterPos.x, minY);
			}

		} else if (hunterDir.x == -1 && hunterDir.y == -1) {
			
			int minX = 0;
			int minY = 0;
			int countX = 0;
			int countY = 0;
			for (int i = hunterPos.x; i < 500; i++) {
				if (wallGrid[i][hunterPos.y] == -1) {
					minX = i;
					countX++;
				}
			}

			for (int i = hunterPos.y; i < 500; i++) {
				if (wallGrid[hunterPos.x][i] == 1) {
					minY = i;
					countY++;
				}
			}

			if (countX > countY) {
				removeId = getWallNo(minX, hunterPos.y);
			} else{
				removeId = getWallNo(hunterPos.x, minY);
			}
		}
		
		*/
		
		return removeId;

	}

	public int getWallNo(int x, int y) {
		int no = 0;
		for (Wall wall : walls) {
			if (wall.start.y == y && wall.end.y == y && wall.start.x <= x
					&& x <= wall.end.x) {
				no = wall.no;
				break;
			}
			if (wall.start.x == x && wall.end.x == x && wall.start.y <= y
					&& y <= wall.end.y) {
				no = wall.no;
				break;
			}
		}
		return no;
	}

	public String play() {

		if (isHunter)
			return playLikeAHunter();
		else
			return playLikeAPrey();
	}

	public String playLikeAHunter() {

		count++;
		String buildDirStep1 = "Build:";

		String removeStr = "Remove:[]";

		if (count >= interval) {
			String wallCor = buildWall();
			if (!wallCor.equals("")) {
				count = 0;
				
				String []elems = wallCor.split(" "); 
				int dir = Integer.parseInt(elems[0]);
				int begin = Integer.parseInt(elems[1]);
				int end = Integer.parseInt(elems[2]);
				if(dir == 0)
				{
					for(int i=begin;i<=end;i++)
						wallGrid[i][hunterPos.y] = 1;
				}else
				{
					for(int i=begin;i<=end;i++)
						wallGrid[hunterPos.x][i] = -1;
				}
				
			} else {
				count++;
			}

			buildDirStep1 += wallCor;
		}

		count++;

		if (walls.size() == maxWall) {
			int no;
			no = removeWall();
			removeStr = "Remove:[" + no + "]";
		}

		System.out.println("Remove:[] " + buildDirStep1 + " " + removeStr
				+ " Build:");

		return "Remove:[] " + buildDirStep1 + " " + removeStr + " Build:";
	}

	public boolean isValid(int x, int y) {
		if (x >= 0 && x <= 499 && y >= 0 && y <= 499) {
			return true;
		} else {
			return false;
		}
	}

	public String playLikeAPrey() {

		if (hunterDir.x == 1 && hunterDir.y == 1) {
			if (isValid(preyPos.x - 1, preyPos.y - 1)
					&& wallGrid[preyPos.x - 1][preyPos.y - 1] == 0
					&& Math.abs(Math.abs(hunterPos.x - preyPos.x)
							- Math.abs(hunterPos.y - preyPos.y)) > 4) {
				return "-1 -1";
			} else if (isValid(preyPos.x - 1, preyPos.y)
					&& wallGrid[preyPos.x - 1][preyPos.y] == 0) {
				return "-1 0";
			} else if (isValid(preyPos.x, preyPos.y - 1)
					&& wallGrid[preyPos.x][preyPos.y - 1] == 0) {
				return "0 -1";
			} else
				return "0 0";

		} else if (hunterDir.x == 1 && hunterDir.y == -1) {

			if (isValid(preyPos.x - 1, preyPos.y + 1)
					&& wallGrid[preyPos.x - 1][preyPos.y + 1] == 0
					&& Math.abs(Math.abs(hunterPos.x - preyPos.x)
							- Math.abs(hunterPos.y - preyPos.y)) > 4) {
				return "-1 1";
			} else if (isValid(preyPos.x - 1, preyPos.y)
					&& wallGrid[preyPos.x - 1][preyPos.y] == 0) {
				return "-1 0";
			} else if (isValid(preyPos.x, preyPos.y + 1)
					&& wallGrid[preyPos.x][preyPos.y + 1] == 0) {
				return "0 1";
			} else
				return "0 0";

		} else if (hunterDir.x == -1 && hunterDir.y == 1) {

			if (isValid(preyPos.x + 1, preyPos.y - 1)
					&& wallGrid[preyPos.x + 1][preyPos.y - 1] == 0
					&& Math.abs(Math.abs(hunterPos.x - preyPos.x)
							- Math.abs(hunterPos.y - preyPos.y)) > 4) {
				return "1 -1";
			} else if (isValid(preyPos.x + 1, preyPos.y)
					&& wallGrid[preyPos.x + 1][preyPos.y] == 0) {
				return "1 0";
			} else if (isValid(preyPos.x, preyPos.y - 1)
					&& wallGrid[preyPos.x][preyPos.y - 1] == 0) {
				return "0 -1";
			} else
				return "0 0";

		} else if (hunterDir.x == -1 && hunterDir.y == -1) {
			if (isValid(preyPos.x + 1, preyPos.y + 1)
					&& wallGrid[preyPos.x + 1][preyPos.y + 1] == 0
					&& Math.abs(Math.abs(hunterPos.x - preyPos.x)
							- Math.abs(hunterPos.y - preyPos.y)) > 4) {
				return "1 1";
			} else if (isValid(preyPos.x + 1, preyPos.y)
					&& wallGrid[preyPos.x + 1][preyPos.y] == 0) {
				return "1 0";
			} else if (isValid(preyPos.x, preyPos.y + 1)
					&& wallGrid[preyPos.x][preyPos.y + 1] == 0) {
				return "0 1";
			} else
				return "0 0";

		}

		return "0 0";

	}

	public void connect() {

		try {

			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException ev) {
			System.err.println(ev.getMessage());
		}
		String command;
		StringBuffer state = new StringBuffer();
		try {
			while ((command = in.readLine()) != null) {

				if (command.equals("END")) {
					break;
				}

				state.append(command + "\n");
				if (command.startsWith("Wall")) {
					System.out.println(state.toString());
					readState(state.toString());
					out.println(play());
					state.delete(0, state.length());
				}

			}
		} catch (IOException io) {
			System.err.println(io.getMessage());
		}
		out.close();
		try {
			in.close();
			socket.close();
		} catch (IOException io) {
			System.err.println(io.getMessage());
		}

	}

	public static void main(String args[]) {

		String host = "localhost";
		int port = 8888;
		int M = 0;
		int N = 0;

		if (args.length == 4) {
			try {
				host = args[0];
				port = Integer.parseInt(args[1]);
				M = Integer.parseInt(args[2]);
				N = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {

				System.exit(1);
			}
		} else {
			System.err.println("host port M N");
			System.exit(1);
		}

		Player winner = new Player(host, port, M, N);
		winner.connect();

	}

}
