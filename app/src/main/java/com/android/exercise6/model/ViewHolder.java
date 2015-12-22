package com.android.exercise6.model;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.exercise6.R;
import com.android.exercise6.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Khang on 07/12/2015.
 */
/**
 * This class contains all butterknife-injected Views & Layouts from layout file 'list_item.xml'
 * for easy to all layout elements.
 *
 * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    View root;
    @Bind(R.id.tv_score)
    TextView tvScore;
    @Bind(R.id.tv_author_subreddit)
    TextView tvAuthorSubreddit;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_comment_domain_createdutc)
    TextView tvCommentDomainCreatedutc;
    @Bind(R.id.ib_star)
    AppCompatImageButton ibStar;

    ViewHolder(View view, boolean bindView) {
        super(view);
        this.root = view;
        if (bindView)
            ButterKnife.bind(this, view);
    }

    public void bindData(RedditPost data, boolean isLandscape){
        tvScore.setText(data.getScore() + "");
        tvAuthorSubreddit.setText(Utils.buildAuthorAndSubredditText(
                data.getAuthor(),
                data.getSubreddit(),
                isLandscape
        ));
        tvTitle.setText(Utils.buildTitleText(
                data.getTitle(),
                data.isStickyPost(),
                isLandscape
        ));
        tvCommentDomainCreatedutc.setText(Utils.buildCommentDomainCreatedtimeText(
                data.getCommentCount(),
                data.getDomain(),
                data.getCreatedUTC()
        ));
        if (isLandscape) {
            ibStar.setImageResource(data.isBookmark() ?
                    R.drawable.ic_bookmark_star_selected_y : R.drawable.ic_bookmark_star_unselected_white);
        } else {
            ibStar.setImageResource(data.isBookmark() ?
                    R.drawable.ic_bookmark_star_selected_r : R.drawable.ic_bookmark_star_unselected_black);
        }

    }
}
