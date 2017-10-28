package org.jeromegout.simplycloud.send;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.activities.BaseActivity;

import java.io.File;
import java.util.List;

public class FreeSendActivity extends BaseActivity implements ArchiveMaker.OnArchiveCreatedListener,
		FreeTransfer.OnArchiveSentListener {

	private File zipFile;
	private UploadInfo info;
	private ProgressBar progressBar;
	private FloatingActionButton sendFab;
	private FloatingActionButton shareFab;
	private TextView statusView;

	@Override
	protected void onStart() {
		super.onStart();
		if (getIntent().getExtras() != null) {
			List<Uri> filesUri = getIntent().getExtras().getParcelableArrayList("selection");
			RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
			FileSendAdapter fileAdapter = new FileSendAdapter(filesUri, this);
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
			recyclerView.setAdapter(fileAdapter);
			progressBar = (ProgressBar) findViewById(R.id.progressBar);
			progressBar.setMax((int) getIntent().getLongExtra("size", 0));
			statusView = (TextView) findViewById(R.id.statusView);
			makeArchive(filesUri);
			initFab();
		}
	}

	private void initFab() {
		sendFab = (FloatingActionButton) findViewById(R.id.sendButton);
		setEnableFab(sendFab, false);
		sendFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendArchive();
			}
		});
		shareFab = (FloatingActionButton) findViewById(R.id.shareButton);
		setEnableFab(shareFab, false);
		shareFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				instantShare();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		deleteArchive();
	}

	private void deleteArchive() {
		if(zipFile != null && zipFile.exists()) {
			zipFile.delete();
		}
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_send;
	}

	private void makeArchive(List<Uri> filesUri) {
		new ArchiveMaker(this, filesUri, progressBar, statusView, this).execute();
	}

	private void sendArchive() {
		if(zipFile != null)  {
			setEnableFab(sendFab, false);
			new FreeTransfer(zipFile, statusView, this).execute();
		}
	}

	private void instantShare() {
		String shareBody = "Here is the link to download the files I want to share with you\n\n";
		shareBody += info.downloadLink;
		shareBody += "\n\n(sent with"+getResources().getString(R.string.app_name)+")";
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Link");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent, "Share using"));
	}

	private void setEnableFab(FloatingActionButton fab, boolean enabled) {
		fab.setVisibility(enabled ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onArchiveCreated(File archive) {
		if(archive != null) {
			setEnableFab(sendFab, true);
			zipFile = archive;
			statusView.setText("Packaging archive done");
			Log.d("====== DEBUG ZIP  ==== ", archive.getAbsolutePath());
			Log.d("====== DEBUG SIZE ==== ", String.valueOf(archive.length()));
		}
	}

	@Override
	public void onArchiveSent(UploadInfo info) {
		if(info != null) {
			this.info = info;
			setEnableFab(sendFab, false);
			setEnableFab(shareFab, true);
			Log.d("====== DEBUG DL  ===== ", info.downloadLink);
			Log.d("====== DEBUG DEL ===== ", info.deleteLink);
		}
	}
}
