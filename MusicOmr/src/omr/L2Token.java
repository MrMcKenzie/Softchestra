package omr;

import java.util.LinkedList;

public class L2Token {
	private Integer x, y, width, height; //Inclusive rectangular token.
	private LinkedList<Pair<Integer, Integer>> yRLE;
	private StaffSet staff;
	boolean hasNote;

	public L2Token(int x, int width, int y, int height, StaffSet staff, boolean hasNote)
	{
		this.x = x;
		this.width = width;
		this.y = y;
		this.height = height;
		this.staff = staff;
		this.hasNote = hasNote;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getHeight()
	{
		return height;
	}
}
