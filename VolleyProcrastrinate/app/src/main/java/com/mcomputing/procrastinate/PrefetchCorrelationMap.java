package com.mcomputing.procrastinate;

import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;


/**
 * Created by tau on 3/24/17.
 */

public class PrefetchCorrelationMap {

    private static PrefetchCorrelationMap prefetchCorrelationMapInstance;

    public HashMap<String, ViewPrefetchCorrelation> correlationMap;

    private PrefetchCorrelationMap()
    {
        correlationMap = new HashMap<String, ViewPrefetchCorrelation>();
        loadCorrelationData();
    }

    private void loadCorrelationData(){
        try {
            //fetch data from network
            FileInputStream fis = new FileInputStream("correlationMap");
            ObjectInputStream ois = new ObjectInputStream(fis);
            correlationMap = (HashMap<String, ViewPrefetchCorrelation>) ois.readObject();
            ois.close();
        }
        catch(Exception ex)
        {
            Log.d("Load Exception", "Something went wrong here");
        }
    }

    public void persistData(){

        try {
            // push the correlation data to the server
            File file = new File("correlationMap");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(correlationMap);
            outputStream.flush();
            outputStream.close();
        }
        catch(Exception ex)
        {
            Log.d("Correlation Exception", "Error while writing data to file");
        }
    }

    public void incrementPrefetchCount(String activityName)
    {
        if(!PrefetchCorrelationMap.getInstance().correlationMap.containsKey(activityName))
        {
            ViewPrefetchCorrelation vp = new ViewPrefetchCorrelation(activityName, 0, 0);
            PrefetchCorrelationMap.getInstance().correlationMap.put(activityName, vp);
        }

        PrefetchCorrelationMap.getInstance().correlationMap.get(activityName).prefetchCount += 1;
    }

    public void incrementActivityViewCount(String nextActivityName)
    {

        if(!PrefetchCorrelationMap.getInstance().correlationMap.containsKey(nextActivityName))
        {
            ViewPrefetchCorrelation vp = new ViewPrefetchCorrelation(nextActivityName, 0, 0);
            PrefetchCorrelationMap.getInstance().correlationMap.put(nextActivityName, vp);
        }

        PrefetchCorrelationMap.getInstance().correlationMap.get(nextActivityName).viewCount += 1;
    }

    public static synchronized PrefetchCorrelationMap getInstance(){

        if(prefetchCorrelationMapInstance == null)
        {
            prefetchCorrelationMapInstance = new PrefetchCorrelationMap();
        }

        return prefetchCorrelationMapInstance;
    }
}
