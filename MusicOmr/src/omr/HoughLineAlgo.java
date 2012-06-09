package omr;

import java.awt.image.BufferedImage;

public class HoughLineAlgo {
	
	public HoughLineAlgo()
	{
	
	}
	
	public BufferedImage HighlightHorizontalLines(BufferedImage image)
	{
		int height = image.getHeight();
		int width = image.getWidth();
		int diagonal = width + height;
		int[][] houghArray = new int[height][diagonal + 1];
		
		for (int h = 0; h < height; h++)
		{
			double hypotenuse = Math.sqrt(Math.pow(width, 2) + Math.pow(h, 2));
			for (int i = 0; i < width; i++)
			{
				for (int j = 0; j < height; j++)
				{
					//r = x * cos(theta) + y * sin(theta)
					if (image.getRGB(i, j) == 0xFF000000)
					{
						int r = (int)Math.round(i * width / hypotenuse + j * h / hypotenuse);
						houghArray[h][r]++;
					}
				}
			}
		}
		
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j <= diagonal; j++)
			{
				if (houghArray[i][j] > 100)
				{
					double hypotenuse = Math.sqrt(Math.pow(width, 2) + Math.pow(i, 2));
					int x = (int)Math.round(j * i / hypotenuse);
					int y = (int)Math.round(j * width / hypotenuse);
					System.out.println(x + "\t" + y + "\t" + width + "\t" + i);
					//image = DrawLine(image, x, y, width, i);
				}
			}
		}
		
		return image;
	}
	
	public BufferedImage DrawLine(BufferedImage image, int x, int y, int dx, int dy)
	{
		int gcd = GCD(dx, dy);
		int height = image.getHeight();
		int width = image.getWidth();
		if (gcd != 0)
		{
			dx = dx / gcd;
			dy = dy / gcd;
		}
		image.setRGB(x, y, 0xFFFF0000);
		int xCount, yCount;
		int xCur, yCur;
		
		xCur = x;
		yCur = y;
		xCount = 0;
		yCount = 0;
		while (x > -1 && x < width && y > -1 && y < height)
		{
			if (xCount == 0 && yCount == 0)
			{
				xCount = dx;
				yCount = dy;
			}
			else
			{
				if (dy < dx)
				{
					while (xCount > 0 && x > -1 && x < width)
					{
						image.setRGB(xCur, yCur, 0xFFFF0000);
						xCur--;
						xCount--;
					}
					while (yCount > 0 && y > -1 && y < height)
					{
						image.setRGB(xCur, yCur, 0xFFFF0000);
						yCur--;
						yCount--;
					}
				}
				else
				{
					while (yCount > 0 && y > -1 && y < height)
					{
						image.setRGB(xCur, yCur, 0xFFFF0000);
						yCur--;
						yCount--;
					}
					while (xCount > 0 && x > -1 && x < width)
					{
						image.setRGB(xCur, yCur, 0xFFFF0000);
						xCur--;
						xCount--;
					}
				}
			}
			
		}
		
		dx = -dx;
		dy = -dy;
		
		xCur = x;
		yCur = y;
		xCount = 0;
		yCount = 0;
		while (x > -1 && x < width && y > -1 && y < height)
		{
			if (xCount == 0 && yCount == 0)
			{
				xCount = dx;
				yCount = dy;
			}
			else
			{
				if (dy < dx)
				{
					while (xCount > 0 && x > -1 && x < width)
					{
						image.setRGB(xCur, yCur, 0xFFFF0000);
						xCur--;
						xCount--;
					}
					while (yCount > 0 && y > -1 && y < height)
					{
						image.setRGB(xCur, yCur, 0xFFFF0000);
						yCur--;
						yCount--;
					}
				}
				else
				{
					while (yCount > 0 && y > -1 && y < height)
					{
						image.setRGB(xCur, yCur, 0xFFFF0000);
						yCur--;
						yCount--;
					}
					while (xCount > 0 && x > -1 && x < width)
					{
						image.setRGB(xCur, yCur, 0xFFFF0000);
						xCur--;
						xCount--;
					}
				}
			}
		}
		return image;
	}
	
	public int GCD(int a, int b)
	{
	   if (b==0) return a;
	   return GCD(b,a%b);
	}
}
