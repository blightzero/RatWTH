package edu.rwth.datacenterrats.L2P;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.rwth.datacenterrats.R;



/**
 * Show Sections Activity
 * Just List all the available Sections in L2P. 
 * @author blightzero
 *
 */
public class Sections extends ListActivity {

	//Define our Sections for the L2p Rooms.
	//There are only certain Types of Sections
	//So far only Materials can be parsed.
	int MATERIALS_POS = 2;
    String[] sections = {
            "Description",
            "Information",
            "Materials",
            "Literature",
            "ExerciseCourse",
            "Shared",
    };

   String l2proom_url;
   
    @Override  
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.sections);
        l2proom_url = (String) getIntent().getExtras().getString("url");
        //If we got an url passed we show the List.
        if(l2proom_url != null){
        	setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, sections));
        }
    }
    
	/**
	 * Start Browser
	 * @param Url
	 */
	private void startBrowser(String Url){
		Intent browserIntent = new Intent("android.intent.action.VIEW",Uri.parse(Url));
		startActivity(browserIntent);
	}
	
	/**
	 * Start the Materials Section
	 * @param Url
	 */
	private void startMaterials(String Url){
		Intent materials = new Intent(this, Materials.class);
		
		//Instead of Bundles now lets use putExtra...
		materials.putExtra("url", Url);
        startActivity(materials);
	}
	
	/**
	 * If the user Selected the Materials Section we show the Materials Intent.
	 * Everything else opens a Browser Intent.
	 */
	public void onListItemClick(ListView parent, View v,  int position, long id){
    	if (position == MATERIALS_POS){
			startMaterials(l2proom_url + "/materials/structured/Forms/all.aspx");
		}else{
			startBrowser(l2proom_url + "/" + sections[position]);
		}
	}  
}