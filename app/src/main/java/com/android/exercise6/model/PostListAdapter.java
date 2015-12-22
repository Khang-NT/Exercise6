package com.android.exercise6.model;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.exercise6.R;
import com.android.exercise6.util.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khang on 07/12/2015.
 */
public class PostListAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "PostListAdapter";
    public static final int DEFAULT_ITEM = 0, LOADING_ITEM = 1;
    private OnItemClick callBack;
    private OnRequestLoadMore requestLoadMore;
    private List<RedditPost> mPostList;
    private OnSelectStateChange selectStateCallback;
    private boolean isLandscape;
    private String topic;

    public boolean[] selecting;
    public int selectedCount = 0;

    public PostListAdapter(OnItemClick callBack, OnRequestLoadMore requestLoadMore, OnSelectStateChange selectStateCallback, boolean isLandscape, String topic) {
        this.callBack = callBack;
        this.requestLoadMore = requestLoadMore;
        this.selectStateCallback = selectStateCallback;
        this.isLandscape = isLandscape;
        this.topic = topic;
        this.mPostList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean isDefaultItem = viewType == DEFAULT_ITEM;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                isDefaultItem? R.layout.list_item : R.layout.loading,
                parent, false
        );
        if (isDefaultItem) {
            v.findViewById(R.id.view_clickable).setOnClickListener(this);
            if (topic.equalsIgnoreCase("bookmarks"))
                v.findViewById(R.id.view_clickable).setOnLongClickListener(this);
            v.findViewById(R.id.ib_star).setOnClickListener(this);
        }

        return new ViewHolder(v, isDefaultItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RedditPost data = getItem(position);
        if (data != null) {
            holder.bindData(data, isLandscape);
            holder.root.findViewById(R.id.view_clickable).setTag(position);
            if (selecting != null)
                setSelectState(holder.root.findViewById(R.id.view_clickable), selecting[position]);
            else setSelectState(holder.root.findViewById(R.id.view_clickable), false);
            holder.root.findViewById(R.id.ib_star).setTag(position);
        } else {
            requestLoadMore.load();
        }

    }

    public RedditPost getItem(int position) {
        return mPostList == null ||
                position > mPostList.size() - 1 ?
                null : mPostList.get(position);
    }

    @Override
    public int getItemCount() {
        if (topic.equalsIgnoreCase("bookmarks"))
            return mPostList == null ? 0 : mPostList.size();
        return mPostList == null || mPostList.size() == 0 ?
                0 : mPostList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (topic.equalsIgnoreCase("bookmarks"))
            return DEFAULT_ITEM;
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

    private void setSelectState(View v, boolean isSelected) {
        CardView card = (CardView) v;
        if (isSelected) {
            card.setCardBackgroundColor(v.getResources().getColor(R.color.card_color_checked));
        } else {
            card.setCardBackgroundColor(v.getResources().getColor(R.color.card_color));
        }
    }

    @Override
    public void onClick(final View v) {
        final int position = (int) v.getTag();
        final RedditPost data = getItem(position);
        if (v.getId() == R.id.view_clickable) {
            if (selecting != null) {
                selecting[position] = !selecting[position];

                setSelectState(v, selecting[position]);

                if (selecting[position])
                    selectedCount++;
                else if ((selectedCount = selectedCount - 1) == 0)
                    selecting = null;

                selectStateCallback.onChanged(selectedCount);
                return;
            }

            final String url = (data == null) ?
                    v.getContext().getString(R.string.def_url) : data.getUrl();
            if (data == null)
                callBack.onItemClick(url, "", "", "");
            else
                callBack.onItemClick(url, data.getSelftext_html(), data.getTitle(), data.getSubreddit());
        } else if (selecting == null) {
            v.setClickable(false);
            final boolean isBookmark = !data.isBookmark();
            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.fade_in_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setClickable(true);
                    Log.e(TAG, "onAnimationEnd: ");
                    if (topic.equalsIgnoreCase("bookmarks")) {
                        mPostList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mPostList.size());
                    }


                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Log.e(TAG, "onAnimationRepeat: ");
                    AppCompatImageButton ibStar = (AppCompatImageButton) v;
                    if (isLandscape) {
                        ibStar.setImageResource(isBookmark ?
                                R.drawable.ic_bookmark_star_selected_y : R.drawable.ic_bookmark_star_unselected_white);
                    } else {
                        ibStar.setImageResource(isBookmark ?
                                R.drawable.ic_bookmark_star_selected_r : R.drawable.ic_bookmark_star_unselected_black);
                    }
                }
            });
            v.startAnimation(animation);
            data.setBookmark(isBookmark);
            if (isBookmark)
                SqliteHelper.insertBookmark(data);
            else {
                SqliteHelper.removeBookmark(data.getId());
            }
        }
    }


    @Override
    public boolean onLongClick(View v) {
        if (selecting == null)
            selecting = new boolean[getItemCount()];
        int position = (int) v.getTag();
        selecting[position] = !selecting[position];

        setSelectState(v, selecting[position]);

        if (selecting[position])
            selectedCount++;
        else if ((selectedCount = selectedCount - 1) == 0)
            selecting = null;

        selectStateCallback.onChanged(selectedCount);
        return true;
    }

    public void removeAllSelecting() {
        if (selecting != null) {
            List<RedditPost> postToRemove = new ArrayList<>();
            for (int i = 0; i < selecting.length; i++)
                if (selecting[i]) {
                    SqliteHelper.removeBookmark(getItem(i).getId());
                    postToRemove.add(getItem(i));
                }
            mPostList.removeAll(postToRemove);
            selecting = null;
            selectedCount = 0;
            notifyDataSetChanged();
        }
    }

    public void selectAll() {
        if (selecting != null) {
            for (int i = 0; i < selecting.length; i++)
                selecting[i] = true;
            selectedCount = selecting.length;
            notifyDataSetChanged();
        }
    }

    public void cancelSelect() {
        selecting = null;
        selectedCount = 0;
        notifyDataSetChanged();
    }
}
