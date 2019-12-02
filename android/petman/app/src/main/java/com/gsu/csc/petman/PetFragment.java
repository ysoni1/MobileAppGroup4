package com.gsu.csc.petman;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gsu.csc.adapters.PetAdapter;
import com.gsu.csc.models.PetModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PetFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private PetAdapter petAdapter;
    private List<PetModel> petModelList;
    private RequestQueue requestQueue;
    private GlobalVariables globalVariables;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_pet, container, false);

        GlobalVariables globalVariables = (GlobalVariables) getActivity().getApplicationContext();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPetsList(true, globalVariables.getUserId());

                petAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("action", "add");

                PetFragmentDetails petFragmentDetails = new PetFragmentDetails();
                petFragmentDetails.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, petFragmentDetails).commit();
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        petModelList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());
        loadPetsList(false, globalVariables.getUserId());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void loadPetsList(final boolean reload, int owner_id) {

        String url = getString(R.string.API_URL).concat("/pets/" + owner_id);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("pets");

                    if (reload) petModelList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject pet = jsonArray.getJSONObject(i);

                        try{
                            Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(pet.getString("dob"));

                            petModelList.add(new PetModel(pet.getInt("id"),
                                    pet.getString("name"),
                                    pet.getString("sex"),
                                    pet.getString("breed"),
                                    pet.getInt("owner"),
                                    dob,
                                    pet.getString("type"))
                            );

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    petAdapter = new PetAdapter(getContext(), petModelList);
                    recyclerView.setAdapter(petAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(Integer.parseInt(getString(R.string.API_FETCH_TIMEOUT)),  DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            final int position = viewHolder.getAdapterPosition();

            AlertDialog.Builder alertDialogBuilder;

            switch (i){
                case ItemTouchHelper.LEFT:

                    alertDialogBuilder = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.dialog_title)
                            .setMessage(R.string.dialog_message)
                            .setCancelable(false)
                            .setPositiveButton(R.string.dialog_btn_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    int ownerId = petModelList.get(position).getOwner_id();
                                    int petId = petModelList.get(position).getId();

                                    deleteRecord(ownerId, petId );

                                    petModelList.remove(position);
                                    petAdapter.notifyItemRemoved(position);
                                }
                            })
                            .setNegativeButton(R.string.dialog_btn_no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    petAdapter.notifyDataSetChanged();
                                    dialogInterface.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    break;

                case ItemTouchHelper.RIGHT:
                    break;
            }

        }
    };

    private boolean deleteRecord(int ownerId, int petId) {
        String url = getString(R.string.API_URL).concat("/pet/" + petId + "/" + ownerId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getContext(), "Record removed without issues", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error removing record - " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,  DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

        return true;
    }



}
