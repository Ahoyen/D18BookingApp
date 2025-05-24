package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.UserResponse;

public interface RegisterResponseListener {
    void didFetch(UserResponse userResponse, String message);
    void didError(String message);
}
