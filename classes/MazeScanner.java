/*
This program takes in a picture of a maze as input and attempts to translate it into computer readable form.
It then solves the maze and draw the solved version as output.

It works in three parts:
	1. Using the Picture and Pixel classes from the Pix Lab to input and read the maze picture.
	2. Using a depth-first search recursion algorithm to solve the maze.
	3. Using the graphic classes provided by the textbook to draw and output the finished maze.
*/

import java.util.*;
import java.io.*;
import java.awt.*;

public class MazeScanner
{
	private static Pixel[][] pic;
	private static int w, h;
	private static Point[] q = new Point[4]; //0: nw, 1: ne, 2: sw, 3: se
	private static Pixel white;
	//grids contains the vertex grid points
	private static ArrayList<Integer> xGrid = new ArrayList<Integer>(); //start of a black line
	private static ArrayList<Integer> yGrid = new ArrayList<Integer>();
	private static ArrayList<Integer> xGrid2 = new ArrayList<Integer>(); //end of a black line
	private static ArrayList<Integer> yGrid2 = new ArrayList<Integer>();
	private static int cSen; //color
	private static int lSen; //line
	private static int size; //size of output screen
	private static int oSen; //offset
	private static boolean prog; //whether or not to show the progress
	private static int speed; //how fast to run the drawing
	
	public static void main(String[] args) throws Exception
	{
		//ask for input
		Scanner sc = new Scanner(System.in);
		System.out.println("Input a maze picture and I'll try to solve it.");
		System.out.print("File: ");
		//load the picture	
		Picture picture = new Picture(sc.next());
		System.out.println("Reading picture...");
		pic = picture.getPixels2D();
		h = pic.length;
		w = pic[0].length;
		white = pic[0][0];
		
		//get size
		System.out.println("Size is how large your output screen will be.");
		do
		{
			System.out.print("Size? (1-5): ");
			while (!sc.hasNextInt())
			{
				System.out.println("Not valid, try again.");
				System.out.print("Size? (1-5): ");
				sc.next();
			}
			size = sc.nextInt();
			if (size < 1 || size > 5) System.out.println("Not valid, try again.");
		} while (size < 1 || size > 5);
		System.out.println();

		//get line sensitivity
		System.out.println("Line sensitivity is used to adjust for how thick the walls of the maze are.");
		System.out.println("If you aren't picking up all the walls, change the sensitivity.\nDefault Setting: 20");
		do
		{
			System.out.print("Line Sensitivity? (1-100): ");
			while (!sc.hasNextInt())
			{
				System.out.println("Not valid, try again.");
				System.out.print("Line Sensitivity? (1-100): ");
				sc.next();
			}
			lSen = sc.nextInt();
			if (lSen < 1 || lSen > 100) System.out.println("Not valid, try again.");
		} while (lSen < 1 || lSen > 100);
		System.out.println();

		//get color sensitivity
		System.out.println("Color sensitivity is used to adjust for how dark the walls are.");
		System.out.println("For mazes with markings or blemishes, use a higher sensitivity.\nDefault Setting: 200");
		do
		{
			System.out.print("Color Sensitivity? (0-500): ");
			while (!sc.hasNextInt())
			{
				System.out.println("Not valid, try again.");
				System.out.print("Color Sensitivity? (0-500): ");
				sc.next();
			}
			cSen = sc.nextInt();
			if (cSen < 0 || cSen > 500) System.out.println("Not valid, try again.");
		} while (cSen < 0 || cSen > 500);
		System.out.println();
		
		//get offset sensitivity
		System.out.println("Offset sensitivity is used to adjust for not completely straight lines.");
		System.out.println("For more badly drawn mazes, use higher offset sensitivity.\nDefault Setting: 2");
		do
		{
			System.out.print("Offset Sensitivity? (0-5): ");
			while (!sc.hasNextInt())
			{
				System.out.println("Not valid, try again.");
				System.out.print("Offset Sensitivity? (0-5): ");
				sc.next();
			}
			oSen = sc.nextInt();
			if (oSen < 0 || oSen > 5) System.out.println("Not valid, try again.");
		} while (oSen < 0 || oSen > 5);
		System.out.println();		
		
		//get progress boolean
		do
		{
			System.out.print("Show progress? (Y/N): ");
			String med = sc.next().toUpperCase();
			if (med.equals("YES") || med.equals("Y"))
			{
				prog = true;
				break;
			}
			else if (med.equals("NO") || med.equals("N"))
			{
				prog = false;
				break;
			}
			else System.out.println("Not valid, try again.");
		} while (true);
		System.out.println();
		
		//if prog true, get speed
		if (prog)
		{
			System.out.println("Speed dictates how fast the program runs through the maze.\nDefault Setting: 3");
			do
			{
				System.out.print("Speed? (1-5): ");
				while (!sc.hasNextInt())
				{
					System.out.println("Not valid, try again.");
					System.out.print("Speed? (1-5): ");
					sc.next();
				}
				speed = sc.nextInt();
				if (speed < 1 || speed > 5) System.out.println("Not valid, try again.");
			} while (speed < 1 || speed > 5);
			System.out.println();
		}
		
		//find the four corner points
		int medX1 = 0, medY1 = 0; //find nw
		while (checkWhite(pic[medY1][medX1], cSen))
		{
			medX1++;
			if (medX1 == w)
			{
				medX1 = 0;
				medY1++;
			}
		}
		q[0] = new Point(medX1, medY1);
		medX1 = w - 1; //find ne
		while (checkWhite(pic[medY1][medX1], cSen)) medX1--;
		q[1] = new Point(medX1, medY1);
		medY1 = h - 1; //find se
		while (checkWhite(pic[medY1][medX1], cSen)) medY1--;
		q[2] = new Point(medX1, medY1);
		medX1 = 0; //find sw
		while (checkWhite(pic[medY1][medX1], cSen)) medX1++;
		q[3] = new Point(medX1, medY1);
		
		System.out.println("Height: " + h + " Width: " + w);
		System.out.println("Maze Corners at: " + q[0] + ", " + q[1] + ", " + q[2] + ", " + q[3]);
		System.out.println("Scanning y axis for horizontal walls...");
				
		//top-bottom scan, check horizontal lines and mark grid points
		boolean onOffC = false; //true means we're currently on black, false means we're currently on white
		for (int i = q[0].getY() - 1; i <= q[2].getY(); i++)
		{
			boolean c = blackCheck(q[0].getX(), i, q[2].getX(), i, lSen);
			if (c && !onOffC)
			{
				yGrid.add(i);
				onOffC = true;
			} else if (!c && onOffC)
			{
				onOffC = false;
				yGrid2.add(i);
			}
		}
		
		System.out.println("Scanning x axis for vertical walls...");
		
		//left-right scan, check vertical lines and mark grid points
		onOffC = false;
		for (int i = q[0].getX() - 1; i <= q[2].getX(); i++)
		{
			boolean c = blackCheck(i, q[0].getY(), i, q[2].getY(), lSen);
			if (c && !onOffC)
			{
				xGrid.add(i);
				onOffC = true;
			} else if (!c && onOffC)
			{
				onOffC = false;
				xGrid2.add(i);
			}
		}
		
		if (yGrid.size() <= 2 || xGrid.size() <= 2)
		{
			System.out.println("Could not detect a valid maze. Try changing the sensitivity.\nForce quitting...");
			System.exit(0);
		}
		
		System.out.println("Found " + yGrid.size() + " horizontal walls and " + xGrid.size() + " vertical walls.");
		
		//create the default cell grid to work with
		Cell[][] grid = new Cell[yGrid.size() - 1][xGrid.size() - 1];
		
		for (int i = 0; i < grid.length; i++)
		{
			for (int j = 0; j < grid[0].length; j++)
			{
				grid[i][j] = new Cell(); //all walls up initially
			}
		}
		for (int i = 0; i < grid.length; i++) //put borders up
		{
			grid[i][0].raiseBorder(0);
			grid[i][grid[i].length - 1].raiseBorder(2);
		}
		for (int i = 0; i < grid[0].length; i++)
		{
			grid[0][i].raiseBorder(3);
			grid[grid.length - 1][i].raiseBorder(1);
		}		
		
		System.out.println("Checking individual grid spaces for walls...");
		
		//do the reading, update the cells when needed
		for (int i = 1; i < yGrid.size(); i++)
		{
			for (int j = 1; j < xGrid.size(); j++)
			{
				//test the east face (north from the vertex) and north face (west from the vertex)
				boolean c1 = true;
				boolean c2 = true;
				for (int k = -1*oSen; k <= oSen; k++)
				{
					if (!spaceCheck(xGrid.get(j)+k, yGrid2.get(i-1), xGrid.get(j)+k, yGrid.get(i))) c1 = false;
					if (!spaceCheck(xGrid2.get(j-1), yGrid.get(i)+k, xGrid.get(j), yGrid.get(i)+k)) c2 = false;
				}
				if (c1)
				{
					grid[i-1][j-1].removeWall(2);
					if (j < grid[0].length) grid[i-1][j].removeWall(0);
				}
				if (c2)
				{
					grid[i-1][j-1].removeWall(1);
					if (i < grid.length) grid[i][j-1].removeWall(3);
				}
			}
		}
		
		System.out.println("Searching for start and end points...");
		
		//find the start and end points by going through all four outer walls
		int xC = -1, yC = -1, xS = -1, yS = -1;
		for (int i = 1; i < yGrid.size(); i++) //left and right walls
		{
			int yTest = (yGrid.get(i) + yGrid.get(i-1)) / 2;
			if (spaceCheck(xGrid.get(0), yGrid2.get(i-1), xGrid.get(0), yGrid.get(i))) //left wall
			{
				if (xC == -1) //we'll take the first one we find as the start points
				{
					xC = 0;
					yC = i - 1;
					grid[yC][xC].removeWall(0);
				} else
				{
					xS = 0;
					yS = i - 1;
					grid[yS][xS].removeWall(0);
				}
			}
			if (spaceCheck(xGrid.get(xGrid.size()-1), yGrid2.get(i-1), xGrid.get(xGrid.size()-1), yGrid.get(i))) //right wall
			{
				if (xC == -1)
				{
					xC = xGrid.size()-2;
					yC = i - 1;
					grid[yC][xC].removeWall(2);
				} else
				{
					xS = xGrid.size()-2;
					yS = i - 1;
					grid[yS][xS].removeWall(2);
				}
			}
		}
		for (int i = 1; i < xGrid.size(); i++)
		{
			int xTest = (xGrid.get(i) + xGrid.get(i-1)) / 2;
			if (spaceCheck(xGrid2.get(i-1), yGrid.get(0), xGrid.get(i), yGrid.get(0))) //north wall
			{
				//System.out.println("Found at: " + xGrid2.get(i-1) + ", " + yGrid.get(0) + " to " + xGrid.get(i) + ", " + yGrid.get(0));
				if (xC == -1)
				{
					xC = i - 1;
					yC = 0;
					grid[yC][xC].removeWall(3);
				} else
				{
					xS = i - 1;
					yS = 0;
					grid[yS][xS].removeWall(3);
				}
			}
			if (spaceCheck(xGrid2.get(i-1), yGrid.get(yGrid.size()-1), xGrid.get(i), yGrid.get(yGrid.size()-1))) //south wall
			{
				if (xC == -1)
				{
					xC = i - 1;
					yC = yGrid.size()-2;
					grid[yC][xC].removeWall(1);
				} else
				{
					xS = i - 1;
					yS = yGrid.size()-2;
					grid[yS][xS].removeWall(1);
				}				
			}
		}
		
		System.out.println("Found start coordinates on the grid at: (" + yC + ", " + xC + ")");
		System.out.println("Found end coordinates on the grid at: (" + yS + ", " + xS + ")");
		
		//if there is no start or end, set defaults
		if (xC == -1)
		{
			System.out.println("Could not find start coordinates, setting defaults...");
			xC = 0;
			yC = 0;
		}
		if (xS == -1)
		{
			System.out.println("Could not find end coordinates, setting defaults...");
			xS = grid[0].length - 1;
			yS = grid.length - 1;
		}
		
		//finally, create the object
		System.out.println("Generating maze...");
		GrowingTree maze = new GrowingTree(grid, size, prog, speed);
		System.out.println("Attempting to solve maze...");
		System.out.println("Printing maze...");
		maze.solve(grid.length, grid[0].length, xC, xS, yC, yS);
	}
	
	//takes the area between the two given coordinates and returns whether or not it's significantly black
	public static boolean blackCheck(int x1, int y1, int x2, int y2, int lSen)
	{
		int whiteCheck = 0;
		int blackCheck = 0;
		for (int i = y1; i <= y2; i++)
		{
			for (int j = x1; j <= x2; j++)
			{
				if (checkWhite(pic[i][j], cSen)) whiteCheck++;
				else blackCheck++;
			}
		}
		return (blackCheck*lSen > whiteCheck*10);
	}
	
	//checks for white space in between two given coordinates
	//returns true for yes space, false if there's a wall there
	public static boolean spaceCheck(int x1, int y1, int x2, int y2)
	{
		int count = 0;
		if (x1 == x2)
		{
			for (int i = y1; i <= y2; i++)
			{
				if (checkWhite(pic[i][x1], cSen)) count++;
			}
			return (count*10 > y2 - y1);
		} else
		{
			for (int j = x1; j <= x2; j++)
			{
				if (checkWhite(pic[y1][j], cSen)) count++;
			}
			return (count*10 > x2 - x1);
		}
	}
		
	//tests for whether or not this color is significantly different from white
	public static boolean checkWhite(Pixel p, int cSen) //true if it's same, false if it's different
	{
		int x = (int) Math.abs(p.getRed() - white.getRed());
		x += (int) Math.abs(p.getGreen() - white.getGreen());
		x += (int) Math.abs(p.getBlue() - white.getBlue());
		if (x < cSen) return true;
		else return false;
	}
}