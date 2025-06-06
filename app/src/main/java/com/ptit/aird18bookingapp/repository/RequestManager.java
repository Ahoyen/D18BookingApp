package com.ptit.aird18bookingapp.repository;

import android.content.Context;
import android.util.Log;

import com.ptit.aird18bookingapp.listeners.BookingResponseDetailListener;
import com.ptit.aird18bookingapp.listeners.BookingResponseListener;
import com.ptit.aird18bookingapp.listeners.CategoryResponseListener;
import com.ptit.aird18bookingapp.listeners.LoginResponseListener;
import com.ptit.aird18bookingapp.listeners.PasswordResponseListener;
import com.ptit.aird18bookingapp.listeners.RegisterResponseListener;
import com.ptit.aird18bookingapp.listeners.RoomDetailResponseListener;
import com.ptit.aird18bookingapp.listeners.RoomResponseListener;
import com.ptit.aird18bookingapp.listeners.UserProfileResponseListener;
import com.ptit.aird18bookingapp.listeners.WishlistEventResponseListener;
import com.ptit.aird18bookingapp.listeners.WishlistListAllResponseListener;
import com.ptit.aird18bookingapp.listeners.WishlistListResponseListener;
import com.ptit.aird18bookingapp.models.BookingResponseDetail;
import com.ptit.aird18bookingapp.models.CategoryResponse;
import com.ptit.aird18bookingapp.models.ResetPass;
import com.ptit.aird18bookingapp.models.Response.BookingResponse;
import com.ptit.aird18bookingapp.models.Response.PasswordResponse;
import com.ptit.aird18bookingapp.models.RoomDetailResponse;
import com.ptit.aird18bookingapp.models.RoomResponse;
import com.ptit.aird18bookingapp.models.UserLogin;
import com.ptit.aird18bookingapp.models.UserRegister;
import com.ptit.aird18bookingapp.models.UserResponse;
import com.ptit.aird18bookingapp.models.WishlistListIdResponse;
import com.ptit.aird18bookingapp.models.WishlistResponse;
import com.ptit.aird18bookingapp.models.WishlistResponseData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestManager {
    Context context;

    public RequestManager(Context context) {
        this.context = context;
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://solodev-serverapi-cc3d02f75359.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public void getCategories(CategoryResponseListener listener) {
        CallCategories callCategories = retrofit.create(CallCategories.class);
        Call<CategoryResponse> call = callCategories.categoryResponseCall();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getRoomByCategory(RoomResponseListener listener, String  id) {
        CallRoomByCategory callRandomRecipes = retrofit.create(CallRoomByCategory.class);
        Call<RoomResponse> call = callRandomRecipes.roomResponseCall(id);
        call.enqueue(new Callback<RoomResponse>() {
            @Override
            public void onResponse(Call<RoomResponse> call, Response<RoomResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }

                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RoomResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getRoomByQuery(RoomResponseListener listener, String  id, String search) {
        CallRoomByQuery callRandomRecipes = retrofit.create(CallRoomByQuery.class);
        Call<RoomResponse> call = callRandomRecipes.roomResponseCallQuery(id, search);
        call.enqueue(new Callback<RoomResponse>() {
            @Override
            public void onResponse(Call<RoomResponse> call, Response<RoomResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }

                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RoomResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getRoomDetail(RoomDetailResponseListener listener, int id) {
        CallRoomDetail callRoomDetail = retrofit.create(CallRoomDetail.class);
        Call<RoomDetailResponse> call = callRoomDetail.roomDetailResponseCall(id);
        call.enqueue(new Callback<RoomDetailResponse>() {
            @Override
            public void onResponse(Call<RoomDetailResponse> call, Response<RoomDetailResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RoomDetailResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getBooking(BookingResponseListener listener, int roomid, String checkin, String checkout, String clientMessage, int numberOfDays, String cookie) {
        CallBooking callBooking = retrofit.create(CallBooking.class);
        Call<BookingResponse> call = callBooking.bookingResponseCall(roomid, checkin, checkout, numberOfDays, clientMessage, cookie);
        call.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getLogin(LoginResponseListener listener, UserLogin userLogin) {
        CallLogin callLogin = retrofit.create(CallLogin.class);
        Call<UserResponse> call = callLogin.loginUser(userLogin);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.code() == 400) {
                    if (!response.isSuccessful()) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            JSONObject errorMessage = jsonObject.getJSONObject("error");
                            listener.didError(errorMessage.getString("message"));
                        } catch (JSONException e) {
                            listener.didError(e.toString());
                            e.printStackTrace();
                        } catch (IOException e) {
                            listener.didError(e.toString());
                            e.printStackTrace();
                        }
                    }
                    return;
                }
                List<String> Cookielist = response.headers().values("Set-Cookie");
                String jsessionid = (Cookielist.get(0).split(";"))[0];


                Log.d("Header", jsessionid);
                listener.didFetch(response.body(), response.message(), jsessionid);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getRegister(RegisterResponseListener listener, UserRegister userRegister) {
        CallRegister callRegister = retrofit.create(CallRegister.class);
        Call<UserResponse> call = callRegister.registerUser(userRegister);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.code() == 400) {
                    if (!response.isSuccessful()) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            listener.didError(jsonObject.getString("error"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

               // System.out.println(response.body().toString());
                listener.didFetch(response.body(), response.message());

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                listener.didError("Error0: " + t.getMessage());
            }
        });
    }

    public void getWishlists(WishlistListResponseListener listener, String cookie) {
        CallWishListData callWishListData = retrofit.create(CallWishListData.class);
        Call<WishlistListIdResponse> call = callWishListData.getWishListResponseCall(cookie);
        call.enqueue(new Callback<WishlistListIdResponse>() {
            @Override
            public void onResponse(Call<WishlistListIdResponse> call, Response<WishlistListIdResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d("XXXX1", response.errorBody() + "hfdh");
                    listener.didError(response.message());
                    return;
                }
                Log.d("XXXX", response.body().success + "");
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<WishlistListIdResponse> call, Throwable t) {
                Log.d("XXXX2", t.getMessage() + "");
                listener.didError(t.getMessage());
            }
        });
    }

    public void getAddWishlist(WishlistEventResponseListener listener, String id, String cookie) {
        CallAddWishList callAddWishlist = retrofit.create(CallAddWishList.class);
        Call<WishlistResponse> call = callAddWishlist.addWishListResponseCall(id, cookie);
        call.enqueue(new Callback<WishlistResponse>() {
            @Override
            public void onResponse(Call<WishlistResponse> call, Response<WishlistResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<WishlistResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getRemoveWishlist(WishlistEventResponseListener listener, String id, String cookie) {
        CallRemoveWishList callAddWishlist = retrofit.create(CallRemoveWishList.class);
        Call<WishlistResponse> call = callAddWishlist.removeWishListResponseCall(id, cookie);
        call.enqueue(new Callback<WishlistResponse>() {
            @Override
            public void onResponse(Call<WishlistResponse> call, Response<WishlistResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<WishlistResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getAllWishlists(WishlistListAllResponseListener listener, String cookie) {
        CallAllWishList callAllWishList = retrofit.create(CallAllWishList.class);
        Call<WishlistResponseData> call = callAllWishList.getWishListResponseCall(cookie);
        call.enqueue(new Callback<WishlistResponseData>() {
            @Override
            public void onResponse(Call<WishlistResponseData> call, Response<WishlistResponseData> response) {
                if (!response.isSuccessful()) {
                    Log.d("XXXX1", response.errorBody() + "hfdh");
                    listener.didError(response.message());
                    return;
                }
                Log.d("XXXX", response.body().success + "");
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<WishlistResponseData> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getBookingResponseDetail(BookingResponseDetailListener listener, String cookie) {
        CallBookingDetail callBookingDetail = retrofit.create(CallBookingDetail.class);
        Call<BookingResponseDetail> call = callBookingDetail.bookingResponseCall(cookie);
        call.enqueue(new Callback<BookingResponseDetail>() {
            @Override
            public void onResponse(Call<BookingResponseDetail> call, Response<BookingResponseDetail> response) {
                if (!response.isSuccessful()) {
                    Log.d("XXXX1", response.errorBody() + "hfdh");
                    listener.didError(response.message());
                    return;
                }
                Log.d("XXXX", response.body().success + "");
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<BookingResponseDetail> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void resetPassword(PasswordResponseListener listener, String email, String password) {
        CallResetPassword callResetPassword = retrofit.create(CallResetPassword.class);
        Log.d("BBBBB", email + "/" + password);
        Call<PasswordResponse> call = callResetPassword.resetPassword(new ResetPass(email, password, password));
        call.enqueue(new Callback<PasswordResponse>() {
            @Override
            public void onResponse(Call<PasswordResponse> call, Response<PasswordResponse> response) {

                if (response.code() == 400) {
                    if (!response.isSuccessful()) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            //JSONObject errorMessage = jsonObject.getJSONObject("error");
                            listener.didError(jsonObject.getString("error"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

//                System.out.println(response.body().toString());
//                listener.didFetch(response.body(), response.message());
//
//
//                if (!response.isSuccessful()) {
//                    listener.didError(response.message());
//                    return;
//                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<PasswordResponse> call, Throwable t) {
                Log.d("FFFF", "Fail");
                listener.didError(t.getMessage());
            }
        });
    }
    public void getUserProfile(UserProfileResponseListener listener, String cookie) {
        CallUserProfile callUserProfile = retrofit.create(CallUserProfile.class);
        Call<UserResponse> call = callUserProfile.getUserProfile(cookie);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private interface CallBookingDetail {
        @Headers({"Content-Type: application/json"})
        @GET("/api/user/booked-rooms?query=")
        Call<BookingResponseDetail> bookingResponseCall(@Header("Cookie") String cookie);
    }

    private interface CallCategories {
        @Headers({"Content-Type: application/json"})
        @GET("/api/categories")
        Call<CategoryResponse> categoryResponseCall();
    }

    private interface CallAllWishList {
        @Headers({"Content-Type: application/json"})
        @GET("/api/user/wishlists")
        Call<WishlistResponseData> getWishListResponseCall(@Header("Cookie") String cookie);
    }

    private interface CallWishListData {
        @Headers({"Content-Type: application/json"})
        @GET("/api/user/wishlists/ids")
        Call<WishlistListIdResponse> getWishListResponseCall(@Header("Cookie") String cookie);
    }

    private interface CallAddWishList {
        @Headers({"Content-Type: application/json"})
        @PUT("/api/user/add-to-favoritelists/{id}")
        Call<WishlistResponse> addWishListResponseCall(@Path("id") String id,
                                                       @Header("Cookie") String cookie);
    }

    private interface CallRemoveWishList {
        @Headers({"Content-Type: application/json"})
        @PUT("/api/user/remove-from-favoritelists/{id}")
        Call<WishlistResponse> removeWishListResponseCall(@Path("id") String id,
                                                          @Header("Cookie") String cookie);
    }

    private interface CallLogin {
        @POST("/api/auth/login")
        Call<UserResponse> loginUser(@Body UserLogin userLogin);
    }

    private interface CallRegister {
        @POST("/api/auth/register")
        Call<UserResponse> registerUser(@Body UserRegister userRegister);
    }

    private interface CallRoomByCategory {
        @GET("/api/rooms")
        Call<RoomResponse> roomResponseCall(@Query("categoryId") String  id);
    }

    private interface CallRoomByQuery {
        @GET("/api/rooms")
        Call<RoomResponse> roomResponseCallQuery(@Query("categoryId") String  id,
                                                 @Query("query") String query);
    }

    private interface CallResetPassword {
        @Headers({"Content-Type: application/json"})
        @PUT("/api/auth/reset-password")
        Call<PasswordResponse> resetPassword(@Body ResetPass resetPass);
    }

    private interface CallRoomDetail {
        @GET("/api/rooms/{id}")
        Call<RoomDetailResponse> roomDetailResponseCall(@Path("id") int id);
    }

    private interface CallBooking {
        @Headers({"Content-Type: application/json"})
        @GET("api/booking/{roomid}/create")
        Call<BookingResponse> bookingResponseCall(@Path("roomid") int roomid,
                                                  @Query("checkin") String checkin,
                                                  @Query("checkout") String checkout,
                                                  @Query("numberOfDays") int numberOfDays,
                                                  @Query("clientMessage") String clientMessage,
                                                  @Header("Cookie") String cookie);
    }

    private interface CallUserProfile {
        @Headers({"Content-Type: application/json"})
        @GET("/api/user/profile")
        Call<UserResponse> getUserProfile(@Header("Cookie") String cookie);
    }
}
