package com.example.jiangyue.androidap.algorithm.lifegame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.jiangyue.androidap.R;

/**
 * Created by jiangyue on 17/4/5.
 */
public class LifeGameActivity extends Activity {

    private LifeGameView lifeGameView;
    private Button btnStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_game);

        lifeGameView = (LifeGameView) findViewById(R.id.lifeg_view);
        btnStart = (Button) findViewById(R.id.btn_start_game);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lifeGameView.getStartGame()) {
                    lifeGameView.setStartGame(true);
                    btnStart.setText("暂停");
                } else {
                    lifeGameView.setStartGame(false);
                    btnStart.setText("开始生命游戏");
                }
            }
        });

        lifeGameView.lifeStatusChangeListener = new LifeGameView.LifeStatusChangeListener() {
            @Override
            public void lifeChangeStop() {
                btnStart.setText("开始生命游戏");
            }
        };
    }
}
