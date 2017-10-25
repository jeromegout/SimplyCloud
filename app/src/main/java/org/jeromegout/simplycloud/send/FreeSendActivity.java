package org.jeromegout.simplycloud.send;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.activities.BaseActivity;

import java.io.File;
import java.util.List;

public class FreeSendActivity extends BaseActivity implements ArchiveMaker.OnArchiveCreatedListener {

	private FloatingActionButton fab;
	private File zipFile;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getExtras() != null) {
			List<Uri> filesUri = getIntent().getExtras().getParcelableArrayList("selection");
			RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
			FileSendAdapter fileAdapter = new FileSendAdapter(filesUri);
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
			recyclerView.setAdapter(fileAdapter);
			progressBar = (ProgressBar) findViewById(R.id.progressBar);
			progressBar.setMax((int) getIntent().getLongExtra("size", 0));
			fab = (FloatingActionButton) findViewById(R.id.sendButton);
			fab.setEnabled(false);
			makeArchive(filesUri);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sendArchive(zipFile);
				}
			});
		}
	}


	private void makeArchive(List<Uri> filesUri) {
		new ArchiveMaker(this, filesUri, progressBar, this).execute();
	}

	private void sendArchive(File zip) {
		if(zip != null)  {
			Log.d("DEBUG ======", zip.getAbsolutePath());
		}
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_send;
	}

	@Override
	public void onArchiveCreated(File archive) {
		if(archive != null) {
			fab.setEnabled(true);
			zipFile = archive;
		}
	}
}
