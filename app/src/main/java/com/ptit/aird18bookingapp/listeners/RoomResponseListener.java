package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.Room;
import com.ptit.aird18bookingapp.models.RoomResponse;

import java.util.List;

public interface RoomResponseListener {
    void didFetch(RoomResponse response, String message);
    void didError(String message);
}
