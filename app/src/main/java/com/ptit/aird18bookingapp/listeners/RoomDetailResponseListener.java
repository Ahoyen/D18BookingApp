package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.Room;
import com.ptit.aird18bookingapp.models.RoomDetail;
import com.ptit.aird18bookingapp.models.RoomDetailResponse;

import java.util.List;

public interface RoomDetailResponseListener {
    void didFetch(RoomDetailResponse response, String message);
    void didError(String message);
}
