package edu.hu.ssbe.dao;

import com.datastax.driver.core.querybuilder.*;
import edu.hu.ssbe.bean.Provider;
import edu.hu.ssbe.utils.KafkaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ProviderDao {

    private final static String KEYSPACE = "data";
    private final static String TABLE = "provider";

    @Autowired
    private KafkaUtils kafkaUtils;

    @Autowired
    private CassandraOperations cassandraOperations;

    public List<Provider> findAll() {
        List<Provider> result = null;
        Select select = QueryBuilder.select()
                .all()
                .from(KEYSPACE, TABLE);
        List rows = cassandraOperations.select(select, Provider.class);
        if (rows != null && !rows.isEmpty()) {
            result = rows;
        }
        return result;
    }

    public Provider findOne(String id) {
        Select select = QueryBuilder.select()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .limit(1);
        Provider provider = cassandraOperations.selectOne(select, Provider.class);
        return provider;
    }

    public boolean saveOne(Provider provider) {
        Insert insert = QueryBuilder.insertInto(KEYSPACE, TABLE)
                .value("id", UUID.fromString(provider.getId()))
                .value("name", provider.getName())
                .value("createdon", provider.getCreatedon())
                .value("contact1", provider.getContact1())
                .value("contact2", provider.getContact2());
        boolean result = cassandraOperations.getCqlOperations().execute(insert);
        kafkaUtils.sendToKafka("provider", provider, "insert");
        return result;
    }

    public boolean update(Provider provider) {
        Update update = QueryBuilder.update(KEYSPACE, TABLE);
        update.with(QueryBuilder.set("name", provider.getName()))
                .and(QueryBuilder.set("contact1", provider.getContact1()))
                .and(QueryBuilder.set("contact2", provider.getContact2()))
                .where(QueryBuilder.eq("id", UUID.fromString(provider.getId())));
        boolean result = cassandraOperations.getCqlOperations().execute(update);
        kafkaUtils.sendToKafka("provider", provider, "update");
        return result;
    }

    public boolean deleteOne(String id) {
        Delete delete = QueryBuilder.delete()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .ifExists();
        boolean result = cassandraOperations.getCqlOperations().execute(delete);
        kafkaUtils.sendToKafka("provider", Provider.builder().id(id).build(), "delete");
        return result;
    }
}
