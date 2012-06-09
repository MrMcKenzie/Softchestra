package omr;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

public class L0Token {
	
		
	public Integer X0, Y0, X1, Y1; //Inclusive rectangular token.
	public Byte[][] SegmentArray;
	public Integer AvgX, AvgY;
	private StaffSet parentStaff;
	
	// contains the list of SubTokens for further processing
	private LinkedList<L1Token> subTokens;
	
	public L0Token()
	{
	}
	
	/**
	 * Sets the staff which contains this token
	 * @param staff
	 */
	public void setStaff(StaffSet staff)
	{
		this.parentStaff = staff;
	}
	
	/**
	 * Gets the parent staff which contains this token
	 */
	public StaffSet getStaff()
	{
		return parentStaff;
	}

	
	public HashSet<Point> CreateToken(BufferedImage image, Integer x, Integer y)
	{
		HashSet<Point> pointList = new HashSet<Point>();
		pointList = MapToken(image, x, y, pointList);
		
		List<Integer> xList = new ArrayList<Integer>();
		List<Integer> yList = new ArrayList<Integer>();
		
		Enumeration<Point> e = Collections.enumeration(pointList);
		while (e.hasMoreElements())
		{
			Point point = e.nextElement();
			xList.add(point.x);
			yList.add(point.y);
		}
		
		X0 = Collections.min(xList);
		Y0 = Collections.min(yList);
		X1 = Collections.max(xList);
		Y1 = Collections.max(yList);
		AvgX = (X0 + X1) / 2;
		AvgY = (Y0 + Y1) / 2;
		
		Integer arrayWidth = X1 - X0 + 1;
		Integer arrayHeight = Y1 - Y0 + 1;
		SegmentArray = new Byte[arrayWidth][arrayHeight];
		for (int i = 0; i < arrayWidth; i++)
		{
			for (int j = 0; j < arrayHeight; j++)
			{
				SegmentArray[i][j] = 0;
			}
		}
		
		for (int i = 0; i < pointList.size(); i++)
		{
			SegmentArray[xList.get(i) - X0][yList.get(i) - Y0] = 1;
		}
		
		return pointList;
	}
	
	private HashSet<Point> MapToken(BufferedImage image, Integer x, Integer y, HashSet<Point> list)
	{
		Point newPoint = new Point(x, y);
		if (!list.contains(newPoint) && image.getRGB(x, y) == 0xFF000000)
		{
			list.add(newPoint);
			for (int row = -1; row < 2; row++)
			{
				for (int col = -1; col < 2; col++)
				{
					if (x + row < 0 || x + row >= image.getWidth())
						continue;
					if (y + col < 0 || y + col >= image.getHeight())
						continue;
//					if (row == 0 && col == 0 || (row != 0 && col != 0)) //Segmentation algo
//						continue;
					if (row == 0 && col == 0)
					continue;
					if (!list.contains(new Point(x + row, y + col)))
					{
						MapToken(image, x + row, y + col, list);
					}
				}
			}
		}
		return list;
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
		subTokens = detector.retrieveL1Tokens(this);
	}
	
	public LinkedList<L1Token> getSubTokens()
	{
		return subTokens;
	}
	
	
}
