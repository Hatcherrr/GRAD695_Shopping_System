package edu.hu.ssbe.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationUtils {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationUtils.class);

    @Value("${ssbe.cassandra.host}")
    private String cassandraHost;

    @Value("${ssbe.cassandra.port}")
    private int cassandraPort;

    @Value("${ssbe.es.transport.sniff}")
    private boolean sniff;

    @Value("${ssbe.es.seeds}")
    private String esSeeds;

    @Value("${ssbe.es.port}")
    private int esPort;

    @Value("${ssbe.es.bulkSize}")
    private int esBulkSize;

    @Value("${ssbe.kafka.bootstrap-server}")
    private String kafkaSeed;

    @Value("${ssbe.kafka.topic}")
    private String kafkaTopic;

    public String getCassandraHost() {
//        LOGGER.info("------------host: {}", cassandraHost);
        return cassandraHost;
    }

    public int getCassandraPort() {
//        LOGGER.info("------------port: {}", cassandraPort);
        return cassandraPort;
    }

    public boolean getSniff() {
        return sniff;
    }

    public String getSearchSeeds() {
        return esSeeds;
    }

    public int getSearchPort() {
        return esPort;
    }

    public int getEsBulkSize() {
        return esBulkSize;
    }

    public String getKafkaSeed() {
        return kafkaSeed;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }
}
