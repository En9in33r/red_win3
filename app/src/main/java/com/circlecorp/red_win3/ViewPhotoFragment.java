package com.circlecorp.red_win3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ViewPhotoFragment extends Fragment
{
    public static String image_url;
    public static String description_text = "";

    ImageView image_view;
    TextView text_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_view_photo, container, false);

        image_view = view.findViewById(R.id.imageView3);

        Glide.with(getActivity()).load(image_url).into(image_view);

        return view;
    }
}
