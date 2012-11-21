package edu.rwth.datacenterrats;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import edu.rwth.datacenterrats.Calendar.eventView;
import edu.rwth.datacenterrats.Helper.GAHelper;
import edu.rwth.datacenterrats.L2P.L2PRooms;
import edu.rwth.datacenterrats.LectureRooms.hoersaele;
import edu.rwth.datacenterrats.RSS.RssReader;

/**
 * StartPage Activity.
 * This Activity Displays the Main-Menu.
 * @author Benjamin Grap
 *
 */
public class StartPage extends Activity {
    /** Called when the activity is first created. */
	
	private ImageButton iButton1;
	private ImageButton iButton2;
	private ImageButton iButton3;
	private ImageButton iButton4;
	private ImageButton iButton5;
	private ImageButton iButton6;
	private TextView textView;
	GAHelper tracker;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        tracker = new GAHelper(this,"StartPage");
        
        //Get Objects from the Resources Field.
        this.iButton1 = (ImageButton) findViewById(R.id.imageButton3);
    	this.iButton2 = (ImageButton) findViewById(R.id.imageButton4);
    	this.iButton3 = (ImageButton) findViewById(R.id.imageButton1);
    	this.iButton4 = (ImageButton) findViewById(R.id.imageButton2);
    	this.iButton5 = (ImageButton) findViewById(R.id.imageButton5);
    	this.iButton6 = (ImageButton) findViewById(R.id.imageButton6);
    	this.textView = (TextView) (TextView)findViewById(R.id.textView1);
    	this.textView.setText(Html.fromHtml(getString(R.string.github)));
    	this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        //Setup Callback functionalities.
        this.iButton1.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
        	 
        	  startL2PRooms();
          }
        });   
        this.iButton2.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {

        	  startRssReader();
          }
        });
        this.iButton3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	startConfig();

            }
          });
        
        this.iButton4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	loadCalendar();

            }
          });
        
        this.iButton5.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		startToDo();

        	}
        });
        
        this.iButton6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	startHoersaele();

            }
          });
        //Check whether we have a working config already or start the Confing Intent
        if (Load_Preferences()){
        	tracker.trackPageView("/StartPage");
        }else{
        	startConfig();
        }
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	tracker.destroy();
    }
    /**
     * Start the RssReader Intent with the global L2P RSS Feed.
     */
    public void startRssReader(){
    	if(isOnline()){
    		tracker.trackEvent("Click","RSS","clicked",1);
    		startActivity(new Intent(this, RssReader.class));
    	}else{
    		showDialog(DIALOG_NO_NETWORK);
    	}
    }
    /**
     * Start the Config Intent.
     */
    public void startConfig(){
    	tracker.trackEvent("Click","Config","clicked",1);
    	startActivity(new Intent(this, Config.class));
    }
    
    /**
     * Start the ToDo Intent.
     */
    public void startToDo(){
    	tracker.trackEvent("Click","ToDo","clicked",1);
    	showDialog(DIALOG_NOT_IMPLEMENTED);
    }
    /**
     * Start the L2P Intent.
     */
    public void startL2PRooms(){
    	if(isOnline()){
    		tracker.trackEvent("Click","L2P","clicked",1);
    		startActivity(new Intent(this, L2PRooms.class));
    	}else{
    		showDialog(DIALOG_NO_NETWORK);
    	}
    }
    /**
     * Start the Hoersaele Intent.
     */
    public void startHoersaele(){
    	tracker.trackEvent("Click","Hoersaele","clicked",1);
    	startActivity(new Intent(this, hoersaele.class));
    }
    
    /**
     * Start the Calendar EventView Intent.
     */
    public void loadCalendar(){
    	tracker.trackEvent("Click","Calendar","clicked",1);
    	startActivity(new Intent(this, eventView.class));
    }
    /**
     * Load the Preferences from the Preferencemanager of the Androidsystem.
     */
    public boolean Load_Preferences(){
    	SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(this);
    	if(app_preferences.getString("PassL2P", "") == ""){
    		return false;
    	}else{
    		return true;
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