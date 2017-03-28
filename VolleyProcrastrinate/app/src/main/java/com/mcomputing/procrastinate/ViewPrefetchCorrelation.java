package com.mcomputing.procrastinate;

import android.util.Log;

/**
 * Created by tau on 3/24/17.
 */

public class ViewPrefetchCorrelation {

    public String viewName = "";
    public int prefetchCount = 0, viewCount = 0;

    public ViewPrefetchCorrelation(String name, int prefetchCount, int viewCount){
        this.prefetchCount = prefetchCount;
        this.viewName = name;
        this.viewCount = viewCount;
    }

    public int calculateCorrelation(){
        try {
            return viewCount / prefetchCount;
        }
        catch(Exception ex)
        {
            Log.d("ViewPrefetchCorrelation", "Error occured while calculating correlation" +ex.getLocalizedMessage());
            return 0;
        }
    }
}
