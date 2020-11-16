package bni.emplus.Fragment;

/**
 * Created by ZulfahPermataIlliyin on 1/11/2017.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bni.emplus.R;

public class NoInternet extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.nointernet, container, false);

        return v;
    }
}
