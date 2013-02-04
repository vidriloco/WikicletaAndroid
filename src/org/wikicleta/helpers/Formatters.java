package org.wikicleta.helpers;

public class Formatters {
	
	public static String millisecondsToTime(long milliseconds) {
		int seconds = (int) (milliseconds / 1000);
	    int minutes = seconds / 60;
	    seconds     = seconds % 60;
       
	    String timeString = "";
       
	    if(minutes > 0 && minutes < 10)
    	   timeString = "0" + String.valueOf(minutes) + ":";
	    else if(minutes >= 10) 
    	   timeString = String.valueOf(minutes) + ":";
	    else {
    	   timeString = "00:";
	    }
       
	    if (seconds < 10)
    	   timeString += "0"+seconds;
	    else
    	   timeString += String.valueOf(seconds);
	    return timeString;
	}
}
