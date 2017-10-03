package org.jeromegout.simplycloud.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jeromegout.simplycloud.R;

/**
 * Created by jgout on 03/10/2017.
 */

public class PhotoFragment extends Fragment {

	public PhotoFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_photo, container, false);
	}
}