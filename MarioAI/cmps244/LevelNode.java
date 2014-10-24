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
	public static int levelSize = 160;
	public static int numIterations = 100;
    static double epsilon = 1e-6;
	LevelNode parent;
	LevelNode[] children;
	public double nVisits, totValue;

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
	}
	
	public LevelNode selectAction() {
        List<LevelNode> visited = new LinkedList<LevelNode>();
        LevelNode cur = this;
        visited.add(this);
        for (int ii = 0; ii < numIterations; ii++){
	        while (!cur.isLeaf()) {
	            cur = cur.select();
	            visited.add(cur);
	        }
	        cur.expand();
	        LevelNode newNode = cur.select();
	        visited.add(newNode);
	        double value = rollOut(newNode);
	        for (LevelNode node : visited) {
	       // 	System.out.println(visited.size() + ", "+ node);
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
    }
	 public void expand() {
		 /*
	        children = new LevelNode[nActions];
	        for (int i=0; i<nActions; i++) {
	            children[i] = new LevelNode();
	        }
	        */
		 children = new LevelNode[potentialRight.size()];
		 for (int ii = 0; ii < potentialRight.size(); ii++){
			 children[ii] = new LevelNode(levelPieces.get(potentialRight.get(ii).obj));
			 children[ii].parent = this;
		 }
	}

	    private LevelNode select() {
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
	    }

	    public boolean isLeaf() {
	        return children == null;
	    }
	    public int getSize(){
	    	return levelChunk.indexOf(";")/2;
	    }
	    public int getSizeOfParents(){
	    	if (parent == null){
	    		return  getSize();
	    	}
	    	else {
	    		return getSize()+parent.getSizeOfParents();
	    	}
	    }
	    public double rollOut(LevelNode tn) {
	        // ultimately a roll out will end in some value
	        // assume for now that it ends in a win or a loss
	        // and just return this at random
	    	ArrayList<LevelNode> restOfLevel = new ArrayList<LevelNode>();
	    	randomLevelCreation(restOfLevel,levelSize-getSizeOfParents());
	    	
	        return EvaluateLevel(restOfLevel);
	    	
	    }
	    public float EvaluateLevel(ArrayList<LevelNode> restOfLevel){
	    	//TODO Evaluate level
	    	int size = getSizeOfParents();
	    	for (LevelNode node : restOfLevel){
	    		size += node.getSize();
	    	}
	    	
	    	if (size > levelSize){
	    		return -1.0f;
	    	}
	    	return 1.0f;
	    }
	    public void randomLevelCreation(ArrayList<LevelNode> levelSoFar,int totalSize){
	    	if (totalSize > 0){
	    		if (this.potentialRight.size() != 0){
		    		LevelNode nextPiece = new LevelNode(levelPieces.get(WeightPair.GetWeightPair(this.potentialRight,rand.nextFloat()).obj));
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

	    public int arity() {
	        return children == null ? 0 : children.length;
	    }
	
	public static double evaluateLevel(LevelNode toBeAdded,List<LevelNode> level){
		return 0.0;
	}
}
