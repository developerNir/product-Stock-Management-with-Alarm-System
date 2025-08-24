package com.example.inventorymanager.Adapter;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.Database.DatabaseHelper;
import com.example.inventorymanager.Helper.LowStockReceiver;
import com.example.inventorymanager.Model.ProductModel;
import com.example.inventorymanager.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<ProductModel> fullList;
    private List<ProductModel> filteredList;
    private OnProductClickListener listener;
    private DatabaseHelper databaseHelper;

    public interface OnProductClickListener {
        void onEdit(ProductModel product);
        void onDelete(ProductModel product);
        void onView(ProductModel product);
    }

    public ProductAdapter(Context context, List<ProductModel> list, OnProductClickListener listener) {
        this.context = context;
        this.fullList = new ArrayList<>(list);
        this.filteredList = list;
        this.listener = listener;
        this.databaseHelper = new DatabaseHelper(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductStock, tvProductQuantity, tvProductDescription;
        Button btnEdit, btnDelete;
        ImageButton btnMinus,btnPlus;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductStock = itemView.findViewById(R.id.tvProductStock);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            imageView = itemView.findViewById(R.id.ProductImage);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
        }
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        ProductModel product = filteredList.get(position);

        holder.tvProductName.setText("Name: " + product.getName());
        holder.tvProductPrice.setText("Price: " + product.getPrice());
        holder.tvProductQuantity.setText("Qty: " + product.getQuantity());
        holder.tvProductStock.setText("Stock: " + product.getStock());
        holder.tvProductDescription.setText("Description: "+product.getDescription());
        if (product.getQuantity() <= 5) {
            setLowStockAlarm(context, product.getName(), 60 * 1000);
            Toast.makeText(context, "this is Stock 5 or lass then", Toast.LENGTH_SHORT).show();
            // When updating product quantity


// Check if low stock after update
            checkLowStockAndAlert(product);

        }


        // Auto-stock status
        if (product.getQuantity() > 0) {
            holder.tvProductStock.setText("In Stock");
            holder.tvProductStock.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvProductStock.setText("Out of Stock");
            holder.tvProductStock.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Load image if you have a library (e.g., Glide or Picasso)
//        Picasso.get().load(product.getImage()).into(holder.imageView);

        byte[] imageBytes = product.getImage();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.imageView.setImageBitmap(bitmap);


        } else {
            holder.imageView.setImageResource(R.drawable.image); // fallback image
        }


        // Increase quantity
        holder.btnPlus.setOnClickListener(v -> {
            int newQty = product.getQuantity() + 1;
            product.setQuantity(newQty);
            databaseHelper.updateProductQuantity(product.getId(), newQty);
            notifyItemChanged(position);
        });

        // Decrease quantity (not below zero)
        holder.btnMinus.setOnClickListener(v -> {
            if (product.getQuantity() > 0) {
                int newQty = product.getQuantity() - 1;
                product.setQuantity(newQty);
                databaseHelper.updateProductQuantity(product.getId(), newQty);
                notifyItemChanged(position);
            }
        });

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(product));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(product));

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
                List<ProductModel> filtered = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(fullList);
                } else {
                    String query = constraint.toString().toLowerCase();
                    for (ProductModel item : fullList) {
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
                filteredList.addAll((List<ProductModel>) results.values);
                notifyDataSetChanged();
            }
        };
    }


    public void updateData(List<ProductModel> newList) {
        fullList.clear();
        fullList.addAll(newList);
        filteredList.clear();
        filteredList.addAll(newList);
        notifyDataSetChanged();
    }


//    private void setLowStockAlarm(Context context, String productName) {
//        Intent intent = new Intent(context, LowStockReceiver.class);
//        intent.putExtra("productName", productName);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                context,
//                productName.hashCode(), // unique for each product
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        // Set alarm for immediate trigger (can set future time if needed)
//        alarmManager.set(
//                AlarmManager.RTC_WAKEUP,
//                System.currentTimeMillis() + 1000, // 1 second delay
//
//                pendingIntent
//        );
//    }





    private void setLowStockAlarm(Context context, String productName, long intervalMillis) {
        Intent intent = new Intent(context, LowStockReceiver.class);
        intent.putExtra("productName", productName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                productName.hashCode(), // unique ID for each product
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Use setRepeating to keep ringing forever
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 60 * 1000, // first after 1 min
                intervalMillis,                          // repeat every X mins/hours
                pendingIntent
        );
    }


//    private void setLowStockAlarm(Context context, String productName, long intervalMillis) {
//        Intent intent = new Intent(context, LowStockReceiver.class);
//        intent.putExtra("productName", productName);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                context,
//                productName.hashCode(), // unique ID for each product
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        // Start 1 minute from now and repeat
//        alarmManager.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                System.currentTimeMillis() + 60 * 1000, // first trigger after 1 minute
//                intervalMillis,                          // repeat interval
//                pendingIntent
//        );
//
//
//    }


    private void callArlarmwithNotification(ProductModel product){
        MediaPlayer mp = MediaPlayer.create(context, R.raw.low_stock_alert); // put mp3/wav in res/raw
        mp.start();

        // Optional: Show notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "low_stock_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Low Stock Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_warning) // your warning icon
                .setContentTitle("Low Stock Alert")
                .setContentText(product.getName() + " stock is low (" + product.getQuantity() + ")")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(product.getId(), builder.build());


    }


    @SuppressLint({"ObsoleteSdkInt", "ScheduleExactAlarm"})
    private void checkLowStockAndAlert(ProductModel product) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        int threshold = prefs.getInt("low_stock_threshold", 5); // default 5
        int freq = prefs.getInt("low_stock_frequency", 0); // 0=once, 1=hourly, 2=daily, 3=10 min

        if (product.getQuantity() <= threshold) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, LowStockReceiver.class);
            intent.putExtra("productName", product.getName());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    product.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );



            long interval;

            switch (freq) {
                case 1: // Every Minute
                    interval = 60 * 1000; // 1 minute in milliseconds
                    callArlarmwithNotification(product);
                    break;
                case 2: // Every 10 Minutes
                    interval = 10 * 60 * 1000;
                    callArlarmwithNotification(product);
                    break;
                case 3: // Every Hour
                    interval = AlarmManager.INTERVAL_HOUR;
                    callArlarmwithNotification(product);
                    break;
                case 4: // Every Day
                    interval = AlarmManager.INTERVAL_DAY;
                    callArlarmwithNotification(product);
                    break;
                default: // Once
                    interval = 0;
                    callArlarmwithNotification(product);
                    break;
            }

            if (interval > 0) {
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(),
                        interval,
                        pendingIntent
                );
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis(),
                            pendingIntent
                    );
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis(),
                            pendingIntent
                    );
                }
            }


//            long interval;
//
//            switch (freq) {
//                case 1: // Every Hour
//                    interval = AlarmManager.INTERVAL_HOUR;
//                    break;
//                case 2: // Every Day
//                    interval = AlarmManager.INTERVAL_DAY;
//                    break;
//                case 3: // Every 10 Minutes
//                    interval = 10 * 60 * 1000;
//                    break;
//                case 4: // Every 1 Minute
//                    interval = 60 * 1000;
//                    break;
//                default: // Once
//                    interval = 0;
//                    break;
//            }
//
//            if (product.getQuantity() <= threshold) {
//                if (interval > 0) {
//                    setLowStockAlarm(context, product.getName(), interval);
//                } else {
//                    // fire once only
//                    setLowStockAlarm(context, product.getName(), 0);
//                }
//            }



        }
    }


//    @SuppressLint({"ObsoleteSdkInt", "ScheduleExactAlarm"})
//    private void checkLowStockAndAlert(ProductModel product) {
//        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
//        int threshold = prefs.getInt("low_stock_threshold", 5);
//        int freq = prefs.getInt("low_stock_frequency", 0);
//
//        if (product.getQuantity() <= threshold) {
//            long interval;
//
//            switch (freq) {
//                case 1: // Every Hour
//                    interval = AlarmManager.INTERVAL_HOUR;
//                    break;
//                case 2: // Every Day
//                    interval = AlarmManager.INTERVAL_DAY;
//                    break;
//                case 3: // Every 10 Minutes
//                    interval = 10 * 60 * 1000;
//                    break;
//                case 4: // Every 1 Minute
//                    interval = 60 * 1000;
//                    break;
//                default: // Once
//                    interval = 0;
//                    break;
//            }
//
//            if (interval > 0) {
//                // schedule repeating forever
//                setLowStockAlarm(context, product.getName(), interval);
//            } else {
//                // schedule only once
//                setLowStockAlarm(context, product.getName(), 0);
//            }
//        }
//    }


//    @SuppressLint("ScheduleExactAlarm")
//    private void scheduleLowStockAlarm(Context context, String productName, long intervalMillis) {
//        Intent intent = new Intent(context, LowStockReceiver.class);
//        intent.putExtra("productName", productName);
//        intent.putExtra("intervalMillis", intervalMillis);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                context,
//                productName.hashCode(),
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                System.currentTimeMillis() + intervalMillis,
//                pendingIntent
//        );
//    }






}
