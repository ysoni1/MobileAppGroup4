package com.gsu.csc.petman;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private TextInputLayout username;
    private TextInputLayout password;
    private GlobalVariables globalVariables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        configRegisterSpan();
        configLoginAction();
        configGlobalVariables();
    }

    private void configGlobalVariables() {
        globalVariables = (GlobalVariables) getApplicationContext();
    }

    private void configRegisterSpan() {

        TextView register = findViewById(R.id.txt_register);
        Spannable spanRegister = new SpannableString(getString(R.string.register_message));

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

            }
        };
        spanRegister.setSpan(clickableSpan, 24,32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        register.setText(spanRegister);
        register.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void configLoginAction() {
        loginButton = (Button) findViewById(R.id.btn_login);
        username = (TextInputLayout) findViewById(R.id.txt_username);
        password = (TextInputLayout) findViewById(R.id.txt_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getEditText().getText().toString().isEmpty()) {
                    username.setError("Field can't be empty");

                    return;

                } else {
                    username.setError(null);
                }

                if ( password.getEditText().getText().toString().isEmpty()) {
                    password.setError("Field can't be empty");

                    return;

                } else {
                    password.setError(null);
                }

                JSONObject payload = new JSONObject();

                try {
                    payload.put("username", username.getEditText().getText());
                    payload.put("password", password.getEditText().getText());

                    performAPILogin(payload);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "configLoginAction - " + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            }
        });
    }

    private void performAPILogin(JSONObject payload) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.API_URL) + "/auth", payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    globalVariables.setAccessToken(response.getString("access_token"));
                    getUserInfo(username.getEditText().getText().toString());

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "performAPILogin - " + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), getString(R.string.invalid_username_password), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Integer.parseInt(getString(R.string.API_FETCH_TIMEOUT)),  DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    private void getUserInfo(final String username) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.API_URL) + "/user/" + username, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    globalVariables.setUserId(response.getInt("id"));
                    globalVariables.setUsername(response.getString("username"));
                    globalVariables.setEmail(response.getString("email"));

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "performAPILogin - " + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), getString(R.string.invalid_username_password), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "JWT " + globalVariables.getAccessToken());
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Integer.parseInt(getString(R.string.API_FETCH_TIMEOUT)),  DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }
}
