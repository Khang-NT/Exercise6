package com.android.exercise6.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.exercise6.model.RedditPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khang on 22/12/2015.
 */
public class SqliteHelper extends SQLiteOpenHelper {
    private static final String NAME = "Bookmarks.db";
    private static SqliteHelper instance;


    public static void initialize(Context context) {
        instance = new SqliteHelper(context);
    }

    public static SqliteHelper getInstance() {
        assert instance != null : "Initialize First";
        return instance;
    }

    public SqliteHelper(Context context) {
        super(context, NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bookmarks (\n" +
                "id INTEGER NOT NULL,\n" +
                "json_data TEXT NOT NULL\n" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static boolean isBookmark(String id) {
        boolean result;
        SqliteHelper helper = getInstance();
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cur = db.query("bookmarks", new String[]{"id"},
                " id = ?", new String[]{id}, null, null, null);
        result = cur != null && cur.getCount() > 0;
        if (cur != null)
            cur.close();

        db.close();
        return result;
    }

    public static void insertBookmark(RedditPost post) {
        ContentValues values = new ContentValues();
        values.put("id", post.getId());
        values.put("json_data", post.toString());

        SQLiteDatabase db = getInstance().getWritableDatabase();
        db.insert("bookmarks", null, values);
        db.close();
    }

    public static void removeBookmark(String id) {
        SQLiteDatabase db = getInstance().getWritableDatabase();
        db.delete("bookmarks", "id = ?", new String[]{id});
        db.close();
    }

    public static List<RedditPost> getAllBookmarks() throws JSONException {
        List<RedditPost> result = new ArrayList<>();

        SQLiteDatabase db = getInstance().getWritableDatabase();

        Cursor cur = db.query("bookmarks", new String[]{"json_data"}, null, null, null, null, null);
        if (cur != null && cur.moveToFirst())
            try {
                do {
                    RedditPost post = new RedditPost(new JSONObject(cur.getString(0)), true);
                    result.add(post);
                } while (cur.moveToNext());
            } finally {
                cur.close();
            }
        db.close();

        return result;
    }
}
