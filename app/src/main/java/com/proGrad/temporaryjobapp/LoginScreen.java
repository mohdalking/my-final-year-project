package com.proGrad.temporaryjobapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.proGrad.temporaryjobapp.OwnerActivities.OwnerMainScreen;
import com.proGrad.temporaryjobapp.WorkerActivities.WorkerMainScreen;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginScreen extends AppCompatActivity {
    private EditText mUsernameET, mPasswordET;
    private Button mLoginAdminButton , mLoginUserButton;
    private TextView mSignupLink,forget_et;
    private String mUsername, mPassword;
    private int type = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        StatusBarUtil.setTransparent(LoginScreen.this);
        mUsernameET = (EditText) findViewById(R.id.job_name_modify_et);
        mPasswordET = (EditText) findViewById(R.id.editText2);
        mLoginAdminButton = findViewById(R.id.login_admin_button);
        mLoginAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                type = 0;
                attempLogin();
            }
        });
        mLoginUserButton = findViewById(R.id.login_user_button);
        mLoginUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
                attempLogin();
            }
        });
        forget_et=findViewById(R.id.forget_et);
        forget_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreen.this , forgetPassword.class);
                startActivity(intent);
            }
        });

        mSignupLink = findViewById(R.id.new_account_link_button);
        mSignupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreen.this , RegisterScreen.class);
                startActivity(intent);
            }
        });


        if (SessionManager.getInstance(getApplicationContext()).isLoggedIn())
        {
            int type = SessionManager.getInstance(getApplicationContext()).getUserType();

            Intent intent = null;
            switch (type)
            {
                case 0:
                    intent = new Intent(LoginScreen.this ,
                            OwnerMainScreen.class);
                    break;

                 case 1:
                    intent = new Intent(LoginScreen.this ,
                            WorkerMainScreen.class);
                    break;
            }

            startActivity(intent);
        }
    }

    private void attempLogin()
    {
        mUsername = mUsernameET.getText().toString();
        mPassword = mPasswordET.getText().toString();

        if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword))
        {
            Toast.makeText(getApplicationContext(), "Required All Fields!", Toast.LENGTH_LONG).show();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("username", mUsername);
        params.put("password", mPassword);
        params.put("type", type + "");

        Log.e("username", mUsername);
        Log.e("password", mPassword);
        Log.e("type", type + "");

        TempJobsRestClientUsage.post("login.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try
                {
                    String res = new String(responseBody, "UTF-8");

                    JSONObject resObj = new JSONObject(res);

                    if (resObj.getBoolean("success"))
                    {
                        int id = resObj.getInt("id");

                        SessionManager.getInstance(getApplicationContext()).setLoggedIn(true);
                        SessionManager.getInstance(getApplicationContext()).setUserID(id);
                        SessionManager.getInstance(getApplicationContext()).setUserType(type);
                        SessionManager.getInstance(getApplicationContext()).setUserName(resObj.getString("name"));
                        SessionManager.getInstance(getApplicationContext()).setUserPass(resObj.getString("pass"));
                        SessionManager.getInstance(getApplicationContext()).setUserFname(resObj.getString("fnam"));
                        SessionManager.getInstance(getApplicationContext()).setUserEmail(resObj.getString("emai"));
                        SessionManager.getInstance(getApplicationContext()).setUserPhone(resObj.getString("phon"));

                        Intent intent = null;
                        switch (type)
                        {
                            case 0:
                                intent = new Intent(LoginScreen.this ,
                                        OwnerMainScreen.class);
                                Toast.makeText(getApplicationContext(), "Login success!", Toast.LENGTH_LONG).show();
                                break;

                            case 1:
                                intent = new Intent(LoginScreen.this ,
                                        WorkerMainScreen.class);
                                Toast.makeText(getApplicationContext(), "Login success!", Toast.LENGTH_LONG).show();
                                break;
                        }

                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), resObj.getString("message"),
                                Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex)
                {
                    Log.e("exex", ex + "");
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
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private   boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.[0-9])(?=.[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}
