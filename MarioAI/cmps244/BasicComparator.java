package cmps244;

import java.util.Comparator;
import java.util.Hashtable;

public class BasicComparator implements Comparator<BasicNode> {
	public Hashtable<BasicNode, Double> score;
	public BasicComparator(Hashtable<BasicNode, Double> fScore) {
		score = fScore;
	}

	public int compare(BasicNode arg0, BasicNode arg1) {
		return (int) (score.get(arg0)-score.get(arg1));
	}

}
