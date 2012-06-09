package omr;

import java.awt.image.BufferedImage;
import java.util.List;

public class StaffRemoval {
	private BufferedImage image;
	
	private Integer thickness;
	private Integer space;
	private Integer staveSetHeight;
	
	public StaffRemoval(BufferedImage image, Integer thickness, Integer space, Integer staveSetHeight)
	{
		this.image = image;
		this.thickness = thickness;
		this.space = space;
		this.staveSetHeight = staveSetHeight;
	}
	
	public BufferedImage RemoveStave(List<StaffSet> staffSetList)
	{
		for (int i = 0; i < staffSetList.size(); i++)
		{
			for (int j = 0; j < 5; j++)
			{
				Integer row = staffSetList.get(i).StaffLocation.get(j);
				RemoveSingleStaffLine(row);
			}
		}
		return image;
	}
	
	private void RemoveSingleStaffLine(Integer row)
	{
		Integer lineWidth = (int)Math.ceil(thickness / 2);
		Integer spaceWidth = (int)Math.ceil((thickness + space) / 2);
		for (int i = 0; i < image.getWidth(); i++)
		{
			boolean lineFilledAbove = true;
			boolean lineFilledBelow = true;
			for (int j = 0; j <= lineWidth; j++)
			{
				if (image.getRGB(i, row - j) == 0xFFFFFFFF)
					lineFilledAbove = false;
				if (image.getRGB(i, row + j) == 0xFFFFFFFF)
					lineFilledBelow = false;
			}
			
			Integer pixelsAbove = 0;
			if (lineFilledAbove == true)
			{
				for (int j = lineWidth + 1; j <= spaceWidth; j++)
				{
					if (image.getRGB(i, row - j) == 0xFF000000)
						pixelsAbove++;
					else
						break;
				}
			}
			Integer pixelsBelow = 0;
			if (lineFilledBelow == true)
			{
				for (int j = lineWidth + 1; j <= spaceWidth; j++)
				{
					if (image.getRGB(i, row + j) == 0xFF000000)
						pixelsBelow++;
					else
						break;
				}
			}
			
			boolean deleteAbove = pixelsAbove < spaceWidth / 2;
			boolean deleteBelow = pixelsBelow < spaceWidth / 2;
			if (deleteAbove == true) //HEURISTIC FOR DELETING LINES
			{
				for (int j = 1; j <= lineWidth + pixelsAbove + 1; j++)
					image.setRGB(i, row - j, 0xFFFFFFFF);
			}
			if (deleteBelow == true) //HEURISTIC FOR DELETING LINES
			{
				for (int j = 1; j <= lineWidth + pixelsBelow + 1; j++)
					image.setRGB(i, row + j, 0xFFFFFFFF);
			}
			if (deleteAbove && deleteBelow)
				image.setRGB(i, row, 0xFFFFFFFF);
		}
	}
}
