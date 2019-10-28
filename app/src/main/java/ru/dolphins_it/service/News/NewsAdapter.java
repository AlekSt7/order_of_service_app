package ru.dolphins_it.service.News;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import ru.dolphins_it.service.ImageManager;
import ru.dolphins_it.service.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    public ImageManager imageManager;
    public Bitmap bitmap;
    static Activity activity;

    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        TextView NewsTitle;
        TextView NewsDescrition;
        TextView NewsDate;
        ImageView NewsImage;
        LinearLayout linearLayout;
        ProgressBar progressBar;
        String newsURL;
        NewsViewHolder(View itemView) {
            super(itemView);
            NewsTitle = (TextView)itemView.findViewById(R.id.title);
            NewsDescrition = (TextView)itemView.findViewById(R.id.description);
            NewsDate = (TextView)itemView.findViewById(R.id.date);
            NewsImage = (ImageView)itemView.findViewById(R.id.image);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.wrapper);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        }
    }

    class News { //Класс, представляюющий данные
        String title;
        String description;
        String date;
        String imageURL;
        String newsURL;
        News(String title, String description, String date, String imageUrl, String newsUrl) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.imageURL = imageUrl;
            this.newsURL = newsUrl;
        }
    }

    private List<News> news;
    NewsAdapter(List<News> news){
        this.news = news;
    }

    public void initializeData(Activity context)
    {
        news = new ArrayList<>();
        activity = context;
    }

    public void clear(){news.clear();}

    public void addData(String title, String description, String date, String imageUrl, String newsUrl){ //Метод для инициализации списка
        news.add(new NewsAdapter.News(title, description, date, imageUrl, newsUrl));
    }

    //Переопределение методов RecycleView.Adapter

    @Override
    public int getItemCount() {
        return news.size();
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item, viewGroup, false);
        NewsViewHolder pvh = new NewsViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder newsViewHolder, final int i) {
        newsViewHolder.NewsTitle.setText(news.get(i).title);
        newsViewHolder.NewsDescrition.setText(news.get(i).description);
        newsViewHolder.NewsDate.setText(news.get(i).date);



        imageManager.fetchImage(news.get(i).imageURL, newsViewHolder.NewsImage, newsViewHolder.progressBar, true, activity); //Загружаем изображение по URL

            newsViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() { //Задаём слушатель нажатия на элемент RecyclerView
                @Override
                public void onClick(View view) {

                    Bundle bundle = null;

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) { //Говорим второй Activity о том, что должна воспрозивестись анимация перехода между Activity
                        View v = newsViewHolder.NewsImage;
                        if (v != null) {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, v, "news_image_transition");
                            bundle = options.toBundle();
                        }
                    }

                    Intent intent = new Intent(activity, news_full_activity.class);

                    intent.putExtra("news_title", news.get(i).title); //Передаём заголовок нновости в Activity с полной новостью
                    intent.putExtra("news_url", news.get(i).newsURL); //Передаём ссылку в Activity с полной новостью

                    if(newsViewHolder.NewsImage.getDrawable() != null) { //Если картинка загрузилась

                        bitmap = ((BitmapDrawable) newsViewHolder.NewsImage.getDrawable()).getBitmap(); //Получаем BitMap изображения новости для передачи её в другую Activity


                        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true); //Алгорити сжатия картинки для передачи её в другую Activity
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] byteArray = baos.toByteArray();

                        intent.putExtra("news_picture", byteArray); //Передаём каартинку во второе Activity

                        if (bundle == null) { //Запускаем activity с полной новостью
                            activity.startActivity(intent);
                        } else {
                            activity.startActivity(intent, bundle);
                        }
                    }else { //Если картинка не загрузилась

                        newsViewHolder.progressBar.setVisibility(View.GONE);
                        newsViewHolder.NewsImage.setVisibility(View.VISIBLE);

                        intent.putExtra("news_picture_url", news.get(i).imageURL); //Передаём каартинку во второе Activity

                        if (bundle == null) { //Запускаем activity с полной новостью
                            activity.startActivity(intent);
                        } else {
                            activity.startActivity(intent, bundle);
                        }

                    }

                }
            });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

