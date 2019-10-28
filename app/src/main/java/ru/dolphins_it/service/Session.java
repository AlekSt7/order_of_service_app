package ru.dolphins_it.service;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.dolphins_it.service.Api.Api;
import ru.dolphins_it.service.Api.ApiService;

public class Session {

    private Api api;
    private Gson gson;
    private Retrofit retrofit;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;

    public Session(){
    }

    public void inicialize(Context context){ //Инициализирует сессию

        gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://37.204.111.194/dolphins/") // Базовый URL
                .addConverterFactory(GsonConverterFactory.create(gson)) // Конвертер JSON
                .build();

        dataBaseHelper = new DataBaseHelper(context);

        db = dataBaseHelper.getWritableDatabase();

        userCursor = db.query(dataBaseHelper.TABLE_USER, new String[]{dataBaseHelper.USER_TOKEN},
                null, null, null, null, null);

    }

    public String token_status;

    public boolean session_exist() { //Проверяет существованиие токена

        if (DatabaseUtils.longForQuery(db, "SELECT COUNT(" + dataBaseHelper.USER_TOKEN + ") FROM " + dataBaseHelper.TABLE_USER, null) > 0) { //Проверяем существование токена в базе данных
            if (userCursor.moveToFirst()) {
                api = retrofit.create(Api.class);
                final Call<List<ApiService>> call = api.sessionUnset("session_exist", userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.USER_TOKEN))); //Запрос на удаление токена

                        Thread thread = new Thread() {
                            @Override
                            public void run() {

                                try {
                                    token_status = call.execute().body().get(0).getTokenStatus();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        };

                thread.setPriority(4);
                thread.start();
                while (thread.isAlive()){if(!thread.isAlive()){break;}} //В цикле ждём завершения запроса, костыль, ток не бейте

            }

        } else {
            return false;
        }

            if (new String("yes").equals(token_status)) {
                return true;
            } else {
                return false;
            }
    }



    public void session_unset(){ //Удаляет сессию

        if (userCursor.moveToFirst()) {

            api = retrofit.create(Api.class);
            Call<List<ApiService>> call = api.sessionUnset("session_unset", userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.USER_TOKEN))); //Запрос на удаление токена

            call.enqueue(new Callback<List<ApiService>>() {

                @Override
                public void onResponse(Call<List<ApiService>> call, Response<List<ApiService>> response) {

                    if (!response.isSuccessful()) {

                        String tok = null;

                        userCursor = db.query(dataBaseHelper.TABLE_USER, new String[]{dataBaseHelper.USER_TOKEN},
                                null, null, null, null, null);

                        if (userCursor.moveToFirst()) {
                            tok = userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.USER_TOKEN));
                        }

                        db.delete(dataBaseHelper.TABLE_USER, dataBaseHelper.USER_TOKEN + "= ?", new String[]{tok});

                    }
                }

                @Override
                public void onFailure(Call<List<ApiService>> call, Throwable t) { //Запрос провалился

                }

            });

        }

    }

    public String getToken(){
        userCursor.moveToFirst();
        return userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.USER_TOKEN));
    }

    String access;

    public boolean getAccessRight(){
        userCursor.moveToFirst();
        api = retrofit.create(Api.class);
        final Call<List<ApiService>> call = api.sessionUnset("get_access_right", userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.USER_TOKEN)));

        Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    access = call.execute().body().get(0).getAccess_right();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };

        thread.setPriority(5);
        thread.start();
        while (thread.isAlive()){if(!thread.isAlive()){break;}}

        if (new String("1").equals(access)) {
            return true;
        } else {
            return false;
        }

    }

}
