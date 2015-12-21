package com.android.exercise6.datastore;

import android.util.Log;

import com.android.exercise6.model.RedditPost;
import com.android.exercise6.util.VolleySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 10/22/15.
 */
public class NetworkBasedFeedDatastore implements FeedDataStore {

    @Override
    public void getPostList(String topic, String before, String after,
            final OnRedditPostsRetrievedListener onRedditPostsRetrievedListener) {

        final String url = "https://www.reddit.com/r/" + topic + "/hot.json?raw_json=1&after=" + after;
        Log.e("URL", url );

        final RequestQueue requestQueue = VolleySingleton.getRequestQueue();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            List<RedditPost> list = new ArrayList<>();
                            String af = data.getString("after");
                            JSONArray arrayPost = data.getJSONArray("children");
                            for (int i = 0; i < arrayPost.length(); i++) {
                                JSONObject postData = arrayPost.getJSONObject(i).getJSONObject("data");
                                list.add(new RedditPost(postData));
                            }

                            onRedditPostsRetrievedListener.onRedditPostsRetrieved(list,af,null);
                        } catch (JSONException e) {
                            onRedditPostsRetrievedListener.onRedditPostsRetrieved(null, null, e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onRedditPostsRetrievedListener.onRedditPostsRetrieved(null, null, error);
                    }
                }
        ){
            @Override
            public Priority getPriority() {
                return Priority.NORMAL;
            }
        };

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonRequest);
    }

}