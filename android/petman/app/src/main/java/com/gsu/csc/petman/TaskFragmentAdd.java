package com.gsu.csc.petman;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TaskFragmentAdd extends Fragment {

    private TextInputLayout txtDescription;
    private EditText txtStartTime;
    private EditText txtEndTime;
    private FloatingActionButton fabSave;
    private FloatingActionButton fabBack;

    private GlobalVariables globalVariables;

    private int ownerId;
    private String taskDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_add, container, false);

        GlobalVariables globalVariables = (GlobalVariables) getActivity().getApplicationContext();
        ownerId = globalVariables.getUserId();

        Bundle bundle = getArguments();

        if ( bundle != null ){
            taskDate = bundle.getString("task_date");
        }

        txtDescription = (TextInputLayout) view.findViewById(R.id.txt_task_description);

        txtStartTime = (EditText) view.findViewById(R.id.txt_task_start_time);
        txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR);
                int min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        txtStartTime.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute));
                    }
                },hour, min, true);

                timePickerDialog.show();
            }
        });

        txtEndTime = (EditText) view.findViewById(R.id.txt_task_end_time);
        txtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR);
                int min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        txtEndTime.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute));
                    }
                },hour, min, true);

                timePickerDialog.show();
            }
        });

        fabBack = (FloatingActionButton) view.findViewById(R.id.task_fab_back);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToParentFragment("none", "BACK");
            }
        });

        fabSave = (FloatingActionButton) view.findViewById(R.id.task_fab_save);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput()) {
                    saveRecord();
                }
            }
        });



        return view;
    }

    private void returnToParentFragment(String payload, String statusCode) {

        Bundle bundle = new Bundle();
        bundle.putString("payload", payload);
        bundle.putString("statuscode", statusCode);

        TaskFragment taskFragment = new TaskFragment();
        taskFragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, taskFragment).commit();

    }

    private boolean validateInput() {

        if (txtDescription.getEditText().getText().toString().isEmpty()) {
            txtDescription.setError("Field cannot be empty");

            return false;
        } else {
            txtDescription.setError(null);
        }

        if (txtStartTime.getText().toString().isEmpty()) {
            txtStartTime.setError("Field cannot be empty");

            return false;
        } else {
            txtStartTime.setError(null);
        }

        if (txtEndTime.getText().toString().isEmpty()) {
            txtEndTime.setError("Field cannot be empty");

            return false;
        } else {
            txtEndTime.setError(null);
        }

        return true;

    }

    private boolean saveRecord() {
        RequestQueue requestQueue;
        JSONObject payload = new JSONObject();
        int httpMethod = 0;
        String url = null;

        try {
            payload.put("description", txtDescription.getEditText().getText());
            payload.put("start_date", taskDate.concat(" ").concat(txtStartTime.getText().toString().concat(":00")));
            payload.put("end_date", taskDate.concat(" ").concat(txtEndTime.getText().toString().concat(":00")));
            payload.put("owner_id", ownerId);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( Request.Method.POST, getString(R.string.API_URL) + "/task", payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String payloadResult = response.getJSONObject("record").toString();

                    returnToParentFragment(payloadResult, "OK");

                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(getContext(), "Error adding record " + e.toString(), Toast.LENGTH_LONG).show();
                    returnToParentFragment(e.toString(), "ERROR");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Toast.makeText(getContext(), "Error adding record " + error.toString(), Toast.LENGTH_LONG).show();
                returnToParentFragment(error.toString(), "ERROR");
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

        return true;
    }

}
