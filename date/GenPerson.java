import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GenPerson {

	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Please input N");
			System.exit(1);
		}

		int attrNum = Integer.parseInt(args[0]);
		List<Float> attrList = new ArrayList<Float>();
		
		int posLeft = 100;
		int negLeft = -100;

		DecimalFormat df = new DecimalFormat("###.00");

		for (int i = 0; i < attrNum-2; i++) {
			
			int attr = (int)(Math.random()*(posLeft - negLeft))+negLeft;
			double posOrNeg = Math.random();
            int tmp;

            if(posOrNeg >= 0.5)
            {
                float avg = (float)posLeft/(attrNum-i)*2.0f;
                tmp = (int)(Math.random()*avg);
                posLeft -= tmp;
            }else
            {
                float avg = (float)negLeft/(attrNum-i)*2.0f;
                tmp = (int)(Math.random()*avg);
                negLeft -= tmp;
            }

			//String val = df.format((float)attr/100);
			attrList.add((float)(tmp)/100.0f);
		}
		
		attrList.add((float)posLeft/100.0f);
		attrList.add((float)negLeft/100.0f);
		
		System.out.println("weights:");
		for(int i=0;i<attrNum;i++)
		{
			System.out.println(attrList.get(i));
		}

	}

}
