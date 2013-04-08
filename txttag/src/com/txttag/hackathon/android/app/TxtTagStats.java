package com.txttag.hackathon.android.app;

import com.txttag.hackathon.android.models.ServiceStats;
import com.txttag.hackathon.android.net.TxtTagService;

public class TxtTagStats 
{
	private static TxtTagStats instance;
	public static TxtTagStats getInstance()
	{
		if(instance == null)
			instance = new TxtTagStats();
		
		return instance;
	}
	
	private int numTags;
	private int numMessages;
	
	private TxtTagStats() {}
	
	public int getNumTags() { return numTags; }
	
	public int getNumMessages() { return numMessages; }
	
	public void refresh()
	{
		TxtTagService service = new TxtTagService();
		ServiceStats stats = service.getServiceStats();
		
		if(stats == null)
			return;
		
		numTags = stats.tags;
		numMessages = stats.msgs;
	}
}
