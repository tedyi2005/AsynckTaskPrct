package com.example.tedyi2005.asyncktaskprct;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ImageView iv;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.imageView1);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
    }

    public void downloadCow(View v){

        String url = "http://www.ouiouiphoto.fr/Gallerie/Files/Forum/HD/Mar04-HD.jpg";
        Downloader downloader = new Downloader();
        downloader.execute(url);


    }



    public class Downloader extends AsyncTask<String, Integer, Bitmap> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bmp = null;

            String url = params[0];
            URL downloadURL = null;
            HttpURLConnection conn = null;
            InputStream inputStream = null;


            try {
                downloadURL = new URL(url);
                conn = (HttpURLConnection) downloadURL.openConnection();
                int length = conn.getContentLength();
                inputStream = conn.getInputStream();
                byte[] imageData = new byte[length];
                int buffersize = (int) Math.ceil(length / (double) 100);
                int downloaded = 0;
                int read;
                while (downloaded < length) {
                    if (length < buffersize) {
                        read = inputStream.read(imageData, downloaded, length);
                    } else if ((length - downloaded) <= buffersize) {
                        read = inputStream.read(imageData, downloaded, length
                                - downloaded);
                    } else {
                        read = inputStream.read(imageData, downloaded, buffersize);
                    }
                    downloaded += read;
                    publishProgress((downloaded * 100) / length);
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
                options.inSampleSize = calculateInSampleSize(options, 1024, 128);
                options.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length,options);




            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (conn != null) {
                    conn.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }



            return bmp;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pb.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            iv.setImageBitmap(result);
        }



    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}