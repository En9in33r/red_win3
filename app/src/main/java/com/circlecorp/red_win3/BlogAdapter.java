package com.circlecorp.red_win3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    ImageView mLikeButton;

    LikedPostsDB helper;

    Upvote upvote;
    Downwote downwote;

    View view;

    ConstraintLayout wholeLikeButton;

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
    public View getView(final int position, final View convertView, ViewGroup parent)
    {
        view = convertView;
        if (view == null)
        {
            view = inflater.inflate(R.layout.blog_item, parent, false);
        }

        mAvatar = view.findViewById(R.id.avatar);
        mImage = view.findViewById(R.id.blogItemImage);

        mLikeButton = view.findViewById(R.id.imageView);

        final BlogItem b = getBlogItem(position);

        ((TextView)view.findViewById(R.id.blogItemText)).setText(b.content_text);
        Glide.with(context).load(b.image_uri).into(mImage);
        ((TextView)view.findViewById(R.id.numberOfLikes)).setText(b.likes);

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

        helper = new LikedPostsDB(context);
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.query(LikedPostsDB.TABLE_FAVOURITES,
                null,
                "real_id = ?",
                new String[] { b.real_item_id },
                null, null, null);

        if (cursor.moveToFirst()) // если таковой имеется
        {
            mLikeButton.setImageResource(R.drawable.like);
        }
        else  // если в локальной бд нет элемента с таким real_id
        {
            mLikeButton.setImageResource(R.drawable.unlike);
        }

        wholeLikeButton = view.findViewById(R.id.wholeLikeButton);
        wholeLikeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View viewe)
            {
                LikedPostsDB helper_inside = new LikedPostsDB(context);
                SQLiteDatabase database_inside = helper_inside.getWritableDatabase();
                Cursor cursor_inside = database_inside.query(LikedPostsDB.TABLE_FAVOURITES,
                        null,
                        "real_id = ?",
                        new String[] { b.real_item_id },
                        null, null, null);

                if (cursor_inside.moveToFirst()) // если такой элемент существует, то мы его удаляем
                {
                    database_inside.delete(LikedPostsDB.TABLE_FAVOURITES, "real_id = ?", new String[] { b.real_item_id });
                    // downvote
                    downwote = new Downwote(context);
                    downwote.execute(b.real_item_id);
                    try
                    {
                        if (downwote.get().equals("connection_failed"))
                        {
                            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            ((ImageView)viewe.findViewById(R.id.imageView)).setImageResource(R.drawable.unlike);
                            String jsonina = downwote.get();
                            JSONObject element = new JSONObject(jsonina);

                            ((TextView)viewe.findViewById(R.id.numberOfLikes)).setText(element.getString("likes"));
                            // Toast.makeText(context, element.getString("likes"), Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (InterruptedException | ExecutionException | JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else // если его не существует, то добавляем!
                {
                    ContentValues values = new ContentValues();
                    values.put("real_id", b.real_item_id);
                    database_inside.insert(LikedPostsDB.TABLE_FAVOURITES, null, values);
                    // upvote
                    upvote = new Upvote(context);
                    upvote.execute(b.real_item_id);

                    try
                    {
                        if (upvote.get().equals("connection_failed"))
                        {
                            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            ((ImageView)viewe.findViewById(R.id.imageView)).setImageResource(R.drawable.like);
                            String jsonina = upvote.get();
                            JSONObject element = new JSONObject(jsonina);

                            ((TextView)viewe.findViewById(R.id.numberOfLikes)).setText(element.getString("likes"));
                            //Toast.makeText(context, element.getString("likes"), Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (InterruptedException | ExecutionException | JSONException e)
                    {
                        e.printStackTrace();
                    }


                }

                cursor_inside.close();
            }
        });

        return view;
    }

    BlogItem getBlogItem(int position)
    {
        return ((BlogItem)getItem(position));
    }

    public class Downwote extends AsyncTask<String, Void, String>
    {
        Context context1;

        public Downwote(Context context)
        {
            context1 = context;
        }

        @Override
        protected String doInBackground(String... strings)
        {
            ConnectivityManager cm = (ConnectivityManager)context1.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected())
            {
                HttpRequest request = HttpRequest.get
                        ("https://redwin3.herokuapp.com/posts/downvote/" + strings[0]);
                request.connectTimeout(1000);
                if (request.code() == 200)
                {
                    return request.body();
                }
                else
                    return "connection_failed";
            }
            else
            {
                return "connection_failed";
            }
        }
    }

    public class Upvote extends AsyncTask<String, Void, String>
    {
        Context context1;

        public Upvote(Context context)
        {
            context1 = context;
        }

        @Override
        protected String doInBackground(String... strings)
        {
            ConnectivityManager cm = (ConnectivityManager)context1.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected())
            {
                HttpRequest request = HttpRequest.get
                        ("https://redwin3.herokuapp.com/posts/upvote/" + strings[0]);
                request.connectTimeout(1000);
                if (request.code() == 200)
                {
                    return request.body();
                }
                else
                    return "connection_failed";
            }
            else
            {
                return "connection_failed";
            }
        }
    }
}
