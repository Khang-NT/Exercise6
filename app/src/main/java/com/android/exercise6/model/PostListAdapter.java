package com.android.exercise6.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.exercise6.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khang on 07/12/2015.
 */
public class PostListAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener {
    private static final String TAG = "PostListAdapter";
    public static final int DEFAULT_ITEM = 0, LOADING_ITEM = 1;
    private OnItemClick callBack;
    private OnRequestLoadMore requestLoadMore;
    private List<RedditPost> mPostList;
    private boolean isLandscape;

    public PostListAdapter(OnItemClick callBack, OnRequestLoadMore requestLoadMore, boolean isLandscape) {
        this.callBack = callBack;
        this.requestLoadMore = requestLoadMore;
        this.isLandscape = isLandscape;
        this.mPostList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean isDefaultItem = viewType == DEFAULT_ITEM;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                isDefaultItem? R.layout.list_item : R.layout.loading,
                parent, false
        );
        if (isDefaultItem)
            v.findViewById(R.id.view_clickable).setOnClickListener(this);

        return new ViewHolder(v, isDefaultItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RedditPost data = getItem(position);
        if (data != null) {
            holder.bindData(data, isLandscape);
            holder.root.findViewById(R.id.view_clickable).setTag(position);
        } else {
            requestLoadMore.load();
        }
    }

    public RedditPost getItem(int position) {
        return mPostList == null ||
                position == getItemCount() - 1 ?
                null : mPostList.get(position);
    }

    @Override
    public int getItemCount() {
        return mPostList == null || mPostList.size() == 0 ?
                0 : mPostList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount() - 1 ? LOADING_ITEM : DEFAULT_ITEM;
    }

    public void addPost(List<RedditPost> newPost){
        mPostList.addAll(newPost);
        notifyDataSetChanged();
    }

    public void setPost(List<RedditPost> newPost){
        mPostList = newPost;
        notifyDataSetChanged();
    }
    public List<RedditPost> getPostList() {
        return mPostList;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        final RedditPost data = getItem(position);
        final String url = (data == null) ?
                v.getContext().getString(R.string.def_url) : data.getUrl();
        if (data == null)
            callBack.onItemClick(url, "", "", "");
        else
            callBack.onItemClick(url, data.getSelftext_html(), data.getTitle(), data.getSubreddit());
    }


}
