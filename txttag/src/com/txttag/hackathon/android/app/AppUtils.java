package com.txttag.hackathon.android.app;

import java.util.Arrays;
import java.util.List;

public class AppUtils 
{
	private static String[] stateCodes = new String[] {
		"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI",
		"MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", 
		"VT", "VA", "WA", "WV", "WI", "WY"
	};
	private static List<String> stateCodesList = Arrays.asList(stateCodes);
	
	private static String[] stateNames = new String[] {
		"Alabama", "Alaska", "Arkansas", "Arizona", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", 
		"Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Las Angelos", "Maine", "Maryland", "Massachussets", "Michigan",
		"Minnesota", "Mississippi", "Missouri", "Montana", "New England", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", 
		"Nouth Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "San Diego", 
		"Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
	};
	private static List<String> stateNamesList = Arrays.asList(stateNames);
	
	public static String getStateCodeFromName(String stateName)
	{
		int index = stateNamesList.indexOf(stateName);
		return stateCodesList.get(index);
	}
	
	public static int getStateIndexFromName(String stateName)
	{
		return stateNamesList.indexOf(stateName);
	}
	
	public static String getStateNameFromCode(String stateCode)
	{
		int index = stateCodesList.indexOf(stateCode.toUpperCase());
		return stateNamesList.get(index);
	}
	
	public static int getStateIndexFromCode(String stateCode)
	{
		return stateCodesList.indexOf(stateCode.toUpperCase());
	}
}
