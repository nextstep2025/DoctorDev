package com.routetracking.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        holder.cptime.setText(pos.getStartTrackTime());

        holder.deleart.setOnClickListener(new Delete(position, holder));

    }


    @Override
    public int getItemCount() {
        return this.itemList == null ? 0 : this.itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView routeName,cptime;
        private ImageView deleart;
        ViewHolder(View itemView) {
            super(itemView);


            routeName = itemView.findViewById(R.id.route_name);
            cptime = itemView.findViewById(R.id.cptime);
            deleart = (ImageView) itemView.findViewById(R.id.deleart);
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


    private class Delete implements View.OnClickListener {

        int position;
        RecyclerView.ViewHolder hld;


        public Delete(int position, RecyclerView.ViewHolder hld) {
            this.position = position;
            this.hld = hld;
        }

        @Override
        public void onClick(View v) {

            //   System.out.println("itemList3 = " + itemList.size());
            //     System.out.println("itemList.get(position) = " + itemList.get(position));

//            Article myCustomer = (Article.find(Article.class,
//                    "mid = ?",  itemList.get(position).get_id())).get(position);
//            myCustomer.delete();


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage(R.string.deletemg).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {



                    Routes.executeQuery("DELETE FROM ROUTES WHERE ID = '"
                            + itemList.get(position).getId() + "'");



                    itemList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, itemList.size());

                    //  System.out.println("itemList4 = " + itemList.size());

                    dialog.dismiss();
                }
            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            }).show();


        }
    }


}

