package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.UserResponse;

public interface LoginResponseListener {
    void didFetch(UserResponse userResponse, String message, String cookie);
    void didError(String message);
}
