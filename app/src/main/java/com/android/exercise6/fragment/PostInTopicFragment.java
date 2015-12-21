package com.android.exercise6.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.exercise6.R;
import com.android.exercise6.activity.PostDetailActivity;
import com.android.exercise6.datastore.FeedDataStore;
import com.android.exercise6.datastore.NetworkBasedFeedDatastore;
import com.android.exercise6.model.OnItemClick;
import com.android.exercise6.model.OnRequestLoadMore;
import com.android.exercise6.model.PostListAdapter;
import com.android.exercise6.model.RedditPost;
import com.android.exercise6.util.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Khang on 07/12/2015.
 */
public class PostInTopicFragment extends Fragment implements OnItemClick, SwipeRefreshLayout.OnRefreshListener, OnRequestLoadMore {
    private static final String POST_LIST = "POST_LIST";
    private static final String SCROLL_POSITION = "SCROLL_POSITION";
    private static final String TOPIC = "TOPIC", AFTER = "AFTER";

    private String topic, after;
    private PostListAdapter adapter;
    private boolean isLoading;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.no_post_layout)
    LinearLayout no_post_layout;
    @BindInt(R.integer.num_column)
    int num_column;


    public static PostInTopicFragment newInstance(String topic) {
        PostInTopicFragment instance = new PostInTopicFragment();
        instance.topic = topic;
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        adapter = new PostListAdapter(this, this, isLandscape);

        mRecyclerView.setHasFixedSize(true);
        if (isLandscape) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),
                    getResources().getInteger(R.integer.num_column));
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {

                    if (adapter.getItemViewType(position) == PostListAdapter.LOADING_ITEM)
                        return num_column;
                    else
                        return 1;
                }
            });
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            String[] arr = savedInstanceState.getStringArray(POST_LIST);
            List<RedditPost> list = new ArrayList<>();
            try {
                for (String json : arr) {
                    list.add(new RedditPost(new JSONObject(json)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            int scroll_position = savedInstanceState.getInt(SCROLL_POSITION);
            topic = savedInstanceState.getString(TOPIC);
            after = savedInstanceState.getString(AFTER);

            adapter.addPost(list);

            mRecyclerView.scrollTo(0, scroll_position);
        }

        if (adapter.getItemCount() == 0){
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                    loadMorePost(true);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<RedditPost> list = adapter.getPostList();
        String[] arrayPost = new String[list.size()];
        int i = 0;
        for (RedditPost post : list) {
            arrayPost[i] = post.toString();
            i++;
        }
        outState.putStringArray(POST_LIST, arrayPost);
        outState.putInt(SCROLL_POSITION, mRecyclerView.getScrollY());
        outState.putString(AFTER, after);
        outState.putString(TOPIC, topic);
    }

    @OnClick (R.id.b_retry)
    public void onRetry() {
        refreshLayout.setVisibility(View.VISIBLE);
        no_post_layout.setVisibility(View.GONE);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                loadMorePost(true);
            }
        });
    }

    private void handleError(Exception e) {
        if (adapter.getItemCount() == 0) {
            refreshLayout.setVisibility(View.GONE);
            no_post_layout.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }
        e.printStackTrace();
    }

    private void loadMorePost(final boolean clearOld){
        isLoading = true;
        NetworkBasedFeedDatastore networkBasedFeedDatastore = new NetworkBasedFeedDatastore();
        networkBasedFeedDatastore.getPostList(topic, null, after,
                new FeedDataStore.OnRedditPostsRetrievedListener() {
                    @Override
                    public void onRedditPostsRetrieved(List<RedditPost> postList, String after, Exception ex) {
                        try {
                            if (postList == null)
                                handleError(ex);
                            else {
                                if (clearOld)
                                    adapter.setPost(postList);
                                else
                                    adapter.addPost(postList);
                                PostInTopicFragment.this.after = after;
                            }


                            if (refreshLayout.isRefreshing())
                                refreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            refreshLayout.setRefreshing(false);
                                        } catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        } catch (Exception e) {
                            //Catch null pointer exception if call back when fragment go to destroy
                            e.printStackTrace();
                        } finally {
                            isLoading = false;
                        }
                    }
                });
    }

    // Cancel all background network thread
    private void cancelAll(){
        VolleySingleton.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        isLoading = false;
    }

    @Override
    public void onItemClick(String url, String selfText_html, String title, String subReddit) {
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra("URL", url == null ? "" : url);
        intent.putExtra("selftext_html", selfText_html == null ? "" : selfText_html);
        intent.putExtra("subreddit", subReddit);
        intent.putExtra("title", title);
        getActivity().startActivity(intent);
    }

    @Override
    public void load() {
        new Handler().postDelayed(new Runnable() {
            @Override
            //delay a bit to show wheel effect
            public void run() {
                if (!isLoading)
                    loadMorePost(false);
            }
        },1000);

    }

    @Override
    public void onDestroyView() {
        cancelAll();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onRefresh() {
        cancelAll();
        after = null; // Clear all, load 1st page
        loadMorePost(true);
    }


}
