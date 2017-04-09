package com.mcomputing.Measurements;

import java.io.Serializable;

/**
 * Created by tau on 4/4/17.
 */



public class NetStats implements Serializable {
    public double receivedBytes;
    public double sentBytes;

    public NetStats(double recd, double sent)
    {
        this.receivedBytes = recd;
        this.sentBytes = sent;
    }
}
