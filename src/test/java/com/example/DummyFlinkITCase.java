package com.example;

import org.apache.flink.test.util.AbstractTestBase;
import org.junit.Test;


public class DummyFlinkITCase extends AbstractTestBase {

    @Test
    public void testDummy() throws Exception {

        // use StreamExecutionEnvironment.getExecutionEnvironment() to plan a Flink job
        // use DataStreamUtils.collect(stream) to get the job output.
    }
}
