package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.UserResponse;

public interface UserProfileResponseListener {
    void didFetch(UserResponse response, String message);
    void didError(String message);
}
