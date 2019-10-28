package ru.dolphins_it.service.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public interface Api { //Интерфейс, описывающий методы Api

    //Запрос на проверку валидности sms-кода и создание токена
    @POST("api.php")
    @FormUrlEncoded
    Call<List<ApiService>> getToken(@Field("api_method") String api_method, @Field("sms_code") String sms_code, @Field("phone_number") String phone_number);

    //Запрос на отправку sms-кода
    @POST("api.php")
    @FormUrlEncoded
    Call<List<ApiService>> getSmsCode(@Field("api_method") String api_method, @Field("phone_number") String phone_number);

    //Запрос на удаление сессии
    @POST("api.php")
    @FormUrlEncoded
    Call<List<ApiService>> sessionUnset(@Field("api_method") String api_method, @Field("token") String token);

    //Запрос на получение данных пользователя
    @POST("api.php")
    @FormUrlEncoded
    Call<List<ApiService>> getUserData(@Field("api_method") String api_method, @Field("token") String token);

    //Запрос на обновление данных пользователя
    @POST("api.php")
    @FormUrlEncoded
    Call<Void> userDataUpdate(@Field("api_method") String api_method, @Field("token") String token, @Field("user_data") String user_data);

    //Запрос на установку sms-рассылки
    @POST("api.php")
    @FormUrlEncoded
    Call<Void> setMailing(@Field("api_method") String api_method, @Field("token") String token, @Field("mailing") String mailing);

    //Запрос на добавление заказа в базу
    @POST("api.php")
    @FormUrlEncoded
    Call<Void> addOrder(@Field("api_method") String api_method, @Field("token") String token, @Field("order") String body_order, @Field("e_ocher") String body_e, @Field("signage") String body_singnage);

    //Запрос на получение заявоок пользователя
    @POST("api.php")
    @FormUrlEncoded
    Call<List<ApiService>> getUserOrder(@Field("api_method") String api_method, @Field("token") String token);

    //Запрос на получение всех заявок
    @POST("api.php")
    @FormUrlEncoded
    Call<List<ApiService>> getOrder(@Field("api_method") String api_method);

    //Запрос на получение полной заявки
    @POST("api.php")
    @FormUrlEncoded
    Call<List<OrderApi>> getFullOrder(@Field("api_method") String api_method, @Field("order_id") String order_id);

    //Запрос на получение полной заявки
    @POST("api.php")
    @FormUrlEncoded
    Call<Void> setOrderStatus(@Field("api_method") String api_method, @Field("order_id") String order_id, @Field("order_status") String order_status, @Field("comment") String comment);
}

