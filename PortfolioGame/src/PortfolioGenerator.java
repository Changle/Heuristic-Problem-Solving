import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
 * Utility Class for generating gambles
 */
public class PortfolioGenerator {

	
	public static List<Gamble> generateGambles(int gambleNum, int classNum) throws Exception {
		double expectedReturn = 2.0*gambleNum;
		List<Double> rates = new ArrayList<Double>();
		List<Gamble> gamblesList = new ArrayList<Gamble>();
		double sum = 0;
		for (int i = 0; i < gambleNum; i++) {
			double r = getARandomNumInRange(1, 1.5);
			rates.add(r);
			sum += r;
		}
		for (int i = 0; i < gambleNum; i++) {
			double expect = rates.get(i) / sum * expectedReturn;
			Gamble gamble = generate(i, classNum, expect);
			gamblesList.add(gamble);
		}
		return gamblesList;
	}

	public static Gamble generate(int id,int classNum, double expectedReturn) throws Exception {
		Gamble gamble = new Gamble();
		int classId = getARandomInttInRange(0, classNum - 1);
		gamble.classId = classId;
		gamble.id = id;

		BigDecimal bigDecimal = null;
		while (true) {
			while (true) {
				gamble.medProb = getARandomNumInRange(0.4, 0.6);
				bigDecimal = new BigDecimal(gamble.medProb);
				gamble.medProb = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

				if (gamble.medProb != 1) {
					break;
				}
			}
			
			while (true) {
				gamble.lowProb = getARandomNumInRange(0, 1 - gamble.medProb);
				bigDecimal = new BigDecimal(gamble.lowProb);
				gamble.lowProb = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				if (gamble.lowProb != 0 && gamble.lowProb != 1 - gamble.medProb) {
					break;
				}
			}
			
			gamble.highProb = 1 - gamble.medProb - gamble.lowProb;
			bigDecimal = new BigDecimal(gamble.highProb);
			gamble.highProb = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			if (gamble.highProb != 0) {
				break;
			}
		}
		
		while (true) {
			double expectedReturnCopy = expectedReturn;
			double highrange = Math.min(10.0, (expectedReturnCopy) / gamble.highProb);
			gamble.high_return = getARandomNumInRange(1.0, highrange);


			expectedReturnCopy -= gamble.high_return * gamble.highProb;
			
			if(Math.min(gamble.high_return, expectedReturnCopy / gamble.medProb)<1)
				continue;
			
			gamble.medium_return = getARandomNumInRange(1, Math.min(gamble.high_return, expectedReturnCopy / gamble.medProb));

			expectedReturnCopy -= gamble.medium_return * gamble.medProb;

			gamble.low_return = expectedReturnCopy / gamble.lowProb;

			bigDecimal = new BigDecimal(gamble.high_return);
			gamble.high_return = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			bigDecimal = new BigDecimal(gamble.medium_return);
			gamble.medium_return = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			bigDecimal = new BigDecimal(gamble.low_return);
			gamble.low_return = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			
			if (gamble.high_return > gamble.medium_return
					&& gamble.medium_return > gamble.low_return
					&& gamble.high_return > 1
					&& gamble.low_return < 1
					&& gamble.low_return > 0.09) {
				break;
			}
			
			
			
		}
		return gamble;
	}
	
	public static double round2Digit(double num)
	{
		int tmp=(int)Math.round(num*100);
		return (double)tmp/100;
	}

	public static double getARandomNumInRange(double min, double max) throws Exception {
		if (max<min) {
			throw new Exception("the high bound should be no less than the low bound!");
		}
		return min + (Math.random() * ((max - min)));
	}
	
	public static int getARandomInttInRange(int min, int max) throws Exception {
		if (max<min) {
			throw new Exception("the high bound should be no less than the low bound!");
		}
		return min + (int)(Math.random() * ((max - min)+1));
	}

	public static double getARandomNumLargerThanAPositive(double n) throws Exception {
		if (n < 0) {
			throw new Exception("N should be no smaller than 0!");
		}
		double highBound = getARandomNumInRange(n, Double.MAX_VALUE);
		return getARandomNumInRange(n, highBound);
	}
	
	
	//public static 

	public static String outputAsString(List<Gamble>list, int links[][]) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Gamble\n gi class hi:hiprob med:medprob low:lowprob\n");
		for(Gamble gamble : list)
		{
			sb.append(gamble.outputToFile()+"\n");
		}
		sb.append("\nLink\n  gi  gj\n");
		for (int i = 0; i < links.length; i++) {
			for (int j = i + 1; j < links.length; j++) {
				if (links[i][j] == 1) {
					sb.append("  "+i + " , " + j + "\n");
				}					
			}
		}
		
		return sb.toString();

	}

	public static int[][] genLinks(int gambleNum) {
		int [][] linkedMatrix = new int[gambleNum][gambleNum];
		for (int i = 0; i < gambleNum; i++) {
			for (int j = i + 1; j < gambleNum; j++) {
				double dice = Math.random();
				if (dice < 0.8) {
					linkedMatrix[i][j] = 0;
					linkedMatrix[j][i] = 0;
				} else {
					linkedMatrix[i][j] = 1;
					linkedMatrix[j][i] = 1;
				}
			}
		}
		return linkedMatrix;
	}


	public static void main(String[] args) throws Exception {
		
		int gambelNum = 10;
		int classNum = 5;
		
		List<Gamble> list =generateGambles(gambelNum, classNum);
	    int links[][]=genLinks(gambelNum);
	    String output = outputAsString(list,links);
	    System.out.println(output);
	}
}
