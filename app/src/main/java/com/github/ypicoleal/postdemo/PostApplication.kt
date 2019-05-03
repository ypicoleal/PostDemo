package com.github.ypicoleal.postdemo

import android.app.Application
import com.github.ypicoleal.postdemo.dagger.ApplicationComponent
import com.github.ypicoleal.postdemo.dagger.ApplicationModule
import com.github.ypicoleal.postdemo.dagger.DaggerApplicationComponent

class PostApplication: Application() {

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

}