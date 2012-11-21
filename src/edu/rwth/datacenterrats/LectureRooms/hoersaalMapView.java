package edu.rwth.datacenterrats.LectureRooms;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.rwth.datacenterrats.R;
import edu.rwth.datacenterrats.R.drawable;
import edu.rwth.datacenterrats.R.id;
import edu.rwth.datacenterrats.R.layout;



/**
 * Activity showing the GoogleMaps View.
 * @author Benjamin Grap
 *
 */
public class hoersaalMapView extends MapActivity {
	MapController mc;
	GeoPoint p;
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    String RaumID = "";
	    String Raumname = "";
	    String Adresse = "";
	    String Sitzplaetze = "";
	    String Raumart = "";
		
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.mapview);
	    
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    
	    Intent startingIntent = getIntent();
        
        if (startingIntent != null)
        {
        	Bundle b = startingIntent.getBundleExtra("android.intent.extra.INTENT");
        	if (b == null)
        	{   //Set an default Address
        		Adresse = "Templergraben 55";
        	}
        	else
    		{
        		 RaumID = b.getString("RaumID");
        		 Raumname = b.getString("Raumname"); 
        		 Adresse = b.getString("Adresse");
        		 Sitzplaetze = b.getString("Sitzplaetze");
        		 Raumart = b.getString("Raumart");
    		}
        }
	    
	    
	    mc = mapView.getController();
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.l2p_point);
	    hoersaalItemizedOverlay itemizedoverlay = new hoersaalItemizedOverlay(drawable,mapView.getContext());
	    
	    Geocoder geoCoder = new Geocoder(this, Locale.getDefault());    
        try {
        	//Make a call to the geoCoder API from Google and get the Location of the Address.
            List<Address> addresses = geoCoder.getFromLocationName(Adresse + ", Aachen", 5);
            if (addresses.size() > 0) {
                p = new GeoPoint((int) (addresses.get(0).getLatitude() * 1E6),(int) (addresses.get(0).getLongitude() * 1E6));
                OverlayItem overlayitem = new OverlayItem(p,Raumname,Adresse);
                mc.animateTo(p);
                mc.setZoom(19);
                mapView.invalidate();
                itemizedoverlay.addOverlay(overlayitem);
                mapOverlays.add(itemizedoverlay);
            }    
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}
}
