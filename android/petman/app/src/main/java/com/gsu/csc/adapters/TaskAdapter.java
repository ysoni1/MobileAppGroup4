package com.gsu.csc.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gsu.csc.models.TaskModel;
import com.gsu.csc.petman.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TaskAdapter extends  RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    private Context context;
    private List<TaskModel> taskModel;

    public TaskAdapter(Context context, List<TaskModel> taskModel) {
        this.context = context;
        this.taskModel = taskModel;
    }


    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_recycleview_row, viewGroup, false);

        return new TaskAdapter.TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder taskViewHolder, int i) {

        TaskModel item = taskModel.get(i);

        taskViewHolder.txtDescription.setText(item.getDescription());

        StringBuilder stringBuilder = new StringBuilder();

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(item.getStartDate());
        stringBuilder.append(calendar.get(Calendar.HOUR_OF_DAY));
        stringBuilder.append(":");
        stringBuilder.append(String.format("%02d", calendar.get(Calendar.MINUTE)));
        stringBuilder.append(" - ");

        calendar.setTime(item.getEndDate());
        stringBuilder.append(calendar.get(Calendar.HOUR_OF_DAY));
        stringBuilder.append(":");
        stringBuilder.append(calendar.get(Calendar.MINUTE));

        taskViewHolder.txtWhen.setText(stringBuilder);

        if (item.getCompleted()) {
            taskViewHolder.txtDescription.setPaintFlags(taskViewHolder.txtDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            taskViewHolder.txtWhen.setPaintFlags(taskViewHolder.txtWhen.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            taskViewHolder.txtDescription.setPaintFlags(taskViewHolder.txtDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            taskViewHolder.txtWhen.setPaintFlags(taskViewHolder.txtWhen.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return taskModel.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView txtDescription;
        private TextView txtWhen;
        private RequestQueue requestQueue;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtWhen = itemView.findViewById(R.id.txtWhen);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();

            requestQueue = Volley.newRequestQueue(view.getContext());
            TaskModel currentItem = taskModel.get(position);

            currentItem.setCompleted(!currentItem.getCompleted());

            updateTask(currentItem.getCompleted(),
                    view.getContext().getString(R.string.API_URL).concat("/task/" + currentItem.getId() + "/" + currentItem.getOwner_id()),
                    Integer.parseInt(view.getContext().getString(R.string.API_FETCH_TIMEOUT)),
                    requestQueue
            );

            notifyDataSetChanged();
            return true;
        }

        private void updateTask(boolean status, String url, int timeout, RequestQueue requestQueue) {
            JSONObject payload = new JSONObject();

            try {
                payload.put("completed",status);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, payload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String payloadResult = response.getJSONObject("record").toString();

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

            request.setRetryPolicy(new DefaultRetryPolicy(timeout,  DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(request);
        }
    }
}
