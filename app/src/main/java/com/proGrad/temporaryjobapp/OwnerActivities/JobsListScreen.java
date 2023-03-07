package com.proGrad.temporaryjobapp.OwnerActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.proGrad.temporaryjobapp.R;
import com.proGrad.temporaryjobapp.SessionManager;
import com.proGrad.temporaryjobapp.TempJobsRestClientUsage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class JobsListScreen extends AppCompatActivity {

    private ListView lv;

    private ArrayList<HashMap<String, Integer>> jobs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_list_screen);

        lv = (ListView) findViewById(R.id.Job_admin_show_lv);

        RequestParams params = new RequestParams();
        params.put("owner_id", SessionManager.getInstance(getApplicationContext()).getUserID() + "");

        TempJobsRestClientUsage.post("getMyJobs.php", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try
                {
                    String res = new String(responseBody, "UTF-8");

                    JSONObject resObj = new JSONObject(res);

                    if (resObj.getBoolean("success"))
                    {
                        JSONArray jsonArray = resObj.getJSONArray("message");

                        String[] js = new String[jsonArray.length()];

                        for (int i = 0, len = jsonArray.length(); i < len; i++)
                        {
                            JSONObject job = jsonArray.getJSONObject(i);

                            HashMap<String, Integer> hashMap = new HashMap<>();
                            hashMap.put(job.getString("job_name"),
                                    job.getInt("job_id"));

                            js[i] = job.getString("job_name");

                            jobs.add(hashMap);
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                com.proGrad.temporaryjobapp.OwnerActivities.JobsListScreen.this,
                                android.R.layout.simple_list_item_1,
                                js );

                        lv.setAdapter(arrayAdapter);
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
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error)
            {
                Toast.makeText(getApplicationContext(),"Response Failure!",
                        Toast.LENGTH_LONG).show();
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(com.proGrad.temporaryjobapp.OwnerActivities.JobsListScreen.this , ModifyJobScreen.class);
                intent.putExtra("job_id", checkJob(lv.getItemAtPosition(i).toString()));
                intent.putExtra("is_owner", true);
                startActivityForResult(intent, 0);

            }
        });
    }
    private int checkJob(String mJob)
    {
        int res = -1;

        for (int i = 0, len = jobs.size(); i < len; i++)
        {
            HashMap<String, Integer> c = jobs.get(i);

            if (c.containsKey(mJob))
            {
                res = c.get(mJob);
                break;
            }
        }

        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {
                finish();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}