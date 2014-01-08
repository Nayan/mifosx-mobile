package org.mifosx.mobile.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.mifosx.mobile.R;

/**
 * Created by Anuruddha on 1/8/14.
 */
public class MPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);
    }
}
