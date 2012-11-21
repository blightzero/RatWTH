package edu.rwth.datacenterrats.Calendar;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Class that represents a Calendar Event.
 * Summary, Start, End, Location, Description, Category, UniqueID 
 * @author Benjamin Grap
 *
 */
public class vEvent {

	private String summary;
	private String location;
	private String uid;
	private String description;
	private String category;
	private Date start;
	private Date end;
	private static SimpleDateFormat df = new SimpleDateFormat("EE dd.MM.yyyy HH:mm");
	
	
	vEvent(String uid, String summary, String location,String description, String category, Date start, Date end){
		this.summary = summary;
		this.uid = uid;
		this.location = location;
		this.description = description;
		this.category = category;
		this.start = start;
		this.end = end;
	}
	
	vEvent(){
		this.summary = "";
		this.uid = "";
		this.location = "";
		this.description = "";
		this.category = "";
		this.start = new Date();
		this.end = new Date();
	}
	
	public String getSummary(){
		return this.summary;
	}
	
	public String getLocation(){
		return this.location;
	}
	
	public String getUid(){
		return this.uid;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public String getCategory(){
		return this.category;
	}
	
	public String getStart(){
		return df.format(this.start);
	}
	
	public Date getStartDate(){
		return this.start;
	}
	
	public String getEnd(){
		return df.format(this.end);
	}
	
	public Date getEndDate(){
		return this.end;
	}
	
	public String toString(){
		return this.summary;
	}
}
