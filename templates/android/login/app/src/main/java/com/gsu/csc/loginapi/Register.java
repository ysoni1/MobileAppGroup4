package com.gsu.csc.loginapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private Button btnBack;
    private Button btnRegister;

    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtEmail;

    private RequestQueue requestQueue;
    private final static String API_URL = "https://gsu-petman.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        txtUsername = (EditText) findViewById(R.id.username);
        txtPassword = (EditText) findViewById(R.id.password);
        txtFirstName = (EditText) findViewById(R.id.firstName);
        txtLastName = (EditText) findViewById(R.id.lastName);
        txtEmail = (EditText) findViewById(R.id.email);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, MainActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject payload = new JSONObject();

                try {
                    payload.put("username", txtUsername.getText());
                    payload.put("password", txtPassword.getText());
                    payload.put("first_name", txtFirstName.getText());
                    payload.put("last_name", txtLastName.getText());
                    payload.put("email", txtEmail.getText());

                    saveData(payload);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error - " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void saveData(JSONObject payload) {
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(API_URL + "/register", payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "User created without issues", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error creating user " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);


    }
}
