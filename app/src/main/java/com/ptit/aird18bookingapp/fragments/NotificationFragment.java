package com.ptit.aird18bookingapp.fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ptit.aird18bookingapp.R;

import com.ptit.aird18bookingapp.adapters.HistoryBookingAdapter;
import com.ptit.aird18bookingapp.adapters.PostAdapter;
import com.ptit.aird18bookingapp.listeners.BookingResponseDetailListener;
import com.ptit.aird18bookingapp.models.BookingResponseDetail;


import com.ptit.aird18bookingapp.listeners.BookingResponseDetailListener;
import com.ptit.aird18bookingapp.models.BookingResponseDetail;
import com.ptit.aird18bookingapp.models.Notification;
import com.ptit.aird18bookingapp.repository.RequestManager;
import com.ptit.aird18bookingapp.repository.notifications.ApiClient;
import com.ptit.aird18bookingapp.repository.notifications.ApiService;
import com.ptit.aird18bookingapp.utils.Constants;
import com.ptit.aird18bookingapp.utils.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private PostAdapter postAdapter;
    private RecyclerView recyclerView;
    private LinearLayout layoutNotFound;
    private PreferenceManager preferenceManager;
    private String lastShownNotification = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        preferenceManager = new PreferenceManager(getContext());
        recyclerView = view.findViewById(R.id.recycle_notification_list);
        layoutNotFound = view.findViewById(R.id.lvl_notfound_notification);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        getToken();
        loadNotifications();

        return view;
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
    }

    private void loadNotifications() {
        DatabaseReference notificationsRef = FirebaseDatabase
                .getInstance("https://aird18bookingapp-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Notifications");

        notificationsRef.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Notification> notifications = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notification notification = snapshot.getValue(Notification.class);
                    if (notification != null) {
                        notifications.add(notification);
                    }
                }

                Collections.reverse(notifications);

                if (notifications.isEmpty()) {
                    layoutNotFound.setVisibility(View.VISIBLE);
                } else {
                    layoutNotFound.setVisibility(View.GONE);
                    postAdapter = new PostAdapter(notifications);
                    recyclerView.setAdapter(postAdapter);

                    Notification latest = notifications.get(0);
                    if (latest.content != null && (lastShownNotification == null || !lastShownNotification.equals(latest.content))) {
                        showSystemNotification(latest.content);
                        lastShownNotification = latest.content;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("NotificationFragment", "Database error: " + error.getMessage());
            }
        });
    }

    private void showSystemNotification(String message) {
        Context context = requireContext();
        String channelId = "notify_message";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Thông báo",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kênh thông báo từ Firebase Database");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo) // ensure this icon exists
                .setContentTitle("AirD18 Booking")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}

