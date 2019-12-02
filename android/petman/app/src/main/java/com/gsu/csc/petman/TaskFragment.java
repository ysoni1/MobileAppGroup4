package com.gsu.csc.petman;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gsu.csc.adapters.TaskAdapter;
import com.gsu.csc.models.TaskModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskFragment extends Fragment {

    private CalendarView calendarView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private CheckBox checkBoxInProgress;
    private CheckBox checkBoxCompleted;

    private TaskAdapter taskAdapter;
    private List<TaskModel> taskModelList;
    private GlobalVariables globalVariables;
    private Calendar calendarViewDate;

    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_task, container, false);

        GlobalVariables globalVariables = (GlobalVariables) getActivity().getApplicationContext();

        checkBoxInProgress = (CheckBox) view.findViewById(R.id.check_in_progress);
        checkBoxInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTaskList(true, globalVariables.getUserId(), getCalendarPayload());
            }
        });


        checkBoxCompleted = (CheckBox) view.findViewById(R.id.check_completed);
        checkBoxCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTaskList(true, globalVariables.getUserId(), getCalendarPayload());
            }
        });

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.task_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View view) {
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("action", "add");
                                                        bundle.putString("task_date", getFormatCalendarDate());

                                                        TaskFragmentAdd taskFragmentAdd = new TaskFragmentAdd();
                                                        taskFragmentAdd.setArguments(bundle);

                                                        FragmentManager fragmentManager =  getFragmentManager();
                                                        fragmentManager.beginTransaction().replace(R.id.fragment_container, taskFragmentAdd).commit();
                                                    }
                                                });



        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.task_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTaskList(true, globalVariables.getUserId(), getCalendarPayload());

                taskAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        calendarView = (CalendarView) view.findViewById(R.id.task_calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {

                calendarViewDate.set(Calendar.DAY_OF_MONTH, i2);
                calendarViewDate.set(Calendar.MONTH, i1);
                calendarViewDate.set(Calendar.YEAR, i);

                loadTaskList(true, globalVariables.getUserId(), getCalendarPayload());
                taskAdapter.notifyDataSetChanged();
            }
        });

        if ( calendarViewDate == null ) {
            calendarViewDate = Calendar.getInstance();
            calendarViewDate.setTime(new Date(calendarView.getDate()));
        }


        recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskModelList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());

        loadTaskList(false, globalVariables.getUserId(), getCalendarPayload());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void loadTaskList(final boolean reload, int owner_id, JSONObject payload){
        String url = getString(R.string.API_URL).concat("/tasks/" + owner_id);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("tasks");

                    if (reload) taskModelList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject task = jsonArray.getJSONObject(i);

                        try {
                            taskModelList.add(new TaskModel(task.getInt("id"),
                                    task.getString("description"),
                                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(task.getString("start_date")),
                                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(task.getString("end_date")),
                                    task.getBoolean("completed"),
                                    task.getInt("owner_id")
                            ));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    taskAdapter = new TaskAdapter(getContext(), taskModelList);
                    recyclerView.setAdapter(taskAdapter);

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

    private JSONObject getCalendarPayload() {

        JSONObject payload = new JSONObject();

        try {
            payload.put("start_date", getFormatCalendarDate());

            if ( checkBoxInProgress.isChecked() == true && checkBoxCompleted.isChecked() == false) {
                payload.put("completed", false);

            } else if ( checkBoxInProgress.isChecked() == false && checkBoxCompleted.isChecked() == true) {
                payload.put("completed", true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payload;

    }

    private String getFormatCalendarDate() {

        int year = calendarViewDate.get(Calendar.YEAR);
        int month = calendarViewDate.get(Calendar.MONTH) + 1;
        int day = calendarViewDate.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d", year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);

    };

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

                                    int ownerId = taskModelList.get(position).getOwner_id();
                                    int taskId = taskModelList.get(position).getId();

                                    deleteRecord(ownerId, taskId );

                                    taskModelList.remove(position);
                                    taskAdapter.notifyItemRemoved(position);
                                }
                            })
                            .setNegativeButton(R.string.dialog_btn_no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    taskAdapter.notifyDataSetChanged();
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

    private boolean deleteRecord(int ownerId, int taskId) {
        String url = getString(R.string.API_URL).concat("/task/" + taskId + "/" + ownerId);

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

