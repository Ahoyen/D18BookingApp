package com.ptit.aird18bookingapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ptit.aird18bookingapp.R;
import com.ptit.aird18bookingapp.listeners.BookingClicksListener;
import com.ptit.aird18bookingapp.listeners.WishlistClicksListener;
import com.ptit.aird18bookingapp.models.Room;
import com.ptit.aird18bookingapp.models.Wishlist;
import com.ptit.aird18bookingapp.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistViewHolder> {

    Context context;
    List<Wishlist> list;
    WishlistClicksListener listener;

    public WishlistAdapter(Context context, List<Wishlist> list, WishlistClicksListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WishlistViewHolder(LayoutInflater.from(context).inflate(R.layout.list_wishlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        Picasso.get().load(Constants.BASE_URL + list.get(position).images).placeholder(R.drawable.playholder).into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWishlistClicked(String.valueOf(list.get(holder.getAdapterPosition()).id));
            }
        });

        holder.image_remove_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeleteWishlistClicked(String.valueOf(list.get(holder.getAdapterPosition()).id));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItemById(int id) {
        if (list == null) {
            Log.e("WishlistAdapter", "List is null");
            return;
        }
        int position = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id == id) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
        } else {
            Log.e("WishlistAdapter", "Item id " + id + " not found in list");
        }
    }

}

class WishlistViewHolder extends RecyclerView.ViewHolder {
    CardView cardView;
    ImageView imageView, image_remove_wishlist;

    public WishlistViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.cardViewWishlist);
        imageView = itemView.findViewById(R.id.image_wishlist);
        image_remove_wishlist = itemView.findViewById(R.id.image_remove_wishlist);
    }
}
