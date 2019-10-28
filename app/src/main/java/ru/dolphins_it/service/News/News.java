package ru.dolphins_it.service.News;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.List;
import ru.dolphins_it.service.R;


/**
 * A simple {@link Fragment} subclass.
 */

/*Фрагмент для отображения новостей с сайта*/

public class News extends Fragment {

    Document newsHTML;
    RecyclerView recyclerview;
    LayoutAnimationController animation_controller;
    SwipeRefreshLayout swipeRefreshLayout;
    List<NewsAdapter.News> news;
    Elements elements;
    NewsAdapter adapter;
    ProgressBar progressBar;
    TextView errorText;
    Button errorButton;
    Toast toast;

    boolean errorFlag = false;
    boolean refresh = false;

    public News() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Раздуваем макет для данного фрагмента
        return inflater.inflate(R.layout.fragment_news, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { //Иницициализация элементов во фрагменте

        swipeRefreshLayout = getView().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        recyclerview = (RecyclerView) getView().findViewById(R.id.rv); //Поиск RecyclerView по id в layout activity
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar); //Поиск ProgressBar по id в layout activity
        errorText = (TextView) getView().findViewById(R.id.error); //Поиск ProgressBar по id в layout activity
        errorButton = (Button) getView().findViewById(R.id.error_button);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()); //Задаём LayoutManager для RecyclerView
        recyclerview.setLayoutManager(linearLayoutManager);

        adapter = new NewsAdapter(news);
        adapter.initializeData(getActivity()); //Инициализируем новый массив данных в адаптере для RecyclerView

        new AsyncRequest().execute();

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorFlag = false;
                new AsyncRequest().execute();
            }
        });

    }

    private final SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            errorFlag = false;
            refresh = true;
            new AsyncRequest().execute();
        }
    };

    class AsyncRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //Что мы делаем в фоне
            try {
                if(refresh){adapter.clear();}
                newsHTML = Jsoup.connect(getString(R.string.DEFAULT_URL)+getString(R.string.NEWS_URL)).execute().parse(); //Получаем html страницу для парсинга по URL
                elements = newsHTML.getElementsByClass("news"); //Записываем в массив все дочерние тэги родительского контейнера news

                for (Element e: elements) {
                    adapter.addData(e.select("a.news__name").select("a.hidden-sm-down").text(), e.select("div.news__text").text(), e.select("p.hidden-sm-down").select("p.news__date").text(),
                            getString(R.string.DEFAULT_URL)+e.select("img.news__img").attr("src"), getString(R.string.DEFAULT_URL)+e.select("a.news__name").select("a.hidden-sm-down").attr("href")); //В цикле добавляем новые элементы списка для RecyclerView
                }

            } catch (IOException e) {
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
                if(refresh){
                    refresh = false;
                    swipeRefreshLayout.setRefreshing(false);
                    toast = Toast.makeText(getContext(), "Возникла ошибка при загрузке", Toast.LENGTH_SHORT);
                    toast.show();}
                else{errorText.setVisibility(View.VISIBLE); errorButton.setVisibility(View.VISIBLE);}
            }
            else {

                if(refresh){
                    refresh = false;
                    swipeRefreshLayout.setRefreshing(false);
                }else{
                errorText.setVisibility(View.GONE);
                errorButton.setVisibility(View.GONE);
                recyclerview.setVisibility(View.VISIBLE);
                }
                recyclerview.setAdapter(adapter); //Задаём адаптер для RV

                Context context = recyclerview.getContext();
                animation_controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_from_bottom);
                recyclerview.setLayoutAnimation(animation_controller);
            }

        }
        @Override
        protected void onPreExecute() {
            if(!refresh) {
                errorText.setVisibility(View.GONE);
                errorButton.setVisibility(View.GONE);
                progressBar.setVisibility(ProgressBar.VISIBLE);
            }
        }
    }



}
