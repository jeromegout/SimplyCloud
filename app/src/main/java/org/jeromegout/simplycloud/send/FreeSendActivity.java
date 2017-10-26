package org.jeromegout.simplycloud.send;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.activities.BaseActivity;

import java.io.File;
import java.util.List;

public class FreeSendActivity extends BaseActivity implements ArchiveMaker.OnArchiveCreatedListener,
        FreeTransfer.OnArchiveSentListener {

    private FloatingActionButton sendFab;
    private FloatingActionButton shareFab;
	private File zipFile;
	private ProgressBar progressBar;
	private List<Uri> files;
    private UploadInfo uploadedInfo;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getExtras() != null) {
			files = getIntent().getExtras().getParcelableArrayList("selection");
			RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
			FileSendAdapter fileAdapter = new FileSendAdapter(files, this);
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
			recyclerView.setAdapter(fileAdapter);
			progressBar = (ProgressBar) findViewById(R.id.progressBar);
			progressBar.setMax((int) getIntent().getLongExtra("size", 0));
			sendFab = (FloatingActionButton) findViewById(R.id.sendButton);
			shareFab = (FloatingActionButton) findViewById(R.id.shareButton);
            setEnableFab(sendFab,false);
			makeArchive();
			sendFab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sendArchive();
				}
			});
			shareFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    instantShare();
                }
            });
		}
	}

    @Override
	protected int getLayoutResource() {
		return R.layout.activity_send;
	}

	private void makeArchive() {
		new ArchiveMaker(this, files, progressBar, this).execute();
	}

	private void sendArchive() {
		if(zipFile != null)  {
			setEnableFab(sendFab,false);
			new FreeTransfer(this, zipFile, progressBar, this).execute();
		}
	}

    private void instantShare() {
        String shareBody = "Here is the link to download the files I want to share with you\n\n";
        shareBody += uploadedInfo.downloadLink;
        shareBody += "\n\n(shared with "+getResources().getString(R.string.app_name)+")";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Link");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share using..."));
    }

	private void setEnableFab(FloatingActionButton fab, boolean enabled) {
		fab.setVisibility(enabled ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onArchiveCreated(File archive) {
		if(archive != null) {
			setEnableFab(sendFab, true);
			zipFile = archive;
		}
	}

    @Override
    public void onArchiveSent(UploadInfo info) {
        if (! info.hasError()) {
            uploadedInfo = info;
            //- archive file has been uploaded to the server
            setEnableFab(sendFab, false);
            setEnableFab(shareFab, true);
            //TODO add close button to the toolbar to close the page and go back to history
        }
    }
}
