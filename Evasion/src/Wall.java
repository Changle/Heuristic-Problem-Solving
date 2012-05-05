
public class Wall {
	
	public Point start;
	public Point end;
	int no;
	
	Wall(int num, Point start, Point end)
	{
		this.no = num;
		this.start = start;
		this.end = end;
	}
	
	Wall(int num, int x, int y, int i, int j)
	{
		this.no = num;
		start = new Point(x,y);
		end = new Point(i,j);
	}
}
