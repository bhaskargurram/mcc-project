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
public class Register extends ActionBarActivity {
    Button submit;
    EditText fname, lname, email, phone, password, confirmpass, secret;
    private final String URL = "http://ves.hol.es/register.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        submit = (Button) findViewById(R.id.register);
        fname = (EditText) findViewById(R.id.rfname);
        lname = (EditText) findViewById(R.id.rlname);
        email = (EditText) findViewById(R.id.remail);
        phone = (EditText) findViewById(R.id.rcontact);
        password = (EditText) findViewById(R.id.rpasswd);
        confirmpass = (EditText) findViewById(R.id.rconfpasswd);
        secret = (EditText) findViewById(R.id.rsecret);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sfname = fname.getText().toString().trim();
                String slname = lname.getText().toString().trim();
                final String semail = email.getText().toString().trim();
                String sphone = phone.getText().toString().trim();
                String spass = password.getText().toString().trim();
                String sconpass = confirmpass.getText().toString().trim();
                String ssecret = secret.getText().toString().trim();

                if (sfname.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid first name", Toast.LENGTH_SHORT).show();

                } else if (slname.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid last name", Toast.LENGTH_SHORT).show();
                } else if (semail.length() == 0 && !semail.contains("@")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid email id", Toast.LENGTH_SHORT).show();
                } else if (spass.length() <= 4) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid 5 or more digit password", Toast.LENGTH_SHORT).show();
                } else if (!sconpass.equals(spass)) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                } else if (sphone.length() != 10) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid contact name", Toast.LENGTH_SHORT).show();
                } else if (secret.length() != 5) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid 5 digit secret code which will be late used for tracking", Toast.LENGTH_SHORT).show();

                } else {

                    if (isConnectedToInternet(Register.this)) {
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();


                        params.put("fname", sfname);
                        params.put("lname", slname);
                        params.put("email", semail);
                        params.put("contact", sphone);
                        params.put("password", spass);
                        params.put("secret", ssecret);

                        client.post(URL, params, new JsonHttpResponseHandler() {
                            ProgressDialog nDialog;

                            @Override
                            public void onStart() {
                                //Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
                                super.onStart();

                                nDialog = new ProgressDialog(Register.this);
                                nDialog.setMessage("Regsitration");
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
                                    if (success.equals("1")) {


                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);


                                    } else if (success.equals("2")) {
                                        Toast.makeText(getApplicationContext(), "Email id already registered", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Some error occurred, Please try again", Toast.LENGTH_SHORT).show();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                nDialog.dismiss();


                            }


                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                nDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed Sending Data", Toast.LENGTH_SHORT).show();
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
