package org.jeromegout.simplycloud.send;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jeromegout.simplycloud.Logging;
import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.activities.BaseActivity;
import org.jeromegout.simplycloud.history.HistoryModel;
import org.jeromegout.simplycloud.history.UploadItem;
import org.jeromegout.simplycloud.hosts.HostManager;
import org.jeromegout.simplycloud.hosts.HostServices;
import org.jeromegout.simplycloud.hosts.free.FreeTransfer;
import org.jeromegout.simplycloud.selection.fragments.FileUtil;
import org.jeromegout.simplycloud.share.ShareActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UploadActivity extends BaseActivity implements ArchiveMaker.OnArchiveCreationListener,
		HostServices.OnListener {

	private File zipFile;
	private List<Uri> filesUri;
    private ProgressBar progressBar;
	private FloatingActionButton sendFab;
	private TextView statusView;
    private EditText titleEdit;
	private HostServices currentHost;

	@Override
	protected void onStart() {
		super.onStart();

		this.currentHost = HostManager.instance.getCurrentHost();

		if (getIntent().getExtras() != null) {
			filesUri = getIntent().getExtras().getParcelableArrayList("selection");
            String title = getIntent().getStringExtra("title");
			ImageView logo = (ImageView) findViewById(R.id.logo);
			logo.setImageDrawable(getResources().getDrawable(currentHost.getHostLogoId()));
			RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
			FileSendAdapter fileAdapter = new FileSendAdapter(filesUri, this);
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(fileAdapter);
			progressBar = (ProgressBar) findViewById(R.id.progressBar);
			progressBar.setMax((int) getSelectionSize());
			statusView = (TextView) findViewById(R.id.statusView);
			titleEdit = (EditText) findViewById(R.id.uploadTitle);
			if(title != null && title.length() > 0) {
			    titleEdit.setText(title);
            }
			makeArchive(filesUri);
			initFab();
		}
	}

	private long getSelectionSize() {
	    long size = 0;
        for (Uri uri : filesUri) {
            size += FileUtil.getFileSize(uri);
        }
        return size;
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
		new ArchiveMaker()
				.files(filesUri)
				.into(getArchiveFile())
				.withListener(this)
				.execute();
	}

	private void sendArchive() {
		if(zipFile != null)  {
			setEnableFab(sendFab, false);
			currentHost.uploadArchive(this, zipFile, this);
			new FreeTransfer(zipFile, statusView, this).execute();
		}
	}

	private void setEnableFab(FloatingActionButton fab, boolean enabled) {
		fab.setVisibility(enabled ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onArchiveUpdate(int bytesNum) {
		progressBar.incrementProgressBy(bytesNum);
	}

	@Override
	public void onArchiveStepUpdate(String step) {
		statusView.setText(step);
	}

	@Override
	public void onArchiveCreated(File archive) {
		if(archive != null) {
			setEnableFab(sendFab, true);
			zipFile = archive;
			statusView.setText("Packaging files done");
		}
	}

	@Override
	public void onUploadUpdate(String update) {

	}

	@Override
	public void onUploadFinished(UploadInfo info) {
		if(info != null) {
            UploadItem uploadItem = new UploadItem(filesUri, zipFile.length(), info, titleEdit.getText().toString());
            HistoryModel.instance.addHistory(uploadItem);
            //- invoke share activity
            Intent intent = new Intent(this, ShareActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("uploadItem", uploadItem);
            intent.putExtras(bundle);
            startActivity(intent);

            Log.d("====== DEBUG DL  ===== ", info.downloadLink);
			Log.d("====== DEBUG DEL ===== ", info.deleteLink);
		}
	}

	@Override
	public void onArchiveDeleted(boolean deletePerformed) {/* nop*/}


	private File getArchiveFile() {
		File outputDir = getCacheDir();
		String name;
		String title = titleEdit.getText().toString();
		if(title != null && title.length() > 0) {
			name = title;
		} else {
			name = getResources().getString(R.string.app_name);
		}
		name +="-"+currentHost.getHostId()+"-";
		try {
			return File.createTempFile(name, ".zip", outputDir);
		} catch (IOException e) {
			Logging.e("Unable to create archive "+name, e);
			return null;
		}
	}
}