package com.android.exercise6.datastore;

import com.android.exercise6.model.RedditPost;

import java.util.List;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 10/8/15.
 */
public interface FeedDataStore {
    interface OnRedditPostsRetrievedListener {
        void onRedditPostsRetrieved(List<RedditPost> postList, String after, Exception ex);
    }

    void getPostList(String topic, String before, String after,
            OnRedditPostsRetrievedListener onRedditPostsRetrievedListener);

}