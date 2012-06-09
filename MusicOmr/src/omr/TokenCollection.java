package omr;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.*;

public class TokenCollection {

//	public HashMap<Point, List<Integer>> TokenIndices;
	public HashMap<Point, Integer> TokenIndices;
//	public HashMap<Point, List<Integer>> RectangularTokenIndices;
	public List<L0Token> Tokens;
	
	public TokenCollection(BufferedImage image)
	{
//		TokenIndices = new HashMap<Point, List<Integer>>();
		TokenIndices = new HashMap<Point, Integer>();
//		RectangularTokenIndices = new HashMap<Point, List<Integer>>();
		for (int i = 0; i < image.getWidth(); i++)
		{
			for (int j = 0; j < image.getHeight(); j++)
			{
				Point point = new Point(i, j);
				TokenIndices.put(point, -1);
//				TokenIndices.put(point, new ArrayList<Integer>());
//				RectangularTokenIndices.put(point, new ArrayList<Integer>());
			}
		}
		Tokens = new ArrayList<L0Token>();
	}
	
	public void AddCharToken(L0Token token)
	{
		Integer index = Tokens.size();
		for (int i = token.X0; i < token.X1 + 1; i++)
		{
			for (int j = token.Y0; j < token.Y1 + 1; j++)
			{
				Point p = new Point(i, j);
//				RectangularTokenIndices.get(p).add(index);
				if (token.SegmentArray[i - token.X0][j - token.Y0] == 1)
				{
//					TokenIndices.get(p).add(index);
					TokenIndices.put(p, index);
				}
			}
		}
		Tokens.add(token);
	}
}
