package com.txttag.hackathon.android.net;

public class JsonResponse<ContentType>
{
	public boolean success;
	public String message;
	public ContentType data;
	
	public String toString()
	{
		return "[JsonResponse] - Success: " + success + ", " + "Data: " + data;
	}
}
