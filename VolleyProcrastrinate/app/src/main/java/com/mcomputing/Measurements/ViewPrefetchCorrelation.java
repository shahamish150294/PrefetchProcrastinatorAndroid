package com.mcomputing.Measurements;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by tau on 3/24/17.
 */


public class ViewPrefetchCorrelation implements Serializable {

    public String viewName = "";
    public double prefetchCount = 0, viewCount = 0;

    public ViewPrefetchCorrelation(String name, int prefetchCount, int viewCount){
        this.prefetchCount = prefetchCount;
        this.viewName = name;
        this.viewCount = viewCount;
    }

    public double calculateCorrelation(){
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
