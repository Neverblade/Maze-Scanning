public class Point
{
	public int X;
	public int Y;
	
	public Point(int X, int Y)
	{
		this.X = X;
		this.Y = Y;
	}
	
	public void setX(int X)
	{
		this.X = X;
	}
	
	public void setY(int Y)
	{
		this.Y = Y;
	}
	
	public int getX()
	{
		return X;
	}
	
	public int getY()
	{
		return Y;
	}
	
	public String toString()
	{
		return "(" + this.Y + ", " + this.X + ")";
	}
}