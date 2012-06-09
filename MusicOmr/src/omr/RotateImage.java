package omr;

import java.awt.image.BufferedImage;

public class RotateImage {

	
	public RotateImage()
	{
	}
	
	public BufferedImage Rotate(BufferedImage image, double rotationAngle)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		double cos = Math.cos(rotationAngle);
		double sin = Math.sin(rotationAngle);
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < width; i ++)
		{
			for (int j = 0; j < height; j++)
			{
				newImage.setRGB(i, j, 0xFFFFFFFF);
			}
		}
		for (int i = 0; i < width; i ++)
		{
			for (int j = 0; j < height; j++)
			{
				if (image.getRGB(i, j) == 0xFF000000)
				{
					int x = (int)Math.round(i * cos - j * sin);
					int y = (int)Math.round(x * sin + j * cos);
					if (x > -1 && x < width && y > -1 && y < height)
						newImage.setRGB(x, y, 0xFF000000);
				}
			}
		}
		return newImage;
	}
	
}
