package bni.emplus;

import android.os.Environment;

import java.io.File;

/**
 * Created by ZulfahPermataIlliyin on 10/14/2016.
 */
public class Delete {
    
    public void delete() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/test/");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
            dir.delete();
        }
    }
}
