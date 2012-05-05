import java.util.ArrayList;


/*
 * Player Class
 */
public class Player {
	
	private int id;
	private double wealth;
	private int score;
	private boolean cheated;
	private ArrayList<Double> returns;
	private double returnSum;
	private double variance;
	private double previous;
	private double sharpeRatio;
	
	Player(int id){
		this.id = id;
		this.wealth = 10;
		this.previous = 10;
		this.cheated = false;
		returns = new ArrayList<Double>();
		variance = 0.0;
		returnSum = 0.0;
		sharpeRatio = 0.0;
	}
	
	public void caculateSharpeRatio()
	{
		double sum = 0.0;
		for(Double ret:returns)
		{
			sum += ret;
		}
		returnSum = sum;
		if(returnSum == 0)
		{
			this.sharpeRatio = 0;
			return;
		}
		
		double mean = sum/returns.size();
		double var = 0.0;
		for(Double ret:returns)
		{
			var += Math.pow(ret-mean, 2);
		}
		var = Math.sqrt(var/returns.size());
		this.variance = var;
		this.sharpeRatio = returnSum / var;
	}


	public double getWealth() {
		return wealth;
	}

	public void setWealth(double wealth) {
		this.wealth = wealth;
	}
	
	public void increaseScoreByOne()
	{
		this.score++;
	}
	
	public double getPNL()
	{
		if(this.returns.size() == 0)
		{
			return 0;
		}else
		{
			return returns.get(returns.size()-1);
		}
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isCheated() {
		return cheated;
	}

	public void setCheated(boolean cheated) {
		this.cheated = cheated;
	}

	public ArrayList<Double> getReturns() {
		return returns;
	}

	public void setReturns(ArrayList<Double> returns) {
		this.returns = returns;
	}

	public double getVariance() {
		return variance;
	}

	public void setVariance(double variance) {
		this.variance = variance;
	}

	public double getReturnSum() {
		return returnSum;
	}

	public void setReturnSum(double returnSum) {
		this.returnSum = returnSum;
	}

	public double getSharpeRatio() {
		return sharpeRatio;
	}

	public void setSharpeRatio(double sharpeRatio) {
		this.sharpeRatio = sharpeRatio;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getPrevious() {
		return previous;
	}

	public void setPrevious(double previous) {
		this.previous = previous;
	}

}
