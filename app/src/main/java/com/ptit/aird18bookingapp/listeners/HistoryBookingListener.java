package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.BookedRoom;

public interface HistoryBookingListener {
    void onBookingDetailClicked(String id);
    void onBookingGetInvoiceClicked(BookedRoom bookedRoom);
}
