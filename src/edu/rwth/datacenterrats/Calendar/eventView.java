package edu.rwth.datacenterrats.Calendar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import edu.rwth.datacenterrats.R;
import edu.rwth.datacenterrats.Helper.GAHelper;
import edu.rwth.datacenterrats.Helper.HTTPHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


/**
 * EventView Class
 * Display the CalendarEvents.
 * @author Benjamin Grap
 *
 */
public class eventView extends Activity implements OnItemClickListener{
    
	public eventStore eventStore;
	public List<vEvent> eventlist;
	
	private static String username;
	private static String password;
	private EventArrayAdapter adapter;
	private Boolean failure = false;
	private ListView itemlist;
	private GAHelper tracker;
	private static Calendar cal1 = Calendar.getInstance();
	private static Calendar cal2 = Calendar.getInstance();
	HandlerThread uiThread;
	UIHandler uiHandler;
	
	
	private Boolean isSameDay(Date d1, Date d2){
		cal1.setTime(d1);
		cal2.setTime(d2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}
	
	public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.eventview);
        itemlist = (ListView) findViewById(R.id.eventlistView);
        
        uiThread = new HandlerThread("UIHandler");
    	uiThread.start();
    	uiHandler = new UIHandler( (Looper)uiThread.getLooper() );
        
        
        SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(this);
        username = app_preferences.getString("LoginCampus", "123456");
        password = app_preferences.getString("PassCampus", "");
        tracker = new GAHelper(this,"eventView");
        tracker.trackPageView("/eventView");
        new DownloadParseTask().execute();
    }
	
	private void UpdateDisplay()
    {
        
        if (eventStore != null){
        	eventStore.prune();
        	eventlist = (List<vEvent>) eventStore.getAllItems();
        }
        adapter = new EventArrayAdapter(eventView.this, R.layout.eventlistitem, eventlist);
        
        itemlist.setAdapter(adapter);
        
        itemlist.setOnItemClickListener(this);
        
        itemlist.setSelection(0);        
    }
	
	private void loadEvents(){
		String eventData;
		StringBuilder total = new StringBuilder();
		try{
			FileInputStream fis = getApplicationContext().openFileInput("eventData.data");
			BufferedReader r = new BufferedReader(new InputStreamReader(fis));
		    
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line + "\n");
			}
			if(total.indexOf("BEGIN:VCALENDAR")>=0){
				eventData = total.toString();
				//Log.i("Events-Loader","Loaded File");
				parseEventData(eventData);
			}else{
				//Log.i("Events-Loader","File Data not an iCal.");
				//Log.i("Events-Loader","Downloading Events");
				downloadEvents();
			}
		}catch(FileNotFoundException e){
			//Log.i("Events-Loader","Storage File not Found!");
			//Log.i("Events-Loader","Downloading Events");
			downloadEvents();	
		} catch (IOException e) {
			//Log.i("Events-Loader","Could not read from file!");
			//Log.i("Events-Loader","Downloading Events");
			downloadEvents();
			//e.printStackTrace();
		}
	}
	
	private void saveEvents(String eventData){
		FileOutputStream fos;
		try {
			fos = getApplicationContext().openFileOutput("eventData.data", Context.MODE_PRIVATE);
			fos.write(eventData.getBytes());
			fos.close();
			//Log.i("Events-Saver","Event Data written to File.");
		} catch (FileNotFoundException e1) {
			//Log.i("Events-Saver","Storage File not Found!");
		} catch (IOException e) {
			//Log.i("Events-Saver","Could not write to file!");
		}
	}
	
	private void downloadEvents(){
		HTTPHelper connect = new HTTPHelper();
		String eventData;
		//Log.i("HTTP-Cookies",connect.getCookies());
		List<NameValuePair> loginstuff = new ArrayList <NameValuePair>();
		loginstuff.add(new BasicNameValuePair("regwaygguid", ""));
		loginstuff.add(new BasicNameValuePair("evgguid", ""));
		loginstuff.add(new BasicNameValuePair("size", "1024"));
		loginstuff.add(new BasicNameValuePair("u", username));
		loginstuff.add(new BasicNameValuePair("p", password));
		loginstuff.add(new BasicNameValuePair("login", "> Login"));
		loginstuff.add(new BasicNameValuePair("Newsletter", ""));
		connect.get("https://www.campus.rwth-aachen.de/office/default.asp");
		//Log.i("HTTP-Cookies",connect.getCookies());
		connect.postData("https://www.campus.rwth-aachen.de/office/views/campus/redirect.asp",loginstuff);
		//Log.i("HTTP-Cookies",connect.getCookies());
		connect.get("https://www.campus.rwth-aachen.de/office/views/campus/groups.asp");
		//Log.i("HTTP-Cookies",connect.getCookies());
        eventData = connect.get("https://www.campus.rwth-aachen.de/office/views/calendar/iCalExport.asp?startdt=01.04.2011&enddt=30.09.2021%2023:59:59");
        //Log.i("HTTP-Cookies",connect.getCookies());
        saveEvents(eventData);
        parseEventData(eventData);
	}
	
	private void parseEventData(String eventData){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		eventStore = new eventStore();
		//Log.i("PARSER",eventData);
		String [] entries = eventData.split("BEGIN:VEVENT");
		//Log.i("PARSER","Parsing...");
		for(int i=1; i < entries.length;i++){
			//Log.i("PARSER",entries[i]);
			String [] lines = entries[i].split("\\r?\\n");
			//Log.i("PARSER","Lines: " + lines.length );
			if(lines.length > 12){
				//Log.i("PARSER",lines[0]);
				String uid = lines[1].split(":",-1)[1];
				String summary = lines[2].split(":",-1)[1];
				String location = lines[8].split(":",-1)[1];
				String category = lines[9].split(":",-1)[1];
				String description = lines[10].split(":",-1)[1];
				Date start;
				Date end;
				try{
					//Log.i("PARSER","StartDate: " + lines[4].split(":",-1)[1]);
					start = df.parse(lines[3].split(":",-1)[1]);
					//Log.i("PARSER","EndDate: " + lines[5].split(":",-1)[1]);
					end = df.parse(lines[4].split(":",-1)[1]);	
				}catch(ParseException e){
					start = new Date();
					end = new Date();
					//Log.i("PARSER","Failed to Parse Date");
				}
				eventStore.addItem(new vEvent(uid, summary, location, description, category, start, end));
			}
		}
		//Log.i("PARSER","Events read.");
	}
	
	@Override
    public void onItemClick(AdapterView parent, View v, int position, long id)
    {
   	 	//Log.i("eventView","item clicked! [" + hoersaallist.get(position).toString() + "]");
		tracker.trackEvent("Click", "Click", "EventItem", 1);
	   	Intent itemintent = new Intent(this,eventDescription.class);
	        
	   	Bundle b = new Bundle();
	   	 
	   	b.putString("Summary", eventlist.get(position).getSummary());
	   	b.putString("Start", eventlist.get(position).getStart());
	   	b.putString("End", eventlist.get(position).getEnd());
	   	b.putString("Location", eventlist.get(position).getLocation());
	   	b.putString("Category", eventlist.get(position).getCategory());
	   	b.putString("Description", eventlist.get(position).getDescription());
	   	
	   	itemintent.putExtra("android.intent.extra.INTENT", b);
	        
	   	startActivityForResult(itemintent,0); 
    }
	
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	tracker.destroy();
    }
    
	public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	
    	menu.add(0,0,0,"Re-download Calendar");
    	Log.i("eventView","onCreateOptionsMenu");
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
        case 0:
        	downloadEvents();
        	UpdateDisplay();
        	return true;
        case 1:
        	//
        	return true;
        }
        return false;
    }
	

	
	private class EventArrayAdapter extends ArrayAdapter<vEvent> {
		private static final String tag = "EventArraryAdapter";
		private Context context;
		private List<vEvent> objects;
		
		public EventArrayAdapter(Context context, int textViewResourceId, List<vEvent> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.objects = objects;
		}

		public int getCount() {
			return this.objects.size();
		}

		public vEvent getItem(int index) {
			return this.objects.get(index);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				// ROW INFLATION
				Log.d(tag, "Starting XML Row Inflation ... ");
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
				row = inflater.inflate(R.layout.eventlistitem, parent, false);
				Log.d(tag, "Successfully completed XML Row Inflation!");
			}

			// Get item
			vEvent event = getItem(position);
			if(event != null){
				// Get reference to Buttons
				TextView summary = (TextView)row.findViewById(R.id.eventsummary);
				TextView startDate = (TextView)row.findViewById(R.id.eventstartdate);
				TextView seperator = (TextView)row.findViewById(R.id.eventSeperator);
				//Set Text and Tags
				if(summary != null){
					summary.setText(event.getSummary());
				}
				if(startDate != null){
					startDate.setText(event.getStart());
					seperator.setText(event.getStart().substring(0,15));
					if(position > 0){
						if(isSameDay(event.getStartDate(), getItem(position-1).getStartDate())){
							seperator.setVisibility(View.GONE);
						}else{
							seperator.setVisibility(View.VISIBLE);
						}
					}else{
						seperator.setVisibility(View.VISIBLE);
					}
				}
				
			}
			return row;
		}
	}
	
	private class DownloadParseTask extends AsyncTask{
		@Override
		protected void onPreExecute(){
			showDialog(DIALOG_LOADING);
		}
		
		@Override
		protected Object doInBackground(Object... params) {
			if(isOnline()){
				if(!(username.equals("123456") || password.equals(""))){
		        	loadEvents();    
		            return true;
		        }else{
		        	handleUIRequest(0);
		        	return false;
		        }
	        }else{
	        	handleUIRequest(1);
	        	return false;
	        }
		}
		
		@Override
		protected void onPostExecute(Object result){
			if(!failure){
        		UpdateDisplay();
        		dismissDialog(DIALOG_LOADING);
        	}
		}
	}
	
	protected void handleUIRequest(int message){
	    Message msg = uiHandler.obtainMessage(message);
	    msg.obj = message;
	    uiHandler.sendMessage(msg);
	}
	
	private final class UIHandler extends Handler{
	    public static final int DISPLAY_UI_NO_PASS = 0;
	    public static final int DISPLAY_UI_NO_NETWORK = 1;
	    public static final int DISPLAY_UI_REMOVE_DIAG = 2;
	    
	    
	    public UIHandler(Looper looper){
	        super(looper);
	    }

	    @Override
	    public void handleMessage(Message msg){
	        switch(msg.what){
	        case UIHandler.DISPLAY_UI_NO_PASS:{
	        	dismissDialog(DIALOG_LOADING);
	        	showDialog(DIALOG_CAMPUS_WRONGPASS);
	        	failure = true;
	        }case UIHandler.DISPLAY_UI_NO_NETWORK:{
	        	dismissDialog(DIALOG_LOADING);
	        	showDialog(DIALOG_NO_NETWORK);
	        	failure = true;
	        }case UIHandler.DISPLAY_UI_REMOVE_DIAG:{
	        	
	        }default:
	            break;
	        }
	    }
	}
	
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
    static final int DIALOG_NOT_IMPLEMENTED = 4;
	
    protected Dialog onCreateDialog(int id){
    	Dialog dialog;
    	AlertDialog.Builder builder;
    	switch(id) {
    	case DIALOG_LOADING:
    		dialog = ProgressDialog.show(this, "", "Loading...", true);
    		break;
    	case DIALOG_NO_NETWORK:
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle("Network failure");
    		builder.setMessage("You are not connected to any Network. To use this feature you need Internet access.");
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
    	case DIALOG_NOT_IMPLEMENTED:
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle("Not Implemented");
    		builder.setMessage("This function is not implemented yet. Stay tuned for a later release.");
    		dialog = builder.create();
    		break;
    	default:
    		dialog = null;
    	}
    	return dialog;
    }
}
