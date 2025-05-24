package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.WishlistListIdResponse;

public interface WishlistListResponseListener {
    void didFetch(WishlistListIdResponse response, String message);
    void didError(String message);
}
