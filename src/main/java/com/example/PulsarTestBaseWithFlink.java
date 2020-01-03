package com.example;

import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.junit.ClassRule;

public abstract class PulsarTestBaseWithFlink extends PulsarTestBase {

    protected static final int NUM_TMS = 1;

    protected static final int TM_SLOTS = 8;

    @ClassRule
    public static MiniClusterWithClientResource flink = new MiniClusterWithClientResource(
        new MiniClusterResourceConfiguration.Builder()
            .setConfiguration(getFlinkConfiguration())
            .setNumberTaskManagers(NUM_TMS)
            .setNumberSlotsPerTaskManager(TM_SLOTS)
            .build());
}
