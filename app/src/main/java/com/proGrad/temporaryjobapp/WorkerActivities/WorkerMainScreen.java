package com.proGrad.temporaryjobapp.WorkerActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.proGrad.temporaryjobapp.LoginScreen;
import com.proGrad.temporaryjobapp.R;
import com.proGrad.temporaryjobapp.SessionManager;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WorkerMainScreen extends AppCompatActivity {
    private Button profileBtn , SearchJobButton , ShowMapButton , RateJobsButtton, mExitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_main_screen);

        profileBtn        = findViewById(R.id.user_info_main_bt);
        SearchJobButton   = findViewById(R.id.search_job_main_bt);
        RateJobsButtton = findViewById(R.id.rate_jobs_main_bt);
        ShowMapButton     = findViewById(R.id.show_map_main_bt);
        mExitBtn          = findViewById(R.id.exist_main_bt);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkerMainScreen.this , WorkerProfileInfoScreen.class);
                startActivity(intent);
            }
        });
        SearchJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkerMainScreen.this , SearchJobsScreen.class);
                startActivity(intent);
            }
        });
        ShowMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkerMainScreen.this , ShowMapScreen.class);
                startActivity(intent);
            }
        });

        RateJobsButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkerMainScreen.this , JobsDone.class);
                startActivity(intent);
            }
        });

        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager.getInstance(getApplicationContext()).setLoggedIn(false);
                Intent mIntent = new Intent(WorkerMainScreen.this, LoginScreen.class);
                startActivity(mIntent);
            }
        });


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
