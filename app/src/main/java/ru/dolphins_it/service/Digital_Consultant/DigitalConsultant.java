package ru.dolphins_it.service.Digital_Consultant;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.dolphins_it.service.DataBaseHelper;
import ru.dolphins_it.service.DigitalSignage.Digital_signance;
import ru.dolphins_it.service.ImageManager;
import ru.dolphins_it.service.R;
import ru.dolphins_it.service.Session;

/**
 * A simple {@link Fragment} subclass.
 */
public class DigitalConsultant extends Fragment {

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;

    private Session session = new Session();

    private TextInputEditText inputEditText;
    private TextInputLayout inputLayout;
    private ImageManager imageManager;
    private Document descriptionHTML;
    private ProgressBar progressBar;
    private Elements elementsDESCP;
    private ScrollView scrollView;
    private TextView description;
    private ImageView imageView;
    private Button errorButton;
    private TextView errorText;
    boolean errorFlag = false;
    private Button order;

    public DigitalConsultant() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        session.inicialize(getContext());
        dataBaseHelper = new DataBaseHelper(getContext());
        db = dataBaseHelper.getWritableDatabase();
        session.inicialize(getContext());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_digital_consultant, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { //Иницициализация элементов во фрагменте

        inputLayout = getView().findViewById(R.id.comment_til);
        inputEditText = getView().findViewById(R.id.comment);

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar); //Поиск ProgressBar по id в layout activity
        errorText = (TextView) getView().findViewById(R.id.error); //Поиск ProgressBar по id в layout activity
        errorButton = (Button) getView().findViewById(R.id.error_button);
        scrollView = (ScrollView) getView().findViewById(R.id.scroll_view);
        description = (TextView) getView().findViewById(R.id.text);
        imageView = (ImageView) getView().findViewById(R.id.image);
        order = (Button) getView().findViewById(R.id.button);

        new AsyncRequest().execute();

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorFlag = false;
                new AsyncRequest().execute();
            }
        });

        if (session.session_exist()) {

            if(session.getAccessRight()){
                order.setVisibility(View.GONE);
                inputLayout.setVisibility(View.GONE);
            }else {

                order.setText("Добавить в корзину");
                inputLayout.setVisibility(View.VISIBLE);
                userCursor = db.query(DataBaseHelper.TABLE_ORDER, null, dataBaseHelper.DIGITAL_CONSULTANT + "= ?", new String[]{"1"},
                        null, null, null, null);

                if (userCursor.getCount() > 0) { //Проверяем существоваание именно этой услуги в корзине
                    order.setText("Добавлено в корзину");
                    order.setEnabled(false);
                    userCursor.moveToFirst();
                    inputEditText.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.DIGITAL_CONSULTANT_COMMENT)));

                }
            }
        }

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.session_exist()) {
                    ContentValues contentValues = new ContentValues();

                    if (DatabaseUtils.longForQuery(db, "SELECT COUNT(" + dataBaseHelper.ORDER_ID + ") FROM " + dataBaseHelper.TABLE_ORDER, null) > 0) { //Проверяем наличие заказов в базе данных

                        userCursor = db.query(dataBaseHelper.TABLE_ORDER, new String[]{dataBaseHelper.ORDER_ID},
                                null, null, null, null, null);

                        String id;

                        userCursor.moveToFirst();
                        id = userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.ORDER_ID));


                        contentValues.put(dataBaseHelper.DIGITAL_CONSULTANT_COMMENT, inputEditText.getText().toString());
                        contentValues.put(dataBaseHelper.DIGITAL_CONSULTANT, "1");
                        db.update(DataBaseHelper.TABLE_ORDER, contentValues, dataBaseHelper.ORDER_ID + "= ?", new String[]{id}); //Обновляем заказ и добавляем услугу

                    }else{ //Если закозов нет
                        contentValues.put(dataBaseHelper.DIGITAL_CONSULTANT_COMMENT, inputEditText.getText().toString());
                        contentValues.put(dataBaseHelper.DIGITAL_CONSULTANT, "1");
                        db.insert(DataBaseHelper.TABLE_ORDER, null, contentValues); //Добавляем услугу к заказу
                    }

                    order.setText("Добавлено в корзину");
                    order.setEnabled(false);

                }else{
                    Toast toast = Toast.makeText(getContext(),
                            "Авторизируйтесь или зарегистрируйтесь, чтобы оставить заявку", Toast.LENGTH_SHORT);
                    toast.show();
                }}
        });

    }


    private class AsyncRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //Что мы делаем в фоне
            try {
                descriptionHTML = Jsoup.connect(getString(R.string.DEFAULT_URL)+getString(R.string.DIGITAL_CONSULTANT_URL)).execute().parse(); //Получаем html страницу для парсинга по URL
                elementsDESCP = descriptionHTML.getElementsByClass("content"); //Записываем в массив все дочерние тэги родительского контейнера
            } catch (IOException e) {
                Log.e("ASYNCTASK_LOAD_FAILED: ", e.getMessage());
                e.printStackTrace();
                errorFlag = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Если процесс загрузился
            progressBar.setVisibility(ProgressBar.GONE);
            if(errorFlag == true){
                scrollView.setVisibility(View.GONE);
                errorText.setVisibility(View.VISIBLE);
                errorButton.setVisibility(View.VISIBLE);}
            else {
                errorText.setVisibility(View.GONE);
                errorButton.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                description.setText(elementsDESCP.select("p").text());
                imageManager.fetchImage(getString(R.string.DEFAULT_URL) + elementsDESCP.select("img").attr("src"), imageView, null, false, getContext()); //Загружаем изображение по URL
            }

        }
        @Override
        protected void onPreExecute() {
            errorText.setVisibility(View.GONE);
            errorButton.setVisibility(View.GONE);
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }

}
