/*
 * Copyright (c) 2017 Disney. All rights reserved.
 */

package com.github.ypicoleal.postdemo.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger Module to provide Application dependencies
 */
@Module
class ApplicationModule(private val context: Context) {

    @Provides
    @Singleton
    internal fun providesContext() = context
}
