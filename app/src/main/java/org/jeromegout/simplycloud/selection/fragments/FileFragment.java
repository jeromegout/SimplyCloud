package org.jeromegout.simplycloud.selection.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.selection.adapters.FileAdapter;
import org.jeromegout.simplycloud.selection.adapters.FileHolder;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileFragment extends Fragment implements View.OnClickListener, FileHolder.OnItemClickListener{

	private TextView directoryName;
	private String currentDirectoryPath;
	private FileAdapter fileAdapter;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_file, container, false);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_file);
		ImageButton prevButton = (ImageButton) view.findViewById(R.id.previous_dir_imagebutton);
		prevButton.setOnClickListener(this);
		directoryName = (TextView) view.findViewById(R.id.current_dir_textview);
		fileAdapter = new FileAdapter(this);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(fileAdapter);
		//- init with root directory
		loadItemsFromDirectory(Environment.getExternalStorageDirectory().getPath());
		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.previous_dir_imagebutton) {
			//- load previous directory
			File parent = new File(currentDirectoryPath).getParentFile();
			if (parent != null && parent.canRead()) {
				loadItemsFromDirectory(parent.getPath());
			}
		}
	}

	private void loadItemsFromDirectory(String dirName) {
		currentDirectoryPath = dirName;
		String currentDir = dirName.substring(dirName.lastIndexOf(File.separator) + 1);
		directoryName.setText(currentDir);

		File[] files = new File(dirName).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.canRead() && (file.isFile() || file.isDirectory());
			}});
		List<FileItem> items = new ArrayList<>();
		for (File f : files) {
			Drawable drawable = FileUtil.getDrawable(getActivity().getApplicationContext(), f);
			items.add(new FileItem(f.getPath(), drawable));
		}
		Collections.sort(items);
		fileAdapter.setItems(items);
	}

	@Override
	public void onItemClick(FileItem item) {
		loadItemsFromDirectory(item.getPath());
	}
}