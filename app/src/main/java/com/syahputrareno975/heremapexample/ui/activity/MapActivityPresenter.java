package com.syahputrareno975.heremapexample.ui.activity;

public class MapActivityPresenter implements MapActivityContract.Presenter {

    private MapActivityContract.View view;

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void attach(MapActivityContract.View view) {
        this.view = view;
    }
}
