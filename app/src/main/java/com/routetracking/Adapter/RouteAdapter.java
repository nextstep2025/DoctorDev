package com.routetracking.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.routetracking.POJO.Routes;
import com.routetracking.R;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Routes> itemList;
    private Context context;
    private OnItemClickListener mItemClickListener;


    public RouteAdapter(List<Routes> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_root_list, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {

        Routes pos = itemList.get(position);
        ViewHolder holder = (ViewHolder) holder1;
        holder.routeName.setText(pos.getName());



    }


    @Override
    public int getItemCount() {
        return this.itemList == null ? 0 : this.itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView routeName;

        ViewHolder(View itemView) {
            super(itemView);


            routeName = itemView.findViewById(R.id.route_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


}

