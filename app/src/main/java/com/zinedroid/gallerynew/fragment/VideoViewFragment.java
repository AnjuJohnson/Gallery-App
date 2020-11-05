package com.zinedroid.gallerynew.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.zinedroid.gallerynew.R;
import com.zinedroid.gallerynew.activity.HomeActivity;
import com.zinedroid.gallerynew.base.BaseFragment;
import com.zinedroid.gallerynew.common.Constants;

/**
 * Created by Anjumol Johnson on 1/1/19.
 */
public class VideoViewFragment extends BaseFragment {

    VideoView videoView;

    public VideoViewFragment() {
        // Required empty public constructor
        HomeActivity.mBaseFragment = this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mSingleImageFragmentView = inflater.inflate(R.layout.fragment_videoview, container, false);
        initComponents(mSingleImageFragmentView);
        return mSingleImageFragmentView;
    }

    public void initComponents(View mSingleImageFragmentView) {
        videoView = (VideoView) mSingleImageFragmentView.findViewById(R.id.videoViewRelative);


        //Creating MediaController
        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(videoView);
        Uri uri = Uri.parse(Constants.videopath);
        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }

}