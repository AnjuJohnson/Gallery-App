package com.zinedroid.gallerynew.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zinedroid.gallerynew.R;
import com.zinedroid.gallerynew.base.BaseFragment;
import com.zinedroid.gallerynew.common.Functions;
import com.zinedroid.gallerynew.models.Folder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cecil Paul on 30/8/18.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private List<Folder> mImageList;
    Context context;
    Functions.GoDown mGoDown;

    public FolderAdapter(Context context, ArrayList<Folder> imagesPath) {
        mImageList = imagesPath;
        this.context = context;
        mGoDown = (Functions.GoDown) context;
    }

    public FolderAdapter(BaseFragment folderViewFragment, ArrayList<Folder> imagesPath) {
        mImageList = imagesPath;
        this.context = folderViewFragment.getContext();
       mGoDown = (Functions.GoDown) folderViewFragment;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout mSelectionLayout;
        // each data item is just a string in this case
        ImageView mGalleryImageView,mTypeIconImageView;
        TextView mFolderTextView;
        public ImageView button;


        public ViewHolder(View v) {
            super(v);
            button = (ImageView) v.findViewById(R.id.mAddButtonImageView);
            mGalleryImageView = (ImageView) v.findViewById(R.id.mGalleryImageView);
            mFolderTextView = (TextView) v.findViewById(R.id.mFolderTextView);
            mSelectionLayout = (FrameLayout) v.findViewById(R.id.mSelectionLayout);
            mTypeIconImageView = (ImageView) v.findViewById(R.id.mTypeIconImageView);
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public FolderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View itemView;

        if(viewType == R.layout.item_gallery){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        }

        else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_button_layout, parent, false);
        }

        return new ViewHolder(itemView);


    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position == mImageList.size()) {
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoDown.onAddFolder(position);
                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            final Folder mFolder = mImageList.get(position);
            File mFile = new File(mFolder.getImagePath());
            holder.mFolderTextView.setText(mFolder.getFolderName());
            Log.d("imageurl", String.valueOf(Uri.fromFile(mFile)));

            if(mFolder.getFiletype().equalsIgnoreCase("image")){
             //   Picasso.with(context).load(Uri.fromFile(mFile)).into(holder.mGalleryImageView);
                holder.mGalleryImageView.setImageURI(Uri.fromFile(mFile));
                holder.mTypeIconImageView.setImageResource(R.drawable.ic_image_black_24dp);

            }
            else if(mFolder.getFiletype().equalsIgnoreCase("video")){

                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(mFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                holder.mGalleryImageView.setImageBitmap(bMap);


                holder.mTypeIconImageView.setImageResource(R.drawable.ic_videocam_black_24dp);
            }
            else if(mFolder.getFiletype().equalsIgnoreCase("other")){
                holder.mTypeIconImageView.setVisibility(View.GONE);
            }


            holder.mFolderTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGoDown.updatePage(mFolder, position,"other");
                }
            });
            holder.mGalleryImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoDown.updatePage(mFolder, position,"other");
                }
            });
            holder.mFolderTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mGoDown.onLongPress(mFolder, position);
                    return true;
                }
            });
            holder.mGalleryImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mGoDown.onLongPress(mFolder, position);
                    return true;
                }
            });
            if(mFolder.isSelected()){
                holder.mSelectionLayout.setVisibility(View.VISIBLE);
            }else{
                holder.mSelectionLayout.setVisibility(View.INVISIBLE);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mImageList.size()) ? R.layout.add_button_layout : R.layout.item_gallery;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mImageList.size()+1;
    }


}