package com.qidi.uploadfile;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Button uploadButton;
    private Button retryButton;

    private EditText textFile;
    private EditText textRetry;

    private UploadFileService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        textFile = (EditText) findViewById(R.id.testFile);
        textRetry = (EditText) findViewById(R.id.textRetry);

        uploadButton = (Button) findViewById(R.id.buttonUpload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    service = new UploadFileService(textFile.getText().toString());
                    service.upload();
                } catch (Exception e) {
                    if (service != null) {
                        service.stop();
                    }
//                    Log.e(UploadActivity.class.getName(), e.toString());
                }
            }
        });

        retryButton = (Button) findViewById(R.id.buttonRetry);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String retry = textRetry.getText().toString();
                if (retry == null || "".equals(retry)) {
                    return;
                }

                String[] retrys = retry.split("[ ]");
                int length = retrys.length;
                Integer[] indexes = new Integer[length];
                for (int i = 0; i < length; i++) {
                    indexes[i] = Integer.valueOf(retrys[i]);
                }

                try {
                    service = new UploadFileService(textFile.getText().toString());
                    service.retry(indexes);
                } catch (Exception e) {
                    if (service != null) {
                        service.stop();
                    }
//                    Log.e(UploadActivity.class.getName(), e.toString());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.upload, menu);
        return true;
    }
}