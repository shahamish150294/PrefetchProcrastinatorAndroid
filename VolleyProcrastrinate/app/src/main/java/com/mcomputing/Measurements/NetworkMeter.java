package com.mcomputing.Measurements;

import android.content.Context;
import android.net.TrafficStats;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tau on 4/4/17.
 */

public class NetworkMeter {

    private HashMap<String, NetStats> netStatsHashMap;
    private static NetworkMeter instance;

    Context context;

    private NetworkMeter(Context context){
        netStatsHashMap = new HashMap<>();
        this.context = context;
        loadStatsData();
    }

    public static NetworkMeter getInstance(Context context){

        if(instance == null)
        {
            instance = new NetworkMeter(context);
        }

        return instance;
    }

    private void logData(){

        for(Map.Entry<String, NetStats> entry: netStatsHashMap.entrySet()){

            String className = entry.getKey();
            NetStats vp = entry.getValue();
            Log.d(className, "sent: "+vp.sentBytes + " recd: "+vp.receivedBytes);
        }
    }

    public void persistData(){

        try {
            // push the stats data to the server
            File file = new File(context.getFilesDir(), "netStatsMap");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(this.netStatsHashMap);
            outputStream.flush();
            outputStream.close();
        }
        catch(Exception ex)
        {
            Log.d("NetStats Exception", ex.getMessage());
        }
    }

    public void loadStatsData(){
        try {
            //fetch data from network
            File file = new File(context.getFilesDir(), "netStatsMap");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            netStatsHashMap = (HashMap<String, NetStats>) ois.readObject();
            ois.close();

            //logData();
        }
        catch(Exception ex)
        {
            Log.d("Load Exception", ex.getMessage());
        }
    }

    public String getStatsInText()
    {
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for(Map.Entry<String, NetStats> entry: netStatsHashMap.entrySet()){

            String className = entry.getKey();
            NetStats vp = entry.getValue();
            sb.append(count +": "+ className + " sent: "+vp.sentBytes + " recd: "+vp.receivedBytes+"\n");
            count++;
        }

        return sb.toString();
    }

    public void updateDailyStatsAndSave()
    {
        try {
            int uid = android.os.Process.myUid();
            double sent = TrafficStats.getUidTxBytes(uid);
            double recd = TrafficStats.getUidRxBytes(uid);

            Log.d("asd", "sentdd: "+sent);

            NetStats netStats = new NetStats(recd, sent);

            Date date = Calendar.getInstance().getTime();
            String today = new SimpleDateFormat("yyyy-MM-dd").format(date);

            this.netStatsHashMap.put(today, netStats);

            logData();

            persistData();
        }
        catch(Exception ex)
        {
            Log.e("NetMeterException", ex.getMessage());
        }
    }
}
