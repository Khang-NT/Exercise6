package com.android.exercise6.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.android.exercise6.R;
import com.android.exercise6.fragment.PostInTopicFragment;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Khang on 21/12/2015.
 */
public class NavigationDrawerPostActivity extends AppCompatActivity {
    private static final String TAG = "NavigationDrawerPostActivity", SELECTING_ID = "SELECTING_ID";
    public static final String ANDROID_DEV = "ANDROIDDEV",
            MOVIES = "MOVIES",
            PICS = "PICS",
            FOODS = "FOOD",
            MUSIC = "MUSIC",
            COMIC = "COMIC";
    private static final java.lang.String TITLE = "TITLE";
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.container)
    FrameLayout container;
    @Bind(R.id.drawer_layout)
    public DrawerLayout drawerLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    ActionBarDrawerToggle drawerToggle;

    private static HashMap<Integer, String> topics = new HashMap<>();

    private static final String BOOKMARKS = "BOOKMARKS";


    static {
        topics = new HashMap<>();
        topics.put(R.id.androiddev, ANDROID_DEV);
        topics.put(R.id.movies, MOVIES);
        topics.put(R.id.pics, PICS);
        topics.put(R.id.foods, FOODS);
        topics.put(R.id.music, MUSIC);
        topics.put(R.id.comic, COMIC);
        topics.put(R.id.bookmark, BOOKMARKS);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0) {

        };

        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//                    case R.id.androiddev:
//                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ANDROID_DEV);
//                        if (fragment == null)
//                            fragment = PostInTopicFragment
//                        break;
//                }
                String topic = topics.get(menuItem.getItemId());
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(topic);
                if (fragment == null)
                    fragment = PostInTopicFragment.newInstance(topic);
                showFragment(fragment, topic);
                setTitle(topic);
                drawerLayout.closeDrawer(Gravity.START);
                return true;
            }
        });


        if (savedInstanceState == null) {
            showFragment(PostInTopicFragment.newInstance(ANDROID_DEV), ANDROID_DEV);
            navigationView.setCheckedItem(R.id.androiddev);
            setTitle(topics.get(R.id.androiddev));
        } else
            setTitle(savedInstanceState.getString(TITLE));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE, getTitle().toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments)
            if (fragment instanceof PostInTopicFragment)
                fragmentManager.putFragment(outState, ((PostInTopicFragment) fragment).getTopic(), fragment);

    }

    private void showFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit();
    }
}
