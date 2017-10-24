package org.jeromegout.simplycloud.selection.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;


public class SelectionBaseFragment extends Fragment {

	public interface Callbacks {

		void onSelectionUpdated(int count, long totalSize);

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
