package edu.rwth.datacenterrats.Calendar;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;


/**
 * Wrapper Class for all the Calendar Events.
 * @author Benjamin Grap
 *
 */
public class eventStore {


	private List<vEvent> itemlist;
	private int itemcount = 0;
	
	eventStore()
	{
		itemlist = new Vector<vEvent>(0);
	}
	
	int addItem(vEvent item)
	{
		itemlist.add(item);
		itemcount++;
		return itemcount;
	}
	
	vEvent getItem(int location)
	{
		return itemlist.get(location);
	}
	
	List<vEvent> getAllItems()
	{
		
		return itemlist;
	}
	

	
	int getItemCount()
	{
		return itemcount;
	}
	
	/*
	 * Remove all the Events that are in the past.
	 * Only does this on the current instance!
	 * Does not affect the file that is parsed.
	 */
	void prune(){
		Date today = new Date();
				
		for(int i=0;i<this.itemlist.size();){
			vEvent event = itemlist.get(i);
			if(today.compareTo(event.getEndDate())>0){
				itemlist.remove(i);
			}else{
				i++;
			}
		}
	}

	/*
	 * Sort Calendarevents by Date.
	 */
	void sort(){
		Comparator<vEvent> date_compare = new Comparator<vEvent>(){
			@Override
			public int compare(vEvent obj1, vEvent obj2){
				Date date_obj1;
				Date date_obj2;
				
				date_obj1 = obj1.getStartDate();
				date_obj2 = obj2.getStartDate();
				return date_obj1.compareTo(date_obj2);
			}
		};
		Collections.sort(itemlist,date_compare);
	}
}
