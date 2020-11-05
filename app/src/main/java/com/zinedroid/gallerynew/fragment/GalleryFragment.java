package com.zinedroid.gallerynew.fragment;


import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.zinedroid.gallerynew.R;
import com.zinedroid.gallerynew.activity.HomeActivity;
import com.zinedroid.gallerynew.adapters.GalleryAdapter;
import com.zinedroid.gallerynew.base.BaseFragment;
import com.zinedroid.gallerynew.common.Constants;
import com.zinedroid.gallerynew.common.Functions;
import com.zinedroid.gallerynew.models.Folder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends BaseFragment implements Functions.GoDown {

    RecyclerView mFolderRecyclerView;

    ArrayList<File> mImageFileList = new ArrayList<>();
    ArrayList<Folder> mAllMediaFolderList;
    Functions.ChangeFragment mChangeFragment;
    ArrayList<Folder> mSelectionList;
    GalleryAdapter mFolderAdapter;
    int i = 6;
    String parentfolder, parentfolderpath, position;
    File soursefile;
    Functions.UpdateThumbnamil mUpdateThumbnail;

    public GalleryFragment() {
        // Required empty public constructor
        HomeActivity.mBaseFragment = this;
    }


    @Override
    public void onResume() {
        super.onResume();
        //  mUpdateThumbnail = (Functions.UpdateThumbnamil) getActivity();
        HomeActivity.mBaseFragment = this;
        initComponents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mGalleryFragmentView = inflater.inflate(R.layout.fragment_gallery, container, false);
        mChangeFragment = (Functions.ChangeFragment) getActivity();
        mFolderRecyclerView = (RecyclerView) mGalleryFragmentView.findViewById(R.id.mFolderRecyclerView);
        setHasOptionsMenu(true);

        return mGalleryFragmentView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initComponents();
    }

    /*  @Override
public void onAttach(Activity activity) {
       super.onAttach(activity);
       try {
           mUpdateThumbnail = (UpdateThumbnamil) GalleryFragment.this;
       } catch (ClassCastException e) {
           throw new ClassCastException(activity.toString()
                   + " must implement MyInterface ");
       }

   }*/
    private void initComponents() {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            parentfolder = bundle.getString("ParentFolderName");
            parentfolderpath = bundle.getString("ParentFolderPath");
            position = bundle.getString("FolderPosition");
            Log.d("ParentFolderName", parentfolder);
            Log.d("ParentFolderPath", parentfolderpath);
        }
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        GridLayoutManager mGridLayoutManager;
        if (width > height) {
            mGridLayoutManager = new GridLayoutManager(getActivity(), 5, GridLayoutManager.VERTICAL, false);
        } else {
            mGridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        }
        mFolderRecyclerView.setAdapter(null);
        mFolderRecyclerView.setLayoutManager(null);
        mFolderRecyclerView.invalidate();
        mFolderRecyclerView.setLayoutManager(mGridLayoutManager);
        mFolderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        sortImages(new File(Constants.mclickedFolder.getFolderPath()));
        mFolderAdapter = new GalleryAdapter(GalleryFragment.this, mAllMediaFolderList);
        mFolderRecyclerView.setAdapter(mFolderAdapter);
        mFolderAdapter.notifyDataSetChanged();
    }

    void sortImages(File mfile) {


        mAllMediaFolderList = new ArrayList<>();
        ArrayList<File> mDirectoryList = new ArrayList<>();
        File[] mFileLists = mfile.listFiles();
        for (int i = 0; i < mFileLists.length; i++) {
            Log.v(" filesize ", String.valueOf(mFileLists.length));
          /*  if (mFileLists[i].isDirectory()) {

                Log.d("directoryyy is ",mFileLists[i].getParent());
                mDirectoryList.add(mFileLists[i]);
                if(mFileLists[i].getParent().contains("/storage/emulated/0/Download/New folder2/"+parentfolder+"/")){
                    Log.d("childfolder getname ",new File(mFileLists[i].getParent()).getName());
                    Log.d(" chiledfolder parent ",(mFileLists[i].getParent()));
                    Log.d("new folder absolute ",mFileLists[i].getAbsolutePath());
                    Folder newfolder=new Folder();
                    newfolder.setFolderPath(mFileLists[i].getParent());
                    newfolder.setFolderName(new File(mFileLists[i].getParent()).getName());
                    newfolder.setImagePath(mFileLists[i].getAbsolutePath());
                    mAllMediaFolderList.add(newfolder);
                }

            }
*/

            if (isImageFile(mFileLists[i].getAbsolutePath())) {
                mImageFileList.add(mFileLists[i]);
                Folder mFolder = new Folder();
                mFolder.setFolderPath(mFileLists[i].getParent());
                mFolder.setFolderName(mFileLists[i].getName());
                mFolder.setImagePath(mFileLists[i].getAbsolutePath());
                mFolder.setFiletype("image");
                mAllMediaFolderList.add(mFolder);
            } else if (isVideoFile(mFileLists[i].getAbsolutePath())) {
                mImageFileList.add(mFileLists[i]);
                Folder mFolder = new Folder();
                mFolder.setFolderPath(mFileLists[i].getParent());
                mFolder.setFolderName(mFileLists[i].getName());
                mFolder.setImagePath(mFileLists[i].getAbsolutePath());
                mFolder.setFiletype("video");
                mAllMediaFolderList.add(mFolder);
            }

        }
        for (int j = 0; j < mDirectoryList.size(); j++) {
            Log.v(" else filename is ", mFileLists[j].getParent());
            sortImages(mDirectoryList.get(j));
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_folder, menu);  // Use filter.xml from step 1
        MenuItem shareItem = menu.findItem(R.id.share);

        getActivity().invalidateOptionsMenu();

        try {
            if (mSelectionList.size() > 0) {
                shareItem.setVisible(true);
                getActivity().invalidateOptionsMenu();
                //    invalidateOptionsMenu();

            } else {
                shareItem.setVisible(false);
                getActivity().invalidateOptionsMenu();
            }
        } catch (Exception e) {
            e.printStackTrace();
            shareItem.setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.selectAll) {
            mSelectionList = new ArrayList();
            for (Folder folder : mAllMediaFolderList) {
                folder.setSelected(true);
                mSelectionList.add(folder);

            }
            Log.d("selected listsize", String.valueOf(mSelectionList.size()));
            mFolderAdapter = new GalleryAdapter(GalleryFragment.this, mAllMediaFolderList);
            mFolderRecyclerView.setAdapter(mFolderAdapter);
            return true;
        } else if (id == R.id.unSelectAll) {
            if (mSelectionList.size() > 0) {
                for (Folder folder : mAllMediaFolderList) {
                    folder.setSelected(false);
                    mSelectionList.remove(folder);

                }
            }
            Log.d("unseleted listsize", String.valueOf(mSelectionList.size()));
            mFolderAdapter = new GalleryAdapter(GalleryFragment.this, mAllMediaFolderList);
            mFolderRecyclerView.setAdapter(mFolderAdapter);
            return true;
        } else if (id == R.id.delete) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Are you sure you want to delete?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (mSelectionList.size() > 0) {
                                //  mSelectionList.clear();
                                Log.d("mSelectionList.size", String.valueOf(mSelectionList.size()));
                                for (int i = 0; i < mSelectionList.size(); i++) {
                                    Folder folder = mSelectionList.get(i);
                                    String selected_foldername = folder.getFolderName();
                                    for (int j = 0; j < mAllMediaFolderList.size(); j++) {
                                        Folder folder1 = mAllMediaFolderList.get(j);
                                        String unique_foldername = folder1.getFolderName();
                                        if (selected_foldername.equalsIgnoreCase(unique_foldername)) {
                                            mAllMediaFolderList.remove(j);
                                            //delete from internal memory
                                            Boolean delete = deleteRecursive(new File(folder1.getImagePath()));
                                            Log.d("deleted folder", String.valueOf(delete));
                                            Log.d("deleted file", folder1.getFolderPath());
                                        }
                                    }

                                    Log.d("mUniqFolderListt", String.valueOf(mAllMediaFolderList.size()));
                                }

                                mSelectionList.clear();

                                mFolderAdapter = new GalleryAdapter(GalleryFragment.this, mAllMediaFolderList);
                                mFolderRecyclerView.setAdapter(mFolderAdapter);

                            }

                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        } else if (id == R.id.share) {
            if (mSelectionList.size() > 0) {
                for (int i = 0; i < mSelectionList.size(); i++) {
                    Folder folder = mSelectionList.get(i);
                    Uri imageUri = Uri.parse(folder.getImagePath());
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    //Target whatsapp:
                    shareIntent.setPackage("com.whatsapp");

                    //Add text and then Image URI
                    //  shareIntent.putExtra(Intent.EXTRA_TEXT, picture_text);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/jpeg");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        startActivity(Intent.createChooser(shareIntent, "Share Image"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "Please Install Whatsapp", Toast.LENGTH_LONG).show();
                    }

                }

            }
        } else if (id == R.id.copy) {

            /* mUpdateThumbnail.updatethumbnail(Integer.parseInt(position),"storage/emulated/0/Download/album4/album4/images1.jpeg");*/

            Constants.mCopiedFiles = new ArrayList();
            if (mSelectionList.size() > 0) {
                for (int i = 0; i < mSelectionList.size(); i++) {
                    Folder folder = mSelectionList.get(i);
                    //   soursefile = new File(folder.getImagePath());
                    Constants.mCopiedFiles.add(i, folder.getImagePath());
                    //     Constants.soursefolder=folder.getImagePath();
                    Log.d("soursefolder", folder.getFolderPath());

                }
            }
        } else if (id == R.id.paste) {
            try {
                if (Constants.mCopiedFiles.size() > 0) {
                    Constants.destinationfolder = parentfolderpath;
                    for (int j = 0; j < Constants.mCopiedFiles.size(); j++) {
                        Log.d("copiedfolder", Constants.mCopiedFiles.get(j).toString());
                        copyFile(Constants.mCopiedFiles.get(j).toString(), Constants.destinationfolder);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean copyFile(String from, String to) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                int end = from.toString().lastIndexOf("/");
                String str1 = from.toString().substring(0, end);
                String str2 = from.toString().substring(end + 1, from.length());
                Log.d("from_folderpath", str1);
                Log.d("copied_imagename", str2);
                Log.d("destination folder", to);
                File source = new File(str1, str2);
                File destination = new File(to, str2);


                if (source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Folder mFolder = new Folder();
                    mFolder.setFolderPath(to);
                    mFolder.setFolderName(destination.getName());
                    mFolder.setImagePath(to + "/" + str2);
                    int flag = 0;
                    ///check folder value is present in mALLMedialIST

                    for (Folder folder : mAllMediaFolderList) {
                        //  Log.d("pasted_imagepath", mFolder.getImagePath());
                        if (mFolder.getImagePath().equalsIgnoreCase(folder.getImagePath())) {
                            flag = 1;
                            Toast.makeText(getActivity(), "Filename exists", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (flag == 0) {
                        //  Log.d("pasted_imagepath", folder.getImagePath());
                        mAllMediaFolderList.add(mFolder);
                        if (isImageFile(mFolder.getImagePath())) {
                            mFolder.setFiletype("image");

                        } else if (isVideoFile(mFolder.getImagePath())) {
                            mFolder.setFiletype("video");
                        } else {
                            mFolder.setFiletype("other");
                        }
//update folder table
                       /* Folder folder = new Folder();
                        folder = mFolder;*/

                        ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folder_path = " + DatabaseUtils.sqlEscapeString(mFolder.getFolderPath()) + " and image_path = " + DatabaseUtils.sqlEscapeString(mFolder.getFolderPath()));

                        if (mFolderList.size() > 0) {
                            Folder folder=mFolderList.get(0);
                            folder.setImagePath(mFolder.getImagePath());
                            folder.save();
                            mFolder.save();
                        }

                        mFolderAdapter = new GalleryAdapter(GalleryFragment.this, mAllMediaFolderList);
                        mFolderRecyclerView.setAdapter(mFolderAdapter);
                    }
                    //                mUpdateThumbnail.updatethumbnail(Integer.parseInt(position),to + "/" + str2);

                }

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean deleteRecursive(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
//            Log.v("file size ", String.valueOf(files.length));
            if (files == null) {
                //  return true;
                Log.v("path deleted ", "deleted");
                return (path.delete());

            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteRecursive(files[i]);
                } else {
                    files[i].delete();
                    Log.v("deleted ", "deleted");
                }
            }

        }

        return (path.delete());
    }


    @Override
    public void updatePage(Folder mFolder, int position, String filetype) {

        try {
            if ((mSelectionList != null) && (mSelectionList.size() != 0)) {
                if (mSelectionList.size() > 0) {
                    //  mToolBar.setVisibility(View.VISIBLE);
                    mFolder.setSelected(!mFolder.isSelected());
                    if (mFolder.isSelected() == false) {
                        mSelectionList.remove(mFolder);
                        Log.d("mSelectionList", String.valueOf(mSelectionList.size()));

                    } else {
                        if (Constants.mSelectedFolder.isSelected()) {
                            mSelectionList.add(mFolder);
                            Log.d("mSelectionList", String.valueOf(mSelectionList.size()));
                        } else {
                            mSelectionList.remove(mFolder);
                            Log.d("mSelectionList", String.valueOf(mSelectionList.size()));
                        }

                    }

                    mAllMediaFolderList.set(position, mFolder);
                    mFolderAdapter = new GalleryAdapter(GalleryFragment.this, mAllMediaFolderList);
                    mFolderRecyclerView.setAdapter(mFolderAdapter);
                } else {
                    //    mToolBar.setVisibility(View.GONE);
                }
            } else {
                if (filetype.equalsIgnoreCase("image")) {
                    Constants.sPosition = position;
                    Constants.sAllMediaFolderList = mAllMediaFolderList;
                    mChangeFragment.onFragmentChange(new SingleImageFragment(), true);
                } else if (filetype.equalsIgnoreCase("video")) {
                    mChangeFragment.onFragmentChange(new VideoViewFragment(), true);
                }


            }
        } catch (Exception e) {
            if (filetype.equalsIgnoreCase("image")) {
                Constants.sPosition = position;
                Constants.sAllMediaFolderList = mAllMediaFolderList;
                mChangeFragment.onFragmentChange(new SingleImageFragment(), true);
                e.printStackTrace();
            } else if (filetype.equalsIgnoreCase("video")) {
                mChangeFragment.onFragmentChange(new VideoViewFragment(), true);
            }


        }


    }

    @Override
    public void onLongPress(Folder folder, int position) {

        folder.setSelected(true);
        if (mSelectionList == null) {
            mSelectionList = new ArrayList<>();
        }
        Constants.mSelectedFolder = folder;
        mSelectionList.add(folder);
        Log.d("mSelectionList", String.valueOf(mSelectionList.size()));
        //   mToolBar.setVisibility(View.VISIBLE);
        mFolderAdapter = new GalleryAdapter(GalleryFragment.this, mAllMediaFolderList);
        mFolderRecyclerView.setAdapter(mFolderAdapter);

    }

    @Override
    public void onAddFolder(int position) {

        String newfoldername = "New Folder" + i;
        i = i + 2;
        File newfile = createFolder(newfoldername);

        try {
            File[] contents = newfile.listFiles();
// the directory file is not really a directory..
            if (contents != null) {
                Folder newfolder = new Folder();
                newfolder.setFolderPath(newfile.getParent());
                newfolder.setFolderName(newfoldername);
                newfolder.setImagePath(newfile.getAbsolutePath());
                newfolder.setSelected(false);
                Log.d("new folder path", newfile.getParent());
                Log.d("new folder name", new File(newfile.getParent()).getName());
                Log.d("new image path", newfile.getAbsolutePath());

                mAllMediaFolderList.add(position, newfolder);
                mFolderAdapter = new GalleryAdapter(GalleryFragment.this, mAllMediaFolderList);
                mFolderRecyclerView.setAdapter(mFolderAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public File createFolder(String fname) {
        String myfolder = Environment.getExternalStorageDirectory() + "/" + "Download" + "/" + "New folder2" + "/" + parentfolder + "/" + fname;
        File f = new File(myfolder);
        if (!f.exists())
            if (!f.mkdirs()) {
                Toast.makeText(getActivity(), myfolder + " can't be created.", Toast.LENGTH_SHORT).show();
                Log.d("myfolder", "cant create");
            } else {
                Toast.makeText(getActivity(), myfolder + " can be created.", Toast.LENGTH_SHORT).show();
                Log.d("myfolder", myfolder + "created");
                return f;
            }

        else {
            Toast.makeText(getActivity(), myfolder + " already exits.", Toast.LENGTH_SHORT).show();
            Log.d("myfolder", "exist");
            return null;
        }

        return f;
    }
   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mUpdateThumbnail = (Functions.UpdateThumbnamil) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemClickedListener");
        }
    }*/

}
