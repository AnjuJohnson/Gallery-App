package com.zinedroid.gallerynew.common;


import com.zinedroid.gallerynew.base.BaseFragment;
import com.zinedroid.gallerynew.models.Folder;

/**
 * Created by Cecil Paul on 9/10/18.
 */
public class Functions {
    public interface ChangeFragment {
        public void onFragmentChange(BaseFragment mBaseFragment, boolean isReplace);


    }
    public interface GoDown {
        public void updatePage(Folder mFolder, int position, String filetype);
        public void onLongPress(Folder folder, int position);
        public void onAddFolder(int position);

    }

    public interface UpdateThumbnamil {
        public void updatethumbnail(int position, String filename);
    }

}
