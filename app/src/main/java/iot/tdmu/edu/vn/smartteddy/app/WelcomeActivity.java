package iot.tdmu.edu.vn.smartteddy.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iot.tdmu.edu.vn.smartteddy.intro.Session;

        public class WelcomeActivity extends AppCompatActivity {

            private ViewPager viewPager;
            private MyViewPagerAdapter myViewPagerAdapter;
            private LinearLayout dotsLayout;
            private TextView[] dots;
            private int[] layouts;
            private Button btnSkip,btnNext;
            private Session session;

            final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_welcome);

                if(Build.VERSION.SDK_INT >= 23){
                    checkMultiplePermissions();
                }


                // Checking for first time launch - before calling setContentView()
                session = new Session(this);
        if(!session.isFirstTimeLaunch()){
            launchHomeScreen();
            finish();
        }

        //Making notification bar transparent
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSkip = (Button) findViewById(R.id.btn_skip);

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.app_intro1,
                R.layout.app_intro2,
                R.layout.app_intro3};
        addBottomDots(0);

        //making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPageChangeListener);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        }
    }

    private void checkMultiplePermissions() {
        if (Build.VERSION.SDK_INT >= 23){
            List<String> permissionsNeeded = new ArrayList<>();
            List<String> permissionsList = new ArrayList<>();

            if (!addPermission(permissionsList, android.Manifest.permission.ACCESS_FINE_LOCATION)){
                permissionsNeeded.add("GPS");
            }
            if (!addPermission(permissionsList, android.Manifest.permission.ACCESS_WIFI_STATE)){
                permissionsNeeded.add("WIFI");
            }
            if (!addPermission(permissionsList, android.Manifest.permission.READ_PHONE_STATE)){
                permissionsNeeded.add("PHONE");
            }
            if (!addPermission(permissionsList, android.Manifest.permission.MEDIA_CONTENT_CONTROL)){
                permissionsNeeded.add("MEDIA");
            }
            if (!addPermission(permissionsList, android.Manifest.permission.CHANGE_WIFI_STATE)){
                permissionsNeeded.add("WIFI_STATE");
            }
            if (!addPermission(permissionsList, android.Manifest.permission.INTERNET)){
                permissionsNeeded.add("INTERNET");
            }
            if (!addPermission(permissionsList, Manifest.permission.CAMERA)){
                permissionsNeeded.add("CAMERA");
            }
            if (!addPermission(permissionsList, Manifest.permission.BLUETOOTH)){
                permissionsNeeded.add("BLUETOOTH");
            }

            if (permissionsList.size() > 0){
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= 23){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                permissionsList.add(permission);

                //check for Rationable Option
                if (!shouldShowRequestPermissionRationale(permission)){
                    return false;
                }

            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                Map<String,Integer> perms = new HashMap<String,Integer>();
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION,PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.INTERNET,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.BLUETOOTH,PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length;i++){
                    perms.put(permissions[i],grantResults[i]);
                }
                if (perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                        &&perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        &&perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        &&perms.get(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED){
                    return;
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        Toast.makeText(
                                getApplicationContext(),
                                "My App cannot run without cannot operate!" +
                                        "\nRelaunch My App or allow permissions" +
                                        " in Applications Settings",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private void launchHomeScreen() {
        session.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, Scan_BlutoothActivity.class));
        finish();
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    ViewPager.OnPageChangeListener viewPageChangeListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private class MyViewPagerAdapter extends PagerAdapter{

        private LayoutInflater layoutInflater;

        private MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}


