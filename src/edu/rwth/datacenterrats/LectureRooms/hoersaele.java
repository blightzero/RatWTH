package edu.rwth.datacenterrats.LectureRooms;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener; 
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import edu.rwth.datacenterrats.R;
import edu.rwth.datacenterrats.Helper.GAHelper;
import android.content.Intent;

/**
 * An Activity that Displays all the hoersaele.
 * Uses a TextInput to Filter the hoersaele in the ArrayAdapter.
 * @author Benjamin Grap
 *
 */
public class hoersaele extends Activity implements OnItemClickListener{
	public String RSSFEEDOFCHOICE;
	public String loginCampus;
	public final String tag = "RSSReader";
	public List<hoersaal> hoersaallist;
	public static String username;
	public static String password;
	private hoersaalArrayAdapter adapter;
	private String filter;
	private GAHelper tracker;
	
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.hoersaallist);
        Intent startingIntent = getIntent();
        tracker = new GAHelper(this,"Hoersaele");
        tracker.trackPageView("/Hoersaele");
        SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(this);
        username = app_preferences.getString("LoginL2P", "ab123456");
        password = app_preferences.getString("PassL2P", "ab123456");
        
        if (startingIntent != null)
        {
        	Bundle b = startingIntent.getBundleExtra("android.intent.extra.INTENT");
        	if (b == null)
        	{
        		filter = "";
        	}
        	else
    		{
        		filter = b.getString("location");
    		}
        }
        else
        {
        	filter = "";
        
        }
        
        UpdateDisplay();      
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	tracker.destroy();
    }
    
    /**
     * Update the View.
     */
    private void UpdateDisplay()
    {
        TextView toptitle = (TextView) findViewById(R.id.toptitle);
        TextView bottomtitle = (TextView) findViewById(R.id.bottomtitle);
        ListView itemlist = (ListView) findViewById(R.id.hoersaallist);
        hoersaalParser hoersaalParser = new hoersaalParser();
        EditText edittext = (EditText) findViewById(R.id.editTextfilter1);
        edittext.setText(filter);
        
        edittext.addTextChangedListener(filterTextWatchter);
        hoersaalParser.parse(getResources().getXml(R.xml.hoersaele));
        hoersaallist = hoersaalParser.getList();
        sortList(hoersaallist);
        
        toptitle.setText("");
        bottomtitle.setText("");    
       
        adapter = new hoersaalArrayAdapter(this,android.R.layout.simple_list_item_1,hoersaallist);
        itemlist.setAdapter(adapter);      
        itemlist.setOnItemClickListener(this);
        itemlist.setSelection(0);
        adapter.getFilter().filter(filter);
    }
    /**
     * Private Class TextWatcher to watch for new input in the Textinputfield.
     */
    private TextWatcher filterTextWatchter = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			adapter.getFilter().filter(s);
		}
    	
    };
    
    /**
     * Sort the List of hoersaal items alphabeticaly.
     * @param _itemlist
     */
    public void sortList(List<hoersaal> _itemlist){
    	Comparator<hoersaal> name_compare = new Comparator<hoersaal>(){
			@Override
			public int compare(hoersaal obj1, hoersaal obj2){
				String string_obj1;
				String string_obj2;
				string_obj1 = obj1.toString();
				string_obj2 = obj2.toString();
				return string_obj1.compareTo(string_obj2);	
			}
		};
		
		Collections.sort(_itemlist,name_compare);
    }
    
    /*
     * 
     * (non-Javadoc)
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
     public void onItemClick(AdapterView parent, View v, int position, long id)
     {
    	 Integer pos = 1;
    	 //Log.i(tag,"item clicked! [" + hoersaallist.get(position).toString() + "]");
    	 tracker.trackEvent("Click", "Click", "SelectedHoersaal", 1);
    	 Intent itemintent = new Intent(this,hoersaalMapView.class);
         if(v.getTag() != null){
        	 pos = (Integer) v.getTag();
         }
         
    	 Bundle b = new Bundle();
    	 b.putString("RaumID", hoersaallist.get(pos).getRaumID());
    	 b.putString("Raumname", hoersaallist.get(pos).getRaumname());
    	 b.putString("Adresse", hoersaallist.get(pos).getAdresse());
    	 b.putString("Sitzplaetze", hoersaallist.get(pos).getSitzplaetze());
    	 b.putString("Raumart", hoersaallist.get(pos).getRaumart());
    	 
    	 itemintent.putExtra("android.intent.extra.INTENT", b);
         
    	 startActivityForResult(itemintent,0);
     }
    
}
