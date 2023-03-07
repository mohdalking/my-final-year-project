package com.proGrad.temporaryjobapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterScreen extends AppCompatActivity {

    private EditText   mEmailET;
    private EditText   mUsernameET;
    private RadioGroup mTypeRG;
    private EditText   mPasswordET;
    private EditText   mConfirmPasswordET;
    private EditText   mPhoneET;
    private EditText   mFnameET;

    private Button   mRegisterButton;
    private TextView mSigninLink;

    private String mEmail;
    private String mUsername;
    private String mPassword;
    private String mRepPassword;
    private String mPhone;
    private String mFullname;
    private String mType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        StatusBarUtil.setTransparent(RegisterScreen.this);
        mEmailET           = (EditText) findViewById(R.id.job_name_modify_et);
        mUsernameET        = (EditText) findViewById(R.id.job_cat_modify_et);
        mTypeRG            = (RadioGroup) findViewById(R.id.account_type_radio_group);
        mPasswordET        = (EditText) findViewById(R.id.job_location_modify_et);
        mConfirmPasswordET = (EditText) findViewById(R.id.password_reg_repeat);
        mPhoneET           = (EditText) findViewById(R.id.job_deadline_modify_et);
        mFnameET           = (EditText) findViewById(R.id.job_salary_modify_et);

        mTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i)
            {
                if (i == R.id.admin_radio_button)
                {
                    mType = "0";
                }
                else
                {
                    mType = "1";
                }
            }
        });

        mRegisterButton = findViewById(R.id.register_bt);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attempRegister();


            }
        });

        mSigninLink = findViewById(R.id.have_account_link_button);
        mSigninLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    private void attempRegister() {
        mEmail = mEmailET.getText().toString();
        mUsername = mUsernameET.getText().toString();
        mPassword = mPasswordET.getText().toString();
        mRepPassword = mConfirmPasswordET.getText().toString();
        mPhone = mPhoneET.getText().toString();
        mFullname = mFnameET.getText().toString();

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)
                || TextUtils.isEmpty(mPhone) || TextUtils.isEmpty(mFullname)) {
            Toast.makeText(getApplicationContext(),
                    "all fields requried", Toast.LENGTH_LONG).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            Toast.makeText(getApplicationContext(),
                    "Email is not valid!", Toast.LENGTH_LONG).show();
        }
        else if (mPhone.length() != 10 || !mPhone.startsWith("0"))
        {
            Toast.makeText(getApplicationContext(),
                    "number is invalid", Toast.LENGTH_LONG).show();
        }
        else if (mPassword.length() < 8 || !isValidPassword(mPassword))
        {
            Toast.makeText(getApplicationContext(),
                    "The password is invalid!", Toast.LENGTH_LONG).show();
        }
        else if (!mPassword.equals(mRepPassword)) {
            Toast.makeText(getApplicationContext(),
                    "password does not match!", Toast.LENGTH_LONG).show();
        }
        else {


        RequestParams params = new RequestParams();
        params.put("email", mEmail);
        params.put("username", mUsername);
        params.put("phone", mPhone);
        params.put("password", mPassword);
        params.put("fname", mFullname);
        params.put("type", mType);

        TempJobsRestClientUsage.post("register.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String res = new String(responseBody, "UTF-8");

                    JSONObject resObj = new JSONObject(res);

                    if (resObj.getBoolean("success")) {
                        SessionManager.getInstance(getApplicationContext()).setUserID(resObj.getInt("id"));
                        SessionManager.getInstance(getApplicationContext()).setUserName(mUsername);
                        SessionManager.getInstance(getApplicationContext()).setUserPass(mPassword);
                        SessionManager.getInstance(getApplicationContext()).setUserFname(mFullname);
                        SessionManager.getInstance(getApplicationContext()).setUserEmail(mEmail);
                        SessionManager.getInstance(getApplicationContext()).setUserPhone(mPhone);
                        SessionManager.getInstance(getApplicationContext()).setUserType(Integer.parseInt(mType));


                        StyleableToast.makeText(RegisterScreen.this,
                                " Registered", Toast.LENGTH_LONG, R.style.MyToast).show();

                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), resObj.getString("message"),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    StyleableToast.makeText(RegisterScreen.this,
                            " Registered", Toast.LENGTH_LONG, R.style.MyToast).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Response Failure!",
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
