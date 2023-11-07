package com.fmt.mvi.learn

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
//    fun getProxy(context: Context): HttpProxyCacheServer? {
//        val app: com.ch.doudemo.base.MyApp = context.applicationContext as com.ch.doudemo.base.MyApp
//        return if (app.proxy == null) app.newProxy().also { app.proxy = it } else app.proxy
//    }
//
//    private fun newProxy(): HttpProxyCacheServer? {
//        return Builder(this)
//            .maxCacheSize(1024 * 1024 * 1024) // 1 Gb for cache
//            .fileNameGenerator(MyFileNameGenerator())
//            .build()
//    }
}