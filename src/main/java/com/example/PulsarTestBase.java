package com.example;

import io.streamnative.tests.pulsar.service.PulsarService;
import io.streamnative.tests.pulsar.service.PulsarServiceFactory;
import io.streamnative.tests.pulsar.service.PulsarServiceSpec;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.metrics.jmx.JMXReporter;
import org.apache.flink.streaming.util.TestStreamEnvironment;
import org.apache.flink.util.TestLogger;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.com.google.common.collect.Sets;

import java.net.URI;
import java.util.UUID;

public abstract class PulsarTestBase extends TestLogger {

    protected static final Logger LOG = LoggerFactory.getLogger(PulsarTestBase.class);

    protected static PulsarService pulsarService;

    protected static String serviceUrl;

    protected static String adminUrl;

    public static String getServiceUrl() {
        return serviceUrl;
    }

    public static String getAdminUrl() {
        return adminUrl;
    }

    @BeforeClass
    public static void prepare() throws Exception {

        LOG.info("-------------------------------------------------------------------------");
        LOG.info("    Starting PulsarTestBase ");
        LOG.info("-------------------------------------------------------------------------");

        PulsarServiceSpec spec = PulsarServiceSpec.builder()
            .clusterName("standalone-" + UUID.randomUUID())
            .enableContainerLogging(false)
            .build();

        pulsarService = PulsarServiceFactory.createPulsarService(spec);
        pulsarService.start();

        for (URI uri : pulsarService.getServiceUris()) {
            if (uri != null && uri.getScheme().equals("pulsar")) {
                serviceUrl = uri.toString();
            } else if (uri != null && !uri.getScheme().equals("pulsar")) {
                adminUrl = uri.toString();
            }
        }

        try (PulsarAdmin admin = PulsarAdmin.builder().serviceHttpUrl(adminUrl).build()) {
            admin.namespaces().createNamespace("public/default", Sets.newHashSet("standalone"));
        }

        LOG.info("-------------------------------------------------------------------------");
        LOG.info("Successfully started pulsar service at cluster " + spec.clusterName());
        LOG.info("-------------------------------------------------------------------------");

    }


    @AfterClass
    public static void shutDownServices() throws Exception {
        LOG.info("-------------------------------------------------------------------------");
        LOG.info("    Shut down PulsarTestBase ");
        LOG.info("-------------------------------------------------------------------------");

        TestStreamEnvironment.unsetAsContext();

        if (pulsarService != null) {
            pulsarService.stop();
        }

        LOG.info("-------------------------------------------------------------------------");
        LOG.info("    PulsarTestBase finished");
        LOG.info("-------------------------------------------------------------------------");
    }

    protected static Configuration getFlinkConfiguration() {
        Configuration flinkConfig = new Configuration();
        flinkConfig.setString(TaskManagerOptions.MANAGED_MEMORY_SIZE, "16m");
        flinkConfig.setString(ConfigConstants.METRICS_REPORTER_PREFIX + "my_reporter." + ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX, JMXReporter.class.getName());
        return flinkConfig;
    }
}
