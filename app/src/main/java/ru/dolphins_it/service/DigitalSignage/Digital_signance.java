package ru.dolphins_it.service.DigitalSignage;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.dolphins_it.service.ImageManager;
import ru.dolphins_it.service.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Digital_signance extends Fragment {


    Document appHTML;
    Document rentHTML;
    Elements elementsAPP;
    Elements elementsRENT;
    ProgressBar progressBar;
    ScrollView scrollView;
    TextView description;
    TextView errorText;
    Button errorButton;
    ImageManager imageManager;
    LinearLayout linearAPP;
    View viewAPP;

    List<View> app_elements;

    boolean errorFlag = false;

    public Digital_signance() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_digital_signance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { //Иницициализация элементов во фрагменте

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar); //Поиск ProgressBar по id в layout activity
        errorText = (TextView) getView().findViewById(R.id.error); //Поиск ProgressBar по id в layout activity
        errorButton = (Button) getView().findViewById(R.id.error_button);
        scrollView = (ScrollView) getView().findViewById(R.id.scroll_view);
        description = (TextView) getView().findViewById(R.id.header3);

        app_elements = new ArrayList<>();

        linearAPP = getView().findViewById(R.id.application); //Layout, в который будем помещать элементы описания системы э.очереди

        new AsyncRequest().execute();

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorFlag = false;
                new AsyncRequest().execute();
            }
        });

    }


    private class AsyncRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //Что мы делаем в фоне
            try {
                appHTML = Jsoup.connect(getString(R.string.DEFAULT_URL)+getString(R.string.DIGITAL_SIGNAGE_CUSTOMERS_URL)).execute().parse(); //Получаем html страницу для парсинга по URL
                elementsAPP = appHTML.getElementsByClass("mb-4"); //Записываем в массив все дочерние тэги родительского контейнера

                rentHTML = Jsoup.connect(getString(R.string.DEFAULT_URL)+getString(R.string.DIGITAL_SIGNAGE_RENT_URL)).execute().parse(); //Получаем html страницу для парсинга по URL
                elementsRENT = rentHTML.getElementsByClass("text"); //Записываем в массив все дочерние тэги родительского контейнера
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

                description.setText(elementsRENT.select("p").text());

                for (Element e : elementsAPP) { //Парсинг страницы структуры системы
                    viewAPP = getLayoutInflater().inflate(R.layout.item_2, null); //Берем лайаут, находим в неё все элементы, задаем нужные данные
                    TextView title = viewAPP.findViewById(R.id.title);
                    TextView description = viewAPP.findViewById(R.id.description);
                    ImageView image = viewAPP.findViewById(R.id.image);
                    imageManager.fetchImage(getString(R.string.DEFAULT_URL) + e.select("img").attr("src"), image, null, false, getContext()); //Загружаем изображение по URL
                    title.setText(e.select("h3.title_sub_big").text());
                    description.setText(e.select("p").text());
                    app_elements.add(viewAPP); //добавляем все что создаем в массив
                    linearAPP.addView(viewAPP); //добавляем елементы в linearlayout
                }
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
