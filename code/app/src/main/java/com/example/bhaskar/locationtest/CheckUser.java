package com.example.bhaskar.locationtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bhaskar on 30/3/15.
 */
public class CheckUser extends ActionBarActivity {
    EditText email, secret;
    Button submit;
    private final String URL = "http://ves.hol.es/getdata.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkuser);
        email = (EditText) findViewById(R.id.emaildata);
        secret = (EditText) findViewById(R.id.secretdata);
        submit = (Button) findViewById(R.id.check);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String semail = email.getText().toString();
                String ssecret = secret.getText().toString();
                if (isConnectedToInternet(CheckUser.this)) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();


                    params.put("email", semail);
                    params.put("secret", ssecret);


                    client.post(URL, params, new JsonHttpResponseHandler() {
                        ProgressDialog nDialog;

                        @Override
                        public void onStart() {
                            //Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
                            super.onStart();

                            nDialog = new ProgressDialog(CheckUser.this);
                            nDialog.setMessage("Checking User");
                            nDialog.setTitle("Sending Data...");
                            nDialog.setIndeterminate(false);
                            nDialog.setCancelable(false);
                            nDialog.show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                JSONArray array = response.getJSONArray("result");
                                JSONObject object = array.getJSONObject(0);
                                String success = object.getString("success");

                             switch(Integer.parseInt(success))
                             {
                                 case 0:
                                     Toast.makeText(getApplicationContext(),"some error in sending data",Toast.LENGTH_LONG).show();
                                     break;
                                 case 1:
                                     String location=object.getString("location");
                                     //Toast.makeText(getApplicationContext(),"Location found:"+location,Toast.LENGTH_LONG).show();
                                     Intent intent=new Intent(CheckUser.this,PlotUser.class);
                                     intent.putExtra("location",location);
                                     startActivity(intent);
                                     //finish();
                                     break;
                                 case 2:
                                     Toast.makeText(getApplicationContext(),"User registered but location not found",Toast.LENGTH_LONG).show();
                                     break;
                                 case 3:
                                     Toast.makeText(getApplicationContext(),"Invalid email or secret",Toast.LENGTH_LONG).show();
                                     break;



                             }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            nDialog.dismiss();
                        }


                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            nDialog.dismiss();
                            Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRetry(int retryNo) {
                            Toast.makeText(getApplicationContext(), "Retrying... ", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection ", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

    public static boolean isConnectedToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }
}
