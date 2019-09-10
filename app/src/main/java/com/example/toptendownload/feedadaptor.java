package com.example.toptendownload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class feedadaptor extends ArrayAdapter {
    private static final String TAG = "feedadaptor";
    private final int layoutresource;
    private final LayoutInflater layoutInflater;
    private List<feedentry> applicaitons;

    public feedadaptor(Context context, int resource, List<feedentry> applicaitons) {
        super(context, resource);
        this.layoutresource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applicaitons = applicaitons;
    }

    @Override
    public int getCount() {
        return applicaitons.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutresource, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvArtist = (TextView) convertView.findViewById(R.id.tvArtist);
        TextView tvSummary = (TextView) convertView.findViewById(R.id.tvSummary);
        feedentry currentapp = applicaitons.get(position);
        tvName.setText(currentapp.getName());
        tvArtist.setText(currentapp.getArtist());
        tvSummary.setText(currentapp.getSummary());
        return convertView;
    }
}
