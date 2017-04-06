package com.example.jiangyue.androidap.algorithm.lifegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangyue on 17/4/5.
 * 生命算法视图，记录List<Point>作为绘制react的依据
 * 本世纪70年代，人们曾疯魔一种被称作“生命游戏”的小游戏，这种游戏相当简单。假设有一个像棋盘一样的方格网，每个方格中放置一个生命细胞，
 * 生命细胞只有两种状态：“生”或“死”。游戏规则如下：
 * 　　1． 如果一个细胞周围有3个细胞为生（一个细胞周围共有8个细胞），则该细胞为生，即该细胞若原先为死，则转为生，若原先为生，则保持不变；
 * 　　2． 如果一个细胞周围有2个细胞为生，则该细胞的生死状态保持不变；
 * 　　3． 在其它情况下，该细胞为死，即该细胞若原先为生，则转为死，若原先为死，则保持不变设定图像中每个像素的初始状态后依据上述的游戏规则演绎生命的变化
 * ，由于初始状态和迭代次数不同，将会得到令人叹服的优美图案。
 * 这个程序还能统计当前活着的细胞的个数：
 */
public class LifeGameView extends View {

    private float xPixelsPerUnit = 0f, yPixelsPerUnit = 0f;
    private boolean startGame = false;//开启生命算法
    private int defineNum = 50;//单元格Y数量
    private float scaleXY = 0;//xy比例
    private int maxX = 0;

    private StringBuffer lifeSbf = new StringBuffer();
    private List<Spirit> points = new ArrayList<Spirit>();

    private Paint paint;

    public LifeStatusChangeListener lifeStatusChangeListener;

    public interface LifeStatusChangeListener {
        public abstract void lifeChangeStop();
    }

    public LifeGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LifeGameView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void setStartGame(boolean startGame) {
        this.startGame = startGame;
        invalidate();
    }

    public boolean getStartGame() {
        return startGame;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        scaleXY = getWidth() * 1.0f / getHeight();
        float minX = 0;
        maxX = (int) (scaleXY * defineNum);
        float minY = 0;
        int maxY = defineNum;
        xPixelsPerUnit = 0f;
        yPixelsPerUnit = 0f;
        //获取单位值对应的屏幕像素值
        if (maxX - minX != 0) {
            xPixelsPerUnit = (right - left) / (maxX - minX);
        }
        if (maxY - minY != 0) {
            yPixelsPerUnit = (bottom - top) / (maxY - minY);
        }
        //绘制view
        points.clear();
        for (int i = 0; i < maxY * maxY; i++) {
            Spirit spirit = new Spirit();
            spirit.xVal = (left + xPixelsPerUnit * (i % maxX - minX));
            spirit.yVal = (top + yPixelsPerUnit * ((int) (i / maxX) - minY));
            spirit.position.x = i % maxX;
            spirit.position.y = (int) (i / maxX);
            points.add(spirit);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pos = (int) (event.getX() / xPixelsPerUnit) + maxX * (int) (event.getY() / yPixelsPerUnit);
        if (pos >= 0 && pos < points.size()) {
            points.get(pos).nextStatus = true;
            invalidate();
        }
        return super.onTouchEvent(event);
    }

    /**
     * draw会调用onDraw，一般情况使用onDraw
     *
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSeries(canvas, paint, points, xPixelsPerUnit, yPixelsPerUnit, maxX);
    }

    private void drawSeries(Canvas canvas, Paint paint, List<Spirit> points, float xPixelsPerUnit, float yPixelsPerUnit, int maxX) {
        for (int i = 0; i < points.size(); i += 2) {
            if (i + 1 < points.size()) {
                canvas.drawRect(points.get(i).xVal, points.get(i).yVal, points.get(i).xVal + xPixelsPerUnit, points.get(i).yVal + yPixelsPerUnit, paint);
            }
        }
        //计算细胞下阶段生存状态
        StringBuffer lifeStr = new StringBuffer();
        for (int i = 0, size = points.size(); i < size; i++) {
            Spirit s0 = getSprite(i - maxX - 1, points);
            Spirit s1 = getSprite(i - maxX, points);
            Spirit s2 = getSprite(i - maxX + 1, points);
            Spirit s3 = getSprite(i - 1, points);
            Spirit s4 = getSprite(i + 1, points);
            Spirit s5 = getSprite(i + maxX - 1, points);
            Spirit s6 = getSprite(i + maxX, points);
            Spirit s7 = getSprite(i + maxX + 1, points);
            int numLife = resetLifeStatus(s0, s1, s2, s3, s4, s5, s6, s7);
            if (startGame) {
                if (numLife == 3) {
                    points.get(i).nextStatus = true;
                    lifeStr.append(i);
                } else if (numLife != 2) {
                    points.get(i).nextStatus = false;
                }
            }
        }
        //显示存活
        for (int i = 0, size = points.size(); i < size; i++) {
            points.get(i).lifeStatus = points.get(i).nextStatus;
            if (points.get(i).lifeStatus) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(points.get(i).xVal, points.get(i).yVal, points.get(i).xVal + xPixelsPerUnit, points.get(i).yVal + yPixelsPerUnit, paint);
                paint.setStyle(Paint.Style.STROKE);
            }
        }

        if (!TextUtils.isEmpty(lifeStr) && !lifeStr.toString().equals(lifeSbf.toString())) {
            lifeSbf = lifeStr;
            invalidate();
        } else {
            lifeSbf = lifeStr;
            startGame = false;
            if (lifeStatusChangeListener != null) {
                lifeStatusChangeListener.lifeChangeStop();
            }
        }
    }

    private int resetLifeStatus(Spirit... spirits) {
        int numLife = 0;
        for (Spirit s : spirits) {
            if (getSpiritStatus(s)) {
                numLife++;
            }
        }
        return numLife;
    }

    private Spirit getSprite(int pos, List<Spirit> points) {
        if (pos < points.size() && pos >= 0) {
            return points.get(pos);
        }
        return null;
    }

    private boolean getSpiritStatus(Spirit s) {
        if (s == null) {
            return false;
        }
        return s.lifeStatus;
    }

    /**
     * 单元格对象
     */
    class Spirit {
        private float xVal;
        private float yVal;
        private PointF position = new PointF();
        private boolean lifeStatus = false;//单元格状态，生为true，死为false
        private boolean nextStatus = false;//单元格状态，生为true，死为false
    }
}
