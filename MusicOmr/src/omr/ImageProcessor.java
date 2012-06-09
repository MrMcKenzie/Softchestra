package omr;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageProcessor {
	public ImageProcessor(){}
	
	public BufferedImage ProcessImage(BufferedImage image)
	{
		BlackWhiteConversion blackWhiteConverter = new BlackWhiteConversion();
		image = blackWhiteConverter.GetBlackWhiteImage(image);
		
		ResampleBufferedImage resampleBufferedImage = new ResampleBufferedImage();
		int multiplier = 1;
		while (image.getWidth() * image.getHeight() / Math.pow(multiplier, 2) > 800000)
		{
			multiplier++;
		}
		image = resampleBufferedImage.ScaleBlackWhiteImage(image, multiplier, new Color(0xFFFFFFFF));
		
//		ImageLoader imageLoader = new ImageLoader();
//		BufferedImage imageCopy = imageLoader.DeepCopy(image);
//		int multiplier = 1;
//		while (image.getWidth() * image.getHeight() / Math.pow(multiplier, 2) > 800000)
//		{
//			multiplier++;
//		}
//		imageCopy = resampleBufferedImage.ScaleBlackWhiteImage(imageCopy, multiplier, new Color(0xFFFFFFFF));

		//HoughLineAlgo houghLineAlgo = new HoughLineAlgo();
		//image = houghLineAlgo.HighlightHorizontalLines(image);
		
		int size = (int)Math.pow(2, Math.ceil(Math.log(Math.max(image.getWidth(), image.getHeight())) / Math.log(2)));
		FFT fft = new FFT(image, size);
		fft.doFFT();
		
		RotateImage rotater = new RotateImage();
		image = rotater.Rotate(image, fft.getRotationAngle());
		return image;
	}
}
