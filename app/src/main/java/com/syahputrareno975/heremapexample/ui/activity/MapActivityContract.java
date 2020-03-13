package com.syahputrareno975.heremapexample.ui.activity;

import com.syahputrareno975.heremapexample.base.BaseContract;

public class MapActivityContract {
    public interface View extends BaseContract.View {
        public void showProgress(Boolean show);
        public void showError(String error);
    }

    public interface Presenter extends BaseContract.Presenter<View> {

    }
}
