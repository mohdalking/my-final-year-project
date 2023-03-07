package com.proGrad.temporaryjobapp.OwnerActivities;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.proGrad.temporaryjobapp.Map;
import com.proGrad.temporaryjobapp.R;
import com.proGrad.temporaryjobapp.SessionManager;
import com.proGrad.temporaryjobapp.TempJobsRestClientUsage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
public class AddJobScreen extends AppCompatActivity {
    private EditText             mJobNameET;
    private AutoCompleteTextView mJobCategoryAC;
    private TextView             mJobLocationTV;
    private TextView             mDeadLineTV;
    private EditText             mJobSalaryET;
    private EditText             mJobDescriptionET;
    private Button AddJobButton;
    private ArrayList<HashMap<String, Integer>> cates = new ArrayList<>();
    private String mJobName;
    private String mJobCategory;
    private int    mCategoryId;
    private String mJobLocation;
    private String mJobDeadLine;
    private String mJobSalary;
    private String mJobDescription;
    private int mYear, mMonth, mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job_screen);
        mJobNameET          = (EditText)             findViewById(R.id.job_name_add_et);
        mJobCategoryAC      = (AutoCompleteTextView) findViewById(R.id.job_cat_add_et);
        mJobLocationTV      = (TextView)             findViewById(R.id.job_location_add_et);
        mDeadLineTV         = (TextView)             findViewById(R.id.job_deadline_add_et);
        mJobSalaryET        = (EditText)             findViewById(R.id.job_salary_add_et);
        mJobDescriptionET   = (EditText)             findViewById(R.id.job_desc_add_et);
        mJobLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(AddJobScreen.this, Map.class);
                startActivityForResult(mIntent, 0);
            }
        });

        mDeadLineTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mMonth = c.get(Calendar.MONTH); // 7
                mDay = c.get(Calendar.DAY_OF_MONTH); // 26
                mYear = c.get(Calendar.YEAR); // 2021

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddJobScreen.this,
                        datePickerListener, mYear, mMonth, mDay){

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        if (monthOfYear < mMonth && year == mYear)
                            view.updateDate(mMonth, mDay, mYear);


                        if (dayOfMonth < mDay && year == mYear && monthOfYear == mMonth)
                            view.updateDate(mMonth, mDay, mYear);

                        if (year < mYear)
                            view.updateDate(mMonth, mDay, mYear);
                    }

                };
                datePickerDialog.show();
            }
        });

        AddJobButton = findViewById(R.id.add_job_bt);

        AddJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempAddJob();
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

                        String[] cs = new String[jsonArray.length()]; // 3

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
                                (AddJobScreen.this, android.R.layout.simple_list_item_1, cs);
                        mJobCategoryAC.setThreshold(1);
                        mJobCategoryAC.setAdapter(adapter);
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
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {


        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            mYear = selectedYear;
            mMonth = selectedMonth;
            mDay = selectedDay;

            mDeadLineTV.setText(new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));

        }
    };

    public void attempAddJob()
    {
        mJobName        = mJobNameET.getText().toString();
        mJobCategory    = mJobCategoryAC.getText().toString();
        mJobLocation    = mJobLocationTV.getText().toString();
        mJobDeadLine    = mDeadLineTV.getText().toString();
        mJobSalary      = mJobSalaryET.getText().toString();
        mJobDescription = mJobDescriptionET.getText().toString();

        checkCategory();
        if (TextUtils.isEmpty(mJobName) || TextUtils.isEmpty(mJobCategory) || TextUtils.isEmpty(mJobLocation)
                || TextUtils.isEmpty(mJobDeadLine) || TextUtils.isEmpty(mJobSalary) || TextUtils.isEmpty(mJobDescription)) {
            Toast.makeText(getApplicationContext(),
                    "all fields requried", Toast.LENGTH_LONG).show();
        }else {
        RequestParams requestParams = new RequestParams();
        requestParams.put("job_name", mJobName);
        requestParams.put("job_category", mJobCategory);
        requestParams.put("job_category_id", mCategoryId + ""); // -1
        requestParams.put("job_location", mJobLocation);
        requestParams.put("job_deadline", mJobDeadLine);
        requestParams.put("job_salary", mJobSalary);
        requestParams.put("job_description", mJobDescription);
        requestParams.put("owner_id", SessionManager.getInstance(getApplicationContext()).getUserID() + "");

        TempJobsRestClientUsage.post("addJob.php", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try
                {
                    String res = new String(responseBody, "UTF-8");

                    JSONObject resObj = new JSONObject(res);

                    if (resObj.getBoolean("success"))
                    {
                        StyleableToast.makeText(AddJobScreen.this,
                                " Job Added", Toast.LENGTH_LONG, R.style.MyToast).show();
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
                    Toast.makeText(getApplicationContext(),"Response Failure!",
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
    }}

    private void checkCategory()
    {
        int res = -1;

        for (int i = 0, len = cates.size(); i < len; i++)
        {
            HashMap<String, Integer> c = cates.get(i);

            if (c.containsKey(mJobCategory))
            {
                res = c.get(mJobCategory);
                break;
            }
        }

        mCategoryId = res; // -1
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