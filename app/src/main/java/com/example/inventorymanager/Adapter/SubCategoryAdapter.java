package com.example.inventorymanager.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.Model.ProductModel;
import com.example.inventorymanager.Model.SubCategory;
import com.example.inventorymanager.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> implements Filterable {
    private List<SubCategory> subCategories;
    private List<SubCategory> filteredList; // For filtering
    private Context context;
    private OnItemActionListener listener;

    public SubCategoryAdapter(Context context, List<SubCategory> subCategories, OnItemActionListener listener) {
        this.context = context;
        this.subCategories = subCategories;
        this.filteredList = new ArrayList<>(subCategories); // copy
        this.listener = listener;
    }

    public interface OnItemActionListener {
        void onEdit(SubCategory subCategory);
        void onDelete(SubCategory subCategory);
        void changeActivity(SubCategory subCategory);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView edit, delete;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sub_category_name);
            edit = itemView.findViewById(R.id.btn_edit_subcat);
            delete = itemView.findViewById(R.id.btn_delete_subcat);
            layout = itemView.findViewById(R.id.SubCetaLayoutItem);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_sub_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SubCategory s = filteredList.get(position); // use filteredList
        holder.name.setText(s.getName());
        holder.edit.setOnClickListener(v -> listener.onEdit(s));
        holder.delete.setOnClickListener(v -> listener.onDelete(s));

        holder.layout.setOnClickListener(v -> {
            Toast.makeText(context, "id:" + s.getId(), Toast.LENGTH_SHORT).show();
            listener.changeActivity(s);
        });
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<SubCategory> filtered = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(subCategories); // original list
                } else {
                    String query = constraint.toString().toLowerCase().trim();
                    for (SubCategory s : subCategories) {
                        if (s.getName().toLowerCase().contains(query)) { // contains, not just prefix
                            filtered.add(s);
                        }
                    }
                    // Sort alphabetically
                    Collections.sort(filtered, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                }

                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                filteredList.addAll((List<SubCategory>) results.values);
                notifyDataSetChanged();
            }
        };
    }


    public void updateList(List<SubCategory> newList) {
        subCategories = newList;
        filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

}
