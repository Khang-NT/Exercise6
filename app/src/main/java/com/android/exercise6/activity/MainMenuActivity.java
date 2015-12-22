package com.android.exercise6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.exercise6.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Khang on 23/12/2015.
 */
public class MainMenuActivity extends AppCompatActivity {
    private static final String TAG = "MainMenuActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.b_view_tab)
    public void onB1Click() {
        Intent intent = new Intent(this, PostListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.b_view_navigation_drawer)
    public void onB2Click() {
        Intent intent = new Intent(this, NavigationDrawerPostActivity.class);
        startActivity(intent);
    }
}
