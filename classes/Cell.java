import java.util.*;
import java.io.*;

public class Cell
{
	//a cell is one space in the maze grid; it has borders, walls, and a boolean to check whether it's been passed through before
	//order of all directional arrays: W S E N for 0 1 2 3 (% 4 for stuffs)
	
	//true = is up
	public boolean[] wall;
	public boolean[] border;
	public boolean beenThrough;
	public boolean solution;
	
	public Cell()
	{
		wall = new boolean[] {true, true, true, true};
		border = new boolean[] {false, false, false, false};
		beenThrough = false;
		solution = false;
	}
	
	public Cell(boolean[] wall, boolean[] border)
	{
		this.wall = wall;
		this.border = border;
		beenThrough = false;
		solution = false;
	}
	
	public boolean getBeenThrough()
	{
		return this.beenThrough;
	}
	
	public boolean getWall(int x) //return true if there's a wall
	{
		return this.wall[x];
	}
	
	public boolean getBorder(int x) //returns true if it's a border
	{
		return this.border[x];
	}
	
	public void removeWall(int x)
	{
		this.wall[x] = false;
	}
	
	public void raiseBorder(int x)
	{
		this.border[x] = true;
	}
	
	public void goThrough()
	{
		this.beenThrough = true;
	}
	
	public void isSolution()
	{
		this.solution = true;
	}
	
	public void goOut()
	{
		this.beenThrough = false;
	}
	
	public void notSolution()
	{
		this.solution = false;
	}
	
	public boolean getSolution()
	{
		return this.solution;
	}
}