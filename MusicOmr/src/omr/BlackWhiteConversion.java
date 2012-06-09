package omr;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class BlackWhiteConversion {
	
	public BlackWhiteConversion(){}
	
	public BufferedImage GetGrayScaleImage(BufferedImage image)
	{
		BufferedImage imageGrayScale = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = image.getGraphics();  
		g.drawImage(image, 0, 0, null);  
		g.dispose(); 
		return imageGrayScale;
	}
	public BufferedImage GetBlackWhiteImage(BufferedImage image)
	{
		int height = image.getHeight();
		int width = image.getWidth();
		Color white = new Color(255, 255, 255);
		Color black = new Color(0, 0, 0);
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				Color c = new Color(image.getRGB(j, i), true);
				Color toSet = ((c.getBlue() + c.getGreen() + c.getRed()) / 3) > 127 ? white : black;
				image.setRGB(j, i, toSet.getRGB());
			}
		}
		return image;
		
	}
}
