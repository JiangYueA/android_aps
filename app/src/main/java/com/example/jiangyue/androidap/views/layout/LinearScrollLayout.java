package com.example.jiangyue.androidap.views.layout;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;

import com.example.jiangyue.androidap.R;

/**
 * Created by jiangyue on 16/7/13.
 */
public class LinearScrollLayout extends LinearLayout {

    private static final boolean Debug = false;

    private Context mContext;

    private View mTopView;
    private View mIndicator;
    private ViewPager mViewPager;

    private int mTopViewHeight;
    private ViewGroup mInnerScrollView;
    private boolean isTopHidden = false;

    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMaximumVelocity, mMinimumVelocity;
    private int currentShowPage = 1;//当fragment里是viewpage时

    private float mLastY;
    private boolean mDragging;

    private boolean isInControl = false;

    //回调函数，改变navigation的透明度
    private ScrollChangeListener mScrollChangeListener;

    public void setScrollChangeListener(ScrollChangeListener scrollChangeListener) {
        mScrollChangeListener = scrollChangeListener;
    }

    public interface ScrollChangeListener {
        public void onLayoutScroll(float y, float topViewHeight);
    }

    public LinearScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);

        mContext = context;
        mScroller = new OverScroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTopView = findViewById(R.id.id_scrolllayout_topview);
        mIndicator = findViewById(R.id.id_scrolllayout_indicator);
        View view = findViewById(R.id.id_scrolllayout_viewpager);
        if (!(view instanceof ViewPager)) {
            throw new RuntimeException("id_scrolllayout_viewpager show used by ViewPager !");
        }
        mViewPager = (ViewPager) view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        //根据版本设置viewpager height
        params.height = getMeasuredHeight() - mIndicator.getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //根据版本设置mTopViewHeight
        mTopViewHeight = mTopView.getMeasuredHeight();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                getCurrentScrollView();

                if (mInnerScrollView instanceof ListView) {
                    //向下滑没有回弹，向上滑有回弹
                    if (dy > 0) {
                        mInnerScrollView.setOverScrollMode(OVER_SCROLL_NEVER);
                    } else {
                        mInnerScrollView.setOverScrollMode(OVER_SCROLL_ALWAYS);
                    }

                    //topView事件分发
                    ListView lv = (ListView) mInnerScrollView;
                    View c = lv.getChildAt(lv.getFirstVisiblePosition());

                    if (!isInControl && c != null && c.getTop() == 0 && isTopHidden
                            && dy > 0) {
                        isInControl = true;
                        //topView隐藏时，上拉拦截
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        MotionEvent ev2 = MotionEvent.obtain(ev);
                        dispatchTouchEvent(ev);
                        ev2.setAction(MotionEvent.ACTION_DOWN);
                        return dispatchTouchEvent(ev2);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                getCurrentScrollView();
                if (Math.abs(dy) > mTouchSlop) {
                    //fling
                    mDragging = true;

                    if (mInnerScrollView instanceof ListView) {
                        ListView lv = (ListView) mInnerScrollView;
                        View c = lv.getChildAt(lv.getFirstVisiblePosition());
                        if (!isTopHidden) {
                            // 如果topView没有隐藏，滚动scroll，不滚动listview
                            if (getScrollY() == 0 && c != null && c.getTop() == 0 && dy < 0) {
                                // topView在顶部 && 上拉，则拦截
                                initVelocityTrackerIfNotExists();
                                mVelocityTracker.addMovement(ev);
                                mLastY = y;
                                return true;
                            }
                            if (getScrollY() != 0 && c != null && c.getTop() == 0) {
                                // topView已经滚了一点点
                                initVelocityTrackerIfNotExists();
                                mVelocityTracker.addMovement(ev);
                                mLastY = y;
                                return true;
                            }
                        } else {
                            // topView隐藏且listiew在顶部，滚动scroll，不滚动listview
                            if ((c != null && c.getTop() == 0 && dy > 0)) {
                                // 下拉，则拦截
                                initVelocityTrackerIfNotExists();
                                mVelocityTracker.addMovement(ev);
                                mLastY = y;
                                return true;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDragging = false;
                recycleVelocityTracker();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!(mInnerScrollView instanceof ListView)) {
            return false;
        }

        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                mLastY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;

                if (!mDragging && Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                }
                if (mDragging) {
                    scrollBy(0, (int) -dy);

                    //下拉，防止topView隐藏后，不能连续滚动
                    if (getScrollY() == mTopViewHeight && dy < 0) {
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        isInControl = false;
                    }
                }

                mLastY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                recycleVelocityTracker();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                }
                recycleVelocityTracker();
                break;
        }

        return super.onTouchEvent(event);
    }

    /* 绑定滚动的视图 */
    private void getCurrentScrollView() {
        int currentItem = mViewPager.getCurrentItem();
        PagerAdapter a = mViewPager.getAdapter();
        if (a instanceof FragmentPagerAdapter) {
            FragmentPagerAdapter fadapter = (FragmentPagerAdapter) a;
            Fragment item = (Fragment) fadapter.instantiateItem(mViewPager,
                    currentItem);
            switch (currentShowPage) {
                case 1: {
                    mInnerScrollView = (ViewGroup) (item.getView().findViewById(R.id.id_scrolllayout_innerscrollview01));
                    break;
                }
                case 2: {
                    mInnerScrollView = (ViewGroup) (item.getView().findViewById(R.id.id_scrolllayout_innerscrollview02));
                    break;
                }
                case 3: {
                    mInnerScrollView = (ViewGroup) (item.getView().findViewById(R.id.id_scrolllayout_innerscrollview03));
                    break;
                }
            }
        } else if (a instanceof FragmentStatePagerAdapter) {
            FragmentStatePagerAdapter fsAdapter = (FragmentStatePagerAdapter) a;
            Fragment item = (Fragment) fsAdapter.instantiateItem(mViewPager,
                    currentItem);
            switch (currentShowPage) {
                case 1: {
                    mInnerScrollView = (ViewGroup) (item.getView().findViewById(R.id.id_scrolllayout_innerscrollview01));
                    break;
                }
                case 2: {
                    mInnerScrollView = (ViewGroup) (item.getView().findViewById(R.id.id_scrolllayout_innerscrollview02));
                    break;
                }
                case 3: {
                    mInnerScrollView = (ViewGroup) (item.getView().findViewById(R.id.id_scrolllayout_innerscrollview03));
                    break;
                }
            }
        }
    }

    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        invalidate();
    }

    /* 设置viewpage里的当前页面 */
    public void setCurrentItemPage(int page) {
        this.currentShowPage = page;
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > mTopViewHeight) {
            y = mTopViewHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }

        if (mScrollChangeListener != null) {
            mScrollChangeListener.onLayoutScroll(y, mTopViewHeight);
        }

        isTopHidden = getScrollY() == mTopViewHeight;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    //设置tabViewHeight
    public void setTabViewHeight(int tabViewHeight) {
        mTopViewHeight -= tabViewHeight;
        invalidate();
    }

    //获取tabViewHeight高度
    public int getTopViewHeight() {
        return mTopViewHeight;
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
