/**
 * This class represents the smaller segments obtained from the larger
 * original segments, meant to decrease the search space and detect notes
 */
package omr;

import java.util.LinkedList;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class L1Token {
	private Integer x, y, width, height; //Inclusive rectangular token.
	//public Byte[][] SegmentArray;
	//private Integer AvgX, AvgY;
	private LinkedList<Pair<Integer, Integer>> yRLE;
	//private LinkedList<Glyph> glyphs;
	private StaffSet staff;
	boolean hasNote;
	
	// contains the list of SubTokens for further processing
	private LinkedList<L2Token> l2Tokens;	
	
	public L1Token(int x, int width, int y, int height, StaffSet staff, boolean hasNote)
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
	
	public StaffSet getStaff()
	{
		return staff;
	}
	
	/**
	 * Retrieves all Subtokens within the Token for further processing
	 * @param image
	 * @return
	 */
	public void processToken(BufferedImage image, TokenDetector detector)
	{
		// first copy the image as it will be modified to remove runs greater than 2*lineThickness + spaceThickness
		ImageLoader imageLoader = new ImageLoader();
		BufferedImage tempImage = imageLoader.DeepCopy(image);
		// if the section has a notehead, create a token of same height with the width of the note head
		l2Tokens = detector.retrieveL2Tokens(this);
	}
	
	
	
	public LinkedList<L2Token> getSubTokens()
	{
		return l2Tokens;
	}
}
