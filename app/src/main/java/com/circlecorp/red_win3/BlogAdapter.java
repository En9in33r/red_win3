package com.circlecorp.red_win3;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogAdapter extends BaseAdapter
{
    Context context;
    LayoutInflater inflater;
    ArrayList<BlogItem> blogItems;

    CircleImageView mAvatar;
    ImageView mImage;

    BlogAdapter(Context context, ArrayList<BlogItem> blogItems)
    {
        this.context = context;
        this.blogItems = blogItems;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return blogItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return blogItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null)
        {
            view = inflater.inflate(R.layout.blog_item, parent, false);
        }

        mAvatar = view.findViewById(R.id.avatar);
        mImage = view.findViewById(R.id.blogItemImage);

        BlogItem b = getBlogItem(position);

        ((TextView)view.findViewById(R.id.blogItemText)).setText(b.content_text);
        Glide.with(context).load(b.image_uri).into(mImage);

        try
        {
            JSONObject jsonObjectTop = new JSONObject(BlogFragment.header_json);
            JSONObject data = jsonObjectTop.getJSONObject("data");

            String a = data.getString("profile_picture");

            ((TextView)view.findViewById(R.id.posterName)).setText(data.getString("full_name"));
            Glide.with(context).load(Uri.parse(a)).into(mAvatar);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return view;
    }

    BlogItem getBlogItem(int position)
    {
        return ((BlogItem)getItem(position));
    }
}
