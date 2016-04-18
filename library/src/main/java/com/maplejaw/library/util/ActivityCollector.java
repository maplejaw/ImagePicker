/**   
* @author maplejaw 
* @date 2015-3-18 上午11:25:16   
* @description TODO
*/
package com.maplejaw.library.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<Activity>();  

    public static void addActivity(Activity activity) {  
        activities.add(activity);  
    }  
  
    public static void removeActivity(Activity activity) {  
        activities.remove(activity);  
    }  
  
    public static void finishAll() {  
        for (Activity activity : activities) {  
            if (!activity.isFinishing()) {  
                activity.finish();  
            }  
        }  
    }


    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activities.remove(activity);
            if (!activity.isFinishing()){
                activity.finish();
            }
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        for (Activity activity : activities) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }
}


