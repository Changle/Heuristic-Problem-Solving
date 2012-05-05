import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;


/*
 * Panel Class for painting histogram
 */
class MyPanel extends JPanel {

	private boolean flag = true;
	private final int SIZE = 20;
	private final int CAKY_WIDTH = 400;
	private final int STEP = 10;
	private String xTitle;
	private String yTitle;
	private static List<String> elem = new ArrayList<String>();
	private static List<Double> value = new ArrayList<Double>();
	private static DecimalFormat df = new DecimalFormat("####.##");

	public MyPanel() {
		this.xTitle = "X";
		this.yTitle = "Y";
	}

	public MyPanel(String x, String y) {
		this.xTitle = x;
		yTitle = y;
	}

	public void init()
	{
		elem.clear();
		value.clear();
	}
	public  void insert(String aElem, double aValue) {
		elem.add(aElem);
		value.add(aValue);
	}

	public  void update(String aElem, double aValue) {
		int index = elem.indexOf(aElem);
		value.set(index, aValue);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		drawHistogram(g);
	}

	public void drawHistogram(Graphics g) {
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("SANS_SERIF", Font.PLAIN, 15));
		g.drawString("Gambles Return", SIZE + 200, 30);
		g.setColor(Color.GREEN);
		// vertical line
		g.drawLine(SIZE, this.getHeight() - SIZE, SIZE, SIZE);
		// horizontal line
		g.drawLine(SIZE, this.getHeight() - SIZE, this.getWidth() - SIZE,
				this.getHeight() - SIZE);
		// arrow
		g.setColor(Color.RED);
		int[] x1 = { SIZE - 6, SIZE, SIZE + 6 };
		int[] y1 = { SIZE + 8, SIZE, SIZE + 8 };
		g.drawPolyline(x1, y1, 3);
		int[] x2 = { this.getWidth() - SIZE - 8, this.getWidth() - SIZE,
				this.getWidth() - SIZE - 8 };
		int[] y2 = { this.getHeight() - SIZE - 6, this.getHeight() - SIZE,
				this.getHeight() - SIZE + 6 };
		g.drawPolyline(x2, y2, 3);
		
		if(value.size() == 0)
			return;
		// title
		
		/*
		g.drawString(this.yTitle, SIZE - 20, SIZE - 6);
		g.drawString(this.xTitle, this.getWidth() - SIZE - 20, this.getHeight()
				- SIZE + 20);
		*/

		// compute width
		int wigth = (int) ((this.getWidth() - 3 * SIZE) / (value.size() * 2));

		double max = 0;
		for (Double elem : value) {
			if (max < elem) {
				max = elem;
			}
		}

		double num = (double) (this.getHeight() - 2 * (SIZE + 10))
				/ (double) (1.2 * (max + 0.001));

		for (int i = 0; i < elem.size(); i++) {
			int height = (int) (value.get(i) * num);
			g.setColor(new java.awt.Color(Digit.getDigit(255), Digit
					.getDigit(255), Digit.getDigit(255)));
			// fill color
			g.fillRect(wigth * (i * 2 + 1) + SIZE, this.getHeight() - SIZE
					- height, wigth, height);
			g.setColor(Color.RED);

			g.drawString(df.format(value.get(i)), wigth * (i * 2 + 1) + SIZE,
					this.getHeight() - SIZE - 10 - height);

			g.drawString(elem.get(i), wigth * (i * 2 + 1) + SIZE,
					this.getHeight() - SIZE + 15);

		}
	}

	public void setHistogramTitle(String y, String x) {
		xTitle = x;
		yTitle = y;
	}

}

class Digit {
	public Digit() {
	}

	public static int getDigit(int digit) {
		java.util.Random ran = new Random();
		return (int) (ran.nextDouble() * digit);
	}

}
