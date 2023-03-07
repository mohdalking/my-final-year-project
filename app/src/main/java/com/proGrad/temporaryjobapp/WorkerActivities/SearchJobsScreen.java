package com.proGrad.temporaryjobapp.WorkerActivities;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.proGrad.temporaryjobapp.Map;
import com.proGrad.temporaryjobapp.R;
import com.proGrad.temporaryjobapp.TempJobsRestClientUsage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchJobsScreen extends AppCompatActivity {
    private EditText mJobSalaryET;
    private Spinner mCatSP;
    private TextView mJobLocationTV;
    private Button SearchByKeywordButton;
    private ArrayList<HashMap<String, Integer>> cates = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_jobs_screen);
        mJobSalaryET        = (EditText) findViewById(R.id.job_salary_search_et);
        mJobLocationTV      = (TextView) findViewById(R.id.job_location_search_et);
        mCatSP              = (Spinner)  findViewById(R.id.spinner_cat);
        mJobLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(com.proGrad.temporaryjobapp.WorkerActivities.SearchJobsScreen.this, Map.class);
                startActivityForResult(mIntent, 0);
            }
        });
        SearchByKeywordButton = findViewById(R.id.search_job_by_keywords_bt);
        SearchByKeywordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(com.proGrad.temporaryjobapp.WorkerActivities.SearchJobsScreen.this , WorkerJobsListScreen.class);
                intent.putExtra("salary", mJobSalaryET.getText().toString());
                intent.putExtra("location", mJobLocationTV.getText().toString());
                intent.putExtra("category", checkCategory() + "");
                startActivity(intent);
            }
        });
        TempJobsRestClientUsage.get("getCategories.php", null, new AsyncHttpResponseHandler() {
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
                        String[] cs = new String[jsonArray.length()];

                        for (int i = 0, len = jsonArray.length(); i < len; i++)
                        {
                            JSONObject category = jsonArray.getJSONObject(i);

                            HashMap<String, Integer> hashMap = new HashMap<>();
                            hashMap.put(category.getString("category_name"),
                                    category.getInt("category_id"));

                            cs[i] = category.getString("category_name");

                            cates.add(hashMap);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (com.proGrad.temporaryjobapp.WorkerActivities.SearchJobsScreen.this,
                                android.R.layout.simple_spinner_item, cs);
                        mCatSP.setAdapter(adapter);
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
    private int checkCategory()
    {
        int res = -1;

        for (int i = 0, len = cates.size(); i < len; i++)
        {
            HashMap<String, Integer> c = cates.get(i);

            if (c.containsKey(mCatSP.getSelectedItem().toString()))
            {
                res = c.get(mCatSP.getSelectedItem().toString());
                break;
            }
        }

        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String returnedResult = data.getData().toString();
                mJobLocationTV.setText(returnedResult);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}