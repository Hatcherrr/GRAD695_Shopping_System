package edu.hu.ssbe.dao;

import com.datastax.driver.core.querybuilder.*;
import edu.hu.ssbe.bean.Order;
import edu.hu.ssbe.utils.ConfigurationUtils;
import edu.hu.ssbe.utils.KafkaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderDao {

    private final static String KEYSPACE = "data";
    private final static String TABLE = "order_new";

    @Autowired
    private KafkaUtils kafkaUtils;

    @Autowired
    private CassandraOperations cassandraOperations;

    public List<Order> findAll() {
        List<Order> result = null;
        Select select = QueryBuilder.select()
                .all()
                .from(KEYSPACE, TABLE);
        List rows = cassandraOperations.select(select, Order.class);
        if (rows != null && !rows.isEmpty()) {
            result = rows;
        }
        return result;
    }

    public Order findOne(String id, String foreignid, String productid) {
        Select select = QueryBuilder.select()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .and(QueryBuilder.eq("foreignid", UUID.fromString(foreignid)))
                .and(QueryBuilder.eq("productid", UUID.fromString(productid)))
                .limit(1);
        Order order = cassandraOperations.selectOne(select, Order.class);
        return order;
    }

    public Order findOne(String id) {
        Select select = QueryBuilder.select()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .limit(1);
        Order order = cassandraOperations.selectOne(select, Order.class);
        return order;
    }

    public boolean saveOne(Order order) {
        Insert insert = QueryBuilder.insertInto(KEYSPACE, TABLE)
                .value("id", UUID.fromString(order.getId()))
                .value("foreignid",UUID.fromString(order.getForeignid()))
                .value("productid", UUID.fromString(order.getProductid()))
                .value("createdon", order.getCreatedon())
                .value("lastupdate", order.getLastupdate())
                .value("paymentid", UUID.fromString(order.getPaymentid()))
                .value("amount", order.getAmount())
                .value("address", order.getAddress())
                .value("status", order.getStatus());
        boolean result = cassandraOperations.getCqlOperations().execute(insert);
        kafkaUtils.sendToKafka("order", order, "insert");
        return result;
    }

    public boolean update(Order order) {
        Update update = QueryBuilder.update(KEYSPACE, TABLE);
        update.with(QueryBuilder.set("lastupdate", order.getLastupdate()))
                .and(QueryBuilder.set("paymentid", order.getPaymentid()))
                .and(QueryBuilder.set("amount", order.getAmount()))
                .and(QueryBuilder.set("address", order.getAddress()))
                .and(QueryBuilder.set("status", order.getStatus()))
                .where(QueryBuilder.eq("id", UUID.fromString(order.getId())))
                .and(QueryBuilder.eq("foreignid", UUID.fromString(order.getForeignid())))
                .and(QueryBuilder.eq("productid", UUID.fromString(order.getProductid())));
        boolean result = cassandraOperations.getCqlOperations().execute(update);
        kafkaUtils.sendToKafka("order", order, "update");
        return result;
    }

    public boolean deleteOne(String id, String foreignid, String productid) {
        Delete delete = QueryBuilder.delete()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .and(QueryBuilder.eq("foreignid", UUID.fromString(foreignid)))
                .and(QueryBuilder.eq("productid", UUID.fromString(productid)))
                .ifExists();
        boolean result = cassandraOperations.getCqlOperations().execute(delete);
        Order order = Order.builder()
                .id(id)
                .foreignid(foreignid)
                .productid(productid)
                .build();
        kafkaUtils.sendToKafka("order", order, "delete");
        return result;
    }

    public boolean deleteOne(String id) {
        Delete delete = QueryBuilder.delete()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .ifExists();
        boolean result = cassandraOperations.getCqlOperations().execute(delete);
        Order order = Order.builder()
                .id(id)
                .build();
        kafkaUtils.sendToKafka("order", order, "delete");
        return result;
    }
}
