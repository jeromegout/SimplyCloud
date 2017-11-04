package org.jeromegout.simplycloud.send;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.activities.BaseActivity;
import org.jeromegout.simplycloud.history.HistoryActivity;
import org.jeromegout.simplycloud.history.HistoryModel;
import org.jeromegout.simplycloud.history.UploadItem;
import org.jeromegout.simplycloud.selection.fragments.FileUtil;
import org.jeromegout.simplycloud.share.ShareActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FreeSendActivity extends BaseActivity implements ArchiveMaker.OnArchiveCreatedListener,
		FreeTransfer.OnArchiveSentListener {

	private File zipFile;
	private List<Uri> filesUri;
    private ProgressBar progressBar;
	private FloatingActionButton sendFab;
	private TextView statusView;
    private EditText titleEdit;

    @Override
	protected void onStart() {
		super.onStart();
		if (getIntent().getExtras() != null) {
			filesUri = getIntent().getExtras().getParcelableArrayList("selection");
			RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
			FileSendAdapter fileAdapter = new FileSendAdapter(filesUri, this);
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(fileAdapter);
			progressBar = (ProgressBar) findViewById(R.id.progressBar);
			progressBar.setMax((int) getSelectionSize());
			statusView = (TextView) findViewById(R.id.statusView);
			titleEdit = (EditText) findViewById(R.id.uploadTitle);
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
		new ArchiveMaker(this, filesUri, titleEdit.getText().toString(), progressBar, statusView, this).execute();
	}

	private void sendArchive() {
		if(zipFile != null)  {
			setEnableFab(sendFab, false);
			new FreeTransfer(zipFile, statusView, this).execute();
		}
	}

	private void setEnableFab(FloatingActionButton fab, boolean enabled) {
		fab.setVisibility(enabled ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onArchiveCreated(File archive) {
		if(archive != null) {
			setEnableFab(sendFab, true);
			zipFile = archive;
			statusView.setText("Packaging files done");
			Log.d("====== DEBUG ZIP  ==== ", archive.getAbsolutePath());
			Log.d("====== DEBUG SIZE ==== ", String.valueOf(archive.length()));
		}
	}

	@Override
	public void onArchiveSent(UploadInfo info) {
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
}
