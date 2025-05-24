package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.BookingResponseDetail;

public interface BookingResponseDetailListener {
        void didFetch(BookingResponseDetail response, String message);
        void didError(String message);
}
