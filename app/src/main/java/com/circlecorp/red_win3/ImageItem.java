package com.circlecorp.red_win3;

import android.net.Uri;

public class ImageItem
{
    private Uri image;

    public ImageItem(Uri image, String title)
    {
        super();
        this.image = image;
    }

    public Uri getImage()
    {
        return image;
    }

    public void setImage(Uri image)
    {
        this.image = image;
    }
}