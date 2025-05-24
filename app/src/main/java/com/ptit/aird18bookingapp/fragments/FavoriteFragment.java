package com.ptit.aird18bookingapp.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

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
import com.ptit.aird18bookingapp.activities.DetailsBookingActivity;
import com.ptit.aird18bookingapp.adapters.BookingAdapter;
import com.ptit.aird18bookingapp.adapters.WishlistAdapter;
import com.ptit.aird18bookingapp.listeners.WishlistClicksListener;
import com.ptit.aird18bookingapp.listeners.WishlistEventResponseListener;
import com.ptit.aird18bookingapp.listeners.WishlistListAllResponseListener;
import com.ptit.aird18bookingapp.listeners.WishlistListResponseListener;
import com.ptit.aird18bookingapp.models.WishlistListIdResponse;
import com.ptit.aird18bookingapp.models.WishlistResponse;
import com.ptit.aird18bookingapp.models.WishlistResponseData;
import com.ptit.aird18bookingapp.repository.RequestManager;
import com.ptit.aird18bookingapp.utils.Constants;
import com.ptit.aird18bookingapp.utils.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class FavoriteFragment extends Fragment {

    RequestManager manager;
    RecyclerView recyclerView;
    WishlistAdapter adapter;
    AlertDialog dialog;
    private PreferenceManager preferenceManager;
    String cookie;
    LinearLayout lvl_notfound;
    List<Integer> listWishlistId = new ArrayList<>();
    BottomNavigationView navBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        getViews(view);
        manager = new RequestManager(getContext());
        preferenceManager = new PreferenceManager(getContext());
        cookie = preferenceManager.getString(Constants.KEY_COOKIE);
        navBar  = getActivity().findViewById(R.id.bottom_navigation);

        manager.getWishlists(wishlistListResponseListener, cookie);

        manager.getAllWishlists(wishlistListAllResponseListener, cookie);
        dialog.show();

        return view;
    }

    private final WishlistListResponseListener wishlistListResponseListener = new WishlistListResponseListener() {
        @Override
        public void didFetch(WishlistListIdResponse response, String message) {
            listWishlistId = response.data;
        }

        @Override
        public void didError(String message) {
            Log.d("XXX", message);
        }
    };

    private final WishlistListAllResponseListener wishlistListAllResponseListener = new WishlistListAllResponseListener() {
        @Override
        public void didFetch(WishlistResponseData response, String message) {
            dialog.dismiss();
            if (!response.data.isEmpty()) {
                lvl_notfound.setVisibility(View.INVISIBLE);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                adapter = new WishlistAdapter(getContext(), response.data, wishlistClicksListener);
                recyclerView.setAdapter(adapter);
            } else {
                lvl_notfound.setVisibility(View.VISIBLE);            }
        }

        @Override
        public void didError(String message) {

        }
    };

    WishlistClicksListener wishlistClicksListener = new WishlistClicksListener() {
        @Override
        public void onWishlistClicked(String id) {
            startActivity(new Intent(getActivity(), DetailsBookingActivity.class)
                    .putExtra("id", id));
        }

        @Override
        public void onDeleteWishlistClicked(String id) {
            manager.getRemoveWishlist(wishlistEventResponseListener, id, cookie);
            listWishlistId.remove(Integer.valueOf(id));
            navBar.getOrCreateBadge(R.id.favorite)
                    .setNumber(listWishlistId.size());

        }

    };

    private WishlistEventResponseListener wishlistEventResponseListener = new WishlistEventResponseListener() {
        @Override
        public void didFetch(WishlistResponse response, String message) {
            manager.getAllWishlists(wishlistListAllResponseListener, cookie);
            adapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "Action " + response.data, Toast.LENGTH_LONG).show();
        }

        @Override
        public void didError(String message) {
            Toast.makeText(getActivity(), "Error " + message, Toast.LENGTH_LONG).show();
        }
    };

    private void getViews(View view) {
        recyclerView = view.findViewById(R.id.recycle_wishlist_list);
        lvl_notfound = view.findViewById(R.id.lvl_notfound);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.Custom).build();

    }
}