package com.mcomputing.Measurements;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by tau on 3/24/17.
 */

public class PrefetchCorrelationMap {

    private static PrefetchCorrelationMap prefetchCorrelationMapInstance;

    public HashMap<String, ViewPrefetchCorrelation> correlationMap;

    private Context context;

    private PrefetchCorrelationMap(Context context)
    {
        correlationMap = new HashMap<String, ViewPrefetchCorrelation>();
        this.context = context;
        loadCorrelationData();
    }

    public void loadCorrelationData(){
        try {
            //fetch data from network
            File file = new File(context.getFilesDir(), "correlationMap");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            correlationMap = (HashMap<String, ViewPrefetchCorrelation>) ois.readObject();
            ois.close();

            //logData();
        }
        catch(Exception ex)
        {
            Log.d("Load Exception", ex.getMessage());
        }
    }

    public String getVpcInText()
    {
        StringBuilder sb = new StringBuilder();
        int count  = 1;
        for(Map.Entry<String, ViewPrefetchCorrelation> entry: this.correlationMap.entrySet()){

            String className = entry.getKey();
            ViewPrefetchCorrelation vp = entry.getValue();
            sb.append(count +" : " + vp.viewName + " View Count: " + vp.viewCount + " Prefetch Count: " + vp.prefetchCount + "VPC: " + vp.calculateCorrelation() + "\n\n");
            count++;
        }

        return sb.toString();
    }

    public void persistData(){

        try {
            // push the correlation data to the server
            File file = new File(context.getFilesDir(), "correlationMap");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(correlationMap);
            outputStream.flush();
            outputStream.close();

            //logData();
        }
        catch(Exception ex)
        {
            Log.d("Correlation Exception", ex.getMessage());
        }
    }

    private void logData(){

        for(Map.Entry<String, ViewPrefetchCorrelation> entry: correlationMap.entrySet()){

            String className = entry.getKey();
            ViewPrefetchCorrelation vp = entry.getValue();
            Log.d("Storing Data:" + className, vp.viewName + " View Count: " + vp.viewCount + "Prefetch Count: " + vp.prefetchCount + "VPC: " + vp.calculateCorrelation());
        }
    }

    public void incrementPrefetchCount(String activityName)
    {
        if(!PrefetchCorrelationMap.getInstance(context).correlationMap.containsKey(activityName))
        {
            ViewPrefetchCorrelation vp = new ViewPrefetchCorrelation(activityName, 0, 0);
            PrefetchCorrelationMap.getInstance(context).correlationMap.put(activityName, vp);
        }

        PrefetchCorrelationMap.getInstance(context).correlationMap.get(activityName).prefetchCount += 1;
        logData();
    }

    public void incrementActivityViewCount(String nextActivityName)
    {

        if(!PrefetchCorrelationMap.getInstance(context).correlationMap.containsKey(nextActivityName))
        {
            ViewPrefetchCorrelation vp = new ViewPrefetchCorrelation(nextActivityName, 0, 0);
            PrefetchCorrelationMap.getInstance(context).correlationMap.put(nextActivityName, vp);
        }

        PrefetchCorrelationMap.getInstance(context).correlationMap.get(nextActivityName).viewCount += 1;
        logData();
    }

    public ViewPrefetchCorrelation getCorrelationDataForActivity(String nextActivityName)
    {
        return PrefetchCorrelationMap.getInstance(context).correlationMap.get(nextActivityName);
    }

    public static synchronized PrefetchCorrelationMap getInstance(Context context){

        if(prefetchCorrelationMapInstance == null)
        {
            prefetchCorrelationMapInstance = new PrefetchCorrelationMap(context);
        }

        return prefetchCorrelationMapInstance;
    }
}
