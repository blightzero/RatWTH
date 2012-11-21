package edu.rwth.datacenterrats.L2P;

import java.util.List;
import java.util.Vector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ProgressDialog;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import edu.rwth.datacenterrats.R;
import edu.rwth.datacenterrats.Helper.GAHelper;
import edu.rwth.datacenterrats.Helper.HTTPHelper;
/**
 * Activity that Displays the parsed Data from the Materials Section Downloaded from the L2P Website. 
 * @author
 *
 */
public class Materials extends Activity {  
	
	private static final String l2pLink = "https://www2.elearning.rwth-aachen.de";
	
	private View.OnClickListener l2pButtonListener = new View.OnClickListener(){
		@Override
		public void onClick(View v){
			tracker.trackEvent("Click", "Click", "MaterialDownload", 1);
			startBrowser(l2pLink+v.getTag());
		}
	};
	
	private void startBrowser(String Url){
		String url=Url;
		if(url.contains("dehtt")==true){url = url.substring(url.indexOf(".de")+3);
		Intent browserIntent = new Intent("android.intent.action.VIEW",Uri.parse(url));
		startActivity(browserIntent);
		}else{
		Intent browserIntent = new Intent("android.intent.action.VIEW",Uri.parse(Url));
		startActivity(browserIntent);
		}
	}
	
   
	/**
	 * Task that parses the HTML file returned from the L2P Website.
	 * 
	 * @author Benjamin Grap
	 *
	 */
	private class DownloadParseTask extends AsyncTask{
		@Override
		protected void onPreExecute(){
			showDialog(DIALOG_LOADING);
		}
		
		@Override
		protected Object doInBackground(Object... params) {
	        //Get the Url of the L2P Learnroom
	        
	        HTTPHelper pageget = new HTTPHelper();
	        String allData = pageget.getData(leanroom_url, username, password);
	        
	        materials = parse_HTML(allData);
	        
	        return materials;
		}
		
		@Override
		protected void onPostExecute(Object result){
	        MaterialArrayAdapter adapter = new MaterialArrayAdapter(Materials.this, R.layout.rsslistitem, materials);
	        materialList.setAdapter(adapter);
	        dismissDialog(DIALOG_LOADING);
		}
	}
	
	/**
	 * Check whether there is an Online Connection.
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
    
	//DIALOG Constants.
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
    
    
    private String leanroom_url;
	private String username;
	private String password;
	private List<Materiallist> materials;
	private ListView materialList;
	GAHelper tracker;
	
    @Override  
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);  
        setContentView(R.layout.l2prooms);
        tracker = new GAHelper(this,"Materials");
		tracker.trackPageView("/L2P/Materials");
        
        materialList = (ListView) findViewById(R.id.RoomlistView);	
		
		SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(this);
        username = app_preferences.getString("LoginL2P", "ab123456");
        password = app_preferences.getString("PassL2P", "ab123456");
        leanroom_url = getIntent().getExtras().getString("url");
        
        //Chech whether we are online.
        if(!isOnline()){
			materials = new Vector<Materiallist>(0);
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
     * Parse the HTML file for all the files that are downloadable.
     * @param allData
     * @return
     */
    private List<Materiallist> parse_HTML(String allData){
		List<Materiallist> materials = new Vector<Materiallist>(0);
		String name;
        String link;
		Integer start = allData.indexOf("ms-vb-icon");
		if (start >= 0){
		    allData = allData.substring(start+12);
		    Integer endindex= allData.indexOf("</TABLE>");
		    if(endindex >=0){
			    allData = allData.substring(0, endindex);
			    String [] table_rows = allData.split("\"ms-vb-icon\">");
				       
		        if(table_rows == null){
		        	//Log.d("Something went wrong!");
		        }else{
		        	
		        	for(int i=0;i<table_rows.length;i++){
		        		String [] row_entries = table_rows[i].split("<*>");
		        		if(row_entries.length < 29) continue;
		        		start = row_entries[0].indexOf("\"",0);
		        		endindex = row_entries[0].indexOf("\"",start+1);
		        		link = row_entries[0].substring(start+1,endindex);
		        		//Log.d("link", link);
		        		name = row_entries[1].substring(row_entries[1].indexOf("\" title=\"")+9, row_entries[1].indexOf("\" SRC=\""));
		        		//Log.d("name", name);
		        		materials.add(new Materiallist(name,link));
		
			        }
		        }
		    }
		}
	    return materials;   
	}

    public void onListItemClick(ListView parent, View v,  int position, long id) 
    {   
    	//We don't need to do anything here.
	}
    
    /**
     * Simple ArrayAdapter to Display the Material with a nice clickable Button.
     * @author Benjamin Grap
     *
     */
	private class MaterialArrayAdapter extends ArrayAdapter<Materiallist> {
		private static final String tag = "MaterialArrayAdapter";
		private Context context;
		private List<Materiallist> objects;
		
		public MaterialArrayAdapter(Context context, int textViewResourceId, List<Materiallist> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;

			this.objects = objects;
		}
			public int getCount() {
				return this.objects.size();
			}

			public Materiallist getItem(int index) {
				return this.objects.get(index);
			}
			
			public View getView(int position, View convertView, ViewGroup parent) {
				View row = convertView;
				if (row == null) {
					//Log.d(tag, "Starting XML Row Inflation ... ");
					LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
					row = inflater.inflate(R.layout.materialslist, parent, false);
					//Log.d(tag, "Successfully completed XML Row Inflation!");
				}
				Materiallist materialitem = getItem(position);
				if(materialitem != null){
					// Get reference to Buttons
					ImageButton l2pButton = (ImageButton)row.findViewById(R.id.l2pbutton);
					TextView l2pnameText = (TextView)row.findViewById(R.id.l2pnameText);
					//Set Text and Tags
					//We need the Tag in order to identify the button later.
					if(l2pnameText != null){
						l2pnameText.setText(materialitem.getName());
					}
					
					if(l2pButton != null){
						l2pButton.setTag(materialitem.getlink());
						l2pButton.setOnClickListener(l2pButtonListener);
					}	
				}
				return row;
			}
	}
}