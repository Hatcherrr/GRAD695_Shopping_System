package edu.hu.ssbe.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hu.ssbe.bean.KafkaMessageBean;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaUtils {

    private static final Logger LOGGER = LogManager.getLogger(KafkaUtils.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ConfigurationUtils configurationUtils;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public String toJson(Object value) {
        String result = "";
        try {
            result = objectMapper.writer().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOGGER.error("failed to write json with error: {}", e);
        }
        return result;
    }

    public void sendToKafka(String resourceName, Object data, String type) {
        try {
            KafkaMessageBean messageBean = new KafkaMessageBean();
            String result = objectMapper.writer().writeValueAsString(data);
            messageBean.setData(result.getBytes());
            messageBean.setQos(1);
            messageBean.setRetryCount(1);
            messageBean.setResource(resourceName);
            messageBean.setType(type);
            String content = toJson(messageBean);
            ProducerRecord record = new ProducerRecord(configurationUtils.getKafkaTopic(), content);
            kafkaTemplate.send(record);
        } catch (JsonProcessingException e) {
            LOGGER.error("failed to convert data into json, {}", e);
        }
    }
}
