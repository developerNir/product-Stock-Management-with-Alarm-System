package com.example.inventorymanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.Model.MainCategory;
import com.example.inventorymanager.Model.ProductModel;
import com.example.inventorymanager.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<MainCategory> fullList;
    private List<MainCategory> filteredList;
    private OnMainCategoryClickListener listener;

    public interface OnMainCategoryClickListener {
        void onEdit(MainCategory category);
        void onDelete(MainCategory category);
        void activityChange(MainCategory category);
    }

    public MainCategoryAdapter(Context context, List<MainCategory> list, OnMainCategoryClickListener listener) {
        this.context = context;
        this.fullList = new ArrayList<>(list);
        this.filteredList = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageButton edit, delete;
        LinearLayout cardView;


        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            edit = itemView.findViewById(R.id.btnEdit);
            delete = itemView.findViewById(R.id.btnDelete);
            cardView = itemView.findViewById(R.id.MainCatagoryViewCard);
        }
    }

    @Override
    public MainCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MainCategory category = filteredList.get(position);
        holder.name.setText(category.getName());
        holder.edit.setOnClickListener(v -> listener.onEdit(category));
        holder.delete.setOnClickListener(v -> listener.onDelete(category));
        holder.cardView.setOnClickListener(v -> {
            listener.activityChange(category);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }




    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<MainCategory> filtered = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(fullList);
                } else {
                    String query = constraint.toString().toLowerCase();
                    for (MainCategory item : fullList) {
                        if (item.getName().toLowerCase().startsWith(query)) { // only prefix
                            filtered.add(item);
                        }
                    }
                    // Sort results alphabetically
                    Collections.sort(filtered, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                }
                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                filteredList.addAll((List<MainCategory>) results.values);
                notifyDataSetChanged();
            }
        };
    }






}