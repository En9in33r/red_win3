package com.circlecorp.red_win3;

public class BlogItem
{
    String content_text;
    String image_uri;
    String real_item_id;
    String likes;

    BlogItem(String content_text, String image_uri, String real_item_id, String likes)
    {
        this.content_text = content_text;
        this.image_uri = image_uri;
        this.real_item_id = real_item_id;
        this.likes = likes;
    }
}
