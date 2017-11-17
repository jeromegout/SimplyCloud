package org.jeromegout.simplycloud.send;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import org.jeromegout.simplycloud.selection.fragments.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UploadActivity extends BaseActivity implements ArchiveMaker.OnArchiveCreationListener {

	private File zipFile;
	private List<Uri> filesUri;
    private ProgressBar progressBar;
	private FloatingActionButton sendFab;
	private TextView statusView;
    private EditText titleEdit;
	private HostServices currentHost;

	@Override
	protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.currentHost = HostManager.instance.getCurrentHost();
        String action = getIntent().getAction();
        String type = getIntent().getType();
        String title = null;
        if ((Intent.ACTION_SEND.equals(action) ||
                Intent.ACTION_SEND_MULTIPLE.equals(action)) && type != null) {
            if (type.startsWith("image/") || type.startsWith("video/")) {
                filesUri = new ArrayList<>();
                ArrayList<Uri> list = getIntent().getExtras().getParcelableArrayList(Intent.EXTRA_STREAM);
                for (Uri u: list) {
                    try {
                        filesUri.add(Uri.parse(FileUtil.getPath(this, u)));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (getIntent().getExtras() != null) {
                filesUri = getIntent().getExtras().getParcelableArrayList("selection");
                title = getIntent().getStringExtra("title");
            }
        }
        ImageView logo = findViewById(R.id.logo);
        logo.setImageDrawable(getResources().getDrawable(currentHost.getHostLogoId()));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FileSendAdapter fileAdapter = new FileSendAdapter(filesUri, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(fileAdapter);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax((int) getSelectionSize());
        statusView = findViewById(R.id.statusView);
        titleEdit = findViewById(R.id.uploadTitle);
        if (title != null && title.length() > 0) {
            titleEdit.setText(title);
        }
        sendFab = findViewById(R.id.sendButton);
            sendFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendArchive();
                }
            });
    }

	private long getSelectionSize() {
	    long size = 0;
        for (Uri uri : filesUri) {
            size += FileUtil.getFileSize(uri);
        }
        return size;
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

	private void makeArchive() {
		new ArchiveMaker()
				.files(filesUri)
				.into(getArchiveFile())
				.withListener(this)
				.execute();
	}

    private void sendArchive() {
		if(zipFile == null || !zipFile.exists())  {
			//- file not yet created
			makeArchive();
		}
		if(zipFile != null)  {
//			setEnableFab(sendFab, false);
			//- create the unique id associated to this upload session
			String uploadId = UUID.randomUUID().toString();
			//- create the history entry even if the file is still not uploaded
			UploadItem uploadItem = new UploadItem(filesUri, zipFile.length(), titleEdit.getText().toString(), uploadId);
			HistoryModel.instance.addHistory(uploadItem);
			currentHost.uploadArchive(this, zipFile, uploadId);
			finish();
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
			sendArchive();
		}
	}

//	@Override
//	public void onUploadUpdate(String update) {
//        statusView.setText(update);
//	}
//
//	@Override
//	public void onUploadFinished(UploadLinks info) {
//		if(info != null) {
//		    statusView.setText("Upload fully completed");
//            UploadItem uploadItem = new UploadItem(filesUri, zipFile.length(), info, titleEdit.getText().toString());
//            HistoryModel.instance.addHistory(uploadItem);
//            //- invoke share activity
//            Intent intent = new Intent(this, ShareActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("uploadItem", uploadItem);
//            intent.putExtras(bundle);
//            startActivity(intent);
//
//            Log.d("====== DEBUG DL  ===== ", info.downloadLink);
//			Log.d("====== DEBUG DEL ===== ", info.deleteLink);
//		}
//	}
//
//	@Override
//	public void onArchiveDeleted(boolean deletePerformed) {/* nop*/}

	private File getArchiveFile() {
		File outputDir = getCacheDir();
		String name;
		String title = titleEdit.getText().toString();
		if(title.length() > 0) {
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
