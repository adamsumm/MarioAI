package cmps244Lib;
import java.util.List;

public class WeightPair<X> {
	public int count;
	public X obj;
	public WeightPair(int cc, X o){
		count = cc;
		obj = o;
	}
	public static <X> X GetWeightPair(List<WeightPair<X> > list,int val){
		int runningSum = 0;
		for (WeightPair<X> pair : list){
			runningSum += pair.count;
			if (val <= runningSum){
				return pair.obj;
			}
		}
		return null;
	}
}
