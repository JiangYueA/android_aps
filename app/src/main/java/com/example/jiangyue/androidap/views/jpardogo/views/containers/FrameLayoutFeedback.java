package com.example.jiangyue.androidap.views.jpardogo.views.containers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.jiangyue.androidap.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;


/**
 * RelativeLayout with a touch feedback color as overlay.
 */
public class FrameLayoutFeedback extends FrameLayout {

    private ViewGroup view;
    private int selects;

    private StateListDrawable touchFeedbackDrawable;

    public FrameLayoutFeedback(Context context) {
        super(context);
    }

    public FrameLayoutFeedback(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SelectorOptions, 0, 0);
        setSelector(a);

    }

    private void setSelector(TypedArray a) {
        touchFeedbackDrawable = new StateListDrawable();
        touchFeedbackDrawable.addState(
                new int[]{android.R.attr.state_pressed},
                getColor(a)
        );
    }

    private Drawable getColor(TypedArray a) {
        return new ColorDrawable(
                a.getColor(R.styleable.SelectorOptions_selectorColor,
                        android.R.color.darker_gray)
        );
    }

    public FrameLayoutFeedback(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        // If the parent is pressed, do not set to pressed.
        if (pressed && ((View) getParent()).isPressed()) {
            return;
        }

        super.setPressed(pressed);
    }

    public void addView() {

        if (view == null) {
            selects = 0;
            view = new ViewGroup(getContext()) {
                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {

                }
            };
            view.setBackgroundColor(getContext().getResources().getColor(R.color.red_transparent));

            ImageView img = new ImageView(getContext());
            img.setBackground(getContext().getResources().getDrawable(R.drawable.ic_action_user));

            view.addView(img);
        }

        if (selects == 0) {
            selects = 1;
            this.addView(view);
            setDataWithAnimation(view, 0, 1, this.getWidth(), 0);
        } else if (selects == 1) {
            selects = -1;
            setDataWithAnimation(view, 1, 0, 0, -this.getWidth());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    selects = 0;
                    FrameLayoutFeedback.this.removeView(view);
                }
            }, 300);
        }
    }

    @NonNull
    private void setDataWithAnimation(View view, int alphaStart, int alphaEnd, float transXStard, float transXEnd) {
        Animator alphaAnimator = com.nineoldandroids.animation.ObjectAnimator.ofFloat(view, "alpha", alphaStart, alphaEnd);
        Animator animator = com.nineoldandroids.animation.ObjectAnimator.ofFloat(view, "translationX", transXStard, transXEnd);
        Animator[] animators = new Animator[]{animator, alphaAnimator};

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        set.setDuration(300);
        set.start();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (touchFeedbackDrawable != null) {
            touchFeedbackDrawable.setBounds(0, 0, getWidth(), getHeight());
            touchFeedbackDrawable.draw(canvas);
        }
    }

    @Override
    protected void drawableStateChanged() {
        if (touchFeedbackDrawable != null) {
            touchFeedbackDrawable.setState(getDrawableState());
            invalidate();
        }
        super.drawableStateChanged();
    }
}
