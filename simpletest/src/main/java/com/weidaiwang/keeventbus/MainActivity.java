package com.weidaiwang.keeventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.weidaiwang.eventbus.EventBus;
import com.weidaiwang.eventbus.Subscribe;
import com.weidaiwang.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getInstance().register(this);
        textView = findViewById(R.id.tv_content);
        findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onFDS(String str) {
        textView.setText(str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getInstance().unregister(this);
    }
}
