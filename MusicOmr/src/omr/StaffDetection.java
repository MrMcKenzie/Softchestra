package omr;

import java.awt.image.BufferedImage;
import java.util.*;



public class StaffDetection {
	
	private BufferedImage image;
	
	public Integer Thickness;
	public Integer Space;
	public Integer StaffSetHeight;
	
	
	public StaffDetection(BufferedImage image)
	{
		this.image = image;
	}
	
	public List<StaffSet> DetectStave()
	{
		Projection projector = new Projection(image);
		List<Integer> yProjection = projector.GetYProjection(0, image.getWidth() - 1, 0, image.getHeight() - 1);
		
//		for (int i = 0; i < yProjection.size(); i++)
//		{
//			System.out.println(yProjection.get(i));
//		}
	
		RunLengthEncoding rle = new RunLengthEncoding(image);
		List<Integer> thicknessRLE = rle.GetVerticalRLE(0xFF000000);
		List<Integer> spaceRLE = rle.GetVerticalRLE(0xFFFFFFFF);
		
		Thickness = thicknessRLE.indexOf(Collections.max(thicknessRLE));
		Space = spaceRLE.indexOf(Collections.max(spaceRLE));
		StaffSetHeight = 4 * Space + 5 * Thickness;

		List<Integer> staffSetApproximates = DetectstaffSets(yProjection);
		Collections.sort(staffSetApproximates);
		List<StaffSet> staffSetList = new ArrayList<StaffSet>();
		
		for (int i = 0; i < staffSetApproximates.size(); i++)
		{
			StaffSet staffSet = new StaffSet(Thickness, Space);
			HashMap<Integer, List<Integer>> yProjectionSubset = new HashMap<Integer, List<Integer>>();
			int beginIndex = Math.max(staffSetApproximates.get(i) - StaffSetHeight / 2 - Space - Thickness, 0);
			int endIndex = Math.min(staffSetApproximates.get(i) + StaffSetHeight / 2 + Space + Thickness + 1, yProjection.size() - 1);
			for (int j = beginIndex; j < endIndex + 1; j++)
			{
				if (!yProjectionSubset.containsKey(yProjection.get(j)))
				{
					yProjectionSubset.put(yProjection.get(j), new ArrayList<Integer>());
				}
				yProjectionSubset.get(yProjection.get(j)).add(j);
			}
			List<Integer> keyList = new ArrayList(yProjectionSubset.keySet());
			Collections.sort(keyList);
			for (int j = keyList.size() - 1; j > -1; j--)
			{
				for (int k = 0; k < yProjectionSubset.get(keyList.get(j)).size(); k++)
				{
					Integer rowNum = yProjectionSubset.get(keyList.get(j)).get(k);
					staffSet.AddStaff(rowNum);
					if (staffSet.StaffLocation.size() == 5)
					{
						j = -1;
						break;
					}
				}
			}
			staffSetList.add(staffSet);
		}
		return staffSetList;
	}
	
	private List<Integer> DetectstaffSets(List<Integer> yProjection)
	{				

		int curSum = 0;
		
		for (int i = 0; i < StaffSetHeight; i++)
		{
			curSum += yProjection.get(i);
		}
		List<Integer> yProjectionMovAvg = new ArrayList<Integer>();
		yProjectionMovAvg.add(curSum);
		for (int i = StaffSetHeight; i < yProjection.size() - 1; i++)
		{
			curSum -= yProjection.get(i - StaffSetHeight);
			curSum += yProjection.get(i + 1);
			yProjectionMovAvg.add(curSum);
		}
		
//		for (int i = 0; i < yProjectionMovAvg.size(); i++)
//		{
//			System.out.println(yProjectionMovAvg.get(i));
//		}
		
		List<Integer> localMaxIndices = new ArrayList<Integer>();
		List<Integer> localMaxes = new ArrayList<Integer>();
		while (true)
		{
			
			Integer localMax = Collections.max(yProjectionMovAvg);
			Integer localMaxIndex = yProjectionMovAvg.indexOf(localMax);
			for (int i = Math.max(localMaxIndex - StaffSetHeight / 2 - Space - Thickness, 0); i < Math.min(localMaxIndex + StaffSetHeight / 2 + Space + Thickness, yProjectionMovAvg.size()); i++)
			{
				yProjectionMovAvg.set(i, 0);
			}
			double m = getMean(localMaxes);
			if (m != 0 && Math.abs(localMax - m) / m > 0.2) //THIS IS A DISTINGUISHER OF STAFF SETS
			{
				break;
			}
			localMaxes.add(localMax);
			localMaxIndices.add(localMaxIndex + StaffSetHeight / 2);
		}
		return localMaxIndices;
	}
	
	private double getMean(List<Integer> l)
	{
		double sum = 0;
		for (int i = 0; i < l.size(); i++)
		{
			sum += l.get(i);
		}
		sum /= l.size();
		return sum;
	}
	
//	private int detectDeviationHeuristic(HashMap<Integer, Integer[]> staffSets)
//	{
//		Set<Integer> keySet = staffSets.keySet();
//		int totalError = 0;
//		for (Iterator<Integer> i = keySet.iterator(); i.hasNext(); i.next())
//		{
//			Integer[] ss = staffSets.get(i);
//			int curSetCount = 0;
//			for (int j = 0; j < ss.length; j++)
//			{
//				if (ss[j] != 0)
//				{
//					curSetCount++;
//				}
//				totalError += Math.min(Math.abs(5 - curSetCount), curSetCount);
//			}
//		}
//		return totalError;
//	}
}


