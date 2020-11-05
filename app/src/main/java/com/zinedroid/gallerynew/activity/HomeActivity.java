package com.zinedroid.gallerynew.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.zinedroid.gallerynew.R;
import com.zinedroid.gallerynew.base.BaseActivity;
import com.zinedroid.gallerynew.base.BaseFragment;
import com.zinedroid.gallerynew.common.Functions;
import com.zinedroid.gallerynew.fragment.FolderViewFragment;

public class HomeActivity extends BaseActivity implements Functions.ChangeFragment {

    private FragmentManager fm;
    public static BaseFragment mBaseFragment;
    private Toolbar mTopToolbar;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        setComponents();
    }

    private void setComponents() {
        FragmentManager fm = getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.add(R.id.fragment, new FolderViewFragment());
        fragmentTransaction.commit();
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_folder, menu);
        return true;
    }*/


    @Override
    public void onFragmentChange(BaseFragment mBaseFragment, boolean isReplace) {

        fm = getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        if (isReplace) {
            fragmentTransaction.replace(R.id.fragment, mBaseFragment);
        } else {
            fragmentTransaction.add(R.id.fragment, mBaseFragment);
        }
        fragmentTransaction.addToBackStack("Gallery");
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        if (mBaseFragment.getClass().getSimpleName().equals("FolderViewFragment")) {
            super.onBackPressed();
        } else if ((mBaseFragment.getClass().getSimpleName().equals("GalleryFragment"))) {
            fm.popBackStack();
        }
        else if ((mBaseFragment.getClass().getSimpleName().equals("SettingsFragment"))) {
            mBaseFragment = new FolderViewFragment();
            onFragmentChange(mBaseFragment, true);
        }
        else{
            fm.popBackStack();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

}
