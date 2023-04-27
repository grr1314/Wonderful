package com.lc.nativelib.display;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lc.nativelib.R;

public class MonitorDisplayActivity extends AppCompatActivity {
    TextView anr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_display);
        anr = findViewById(R.id.tv_anr);
        anr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonitorDisplayActivity.this, AnrDisplayActivity.class);
                startActivity(intent);
            }
        });

    }
}
