package com.android.exercise6.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 10/8/15.
 */
public class RedditPost {
    private String id;
    private String title;
    private int score;
    private int commentCount;
    private String url;
    private boolean isStickyPost;
    private String author;
    private String subreddit;
    private String domain;
    private long createdUTC;
    private String selfText;
    private String selftext_html;

    private JSONObject data;

    public RedditPost() {

    }

    public RedditPost(JSONObject data) throws JSONException {
        this.data = data;
        setAuthor(data.getString("author"));
        setCommentCount(data.getInt("num_comments"));
        setCreatedUTC(data.getLong("created_utc"));
        setDomain(data.getString("domain"));
        setId(data.getString("id"));
        setTitle(data.getString("title"));
        setScore(data.getInt("score"));
        setUrl(data.getString("url"));
        setIsStickyPost(data.getBoolean("stickied"));
        setSubreddit(data.getString("subreddit"));
        setSelfText(data.getString("selftext"));

        setSelftext_html(data.getString("selftext_html"));

    }

    public String getSelftext_html() {
        return selftext_html;
    }

    public void setSelftext_html(String selftext_html) {
        this.selftext_html = selftext_html;
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isStickyPost() {
        return isStickyPost;
    }

    public void setIsStickyPost(boolean isStickyPost) {
        this.isStickyPost = isStickyPost;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public long getCreatedUTC() {
        return createdUTC;
    }

    public void setCreatedUTC(long createdUTC) {
        this.createdUTC = createdUTC;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setSelfText(String selfText) {
        this.selfText = selfText;
    }

    public String getSelfText() {
        return selfText;
    }
}