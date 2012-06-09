package omr;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ImageLoader {
	public ImageLoader()
	{
	
	}

	public BufferedImage LoadImage(String imgName)
	{
		try {
			BufferedImage image = ImageIO.read(new File(imgName));
			//BufferedImage image = ImageIO.read(this.getClass().getResource(imgName));
			return image;
			//marchThroughImage(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public BufferedImage DeepCopy(BufferedImage image) 
	{
		 ColorModel colorModel = image.getColorModel();
		 boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
		 WritableRaster writableRaster = image.copyData(null);
		 return new BufferedImage(colorModel, writableRaster, isAlphaPremultiplied, null);
	}
}