package omr;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.*;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String userFolder = System.getProperty("user.dir");
		
		ImageLoader imageLoader = new ImageLoader();
		String fileName = userFolder + System.getProperty("file.separator") + "Chord.png";
		BufferedImage image = imageLoader.LoadImage(fileName);
		
		ImageProcessor preprocessor = new ImageProcessor();
		image = preprocessor.ProcessImage(image);
		
//		File outputFile = new File("test.png");
//		ImageIO.write(image, "png", outputFile);
		
		StaffDetection staffDetector = new StaffDetection(image);
		List<StaffSet> staffSetList = staffDetector.DetectStave();
		
		StaffRemoval staffRemover = new StaffRemoval(image, staffDetector.Thickness, staffDetector.Space, staffDetector.StaffSetHeight);
		image = staffRemover.RemoveStave(staffSetList);

		
		NoiseRemoval noiseRemover = new NoiseRemoval(image);
		image = noiseRemover.RemoveNoise();
		
		TokenDetector tokenDetector = new TokenDetector(image);
		LinkedList<L1Token> subTokenList = new LinkedList<L1Token>();
		
		// Get segments and assign them to the staffset for future reference
		for(StaffSet stave : staffSetList)
		{
			tokenDetector.retrieveL0Tokens(stave);
			for(L0Token token : stave.getTokenCollection().Tokens)
			{
				token.processToken(image, tokenDetector);
				subTokenList.addAll(token.getSubTokens());
			}
		}
		
		LinkedList<L2Token> l2TokenList = new LinkedList<L2Token>();
		for(L1Token token : subTokenList)
		{
			token.processToken(image, tokenDetector);
		}
		//image = tokenDetector.highlightL1Tokens(subTokenList);
		//image = tokenSegmenter.HighlightTokens(firstStave.getTokenCollection());
//		
		//TokenCollection tokenCollection = tokenDetector.SegmentImageTokens();
		//image = tokenDetector.highlightL0Tokens(tokenCollection);
		
		File outputFile = new File("moonlight_sonata_out.png");
		ImageIO.write(image, "png", outputFile);
		
	}

}
