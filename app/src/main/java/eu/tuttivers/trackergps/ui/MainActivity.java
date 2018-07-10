package eu.tuttivers.trackergps.ui;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import eu.tuttivers.trackergps.R;

public class MainActivity extends AppCompatActivity {

    private MapsFragment mapsFragment = new MapsFragment();
    private MyPreferenceFragment myPreferenceFragment = new MyPreferenceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    @Subscribe
    public void onReceiveLocation(String message) {
        mapsFragment.showLocation(message);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapsFragment.sendRequestToTracker();
                } else {
                    Toast.makeText(this, "SMS permission not granted, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mapsFragment;
            } else {
                return myPreferenceFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
