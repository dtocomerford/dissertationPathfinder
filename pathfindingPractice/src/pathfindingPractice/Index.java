package pathfindingPractice;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Map;

public class Index 
{
	static int row = 40;
	static int column = 40;
	public static int startX;
	public static int startY;
	public static int endX;
	public static int endY;
	public static int closedsetSize = 0;
	public static int opensetSize = 0;
	
	
	//Abstract nodes list
	public static ArrayList<Node> abstractNodes = new ArrayList<Node>();
	
	//Zone dictionary 
	public static Map<Integer, ArrayList<Node>> zoneDictionary = new HashMap<Integer, ArrayList<Node>>();
	
	
	public static ArrayList<Node> openSet = new ArrayList<Node>();
	public static ArrayList<Node> closedSet = new ArrayList<Node>();
	public static ArrayList<Node> finalPath = new ArrayList<Node>();
	public static ArrayList<Node> abstractPath = new ArrayList<Node>();
	
	
	public static ArrayList<Node> connectionNodes = new ArrayList<Node>();
	public static ArrayList<Node> testPath = new ArrayList<Node>();
	public static ArrayList<Node> temp = new ArrayList<Node>();
	
	
	public static Random random = new Random();
	
	public static Node[][] grid = new Node[column][row];
	public static Node[][] testGrid = new Node[100][100];

	public static Node start;
	public static Node current;
	public static Node goal;
	
	
	public static Node abstractStart;
	public static Node abstractCurrent;
	public static Node abstractGoal;
	
	public static long HPAStart;
	public static long HPAEnd;
	public static long HPAResult;
	
	public static long AStart;
	public static long AEnd;
	public static long AResult;
	
	
	public static void main(String[] args) 
	{
		
		populateTestGrid(testGrid);
		getNeighbours(testGrid);
		//Allocate zones
		createClusters(testGrid);
		positionTransitionNodes(testGrid);
		
		testAlgorithms();
	}
	
	
	
	
	
	
	public static void testAlgorithms()
	{	
		Random rand = new Random();
		
		
		
		
		for(int i =0; i < 5; i++)
		{
			int startX = rand.nextInt(testGrid.length - 1);
			int startY = rand.nextInt(testGrid.length - 1);
			int goalX = rand.nextInt(testGrid.length - 1);
			int goalY = rand.nextInt(testGrid.length - 1);

			
			
			Node[][] nothing = null;
			//////////////////////////////////////// BUILD GRAPH HPA* ///////////////////////////////////////////////////////////////////
			abstractStart = testGrid[startX][startY];
			abstractGoal = testGrid[goalX][goalY];
			
			//Add start and end to the abstract graph
			abstractNodes.add(testGrid[startX][startY]);
			abstractNodes.add(testGrid[goalX][goalY]);
			
			//Put abstract nodes on the boarder of each cluster
			getAbstractNeighbours(abstractNodes);
			
			printPlainGrid(testGrid);
			
			
			System.out.println();
			System.out.println("LOOP: " + i);
			System.out.println("START X " + abstractStart.yPosition + " Y " + abstractStart.xPosition);
			System.out.println("GOAL: X " + abstractGoal.yPosition + " Y " + abstractGoal.xPosition );
			
			System.out.println("Start: " + System.nanoTime());
			HPAStart = System.nanoTime();
			
			abstractPath = abstractSearch(abstractStart, abstractGoal);
			
			closedsetSize = 0;
			opensetSize = 0;
			for(int ip = 0; ip < abstractPath.size() - 1; ip++)
			{
				//System.out.println("Open " + opensetSize);
				//System.out.println("CLose " + closedsetSize);
				
				temp = hpaSearch(abstractPath.get(ip), abstractPath.get(ip+1), nothing , 1);
				finalPath.addAll(temp);
			}
			abstractStart.label = 'B';
			HPAEnd = System.nanoTime();
			System.out.println("End: " + HPAEnd);
			
			HPAResult =HPAEnd - HPAStart;
			System.out.println("HPA RESULT : " + HPAResult);
			System.out.println("Openset : " + opensetSize);
			System.out.println("CLosedset : " + closedsetSize);
			System.out.println();
			printGrid(testGrid);
			//////////////////////////////////////HPA* ///////////////////////////////////////////////////////////////////
			
			System.out.println();
			printPlainGrid(testGrid);
			closedsetSize = 0;
			opensetSize = 0;
			
			//////////////////////////////////////A*/////////////////////////////////////////////////////////////////////////////
			System.out.println("LOOP: " + i);
			System.out.println("Start A*: " + System.nanoTime());
			AStart = System.nanoTime();
			aStarSearch(abstractStart, abstractGoal);
			AEnd = System.nanoTime();
			System.out.println("End A*: " + AEnd);
			AResult = AEnd - AStart;
			System.out.println("A* RESULT : " + AResult);
			System.out.println("Openset : " + opensetSize);
			System.out.println("CLosedset : " + closedsetSize);
			//////////////////////////////////A*/////////////////////////////////////////////////////////////////////////////
			printGrid(testGrid);
			System.out.println();
			
			
		}
	}
	
	
	
	
	
	
	
	
	public static void printNow(Node[][] map)
	{
		for(int i = 0; i < map[0].length; i++)
		{
			for(int j = 0; j < map[0].length; j++)
			{
				if(map[i][j].isAbstractNode == true)
				{
					System.out.print(map[i][j].zone + " ");
				}else {
					System.out.print(map[i][j].label + " ");
					
				}
				
				//System.out.print(grid[i][j].zone + " ");
			}
			System.out.println("");
		}
	}
	
	
	public static void getAbstractNeighbours(ArrayList<Node> map)
	{
		//System.out.println("Called");
		for(int i = 0; i < map.size(); i++)
		{
			ArrayList<Node> path = new ArrayList<Node>();
			Node temp = map.get(i);
			for(int j = 0; j < map.size(); j++)
			{
				if(temp != map.get(j))
				{
					//Get the goal node
					path = aStarSearch(temp, map.get(j));
					if(temp.zone == map.get(j).zone)
					{
						
						//If nodes are in the same zone they are neighbours
						temp.abstractNeighbours.add(map.get(j));
					}
					
					if(path.size() == 2)
					{
						temp.abstractNeighbours.add(map.get(j));
						//System.out.println("Current X " + temp.yPosition + " Y " + temp.xPosition);
						//System.out.println("Neighbour X " + map.get(j).xPosition + " Y " + map.get(j).yPosition);
						//System.out.println("Current zone: " + temp.zone + "      Nighbour zone " + map.get(j).zone);

						//System.out.println("Path size: " + path.size());
						
						//System.out.println();
						//System.out.println();
					}

				}
				
				
			}
		}
		
	}
	
	
	public static void printAbNeighbours(ArrayList<Node> map)
	{
		for(int i =0; i< 5; i++)
		{
			char previousCurrent = map.get(i).label;
			map.get(i).label = 'C';
			
			for(int j = 0; j < map.get(i).abstractNeighbours.size(); j++)
			{
				map.get(i).abstractNeighbours.get(j).label = 'N';
				//System.out.println("ab neighbours X " + map.get(i).abstractNeighbours.get(j).yPosition + " Y" + map.get(i).abstractNeighbours.get(j).xPosition);
			}
			printGrid(testGrid);
			printPlainGrid(testGrid);
			System.out.println();
			map.get(i).label = previousCurrent;
		}
	}
	
	
	public static void positionTransitionNodes(Node[][] map)
	{

		for(int i =0; i < 4; i++)
		{
			for(int j =0; j < 4; j++)
			{
				
				
				int sumRow = 25 * j;
				int sumCol = 25 * i;
				
				
				
				if(sumRow > 0)
				{
					sumRow-=1;
				}
				if(sumCol > 0)
				{
					sumCol-=1;
				}

				
				abstractNodes.add(testGrid[sumRow][sumCol]);
				abstractNodes.add(testGrid[sumRow][sumCol+1]);
				abstractNodes.add(testGrid[sumRow+1][sumCol+1]);
				abstractNodes.add(testGrid[sumRow+1][sumCol]);

			}
			
		}

		for(int i = 0; i < abstractNodes.size(); i++)
		{
			abstractNodes.get(i).label = 'A';
			abstractNodes.get(i).isAbstractNode = true;
		}
		//System.out.println();
		//printGrid(testGrid);
			
	}
	
	
	public static void createClusters(Node[][] map)
	{
		int zoneLastHighest = 1;
		int zoneStart = 1;
		int zoneCounter = 1;
		int counterX = 1;
		int counterY = 1;
		
		
		//I is how many down
		for(int i = 0; i < map[0].length; i++)
		{
			//J is how many across
			for(int j = 0; j < map[0].length; j++)
			{

				if(counterX <= map[0].length/4 + 1) 
				{
					map[i][j].zone = zoneCounter;
				}
				counterX++;
				
				if(counterX >= map[0].length/4 + 1)
				{		
					counterX = 1;
					zoneCounter++;
				}
				
				
				//Track the highest number that the zone counter goes to in the inside loop
				if(zoneCounter > zoneLastHighest)
	            {
	                zoneLastHighest = zoneCounter;
	            }
				
				//End of inside loop
			}

			//Increase counterY as we've dropped down a row
			counterY++;
			
			if(counterY < map[0].length/4 + 1)
			{
				zoneCounter = zoneStart;
				//System.out.println( i + " zone start " + zoneStart);
			}
			//If we've dropped down 9 rows then we want to change the zone number we assign
			if(counterY >=map[0].length/4 + 1)
			{	
				//Reset the counter
				counterY = 1;	
				
				zoneCounter = zoneLastHighest;
				zoneStart = zoneLastHighest;
				//System.out.println("I = " + i + " zone counter " + zoneCounter);
			}		
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	public static void abstractConnections()
	{
				//ZONE 1 CONNECTIONS
				//Giving the algorithm two nodes and putting the resulting path in a list
				testPath = hpaSearch(testGrid[0][4], testGrid[4][2], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[0][4].abstractNeighbours.add(testGrid[4][2]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[0][4].abstractNeighboursLengths.add(testPath.size());
				
				//Adding the abstract neighbours to each others lists
				testGrid[4][2].abstractNeighbours.add(testGrid[0][4]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][2].abstractNeighboursLengths.add(testPath.size());
				
				System.out.println("Size of path " + testPath.size());
				testPath.clear();
				
				
				
				
				testPath = hpaSearch(testGrid[0][4], testGrid[4][4], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[0][4].abstractNeighbours.add(testGrid[4][4]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[0][4].abstractNeighboursLengths.add(testPath.size());
						
				//Adding the abstract neighbours to each others lists
				testGrid[4][4].abstractNeighbours.add(testGrid[0][4]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][4].abstractNeighboursLengths.add(testPath.size());
				
				System.out.println("Size of path " + testPath.size());
				testPath.clear();
				
				
				//Next connection
				testPath = hpaSearch(testGrid[4][4], testGrid[4][2], testGrid, 1);
				//Adding the abstract neighbours to each others lists
				testGrid[4][2].abstractNeighbours.add(testGrid[4][4]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][2].abstractNeighboursLengths.add(testPath.size());
						
				//Adding the abstract neighbours to each others lists
				testGrid[4][4].abstractNeighbours.add(testGrid[4][2]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][4].abstractNeighboursLengths.add(testPath.size());
				//ZONE 1 CONNECTIONS
				
				
				//ZONE 1 TO ZONE 2 CONNECTION NODE
				testPath = hpaSearch(testGrid[0][4], testGrid[0][5], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[0][4].abstractNeighbours.add(testGrid[0][5]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[0][4].abstractNeighboursLengths.add(testPath.size());
				
				//Adding the abstract neighbours to each others lists
				testGrid[0][5].abstractNeighbours.add(testGrid[0][4]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[0][5].abstractNeighboursLengths.add(testPath.size());
				
				System.out.println("Size of path " + testPath.size());
				testPath.clear();
				
				
				
				//ZONE 1 TO ZONE 2 CONNECTION NODE
				testPath = hpaSearch(testGrid[4][4], testGrid[4][5], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[4][4].abstractNeighbours.add(testGrid[4][5]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][4].abstractNeighboursLengths.add(testPath.size());
				
				//Adding the abstract neighbours to each others lists
				testGrid[4][5].abstractNeighbours.add(testGrid[4][4]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][5].abstractNeighboursLengths.add(testPath.size());
				
				System.out.println("Size of path " + testPath.size());
				testPath.clear();
				//ZONE 1 TO ZONE 2 CONNECTION NODE
				
				
				//ZONE 1 TO ZONE 3 CONNECTION NODE
				testPath = hpaSearch(testGrid[4][2], testGrid[5][2], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[4][2].abstractNeighbours.add(testGrid[5][2]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][2].abstractNeighboursLengths.add(testPath.size());
				
				//Adding the abstract neighbours to each others lists
				testGrid[5][2].abstractNeighbours.add(testGrid[4][2]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[5][2].abstractNeighboursLengths.add(testPath.size());
				
				System.out.println("Size of path " + testPath.size());
				testPath.clear();
				//ZONE 1 TO ZONE 3 CONNECTION NODE
				
				
				
				//ZONE 3 TO ZONE 4 CONNECTION NODE
				testPath = hpaSearch(testGrid[7][4], testGrid[7][5], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[7][4].abstractNeighbours.add(testGrid[7][5]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[7][4].abstractNeighboursLengths.add(testPath.size());
				
				//Adding the abstract neighbours to each others lists
				testGrid[7][5].abstractNeighbours.add(testGrid[7][4]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[7][5].abstractNeighboursLengths.add(testPath.size());
				
				System.out.println("Size of path " + testPath.size());
				testPath.clear();
				//ZONE 3 TO ZONE 4 CONNECTION NODE
				
				
				//ZONE 4 TO ZONE 2 CONNECTION NODE
				testPath = hpaSearch(testGrid[4][8], testGrid[5][8], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[4][8].abstractNeighbours.add(testGrid[5][8]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][8].abstractNeighboursLengths.add(testPath.size());
				
				//Adding the abstract neighbours to each others lists
				testGrid[5][8].abstractNeighbours.add(testGrid[4][8]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[5][8].abstractNeighboursLengths.add(testPath.size());
				
				System.out.println("Size of path " + testPath.size());
				testPath.clear();
				//ZONE 4 TO ZONE 2 CONNECTION NODE
				
				
				
				//ZONE 2 CONNECTIONS
				
				//Next connection
				testPath = hpaSearch(testGrid[0][5], testGrid[4][5], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[0][5].abstractNeighbours.add(testGrid[4][5]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[0][5].abstractNeighboursLengths.add(testPath.size());
						
				//Adding the abstract neighbours to each others lists
				testGrid[4][5].abstractNeighbours.add(testGrid[0][5]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][5].abstractNeighboursLengths.add(testPath.size());
				
				
				//Next connection
				testPath = hpaSearch(testGrid[0][5], testGrid[4][8], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[0][5].abstractNeighbours.add(testGrid[4][8]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[0][5].abstractNeighboursLengths.add(testPath.size());
						
				//Adding the abstract neighbours to each others lists
				testGrid[4][8].abstractNeighbours.add(testGrid[0][5]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][8].abstractNeighboursLengths.add(testPath.size());
				
				
				//Next connection
				testPath = hpaSearch(testGrid[4][5], testGrid[4][8], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[4][5].abstractNeighbours.add(testGrid[4][8]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][5].abstractNeighboursLengths.add(testPath.size());
						
				//Adding the abstract neighbours to each others lists
				testGrid[4][8].abstractNeighbours.add(testGrid[4][5]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[4][8].abstractNeighboursLengths.add(testPath.size());
				//ZONE 2 CONNECTIONS
				
				
				//ZONE 3 CONNECTIONS
				
				//Next connection
				testPath = hpaSearch(testGrid[5][2], testGrid[7][4], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[5][2].abstractNeighbours.add(testGrid[7][4]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[5][2].abstractNeighboursLengths.add(testPath.size());
						
				//Adding the abstract neighbours to each others lists
				testGrid[7][4].abstractNeighbours.add(testGrid[5][2]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[7][4].abstractNeighboursLengths.add(testPath.size());
				//ZONE 3 CONNECTIONS
				
				
				//ZONE 4 CONNECTIONS
				//Next connection
				testPath = hpaSearch(testGrid[7][5], testGrid[5][8], testGrid, 1);
				
				//Adding the abstract neighbours to each others lists
				testGrid[7][5].abstractNeighbours.add(testGrid[5][8]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[7][5].abstractNeighboursLengths.add(testPath.size());
						
				//Adding the abstract neighbours to each others lists
				testGrid[5][8].abstractNeighbours.add(testGrid[7][5]);
				//Adding the length of each path to neighbour abstract nodes
				//These are stored in a parrallel List 
				testGrid[5][8].abstractNeighboursLengths.add(testPath.size());
				
				
				System.out.println("");
				System.out.println("CONNECTON START NODE ");
				for(int i =0; i < abstractNodes.size(); i++)
				{
					if(abstractNodes.get(i).zone == testGrid[0][0].zone && abstractNodes.get(i) != testGrid[0][0])
					{
						System.out.println("Zone of start " + testGrid[0][0].zone);
						
						testPath = hpaSearch(testGrid[0][0], testGrid[abstractNodes.get(i).xPosition][abstractNodes.get(i).yPosition], testGrid, 1);
						
						//Adding the abstract neighbours to each others lists
						testGrid[0][0].abstractNeighbours.add(testGrid[abstractNodes.get(i).xPosition][abstractNodes.get(i).yPosition]);
						//Adding the length of each path to neighbour abstract nodes
						testGrid[0][0].abstractNeighboursLengths.add(testPath.size());
								
						
						//Adding the abstract neighbours to each others lists
						testGrid[abstractNodes.get(i).xPosition][abstractNodes.get(i).yPosition].abstractNeighbours.add(testGrid[0][0]);
						//Adding the length of each path to neighbour abstract nodes
						//These are stored in a parrallel List 
						testGrid[abstractNodes.get(i).xPosition][abstractNodes.get(i).yPosition].abstractNeighboursLengths.add(testPath.size());
						System.out.println("Size of path " + testPath.size());
						for(int j = 0; j < testPath.size(); j++)
						{
							testPath.get(j).label = '%';
							
						}
						printGrid(testGrid);
						printPlainGrid(testGrid);
					}
				}
				
				
				System.out.println("CONNECTON FOR END NODE ");
				for(int i =0; i < abstractNodes.size(); i++)
				{
					if(abstractNodes.get(i).zone == testGrid[testGrid.length-1][testGrid[0].length-1].zone && abstractNodes.get(i) != testGrid[testGrid.length-1][testGrid[0].length-1])
					{
						System.out.println("Zone of start " + testGrid[testGrid.length-1][testGrid[0].length-1].zone);
						
						testPath = hpaSearch(testGrid[testGrid.length-1][testGrid[0].length-1], testGrid[abstractNodes.get(i).xPosition][abstractNodes.get(i).yPosition], testGrid, 1);
						
						//Adding the abstract neighbours to each others lists
						testGrid[testGrid.length-1][testGrid[0].length-1].abstractNeighbours.add(testGrid[abstractNodes.get(i).xPosition][abstractNodes.get(i).yPosition]);
						//Adding the length of each path to neighbour abstract nodes
						testGrid[testGrid.length-1][testGrid[0].length-1].abstractNeighboursLengths.add(testPath.size());
								
						
						//Adding the abstract neighbours to each others lists
						testGrid[abstractNodes.get(i).xPosition][abstractNodes.get(i).yPosition].abstractNeighbours.add(testGrid[testGrid.length-1][testGrid[0].length-1]);
						//Adding the length of each path to neighbour abstract nodes
						//These are stored in a parrallel List 
						testGrid[abstractNodes.get(i).xPosition][abstractNodes.get(i).yPosition].abstractNeighboursLengths.add(testPath.size());
						System.out.println("Size of path " + testPath.size());
						for(int j = 0; j < testPath.size(); j++)
						{
							testPath.get(j).label = '%';
							
						}
						printGrid(testGrid);
						printPlainGrid(testGrid);
					}
				}

				
				
				/*
				System.out.println("Size of path " + testPath.size());
				for(int i = 0; i < testPath.size(); i++)
				{
					testPath.get(i).label = '%';
					
				}
				*/
	}
	
	
	
	public static void getGScores()
	{
		
		for(int i = 0; i < grid[0].length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				grid[i][j].gValue = Math.sqrt(Math.pow(grid[i][j].xPosition - start.xPosition, 2) + Math.pow(grid[i][j].yPosition - start.yPosition, 2));
				//System.out.println("Grid pos X: " + grid[i][j].xPosition + ".  Y: " +  grid[i][j].yPosition + "'s G value " + grid[i][j].gValue);
			}
		}
	}
	
	public static void getHScores()
	{
		for(int i = 0; i < grid[0].length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				grid[i][j].hValue = Math.sqrt(Math.pow(grid[i][j].xPosition - goal.xPosition, 2) + Math.pow(grid[i][j].yPosition - goal.yPosition, 2));
				//System.out.println("Grid pos X: " + grid[i][j].xPosition + ". Y: " +  grid[i][j].yPosition + "'s H value " + grid[i][j].hValue);
			}
		}
	}
	
	
	//Distance from start 
	public static void getNewGScores(Node newStart)
	{
		
		for(int i = 0; i < grid[0].length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				grid[i][j].gValue = Math.sqrt(Math.pow(grid[i][j].xPosition - newStart.xPosition, 2) + Math.pow(grid[i][j].yPosition - newStart.yPosition, 2));
				//System.out.println("Grid pos X: " + grid[i][j].xPosition + ".  Y: " +  grid[i][j].yPosition + "'s G value " + grid[i][j].gValue);
			}
		}
	}
	
	//Distance from goal calculations
	public static void getNewHScores(Node newGoal, int zoneToSearch)
	{
		
		//Get the list of a single zone of nodes
		ArrayList<Node> zone = zoneDictionary.get(zoneToSearch);
				
		//Find closest node to end in that zone
		for(int p =0; p< zone.size(); p++)
		{
			grid[zone.get(p).xPosition][zone.get(p).yPosition].hValue = Math.sqrt(Math.pow(grid[zone.get(p).xPosition][zone.get(p).yPosition].xPosition - newGoal.xPosition, 2) + Math.pow(grid[zone.get(p).xPosition][zone.get(p).yPosition].yPosition - newGoal.yPosition, 2));
			//grid[zone.get(p).xPosition][zone.get(p).yPosition].label = '£';
		}
		
		
		/*
		for(int i = 0; i < grid[0].length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				grid[i][j].hValue = Math.sqrt(Math.pow(grid[i][j].xPosition - newGoal.xPosition, 2) + Math.pow(grid[i][j].yPosition - newGoal.yPosition, 2));
				//System.out.println("Grid pos X: " + grid[i][j].xPosition + ". Y: " +  grid[i][j].yPosition + "'s H value " + grid[i][j].hValue);
			}
		}
		
		*/
	}



	public static void populateGrid()
	{
		
		
		for(int i = 0; i < grid[0].length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				//Do the coordinates match the start point?
				if(i == startX && j == startY)
				{
					Node node = new Node(i, j, true, false, 'S', true);
					grid[i][j] = node;
				}
				//Do the coordinates match the end point?
				else if(i == endX && j == endY)
				{
					Node node = new Node(i, j, false, true, 'G', true);
					grid[i][j] = node;
				}else
				{	
					Node node = new Node(i, j, false, false, '~', true);
					grid[i][j] = node;
					
					/*
					int obstacle = (random.nextInt(3));
					
					if(obstacle == 2)
					{
						Node node = new Node(i, j, false, false, '@', false);
						grid[i][j] = node;
					}
					else{
						Node node = new Node(i, j, false, false, '#', true);
						grid[i][j] = node;
					}
					*/
				}
			}	
		}		
	}
	
	
	public static void populateTestGrid(Node[][] map)
	{
		
		
		for(int i = 0; i < map[0].length; i++)
		{
			for(int j = 0; j < map[0].length; j++)
			{
				//Do the coordinates match the start point?
				if(i == startX && j == startY)
				{
					Node node = new Node(i, j, true, false, '~', true);
					map[i][j] = node;
				}
				//Do the coordinates match the end point?
				else if(i == endX && j == endY)
				{
					Node node = new Node(i, j, false, true, '~', true);
					map[i][j] = node;
				}else
				{	
					Node node = new Node(i, j, false, false, '~', true);
					map[i][j] = node;
					
					/*
					int obstacle = (random.nextInt(3));
					
					if(obstacle == 2)
					{
						Node node = new Node(i, j, false, false, '@', false);
						map[i][j] = node;
					}
					else{
						Node node = new Node(i, j, false, false, '#', true);
						map[i][j] = node;
					}
					*/
					
				}
			}	
		}		
	}
	
	public static void printGrid(Node[][] map)
	{
		for(int i = 0; i < map[0].length; i++)
		{
			for(int j = 0; j < map[0].length; j++)
			{
				System.out.print(map[i][j].label + " ");
				//System.out.print(grid[i][j].zone + " ");
			}
			System.out.println("");
		}
	}
	
	public static void printTrue(Node[][] map)
	{
		for(int i = 0; i < map[0].length; i++)
		{
			for(int j = 0; j < map[0].length; j++)
			{
				if(map[i][j].isAbstractNode == true)
				{
					System.out.print( "T ");
				}else
				{
					System.out.print( "* ");
				}
				
				//System.out.print(grid[i][j].zone + " ");
			}
			System.out.println("");
		}
	}
	
	public static void printPlainGrid(Node[][] map)
	{
		for(int i = 0; i < map[0].length; i++)
		{
			for(int j = 0; j < map[0].length; j++)
			{
				
				if(map[i][j].isAbstractNode)
				{
					map[i][j].label = 'A';
				}else 
				{
					map[i][j].label = '~';
				}
				
				//map[i][j].label = '~';
				//System.out.print(map[i][j].label + " ");
				//System.out.print(grid[i][j].zone + " ");
			}
			//System.out.println("");
		}
	}
	
	public static void printGridInfo()
	{
		for(int i = 0; i < grid[0].length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				System.out.println(grid[i][j].label);
				System.out.println(grid[i][j].zone);
				System.out.println(grid[i][j].gValue);
				System.out.println(grid[i][j].hValue);
				System.out.println(grid[i][j].fValue);
				
				
			}
			System.out.println("Next row");
		}
	}
	
	public static void printZones(Node[][] map)
	{
		for(int i = 0; i < map[0].length; i++)
		{
			for(int j = 0; j < map[0].length; j++)
			{
				if(map[i][j].isAbstractNode == true)
				{
					System.out.print(" " + map[i][j].label + "   ");
				}else 
				{
					if(map[i][j].zone <= 9)
					{
						System.out.print(" " + map[i][j].zone + "   ");
					}
					else 
					{
						System.out.print(map[i][j].zone + "   ");
					}
					
				}
				
				
				
			}
			System.out.println(" ");
			System.out.println(" ");
		}
	}
	

	
	
	public static void getAbNeighbours(Node[][] map)
	{
		
		for(int i = 0; i < map[0].length; i++)
		{
			for(int j = 0; j < map[0].length; j++)
			{
	
				map[i][j].getAbstractNeighbours(map);
			}
		}
	}

	
	
	
	
	public static void getNeighbours(Node[][] map)
	{
		
		for(int i = 0; i < map[0].length; i++)
		{
			for(int j = 0; j < map[0].length; j++)
			{
	
				map[i][j].getNeighbours(map);
			}
		}
	}
	
	public static void printNeighbours(Node[][] grid)
	{
		System.out.println();
		
		for(int i = 0; i < grid[0].length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				grid[i][j].label = 'C';	
				System.out.println("Number of neighbours: " + grid[i][j].neighbours.size());
		
				for(int q = 0; q < grid[i][j].neighbours.size(); q++) 
				{
					grid[i][j].neighbours.get(q).label = 'N';	
				}
				
				//System.out.println();
				printGrid(grid);
				System.out.println();
				grid[i][j].label = '#';
				
				for(int column = 0; column < grid[0].length; column++)
				{
					for(int row = 0; row < grid[0].length; row++)
					{
						grid[column][row].label = '#';
						
						if(grid[column][row].startNode == true)
						{
							grid[column][row].label = 'S';
						}
						if(grid[column][row].endNode == true)
						{
							grid[column][row].label = 'E';
						}
						if(grid[column][row].isWalkable == false)
						{
							grid[column][row].label = '*';
						}
					}
				}
				
			}
		}	
	}
	
	public static void printAbNeighbours(Node[][] grid)
	{
		System.out.println();
		
		for(int i = 0; i < grid[0].length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				grid[i][j].label = 'C';	
				System.out.println("Number of neighbours: " + grid[i][j].abstractNeighbours.size());
		
				for(int q = 0; q < grid[i][j].abstractNeighbours.size(); q++) 
				{
					grid[i][j].abstractNeighbours.get(q).label = 'N';	
				}
				
				//System.out.println();
				printGrid(grid);
				System.out.println();
				grid[i][j].label = '#';
				
				for(int column = 0; column < grid[0].length; column++)
				{
					for(int row = 0; row < grid[0].length; row++)
					{
						grid[column][row].label = '#';
						
						if(grid[column][row].startNode == true)
						{
							grid[column][row].label = 'S';
						}
						if(grid[column][row].endNode == true)
						{
							grid[column][row].label = 'E';
						}
						if(grid[column][row].isWalkable == false)
						{
							grid[column][row].label = '*';
						}
					}
				}
				
			}
		}	
	}

	
	
	public static double returnDistance(Node current, Node neighbour)
	{
		double distance;
		//grid[i][j].hValue = Math.sqrt(Math.pow(grid[i][j].xPosition - goal.xPosition, 2) + Math.pow(grid[i][j].yPosition - goal.yPosition, 2));
		distance = Math.sqrt(Math.pow(current.xPosition - neighbour.xPosition, 2) + Math.pow(current.yPosition - neighbour.yPosition, 2));
		return distance;
	}
	
	
	public static void returnPath(Node startNode, Node goalNode)
	{
		Node currentNode = goalNode;
		ArrayList<Node> path = new ArrayList<Node>();
		
		
		while(currentNode != startNode)
		{
			System.out.println("Start node x " + startNode.yPosition + " y" +  startNode.xPosition);
			System.out.println("Zone " + startNode.zone);
			System.out.println(" ");
			
			
			//Wrong way around purposeful 
			System.out.println("x " + currentNode.yPosition + " y" +  currentNode.xPosition);
			System.out.println("Zone " + currentNode.zone);
			System.out.println(" ");
			
			
			currentNode.label = '.';
			
			if(currentNode == goalNode)
			{
				currentNode.label = 'G';
			}
			path.add(currentNode);
			if(currentNode == startNode)
			{
				System.out.println("WE CALLED IT");
				break;
			}
			currentNode = currentNode.previous;
		}
		//currentNode.label = 'S';
		System.out.println("PATH RETURNED");
	}
	
	public static ArrayList<Node> returnPathList(Node startNode, Node goalNode)
	{
		Node currentNode = goalNode;
		ArrayList<Node> path = new ArrayList<Node>();
		
		
		while(currentNode != startNode)
		{
			
			//System.out.println("X " + currentNode.yPosition + " Y " + currentNode.xPosition);
			//System.out.println("End X " + targetNode.yPosition + " Y " + targetNode.xPosition);
			currentNode.label = '.';
			
			if(currentNode == goalNode)
			{
				currentNode.label = 'G';
			}
			path.add(currentNode);
			currentNode = currentNode.previous;
		}
		currentNode.label = 'S';
		//System.out.println("X " + currentNode.yPosition + " Y " + currentNode.xPosition);
		path.add(currentNode);
		
		//System.out.println("PATH RETURNED");
		return path;
	}
	
	
	public static ArrayList<Node> abstractSearch(Node startNode, Node target)
	{
		ArrayList<Node> foundPath = new ArrayList<Node>();
		Node targetNode = target;
		Node startingNode = startNode;
		
		//System.out.println("Start X " + startNode.yPosition + " Y " + startNode.xPosition);
		//System.out.println("End X " + targetNode.yPosition + " Y " + targetNode.xPosition);
		
		openSet.clear();
		closedSet.clear();
		openSet.add(startingNode);
		
		while(openSet.size() > 0)
		{
			Node currentNode = openSet.get(0);
			
			//Update values for nodes in the open set
			for(int i = 1; i < openSet.size(); i++)
			{
				//Get new h value 
				openSet.get(i).hValue = Math.sqrt(Math.pow(openSet.get(i).xPosition - target.xPosition, 2) + Math.pow(openSet.get(i).yPosition - target.yPosition, 2));
				//Get new g value
				openSet.get(i).gValue = Math.sqrt(Math.pow(openSet.get(i).xPosition - startNode.xPosition, 2) + Math.pow(openSet.get(i).yPosition - startNode.yPosition, 2));
				
			}
			
			
			//Getting the best node from the open set, first loop through there's only one to choose from which is the start node
			for(int i = 1; i < openSet.size(); i++)
			{
				//if two nodes have the same F score we pick the one which is closer to the goal, with the better H score
				if(openSet.get(i).fValue < currentNode.fValue || openSet.get(i).fValue == currentNode.fValue && openSet.get(i).hValue < currentNode.hValue)
				{
					currentNode = openSet.get(i);
				}
			}
			
			//System.out.println("Current X " + currentNode.yPosition + " Y " + currentNode.xPosition);
			
			//remove the current node from the open set as we're now done with it and add current node to the closed set 
			openSet.remove(currentNode);
			closedSet.add(currentNode);
			
			
			//If it's the goal you know what to do
			if(currentNode == targetNode)
			{
				foundPath = returnPathList(startingNode, targetNode);
				
				return foundPath;
			}
			
			
			//Loop through every neighbour of the current node 
			for(int i= 0; i < currentNode.abstractNeighbours.size(); i++)
			{
				Node neighbour = currentNode.abstractNeighbours.get(i);
				
				//Check the closed list to see if the neighbour is in that list, if so the loop breaks the current iteration and goes again 
				if(closedSet.contains(neighbour))
				{
					continue;
				}
				
				//Calculate the distance from the neighbour to the start 
				double newMovementCostToNeighbour = currentNode.gValue + returnDistance(currentNode, neighbour);
				
				//Check to see if distance from the start to the neighbour through the current node is a shorter distance than the G score of the neighbour currently 
				//Add check if the neighbour is in the open set
				if(newMovementCostToNeighbour < neighbour.gValue || !openSet.contains(neighbour))
				{
					neighbour.gValue = newMovementCostToNeighbour;
					neighbour.hValue = returnDistance(neighbour, targetNode);
					neighbour.previous = currentNode;
					
					if(!openSet.contains(neighbour))
					{
						openSet.add(neighbour);
						//neighbour.label = 'O';
					}	
				}		
			}	
		}
		return foundPath;
	}
	
	
	
	///WORKING ON
	public static ArrayList<Node> hpaSearch(Node startNode, Node target, Node[][] makp, int zoneToSearch)
	{
		int zone = zoneToSearch;
		ArrayList<Node> foundPath = new ArrayList<Node>();
		Node targetNode = target;
		Node startingNode = startNode;
		
		//System.out.println("Start X " + startNode.yPosition + " Y " + startNode.xPosition);
		//System.out.println("End X " + targetNode.yPosition + " Y " + targetNode.xPosition);
		
		//System.out.println("Closed set " + closedSize);
		//System.out.println("Open set " + openSize);
		
		openSet.clear();
		closedSet.clear();
		openSet.add(startingNode);
		
		while(openSet.size() > 0)
		{
			Node currentNode = openSet.get(0);
			
			//Update values for nodes in the open set
			for(int i = 1; i < openSet.size(); i++)
			{
				//Get new h value 
				openSet.get(i).hValue = Math.sqrt(Math.pow(openSet.get(i).xPosition - target.xPosition, 2) + Math.pow(openSet.get(i).yPosition - target.yPosition, 2));
				//Get new g value
				openSet.get(i).gValue = Math.sqrt(Math.pow(openSet.get(i).xPosition - startNode.xPosition, 2) + Math.pow(openSet.get(i).yPosition - startNode.yPosition, 2));
				
			}
			
			
			//Getting the best node from the open set, first loop through there's only one to choose from which is the start node
			for(int i = 1; i < openSet.size(); i++)
			{
				//if two nodes have the same F score we pick the one which is closer to the goal, with the better H score
				if(openSet.get(i).fValue < currentNode.fValue || openSet.get(i).fValue == currentNode.fValue && openSet.get(i).hValue < currentNode.hValue)
				{
					currentNode = openSet.get(i);
				}
			}
			
			//System.out.println("Current X " + currentNode.yPosition + " Y " + currentNode.xPosition);
			
			//remove the current node from the open set as we're now done with it and add current node to the closed set 
			openSet.remove(currentNode);
			closedSet.add(currentNode);
			
			//If it's the goal you know what to do
			if(currentNode == targetNode)
			{
				//System.out.println("Called ");
				foundPath = returnPathList(startingNode, targetNode);
				closedsetSize += closedSet.size();
				opensetSize += openSet.size();
				return foundPath;
			}
			
			
			//Loop through every neighbour of the current node 
			for(int i= 0; i < currentNode.neighbours.size(); i++)
			{
				Node neighbour = currentNode.neighbours.get(i);
				
				//Check the closed list to see if the neighbour is in that list, if so the loop breaks the current iteration and goes again 
				if(closedSet.contains(neighbour))
				{
					continue;
				}
				
				//Calculate the distance from the neighbour to the start 
				double newMovementCostToNeighbour = currentNode.gValue + returnDistance(currentNode, neighbour);
				
				//Check to see if distance from the start to the neighbour through the current node is a shorter distance than the G score of the neighbour currently 
				//Add check if the neighbour is in the open set
				if(newMovementCostToNeighbour < neighbour.gValue || !openSet.contains(neighbour))
				{
					neighbour.gValue = newMovementCostToNeighbour;
					neighbour.hValue = returnDistance(neighbour, targetNode);
					neighbour.previous = currentNode;
					
					if(!openSet.contains(neighbour))
					{
						openSet.add(neighbour);
						//neighbour.label = 'O';
					}	
				}		
			}	
		}
		return foundPath;
	}
	
	
	
	public static void createZones(Node[][] map)
	{
		int zoneLastHighest = 1;
		int zoneStart = 1;
		int zoneCounter = 1;
		int counterX = 1;
		int counterY = 1;
		
		//I is how many down
		for(int i = 0; i < map[0].length; i++)
		{
			//J is how many across
			for(int j = 0; j < map[0].length; j++)
			{

				if(counterX <= 9) 
				{
					map[i][j].zone = zoneCounter;
				}
				counterX++;
				
				if(counterX >= 9)
				{		
					counterX = 1;
					zoneCounter++;
				}
				
				
				//Track the highest number that the zone counter goes to in the inside loop
				if(zoneCounter > zoneLastHighest)
	            {
	                zoneLastHighest = zoneCounter;
	            }
				
				//End of inside loop
			}

			//Increase counterY as we've dropped down a row
			counterY++;
			
			if(counterY < 9)
			{
				zoneCounter = zoneStart;
				//System.out.println( i + " zone start " + zoneStart);
			}
			//If we've dropped down 9 rows then we want to change the zone number we assign
			if(counterY >=9)
			{	
				//Reset the counter
				counterY = 1;	
				
				zoneCounter = zoneLastHighest;
				zoneStart = zoneLastHighest;
				//System.out.println("I = " + i + " zone counter " + zoneCounter);
			}		
		}
	}
	
	
	
	public static void zones(Node[][] map)
	{
		int zoneLastHighest = 1;
		int zoneStart = 1;
		int zoneCounter = 1;
		int counterX = 0;
		int counterY = 0;
		int jLastNumber = 0;
		
		//I is how many down
		for(int i = 0; i < map[0].length; i++)
		{
			//J is how many across
			for(int j = 0; j < map[0].length; j++)
			{
				if(counterX <= map.length/2) 
				{
					map[i][j].zone = zoneCounter;
				}
				counterX++;
				
				
				if(counterX >= map.length/2)
				{		
					map[i][j].label = 'X';
					if(j+1 < map[0].length)
					{
						map[i][j+1].label = 'X';
					}
					counterX = 0;
					
					zoneCounter++;
					if(j == map.length - 1)
					{
						map[i][j].label = '~';
					}
				}
				
				
				//Track the highest number that the zone counter goes to in the inside loop
				if(zoneCounter > zoneLastHighest)
	            {
	                zoneLastHighest = zoneCounter;
	            }
				
				if(i == map[0].length/2-1)
				{
					map[i][j].label = 'X';
					if(i+1 < map[0].length)
					{
						map[i+1][j].label = 'X';
					}
					
					
				}
				//End of inside loop
			}

			//Increase counterY as we've dropped down a row
			counterY++;
			
			if(counterY < map[0].length/2)
			{
				zoneCounter = zoneStart;
			}
			//If we've dropped down the number of rows given then we want to change the zone number we assign
			if(counterY >=map[0].length/2)
			{		
				//Reset the counter
				counterY = 0;	
				
				zoneCounter = zoneLastHighest;
				zoneStart = zoneLastHighest;
			}		
			
		}	
	}
	
	
	
	
	
	
	
	
	public static void getConnectionNode()
	{
		//Go through the entire grid and select the nodes with a label 'X' which are connecting nodes and add them to a connecting node list 
		for(int i = 0; i < grid[0].length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				if(grid[i][j].label == 'X')
				{
					connectionNodes.add(grid[i][j]);
					grid[i][j].isConnectionNode = true;
				}
				
				
				
				if(j + 2 < grid[0].length)
				{
					
					if(grid[i][j].isConnectionNode == true && grid[i][j+1].isConnectionNode == true )
					{
						System.out.println("Called");
						
						/*
						//Track old label
						char oldLabelCurrent = grid[i][j].label;
						char oldLabelNeighbour = grid[i+1][j].label;
						
						//Set the label of the neighbour to 'n'
						grid[i][j].label = 'C'; 
						grid[i+1][j].label = 'N';
						
						//Print the grid to show where the neighbour is in relation to the current node
						printGrid();
						
						
						//reset the label to what it was previously
						grid[i+1][j].label = oldLabelNeighbour;
						grid[i][j].label = oldLabelCurrent;
						*/
					}
				}
				
				
			}
		}
	}

	
	public static void connectioNodeList()
	{
		
		
		System.out.println("Size " + connectionNodes.size());
		
		for(int i =0; i < connectionNodes.size(); i++)
		{
			connectionNodes.get(i).label = '~';
			
		}

		
		
	}

	public static ArrayList<Node> aStarSearch(Node startNode, Node goalNode)
	{
		ArrayList<Node> returnPath = new ArrayList<Node>();
		openSet.clear();
		closedSet.clear();
		openSet.add(startNode);
		
		while(openSet.size() > 0)
		{
			Node currentNode = openSet.get(0);
			
			
			//Getting the best node from the openset, first loop through there's only one to choose from which is the start node
			for(int i = 1; i < openSet.size(); i++)
			{
				//if two nodes have the same F score we pick the one which is closer to the goal, with the better H score
				if(openSet.get(i).fValue < currentNode.fValue || openSet.get(i).fValue == currentNode.fValue && openSet.get(i).hValue < currentNode.hValue)
				{
					currentNode = openSet.get(i);
				}
			}
			
			//remove the current node from the open set as we're now done with it and add current node to the closed set 
			openSet.remove(currentNode);
			closedSet.add(currentNode);
			//currentNode.label = 'C';
			
			//If it's the goal you know what to do
			if(currentNode == goalNode)
			{
				returnPath = returnPathList(startNode, goalNode);
				closedsetSize += closedSet.size();
				opensetSize += openSet.size();
				return returnPath;
			}
			
			
			//Loop through every neighbour of the current node 
			for(int i= 0; i < currentNode.neighbours.size(); i++)
			{
				Node neighbour = currentNode.neighbours.get(i);
				
				//Check the closed list to see if the neighbour is in that list, if so the loop breaks the current iteration and goes again 
				if(closedSet.contains(neighbour))
				{
					continue;
				}
				
				//Calculate the distance from the neighbour to the start 
				double newMovementCostToNeighbour = currentNode.gValue + returnDistance(currentNode, neighbour);
				
				//Check to see if distance from the start to the neighbour through the current node is a shorter distance than the G score of the neighbour currently 
				//Add check if the neighbour is in the open set
				if(newMovementCostToNeighbour < neighbour.gValue || !openSet.contains(neighbour))
				{
					neighbour.gValue = newMovementCostToNeighbour;
					neighbour.hValue = returnDistance(neighbour, goalNode);
					neighbour.previous = currentNode;
					
					if(!openSet.contains(neighbour))
					{
						openSet.add(neighbour);
						//neighbour.label = 'O';
					}	
				}		
			}	
		}		
		
		return null;
	}	
	
	
	
	
	public static Node aStarReturnNode(Node startNode, Node goalNode)
	{
		Node endNode = null;
		openSet.clear();
		closedSet.clear();
		openSet.add(startNode);
		
		while(openSet.size() > 0)
		{
			Node currentNode = openSet.get(0);
			
			
			//Getting the best node from the openset, first loop through there's only one to choose from which is the start node
			for(int i = 1; i < openSet.size(); i++)
			{
				//if two nodes have the same F score we pick the one which is closer to the goal, with the better H score
				if(openSet.get(i).fValue < currentNode.fValue || openSet.get(i).fValue == currentNode.fValue && openSet.get(i).hValue < currentNode.hValue)
				{
					currentNode = openSet.get(i);
				}
			}
			
			//remove the current node from the open set as we're now done with it and add current node to the closed set 
			openSet.remove(currentNode);
			closedSet.add(currentNode);
			//currentNode.label = 'C';
			
			//If it's the goal you know what to do
			if(currentNode == goalNode)
			{
				endNode = currentNode;
				//returnPath = returnPathList(startNode, goalNode);
				//closedSize += closedSet.size();
				//openSize += openSet.size();
				return endNode;
			}
			
			
			//Loop through every neighbour of the current node 
			for(int i= 0; i < currentNode.neighbours.size(); i++)
			{
				Node neighbour = currentNode.neighbours.get(i);
				
				//Check the closed list to see if the neighbour is in that list, if so the loop breaks the current iteration and goes again 
				if(closedSet.contains(neighbour))
				{
					continue;
				}
				
				//Calculate the distance from the neighbour to the start 
				double newMovementCostToNeighbour = currentNode.gValue + returnDistance(currentNode, neighbour);
				
				//Check to see if distance from the start to the neighbour through the current node is a shorter distance than the G score of the neighbour currently 
				//Add check if the neighbour is in the open set
				if(newMovementCostToNeighbour < neighbour.gValue || !openSet.contains(neighbour))
				{
					neighbour.gValue = newMovementCostToNeighbour;
					neighbour.hValue = returnDistance(neighbour, goalNode);
					neighbour.previous = currentNode;
					
					if(!openSet.contains(neighbour))
					{
						openSet.add(neighbour);
						//neighbour.label = 'O';
					}	
				}		
			}	
		}		
		
		return null;
	}	
}
	

    



