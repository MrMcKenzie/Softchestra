package omr;

import java.util.*;

public class StaffSet {

	public List<Integer> StaffLocation;
	public Integer Thickness;
	public Integer Space;
	public Integer StaffSetHeight;
	public TokenCollection tokenCollection;
	
	public StaffSet(Integer thickness, Integer space)
	{
		StaffLocation = new ArrayList<Integer>();
		Thickness = thickness;
		Space = space;
		StaffSetHeight = 5 * Thickness + 4 * space;
	}
	
	/**
	 * Sets the tokens that are associate with this staff
	 * @param tokenCollection
	 */
	/**
	 * @param tokenCollection
	 */
	public void setTokenCollection(TokenCollection tokenCollection)
	{
		this.tokenCollection = tokenCollection;
		L0Token currentToken;
		for(int i = 0; i < this.tokenCollection.Tokens.size(); i++)
		{
			currentToken = this.tokenCollection.Tokens.get(i);
			currentToken.setStaff(this);
		}
	}
	
	public TokenCollection getTokenCollection()
	{
		return tokenCollection;
	}
	
	public boolean AddStaff(Integer location)
	{
		if (StaffLocation.size() == 5)
			return false;
		else if (StaffLocation.size() == 0)
		{
			StaffLocation.add(location);
		}
		else
		{
			for (int i = 0; i < StaffLocation.size(); i++)
			{
				Integer distance = Math.abs(location - StaffLocation.get(i));
				if (distance < Math.ceil((double)Thickness / 2) || distance > StaffSetHeight + 2 * Thickness)
				{
					return false;
				}
				if (Math.abs(distance % (Thickness + Space)) < Math.ceil((double)Thickness / 2) ||
						Math.abs(Thickness + Space - distance % (Thickness + Space)) < Math.ceil((double)Thickness / 2))
				{
					StaffLocation.add(location);
					Collections.sort(StaffLocation);
					return true;
				}
			}
			
		}
		return false;
	}
}
