package edu.hu.ssbe.dao;

import com.datastax.driver.core.querybuilder.*;
import edu.hu.ssbe.bean.Payment;
import edu.hu.ssbe.utils.KafkaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PaymentDao {

    private final static String KEYSPACE = "data";
    private final static String TABLE = "payment";

    @Autowired
    private KafkaUtils kafkaUtils;

    @Autowired
    private CassandraOperations cassandraOperations;

    public List<Payment> findAll() {
        List<Payment> result = null;
        Select select = QueryBuilder.select()
                .all()
                .from(KEYSPACE, TABLE);
        List rows = cassandraOperations.select(select, Payment.class);
        if (rows != null && !rows.isEmpty()) {
            result = rows;
        }
        return result;
    }

    public Payment findOne(String id, String foreignid) {
        Select select = QueryBuilder.select()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .and(QueryBuilder.eq("foreignid", UUID.fromString(foreignid)))
                .limit(1);
        Payment payment = cassandraOperations.selectOne(select, Payment.class);
        return payment;
    }

    public Payment findOne(String id) {
        Select select = QueryBuilder.select()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .limit(1);
        Payment payment = cassandraOperations.selectOne(select, Payment.class);
        return payment;
    }

    public boolean saveOne(Payment payment) {
        Insert insert = QueryBuilder.insertInto(KEYSPACE, TABLE)
                .value("id", UUID.fromString(payment.getId()))
                .value("foreignid", payment.getForeignid())
                .value("createdon", payment.getCreatedon())
                .value("lastupdate", payment.getLastupdate())
                .value("method", payment.getMethod())
                .value("cardno", payment.getCardNumber())
                .value("amount", payment.getAmount())
                .value("status", payment.getStatus());
        boolean result = cassandraOperations.getCqlOperations().execute(insert);
        kafkaUtils.sendToKafka("payment", payment, "insert");
        return result;
    }

    public boolean update(Payment payment) {
        Update update = QueryBuilder.update(KEYSPACE, TABLE);
        update.with(QueryBuilder.set("lastupdate", payment.getLastupdate()))
                .and(QueryBuilder.set("method", payment.getMethod()))
                .and(QueryBuilder.set("cardno", payment.getMethod()))
                .and(QueryBuilder.set("amount", payment.getAmount()))
                .and(QueryBuilder.set("status", payment.getStatus()))
                .where(QueryBuilder.eq("id", UUID.fromString(payment.getId())))
                .and(QueryBuilder.eq("foreignid", UUID.fromString(payment.getForeignid())));
        boolean result = cassandraOperations.getCqlOperations().execute(update);
        kafkaUtils.sendToKafka("payment", payment, "update");
        return result;
    }

    public boolean deleteOne(String id, String foreignid) {
        Delete delete = QueryBuilder.delete()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .and(QueryBuilder.eq("foreignid", UUID.fromString(foreignid)))
                .ifExists();
        boolean result = cassandraOperations.getCqlOperations().execute(delete);
        Payment payment = Payment.builder()
                .id(id)
                .foreignid(foreignid)
                .build();
        kafkaUtils.sendToKafka("payment", payment, "delete");
        return result;
    }

    public boolean deleteOne(String id) {
        Delete delete = QueryBuilder.delete()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .ifExists();
        boolean result = cassandraOperations.getCqlOperations().execute(delete);
        Payment payment = Payment.builder()
                .id(id)
                .build();
        kafkaUtils.sendToKafka("payment", payment, "delete");
        return result;
    }
}
