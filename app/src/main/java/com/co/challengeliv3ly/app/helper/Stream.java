package com.co.challengeliv3ly.app.helper;

import android.support.core.helpers.ChainingEvent;

import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Stream<T> extends ChainingEvent<T> {
    private Stream mSource;

    public static <T> Stream<T> from(List<T> items) {
        return new SourceStream<>(items);
    }

    protected <S> void addSource(Stream<S> source, Observer<S> observer) {
        mSource = source;
        super.addSource(source, observer);
    }

    protected void start() {
        mSource.start();
    }

    protected void end() {
        mSource.end();
    }

    public T find(final Function<T, Boolean> function) {
        final AtomicReference<T> founded = new AtomicReference<>();
        new EndStream<>(this, t -> {
            if (function.apply(t)) {
                founded.set(t);
                Stream.this.end();
            }
        });
        return founded.get();
    }

    public void forEach(Consumer<T> consumer) {
        new EndStream<>(this, consumer::accept);
    }

    public Stream<T> filter(final Function<T, Boolean> function) {
        final Stream<T> stream = new Stream<>();
        stream.addSource(this, t -> {
            if (function.apply(t)) stream.next(t);
        });
        return stream;
    }

    public List<T> toList() {
        List<T> items = new ArrayList<>();
        new EndStream<>(this, items::add);
        return items;
    }

    public String reduce(String init, final Function3<String, T, String> function) {
        final AtomicReference<String> reduce = new AtomicReference<>();
        reduce.set(init);
        new EndStream<>(this, t -> reduce.set(function.apply(reduce.get(), t)));
        return reduce.get();
    }

    public static class SourceStream<T> extends Stream<T> {

        private final List<T> mItems;
        private boolean mBreak = false;

        SourceStream(List<T> items) {
            mItems = items;
        }

        @Override
        protected void start() {
            for (T item : mItems) {
                if (mBreak) break;
                next(item);
            }
        }

        @Override
        protected void end() {
            mBreak = true;
        }
    }

    public static class EndStream<T> extends Stream<T> {
        public EndStream(Stream<T> source, Observer<T> observer) {
            addSource(source, observer);
            start();
        }
    }

    public interface Function3<A, B, C> {

        C apply(A acc, B input);
    }
}
