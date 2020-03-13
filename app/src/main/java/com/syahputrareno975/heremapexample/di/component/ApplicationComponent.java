package com.syahputrareno975.heremapexample.di.component;

import com.syahputrareno975.heremapexample.BaseApp;
import com.syahputrareno975.heremapexample.di.module.ApplicationModule;
import dagger.Component;

@Component(modules = { ApplicationModule.class })
public interface ApplicationComponent {
    void inject(BaseApp application);
    // add for each new base
}