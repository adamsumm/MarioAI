package cmps244;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import org.json.JSONObject;

import cmps244Lib.WeightPair;


public class LevelNode {
	static Random rand = new Random();
	public ArrayList<WeightPair<String> >  potentialRight;
	public static Hashtable<String,LevelNode> levelPieces = new Hashtable<String,LevelNode>();
	public String levelChunk;
	public int position;
	public static double K = Math.sqrt(2);
	public static int levelSize = 320;
	public static int numIterations = 1000;
	static double epsilon = 1e-6;
	public LevelNode parent;
	ArrayList<LevelNode> children;
	public double nVisits, totValue;
	protected int size = -1;
	public LevelNode(String[] allChunks){
		levelChunk = "";
		potentialRight = new ArrayList<WeightPair<String> >();
		for (String chunk : allChunks){
			potentialRight.add(new WeightPair<String>(1,chunk));
		}
		children = new ArrayList<LevelNode>();

	}
	public LevelNode(String chunk,JSONObject json){
		levelChunk = chunk;
		JSONObject directions = json.getJSONObject(chunk);
		JSONObject rightJSON = directions.getJSONObject("right");
		//	JSONObject leftJSON = directions.getJSONObject("left");

		String[] rightNodes = JSONObject.getNames(rightJSON);

		potentialRight = new ArrayList<WeightPair<String> >();
		if (rightNodes != null){
			for (String node : rightNodes){
				potentialRight.add(new WeightPair<String>(rightJSON.getInt(node),node));
			}
		}
		children = new ArrayList<LevelNode>();

		/*
		String[] leftNodes = JSONObject.getNames(leftJSON);
		potentialLeft = new ArrayList<WeightPair<String> >();
		for (String node : leftNodes){
			potentialLeft.add(new WeightPair<String>(leftJSON.getInt(node),node));
		}
		 */
		levelPieces.put(chunk, this);
	}

	public LevelNode(LevelNode other){

		potentialRight = new ArrayList<WeightPair<String> >(other.potentialRight);
		levelChunk = other.levelChunk;
		children = new ArrayList<LevelNode>();
	}

    public void backUp(LevelNode node, double result)   {
    	LevelNode n = node;
        while(n != null)
        {
            n.nVisits++;
            n.totValue += result;
            n = n.parent;
        }
    }
	public LevelNode selectAction() {
		for (int ii =0; ii < numIterations; ii++){
			LevelNode selected = select();
			double delta = selected.rollOut();
			backUp(selected,delta);
		}
		return bestAction();
		/*
        List<LevelNode> visited = new LinkedList<LevelNode>();
        LevelNode cur = this;
        visited.add(this);
        for (int ii = 0; ii < numIterations; ii++){
        	cur = this;
	        while (!cur.isLeaf()) {
	            cur = cur.select();
	            visited.add(cur);
	        }
	        cur.expand();
	        LevelNode newNode = cur.select();
	        visited.add(newNode);
	        double value = rollOut(newNode);
	        for (LevelNode node : visited) {
	            node.updateStats(value);
	        }
        }
        double maxValue = Double.NEGATIVE_INFINITY;
        LevelNode best = null;
        for (LevelNode child : children){
        	if (child.totValue > maxValue){
        		maxValue = child.totValue;
        		best = child;
        	}
        }
        return best;
		 */
	}
    public LevelNode bestAction()
    {
        int selected = -1;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (int ii=0; ii<children.size(); ii++) {

            double tieBreaker = rand.nextDouble() * epsilon;
            if(children.get(ii) != null && children.get(ii).totValue + tieBreaker > bestValue) {
                bestValue = children.get(ii).totValue + tieBreaker;
                selected = ii;
            }
        }

        if (selected == -1)
        {
            System.out.println("Unexpected selection!");
            selected = 0;
        }

        return children.get(selected);
    }
	private LevelNode select() {
		LevelNode current = this;
		while (current.position < levelSize){
			if (current.notFullyExpanded()){
				return current.expand();	    			
			}
			else {
				current = current.uct();
			}
		}
		return current;
		/*
	        LevelNode selected = null;
	        double bestValue = Double.NEGATIVE_INFINITY;
	        for (LevelNode c : children) {
	            double uctValue = c.totValue / (c.nVisits + epsilon) +
	                       Math.sqrt(Math.log(nVisits+1) / (c.nVisits + epsilon)) +
	                           rand.nextDouble() * epsilon;
	            // small random number to break ties randomly in unexpanded nodes
	            if (uctValue > bestValue) {
	                selected = c;
	                bestValue = uctValue;
	            }
	        }
	        return selected;
		 */

	}
	private boolean notFullyExpanded() {
		return potentialRight.size() > 0;
	}
	public LevelNode uct(){

		LevelNode selected = null;
		double bestValue = Double.NEGATIVE_INFINITY;
		for (LevelNode child : children)
		{
			double hvVal = child.totValue;
			double childValue =  hvVal / (child.nVisits + LevelNode.epsilon);

			double uctValue = childValue +
					LevelNode.K * Math.sqrt(Math.log(this.nVisits + 1) / (child.nVisits + LevelNode.epsilon)) +
					LevelNode.rand.nextDouble() * LevelNode.epsilon;

			// small sampleRandom numbers: break ties in unexpanded nodes
			if (uctValue > bestValue) {
				selected = child;
				bestValue = uctValue;
			}
		}

		return selected;
	}
	public LevelNode expand(){
		WeightPair<String> pair = WeightPair.GetWeightPair(this.potentialRight,rand.nextFloat());
		String nextChunk = pair.obj;
		this.potentialRight.remove(pair);
		LevelNode nextPiece = new LevelNode(levelPieces.get(nextChunk));
		nextPiece.parent = this;
		children.add(nextPiece);
		return nextPiece;
	}
	public boolean isLeaf() {
		return children.size() == 0;
	}
	public int getSize(){
		if (levelChunk == ""){
			return 0;
		}
		if (size < 0){
			size = levelChunk.split(";")[0].split(",").length;
		}
		return size;
	}
	public int getTotalSize(){
		if (parent == null){
			return  getSize();
		}
		else {
			return getSize()+parent.getTotalSize();
		}
	}
	public double rollOut() {
		ArrayList<LevelNode> restOfLevel = new ArrayList<LevelNode>();
		randomLevelCreation(restOfLevel,levelSize-getTotalSize());

		return EvaluateLevel(restOfLevel);

	}
	public float EvaluateLevel(ArrayList<LevelNode> restOfLevel){
		//TODO Evaluate level
		int size = getTotalSize();
		for (LevelNode node : restOfLevel){
			size += node.getSize();
		}

		if (size != levelSize){
			return -1.0f;
		}
		return 1.0f;
	}
	public void randomLevelCreation(ArrayList<LevelNode> levelSoFar,int totalSize){
		if (totalSize > 0){
			if (this.potentialRight.size() != 0){
				String nextChunk = WeightPair.GetWeightPair(this.potentialRight,rand.nextFloat()).obj;
				LevelNode nextPiece = new LevelNode(levelPieces.get(nextChunk));
				nextPiece.parent = this;
				levelSoFar.add(nextPiece);
				nextPiece.randomLevelCreation(levelSoFar, totalSize-nextPiece.getSize());
			}
		}

	}
	public void updateStats(double value) {
		nVisits++;
		totValue += value;
	}

	public static double evaluateLevel(LevelNode toBeAdded,List<LevelNode> level){
		return 0.0;
	}
}
