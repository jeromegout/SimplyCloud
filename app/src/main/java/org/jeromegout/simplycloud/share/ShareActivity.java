package org.jeromegout.simplycloud.share;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
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
import org.jeromegout.simplycloud.history.UploadItem;
import org.jeromegout.simplycloud.send.FileSendAdapter;


public class ShareActivity extends BaseActivity {

    private UploadItem item;
    private MenuItem closeMenu;
    private TextView titleView;
    private TextView dateView;
    private TextView sizeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        item = getIntent().getExtras().getParcelable("uploadItem");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        FileSendAdapter fileAdapter = new FileSendAdapter(item.getContent(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(fileAdapter);
        titleView = (TextView) findViewById(R.id.uploadTitle);
        titleView.setText(item.getTitle());
        dateView = (TextView) findViewById(R.id.uploadDate);
        dateView.setText(item.getHumanReadableDateTime());
        sizeView = (TextView) findViewById(R.id.uploadSize);
        sizeView.setText(item.getHumanReadableSize());
        FloatingActionButton shareFab = (FloatingActionButton) findViewById(R.id.shareButton);
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

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
        closeMenu = menu.findItem(R.id.close_share_activity);
        closeMenu.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close_share_activity:
                Intent intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
