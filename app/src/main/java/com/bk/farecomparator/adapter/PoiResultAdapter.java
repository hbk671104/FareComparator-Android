package com.bk.farecomparator.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.bk.farecomparator.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by BK on 11/3/16.
 */

public class PoiResultAdapter extends ArrayAdapter<PoiItem> {

    public PoiResultAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get poi item
        PoiItem item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poi_item, parent, false);
        }
        TextView poiTitleTextView = (TextView)convertView.findViewById(R.id.poi_title_text_view);
        TextView poiTypeTextView = (TextView)convertView.findViewById(R.id.poi_type_text_view);
        TextView poiAddressTextView = (TextView)convertView.findViewById(R.id.poi_address_text_view);
        poiTitleTextView.setText(item.getTitle());
        // Little work on types
        if (!item.getTypeDes().isEmpty()) {
            if (item.getTypeDes().contains(";")) {
                String firstItem = Arrays.asList(item.getTypeDes().split(";")).get(0);
                poiTypeTextView.setText(firstItem);
            } else {
                poiTypeTextView.setText(item.getTypeDes());
            }
        }
        poiAddressTextView.setText(item.getSnippet());
        return convertView;
    }

}
