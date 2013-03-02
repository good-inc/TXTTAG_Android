package com.txttag.hackathon.android.models;

public class MessageSentConfirmation 
{
	public String state;
	public String plate;
	public String text; // message that was sent
	
	public String toString()
	{
		return "[MessageSentConfirmation] - " + plate + ", " + state + ", " + text;
	}
}
