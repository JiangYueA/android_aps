package com.example.jiangyue.androidap.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by linlinet on 14-8-19.
 */
public class _AbstractMainFragment extends Fragment implements OnListListener, View.OnClickListener {

    protected final String TAG = getClass().getSimpleName();
    private Bundle mBundle;
    protected boolean mIsCallBack;
    protected final int GALLERY = 0x1001;
    protected final int CAMERA = 0x1002;

    public interface FragmentCallBack {
        public void callback(Bundle bundle);
    }

    private FragmentCallBack _callBackListener;
    protected FragmentActivity context;
    protected OnListListener mCallback;
    protected LayoutInflater inflater;
    protected View contentView;
    protected boolean isCreateView;
    protected ActionBar mActionBar;

    public _AbstractMainFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnListListener) (context = (FragmentActivity) activity);
            mActionBar = getActionBar();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public TupuActionBar getTupuActionBar() {
//        return new TupuActionBar(getActivity(), ((AbstractActivity) getActivity()).getSupportActionBar());
//    }

    public Fragment setCallBack(FragmentCallBack callBackListener) {
        this._callBackListener = callBackListener;
        return this;
    }

    public void callback(Bundle bundle) {
        this.mIsCallBack = true;
        this.mBundle = bundle;
        onBack();
    }

    public void onClick(View v) {

//        switch (v.getId()){
//            case R.id.btn_back:
//                onBack();
//                break;
//        }
    }

    @Override
    public void onLoading() {
        if (mCallback != null)
            mCallback.onLoading();
    }

    @Override
    public void onLoading(String msg) {
        if (mCallback != null)
            mCallback.onLoading(msg);
    }

    @Override
    public void onLoading(String msg, int progress) {
        if (mCallback != null)
            mCallback.onLoading(msg, progress);
    }

    @Override
    public void finishLoading() {
        if (mCallback != null)
            mCallback.finishLoading();
    }

    @Override
    public void switchContent(Fragment fragment) {
        if (mCallback != null)
            mCallback.switchContent(fragment);
    }

    @Override
    public void addContent(Fragment fragment) {
        if (mCallback != null)
            mCallback.addContent(fragment);
    }

    @Override
    public void onBack() {
        if (mCallback != null)
            mCallback.onBack();
    }

    @Override
    public void reloadActivity() {
        if (mCallback != null)
            mCallback.reloadActivity();
    }

    @Override
    public void switchContent(int rid) {
        if (mCallback != null)
            mCallback.switchContent(rid);
    }

    @Override
    public void switchContent(int rid, int type) {
//        IDDLog.i("switchContent", "" + type);
        if (mCallback != null)
            mCallback.switchContent(rid, type);
    }

    @Override
    public ActionBar getActionBar() {
        if (mCallback != null)
            return mCallback.getActionBar();
        return null;
    }

    @Override
    public void setInputKeyPan(boolean allow) {
        if (mCallback != null)
            mCallback.setInputKeyPan(allow);
    }

    @Override
    public void setOnTouchListener(MyOnTouchListener listener) {
        if (mCallback != null)
            mCallback.setOnTouchListener(listener);
    }

    @Override
    public void removeOnTouchListener(MyOnTouchListener listener) {
        if (mCallback != null)
            mCallback.removeOnTouchListener(listener);
    }

    public boolean onSystemBack() {
        return false;
    }

    @Override
    public void onDestroy() {
//        IDDLog.i("", "------------------>onDestroy" + getActivity().getSupportFragmentManager().findFragmentById(R.id.content_frame));
        super.onDestroy();
        setInputKeyPan(true);

        if (mCallback != null) {
            if (_callBackListener != null && mIsCallBack) {
                _callBackListener.callback(mBundle);
            }
        }
    }

}
