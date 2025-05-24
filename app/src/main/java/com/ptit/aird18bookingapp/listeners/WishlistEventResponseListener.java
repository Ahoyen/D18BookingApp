package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.WishlistResponse;

public interface WishlistEventResponseListener {
    void didFetch(WishlistResponse response, String message);
    void didError(String message);
}
