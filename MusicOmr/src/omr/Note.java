/**
 * This class represents a note, which is ia SubToken, but has extra information for the pitch and duration
 */
package omr;

public class Note
{
	private int duration;
	private int pitch;
	
//	public Note(int x0, int x1, int y0, int y1, StaffSet staff)
//	{
//		super(x0, x1, y0, y1, staff, booleanHas);
//	}
	
	public void setDuration(int duration)
	{
		this.duration = duration;
	}
	
	public void setPitch(int pitch)
	{
		this.pitch = pitch;
	}
}
