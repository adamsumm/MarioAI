package cmps244;

public class BasicNode {
	public int xx;
	public int yy;
	public int score;
	public BasicNode(int x, int y){
		xx = x;
		yy = y;
	}
	 @Override public String toString() {
		 return "" + xx + "," + yy;
	 }
}
