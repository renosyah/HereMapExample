package com.syahputrareno975.heremapexample.di.component;

import com.syahputrareno975.heremapexample.di.module.ActivityModule;
import com.syahputrareno975.heremapexample.ui.activity.MapActivity;

import dagger.Component;

@Component(modules = { ActivityModule.class })
public interface ActivityComponent {

    // add for each new activity
    void inject(MapActivity mapActivity);
}
