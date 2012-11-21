package edu.rwth.datacenterrats.LectureRooms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import android.content.res.XmlResourceParser;
import android.util.Log;


/**
 * Parsing Class that reads the hoersaal xml file from the application resources.
 * Creates an List of hoersaal-items.
 * 
 * @author Benjamin Grap
 *
 */
public class hoersaalParser {

	private static final String tag = "hoersaalParser";
	
	private final List<hoersaal> list;

	public hoersaalParser() {
		this.list = new ArrayList<hoersaal>();
	}


	public List<hoersaal> getList() {
		return this.list;
	}

	/**
	 * Parse XML file
	 * 
	 * @param inStream
	 */
	public void parse(XmlResourceParser parser) {
		try {
			while(parser.next() != XmlResourceParser.END_DOCUMENT){
				String name = parser.getName();
				String RaumID = null;
				String Raumname = null;
				String Adresse = null;
				String Sitzplaetze = null;
				String Raumart = null;
				
				if((name != null) && name.equals("hoersaal")){
					int size = parser.getAttributeCount();
					if(size > 1){
						for(int i=0; i < size;i++){
							String attrName = parser.getAttributeName(i);
							String attrValue = parser.getAttributeValue(i);
							if((attrName != null) && attrName.equals("RaumID")){
								RaumID = attrValue;
							}else if((attrName != null) && attrName.equals("Raumname")){
								Raumname = attrValue;
							}else if((attrName != null) && attrName.equals("Adresse")){
								Adresse = attrValue;
							}else if((attrName != null) && attrName.equals("Sitzplaetze")){
								Sitzplaetze = attrValue;
							}else if((attrName != null) && attrName.equals("Raumart")){
								Raumart = attrValue;
							}
						}
						hoersaal hoersaal = new hoersaal(RaumID,Raumname,Adresse,Sitzplaetze,Raumart);
						this.list.add(hoersaal);
						//Log.d(tag, hoersaal.toString());
					}
				}
				
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}