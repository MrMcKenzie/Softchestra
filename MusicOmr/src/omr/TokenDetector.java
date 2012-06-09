package omr;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;

public class TokenDetector {

	private BufferedImage image;
	private static int filecounter = 0;
	
	// Pixel Type to be used in the run length enconding
	private enum PixelType
	{
		BLACK,
		WHITE
	}
	
	public TokenDetector(BufferedImage image)
	{
		//ImageLoader imageLoader = new ImageLoader();
		//this.image = imageLoader.DeepCopy(image);
		this.image = image;
	}
	
	public TokenCollection SegmentImageTokens()
	{
		TokenCollection tokenCollection = new TokenCollection(image);
		for (int i = 0; i < image.getWidth(); i++)
		{
			for (int j = 0; j < image.getHeight(); j++)
			{
//				if (image.getRGB(i, j) == 0xFF000000 && tokenCollection.TokenIndices.get(new Point(i, j)).size() == 0)
				if (image.getRGB(i, j) == 0xFF000000 && tokenCollection.TokenIndices.get(new Point(i, j)) == -1)
				{
					L0Token token = new L0Token();
					token.CreateToken(image, i, j);
					tokenCollection.AddCharToken(token);
				}
			}
		}
		return tokenCollection;
	}
	
	public void retrieveL0Tokens(StaffSet stave)
	{
		TokenCollection tokenCollection = new TokenCollection(image);
		for (int i = 0; i < image.getWidth(); i++)
		{
			for (int j = stave.StaffLocation.get(0); j <= stave.StaffLocation.get(4); j++)
			{
//				if (image.getRGB(i, j) == 0xFF000000 && tokenCollection.TokenIndices.get(new Point(i, j)).size() == 0)
				if (image.getRGB(i, j) == 0xFF000000 && tokenCollection.TokenIndices.get(new Point(i, j)) == -1)
				{
					L0Token token = new L0Token();
					token.CreateToken(image, i, j);
					tokenCollection.AddCharToken(token);
				}
			}
		}
		stave.setTokenCollection(tokenCollection);
	}
	
	// for debugging purposes only
	public BufferedImage highlightL0Tokens(TokenCollection tc)
	{
		ImageLoader imageLoader = new ImageLoader();
		BufferedImage imageCopy = imageLoader.DeepCopy(image);
		Graphics2D g = (Graphics2D) imageCopy.getGraphics();
		for (int i = 0; i < tc.Tokens.size(); i++)
		{
			L0Token ct = tc.Tokens.get(i);
			g.setColor(new Color(0xFF000000));
			g.drawRect(ct.X0, ct.Y0, ct.X1 - ct.X0, ct.Y1 - ct.Y0);
		}
		return imageCopy;
	}
	
	/**
	 * This section is for the L1 token detection
	 */
	
	/**
	 * Returns all Notes and Segments discovered in the
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<L1Token> retrieveL1Tokens(L0Token token)
	{
		int i = 0, j;
		int width = token.X1 - token.X0;
		int height = token.Y1 - token.Y0;
		int xPos = token.X0;
		Pair<PixelType, Integer> run;
		LinkedList<L1Token> subTokens = new LinkedList<L1Token>();
		Object[] noteRuns = getNoteRuns(token);
		boolean[] notePixelDistribution = markNoteSections(noteRuns, token);
		writeRunsToCSV(noteRuns);
		
		// go through note segments array, creating the required tokens and stating whether or not it's a note
		while(i < notePixelDistribution.length)
		{
			j = 1;
			while((i + j < notePixelDistribution.length) && (notePixelDistribution[i + j] == notePixelDistribution[i]))
			{
				j++;
			}
			
			subTokens.add(new L1Token(token.X0 + i, j, token.Y0, height, token.getStaff(), notePixelDistribution[i]));
			i += j;
		}
		return subTokens;
	}
	
	@SuppressWarnings("unchecked")
	private Object[] getNoteRuns(L0Token token)
	{
		int noteThreshold = token.getStaff().Space - 1;
		int maxRun;
		int tokenXPos = 0;
		Object[] runs = new Object[(token.X1 - token.X0) + 1];
		
		for(int i = token.X0; i <= token.X1; i++)
		{
			LinkedList<Pair<PixelType, Integer>> xRLE = getXRLE(i, token.Y0, token.Y1 - token.Y0);
			maxRun = 0;
//			 For RLEs with Black Pixel runs greater that 2*lineThickness + spaceThickness, remove that run
			for(int j = 0; j < xRLE.size(); j++)
			{
				Pair<PixelType, Integer> run = xRLE.get(j);
				if(run.getFirst() == PixelType.BLACK && run.getSecond() >= noteThreshold && run.getSecond() > maxRun)
				//if(run.getFirst() == PixelType.BLACK && run.getSecond() <= runThreshold && run.getSecond() >= noteThreshold)
				{
					runs[tokenXPos] = run;
					maxRun = run.getSecond();
				}
			}
			tokenXPos++;
		}
		return runs;
	}

	
	/**
	 * Improved note determination algorithm
	 * This is dependent on the threshold of the run detection algorithm.
	 * We currently use Staff Space - 1
	 * @param runs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean[] markNoteSections(Object[] runs, L0Token token)
	{
		boolean[] pixels = new boolean[runs.length];
		int noteWidthThreshold = 3;
		int stemThreshold = 4 * token.getStaff().Thickness + 3 * token.getStaff().Space;
		int maxNotePosition;
		
		// first check the entire run for all the maximums, and see if they are any stems
		ArrayList<Integer> stemPositions = new ArrayList<Integer>();
		Pair<PixelType, Integer> run, run2;
		
		// Do an initial check of first position to see if a stem is there, this indicates that all stems in the run are on the left side
		int j = 0;
		run = (Pair<PixelType, Integer>)runs[j];
		
		while(run == null)
		{
			j++;
			run = (Pair<PixelType, Integer>)runs[j];
		}
		boolean stemLeft = run == null? false : run.getSecond() >= stemThreshold;
		run2 = run = null;
		
		for(int i = 0; i < runs.length; i++)
		{
			run = (Pair<PixelType, Integer>)runs[i];
			
			if(run != null)
			{
				if((run2 == null || run2.getSecond() < stemThreshold)&& (run.getSecond() >= stemThreshold))
					stemPositions.add(i);
			}
			
			run2 = run;
		}
		
		// if the stem count is zero, we have no note or a whole note, return all falses (for now, we don't consider the internals of the note in the case of a whole note)
		if(stemPositions.size() != 0)
		{
			if(stemPositions.size() == 1) // if only 1 stem found, we have a single note in the token, return it
			{
				for(int i = 0; i < pixels.length; i++)
					pixels[i] = true;
			}
			else // more than 1 stem found, so we have a run of notes
			{
				// if stem on left, start from stem and work forward, noting the highest run seen before a fall, the position will be the middle of the note
				if(stemLeft)
				{
					for(Integer stem : stemPositions)
					{
						int currentPosition = 1;
						// set the first position after the stem to be true since the run for it  may or may not be missing depending on
						// the thickness used there
						pixels[stem + currentPosition] = true;
						currentPosition++;
						run = (Pair<PixelType, Integer>)runs[stem + currentPosition];
						while((stem + currentPosition < runs.length) && (run != null))
						{
							pixels[stem + currentPosition] = true;
							
							currentPosition++;
							run = (Pair<PixelType, Integer>)runs[stem + currentPosition];
						}
						
						// set the pixel after the last run to be true since its run always is less than the threshold
						pixels[stem + currentPosition] = true;
					}
				}
				// if stem on right, start from stem and work backward, noting the highest run seen before a fall, the position will be the middle of the note
				else
				{
					for(Integer stem : stemPositions)
					{
						int currentPosition = 1;
						run = (Pair<PixelType, Integer>)runs[stem - currentPosition];
						// set the first position before the stem to be true since the run for it  may or may not be missing depending on
						// the thickness used there
						pixels[stem - currentPosition] = true;
						
						while((stem - currentPosition >= 0) && (run != null))
						{
							pixels[stem - currentPosition] = true;
							currentPosition++;
							run = (Pair<PixelType, Integer>)runs[stem - currentPosition];
						}
						
						// set the pixel after the last run to be true since its run always is less than the threshold
						pixels[stem - currentPosition] = true;
					}
				}
			}
		}
		
		return pixels;
	}
	
	/**
	 * Returns the X dimension Run Length Encoding for a single column of the Token
	 * @return
	 */
	private LinkedList<Pair<PixelType, Integer>> getXRLE(int startX, int startY, int height)
	{	
		LinkedList<Pair<PixelType, Integer>> xRLE = new LinkedList<Pair<PixelType, Integer>>();
		PixelType pixelType;
		int pixelCount = 0;
		pixelType = image.getRGB(startX, startY) == Color.BLACK.getRGB() ? PixelType.BLACK: PixelType.WHITE;
		for(int i = startY; i < startY + height; i++)
		{
			if(image.getRGB(startX, i) == Color.BLACK.getRGB())
			{
				// if the previous pixel was black, then add to the count, else
				// start over the count from 1 for the new white pixel
				if(pixelType == PixelType.BLACK)
				{
					pixelCount++;
					
					if(i == startY + height - 1)
						xRLE.add(new Pair<PixelType, Integer>(PixelType.BLACK, new Integer(pixelCount)));
				}
				else
				{
					xRLE.add(new Pair<PixelType, Integer>(PixelType.WHITE, new Integer(pixelCount)));
					pixelCount = 1;
					pixelType = PixelType.BLACK;
				}
			}
			else if(image.getRGB(startX, i) == -1)
			{
				// if the previous pixel was white, then add to the count, else
				// start over the count from 1 for the new white pixel
				if(pixelType == PixelType.WHITE)
				{
					pixelCount++;
					
					if(i == startY + height - 1)
						xRLE.add(new Pair<PixelType, Integer>(PixelType.WHITE, new Integer(pixelCount)));
				}
				else
				{
					xRLE.add(new Pair<PixelType, Integer>(PixelType.BLACK, new Integer(pixelCount)));
					pixelCount = 1;
					pixelType = PixelType.WHITE;
				}
			}
		}
		return xRLE;
	}
	
	// Used for debugging purposes
	public BufferedImage highlightL1Tokens(LinkedList<L1Token> subTokens)
	{
		ImageLoader imageLoader = new ImageLoader();
		BufferedImage imageCopy = imageLoader.DeepCopy(image);
		Graphics2D g = (Graphics2D) imageCopy.getGraphics();
		for (L1Token token :  subTokens)
		{
			g.setColor(new Color(0xFF000000));
			g.drawRect(token.getX(), token.getY(), token.getWidth(), token.getHeight());
		}
		return imageCopy;
	}
	
	// Used for debugging purposes
	@SuppressWarnings("unchecked")
	private void writeRunsToCSV(Object[] runs)
	{
		try
		{
			FileWriter fstream = new FileWriter(String.format("run%d.csv", filecounter));
			filecounter++;
			BufferedWriter out = new BufferedWriter(fstream);
			String info = "";
			for(int i = 0; i < runs.length; i++)
			{
				Pair<PixelType, Integer> run = (Pair<PixelType, Integer>)runs[i];
				
				if(run != null)
				{
					info += String.format("%d, %d",i,run.getSecond().intValue()) + "\n";
				}	
				else
				{
					info += String.format("%d, %d",i,0) + "\n";
				}
			}
			out.write(info);
			out.close();
		}
		catch(Exception err)
		{
			System.out.println("error");
		}
	}
	
	/**
	 * This section is for detection of L2 Tokens
	 */
	public LinkedList<L2Token> retrieveL2Tokens(L1Token token)
	{
		LinkedList<L2Token> l2Tokens = new LinkedList<L2Token>();
		LinkedList<Pair<Integer, Integer>> noteHeads = getNoteHeads(yProjection(token));
		
		for(Pair<Integer, Integer> noteHead : noteHeads)
		{
			l2Tokens.add(new L2Token(token.getX(), token.getWidth(), token.getY() + noteHead.getFirst(), noteHead.getSecond() - noteHead.getFirst(), token.getStaff(), true));
		}
		// if the token has a note in it:
		// Get the Y Projection of the L1 Token and process it for the notes
		
		// note head detection (may have to improve)
		
		
		// Debugging
		//writeYProjToCSV(yProjection);
		// Use the Y Projection to determine the division of notes
		// if the token does not have a note in it:
		// Return the same token as a L2Token
		
		return l2Tokens;
	}
	
	/**
	 * Returns the Y Projection of the specified token
	 * @param token
	 * @return
	 */
	private int[] yProjection(L1Token token)
	{
		int[] yProjection = new int[token.getHeight()];
		
		for(int i = 0; i < yProjection.length; i++)
		{
			for(int j = 0; j < token.getWidth(); j++)
			{
				if(image.getRGB(j + token.getX(), i + token.getY()) == Color.BLACK.getRGB())
				{
					yProjection[i]++;
				}
			}
		}
		
		return yProjection;
	}
	
	/**
	 * note head detection (may have to improve)
	 * @param yProjection
	 * @return
	 */
	private LinkedList<Pair<Integer, Integer>> getNoteHeads(int[] yProjection)
	{
		LinkedList<Pair<Integer, Integer>> noteHeads = new LinkedList<Pair<Integer, Integer>>();
		// This stem removal may have to change if the stem is crooked
		int stemWidth = 1;
		int previous = -1, currentMax = 0, noteStart = -1, noteEnd = -1;
		boolean middleFound = false;
		int i = 0;
		while(i < yProjection.length)
		{
			// remove stem width from row projection
			yProjection[i] -= stemWidth;
			
			// if there is still a count available, this is a note
			if(yProjection[i] != 0)
			{
				if(noteStart == -1)
				{
					noteStart = i;
					currentMax = yProjection[i];
				}
				else if(yProjection[i] > currentMax)
				{
					currentMax = yProjection[i];
				}
				else if(yProjection[i] == currentMax)
				{
					middleFound = true;
				}
				else
				{
					if(middleFound && (previous < yProjection[i]) || (i == yProjection.length - 1))
					{
						noteEnd = i != yProjection.length - 1 ? i - 1 : i;
						
						// add the note start/ note end pair to the list
						noteHeads.add(new Pair<Integer, Integer>(new Integer(noteStart), new Integer(noteEnd)));
						
						// prepare for the start of the next note
						noteStart = noteEnd;
						currentMax = yProjection[i];
						middleFound = false;
					}
				}
				
				
			}
			previous = yProjection[i];
			i++;
		}
		return noteHeads;
	}
	
	// Used for debugging purposes
		@SuppressWarnings("unchecked")
		private void writeYProjToCSV(int[] projection)
		{
			try
			{
				FileWriter fstream = new FileWriter(String.format("proj%d.csv", filecounter));
				filecounter++;
				BufferedWriter out = new BufferedWriter(fstream);
				String info = "";
				for(int i = 0; i < projection.length; i++)
				{
					info += String.format("%d, %d",i,projection[i]) + "\n";
				}
				out.write(info);
				out.close();
			}
			catch(Exception err)
			{
				System.out.println("error");
			}
		}
}
