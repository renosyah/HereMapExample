package com.syahputrareno975.heremapexample.di.module;

import android.app.Activity;

import com.syahputrareno975.heremapexample.ui.activity.MapActivityContract;
import com.syahputrareno975.heremapexample.ui.activity.MapActivityPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private Activity activity;

    public ActivityModule(Activity activity){
        this.activity = activity;
    }

    @Provides
    public Activity provideActivity()  {
        return activity;
    }


    // add more
    @Provides
    public MapActivityContract.Presenter provideMapActivityPresenter() {
        return new MapActivityPresenter();
    }
}
