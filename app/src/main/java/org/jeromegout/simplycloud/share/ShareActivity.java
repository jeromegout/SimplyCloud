package org.jeromegout.simplycloud.share;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.activities.BaseActivity;
import org.jeromegout.simplycloud.history.HistoryActivity;
import org.jeromegout.simplycloud.history.HistoryModel;
import org.jeromegout.simplycloud.history.UploadItem;
import org.jeromegout.simplycloud.hosts.HostServices;
import org.jeromegout.simplycloud.hosts.free.FreeHost;
import org.jeromegout.simplycloud.send.FileSendAdapter;
import org.jeromegout.simplycloud.send.UploadInfo;


public class ShareActivity extends BaseActivity implements HostServices.OnListener {

    private UploadItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getExtras() != null) {
            item = getIntent().getExtras().getParcelable("uploadItem");
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            FileSendAdapter fileAdapter = new FileSendAdapter(item.getContent(), this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(fileAdapter);
            TextView titleView = (TextView) findViewById(R.id.uploadTitle);
            titleView.setText(item.getHumanReadableTitle());
            TextView dateView = (TextView) findViewById(R.id.uploadDate);
            dateView.setText(item.getHumanReadableDateTime());
            TextView sizeView = (TextView) findViewById(R.id.uploadSize);
            sizeView.setText(item.getHumanReadableSize());
            FloatingActionButton shareFab = (FloatingActionButton) findViewById(R.id.shareButton);
            shareFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    share();
                }
            });
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_share;
    }

    private void share() {
        if(item != null) {
            String shareBody = "Here is the link to download the files I want to share with you\n\n";
            shareBody += item.getInfo().downloadLink;
            shareBody += "\n\n(sent with" + getResources().getString(R.string.app_name) + ")";
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Link");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }
    }

    //- Menu stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        menu.findItem(R.id.delete_upload);
        menu.findItem(R.id.close_share_activity);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close_share_activity:
                Intent intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                return true;
            case R.id.delete_upload:
                deleteUploadItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteUploadItem() {
        new AlertDialog.Builder(this)
                .setTitle("Delete uploaded archive")
                .setMessage("Are you sure you want to delete this archive from history and server?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _deleteUploadItem();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void _deleteUploadItem() {
        //- send the delete
        FreeHost free = new FreeHost();
        free.deleteArchive(this, item.getInfo().deleteLink, this);
    }

    @Override
    public void onUploadUpdate(String update) {}

    @Override
    public void onUploadFinished(UploadInfo info) {}

    @Override
    public void onArchiveDeleted(boolean deletePerformed) {
        if(deletePerformed) {
            //- successfully deleted on server, delete in model too
            HistoryModel.instance.removeHistory(item);
            TextView titleView = (TextView) findViewById(R.id.uploadTitle);
            Snackbar.make(titleView, "'"+item.getHumanReadableTitle()+ "' successfully removed on server and history", Snackbar.LENGTH_SHORT);
            finish();
        }
    }
}
