package com.github.ypicoleal.postdemo.dagger

import com.github.ypicoleal.postdemo.PostApplication
import com.github.ypicoleal.postdemo.MainActivity
import com.github.ypicoleal.postdemo.PostActivity
import com.github.ypicoleal.postdemo.ui.main.PostFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(ApplicationModule::class)])
interface ApplicationComponent {

    fun inject(application: PostApplication)

    fun inject(fragment: PostFragment)

    fun inject(activity: PostActivity)
}
