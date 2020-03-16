package com.syahputrareno975.heremapexample.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.syahputrareno975.heremapexample.R;
import com.syahputrareno975.heremapexample.util.Unit;

import java.util.ArrayList;

public class AdapterRouteTracking extends RecyclerView.Adapter<AdapterRouteTracking.Holder> {

    private Context context;
    private ArrayList<String> list;
    private Unit<String> onClick;

    public AdapterRouteTracking(Context context, ArrayList<String> list, Unit<String> onClick) {
        this.context = context;
        this.list = list;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(((Activity)context).getLayoutInflater().inflate(R.layout.adapter_tracking,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.type.setImageDrawable(
                position == 0 && list.isEmpty() ? ContextCompat.getDrawable(context,R.drawable.tracking_idle) :
                position == 0 ? ContextCompat.getDrawable(context,R.drawable.tracking_start) :
                position == list.size() - 1 ? ContextCompat.getDrawable(context,R.drawable.tracking_end) :
                        ContextCompat.getDrawable(context,R.drawable.tracking_middle)
        );
        holder.markerName.setText(list.get(position));
        holder.markerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.invoke(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        public ImageView type;
        public TextView markerName;

        public Holder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type_image_view);
            markerName = itemView.findViewById(R.id.name_marker);
        }
    }
}
