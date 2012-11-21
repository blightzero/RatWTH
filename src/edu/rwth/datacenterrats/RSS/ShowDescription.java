package edu.rwth.datacenterrats.RSS;

import edu.rwth.datacenterrats.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.*;

/**
 * Show the Detailed Description of an RSS Element.
 * 
 * @author Benjamin Grap
 *
 */
public class ShowDescription extends Activity 
{
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        setContentView(R.layout.showdescription);
        
        String theStory = null;
        
        
        Intent startingIntent = getIntent();
        
        if (startingIntent != null)
        {
        	Bundle b = startingIntent.getBundleExtra("android.intent.extra.INTENT");
        	if (b == null)
        	{
        		theStory = "Could not get Data from Intent.";
        	}
        	else
    		{
        		theStory = b.getString("title") + "<br><br>" + b.getString("pubdate") + "<br><br>" + b.getString("description").replace('\n',' ') + "<br><br>More information:<br><a href='" + b.getString("link")+"'>"+b.getString("link")+"</a>";
    		}
        }
        else
        {
        	theStory = "Information Not Found.";
        
        }
        
        TextView db= (TextView) findViewById(R.id.storybox);
        db.setMovementMethod(LinkMovementMethod.getInstance());
        db.setText(Html.fromHtml(theStory));
        
        Button backbutton = (Button) findViewById(R.id.back);
        
        backbutton.setOnClickListener(new Button.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	finish();
            }
        });        
    }
}
