package edu.hu.sync.config;

import edu.hu.sync.utils.ConfigurationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class SearchConfig {

    private static final Logger LOGGER = LogManager.getLogger(SearchConfig.class);

    @Autowired
    private ConfigurationUtils configurationUtils;

    @Bean
    public TransportClient highPriorityClient() throws UnknownHostException {
        return createClient();
    }

    @Bean
    public BulkProcessor highPriorityBulkProcessor() throws UnknownHostException {
        return createBulkProcessor(createClient(), 1);
    }

    private TransportClient createClient() throws UnknownHostException {
        System.setProperty("es.set.netty.runtime.available.processors", "false");

        Settings settings = Settings.builder()
                .put("client.transport.ignore_cluster_name", "true")
                .put("client.transport.ping_timeout", "10s")
                .put("client.transport.sniff", configurationUtils.getSniff())
                .build();

        TransportClient client = new PreBuiltTransportClient(settings);

        client.addTransportAddress(
                new TransportAddress(
                        InetAddress.getByName(configurationUtils.getSearchSeeds()), configurationUtils.getSearchPort()
                )
        );

        return client;
    }

    private BulkProcessor createBulkProcessor(TransportClient client, int flushInterval) {
        return BulkProcessor.builder(client, new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long l, BulkRequest bulkRequest) {
                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest bulkRequest, BulkResponse bulkResponse) {
                        LOGGER.info("BulkProcess {} took {}, {} documents", executionId, bulkResponse.getTook(), bulkResponse.getItems().length);
                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest bulkRequest, Throwable throwable) {
                        LOGGER.error("BulkProcess failed to apply batch for execution id {}", executionId, throwable);
                    }
                })
                .setBulkActions(configurationUtils.getEsBulkSize())
                .setBulkSize(new ByteSizeValue(20, ByteSizeUnit.MB))
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 8))
                .setFlushInterval(TimeValue.timeValueSeconds(flushInterval))
                .setConcurrentRequests(5)
                .build();
    }
}
