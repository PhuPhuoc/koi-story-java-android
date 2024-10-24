package com.koistorynew.ui.fish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koistorynew.R;
import com.koistorynew.ui.fish.model.KoiFish;

import java.util.List;

public class KoiFishAdapter extends BaseAdapter {
    private Context context;
    private List<KoiFish> koiFishList;

    public KoiFishAdapter(Context context, List<KoiFish> koiFishList) {
        this.context = context;
        this.koiFishList = koiFishList;
    }

    @Override
    public int getCount() {
        return koiFishList.size();
    }

    @Override
    public Object getItem(int position) {
        return koiFishList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        ImageView koiImage = convertView.findViewById(R.id.koi_image);
        TextView koiName = convertView.findViewById(R.id.koi_name);

        KoiFish koiFish = koiFishList.get(position);
        koiImage.setImageResource(koiFish.getImageResource());
        koiName.setText(koiFish.getName());

        return convertView;
    }
}
