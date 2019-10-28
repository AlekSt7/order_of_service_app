package ru.dolphins_it.service.Auth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.dolphins_it.service.Api.Api;
import ru.dolphins_it.service.Api.ApiService;
import ru.dolphins_it.service.DataBaseHelper;
import ru.dolphins_it.service.MainActivity;
import ru.dolphins_it.service.Manifest;
import ru.dolphins_it.service.R;
import ru.dolphins_it.service.Session;

public class AuthActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Session session = new Session();
    protected boolean session_status;
    protected boolean access;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        new AsyncRequest().execute();

    }

    class AsyncRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //Что мы делаем в фоне
            progressBar = findViewById(R.id.progressBar);
            session.inicialize(AuthActivity.this);
            session_status = session.session_exist();
            if(session_status){
            access = session.getAccessRight();}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Если процесс загрузился

            progressBar.setVisibility(View.GONE);
            if (session_status) {
                if(access){getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Admin()).commit();}
                else{ getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MyData()).commit();}
            }else{getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Login()).commit();}

        }
    }
}
