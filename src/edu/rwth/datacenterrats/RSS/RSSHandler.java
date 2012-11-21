package edu.rwth.datacenterrats.RSS;


import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;


/**
 * RSS Handler that handles RSS Items for the SAX Parser.
 * @author
 *
 */
public class RSSHandler extends DefaultHandler 
{

    RSSFeed _feed;
    RSSItem _item;
    String _lastElementName = "";
    boolean bFoundChannel = false;
    final int RSS_TITLE = 1;
    final int RSS_LINK = 2;
    final int RSS_DESCRIPTION = 3;
    final int RSS_CATEGORY = 4;
    final int RSS_PUBDATE = 5;

    int depth = 0;
    String currentValue = "";

    RSSHandler(){
    	
    }

    /*
     * return feed
     */
    RSSFeed getFeed(){
        return _feed;
    }


    public void startDocument() throws SAXException{
        _feed = new RSSFeed();
        _item = new RSSItem();
    }
    
    public void endDocument() throws SAXException{
    	
    }
    
    public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException {
        depth++;
        currentValue = "";
        if (localName.equals("item")){
            // create a new item
            _item = new RSSItem();
            return;
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException{
        depth--;

        if (localName.equals("title")){
            _item.setTitle(currentValue);
            return;
        }
        if (localName.equals("description")){
            _item.setDescription(currentValue);
            return;
        }
        if (localName.equals("link")){
            _item.setLink(currentValue);
            return;
        }
        if (localName.equals("category")){
            _item.setCategory(currentValue);
            return;
        }
        if (localName.equals("pubDate")){
            _item.setPubDate(currentValue);
            return;
        }
        if (localName.equals("item")){
            // add our item to the list!
            _feed.addItem(_item);
            return;
        }
    }

    public void characters(char ch[], int start, int length){
        currentValue += new String(ch,start,length);
        return;        
    }
}

