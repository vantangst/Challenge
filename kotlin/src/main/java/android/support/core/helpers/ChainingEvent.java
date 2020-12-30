package android.support.core.helpers;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.Map;

public class ChainingEvent<T> {

    private Map<ChainingEvent, Observer> mNextEvent = new HashMap<>();
    private boolean mActivated = false;
    private T mValue = null;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public Handler getHandler() {
        return mHandler;
    }

    protected void next() {
        next(null);
    }

    protected void next(T value) {
        mActivated = true;
        mValue = value;
        for (ChainingEvent chainingEvent : mNextEvent.keySet()) {
            mNextEvent.get(chainingEvent).onChanged(mValue);
        }
    }

    public T getValue() {
        return mValue;
    }

//    protected void postNext(T value) {
//        mHandler.post(() -> next(value));
//    }

    protected <S> void addSource(ChainingEvent<S> source, Observer<S> observer) {
        source.addNextEvent(this, observer);
        source.notifyIfCan(observer);
    }

    private void notifyIfCan(Observer<T> observer) {
        if (mActivated) observer.onChanged(mValue);
    }

    private <S> void addNextEvent(ChainingEvent<S> next, Observer<T> observer) {
        mNextEvent.put(next, observer);
    }

    protected <S> void removeSource(ChainingEvent<S> source) {
        source.mNextEvent.remove(this);
    }

//    public <S> ChainingEvent<S> switchTo(Function<T, ChainingEvent<S>> function) {
//        ChainingEvent<S> result = new ChainingEvent<>();
//        result.addSource(this, new Observer<T>() {
//            ChainingEvent<S> mSource = null;
//
//            @Override
//            public void onChanged(@Nullable T t) {
//                ChainingEvent<S> newSource = function.apply(t);
//                if (mSource == newSource) return;
//                if (mSource != null) result.removeSource(mSource);
//                mSource = newSource;
//                if (mSource != null) result.addSource(mSource, result::next);
//            }
//        });
//        return result;
//    }
}
