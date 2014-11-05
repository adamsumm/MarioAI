package cmps244;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;

public class BasicAStar {
	protected static int[][] longJumpLocations;
	protected static int[][] shortJumpLocations;
	public static ArrayList<BasicNode> GetPath(LevelEntity[][] level, BasicNode start,BasicNode goal){
		int[][]  temp = {{ 1,-1},
				 { 1,-2},
				 { 2,-2},
				 { 2,-3},
				 { 3,-3},
				 { 3,-4},
				 { 4,-4},
				 { 5,-3},
				 { 6,-3},
				 { 7,-3},
				 { 8,-2},
				 { 8,-1},
				 { 9,-1}};
		int[][]  temp2 = {{ 1,-1},
				 { 1,-2},
				 { 1,-3},
				 {1,-4},
				 { 2,-4}};
		longJumpLocations = temp;
		shortJumpLocations = temp;
		HashSet<BasicNode> closedSet = new HashSet<BasicNode>();
		Hashtable<BasicNode,BasicNode> cameFrom = new Hashtable<BasicNode,BasicNode>();
		Hashtable<BasicNode,Double> gScore =new Hashtable<BasicNode,Double>();
		Hashtable<BasicNode,Double> fScore =new Hashtable<BasicNode,Double>();
		BasicNode[][] nodeGrid = new BasicNode[level.length][level[0].length];

		PriorityQueue<BasicNode> openSet = new PriorityQueue<BasicNode>(500,new BasicComparator(fScore));
		
		nodeGrid[start.xx][start.yy] = start;
		nodeGrid[goal.xx][goal.yy] = goal;
		
		gScore.put(start, 0.0);
		
		fScore.put(start, (double) (goal.xx - start.xx));
		
		openSet.add(start);
		
		while (openSet.size() > 0){
			BasicNode current = openSet.poll();
			if (current == goal){
				ArrayList<BasicNode> path = new ArrayList<BasicNode>();
				while (current != start){
					path.add(current);
					current = cameFrom.get(current);
				}
				return path;//reconstructPath(cameFrom,goal);
			}
			
			closedSet.add(current);
			
			for (BasicNode neighbor : getNeighbors(current,level,nodeGrid)){
				if (!closedSet.contains(neighbor)){
					double tentativeGScore = gScore.get(current) + getDistance(current,neighbor);
					if (!openSet.contains(neighbor) || tentativeGScore < gScore.get(neighbor)){
						cameFrom.put(neighbor, current);
						gScore.put(neighbor, tentativeGScore);
						fScore.put(neighbor, tentativeGScore + goal.xx-neighbor.xx);
						if (openSet.contains(neighbor)){
							openSet.remove(neighbor);
						}
						openSet.add(neighbor);
					}
				}
			}
		}
		
		
		return null;		
	}
	public static double getDistance(BasicNode from, BasicNode to){
		return from.xx-to.xx + Math.abs(from.yy-to.yy);
	}
	public static ArrayList<BasicNode> getNeighbors(BasicNode location, LevelEntity[][] level, BasicNode[][] nodes){
		ArrayList<BasicNode> neighbors = new ArrayList<BasicNode>();
		BasicNode node = null;
		if (IsSolid(location.xx,location.yy+1,level)){
			//On Ground
			
			if (!IsSolid(location.xx+1,location.yy,level)){
				node = GetNode(location.xx+1,location.yy,nodes);
				if (node != null){
					neighbors.add(node);
				}
			}
			
			//Check Jump
			for (int[] offset : shortJumpLocations){
				
				if (!IsSolid(location.xx+offset[0],location.yy+offset[1],level)){
					node = GetNode(location.xx+offset[0],location.yy+offset[1],nodes);

					if (node != null){
						neighbors.add(node);
					}
				}
				else {
					break;
				}
			}
			for (int[] offset : longJumpLocations){
				
				if (!IsSolid(location.xx+offset[0],location.yy+offset[1],level)){
					node = GetNode(location.xx+offset[0],location.yy+offset[1],nodes);

					if (node != null){
						neighbors.add(node);
					}
				}
				else {
					break;
				}
			}
			
		}
		else {
			node = GetNode(location.xx,location.yy+1,nodes);

			if (node != null){
				neighbors.add(node);
			}
			if (!IsSolid(location.xx+1,location.yy+2,level)){	
				node = GetNode(location.xx+1,location.yy+2,nodes);

					if (node != null){
						neighbors.add(node);
					}
			}			
		}
		return neighbors;
	}
	public static BasicNode GetNode(int xx, int yy, BasicNode[][] nodes){
		if (xx < 0 || yy < 0 || xx >= nodes.length || yy >= nodes[0].length){
			return null;
		}
		else {
			BasicNode node = nodes[xx][yy];
			if (node == null){
				node = new BasicNode(xx,yy);
				nodes[xx][yy] = node;
			}
			return node;
			
		}
	}
	public static boolean IsSolid(int xx, int yy, LevelEntity[][] level){
		if (xx < 0 || yy < 0 || xx >= level.length || yy >= level[0].length){
			return false;
		}
		else {
			return level[xx][yy] == LevelEntity.Solid || level[xx][yy] == LevelEntity.Pipe || 
					level[xx][yy] == LevelEntity.Cannon || level[xx][yy] == LevelEntity.Destructible ||
					level[xx][yy] == LevelEntity.PowerUp || level[xx][yy] == LevelEntity.CoinBlock || ((yy>0 ) && level[xx][yy-1] == LevelEntity.Pipe);
		}
	}
}
