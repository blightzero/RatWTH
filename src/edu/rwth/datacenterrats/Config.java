package edu.rwth.datacenterrats;


import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import edu.rwth.datacenterrats.Helper.HTTPHelper;
import edu.rwth.datacenterrats.RSS.RssReader;
/**
 * The Preferences View of the App.
 * This is the Main starting point the first time the App is started.
 * 
 * @author Benjamin Grap
 *
 */
public class Config extends Activity {

	private Button saveButton;
	private Button resetButton;
	private EditText text1;
	private EditText text2;
	private EditText text3;
	private EditText text4;
	private Spinner spinner1;
	private int selection=1;
	
	static final int DIALOG_LOADING = 0;
	static final int DIALOG_NO_NETWORK = 1;
	static final int DIALOG_L2P_WRONGPASS = 2;
	static final int DIALOG_CAMPUS_WRONGPASS = 3;
	static final int DIALOG_SELECT_RSSTIME = 5;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        String[] choices = {"Today","Three Days","One Week", "Two Weeks"};
        //Get Objects from the Resources Field.
    	this.text1 = (EditText) findViewById(R.id.editText1);
    	this.text2 = (EditText) findViewById(R.id.editText2);
    	this.text3 = (EditText) findViewById(R.id.editText3);
    	this.text4 = (EditText) findViewById(R.id.editText4);
    	this.saveButton = (Button)this.findViewById(R.id.Save);
    	this.resetButton = (Button)this.findViewById(R.id.Reset);
    	this.spinner1 = (Spinner) this.findViewById(R.id.spinner1);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,choices);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner1.setAdapter(adapter);
    	spinner1.setOnItemSelectedListener( new MyOnItemSelectedListener());
        
    	//Setup Callback functionalities.
        this.saveButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
        	  Save_Passwords_Clicked();	
          }
        });
                
        this.resetButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
        	  Reset_Passwords_Clicked();
          }
        });
        
        //Further Code Logic.
        Load_Preferences();
    }
    
    private class MyOnItemSelectedListener implements OnItemSelectedListener {

    	@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long which) {
			selection = pos;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
    	
    }
    /**
     * function starts a RSS Reader Intent.
     */
    public void startRssReader(){
    	startActivity(new Intent(this, RssReader.class));
    }
    
    /**
     * Load all the Preferences from the PreferenceManager of the android system.
     * returns true or false based on whether we have a working config.
     * @return
     */
    public boolean Load_Preferences(){
    	SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(this);
    	this.text1.setText(app_preferences.getString("LoginL2P", "ab123456"));
    	this.text2.setText(app_preferences.getString("PassL2P", ""));
    	this.text3.setText(app_preferences.getString("LoginCampus", "123456"));
    	this.text4.setText(app_preferences.getString("PassCampus", ""));
    	String rsstime = app_preferences.getString("RSSTime", "ThreeDays");
    	if(rsstime.equals("Today")){
    		spinner1.setSelection(0);
    	}else if(rsstime.equals("ThreeDays")){
    		spinner1.setSelection(1);
    	}else if(rsstime.equals("OneWeek")){
    		spinner1.setSelection(2);
    	}else{
    		spinner1.setSelection(3);
    	}
    	
    	if(app_preferences.getString("PassL2P", "") == ""){
    		return false;
    	}else{
    		return true;
    	}
    }
    /**
     * Save the Passwords...
     * Check all the information that was entered first.
     */
    public void Save_Passwords_Clicked(){
    	new DownloadParseTask().execute(this);
    }
    
    protected Dialog onCreateDialog(int id){
    	Dialog dialog;
    	AlertDialog.Builder builder;
    	switch(id) {
    	case DIALOG_LOADING:
    		dialog = ProgressDialog.show(this, "", "Checking...", true);
    		break;
    	case DIALOG_NO_NETWORK:
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle("Network failure");
    		builder.setMessage("You are not connected to any Network. To check whether your username and password are correct we need a network connection.");
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
    	case DIALOG_SELECT_RSSTIME:
    		String[] choices = {"Today","Three Days","One Week", "Two Weeks"};
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle("Select Timeframe of RSS-Items");
    		builder.setSingleChoiceItems(choices, selection, new DialogSelectionClickHandler());
    		builder.setIcon(R.drawable.l2p_logo_home);
    		dialog = builder.create();
    		break;
    	default:
    		dialog = null;
    	}
    	return dialog;
    }
    
    
    /**
     * Private Class for the Spinner Selection.
     * Tells us what the user has chosen.
     * @author Benjamin Grap
     *
     */
    private class DialogSelectionClickHandler implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			selection = which;
			SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(Config.this);
			SharedPreferences.Editor editor = app_preferences.edit();
	       	String rsstime;
	       	switch (selection){
	       		case 0:
	       			rsstime = "Today";
	       			break;
	       		case 1:
	       			rsstime = "ThreeDays";
	       			break;
	       		case 2:
	       			rsstime = "OneWeek";
	       			break;
	       		case 3:
	       			rsstime = "TwoWeeks";
	       			break;
	       		default:
	       			rsstime = "ThreeDays";
	       	}
			editor.putString("RSSTime", rsstime );
	       	editor.commit(); // Very important
		}
    	
    }
    /**
     * Reset all the Textfields.
     */
    public void Reset_Passwords_Clicked(){

    	
    	this.text1.setText("");
    	this.text2.setText("");
    	this.text3.setText("");
    	this.text4.setText("");
    }
    /**
     * Taks that checks all the configuration.
     * @author Benjamin Grap
     *
     */
    private class DownloadParseTask extends AsyncTask{
		@Override
		protected void onPreExecute(){
			showDialog(DIALOG_LOADING);
		}
		
		
		protected Object doInBackground(Object... params) {
			Integer returnvalue = 3;
	    	SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(Config.this);
	    	if(isOnline()){
	    		if(checkUserPassL2p()){
	    			if(checkUserPassCampus()){
	    				SharedPreferences.Editor editor = app_preferences.edit();
	    		       	editor.putString("LoginL2P", Config.this.text1.getText().toString());
	    		       	editor.putString("PassL2P", Config.this.text2.getText().toString());
	    		    	editor.putString("LoginCampus", Config.this.text3.getText().toString());
	    		    	editor.putString("PassCampus", Config.this.text4.getText().toString());
	    		    	String rsstime;
	    		       	switch (selection){
	    		       		case 0:
	    		       			rsstime = "Today";
	    		       			break;
	    		       		case 1:
	    		       			rsstime = "ThreeDays";
	    		       			break;
	    		       		case 2:
	    		       			rsstime = "OneWeek";
	    		       			break;
	    		       		case 3:
	    		       			rsstime = "TwoWeeks";
	    		       			break;
	    		       		default:
	    		       			rsstime = "ThreeDays";
	    		       	}
	    				editor.putString("RSSTime", rsstime );
	    		       	editor.commit(); // Very important
	    		        
				        returnvalue = 0;
	    			}else{
	    				returnvalue = 1;
	    			}
	    		}else{
	    			returnvalue = 2;
			    }
	    	}else{
	    		returnvalue = 3;
	    	}
	    	return returnvalue;
		}
		
		@Override
		protected void onPostExecute(Object result){
			Integer answer = (Integer) result;
			dismissDialog(DIALOG_LOADING);
			switch(answer){
			case 0:
				finish();
				break;
			case 1:
				showDialog(DIALOG_CAMPUS_WRONGPASS);
				break;
			case 2:
				showDialog(DIALOG_L2P_WRONGPASS);
				break;
			case 3:
				showDialog(DIALOG_NO_NETWORK);
				break;
			default:
				showDialog(DIALOG_NO_NETWORK);		
			}
		}
	}
    /**
     * Check whether we have an Internetconnection.
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
    
    /**
     * Try to login to L2P to verify the users Username and Password.
     * @return
     */
    public boolean checkUserPassL2p() {
    	String l2pusername = this.text1.getText().toString();
    	String l2pass = this.text2.getText().toString();
    	
    	HTTPHelper pageget = new HTTPHelper();
        String allData = pageget.getData("https://www2.elearning.rwth-aachen.de/foyer/summary/default.aspx", l2pusername, l2pass);
        if (pageget.getStatusCode() != 200){	
        	return false;
        }
        return true; 
    }
    
    /**
     * Login to Campus in order to verify the username and password.
     * @return
     */
    public boolean checkUserPassCampus(){
    	String campususername = this.text3.getText().toString();
    	String campuspass = this.text4.getText().toString();
    	
       	HTTPHelper connect = new HTTPHelper();

		List<NameValuePair> loginstuff = new ArrayList <NameValuePair>();
		loginstuff.add(new BasicNameValuePair("regwaygguid", ""));
		loginstuff.add(new BasicNameValuePair("evgguid", ""));
		loginstuff.add(new BasicNameValuePair("size", "1024"));
		loginstuff.add(new BasicNameValuePair("u", campususername));
		loginstuff.add(new BasicNameValuePair("p", campuspass));
		loginstuff.add(new BasicNameValuePair("login", "> Login"));
		loginstuff.add(new BasicNameValuePair("Newsletter", ""));
		
		connect.get("https://www.campus.rwth-aachen.de/office/default.asp");
		connect.postData("https://www.campus.rwth-aachen.de/office/views/campus/redirect.asp",loginstuff);
    	
		if(connect.getresponseLocation().contains("loginfailed")){ 
			return false;
		}
		return true;
    }
}