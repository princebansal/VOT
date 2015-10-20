package com.gdgvitvellore.volsbbonetouch;

/**
 * Created by shalini on 30-03-2015.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;


/**
 * Created by shalini on 25-12-2014.
 */
public class TabActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    LinearLayout lefttabbar,righttabbar;
    TextView logintab,settingstab;
    OnActivityPageChangeListener mainFragment;
    OnActivityPageChangeListener settingsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        init();
        setPager();

    }

    private void setPager() {
        mPagerAdapter = new PageSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }


    private void init() {
        mPager = (ViewPager) findViewById(R.id.pager1);
        logintab=(TextView)findViewById(R.id.logintab);
        settingstab=(TextView)findViewById(R.id.settingstab);
        //logintab.setOnTouchListener(this);
        //settingstab.setOnTouchListener(this);
        logintab.setOnClickListener(this);
        settingstab.setOnClickListener(this);
        lefttabbar=(LinearLayout)findViewById(R.id.lefttabbar);
        righttabbar=(LinearLayout)findViewById(R.id.righttabbar);
    }



    @Override
    public void onPageScrolled(int i, float v, int i2) {
        if(i==0){
            mainFragment.OnPageChange(i);
        }
        if (i==1){
            settingsFragment.OnPageChange(i);
        }
    }

    @Override
    public void onPageSelected(int i) {
        if(i==0)
        {
            logintab.setTextColor(getResources().getColor(android.R.color.white));
            settingstab.setTextColor(getResources().getColor(R.color.whitelight));
            lefttabbar.setVisibility(LinearLayout.VISIBLE);
            righttabbar.setVisibility(LinearLayout.INVISIBLE);
        }
        if(i==1)
        {
            logintab.setTextColor(getResources().getColor(R.color.whitelight));
            settingstab.setTextColor(getResources().getColor(android.R.color.white));
            lefttabbar.setVisibility(LinearLayout.INVISIBLE);
            righttabbar.setVisibility(LinearLayout.VISIBLE);

        }

    }

    @Override
    public void onPageScrollStateChanged(int i) {


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.logintab:
                mPager.setCurrentItem(0);
                break;
            case R.id.settingstab:
                mPager.setCurrentItem(1);
                break;
        }
    }

   /* @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.logintab:
                    logintab.setBackgroundColor(getResources().getColor(R.color.tabbgpressed));
                    break;
                case R.id.settingstab:
                    settingstab.setBackgroundColor(getResources().getColor(R.color.tabbgpressed));
                    break;
            }
        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            switch (v.getId()) {
                case R.id.logintab:
                    logintab.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    break;
                case R.id.settingstab:
                    settingstab.setBackgroundColor(getResources().getColor(android.R.color.transparent


                    ));
                    break;
            }
        }
        return false;
    }*/
    public void setOnActivityPageChangeListener(OnActivityPageChangeListener onActivityPageChangeListener,String s)
    {
        if(s.equals("main")){
            mainFragment=(OnActivityPageChangeListener)onActivityPageChangeListener;
        }
        if(s.equals("settings")){
            settingsFragment=(OnActivityPageChangeListener)onActivityPageChangeListener;
        }
    }

    private class PageSlidePagerAdapter extends FragmentStatePagerAdapter {
        public PageSlidePagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }
        @Override
        public Fragment getItem(int position) {

            if(position==0)
            {
                return new MainActivity();
            }
            else
            {
                return new SettingsActivity();
            }

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
    public interface OnActivityPageChangeListener{
        public void OnPageChange(int i);
            }
}

