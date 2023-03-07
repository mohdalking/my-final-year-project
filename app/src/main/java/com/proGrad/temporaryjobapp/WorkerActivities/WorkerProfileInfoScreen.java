package com.proGrad.temporaryjobapp.WorkerActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.proGrad.temporaryjobapp.R;
import com.proGrad.temporaryjobapp.SessionManager;
import com.proGrad.temporaryjobapp.TempJobsRestClientUsage;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WorkerProfileInfoScreen extends AppCompatActivity {

    private Button saveChanges;
    private EditText mEmailET;
    private EditText mUsernameET;
    private EditText mPasswordET;
    private EditText mPhoneET;
    private EditText mFullnameET;
    private String mEmail;
    private String mUsername;
    private String mPassword;
    private String mPhone;
    private String mFullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile_info_screen);


        mEmailET    = (EditText) findViewById(R.id.user_email_profile_edit_et) ;
        mUsernameET = (EditText) findViewById(R.id.user_username_profile_edit_et) ;
        mPasswordET = (EditText) findViewById(R.id.user_password_profile_edit_et) ;
        mPhoneET    = (EditText) findViewById(R.id.user_phone_number_profile_edit_et) ;
        mFullnameET = (EditText) findViewById(R.id.user_fullname_profile_edit_et) ;

        mEmailET.setText(SessionManager.getInstance(getApplicationContext()).getUserEmail());
        mUsernameET.setText(SessionManager.getInstance(getApplicationContext()).getUserName());
        mPasswordET.setText(SessionManager.getInstance(getApplicationContext()).getUserPass());
        mPhoneET.setText(SessionManager.getInstance(getApplicationContext()).getUserPhone());
        mFullnameET.setText(SessionManager.getInstance(getApplicationContext()).getUserFname());

        saveChanges = findViewById(R.id.user_save_changes_edit_profile_bt);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attempUpdate();
            }
        });
    }

    private void attempUpdate()
    {
        mEmail    = mEmailET.getText().toString();
        mUsername = mUsernameET.getText().toString();
        mPassword = mPasswordET.getText().toString();
        mPhone    = mPhoneET.getText().toString();
        mFullname = mFullnameET.getText().toString();

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)
                || TextUtils.isEmpty(mPhone) || TextUtils.isEmpty(mFullname)) {
            Toast.makeText(getApplicationContext(),
                    "all fields requried", Toast.LENGTH_LONG).show();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            Toast.makeText(getApplicationContext(),
                    "Email is not valid!", Toast.LENGTH_LONG).show();
        }
        else if (mPassword.length() < 8 || !isValidPassword(mPassword))
        {
            Toast.makeText(getApplicationContext(),
                    "The password is invalid!" +
                            "Use 8 or more characters with a mix of letters, numbers & symbols", Toast.LENGTH_LONG).show();
        }

        else {

        RequestParams params = new RequestParams();
        params.put("id", SessionManager.getInstance(getApplicationContext()).getUserID() + "");
        params.put("email", mEmail);
        params.put("username", mUsername);
        params.put("phone", mPhone);
        params.put("password", mPassword);
        params.put("fname", mFullname);
        params.put("type", "1");

        TempJobsRestClientUsage.post("updateInfo.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try
                {
                    String res = new String(responseBody, "UTF-8");

                    JSONObject resObj = new JSONObject(res);

                    if (resObj.getBoolean("success"))
                    {
                        SessionManager.getInstance(getApplicationContext()).setUserName(mUsername);
                        SessionManager.getInstance(getApplicationContext()).setUserPass(mPassword);
                        SessionManager.getInstance(getApplicationContext()).setUserFname(mFullname);
                        SessionManager.getInstance(getApplicationContext()).setUserEmail(mEmail);
                        SessionManager.getInstance(getApplicationContext()).setUserPhone(mPhone);

                        StyleableToast.makeText(WorkerProfileInfoScreen.this,
                                " Info updated", Toast.LENGTH_LONG, R.style.MyToast).show();

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
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"Response Failure!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }}

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}
