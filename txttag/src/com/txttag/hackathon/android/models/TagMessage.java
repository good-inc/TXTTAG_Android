package com.txttag.hackathon.android.models;

public class TagMessage 
{
	public String tag;
	public String state;
	public String message;
	public String date;
	
	public String toString()
	{
		return "[TagMessage] - " + date + ", State: " + state + ", Tag: " + tag + ", Message: " + message;
	}
}
