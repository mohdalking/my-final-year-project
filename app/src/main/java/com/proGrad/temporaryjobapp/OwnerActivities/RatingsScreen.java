package com.proGrad.temporaryjobapp.OwnerActivities;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
public class RatingsScreen extends AppCompatActivity {
    private ListView lv;
    ArrayList<Map<String, String>> jobs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings_screen);
        lv = (ListView) findViewById(R.id.ratings_admin_lv);
        RequestParams params = new RequestParams();
        params.put("owner_id", SessionManager.getInstance(getApplicationContext()).getUserID() + "");
        TempJobsRestClientUsage.post("getMyJobsRate.php", params, new AsyncHttpResponseHandler()
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
                        for (int i = 0, len = jsonArray.length(); i < len; i++)
                        {
                            JSONObject job = jsonArray.getJSONObject(i);
                            double rate = 0;
                            if (job.getInt("rattings") != 0)
                            {
                                rate = (((1 * job.getInt("oneCount") + (
                                        2 * job.getInt("twoCount") +
                                                3 * job.getInt("threeCount") +
                                                4 * job.getInt("fourCount") +
                                                5 * job.getInt("fiveCount")
                                ))) / job.getInt("rattings"));
                            }



                            Map<String, String> m = new HashMap<>();
                            m.put("name", job.getString("job_name"));
                            m.put("rate", rate + "/5");

                            jobs.add(m);
                        }

                        String[] from = { "name", "rate" };
                        int[] to = { R.id.job_title_tv, R.id.job_rating_tv };

                        SimpleAdapter adapter = new SimpleAdapter(com.proGrad.temporaryjobapp.OwnerActivities.RatingsScreen.this, jobs,
                                R.layout.rate_job_item, from, to);
                        lv.setAdapter(adapter);
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

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}