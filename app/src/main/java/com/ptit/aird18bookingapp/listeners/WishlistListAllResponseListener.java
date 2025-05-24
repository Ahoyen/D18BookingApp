package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.WishlistResponseData;

public interface WishlistListAllResponseListener {
    void didFetch(WishlistResponseData response, String message);
    void didError(String message);
}
