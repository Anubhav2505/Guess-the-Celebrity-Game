package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebUrls = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int Chosecleb=0;
    ImageView imageView;
    int locationofcorrectAnswer=0;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    String[] answers = new String[4];
    public void celebChoosen(View view){
    if(view.getTag().toString().equals(Integer.toString(locationofcorrectAnswer))){
        Toast.makeText(getApplicationContext(),"Correct Answer" ,Toast.LENGTH_LONG).show();
    }
    else {
        Toast.makeText(getApplicationContext(),"Wrong Answer" ,Toast.LENGTH_LONG).show();
    }
        createNewQuestion();
    }

    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myimage = BitmapFactory.decodeStream(inputStream);
                return  myimage;

            } catch (Exception e) {
                e.printStackTrace();
            }
         return null;
        }
    }
    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            String result = " ";
            HttpsURLConnection connection=null;
            try {
                url = new URL(urls[0]);
                connection = (HttpsURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!=-1){
                    char current = (char) data;
                    result += current;
                    data=reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView =(ImageView) findViewById(R.id.imageView);
        button0 = (Button)findViewById(R.id.button);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();
        String result = null;
        try {
            result=task.execute("https://www.imdb.com/list/ls024299269/").get();
            String[] splitResult = result.split(" <div class=\"desc lister-total-num-results\">");
            Pattern p =Pattern.compile("src=\"(.*?)\"");
            Matcher m =p.matcher(splitResult[1]);
            while (m.find()){
                celebUrls.add(m.group(1));
            }
            p =Pattern.compile("img alt=\"(.*?)\"");
            m =p.matcher(splitResult[1]);
            while (m.find()){
                celebNames.add(m.group(1));
            }


            createNewQuestion();
        } catch (ExecutionException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void createNewQuestion(){
        Random rand = new Random();
        Chosecleb = rand.nextInt(50);
        ImageDownloader imagetask = new ImageDownloader();
        Bitmap celebImage;
        try {
            celebImage=imagetask.execute(celebUrls.get(Chosecleb)).get();
            imageView.setImageBitmap(celebImage);
            int locationofIncorrectAnswer;
            locationofcorrectAnswer = rand.nextInt(4);
            for (int i=0; i<4 ; i++){
                if (i==locationofcorrectAnswer){
                    answers[i]=celebNames.get(Chosecleb);
                }
                else {
                    locationofIncorrectAnswer = rand.nextInt(50);
                    while (locationofIncorrectAnswer==Chosecleb){
                        locationofIncorrectAnswer = rand.nextInt(50);
                    }
                    answers[i] =celebNames.get(locationofIncorrectAnswer);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}