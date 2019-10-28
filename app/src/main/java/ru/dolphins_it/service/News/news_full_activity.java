package ru.dolphins_it.service.News;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import ru.dolphins_it.service.ImageManager;
import ru.dolphins_it.service.R;

public class news_full_activity extends SwipeBackActivity {

    Document newsHTML;
    String url;

    private SwipeBackLayout mSwipeBackLayout;

    TextView title;
    TextView description;
    ImageView news_image;
    ImageManager imageManager;

    boolean errorFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_full_activity);

        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        news_image = (ImageView) findViewById(R.id.image);


        Bundle bundle = getIntent().getExtras();

        if (bundle.getByteArray("news_picture") != null) { //Если картинка передалась
            byte[] byteArray = bundle.getByteArray("news_picture"); //Получаем картинку новости
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            news_image.setImageBitmap(bitmap);
        } else { //Если картинку не получили
            imageManager.fetchImage(bundle.getString("news_picture_url"), news_image, null, false, this); //Загружаем изображение по URL уже в Activity
        }

        title.setText(bundle.getString("news_title")); //Получаем заголовок нововсти
        url = bundle.getString("news_url");            //Получаем ссылку на новость

        new AsyncRequest().execute(); //Делаем запрос на загрузку текста новости

        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);

    }


    private class AsyncRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //Что мы делаем в фоне
            try {
                newsHTML = Jsoup.connect(url).execute().parse(); //Получаем html страницу для парсинга по URL

            } catch (IOException e) {
                e.printStackTrace();
                errorFlag = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Если процесс загрузился
            //progressBar.setVisibility(ProgressBar.GONE);
            //if(errorFlag == true){ errorText.setVisibility(View.VISIBLE);}
            description.setText(newsHTML.select("div.news__text_full").text());
            //recyclerview.setVisibility(View.VISIBLE);

        }
        @Override
        protected void onPreExecute() {
            //progressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }


}
