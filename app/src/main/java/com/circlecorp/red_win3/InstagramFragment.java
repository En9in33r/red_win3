package com.circlecorp.red_win3;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class InstagramFragment extends Fragment
{
    public static String body_json_top;

    CircleImageView avatar;
    TextView nickname;
    TextView realname;

    ArrayList<ImageItem> list_of_items;
    GridView gridView;
    GridViewAdapter adapter;

    InstagramJSONCatcher catcher;

    String body;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_instagram, container, false);

        avatar = view.findViewById(R.id.circleImageView);
        nickname = view.findViewById(R.id.nickname);
        realname = view.findViewById(R.id.real_name);

        try
        {
            JSONObject jsonObjectTop = new JSONObject(body_json_top);
            JSONObject data = jsonObjectTop.getJSONObject("data");

            String a = data.getString("profile_picture");

            Glide.with(this).load(Uri.parse(a)).into(avatar);
            nickname.setText(data.getString("username"));
            realname.setText(data.getString("full_name"));

            list_of_items = new ArrayList<>();

            gridView = view.findViewById(R.id.gridView);
            adapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, list_of_items);
            gridView.setAdapter(adapter);

            catcher = new InstagramJSONCatcher();
            catcher.execute();
            body = catcher.get();

            JSONObject jsonObject = new JSONObject(body);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jo = (JSONObject)jsonArray.get(i);
                JSONObject nja = jo.getJSONObject("images");
                JSONObject pur13 = nja.getJSONObject("thumbnail");

                Log.i("parse_json_insta", "" + pur13.getString("url"));

                list_of_items.add(new ImageItem(Uri.parse(pur13.getString("url")), "text"));
            }

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(body);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        JSONObject jo = (JSONObject)jsonArray.get(i);
                        JSONObject nja = jo.getJSONObject("images");
                        JSONObject pur13 = nja.getJSONObject("standard_resolution");

                        ViewPhotoFragment.image_url = pur13.getString("url");

                        /*
                        ViewPhotoFragment fragment = new ViewPhotoFragment();
                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        */

                        DialogFragment fragment = new ViewPhotoFragment();
                        fragment.show(getFragmentManager(), "view_photo");
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (JSONException | InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }

        return view;
    }

    public class InstagramJSONCatcher extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings)
        {
            ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected())
            {
                HttpRequest request = HttpRequest.get
                        ("https://api.instagram.com/v1/users/self/media/recent/?access_token=6728403252.fb5c9d0.fd45fe431e5f41f9b8b314639fb2181e");

                if (request.code() == 200)
                {
                    return request.body();
                }
                else
                {
                    return "[]";
                }
            }
            else
            {
                return "[]";
            }
        }
    }
}
