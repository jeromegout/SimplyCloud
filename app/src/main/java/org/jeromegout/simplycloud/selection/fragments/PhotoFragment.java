package org.jeromegout.simplycloud.selection.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.selection.adapters.PhotoAdapter;

public class PhotoFragment extends SelectionBaseFragment implements PhotoAdapter.Callbacks, PhotoLoader.Callbacks {

    private RecyclerView recyclerView;
    private View emptyView;
    private GridLayoutManager layoutManager;
    private PhotoAdapter photoAdapter;
    private PhotoLoader photoLoader;


	public PhotoFragment(){
        photoLoader = new PhotoLoader();
        photoAdapter = new PhotoAdapter();
        photoAdapter.setCallbacks(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //- register the activity (parent activity in loader in order to create cursorLoader
        photoLoader.onAttach(getActivity(), this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        photoLoader.onDetach();
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

    private void updateEmptyState() {
        recyclerView.setVisibility(photoAdapter.getItemCount() > 0 ? View.VISIBLE : View.INVISIBLE);
        emptyView.setVisibility(photoAdapter.getItemCount() > 0 ? View.INVISIBLE : View.VISIBLE);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_photo, container, false);
        emptyView = view.findViewById(android.R.id.empty);
        layoutManager = new GridLayoutManager(getContext(), 1);

        initRecyclerView(view);

        if (savedInstanceState != null) {
            updateEmptyState();
        }

        return view;
    }

    private void initRecyclerView(View view) {
        final int spacing = getResources().getDimensionPixelSize(R.dimen.selection_item_offset);
        recyclerView = (RecyclerView) view.findViewById(R.id.photo_recycler);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photoAdapter);
        recyclerView.setClipToPadding(false);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(spacing, spacing, spacing, spacing);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                int size = getResources().getDimensionPixelSize(R.dimen.selection_item_size);
                int width = recyclerView.getMeasuredWidth();
                int columnCount = width / (size + spacing);
                layoutManager.setSpanCount(columnCount);
                return false;
            }
        });
    }

    //- Notifications from the photoadapter

    @Override
    public void onBucketClick(long bucketId, String label) {
        photoLoader.loadPhotoBucket(bucketId);
    }

    @Override
    public void onMediaClick(View imageView, View checkView, long bucketId, int position) {

    }

    @Override
    public void onSelectionUpdated(int count) {

    }

    @Override
    public void onBucketLoadFinished(Cursor data) {
        photoAdapter.setData(data);
    }

    @Override
    public void onMediaLoadFinished(Cursor data) {

    }
}