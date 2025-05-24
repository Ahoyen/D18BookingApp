package com.ptit.aird18bookingapp.models.Response;

import com.ptit.aird18bookingapp.models.DTO.BookingDetailDTO;
import com.ptit.aird18bookingapp.models.User;

public class BookingResponse {
    public boolean success;
    public BookingDetailDTO data;
    public Object error;
}
