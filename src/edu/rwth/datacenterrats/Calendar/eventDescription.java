package edu.rwth.datacenterrats.Calendar;

import edu.rwth.datacenterrats.R;
import edu.rwth.datacenterrats.Helper.GAHelper;
import edu.rwth.datacenterrats.LectureRooms.hoersaele;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.text.Html;
import android.view.*;


/**
 * Show the detailed description of a Calendar Event
 * @author Benjamin Grap
 *
 */
public class eventDescription extends Activity 
{
	 private String summary; 
     private String start;
     private String end;
     private String location;
     private String category;
     private String description;
     GAHelper tracker;
	
    /**
     * Creation function called when Activity is opend.
     * 
     */
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        setContentView(R.layout.eventdescription);
        
      	tracker = new GAHelper(this,"eventDescription");
      	tracker.trackPageView("/eventDescription");
        
        summary = new String();
        start = new String();
        end = new String();
        location = new String();
        category = new String();
        description = new String();
        
        //Get the Parameters that were passed to this Intent.
        Intent startingIntent = getIntent();
        
        if (startingIntent != null)
        {
        	Bundle b = startingIntent.getBundleExtra("android.intent.extra.INTENT");
        	if (b == null)
        	{
        		summary = "Could not get Lecture name!";
        	}
        	else
    		{
        		summary = b.getString("Summary");
        		start = b.getString("Start");
        		end = b.getString("End");
        		location = b.getString("Location");
        		category = b.getString("Category");
        		description = b.getString("Description");
        		
             }
        }
        else
        {
        	summary = "Could not get Lecture name! Information not Found!";
        
        }
        
        TextView summaryTV= (TextView) findViewById(R.id.summaryTextView);
        TextView startTV= (TextView) findViewById(R.id.startTextView);
        TextView endTV= (TextView) findViewById(R.id.endTextView);
        TextView locationTV= (TextView) findViewById(R.id.locationTextView);
        TextView categoryTV= (TextView) findViewById(R.id.categoryTextView);
        TextView descriptionTV= (TextView) findViewById(R.id.descriptionTextView);
        
        //Create a simple HTML view
        //uses Html.fromHtml Class function
        summaryTV.setText(Html.fromHtml("<H1>"+ summary +"</H1>"));
        startTV.setText(Html.fromHtml("Starts: " + start));
        endTV.setText(Html.fromHtml("Ends: " + end));
        locationTV.setText(Html.fromHtml("Location: " + location));
        categoryTV.setText(Html.fromHtml("Category: " + category));
        descriptionTV.setText(Html.fromHtml("Description: " + description));
        
        Button backbutton = (Button) findViewById(R.id.fullback);
        Button searchbutton = (Button) findViewById(R.id.searchlocation);
        
        //Create Event Listeners and their respective callback function.
        backbutton.setOnClickListener(new Button.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	finish();
            }
        });
        
        searchbutton.setOnClickListener(new Button.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	tracker.trackEvent("Click", "Click", "searchLocation", 1);
            	Intent itemintent = new Intent(eventDescription.this,hoersaele.class);
                
	           	 Bundle b = new Bundle();
	           	 
	           	 
	           	 b.putString("location", location);
	           	 //get Itemintent.
	           	 itemintent.putExtra("android.intent.extra.INTENT", b);
	                
	           	 startActivityForResult(itemintent,0);
            }
        });
    }
    
    /*
     * onDestroy function
     * We need to take care of the tracker!
     * (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	tracker.destroy();
    }
}
