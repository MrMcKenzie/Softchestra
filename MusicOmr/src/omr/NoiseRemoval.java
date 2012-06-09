package omr;

import java.awt.image.BufferedImage;

public class NoiseRemoval {

	private BufferedImage image;
	
	public NoiseRemoval(BufferedImage image)
	{
		this.image = image;
	}
	
	public BufferedImage RemoveNoise()
	{
		for (int i = 0; i < image.getWidth(); i++)
		{
			for (int j = 0; j < image.getHeight(); j++)
			{
				if (image.getRGB(i, j) == 0xFF000000)
				{
					Integer surroundCount = 0;
					for (int row = -1; row < 2; row++)
					{
						for (int col = -1; col < 2; col++)
						{
							if (i + row < 0 || i + row >= image.getWidth())
								break;
							if (j + col < 0 || j + col >= image.getHeight())
								break;
							if (image.getRGB(i + row, j + col) == 0xFF000000)
							{
								surroundCount++;
							}
						}
					}
					if (surroundCount == 1)
						image.setRGB(i, j, 0xFFFFFFFF);
				}
			}
		}
		return image;
	}
}
