package com.proGrad.temporaryjobapp.WorkerActivities;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.proGrad.temporaryjobapp.R;
import com.proGrad.temporaryjobapp.TempJobsRestClientUsage;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
public class WorkerJobInfo extends AppCompatActivity {
    private TextView mJobNameTV;
    private TextView mJobCategoryTV;
    private TextView mJobLocationTV;
    private TextView mJobDeadLineTV;
    private TextView mJobSalaryTV;
    private TextView mJobDescriptionTV;
    private Button contactOwnerButton,email;
    private String mPhone = "";
    private String mEmail = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_job_info);
        mJobNameTV = (TextView) findViewById(R.id.job_name_user_result_et);
        mJobCategoryTV = (TextView) findViewById(R.id.job_cat_user_result_et);
        mJobLocationTV = (TextView) findViewById(R.id.job_location_user_result_et);
        mJobDeadLineTV = (TextView) findViewById(R.id.job_deadline_user_result_et);
        mJobSalaryTV = (TextView) findViewById(R.id.job_salary_user_result_et);
        mJobDescriptionTV = (TextView) findViewById(R.id.job_desc_user_result_et);
        contactOwnerButton = findViewById(R.id.user_contact_admin_bt);
        email = findViewById(R.id.user_contact_email_admin_bt);
        contactOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallBtnClick();
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onemailClick();
            }
        });

        RequestParams requestParams = new RequestParams();
        requestParams.put("job_id", getIntent().getIntExtra("job_id", 0));

        TempJobsRestClientUsage.post("getJobDetails.php", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String res = new String(responseBody, "UTF-8");

                    JSONObject resObj = new JSONObject(res);

                    if (resObj.getBoolean("success")) {
                        mJobNameTV.setText(resObj.getString("job_name"));
                        mJobSalaryTV.setText(resObj.getString("job_salary"));
                        mJobDescriptionTV.setText(resObj.getString("job_description"));
                        mJobDeadLineTV.setText(resObj.getString("job_deadline"));
                        mJobCategoryTV.setText(resObj.getString("category_name"));

                        mPhone = resObj.getString("phone");
                        mEmail = resObj.getString("email");
                        mJobLocationTV.setText(resObj.getString("job_location"));
                        String loc = resObj.getString("job_location");
                        List<String> locs = Arrays.asList(loc.split(""));


                        try {
                            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> addresses = gcd.getFromLocation(Double.parseDouble(locs.get(0)),
                                    Double.parseDouble(locs.get(1)), 1);
                            mJobLocationTV.setText(addresses.get(0).getLocality());
                        } catch (Exception ex) {
                            Log.e("error", ex + "");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), resObj.getString("message"),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex + "",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Response Failure!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onCallBtnClick() {
        if (Build.VERSION.SDK_INT < 23) {
            phoneCall();
        } else {

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                phoneCall();
            } else {
                final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};

                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 9);
            }
        }
    }

    private void phoneCall(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+mPhone));
            this.startActivity(callIntent);
        }else{
            Toast.makeText(this, "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }
    private void onemailClick(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" +mEmail));
        startActivity(emailIntent);
    }
    @Override
            protected void attachBaseContext(Context newBase) {
                super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
            }
}