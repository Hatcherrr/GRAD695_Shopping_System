package edu.hu.sync.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMessageBean {
    @JsonProperty("resource_name")
    private String resource;
    private String type;
    private byte[] data;
    private Date timestamp;
    private int qos;
    @JsonProperty("retry_count")
    private int retryCount;
}
