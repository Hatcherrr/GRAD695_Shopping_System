package edu.hu.sync.utils;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.MetricsReporter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class KafkaProfile implements MetricsReporter {

    private static Map<String, KafkaProfileMetric> profileMetrics = new LinkedHashMap<>();

    @Override
    public void init(List<KafkaMetric> list) {
        list.forEach(kafkaMetric -> {
            metricChange(kafkaMetric);
        });
    }

    private String getMetricName(KafkaMetric metric) {
        MetricName metricName = metric.metricName();
        return metricName.group() + "." + metricName.name();
    }

    @Override
    public void metricChange(KafkaMetric kafkaMetric) {
        String name = getMetricName(kafkaMetric);
        StringBuilder builder = new StringBuilder();
        for (String key : kafkaMetric.metricName().tags().keySet()) {
            builder.append(key)
                    .append(":")
                    .append(kafkaMetric.metricName().tags().get(key))
                    .append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        profileMetrics.put(name, KafkaProfileMetric.builder()
                .name(name)
                .key(builder.toString())
                .metric(kafkaMetric)
                .build());
    }

    @Override
    public void metricRemoval(KafkaMetric kafkaMetric) {
        profileMetrics.remove(getMetricName(kafkaMetric));
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> map) {
    }
}
