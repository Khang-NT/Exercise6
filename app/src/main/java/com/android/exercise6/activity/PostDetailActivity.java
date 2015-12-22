package com.android.exercise6.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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

        showDialog();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setSubtitle(subReddit);
        setTitle(title);

        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAppCacheEnabled(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setSupportZoom(true);

        wv.setWebChromeClient(new WebChromeClient());
        wv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                showDialog();
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(view.getTitle());
                dismissDialog();
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
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        wv.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        if (wv != null && wv.canGoBack())
            wv.goBack();
        else
            super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    private void showDialog() {
        if (dialog != null && dialog.isShowing())
            return;
        dialog = ProgressDialog.show(this, null, "Loading...", true, false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.e(TAG, "onKey: back press in dialog");
                    dismissDialog();
                }
                return true;
            }
        });
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
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
