package com.fmt.mvi.learn.video.action

import android.annotation.SuppressLint
import android.content.Context


@SuppressLint("StaticFieldLeak")
object VideoHelper {
    //SDK全局Context
    var context: Context? = null
        private set

    fun init(context: Context?) {
        VideoHelper.context = context
        //初始化SDK的时候，初始化Realm数据库
    }
}