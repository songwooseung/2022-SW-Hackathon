package com.example.hackerthon;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;

import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class initActivity extends AppCompatActivity
{
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ImageView startIMAGE = (ImageView) findViewById(R.id.startImage);
        Glide.with(this).load(R.raw.open).into(startIMAGE);
        //layout = findViewById(R.id.layout);

        AssetManager assetManager = this.getAssets();
        List<String[]> dataList = CSVGetter(assetManager);

        Intent intent = new Intent(initActivity.this, MainActivity.class); //화면 전환

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                intent.putExtra("size", dataList.size());
                for (int i = 0; i < dataList.size(); i++)
                {
                    intent.putExtra(Integer.toString(i), dataList.get(i));
                }
                startActivity(intent);
                finish();
            }
        }, 5000); //딜레이 타임 조절*/


    }

    public static List<String[]> CSVGetter(AssetManager manager)
    {
        List<String[]> dataList;
        try
        {
            InputStream inputStream = manager.open("output.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
            dataList = reader.readAll();
        } catch (IOException e)
        {
            dataList = new ArrayList<String[]>();
            dataList.add(new String[]{"error", "-1"});
            e.printStackTrace();
        }

        return dataList;
    }
}