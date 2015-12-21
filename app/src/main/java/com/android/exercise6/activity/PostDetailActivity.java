package com.android.exercise6.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.exercise6.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Khang on 20/12/2015.
 */
public class PostDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostViewActivity";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.webview)
    WebView wv;

    ProgressDialog dialog = null;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail_activity);
        ButterKnife.bind(this);

        url = getIntent().getStringExtra("URL");
        String selftext_html = getIntent().getStringExtra("selftext_html");
        String subReddit = getIntent().getStringExtra("subreddit");
        String title = getIntent().getStringExtra("title");

        setSupportActionBar(toolbar);

        dialog = ProgressDialog.show(this, null, "Loading...", true, false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setSubtitle(subReddit);
        setTitle(title);

        wv.setWebChromeClient(new WebChromeClient());
        wv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                dialog = ProgressDialog.show(view.getContext(), null, "Loading...", true, false);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(view.getTitle());
                if (dialog != null)
                    dialog.dismiss();
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                PostDetailActivity.this.url = url;
                super.onPageStarted(view, url, favicon);
            }
        });
        if (selftext_html == null || selftext_html.equalsIgnoreCase("null"))
            wv.loadUrl(url);
        else
            wv.loadData("<head>" +
                    "<title>" + title + "</title>" +
                    "</head>" + selftext_html, "text/html", "UTF-8");

    }

    @Override
    public void onBackPressed() {
        if (wv != null && wv.canGoBack())
            wv.goBack();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ac_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(i, "Share URL"));
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
