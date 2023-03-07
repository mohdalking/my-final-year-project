package com.proGrad.temporaryjobapp.WorkerActivities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.proGrad.temporaryjobapp.R;
import com.proGrad.temporaryjobapp.TempJobsRestClientUsage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
public class ShowMapScreen extends AppCompatActivity implements OnMapReadyCallback {
    private SupportMapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private LatLng             mLatLng = null;
    private final int REQ_PERMISSION = 0;
    private ArrayList<HashMap<String, Integer>> jobs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map_screen);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(com.proGrad.temporaryjobapp.WorkerActivities.ShowMapScreen.this , WorkerJobInfo.class);
                intent.putExtra("job_id", checkJob(marker.getTitle().toString() + ""));
                intent.putExtra("is_owner", false);
                startActivity(intent);
            }
        });
        if(checkPermission())
        {
            mGoogleMap.setMyLocationEnabled(true);
            TempJobsRestClientUsage.get("getJobs.php", null, new AsyncHttpResponseHandler() {
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

                                HashMap<String, Integer> hashMap = new HashMap<>();
                                hashMap.put(job.getString("job_name"),
                                        job.getInt("job_id"));
                                jobs.add(hashMap);

                                LatLng mLatLng = new LatLng(job.getDouble("lat"),
                                        job.getDouble("lon"));

                                MarkerOptions marker = new MarkerOptions().position(
                                        new LatLng(mLatLng.latitude, mLatLng.longitude))
                                        .title(job.getString("job_name"));

                                mGoogleMap.addMarker(marker);
                            }
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


            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng)
                {
                    mGoogleMap.clear();

                    mLatLng = latLng;


                }
            });
        }
        else
        {
            askPermission();
        }
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

    private boolean checkPermission() {
        Log.d("TTTT", "checkPermission()");

        return (ContextCompat.checkSelfPermission(com.proGrad.temporaryjobapp.WorkerActivities.ShowMapScreen.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    private void askPermission() {
        Log.d("TTTT", "askPermission()");
        ActivityCompat.requestPermissions(
                com.proGrad.temporaryjobapp.WorkerActivities.ShowMapScreen.this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                REQ_PERMISSION
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("TTTT", "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (checkPermission())
                        mGoogleMap.setMyLocationEnabled(true);

                } else {
                    // Permission denied

                }
                break;
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}