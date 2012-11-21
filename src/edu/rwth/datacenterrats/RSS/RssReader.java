package edu.rwth.datacenterrats.RSS;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener; 
import android.util.Log;
import java.util.List;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import android.content.Intent;
import edu.rwth.datacenterrats.R;
import edu.rwth.datacenterrats.Helper.GAHelper;

/**
 * RssReader Activity - Displays an RSS Feed based on the URL supplied in the Intent.
 * @author
 *
 */
public class RssReader extends Activity implements OnItemClickListener{
	public String RSSFEEDOFCHOICE;
	public String loginCampus;
	public final String tag = "RSSReader";
	private RSSFeed feed = null;
	public static String username;
	public static String password;
	public static String rsstime;
	GAHelper tracker;

	/** Called when the activity is first created. */
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.rssfeed);
        tracker = new GAHelper(this,"RSSFeed");
        tracker.trackPageView("/RSSFeed");
        SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(this);
        username = app_preferences.getString("LoginL2P", "ab123456");
        password = app_preferences.getString("PassL2P", "ab123456");
        rsstime = app_preferences.getString("RSSTime","ThreeDays");
        Intent startingIntent = getIntent();
        
        if (startingIntent != null){
        	Bundle b = startingIntent.getBundleExtra("android.intent.extra.INTENT");
        	if (b == null){
        		RSSFEEDOFCHOICE =  "https://www2.elearning.rwth-aachen.de/foyer/recently/default.aspx?Feed=True&Date="+rsstime;
        	}
        	else{
        		RSSFEEDOFCHOICE =  b.getString("feed");
    		}
        }else{
        	RSSFEEDOFCHOICE =  "https://www2.elearning.rwth-aachen.de/foyer/recently/default.aspx?Feed=True&Date="+rsstime;
        }
        new DownloadParseTask().execute();
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	tracker.destroy();
    }

    private class DownloadParseTask extends AsyncTask{
		@Override
		protected void onPreExecute(){
			showDialog(DIALOG_LOADING);
		}
		
		@Override
		protected Object doInBackground(Object... params) {
			if(isOnline()){
		        feed = getFeed(RSSFEEDOFCHOICE);
		        if(feed != null){
		        	feed.sort();
		        }else{
		        	dismissDialog(DIALOG_LOADING);
		        	showDialog(DIALOG_NO_FEED);
		        }
	        }else{
	        	feed = null;
	        	dismissDialog(DIALOG_LOADING);
	        	showDialog(DIALOG_NO_NETWORK);
	        }
	        return null;
		}
		
		@Override
		protected void onPostExecute(Object result){
	        UpdateDisplay();
	        dismissDialog(DIALOG_LOADING);
		}
	}
    
    
    public static InputStream getInputStreamFromUrl(String url) {
		InputStream content = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
			httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet));

			//HttpsGet
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			content = response.getEntity().getContent();
        } catch (Exception e) {
			return content;
		}
		return content;

    }
    
    /**
     * get and parse the RSSFeed
     * @param urlToRssFeed
     * @return
     */
    private RSSFeed getFeed(String urlToRssFeed){
    	try{
           // create the factory
           SAXParserFactory factory = SAXParserFactory.newInstance();
           // create a parser
           SAXParser parser = factory.newSAXParser();

           // create the reader (scanner)
           XMLReader xmlreader = parser.getXMLReader();
           // instantiate our handler
           RSSHandler theRssHandler = new RSSHandler();
           // assign our handler
           xmlreader.setContentHandler(theRssHandler);
           // get our data via the url class
           InputSource is = new InputSource(getInputStreamFromUrl(urlToRssFeed));
           // perform the synchronous parse           
           xmlreader.parse(is);
           // get the results - should be a fully populated RSSFeed instance, or null on error
           return theRssHandler.getFeed();
    	} catch (Exception ee) {
    		TextView feedtitle = (TextView) findViewById(R.id.feedtitle);
    		feedtitle.setText(ee.getMessage() + ee.toString());
    		// if we have a problem, simply return null
    		return null;
    	}
    }
    
    /**
     * Create a Menu when user presses the Menu Key.
     */
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	
    	menu.add(0,0,0,"Refresh");
    	//Log.i(tag,"onCreateOptionsMenu");
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
        case 0:
        	//Log.i(tag,"Refreshing RSS Feed");
        	new DownloadParseTask().execute();
        	UpdateDisplay();
            return true;
        case 1:
        	return true;
        }
        return false;
    }
    
    /**
     * Update the displayed data.
     * Downloaded RSS Feed and 
     */
    private void UpdateDisplay(){
    	
        TextView feedtitle = (TextView) findViewById(R.id.feedtitle);
        ListView itemlist = (ListView) findViewById(R.id.itemlist);
        
        if (feed == null){
        	feedtitle.setText("No RSS Feed Available!");
        	return;
        }else{
        	feedtitle.setText("");
        }

        List<RSSItem> rssItemList = (List<RSSItem>) feed.getAllItems();
        RssArrayAdapter adapter = new RssArrayAdapter(RssReader.this, R.layout.rsslistitem, rssItemList);
        
        itemlist.setAdapter(adapter);
        itemlist.setOnItemClickListener(this);
        itemlist.setSelection(0);
    }
    
    
    public void onItemClick(AdapterView parent, View v, int position, long id){
    	 tracker.trackEvent("Click", "Click", "RSSItemSelected", 1);
    	 Intent itemintent = new Intent(this,ShowDescription.class);
         
    	 Bundle b = new Bundle();
    	 b.putString("title", feed.getItem(position).getTitle());
    	 b.putString("description", feed.getItem(position).getDescription());
    	 b.putString("link", feed.getItem(position).getLink());
    	 b.putString("pubdate", feed.getItem(position).getPubDate());
    	 
    	 itemintent.putExtra("android.intent.extra.INTENT", b);
         
    	 startActivityForResult(itemintent,0);
    }
     
     
    /**
     * Check whether we have an online connection. 
     * @return
     */
    public boolean isOnline() {
         ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo netInfo = cm.getActiveNetworkInfo();
         if (netInfo != null && netInfo.isConnectedOrConnecting()) {
             return true;
         }
         return false;
    }
     
 	static final int DIALOG_LOADING = 0;
 	static final int DIALOG_NO_NETWORK = 1;
 	static final int DIALOG_NO_FEED = 2;
 	static final int DIALOG_CAMPUS_WRONGPASS = 3;
     
     protected Dialog onCreateDialog(int id){
     	Dialog dialog;
     	AlertDialog.Builder builder;
     	switch(id) {
     	case DIALOG_LOADING:
     		dialog = ProgressDialog.show(this, "", "Loading RSS Feed...", true);
     		break;
     	case DIALOG_NO_NETWORK:
     		builder = new AlertDialog.Builder(this);
     		builder.setTitle("Network failure");
     		builder.setMessage("You are not connected to any Network. To view RSS-Feeds you must be connected to a network.");
     		dialog = builder.create();
     		break;
     	case DIALOG_NO_FEED:
     		builder = new AlertDialog.Builder(this);
     		builder.setTitle("Could not download Feed");
     		builder.setMessage("The specified Feed could not be downloaded.");
     		dialog = builder.create();
     		break;
     	case DIALOG_CAMPUS_WRONGPASS:
     		builder = new AlertDialog.Builder(this);
     		builder.setTitle("Wrong Username/Password for Campus");
     		builder.setMessage("Please make sure you entered the right username/password. We could not verify your username and password.");
     		dialog = builder.create();
     		break;
     	default:
     		dialog = null;
     	}
     	return dialog;
     }    
}
