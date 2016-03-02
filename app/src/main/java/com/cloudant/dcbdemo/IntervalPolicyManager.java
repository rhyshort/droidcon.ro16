package com.cloudant.dcbdemo;

import com.cloudant.sync.replication.PeriodicReplicationReceiver;

/**
 * Created by Rhys Short on 27/01/2016.
 */
public class IntervalPolicyManager extends PeriodicReplicationReceiver {

    public IntervalPolicyManager() {
        super(com.cloudant.dcbdemo.ReplicationService.class);
    }

}
