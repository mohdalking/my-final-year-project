package com.proGrad.temporaryjobapp.OwnerActivities;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.proGrad.temporaryjobapp.LoginScreen;
import com.proGrad.temporaryjobapp.R;
import com.proGrad.temporaryjobapp.SessionManager;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
public class OwnerMainScreen extends AppCompatActivity {
    private Button profileButton , AddJobButton , ModifyJobButton , ShowRatingButtton, mExitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_main_screen);
        profileButton     = findViewById(R.id.admin_info_main_bt);
        AddJobButton      = findViewById(R.id.add_job_main_bt);
        ModifyJobButton   = findViewById(R.id.modify_job_main_bt);
        ShowRatingButtton = findViewById(R.id.show_rating_main_bt);
        mExitBtn          = findViewById(R.id.exist_main_bt);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OwnerMainScreen.this , OwnerProfileInfoScreen.class);
                startActivity(intent);
            }
        });
        AddJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OwnerMainScreen.this , AddJobScreen.class);
                startActivity(intent);
            }
        });
        ModifyJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OwnerMainScreen.this , JobsListScreen.class);
                startActivity(intent);

            }
        });


        ShowRatingButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OwnerMainScreen.this , RatingsScreen.class);
                startActivity(intent);

            }
        });

        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager.getInstance(getApplicationContext()).setLoggedIn(false);
                Intent mIntent = new Intent(OwnerMainScreen.this, LoginScreen.class);
                startActivity(mIntent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
