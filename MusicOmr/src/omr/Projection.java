package omr;

import java.awt.image.BufferedImage;
import java.util.*;

public class Projection {
	
	private BufferedImage image;
	
	public Projection(BufferedImage image)
	{
		this.image = image;
	}
	
	public List<Integer> GetYProjection(int xStart, int xEnd, int yStart, int yEnd)
	{
		if (!rectangleInImage(xStart, xEnd, yStart, yEnd))
		{
			return null;
		}
		List<Integer> results = new ArrayList<Integer>();
		for (int i = 0; i < yEnd - yStart + 1; i++)
			results.add(0);
		for (int i = yStart; i <= yEnd; i++)
		{
			for (int j = xStart; j <= xEnd; j++)
			{
				if (image.getRGB(j, i) == 0xFF000000)
					results.set(i, results.get(i) + 1);
			}
		}
		return results;
	}
	
	public int[] GetXProjection(int xStart, int xEnd, int yStart, int yEnd)
	{
		if (!rectangleInImage(xStart, xEnd, yStart, yEnd))
		{
			return null;
		}
		int[] results = new int[yEnd - yStart + 1];
		for (int i = xStart; i <= xEnd; i++)
		{
			for (int j = yStart; j <= yEnd; j++)
			{
				if (image.getRGB(i, j) == 0xFF000000)
					results[i]++;
			}
		}
		return results;
	}
	
	private boolean rectangleInImage(int xStart, int xEnd, int yStart, int yEnd)
	{
		if (xEnd < xStart || yEnd < yStart || !coordinateInImage(xStart, yStart) || !coordinateInImage(xEnd, yEnd))
		{
			return false;
		}
		else
			return true;
	}
	
	private boolean coordinateInImage(int x, int y)
	{
		return x > -1 && x < image.getWidth() && y > -1 && y < image.getHeight();
	}
}

//class ValueComparator implements Comparator {
//
//	Map base;
//	public ValueComparator(Map base) {
//		this.base = base;
//	}
//
//	public int compare(Object a, Object b) {
//
//		if((Double)base.get(a) < (Double)base.get(b)) {
//			return 1;
//		} else if((Double)base.get(a) == (Double)base.get(b)) {
//			return 0;
//		} else {
//			return -1;
//		}
//	}
//}
