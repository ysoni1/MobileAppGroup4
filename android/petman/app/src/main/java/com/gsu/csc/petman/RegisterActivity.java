package com.gsu.csc.petman;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private Button backButton;
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout email;
    private TextInputLayout username;
    private TextInputLayout password;
    private TextInputLayout confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bindActivityFields();
        configButtonListener();
    }

    private void bindActivityFields() {
        registerButton = (Button) findViewById(R.id.btn_register);
        backButton = (Button) findViewById(R.id.btn_return);

        firstName = (TextInputLayout) findViewById(R.id.txt_first_name);
        lastName = (TextInputLayout) findViewById(R.id.txt_last_name);
        email = (TextInputLayout) findViewById(R.id.txt_email);
        username = (TextInputLayout) findViewById(R.id.txt_username);
        password = (TextInputLayout) findViewById(R.id.txt_password);
        confirmPassword = (TextInputLayout) findViewById(R.id.txt_confirm_password);
    }

    private void configButtonListener() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isValid = false;

                List<TextInputLayout> textInputLayoutList = Arrays.asList(firstName, lastName, email, username, password, confirmPassword);
                ArrayList<TextInputLayout> textInputLayoutArrayList = new ArrayList<>();
                textInputLayoutArrayList.addAll(textInputLayoutList);

                for (int i =0; i < textInputLayoutArrayList.size(); i++) {
                    TextInputLayout item = textInputLayoutArrayList.get(i);

                    if (item.getEditText().getText().toString().isEmpty()) {
                        item.setError("Field can't be empty");

                        return;
                    } else {
                        item.setError(null);
                    }
                }

                if (password.getEditText().getText().toString().equals(confirmPassword.getEditText().getText().toString())) {
                    JSONObject payload = new JSONObject();

                    try {
                        payload.put("username", username.getEditText().getText());
                        payload.put("password", password.getEditText().getText());
                        payload.put("first_name", firstName.getEditText().getText());
                        payload.put("last_name", lastName.getEditText().getText());
                        payload.put("email", email.getEditText().getText());

                        performRegisterAccount(payload);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "registerButton - " + e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }




                } else {
                    username.setError("Field does not match");
                    password.setError("Field does not match");

                    return;
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));;
            }
        });
    }

    private void performRegisterAccount(JSONObject payload) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.API_URL) + "/register", payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Account created without issues", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));;
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
}
