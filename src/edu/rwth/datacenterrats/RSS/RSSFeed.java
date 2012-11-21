package edu.rwth.datacenterrats.RSS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import edu.rwth.datacenterrats.RSS.RSSItem;
/**
 * Class that defines a RSSFeed
 * @author
 *
 */
public class RSSFeed{
	
	private String _title = null;
	private String _pubdate = null;
	private int _itemcount = 0;
	private List<RSSItem> _itemlist;
	
	
	RSSFeed(){
		_itemlist = new Vector<RSSItem>(0);
	}
	
	int addItem(RSSItem item){
		_itemlist.add(item);
		_itemcount++;
		return _itemcount;
	}
	
	RSSItem getItem(int location){
		return _itemlist.get(location);
	}
	
	List<RSSItem> getAllItems(){
		return _itemlist;
	}
	
	int getItemCount(){
		return _itemcount;
	}
	
	void setTitle(String title){
		_title = title;
	}
	
	void setPubDate(String pubdate){
		_pubdate = pubdate;
	}
	
	String getTitle(){
		return _title;
	}
	
	String getPubDate(){
		return _pubdate;
	}
	/**
	 * Sort the RSS Feed Items according to their date.
	 */
	void sort(){
		Comparator<RSSItem> date_compare = new Comparator<RSSItem>(){
			@Override
			public int compare(RSSItem obj1, RSSItem obj2){
				SimpleDateFormat df = new SimpleDateFormat("E, dd MMMM yyyy kk:mm:ss zzzz", Locale.US);
				Date date_obj1;
				Date date_obj2;
				try {
					date_obj1 = df.parse(obj1.getPubDate());
					date_obj2 = df.parse(obj2.getPubDate());
					return date_obj2.compareTo(date_obj1);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 1;
				}	
			}
		};
		Collections.sort(_itemlist,date_compare);
	}
}
