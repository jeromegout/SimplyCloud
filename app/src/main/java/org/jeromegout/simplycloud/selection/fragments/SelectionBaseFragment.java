package org.jeromegout.simplycloud.selection.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;


public class SelectionBaseFragment extends Fragment {

	public interface Callbacks {

		void onBucketClick(String label);

		void onItemClick(@NonNull View view, View checkView, long bucketId, int position);

		void onSelectionUpdated(int count);

	}

	protected Callbacks callbacks;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (!(context instanceof Callbacks)) {
			throw new IllegalArgumentException(context.getClass().getSimpleName() + " must implement " + Callbacks.class.getName());
		}
		callbacks = (Callbacks) context;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		callbacks = null;
	}
}
