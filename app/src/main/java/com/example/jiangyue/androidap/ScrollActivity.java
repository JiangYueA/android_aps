package com.example.jiangyue.androidap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.jiangyue.androidap.views.imageview.TransitionImageView;

public class ScrollActivity extends Activity {

    private boolean click = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_scroll);

        final TransitionImageView imageView = (TransitionImageView) findViewById(R.id.transition_img);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!click) {
                    int[] leftTop = {0, 0};
                    //获取输入框当前的location位置
                    imageView.getLocationInWindow(leftTop);
                    imageView.setOriginalInfo(ScrollActivity.this.getResources().getDimensionPixelSize(R.dimen.img_transition_height), ScrollActivity.this.getResources().getDimensionPixelSize(R.dimen.img_transition_height),
                            leftTop[0], leftTop[1]);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                    layoutParams.width = RelativeLayout.LayoutParams.FILL_PARENT;
                    layoutParams.height = RelativeLayout.LayoutParams.FILL_PARENT;
                    imageView.setLayoutParams(layoutParams);

                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.transformIn();
                    click = true;
                } else {
                    imageView.transformOut();
                    click = false;
                }
            }
        });
    }
}
