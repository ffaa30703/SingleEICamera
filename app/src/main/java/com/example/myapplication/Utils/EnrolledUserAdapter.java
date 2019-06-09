package com.example.myapplication.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.wf.wffrsinglecamapp;

import java.util.ArrayList;
import java.util.List;

public class EnrolledUserAdapter extends RecyclerView.Adapter<EnrolledUserAdapter.MyViewHolder>
    implements Filterable{

    private Context context;
    private List<EnrolledDatabaseObject> enrolledDatabaseObjectList;
    private List<EnrolledDatabaseObject> filteredenrolledDatabaseObjectList;
    private boolean isSwipeAvailable = false;
    private boolean isDebug = false;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView curr_image,delete_icon;
        public TextView name,image_index;
        public MyViewHolder(View view) {
            super(view);
            curr_image = (ImageView) view.findViewById(R.id.enrolled_image);
            if (!isSwipeAvailable){
                delete_icon = (ImageView) view.findViewById(R.id.delete_image);
            }
            name = (TextView) view.findViewById(R.id.enrolled_name);
            image_index = (TextView) view.findViewById(R.id.image_index);

        }
    }

    public List<EnrolledDatabaseObject> getFilteredenrolledDatabaseObjectList() {
        return filteredenrolledDatabaseObjectList;
    }

    public EnrolledUserAdapter(Context context, List<EnrolledDatabaseObject> listObjects,
                               boolean isSwipeAvailable, boolean isDebug) {
        this.enrolledDatabaseObjectList = listObjects;
        this.context = context;
        this.isSwipeAvailable = isSwipeAvailable;
        this.filteredenrolledDatabaseObjectList = enrolledDatabaseObjectList;
        this.isDebug = isDebug;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.enrolled_database_recycler_item_2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Glide.with(context).load(filteredenrolledDatabaseObjectList.get(position).getImage_path())
                .into(holder.curr_image);
        holder.name.setText(filteredenrolledDatabaseObjectList.get(position).getImage_name());
        holder.image_index.setText((position+1)+"");

        if (!isSwipeAvailable){

            Glide.with(context).load(R.drawable.close)
                    .into(holder.delete_icon);
            if (isDebug){
                holder.delete_icon.setVisibility(View.VISIBLE);
            }
            else {
                holder.delete_icon.setVisibility(View.INVISIBLE);
            }
            holder.delete_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog(position,
                            filteredenrolledDatabaseObjectList.get(position).getImage_id());
                }
            });
        }
    }


    private void showDeleteDialog(final int position,final int record_id){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("Delete Record!!");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want Delete this Record?");

        // Setting Icon to Dialog

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                // Write your code here to invoke YES event
                dialog.cancel();


            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                wffrsinglecamapp.deletePerson(record_id);
                Toast.makeText(context, "Record Deleted", Toast.LENGTH_SHORT).show();
                removeItem(position);

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public void removeItem(int position) {
        int image_id = filteredenrolledDatabaseObjectList.get(position).getImage_id();
        for (int i = 0;i<enrolledDatabaseObjectList.size();i++){
            if (enrolledDatabaseObjectList.get(i).getImage_id() == image_id){
                enrolledDatabaseObjectList.remove(i);
            }
        }
        for (int i = 0;i<filteredenrolledDatabaseObjectList.size();i++){
            if (filteredenrolledDatabaseObjectList.get(i).getImage_id() == image_id){
                filteredenrolledDatabaseObjectList.remove(i);
            }
        }
//        filteredenrolledDatabaseObjectList.remove(position);

        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void restoreItem(EnrolledDatabaseObject item, int position) {
        filteredenrolledDatabaseObjectList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredenrolledDatabaseObjectList = enrolledDatabaseObjectList;
                } else {
                    List<EnrolledDatabaseObject> filteredList = new ArrayList<>();
                    for (EnrolledDatabaseObject row : enrolledDatabaseObjectList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getImage_name().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filteredenrolledDatabaseObjectList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredenrolledDatabaseObjectList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredenrolledDatabaseObjectList = (ArrayList<EnrolledDatabaseObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        return filteredenrolledDatabaseObjectList.size();
    }
}