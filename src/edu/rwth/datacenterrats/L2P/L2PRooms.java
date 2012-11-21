package edu.rwth.datacenterrats.L2P;

import java.util.List;
import java.util.Vector;

import edu.rwth.datacenterrats.R;
import edu.rwth.datacenterrats.Helper.GAHelper;
import edu.rwth.datacenterrats.Helper.HTTPHelper;
import edu.rwth.datacenterrats.R.id;
import edu.rwth.datacenterrats.R.layout;
import edu.rwth.datacenterrats.RSS.RssReader;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.*;

/**
 * Displays the L2P Rooms of the User.
 * 
 * @author Benjamin Grap
 *
 */
public class L2PRooms extends Activity {
	private static final String l2pLink = "https://www2.elearning.rwth-aachen.de";
	ListView roomList;
	RoomArrayAdapter adapter;
	List<LearnRoom> l2pRoomslist;
	GAHelper tracker;
	
	/**
	 * OnclickListener for the RSSButton in the ListItem.
	 * Detects the URL via the Elements Tag
	 */
	private View.OnClickListener rssButtonListener = new View.OnClickListener(){
		@Override
		public void onClick(View v){
			tracker.trackEvent("Click", "Click", "RSSRoom", 1);
			startRSSReader(l2pLink+v.getTag());
		}
	};
	
	/**
	 * OnclickListener for the L2pButton in the ListItem.
	 * Detects the URL via the Elements Tag
	 */
	private View.OnClickListener l2pButtonListener = new View.OnClickListener(){
		@Override
		public void onClick(View v){
			tracker.trackEvent("Click", "Click", "L2P", 1);
			startL2pSections(l2pLink+v.getTag());
		}
	};
	
	/**
	 * OnclickListener for the CampusButton in the ListItem.
	 * Detects the URL via the Elements Tag
	 */
	private View.OnClickListener campusButtonListener = new View.OnClickListener(){
		@Override
		public void onClick(View v){
			tracker.trackEvent("Click", "Click", "Campus", 1);
			startBrowser((String)v.getTag());
		}
	};
	/**
	 * Start a Browsing Intent with the supplied URL.
	 * @param Url
	 */
	private void startBrowser(String Url){
		Intent browserIntent = new Intent("android.intent.action.VIEW",Uri.parse(Url));
		startActivity(browserIntent);
	}
	/**
	 * Starts the RSS View Intent with the URL to an RSS Feed.
	 * @param Url
	 */
	private void startRSSReader(String Url){
		Intent itemintent = new Intent(this,RssReader.class);
        
	   	Bundle b = new Bundle();
	   	b.putString("feed", Url);
	   	 
	   	itemintent.putExtra("android.intent.extra.INTENT", b);    
	   	startActivityForResult(itemintent,0);
	}
	
	/**
	 * Start the L2P Sections Intent to browse the Materials List.
	 * @param Url
	 */
	private void startL2pSections(String Url){
		Intent l2psections = new Intent(this,Sections.class);

		l2psections.putExtra("url", Url);
		startActivity(l2psections);
	}
	
	/**
	 * Check whether we have an Internet Connection.
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
    
    //Define some Constants we use for Dialog-creation.
	static final int DIALOG_LOADING = 0;
	static final int DIALOG_NO_NETWORK = 1;
	static final int DIALOG_L2P_WRONGPASS = 2;
	static final int DIALOG_CAMPUS_WRONGPASS = 3;
    
    protected Dialog onCreateDialog(int id){
    	Dialog dialog;
    	AlertDialog.Builder builder;
    	switch(id) {
    	case DIALOG_LOADING:
    		dialog = ProgressDialog.show(this, "", "Downloading...", true);
    		break;
    	case DIALOG_NO_NETWORK:
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle("Network failure");
    		builder.setMessage("You are not connected to any Network. To see your L2P Rooms you need a network connection.");
    		dialog = builder.create();
    		break;
    	case DIALOG_L2P_WRONGPASS:
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle("Wrong Username/Password for L2P");
    		builder.setMessage("Please make sure you entered the right username/password. We could not verify your username and password.");
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
    
    /**
	 * onCreate function of the Activity
	 * checks for online connection and starts the download & parse task.
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.l2prooms);
		tracker = new GAHelper(this,"L2P");
		tracker.trackPageView("/L2P");
		List<LearnRoom> l2pRooms;

		roomList = (ListView) findViewById(R.id.RoomlistView);
		SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(this);
        String username = app_preferences.getString("LoginL2P", "ab123456");
        String password = app_preferences.getString("PassL2P", "ab123456");
        
		if(!isOnline()){
			l2pRooms = new Vector<LearnRoom>(0);
        	showDialog(DIALOG_NO_NETWORK);
        }else{
        	new DownloadParseTask().execute(username,password);
        }
       
   
	}
	
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	tracker.destroy();
    }
    
    /**
     * A Task that downloads the L2P Homepage and parses it.
     * 
     * @author Benjamin Grap
     *
     */
	private class DownloadParseTask extends AsyncTask{
		@Override
		protected void onPreExecute(){
			//Show loading Dialog.
			showDialog(DIALOG_LOADING);
		}
		
		@Override
		protected Object doInBackground(Object... params) {
			List<LearnRoom> l2pRooms;
			String username = (String) params[0];
			String password = (String) params[1];
			
	        HTTPHelper pageget = new HTTPHelper();
	        //download Data
	        String allData = pageget.getData("https://www2.elearning.rwth-aachen.de/foyer/summary/default.aspx", username, password);
	        
	        //If Data seems ok, parse it.
	        if(allData.length() <= 1000){
	        	showDialog(DIALOG_NO_NETWORK);
	        	l2pRooms = new Vector<LearnRoom>(0);
	        }else{
	        	l2pRooms = parse_HTML(allData);
	        }
			return l2pRooms;
		}
		
		@Override
		protected void onPostExecute(Object result){
			l2pRoomslist = (List<LearnRoom>) result;
			//Display the List of L2P Rooms.
			adapter = new RoomArrayAdapter(L2PRooms.this, R.layout.rsslistitem, l2pRoomslist);
	        roomList.setAdapter(adapter);
	        //remove the loading Dialog.
	        dismissDialog(DIALOG_LOADING);
		}
	}
	/**
	 * Parse the HTML File downloaded from the L2P Homepage.
	 * @param allData
	 * @return
	 */
	private List<LearnRoom> parse_HTML(String allData){
		List<LearnRoom> l2pRooms = new Vector<LearnRoom>(0);
		String name;
        String linkl2p;
        String linkcampus;
        String rssfeed;
		Integer start_index = allData.indexOf("ms-viewheadertr");
        Integer end_index = allData.indexOf("ms-PartSpacingVertical",start_index);
        if(start_index >= 0 && end_index > start_index && end_index >= 0){
	        allData = allData.substring(start_index,end_index);
	        
	        start_index = allData.indexOf("</table>");
	        end_index = allData.indexOf("</table>",start_index+10);
	        if(start_index >= 0 && end_index > start_index && end_index >= 0){
		        allData = allData.substring(start_index,end_index);
		        //Log.d("L2PRooms",start_index + " " + end_index);
		        String [] table_rows = allData.split("<tr");
		        
		        //Spanned htmlpage =  Html.fromHtml(allData);
		        if(table_rows == null){
		        	//Log.d("Something went wrong!");
		        }else{
		        	
		        	for(int i=1;i<table_rows.length;i++){
		        		String [] row_entries = table_rows[i].split("<*>");
		        		if(row_entries.length < 29) continue;
		        		start_index = row_entries[5].indexOf("\"",0);
		        		end_index = row_entries[5].indexOf("\"",start_index+1);
		        		linkl2p = row_entries[5].substring(start_index+1,end_index);
		        		
		        		start_index = row_entries[10].indexOf("\"",0);
		        		end_index = row_entries[10].lastIndexOf("\"");
		        		linkcampus = row_entries[10].substring(start_index+1,end_index);
		        		
		        		start_index = row_entries[25].indexOf("\"",0);
		        		end_index = row_entries[25].lastIndexOf("\"");
		        		rssfeed = row_entries[25].substring(start_index+1,end_index);
		        		
		        		name = row_entries[16].substring(0,row_entries[16].indexOf("<"));
			        	l2pRooms.add(new LearnRoom(name,linkl2p,linkcampus,rssfeed));
		        		//Log.d("L2PRooms",name + " " + linkl2p +" "+ linkcampus +" "+ rssfeed);
			        }
		        }
	        }
        }
	        //Log.d("L2PRooms",url_strings);
	    return l2pRooms;   
	}
	
	private class RoomArrayAdapter extends ArrayAdapter<LearnRoom> {
		private static final String tag = "L2pRoomArrayAdapter";
		private Context context;
		private List<LearnRoom> objects;
		
		public RoomArrayAdapter(Context context, int textViewResourceId, List<LearnRoom> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.objects = objects;
		}

		public int getCount() {
			return this.objects.size();
		}

		public LearnRoom getItem(int index) {
			return this.objects.get(index);
		}
		
		

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				//Log.d(tag, "Starting XML Row Inflation ... ");
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
				row = inflater.inflate(R.layout.roomlistitem, parent, false);
				//Log.d(tag, "Successfully completed XML Row Inflation!");
			}

			// Get item
			LearnRoom learnroom = getItem(position);
			if(learnroom != null){
				// Get reference to Buttons
				ImageButton rssButton = (ImageButton)row.findViewById(R.id.rssButton);
				ImageButton l2pButton = (ImageButton)row.findViewById(R.id.l2pbutton);
				ImageButton campusButton = (ImageButton)row.findViewById(R.id.campusButton);
				TextView l2pnameText = (TextView)row.findViewById(R.id.l2pnameText);
				
				//Set Text and Tags
				//Tags are important because they allow us to identify the Clicked Object later.
				if(l2pnameText != null){
					l2pnameText.setText(learnroom.getName());
				}
				if(rssButton !=null){
					rssButton.setTag(learnroom.getrssfeed());
					rssButton.setOnClickListener(rssButtonListener);
				}
				if(l2pButton != null){
					l2pButton.setTag(learnroom.getlinkl2p());
					l2pButton.setOnClickListener(l2pButtonListener);
				}
				if(campusButton != null){
					campusButton.setTag(learnroom.getlinkcampus());
					campusButton.setOnClickListener(campusButtonListener);
				}
			}
			return row;
		}
	}
	
	
	

}
