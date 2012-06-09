package omr;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ResampleBufferedImage {
	
	public ResampleBufferedImage(){}
	
	public BufferedImage ScaleBlackWhiteImage(BufferedImage img, int multiplier,
	        Color background) {
	    int imgWidth = img.getWidth() / multiplier;
	    int imgHeight = img.getHeight() / multiplier;
	    
	    BufferedImage newImage = new BufferedImage(imgWidth, imgHeight,
	            BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < imgWidth; i++)
        {
        	for (int j = 0; j < imgHeight; j++)
        	{
        		int color = 0xFFFFFFFF;
        		for (int m = 0; m < multiplier; m++)
        		{
        			for (int n = 0; n < multiplier; n++)
        			{
        				if (img.getRGB(i * multiplier + m, j * multiplier + n) == 0xFF000000)
        				{
        					color = 0xFF000000;
        					break;
        				}
        			}
        			if (color == 0xFF000000)
        			{
        				break;
        			}
        		}
        		newImage.setRGB(i, j, color);
        	}
        }
	    return newImage;

	}
}
