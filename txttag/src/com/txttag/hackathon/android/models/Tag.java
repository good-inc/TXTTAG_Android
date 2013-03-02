package com.txttag.hackathon.android.models;

public class Tag
{
	public String state;
	public String plate;
	
	public Tag clone()
	{
		Tag newTag = new Tag();
		newTag.state = state;
		newTag.plate = plate;
		
		return newTag;
	}
	
	public boolean isValidTag()
	{
		if(state != null && state.length() == 2 && plate != null && plate.length() > 1)
			return true;
		else
			return false;
	}
	
	public boolean equals(Tag other)
	{
		if(other.state.equalsIgnoreCase(state) && other.plate.equalsIgnoreCase(plate))
			return true;
		else
			return false;
	}
}
