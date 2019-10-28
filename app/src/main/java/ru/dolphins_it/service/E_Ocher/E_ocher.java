package ru.dolphins_it.service.E_Ocher;


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
public class E_ocher extends Fragment {

    Document structureHTML;
    Document rendHTML;
    Elements elementsSTR;
    Elements elementsRENT;
    ProgressBar progressBar;
    TextView errorText;
    Button errorButton;
    ImageManager imageManager;
    LinearLayout linearSTR;
    LinearLayout linearRENT;
    ScrollView scrollView;
    View viewSTR;
    View viewRENT;

    List<View> structure_elements; //Для хранения элементов описания о структуре "системы электронной очереди
    List<View> rent_elements; //Для хранения элементов описания о структуре "системы электронной очереди

    boolean errorFlag = false;

    public E_ocher() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_e_ocher, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { //Иницициализация элементов во фрагменте

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar); //Поиск ProgressBar по id в layout activity
        errorText = (TextView) getView().findViewById(R.id.error); //Поиск ProgressBar по id в layout activity
        errorButton = (Button) getView().findViewById(R.id.error_button);
        scrollView = (ScrollView) getView().findViewById(R.id.scroll_view);

        structure_elements = new ArrayList<>();
        rent_elements = new ArrayList<>();

        linearSTR = getView().findViewById(R.id.system_structure); //Layout, в который будем помещать элементы описания системы э.очереди
        linearRENT = getView().findViewById(R.id.rent); //Layout, в который будем помещать элементы описания системы э.очереди

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
                structureHTML = Jsoup.connect(getString(R.string.DEFAULT_URL)+getString(R.string.E_OCHER_STRUCTURE_URL)).execute().parse(); //Получаем html страницу для парсинга по URL
                elementsSTR = structureHTML.getElementsByClass("block-structure"); //Записываем в массив все дочерние тэги родительского контейнера

                rendHTML = Jsoup.connect(getString(R.string.DEFAULT_URL)+getString(R.string.E_OCHER_RENT_URL)).execute().parse(); //Получаем html страницу для парсинга по URL
                elementsRENT = rendHTML.getElementsByClass("block-structure"); //Записываем в массив все дочерние тэги родительского контейнера
            } catch (IOException e) {
                Log.e("LOAD_FAILED: ", e.getMessage());
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
                for (Element e : elementsSTR) { //Парсинг страницы структуры системы
                    viewSTR = getLayoutInflater().inflate(R.layout.item_1, null); //Берем лайаут, находим в неё все элементы, задаем нужные данные
                    TextView title = viewSTR.findViewById(R.id.title);
                    TextView description = viewSTR.findViewById(R.id.description);
                    ImageView image = viewSTR.findViewById(R.id.image);
                    imageManager.fetchImage(getString(R.string.DEFAULT_URL) + e.select("img").attr("src"), image, null, false, getContext()); //Загружаем изображение по URL
                    title.setText(e.select("strong").text());
                    description.setText(e.select("p").text());
                    structure_elements.add(viewSTR); //добавляем все что создаем в массив
                    linearSTR.addView(viewSTR); //добавляем елементы в linearlayout
                }

                for (Element e : elementsRENT) { //Парсинг страницы аренды оборудования
                    viewRENT = getLayoutInflater().inflate(R.layout.item_2, null); //Берем лайаут, находим в неё все элементы, задаем нужные данные
                    TextView title = viewRENT.findViewById(R.id.title);
                    TextView description = viewRENT.findViewById(R.id.description);
                    ImageView image = viewRENT.findViewById(R.id.image);
                    imageManager.fetchImage(getString(R.string.DEFAULT_URL) + e.select("img").attr("src"), image, null, false, getContext()); //Загружаем изображение по URL
                    title.setText(e.select("strong").text());
                    description.setText(e.select("p").text());
                    rent_elements.add(viewRENT); //добавляем все что создаем в массив
                    linearRENT.addView(viewRENT); //добавляем елементы в linearlayout
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
