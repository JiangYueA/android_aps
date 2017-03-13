package com.example.jiangyue.androidap.design.mvvm.rxbus;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by jiangyue on 17/2/22.
 */
public class RxBus {

    public static final String TAG = RxBus.class.getSimpleName();

    private static RxBus INSTANCE;
    private static boolean DEBUG = false;
    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<>();

    private RxBus() {
    }

    public static synchronized RxBus getInstance() {
        //在这里创建临时变量
        RxBus inst = INSTANCE;
        if (inst == null) {
            synchronized (RxBus.class) {
                inst = INSTANCE;
                if (inst == null) {
                    inst = new RxBus();
                    INSTANCE = inst;
                }
            }
        }
        //注意这里返回的是临时变量
        return inst;
    }

    @SuppressWarnings("unchecked")
    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> clazz) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            subjectMapper.put(tag, subjectList);
        }

        Subject<T, T> subject;
        subjectList.add(subject = PublishSubject.create());

        if (DEBUG) {
            Log.d(TAG, "[register]subjectMapper: " + subjectMapper);
        }
        return subject;
    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        List<Subject> subjects = subjectMapper.get(tag);
        if (null != subjects) {
            subjects.remove((Subject) observable);
            if (isEmpty(subjects)) {
                subjectMapper.remove(tag);
            }
        }

        if (DEBUG) {
            Log.d(TAG, "[unregister]subjectMapper: " + subjectMapper);
        }
    }

    public void postEvent(@NonNull Object content) {
        postEvent(content.getClass().getName(), content);
    }

    @SuppressWarnings("unchecked")
    public void postEvent(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjectList = subjectMapper.get(tag);

        if (!isEmpty(subjectList)) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }

        if (DEBUG) {
            Log.d(TAG, "[send]subjectMapper: " + subjectMapper);
        }
    }

    public static boolean isEmpty(List list) {
        if (list == null) {
            return true;
        } else if (list.size() == 0) {
            return true;
        }
        return false;
    }
}
