import java.util.*;
import java.io.*;
import java.awt.*;

public class GrowingTree //the maze map itself!
{
	/*
	W S E N
	0 1 2 3
	*/
	
	public Cell[][] grid; //most important, this is where all the data is
	public int height; //height of your grid (y)
	public int width; //width of your grid (x)
	public static int[] cX = {-1, 0, 1, 0}; //change in x depending on direction selected
	public static int[] cY = {0, 1, 0 ,-1}; //change in y depending on direction selected
	public int size;
	public boolean prog;
	public int speed;
	
	public GrowingTree(Cell[][] grid, int size, boolean prog, int speed)
	{
		this.grid = grid;
		this.height = grid.length;
		this.width = grid[0].length;
		this.size = size;
		this.prog = prog;
		this.speed = speed;
	}
	
	
	//use depth-first search to solve the maze
	public void solve(int height, int width, int xC, int xS, int yC, int yS)
	{
		//create the panel to draw on first
		DrawingPanel panel = new DrawingPanel((grid[0].length*5 + 10)*size, (grid.length*5 + 10)*size);
		Graphics g = panel.getGraphics();
		printMaze(panel, g);
		
		//clear the boolean goThroughs
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				grid[i][j].goOut();
			}
		}
		
		//create the arrays for history
		ArrayList<Integer> xHist = new ArrayList<Integer>();
		ArrayList<Integer> yHist = new ArrayList<Integer>();
		
		//add the starts to the history list/solution list
		xHist.add(xC);
		yHist.add(yC);
		grid[yC][xC].goThrough();
		grid[yC][xC].isSolution();
		
		//paint it red if prog
		if (prog)
		{
			g.setColor(Color.RED);
			g.fillRect((5*xC + 4)*size, (5*yC + 4)*size, 3*size, 3*size);
		}
		
		//do the magic
		do
		{
			//possible choice list
			ArrayList<Integer> pC = new ArrayList<Integer>();
			
			//check which of the 4 possible directions are valid
			for (int i = 0; i < 4; i++)
			{
				//if it doesn't go out of bounds...
				if (xC+cX[i] < width && xC+cX[i] >= 0 && yC+cY[i] < height && yC+cY[i] >= 0)
				{
					//if the neighboring cell of the direction hasn't been gone through, and there's no border...
					//and there are no walls in the way...
					if (!grid[yC+cY[i]][xC+cX[i]].getBeenThrough() && !grid[yC][xC].getBorder(i) && !grid[yC][xC].getWall(i))
					{
						//add it to the possible directions
						pC.add(i);
					}
				}
			}
			
			//if there are no possible directions to go, backtrack
			if (pC.size() == 0)
			{
				//remove your current position from the history
				xHist.remove(xHist.size()-1);
				yHist.remove(yHist.size()-1);
				
				//remove it's solution status
				grid[yC][xC].notSolution();
				
				//color it gray if prog
				if (prog)
				{
					g.setColor(Color.GRAY);
					g.fillRect((5*xC + 4)*size, (5*yC + 4)*size, 3*size, 3*size);
				}
				
				//update your position to your previously visited position
				if (xHist.size() == 0)
				{
					System.out.println("Unsolvable maze. Trying changing the sensitivity.\nAbandoning solution...");
					return;
				}
				xC = xHist.get(xHist.size()-1);
				yC = yHist.get(yHist.size()-1);
			}
			
			//if there are possible directions, travel to one
			else
			{
				//get a random direction
				Random r = new Random();
				int d = pC.get(r.nextInt(pC.size()));
				
				//go to the new cell
				xC += cX[d];
				yC += cY[d];
				grid[yC][xC].goThrough();
				
				//update the history
				xHist.add(xC);
				yHist.add(yC);
				
				//give it solution status
				grid[yC][xC].isSolution();
				
				//paint it red if prog
				if (prog)
				{
					g.setColor(Color.RED);
					g.fillRect((5*xC + 4)*size, (5*yC + 4)*size, 3*size, 3*size);
				}
			}
			
			if (prog) try{Thread.sleep(300 - 50*speed);} catch (InterruptedException e){e.printStackTrace();}
		} while (xC != xS || yC != yS);
		if (!prog) printMaze(panel, g);
	}
	
	//new and improved maze printing
	public void printMaze(DrawingPanel panel, Graphics g)
	{
		g.setColor(Color.BLACK);
		for (int i = 0; i < grid.length; i++)
		{
			for (int j = 0; j < grid[0].length; j++)
			{
				//these represent the center of each square
				int x = (5 + j*5)*size;
				int y = (5 + i*5)*size;
				if (grid[i][j].getWall(0)) g.drawLine(x-2*size, y-2*size, x-2*size, y+3*size);
				if (grid[i][j].getWall(1)) g.drawLine(x-2*size, y+3*size, x+3*size, y+3*size);
				if (grid[i][j].getWall(2)) g.drawLine(x+3*size, y-2*size, x+3*size, y+3*size);
				if (grid[i][j].getWall(3)) g.drawLine(x-2*size, y-2*size, x+3*size, y-2*size);
				if (!prog && grid[i][j].getSolution())
				{
					g.setColor(Color.RED);
					g.fillRect((5*j + 4)*size, (5*i + 4)*size, 3*size, 3*size);
					g.setColor(Color.BLACK);
				}
			}
		}
	}
}
		