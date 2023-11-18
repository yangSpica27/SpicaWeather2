package me.spica.base.tools

import android.app.Activity


class ActivityFinishUtil {
    private val activityList: MutableList<Activity> = ArrayList()

    fun addActivity(activity: Activity) {
        activityList.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activityList.remove(activity)
    }

    fun finishOtherAllActivity(activity: Activity) {
        for (item in activityList) {
            if (item === activity) {
                continue
            }
            item.finish()
        }
    }

    fun finishAllActivity() {
        for (item in activityList) {
            item.finish()
        }
    }

}