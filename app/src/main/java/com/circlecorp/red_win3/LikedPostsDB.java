package com.circlecorp.red_win3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LikedPostsDB extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "likedPostsDB";
    public static final String TABLE_FAVOURITES = "likedPosts";

    public static final String KEY_ID = "_id";
    public static final String KEY_REAL_ID = "real_id";

    public LikedPostsDB(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("create table " + TABLE_FAVOURITES + "(" + KEY_ID +
                " integer primary key," + KEY_REAL_ID + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_FAVOURITES);
        onCreate(sqLiteDatabase);
    }
}
