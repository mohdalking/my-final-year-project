package com.proGrad.temporaryjobapp.WorkerActivities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.proGrad.temporaryjobapp.R;
import com.proGrad.temporaryjobapp.SessionManager;
import com.proGrad.temporaryjobapp.TempJobsRestClientUsage;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RateJobScreen extends AppCompatActivity {
    private Spinner mRateSP;
    private Button rateJobButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_job_screen);
        mRateSP = (Spinner) findViewById(R.id.spinner);
        rateJobButton = findViewById(R.id.rate_done_bt);
        rateJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params = new RequestParams();
                params.put("job_id", getIntent().getIntExtra("job_id", 0));
                params.put("rate", mRateSP.getSelectedItem().toString());
                params.put("worker_id", SessionManager.getInstance(getApplicationContext()).getUserID() + "");
                TempJobsRestClientUsage.post("updateRating.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try
                        {
                            String res = new String(responseBody, "UTF-8");
                            JSONObject resObj = new JSONObject(res);
                            if (resObj.getBoolean("success"))
                            {
                                StyleableToast.makeText(com.proGrad.temporaryjobapp.WorkerActivities.RateJobScreen.this,
                                        " Job has been Rated", Toast.LENGTH_LONG, R.style.MyToast).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), resObj.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(getApplicationContext(),ex + "",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getApplicationContext(),"Response Failure!",
                                Toast.LENGTH_LONG).show();
                    }
                });


            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}