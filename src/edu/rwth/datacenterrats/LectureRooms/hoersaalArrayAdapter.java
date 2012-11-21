package edu.rwth.datacenterrats.LectureRooms;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

/**
 * 
 * An ArrayAdapter for the List in the hoersaal Activity.
 * Incorporates the Search functionality.
 * @author Benjamin Grap
 *
 */
public class hoersaalArrayAdapter extends ArrayAdapter<hoersaal> {
		private static final String tag = "hoersaalArrayAdapter";
		
		private Context context;

		private List<hoersaal> objects;
		private Filter mFilter;
		private final Object mLock = new Object();
		public ArrayList<hoersaal> mItems;
		
		public hoersaalArrayAdapter(Context context, int textViewResourceId, List<hoersaal> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.mItems = (ArrayList<hoersaal>) objects;
			this.objects = objects;
		}

		@Override
        public int getCount() {
            return mItems.size();
        }
        @Override
        public hoersaal getItem(int position) {
            return mItems.get(position);
        }
        @Override
        public int getPosition(hoersaal item) {
            return mItems.indexOf(item);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			String ItemTitle;
			
			if (row == null) {
				// ROW INFLATION
				//Log.d(tag, "Starting XML Row Inflation ... ");
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
				row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
				//Log.d(tag, "Successfully completed XML Row Inflation!");
			}
			
			// Get item
			hoersaal Item = getItem(position);
			if(Item != null){
				// Get reference to View
				TextView ItemTitleView = (TextView)row.findViewById(android.R.id.text1);
				
				ItemTitle = Item.toString();
				if(ItemTitleView != null){
					ItemTitleView.setText(ItemTitle);
				}
			}
			Integer pos = objects.indexOf(Item);
			row.setTag(pos);
			return row;
		}
		
		
		
	    /**
	     * {@inheritDoc}
	     */
	    public Filter getFilter() {
	        if (mFilter == null) {
	            mFilter = new hoersaalFilter();
	        }
	        return mFilter;
	    }

	    /**
	     * An array filters constrains the content of the array adapter with
	     * a prefix. Each item that does not start with the supplied prefix
	     * is removed from the list.
	     */
	    private class hoersaalFilter extends Filter {
	        @Override
	        protected FilterResults performFiltering(CharSequence prefix) {
                // Initiate our results object
                FilterResults results = new FilterResults();
                // If the adapter array is empty, check the actual items array and use it
                if (mItems == null) {
                    synchronized (mLock) { // Notice the declaration above
                        mItems = new ArrayList<hoersaal>(objects);
                    }
                }
                // No prefix is sent to filter by so we're going to send back the original array
                if (prefix == null || prefix.length() == 0) {
                    synchronized (mLock) {
                        results.values = objects;
                        results.count = objects.size();
                    }
                } else {
                        // Compare lower case strings
                    String prefixString = prefix.toString().toLowerCase();
                    // Local to here so we're not changing actual array
                    final ArrayList<hoersaal> items = mItems;
                    final int count = items.size();
                    final ArrayList<hoersaal> newItems = new ArrayList<hoersaal>(count);
                    for (int i = 0; i < count; i++) {
                        final hoersaal item = items.get(i);
                        final String itemName = item.toString().toLowerCase();
                        // First match against the whole, non-splitted value
                        if (itemName.contains(prefixString)) {
                            newItems.add(item);
                        }
                    }
                    // Set and return
                    results.values = newItems;
                    results.count = newItems.size();
                }
                return results;
            }
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence prefix, FilterResults results) {
                //noinspection unchecked
                mItems = (ArrayList<hoersaal>) results.values;
                // Let the adapter know about the updated list
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
	    }
		
}
