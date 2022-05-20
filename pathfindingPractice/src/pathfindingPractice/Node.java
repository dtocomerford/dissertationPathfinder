package pathfindingPractice;

import java.util.ArrayList;

public class Node 
{
	public boolean isAbstractNode;
	public boolean startNode;
	public boolean endNode;
	public boolean isWalkable;
	public char label;
	public int xPosition;
	public int yPosition;
	public double fValue, gValue, hValue;
	public ArrayList<Node> neighbours;
	public boolean isConnectionNode;
	public Node previous;
	public int zone;
	public ArrayList<Node> abstractNeighbours;
	public ArrayList<Integer> abstractNeighboursLengths;
	
	
	public Node(int _x, int _y, boolean _isStart, boolean _isEnd, char _nodeLabel, boolean _isWalkable)
	{
		this.xPosition = _x;
		this.yPosition = _y;
		this.startNode = _isStart;
		this.endNode = _isEnd;
		this.label = _nodeLabel;
		this.isWalkable = _isWalkable;
		neighbours = new ArrayList<Node>();
		fValue = Double.POSITIVE_INFINITY; 
		gValue = Double.POSITIVE_INFINITY;
		hValue = Double.POSITIVE_INFINITY;
		this.isConnectionNode = false;
		this.previous = null;
		this.zone = 0;
		this.isAbstractNode = false;
		abstractNeighbours = new ArrayList<Node>();
		abstractNeighboursLengths = new ArrayList<Integer>();
	}
	
	
	//Hello
	public void getNeighbours(Node[][] map)
	{
		
		if(this.yPosition < map.length -1)
		{
			if(map[this.xPosition][this.yPosition + 1].isWalkable == true)
			{
				neighbours.add(map[this.xPosition][this.yPosition + 1]);
			}
			else{}
		}
		if(this.yPosition > 0)
		{
			if(map[this.xPosition][this.yPosition - 1].isWalkable == true)
			{
				neighbours.add(map[this.xPosition][this.yPosition - 1]);
			}
			else{}
			
		}
		//0 index, (column length - 1) = 9 which is the last column on the grid so no need to get a neighbour to the right 
		//of that node as one doesn't exist
		if(this.xPosition < map[0].length -1)
		{		
			if(map[this.xPosition + 1][this.yPosition].isWalkable == true)
			{
				neighbours.add(map[this.xPosition + 1][this.yPosition]);
			}
			else{}
			
		}
		if(this.xPosition > 0)
		{ 
			if(map[this.xPosition - 1][this.yPosition].isWalkable == true)
			{
				neighbours.add(map[this.xPosition - 1][this.yPosition]);
			}
			else{}
			
		}
		
		
		//return neighbours;
	}
	
	
	public void getAbstractNeighbours(Node[][] map)
	{
		
		if(map[this.xPosition][this.yPosition].isAbstractNode == true)
		{
			
			
			if(this.yPosition < map.length -1)
			{
				if(map[this.xPosition][this.yPosition + 1].isAbstractNode == true)
				{
					abstractNeighbours.add(map[this.xPosition][this.yPosition + 1]);
				}
				else{}
			}
			if(this.yPosition > 0)
			{
				if(map[this.xPosition][this.yPosition - 1].isWalkable == true)
				{
					abstractNeighbours.add(map[this.xPosition][this.yPosition - 1]);
				}
				else{}
				
			}
			//0 index, (column length - 1) = 9 which is the last column on the grid so no need to get a neighbour to the right 
			//of that node as one doesn't exist
			if(this.xPosition < map[0].length -1)
			{		
				if(map[this.xPosition + 1][this.yPosition].isWalkable == true)
				{
					abstractNeighbours.add(map[this.xPosition + 1][this.yPosition]);
				}
				else{}
				
			}
			if(this.xPosition > 0)
			{ 
				if(map[this.xPosition - 1][this.yPosition].isWalkable == true)
				{
					abstractNeighbours.add(map[this.xPosition - 1][this.yPosition]);
				}
				else{}
				
			}
		}
		
		
		
		//return neighbours;
	}
	
	
	
}