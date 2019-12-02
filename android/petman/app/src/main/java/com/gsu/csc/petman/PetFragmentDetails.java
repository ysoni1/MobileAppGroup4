package com.gsu.csc.petman;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PetFragmentDetails extends Fragment {

    private TextInputLayout txtName;
    private TextInputLayout txtBreed;
    private EditText txtDOB;
    private FloatingActionButton fabSave;
    private FloatingActionButton fabBack;
    private Spinner gender;
    private Spinner petType;
    private GlobalVariables globalVariables;

    private int petId;
    private int ownerId;
    private int position = -1;
    private String actionType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.fragment_pet_details, container, false);

        GlobalVariables globalVariables = (GlobalVariables) getActivity().getApplicationContext();

        ownerId = globalVariables.getUserId();

        Bundle bundle = getArguments();

        if ( bundle != null ) {
            petId = bundle.getInt("id", -1);
            actionType = bundle.getString("action");
            position = bundle.getInt("position", -1);
        }

        txtName = (TextInputLayout) view.findViewById(R.id.txtName);
        txtBreed = (TextInputLayout) view.findViewById(R.id.txtBreed);
        txtDOB = (EditText) view.findViewById(R.id.txtDOB);

        gender = (Spinner) view.findViewById(R.id.gender);
        ArrayAdapter<CharSequence> genderArrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.gender, android.R.layout.simple_spinner_item);
        genderArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderArrayAdapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        petType = (Spinner) view.findViewById(R.id.type);
        ArrayAdapter<CharSequence> petArrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.pet_type, android.R.layout.simple_spinner_item);
        petArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petType.setAdapter(petArrayAdapter);
        petType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fabSave = (FloatingActionButton) view.findViewById(R.id.fab_save);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput()) {
                    saveRecord();
                }
            }
        });

        fabBack = (FloatingActionButton) view.findViewById(R.id.fab_back);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToParentFragment("none", "BACK");
            }
        });



        if (actionType.equals("change")) {
            txtName.getEditText().setText(bundle.getString("name"));
            txtBreed.getEditText().setText(bundle.getString("breed"));
            txtDOB.setText(bundle.getString("dob"));

            gender.setSelection(genderArrayAdapter.getPosition(bundle.getString("sex")));
            petType.setSelection(petArrayAdapter.getPosition(bundle.getString("type")));

        }

        txtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);


                if (actionType.equalsIgnoreCase("change")) {
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
                        cal.setTime(df.parse(txtDOB.getText().toString()));

                        year = cal.get(Calendar.YEAR);
                        month = cal.get(Calendar.MONTH);
                        day = cal.get(Calendar.DAY_OF_MONTH);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar c = Calendar.getInstance();

                        c.set(Calendar.YEAR, i);
                        c.set(Calendar.MONTH, i1);
                        c.set(Calendar.DAY_OF_MONTH, i2);

                        txtDOB.setText(DateFormat.getDateInstance().format(c.getTime()));

                    }
                }, year, month, day);

                datePickerDialog.setTitle("Date of Birth");
                datePickerDialog.show();
            }
        });


        return view;
    }

    private boolean saveRecord() {
        RequestQueue requestQueue;
        JSONObject payload = new JSONObject();
        int httpMethod = 0;
        String url = null;

        try {
            payload.put("name", txtName.getEditText().getText());
            payload.put("sex", gender.getSelectedItem());
            payload.put("type", petType.getSelectedItem());
            payload.put("breed", txtBreed.getEditText().getText());
            payload.put("owner_id", ownerId);
            payload.put("dob", txtDOB.getText());

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        requestQueue = Volley.newRequestQueue(getContext());

        if (actionType.equals("add")) {
            httpMethod = Request.Method.POST;
            url = getString(R.string.API_URL) + "/pet";

        } else if ( actionType.equals("change")) {
            httpMethod = Request.Method.PUT;
            url = getString(R.string.API_URL) + "/pet/" + petId + "/" + ownerId;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(httpMethod, url, payload, new Response.Listener<JSONObject>() {
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

    private void returnToParentFragment(String payload, String statusCode) {

        Bundle bundle = new Bundle();
        bundle.putString("payload", payload);
        bundle.putInt("position", position);
        bundle.putString("statuscode", statusCode);

        PetFragment petFragment = new PetFragment();
        petFragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, petFragment).commit();

    }

    private boolean validateInput() {

        if ( txtName.getEditText().getText().toString().isEmpty()) {
            txtName.setError("Field cannot be empty");

            return false;
        } else {
            txtName.setError(null);
        }

        if ( txtBreed.getEditText().getText().toString().isEmpty()) {
            txtBreed.setError("Field cannot be empty");

            return false;
        } else {
            txtBreed.setError(null);
        }

        if ( txtDOB.getText().toString().isEmpty()) {
            txtDOB.setError("Field cannot be empty");

            return false;
        } else {
            txtDOB.setError(null);
        }

        return true;
    }

}
