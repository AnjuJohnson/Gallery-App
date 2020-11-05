package com.zinedroid.gallerynew.fragment;


import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.DatabaseUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.zinedroid.gallerynew.adapters.FolderAdapter;
import com.zinedroid.gallerynew.base.BaseFragment;
import com.zinedroid.gallerynew.common.Constants;
import com.zinedroid.gallerynew.common.Functions;
import com.zinedroid.gallerynew.models.Folder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderViewFragment extends BaseFragment implements Functions.GoDown {
    RecyclerView mFolderRecyclerView;
    public static ArrayList<Folder> mUniqFolderList;
    ArrayList<Folder> mSelectionList, mFolderList;
    ArrayList<File> mImageFileList = new ArrayList<>();
    private int REQUEST_PERMISSION = 1101;
    ArrayList<Folder> mAllMediaFolderList;
    Functions.ChangeFragment mChangeFragment;
    FolderAdapter mFolderAdapter;
    static String mShowNomedia, mShowHIddenMedia;
    int i = 13;
    String sourcedir, destinationdirectory;

    public FolderViewFragment() {
        // Required empty public constructor
        com.zinedroid.gallerynew.activity.HomeActivity.mBaseFragment = this;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initComponents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mFolderViewFragmentView = inflater.inflate(R.layout.fragment_folder_view, container, false);
        mFolderRecyclerView = (RecyclerView) mFolderViewFragmentView.findViewById(R.id.mFolderRecyclerView);
        //   mToolBar = (Toolbar) mFolderViewFragmentView.findViewById(R.id.toolBar);

        //  mDeleteImageView=(ImageView)mFolderViewFragmentView.findViewById(R.id.mDeleteImageView);
        mChangeFragment = (Functions.ChangeFragment) getActivity();
        //   mToolBar.setVisibility(View.GONE);
        setHasOptionsMenu(true);
        return mFolderViewFragmentView;
    }

    private void initComponents() {

        mShowNomedia = getSharedPreference(Constants.ApiConstants.SHOWNOMEDIA);
        mShowHIddenMedia = getSharedPreference(Constants.ApiConstants.SHOWHIDDENMEDIA);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        GridLayoutManager mGridLayoutManager;
        if (width > height) {
            mGridLayoutManager = new GridLayoutManager(getActivity(), 5, GridLayoutManager.VERTICAL, false);
        } else {
            mGridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        }
        mFolderRecyclerView.setLayoutManager(mGridLayoutManager);
        mFolderRecyclerView.setItemAnimator(new DefaultItemAnimator());


        getAllFolders();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_folder, menu);  // Use filter.xml from step 1
        MenuItem shareItem = menu.findItem(R.id.share);
        shareItem.setVisible(true);
        getActivity().invalidateOptionsMenu();
/*
        try {
            if (mSelectionList.size() > 0) {
                shareItem.setVisible(true);
                getActivity().invalidateOptionsMenu();
            //    invalidateOptionsMenu();

            }
            else {
                shareItem.setVisible(false);
                getActivity().invalidateOptionsMenu();
            }
        } catch (Exception e) {
            e.printStackTrace();
            shareItem.setVisible(false);
        }*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.selectAll) {
            mSelectionList = new ArrayList();
            for (Folder folder : mUniqFolderList) {
                folder.setSelected(true);
                mSelectionList.add(folder);

            }
            Log.d("selected listsize", String.valueOf(mSelectionList.size()));
            mFolderAdapter = new FolderAdapter(FolderViewFragment.this, mUniqFolderList);
            mFolderRecyclerView.setAdapter(mFolderAdapter);
            return true;
        } else if (id == R.id.unSelectAll) {
            if (mSelectionList.size() > 0) {
                for (Folder folder : mUniqFolderList) {
                    folder.setSelected(false);
                    mSelectionList.remove(folder);

                }
            }
            Log.d("unseleted listsize", String.valueOf(mSelectionList.size()));
            mFolderAdapter = new FolderAdapter(FolderViewFragment.this, mUniqFolderList);
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

                                for (int i = 0; i < mSelectionList.size(); i++) {
                                    Folder folder = mSelectionList.get(i);
                                    String selected_foldername = folder.getFolderName();
                                    for (int j = 0; j < mUniqFolderList.size(); j++) {
                                        Folder folder1 = mUniqFolderList.get(j);
                                        String unique_foldername = folder1.getFolderName();
                                        if (selected_foldername.equalsIgnoreCase(unique_foldername)) {
                                            mUniqFolderList.remove(j);

                                            //delete from internal memory
                                            Boolean delete = deleteRecursive(new File(folder1.getFolderPath()));
                                            Log.d("deleted folder", String.valueOf(delete));
                                            Log.d("deleted folder", folder1.getFolderPath());
                                            ///delete drom database

                                        }
                                    }

                                    ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder");
                                    for (Folder folder1 : mFolderList) {
                                        if (folder1.getFolderPath().equalsIgnoreCase(mSelectionList.get(i).getFolderPath())) {
                                            folder1.delete();
                                        }
                                    }

                                    /*ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folder_path = " + DatabaseUtils.sqlEscapeString( folder.getFolderPath()));
                                    if(mFolderList.size()>0){
                                        folder.delete();
                                    }*/


                                    Log.d("mUniqFolderListt", String.valueOf(mFolderList.size()));
                                }

                                mSelectionList.clear();

                                mFolderAdapter = new FolderAdapter(FolderViewFragment.this, mUniqFolderList);
                                mFolderRecyclerView.setAdapter(mFolderAdapter);

                            }


                        }
                    })
                    .setNegativeButton("No", null)
                    .show();


        } else if (id == R.id.settings) {
            mChangeFragment.onFragmentChange(new SettingsFragment(), true);
        } else if (id == R.id.copy) {
            if (mSelectionList.size() > 0) {
                for (int i = 0; i < mSelectionList.size(); i++) {
                    Folder folder = mSelectionList.get(i);
                    sourcedir = folder.getFolderPath();
                    folder.setSelected(!folder.isSelected());
                    if (folder.isSelected() == false) {
                        mSelectionList.remove(folder);
                        Log.d("mSelectionList", String.valueOf(mSelectionList.size()));

                    }
                    Log.d("sourcedirec", sourcedir);
                }
            }

            /////////////
            mSelectionList = new ArrayList();
        } else if (id == R.id.paste) {
            Log.d("destinationdirec", destinationdirectory);
            copyFileOrDirectory(sourcedir, destinationdirectory);
        }
        return true;
    }

    public static void copyFileOrDirectory(String srcDir, String dstDir) {
        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }


    public static boolean deleteRecursive(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            Log.v("file size ", String.valueOf(files.length));
            if (files == null) {
                return true;
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

    private void getAllFolders() {
        mAllMediaFolderList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
            return;
        } else {

            File ROOT_DIR = Environment.getExternalStorageDirectory();
            sortDirectory(ROOT_DIR);

            mAllMediaFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder");
            Log.d("mAllMediaFolderList", String.valueOf(mAllMediaFolderList.size()));
            if (mAllMediaFolderList.size() != 0) {

                mUniqFolderList = new ArrayList<>();
                mUniqFolderList = getAllUniqueFolders(mAllMediaFolderList);
                Log.d("mUniqFolderList", String.valueOf(mUniqFolderList.size()));

                FolderAdapter mFolderAdapter = new FolderAdapter(FolderViewFragment.this, mUniqFolderList);
                mFolderRecyclerView.setAdapter(mFolderAdapter);
            } else {
               /* File ROOT_DIR = Environment.getExternalStorageDirectory();
                sortDirectory(ROOT_DIR);
                mAllMediaFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder");
                if (mAllMediaFolderList.size() != 0) {

                    mUniqFolderList = new ArrayList<>();
                    mUniqFolderList = getAllUniqueFolders(mAllMediaFolderList);
                    Log.d("mUniqFolderList", String.valueOf(mUniqFolderList.size()));

                    FolderAdapter mFolderAdapter = new FolderAdapter(FolderViewFragment.this, mUniqFolderList);
                    mFolderRecyclerView.setAdapter(mFolderAdapter);
                }*/

            }

           /* mUniqFolderList = new ArrayList<>();
            mUniqFolderList = getAllUniqueFolders(mAllMediaFolderList);
            Log.d("mUniqFolderList", String.valueOf(mUniqFolderList.size()));
            Log.d("ROOT_DIR", String.valueOf(ROOT_DIR));
            FolderAdapter mFolderAdapter = new FolderAdapter(FolderViewFragment.this, mUniqFolderList);
            mFolderRecyclerView.setAdapter(mFolderAdapter);*/
        }
    }


    void sortDirectory(File mfile) {
        ArrayList<File> mDirectoryList = new ArrayList<>();
        String name = null;
        File[] mFileLists = mfile.listFiles();
        try {
            for (int i = 0; i < mFileLists.length; i++) {
                if (mFileLists[i].isDirectory()) {

                    Log.d("file length ", String.valueOf(mFileLists.length));
                    mDirectoryList.add(mFileLists[i]);
                    if (mFileLists[i].getParent().contains("/storage/emulated/0/Download/New folder2/")) {
                        Log.d("new folder getname ", new File(mFileLists[i].getParent()).getName());
                        Log.d("new folder parent ", (mFileLists[i].getAbsolutePath()));
                        Log.d("new folder absolute ", mFileLists[i].getAbsolutePath());
                        Folder newfolder = new Folder();
                        newfolder.setFolderPath(mFileLists[i].getAbsolutePath());
                        newfolder.setFolderName(new File(mFileLists[i].getParent()).getName());
                        newfolder.setImagePath(mFileLists[i].getAbsolutePath());

                        //    mAllMediaFolderList.add(newfolder);
                        File newfile = new File(mFileLists[i].getAbsolutePath());
                        if (newfile.isDirectory()) {

                            File[] mNewFolderContents = newfile.listFiles();
                            Log.d("mNewFolderContents ", String.valueOf(mNewFolderContents.length));
                            for (int j = 0; j < mNewFolderContents.length; j++) {
                                Log.d("absolute ", mNewFolderContents[j].getAbsolutePath());
                                newfolder.setImagePath(mNewFolderContents[j].getAbsolutePath());
                                Log.d("new folder absolute ", mFileLists[i].getAbsolutePath());
                                Log.d("new folder parent ", (mFileLists[i].getParent()));

                                ArrayList<Folder> mFolderList1 = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folder_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getAbsolutePath()) + " and image_path = " + DatabaseUtils.sqlEscapeString(mNewFolderContents[j].getAbsolutePath()));

                                if (mFolderList1.size() == 0) {
                                    newfolder.save();


                                }

                            }
                        }


                    }
                } else {
                    if (isImageFile(mFileLists[i].getAbsolutePath())) {
                        mImageFileList.add(mFileLists[i]);
                        Folder mFolder = new Folder();
                        mFolder.setFolderPath(mFileLists[i].getParent());
                        mFolder.setFolderName(new File(mFileLists[i].getParent()).getName());
                        mFolder.setImagePath(mFileLists[i].getAbsolutePath());
                        Log.v(" filename is ", mFileLists[i].getPath());

                        /*ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folderPath = " + mFileLists[i].getParent() + " and imagePath = " + mFileLists[i].getAbsolutePath());*/

                        ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folder_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getParent()) + " and image_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getAbsolutePath()));


                        if (mFolderList.size() == 0) {
                            mFolder.save();
                        } else {
                            Log.v("mFolderList", String.valueOf(mFolderList.size()));
                        }
                        //    mAllMediaFolderList.add(mFolder);
                    } else if (isVideoFile(mFileLists[i].getAbsolutePath())) {
                        mImageFileList.add(mFileLists[i]);
                        Folder mFolder = new Folder();
                        mFolder.setFolderPath(mFileLists[i].getParent());
                        mFolder.setFolderName(new File(mFileLists[i].getParent()).getName());
                        mFolder.setImagePath(mFileLists[i].getAbsolutePath());
                        /*ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folderPath = " + mFileLists[i].getParent() + " and imagePath = " + mFileLists[i].getAbsolutePath());*/
                        ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folder_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getParent()) + " and image_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getAbsolutePath()));

                        if (mFolderList.size() == 0) {
                            mFolder.save();
                        }

                        //   mAllMediaFolderList.add(mFolder);
                    } else if (isNoMediaFile(mFileLists[i].getAbsolutePath())) {
                        Folder mFolder = new Folder();
                        mFolder.setFolderPath(mFileLists[i].getParent());
                        mFolder.setFolderName(new File(mFileLists[i].getParent()).getName());
                        mFolder.setImagePath(mFileLists[i].getAbsolutePath());

                        try {
                            /*ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folderPath = " + mFileLists[i].getParent() + " and imagePath = " + mFileLists[i].getAbsolutePath());*/

                            ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folder_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getParent()) + " and image_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getAbsolutePath()));


                            /*  ArrayList<Folder> mFolderList1 = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folderPath ="+"/storage/emulated/0/Download"+ " and imagePath = "+"/storage/emulated/0/Download/.nomedia");
                             */


                            if (mFolderList.size() == 0) {
                                mFolder.save();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        //    mAllMediaFolderList.add(mFolder);
                        Log.v(" nomedia files are: ", mFileLists[i].getAbsolutePath());
                    } else {
                        Log.v(" isnomedia filename? ", mFileLists[i].getAbsolutePath());
                    }
                }
            }
            for (int j = 0; j < mDirectoryList.size(); j++) {
                Log.v(" else filename is ", mDirectoryList.get(j).getAbsolutePath());
                sortDirectory(mDirectoryList.get(j));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.

                File ROOT_DIR = Environment.getExternalStorageDirectory();
                sortDirectory(ROOT_DIR);

                mAllMediaFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder");
                if (mAllMediaFolderList.size() != 0) {

                    mUniqFolderList = new ArrayList<>();
                    mUniqFolderList = getAllUniqueFolders(mAllMediaFolderList);
                    Log.d("mUniqFolderList", String.valueOf(mUniqFolderList.size()));

                    FolderAdapter mFolderAdapter = new FolderAdapter(FolderViewFragment.this, mUniqFolderList);
                    mFolderRecyclerView.setAdapter(mFolderAdapter);
                } else {
                   /* File ROOT_DIR = Environment.getExternalStorageDirectory();
                    sortDirectory(ROOT_DIR);
                    mAllMediaFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder");
                    if (mAllMediaFolderList.size() != 0) {

                        mUniqFolderList = new ArrayList<>();
                        mUniqFolderList = getAllUniqueFolders(mAllMediaFolderList);
                        Log.d("mUniqFolderList", String.valueOf(mUniqFolderList.size()));

                        FolderAdapter mFolderAdapter = new FolderAdapter(FolderViewFragment.this, mUniqFolderList);
                        mFolderRecyclerView.setAdapter(mFolderAdapter);
                    }*/
                }


            } else {
                // User refused to grant permission.
            }
        }
    }

    static int getMediaCount(File mfile) {
        int count = 0;

        File[] mFileLists = mfile.listFiles();
        try {
            for (int i = 0; i < mFileLists.length; i++) {
                if (mFileLists[i].isDirectory()) {

                } else {
                    if (isImageFile(mFileLists[i].getAbsolutePath())) {
                        count = count + 1;
                    } else if (isVideoFile(mFileLists[i].getAbsolutePath())) {
                        count = count + 1;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;

    }

    @Override
    public void onResume() {
        super.onResume();
        com.zinedroid.gallerynew.activity.HomeActivity.mBaseFragment = this;
        initComponents();
    }

    private static ArrayList<Folder> getAllUniqueFolders(ArrayList<Folder> listOfAllImages) {
        Log.d("mShowNomedia", mShowNomedia);
        Log.d("mShowHIddenMedia", mShowHIddenMedia);
        ArrayList<Folder> listOfuniqueImages = new ArrayList<Folder>();
        List<String> enemyIds = new ArrayList<String>();
        for (Folder folder : listOfAllImages) {
            if (!enemyIds.contains(folder.getFolderPath())) {
                enemyIds.add(folder.getFolderPath());
                folder.setFolderName(folder.getFolderName() + " (" + getMediaCount(new File(folder.getFolderPath())) + ")");
                folder.setImagePath(folder.getImagePath());
                folder.setSelected(false);
                Log.d("filetype", folder.getImagePath());
                if (isImageFile(folder.getImagePath())) {
                    folder.setFiletype("image");
                } else if (isVideoFile(folder.getImagePath())) {
                    folder.setFiletype("video");
                } else {
                    folder.setFiletype("other");
                }
                if ((mShowHIddenMedia.equalsIgnoreCase("true")) && (mShowNomedia.equalsIgnoreCase("true"))) {
                    listOfuniqueImages.add(folder);
                } else if ((mShowHIddenMedia.equalsIgnoreCase("false")) && (mShowNomedia.equalsIgnoreCase("false"))) {
                    if ((!folder.getFolderName().startsWith(".")) && (!folder.getImagePath().contains(".nomedia"))) {
                        listOfuniqueImages.add(folder);
                    }
                } else if ((mShowHIddenMedia.equalsIgnoreCase("DEFAULT")) && (mShowNomedia.equalsIgnoreCase("false"))) {
                    if ((!folder.getFolderName().startsWith(".")) && (!folder.getImagePath().contains(".nomedia"))) {
                        listOfuniqueImages.add(folder);
                    }
                } else if ((mShowHIddenMedia.equalsIgnoreCase("false")) && (mShowNomedia.equalsIgnoreCase("DEFAULT"))) {
                    if ((!folder.getFolderName().startsWith(".")) && (!folder.getImagePath().contains(".nomedia"))) {
                        listOfuniqueImages.add(folder);
                    }
                } else if ((mShowHIddenMedia.equalsIgnoreCase("DEFAULT")) && (mShowNomedia.equalsIgnoreCase("DEFAULT"))) {
                    if ((!folder.getFolderName().startsWith(".")) && (!folder.getImagePath().contains(".nomedia"))) {
                        listOfuniqueImages.add(folder);
                    }
                } else if (mShowHIddenMedia.equalsIgnoreCase("true")) {

                    if (folder.getFolderName().startsWith(".")) {
                        listOfuniqueImages.add(folder);
                    } else if (!folder.getImagePath().contains(".nomedia")) {
                        listOfuniqueImages.add(folder);
                    }
                } else if (mShowNomedia.equalsIgnoreCase("true")) {
                    if (!folder.getFolderName().startsWith(".")) {
                        listOfuniqueImages.add(folder);
                    }

                }
            }
        }

        return listOfuniqueImages;
    }

    @Override
    public void updatePage(Folder folder, int position, String filetype) {
        Constants.mclickedFolder = folder;
        try {
            if ((mSelectionList != null) && (mSelectionList.size() != 0)) {
                if (mSelectionList.size() > 0) {
                    //  mToolBar.setVisibility(View.VISIBLE);
                    folder.setSelected(!folder.isSelected());
                    /////////////
                    if (folder.isSelected() == false) {
                        mSelectionList.remove(folder);
                        Log.d("mSelectionList", String.valueOf(mSelectionList.size()));

                    } else {
                        if (Constants.mFolder.isSelected()) {
                            mSelectionList.add(folder);
                            Log.d("mSelectionList", String.valueOf(mSelectionList.size()));
                        } else {
                            mSelectionList.remove(folder);
                            Log.d("mSelectionList", String.valueOf(mSelectionList.size()));
                        }

                    }
                    /////////
                    /*if (Constants.mFolder.isSelected()) {
                        mSelectionList.add(folder);
                        Log.d("mSelectionList", String.valueOf(mSelectionList.size()));
                    } else {
                        mSelectionList.remove(folder);
                        Log.d("mSelectionList", String.valueOf(mSelectionList.size()));
                    }*/

                    mUniqFolderList.set(position, folder);
                    mFolderAdapter = new FolderAdapter(FolderViewFragment.this, mUniqFolderList);
                    mFolderRecyclerView.setAdapter(mFolderAdapter);
                } else {
                    //    mToolBar.setVisibility(View.GONE);
                }
            } else {
                destinationdirectory = folder.getFolderPath();
                BaseFragment fragment = new GalleryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("FolderPosition", String.valueOf(position));
                bundle.putString("ParentFolderName", folder.getFolderName());
                bundle.putString("ParentFolderPath", folder.getFolderPath());
                Log.d("createdfolder", folder.getFolderPath());

                fragment.setArguments(bundle);
                mChangeFragment.onFragmentChange(fragment, true);
            }
        } catch (Exception e) {
            BaseFragment fragment = new GalleryFragment();
            Bundle bundle = new Bundle();
            bundle.putString("ParentFolderName", folder.getFolderName());
            bundle.putString("ParentFolderPath", folder.getFolderPath());
            fragment.setArguments(bundle);
            mChangeFragment.onFragmentChange(fragment, true);
            e.printStackTrace();
        }
    }

    @Override
    public void onLongPress(Folder folder, int position) {
        Constants.mFolder = folder;
        folder.setSelected(true);
        if (mSelectionList == null) {
            mSelectionList = new ArrayList<>();
        }
        Constants.mFolder = folder;
        mSelectionList.add(folder);
        Log.d("mSelectionList", String.valueOf(mSelectionList.size()));
        //   mToolBar.setVisibility(View.VISIBLE);
        mUniqFolderList.set(position, folder);
        mFolderAdapter = new FolderAdapter(FolderViewFragment.this, mUniqFolderList);
        mFolderRecyclerView.setAdapter(mFolderAdapter);
    }

    @Override
    public void onAddFolder(int position) {

        String newfoldername = "New Folder" + i;
        i = i + 1;
        File newfile = createFolder(newfoldername);

        try {
            File[] contents = newfile.listFiles();
// the directory file is not really a directory..
            if (contents != null) {
                Folder newfolder = new Folder();
                newfolder.setFolderPath(newfile.getAbsolutePath());
                newfolder.setFolderName(newfoldername);
                newfolder.setImagePath(newfile.getAbsolutePath());

                newfolder.setSelected(false);
                Log.d("new folder path", newfile.getParent());
                Log.d("new folder name", new File(newfile.getParent()).getName());
                Log.d("new image path", newfile.getAbsolutePath());

                newfolder.setFiletype("other");
                mUniqFolderList.add(position, newfolder);


                ArrayList<Folder> mFolderList1 = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folder_path = " + DatabaseUtils.sqlEscapeString(newfile.getAbsolutePath()) + " and image_path = " + DatabaseUtils.sqlEscapeString(newfile.getAbsolutePath()));


                if (mFolderList1.size() == 0) {
                    newfolder.save();
                }


                mFolderAdapter = new FolderAdapter(FolderViewFragment.this, mUniqFolderList);
                mFolderRecyclerView.setAdapter(mFolderAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File createFolder(String fname) {
        String myfolder = Environment.getExternalStorageDirectory() + "/" + "Download" + "/" + "New folder2" + "/" + fname + "/" + fname;
        File f = new File(myfolder);
        if (!f.exists())
            if (!f.mkdirs()) {
                Toast.makeText(getActivity(), myfolder + " can't be created.", Toast.LENGTH_SHORT).show();
                Log.d("myfolder", "cant create");
            } else {
                Toast.makeText(getActivity(), myfolder + " can be created.", Toast.LENGTH_SHORT).show();
                Log.d("myfolder", "created");
                return f;
            }

        else {
            Toast.makeText(getActivity(), myfolder + " already exits.", Toast.LENGTH_SHORT).show();
            Log.d("myfolder", "exist");
            return null;
        }

        return f;
    }

}
