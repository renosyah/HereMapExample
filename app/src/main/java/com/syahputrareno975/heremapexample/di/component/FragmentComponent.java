package com.syahputrareno975.heremapexample.di.component;

import com.syahputrareno975.heremapexample.di.module.FragmentModule;
import dagger.Component;

@Component(modules = { FragmentModule.class })
public interface FragmentComponent {
    // add for each new fragment
}
