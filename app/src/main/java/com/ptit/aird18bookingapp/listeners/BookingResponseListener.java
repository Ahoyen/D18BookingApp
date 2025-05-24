package com.ptit.aird18bookingapp.listeners;


import com.ptit.aird18bookingapp.models.Response.BookingResponse;

public interface BookingResponseListener {
    void didFetch(BookingResponse response, String message);
    void didError(String message);
}
