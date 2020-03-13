package com.syahputrareno975.heremapexample.base;

public class BaseContract {
    public interface Presenter<T> {
        void subscribe();
        void unsubscribe();
        void attach(T view);
    }

    public interface View {}
}
