package com.zinedroid.gallerynew.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.zinedroid.gallerynew.R;
import com.zinedroid.gallerynew.activity.HomeActivity;
import com.zinedroid.gallerynew.base.BaseFragment;
import com.zinedroid.gallerynew.common.Constants;


/**
 * Created by Anjumol Johnson on 20/12/18.
 */
public class SettingsFragment extends BaseFragment {
    CheckBox mShowHiddenCheckBox,mShowNoMediaCheckBox;

    public SettingsFragment() {
        // Required empty public constructor
        HomeActivity.mBaseFragment = this;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mFolderViewFragmentView = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);
        initComponents(mFolderViewFragmentView);
        return mFolderViewFragmentView;
    }
    public void initComponents(View mFolderViewFragmentView){
        mShowHiddenCheckBox=(CheckBox)mFolderViewFragmentView.findViewById(R.id.mShowHiddenCheckBox);
        mShowNoMediaCheckBox=(CheckBox)mFolderViewFragmentView.findViewById(R.id.mShowNoMediaCheckBox);
        mShowHiddenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setSharedPreference(Constants.ApiConstants.SHOWHIDDENMEDIA,"true");

                }
                else {
                    setSharedPreference(Constants.ApiConstants.SHOWHIDDENMEDIA,"false");
                }
            }
        });

        mShowNoMediaCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setSharedPreference(Constants.ApiConstants.SHOWNOMEDIA,"true");

                }
                else {
                    setSharedPreference(Constants.ApiConstants.SHOWNOMEDIA,"false");
                }
            }
        });



        if(getSharedPreference(Constants.ApiConstants.SHOWNOMEDIA).equalsIgnoreCase("true")){
            mShowNoMediaCheckBox.setChecked(true);
        }
        else {
            mShowNoMediaCheckBox.setChecked(false);
        }

        if(getSharedPreference(Constants.ApiConstants.SHOWHIDDENMEDIA).equalsIgnoreCase("true")){
            mShowHiddenCheckBox.setChecked(true);
        }
        else {
            mShowHiddenCheckBox.setChecked(false);
        }

    }


}