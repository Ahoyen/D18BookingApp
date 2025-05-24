package com.ptit.aird18bookingapp.listeners;

import com.ptit.aird18bookingapp.models.Category;
import com.ptit.aird18bookingapp.models.CategoryResponse;

import java.util.List;

public interface CategoryResponseListener {
    void didFetch(CategoryResponse response, String message);
    void didError(String message);
}
