package com.example.jiangyue.androidap.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.jiangyue.androidap.R;

/**
 * Created by jiangyue on 16/7/28.
 */
public class ListFragment extends _AbstractMainFragment {

    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
            this.inflater = inflater;
            contentView = inflater.inflate(R.layout.fragment_list, container, false);

            initView(contentView);
            initViewData();
        }
        return contentView;
    }

    //初始化视图
    public void initView(View view) {
        listView = (ListView) view.findViewById(R.id.id_scrolllayout_innerscrollview01);
    }

    /* 初始化视图参数 */
    protected void initViewData() {
        ListAdapter adapter = new ListAdapter();
        listView.setAdapter(adapter);
    }

    /************************************************************************/
    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_listview, null);
            return convertView;
        }
    }
}
