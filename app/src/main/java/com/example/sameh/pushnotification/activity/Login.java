package com.example.sameh.pushnotification.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sameh.pushnotification.R;
import com.example.sameh.pushnotification.other.SingleTon;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private int login;

    EditText Email;
    EditText Password;
    Button loginButton;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email  = findViewById(R.id.input_email);
        Password = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);
        sharedPreferences =  getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        if (sharedPreferences.contains("driverId"))
        {
            String email = sharedPreferences.getString("driverId","");
            String passsword = sharedPreferences.getString("password","");
            Email.setText(email);
            Password.setText(passsword);
            login(email,passsword);
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                login(email,password);
            }
        });

    }

    public void login(String email ,String password) {
        Log.d(TAG, "Login");

        if (!validate(email,password)) {
            onLoginFailed(-1);
            return;
        }

        //loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Login.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email_temp = email;
        final String password_temp = password;


        if (sharedPreferences.getString("driverId","").equals(""))
            LoginService(email_temp,password_temp);
        else
            login = 1;

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        // call Log in Service
                        if(login==1)
                            onLoginSuccess();
                        else
                            onLoginFailed(login);

                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 5000);
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        if(sharedPreferences.getString("driverId","").equals("")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("driverId", Email.getText().toString());
            editor.putString("password", Password.getText().toString());
            editor.commit();
        }
        finish();
    }

    public void onLoginFailed(int error) {
        if (error==0 || error==1)
            Toast.makeText(getApplicationContext(),"Wrong Id or Password ",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate(String email, String password) {
        boolean valid = true;

        if (email.isEmpty() ) {
            Email.setError("enter a valid Id address");
            valid = false;
        } else {
            Email.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            Password.setError("minmum 4 alphanumeric characters");
            valid = false;
        } else {
            Password.setError(null);
        }

        return valid;
    }

    public void LoginService(String email, String password)
    {
        String url = "http://seelsapp.herokuapp.com/login/"+email+"/"+password;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    login= response.getInt("logged");
                } catch (JSONException e) {
                    Log.i("message","howwwwwwwwww");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error network Connection",Toast.LENGTH_LONG).show();
            }
        });
        SingleTon.getInstance(getApplicationContext()).addToRequestQueue(request);

    }
}
