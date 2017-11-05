package org.jeromegout.simplycloud.history;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import org.jeromegout.simplycloud.EmptyRecyclerView;
import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.activities.BaseActivity;
import org.jeromegout.simplycloud.selection.SelectionActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        EmptyRecyclerView recyclerView = (EmptyRecyclerView)findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        View emptyView = findViewById(R.id.noHistoriesView);
        recyclerView.setEmptyView(emptyView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new UploadAdapter(this));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addUploadButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUpload();
            }
        });
        //- uploadadpter listens model changes so we can restore the model now.
        HistoryModel.instance.restoreHistories(this);
        setToolbarTitle("All uploads");
    }

    private void createNewUpload() {
        Intent intent = new Intent(this, SelectionActivity.class);
        startActivity(intent);
    }

    @Override
	protected int getLayoutResource() {
		return R.layout.activity_history;
	}

}
