package com.example.bhaskar.locationtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
public class Login extends ActionBarActivity {
    Button login, register;
    EditText email, password;
    private final String URL = "http://ves.hol.es/login.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        email = (EditText) findViewById(R.id.lemail);
        password = (EditText) findViewById(R.id.lpass);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.lregister);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String semail = email.getText().toString().trim();
                String spass = password.getText().toString().trim();
                if (isConnectedToInternet(Login.this)) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();


                    params.put("email", semail);
                    params.put("password", spass);

                    client.post(URL, params, new JsonHttpResponseHandler() {
                        ProgressDialog nDialog;

                        @Override
                        public void onStart() {
                            //Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
                            super.onStart();

                            nDialog = new ProgressDialog(Login.this);
                            nDialog.setMessage("Login");
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
                                    SharedPreferences preferences = getSharedPreferences("locationtest", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("username", semail);
                                    editor.commit();
                                    Intent intent = new Intent(Login.this, TwoWays.class);
                                    startActivity(intent);

                                } else if (success.equals("2")) {
                                    Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();

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
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
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
