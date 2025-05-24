package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.Response.PasswordResponse;

public interface PasswordResponseListener {
    void didFetch(PasswordResponse passwordResponse, String message);
    void didError(String message);
}
