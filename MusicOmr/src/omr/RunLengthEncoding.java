package omr;

import java.awt.image.BufferedImage;
import java.util.*;

public class RunLengthEncoding {
	
	private BufferedImage image;
	
	public RunLengthEncoding(BufferedImage image)
	{
		this.image = image;
	}
	
	public List<Integer> GetVerticalRLE(int rgbMatch)
	{
		List<Integer> rle = new ArrayList<Integer>();
		for (int i = 0; i < image.getHeight(); i++)
			rle.add(0);
		int curMatched;
		for (int i = 0; i < image.getWidth(); i++)
		{
			curMatched = 0;
			for (int j = 0; j < image.getHeight(); j++)
			{
				if (image.getRGB(i, j) == rgbMatch)
				{
					curMatched++;
				}
				else
				{
					if (curMatched > 0)
					{
						rle.set(curMatched, rle.get(curMatched) + 1);
						curMatched = 0;
					}
				}
			}
		}
		return rle;
	}
}
