package com.syahputrareno975.heremapexample.di.module;

import android.app.Application;

import com.syahputrareno975.heremapexample.BaseApp;
import com.syahputrareno975.heremapexample.di.scope.PerApplication;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private BaseApp baseApp;

    public ApplicationModule(BaseApp baseApp){
        this.baseApp = baseApp;
    }

    @Provides
    @Singleton
    @PerApplication
    public Application provideApplication() {
        return baseApp;
    }
}