package com.mcomputing.procrastinate;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by tau on 4/3/17.
 */

public class Procastinator {

    private static Procastinator procastinator;
    private boolean requiresProcrastination = false;
    private Context context;
    private Procastinator(Context context){
        this.context = context;
    }

    public static Procastinator getInstance(Context context){
        if(procastinator == null){
            procastinator = new Procastinator(context);
        }

        return procastinator;
    }

    public boolean requiresProcrastination(boolean firstCall){

        if(firstCall)
        {
            //compute here
            final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final android.net.NetworkInfo acheiveNetworkInfo = connMgr.getActiveNetworkInfo();

            if (acheiveNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                requiresProcrastination = false;
            }else if (acheiveNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                requiresProcrastination = true;
            }

        }

        return requiresProcrastination;
    }
}
