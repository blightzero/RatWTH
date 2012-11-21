package edu.rwth.datacenterrats.RSS;
import java.io.IOException;
import java.util.List;

import edu.rwth.datacenterrats.R;
import edu.rwth.datacenterrats.R.id;
import edu.rwth.datacenterrats.R.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * RSS Array Adapter
 * Uses a nice Elements View with pictures.
 * @author 
 *
 */
public class RssArrayAdapter extends ArrayAdapter<RSSItem> {
	private static final String tag = "RssArrayAdapter";
	private static final String ASSETS_DIR = "images/";
	private Context context;
	private List<RSSItem> objects;

	public RssArrayAdapter(Context context, int textViewResourceId,	List<RSSItem> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;

		this.objects = objects;
	}

	public int getCount() {
		return this.objects.size();
	}

	public RSSItem getItem(int index) {
		return this.objects.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		String ItemTitle;
		String ItemPubDate;
		String imgFilePath;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
			row = inflater.inflate(R.layout.rsslistitem, parent, false);
		}
		
		RSSItem rssItem = getItem(position);
		if(rssItem != null){
			// Get reference to ImageView
			ImageView categoryIcon = (ImageView)row.findViewById(R.id.categoryImage);
	
			// Get reference to TextView
			TextView RSSItemTitle = (TextView)row.findViewById(R.id.feedtitle);
	
			// Get reference to TextView
			TextView RSSItemPubDate = (TextView)row.findViewById(R.id.feedpubdate);

			ItemTitle = rssItem.getTitle();
			if(RSSItemTitle != null){
				RSSItemTitle.setText(ItemTitle);
			}
			if(categoryIcon != null){
				//Log.d(tag, rssItem.getCategory());
				if(rssItem.getCategory().contains("Dokument") || rssItem.getCategory().contains("Document")){
					imgFilePath = ASSETS_DIR + "Dokument.png";
				}else if(rssItem.getCategory().contains("Wiki-Seite") || rssItem.getCategory().equals("WikiPage")){
					imgFilePath = ASSETS_DIR + "Wiki.png";
				}else if(rssItem.getCategory().contains("Link") || rssItem.getCategory().equals("Hyperlink")){
					imgFilePath = ASSETS_DIR + "Link.png";
				}else if(rssItem.getCategory().contains("Umfrage")|| rssItem.getCategory().contains("Survey")){
					imgFilePath = ASSETS_DIR + "Poll.png";
				}else if(rssItem.getCategory().contains("Ankündigung")|| rssItem.getCategory().equals("Announcement")){
					imgFilePath = ASSETS_DIR + "Announcement.png";
				}else if(rssItem.getCategory().contains("Literatur")|| rssItem.getCategory().equals("Literature")){
					imgFilePath = ASSETS_DIR + "Literatur.png";
				}else{
					imgFilePath = ASSETS_DIR + "l2p.png";
				}
				try {
					Bitmap bitmap = BitmapFactory.decodeStream(this.context.getResources().getAssets().open(imgFilePath));
					categoryIcon.setImageBitmap(bitmap);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(RSSItemPubDate != null){
				ItemPubDate = rssItem.getPubDate();
				RSSItemPubDate.setText(ItemPubDate);
			}
		}
		return row;
	}
}
