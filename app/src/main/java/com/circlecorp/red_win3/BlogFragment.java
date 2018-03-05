package com.circlecorp.red_win3;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.github.kevinsawicki.http.HttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class BlogFragment extends Fragment
{
    ArrayList<BlogItem> items;
    ListView mListView;
    BlogAdapter adapter;

    public static String header_json;

    PostLoader loader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_blog, container, false);

        mListView = view.findViewById(R.id.listOfBlogItems);
        items = new ArrayList<>();
        adapter = new BlogAdapter(getActivity(), items);
        mListView.setAdapter(adapter);

        loader = new PostLoader();
        loader.execute();
        try
        {
            if (!loader.get().equals("connection_failed"))
            {
                JSONArray array = new JSONArray(loader.get());
                for (int i = array.length() - 1; i >= 0; i--)
                {
                    JSONObject jo = (JSONObject)array.get(i);
                    JSONObject image = jo.getJSONObject("image");

                    items.add(new BlogItem(jo.getString("content_text"), image.getString("url"),
                            jo.getString("id"), jo.getString("likes")));
                }
            }
        }
        catch (ExecutionException | InterruptedException | JSONException e)
        {
            e.printStackTrace();
        }

        return view;
    }

    public class PostLoader extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids)
        {
            ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected())
            {
                HttpRequest request = HttpRequest.get
                        ("https://redwin3.herokuapp.com/posts");
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
