package com.proGrad.temporaryjobapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class forgetPassword extends AppCompatActivity {
    private EditText mUsername,mPassword;
    private Button mUpdateOnwer,mUpdateWorker;
    private String username, password;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mUsername = (EditText) findViewById(R.id.username_et);
        mPassword = (EditText) findViewById(R.id.pass_et);

        mUpdateOnwer=(Button) findViewById(R.id.update_owner);
        mUpdateOnwer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 0;
                doUpdate();
            }
        });

        mUpdateWorker=(Button) findViewById(R.id.update_worker);
        mUpdateWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
                doUpdate();
            }
        });



    }


    private void doUpdate(){

        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();


        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "All fields are required! ", Toast.LENGTH_LONG).show();
        }


        else if (password.length() < 8 || !isValidPassword(password)) {
            Toast.makeText(getApplicationContext(), "The password is invalid! ", Toast.LENGTH_LONG).show();
            return;
        }


        else {


            RequestParams params = new RequestParams();
            params.put("username",  username);
            params.put("password", password);
            params.put("type", type + "");


            TempJobsRestClientUsage.post("passwordUpdate.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    try
                    {
                        String res = new String(responseBody, "UTF-8");

                        JSONObject resObj = new JSONObject(res);

                        if (resObj.getBoolean("success"))
                        {
                            StyleableToast.makeText(forgetPassword.this,
                                    " Password updated", Toast.LENGTH_LONG, R.style.MyToast).show();

                            setResult(RESULT_OK);
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

        }
    }



    private   boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent(forgetPassword.this, LoginScreen.class);
        startActivity(mIntent);
        finish();
    }
}



