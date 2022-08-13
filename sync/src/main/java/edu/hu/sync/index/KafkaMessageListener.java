package edu.hu.sync.index;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hu.sync.bean.KafkaMessageBean;
import edu.hu.sync.utils.KafkaProfile;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.IOException;

public class KafkaMessageListener {

    private static final Logger LOGGER = LogManager.getLogger(KafkaMessageListener.class);

    private static final String SEARCH_TEMPLATE_TYPE = "ssbe_type";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private KafkaProfile kafkaProfile;

    @Autowired
    private BulkProcessor bulkProcessor;

    @Autowired
    private TransportClient searchClient;

    @KafkaListener(id = "SSBE_Sync_Consumer", topics = "search_sync")
    public void processMessages(ConsumerRecord<String, String> record) {
        LOGGER.info("----------record: {}", record);
        try {
            KafkaMessageBean messageBean = objectMapper.readValue(record.value(), KafkaMessageBean.class);
            process(messageBean);
        } catch (JsonProcessingException e) {
            LOGGER.error("failed to read kafka message: {}", e);
        }
    }

    public void process(KafkaMessageBean messageBean) {
        if (messageBean.getData() != null) {
            try {
                String requestType = messageBean.getType();
                String resourceName = messageBean.getResource();
                JsonNode row = objectMapper.readTree(messageBean.getData());
                LOGGER.info("-------data: {}", row);
                switch (requestType) {
                    case "insert": insertRequest(resourceName, row); break;
                    case "update": updateRequest(resourceName, row); break;
                    case "delete": deleteRequest(resourceName, row); break;
                    default:
                        LOGGER.info("request type not support");
                }
            } catch (IOException e) {
                LOGGER.error("field to read data from message: {}", e);
            }
        } else {
            LOGGER.warn("kafka message empty");
        }
    }

    public void insertRequest(String resourceName, JsonNode row) {
        String indexName = getIndexName(resourceName);
        String id = row.get("id").textValue();
        try {
            IndexRequest indexRequest = new IndexRequest(indexName, SEARCH_TEMPLATE_TYPE, id);
            String payload = objectMapper.writeValueAsString(row);
            indexRequest.source(payload, XContentType.JSON);
            bulkProcessor.add(indexRequest);
        } catch (JsonProcessingException e) {
            LOGGER.error("failed to convert json to string");
        }
    }

    public void updateRequest(String resourceName, JsonNode row) {
        String indexName = getIndexName(resourceName);
        String id = row.get("id").textValue();
        try {
            UpdateRequest updateRequest = new UpdateRequest(indexName, SEARCH_TEMPLATE_TYPE, id);
            String payload = objectMapper.writeValueAsString(row);
            updateRequest.doc(payload, XContentType.JSON);
            updateRequest.fetchSource(false);
            updateRequest.docAsUpsert(true);
            bulkProcessor.add(updateRequest);
        } catch (JsonProcessingException e) {
            LOGGER.error("failed to convert json to string");
        }
    }

    public void deleteRequest(String resourceName, JsonNode row) {
        String indexName = getIndexName(resourceName);
        String id = row.get("id").textValue();
        DeleteResponse rsp = searchClient.prepareDelete(indexName, "_doc", id).get();
        if (rsp.getResult() != DocWriteResponse.Result.DELETED) {
            LOGGER.error("failed to delete the document, id: {}", id);
            return;
        }
        LOGGER.info("document with index name: {}, id: {} is deleted", indexName, id);
    }

    public String getIndexName(String resourceName) {
        String result = "";
        switch (resourceName) {
            case "account": result = "ssbe_account"; break;
            case "order": result = "ssbe_order"; break;
            case "payment": result = "ssbe_payment"; break;
            case "provider": result = "ssbe_provider"; break;
            case "product": result = "ssbe_product"; break;
            default: result = "ssbe_default"; break;
        }
        return result;
    }
}
