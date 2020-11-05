package com.zinedroid.gallerynew.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import com.zinedroid.gallerynew.R;
import com.zinedroid.gallerynew.common.ZoomableImageView;
import com.zinedroid.gallerynew.models.Folder;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Cecil Paul on 9/10/18.
 */
public class FlipImageAdapter extends ArrayAdapter<Folder> {
    Context context;
    int resource;
    ArrayList<Folder> mImageArrayList;

    public FlipImageAdapter(Context context, int resource, ArrayList<Folder> mImageArrayList) {
        super(context, resource, mImageArrayList);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(resource, null);
            mViewHolder.mFlipImageView = (ZoomableImageView) convertView.findViewById(R.id.mFlipImageView);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        File mFile = new File(getItem(position).getImagePath());
        try {
            Picasso.with(context).load(Uri.fromFile(mFile)).into(mViewHolder.mFlipImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    protected class ViewHolder {
        ZoomableImageView mFlipImageView;
    }
}
