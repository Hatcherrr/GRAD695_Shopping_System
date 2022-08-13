package edu.hu.sync.utils;

import lombok.Builder;
import lombok.Data;
import org.apache.kafka.common.metrics.KafkaMetric;

@Data
@Builder
public class KafkaProfileMetric {
    private String name;
    private String key;
    private KafkaMetric metric;
}
