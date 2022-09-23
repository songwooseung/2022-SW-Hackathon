package com.example.hackerthon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class initActivity extends AppCompatActivity
{
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        layout = findViewById(R.id.layout);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(initActivity.this, MainActivity.class); //화면 전환
                startActivity(intent);
                finish();
            }
        }, 2000); //딜레이 타임 조절
        setSize();
    }

    private void setSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels;
        int heightPixels = widthPixels * 3382/1480;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthPixels,heightPixels);

        params.gravity = Gravity.CENTER;

        layout.setLayoutParams(params);
    }
}