package com.zinedroid.gallerynew.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zinedroid.gallerynew.R;
import com.zinedroid.gallerynew.activity.HomeActivity;
import com.zinedroid.gallerynew.adapters.FlipImageAdapter;
import com.zinedroid.gallerynew.base.BaseFragment;
import com.zinedroid.gallerynew.common.Constants;

import se.emilsjolander.flipview.FlipView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleImageFragment extends BaseFragment {

    FlipView mImageFlipView;

    public SingleImageFragment() {
        // Required empty public constructor
        HomeActivity.mBaseFragment = this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mSingleImageFragmentView = inflater.inflate(R.layout.fragment_single_image, container, false);
        initComponents(mSingleImageFragmentView);
        return mSingleImageFragmentView;
    }

    private void initComponents(View mSingleImageFragmentView) {
        mImageFlipView = (FlipView) mSingleImageFragmentView.findViewById(R.id.mImageFlipView);
        FlipImageAdapter mFlipImageAdapter = new FlipImageAdapter(this.getContext(), R.layout.item_image_flip, Constants.sAllMediaFolderList);
        mImageFlipView.setAdapter(mFlipImageAdapter);
        mImageFlipView.flipTo(Constants.sPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.mBaseFragment = this;
    }
}
