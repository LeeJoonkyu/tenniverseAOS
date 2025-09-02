package android.lecture.myapplication.util

import android.util.Log


val Any.name: String get() = this.javaClass.name

fun DEBUG(tag: String, msg: String) {
    Log.d(tag, msg)
}