package ru.dolphins_it.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ImageManager {
    private final static String TAG = "ImageManager";

    /** Private constructor prevents instantiation from other classes */
    private ImageManager () {}

    public static void fetchImage(final String iUrl, final ImageView iView, final View progressBar, final boolean progressBarTrue, final Context context) {
        if ( iUrl == null || iView == null )
            return;

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                final Bitmap image = (Bitmap) message.obj;

                if(progressBarTrue) {
                    progressBar.setVisibility(View.GONE);
                    iView.setVisibility(View.VISIBLE);
                    iView.setImageBitmap(image);
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                    iView.startAnimation(animation);
                }else {
                    iView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackGround));
                    iView.setImageBitmap(image);
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                    iView.startAnimation(animation);
                }

            }
        };

        final Thread thread = new Thread() {
            @Override
            public void run() {
                final Bitmap image = downloadImage(iUrl);
                if ( image != null ) {
                    Log.v(TAG, "Got image by URL: " + iUrl);
                    final Message message = handler.obtainMessage(1, image);
                    handler.sendMessage(message);
                }
            }
        };

        if(progressBarTrue) {
            iView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            //Здесь то, чем заполняем IMageView, пока грузится картинка, если ProgressBar стоит на false
        }

        thread.setPriority(3);
        thread.start();
    }

    public static Bitmap downloadImage(String iUrl) {
        Bitmap bitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream buf_stream = null;
        try {
            Log.v(TAG, "Starting loading image by URL: " + iUrl);
            conn = (HttpURLConnection) new URL(iUrl).openConnection();
            conn.setDoInput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();
            buf_stream = new BufferedInputStream(conn.getInputStream(), 8192);
            bitmap = BitmapFactory.decodeStream(buf_stream);
            buf_stream.close();
            conn.disconnect();
            buf_stream = null;
            conn = null;
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Url parsing was failed: " + iUrl);
        } catch (IOException ex) {
            Log.d(TAG, iUrl + " does not exists");
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "Out of memory!!!");
            return null;
        } finally {
            if ( buf_stream != null )
                try { buf_stream.close(); } catch (IOException ex) {}
            if ( conn != null )
                conn.disconnect();
        }
        return bitmap;
    }
}
