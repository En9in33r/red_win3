package com.circlecorp.red_win3;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter
{
    private Context context;
    private int layoutResourceId;
    private ArrayList data = new ArrayList();

    public GridViewAdapter(@NonNull Context context, int layoutResourceId, ArrayList data)
    {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View row = convertView;
        ViewHolder holder;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            // holder.imageTitle = row.findViewById(R.id.text);
            holder.image = row.findViewById(R.id.image);
            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }

        ImageItem item = (ImageItem) data.get(position);
        // holder.imageTitle.setText(item.getTitle()); (т.к. текст нужно пока убрать, он будет портить видок)
        // holder.image.setImageURI(item.getImage());
        Glide.with(context).load(item.getImage()).into(holder.image);
        return row;
    }

    public void refresh(ArrayList data)
    {
        this.data = data;
        this.notifyDataSetChanged();
    }

    static class ViewHolder
    {
        // TextView imageTitle;
        ImageView image;
    }
}
