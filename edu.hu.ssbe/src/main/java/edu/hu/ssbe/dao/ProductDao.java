package edu.hu.ssbe.dao;

import com.datastax.driver.core.querybuilder.*;
import edu.hu.ssbe.bean.Payment;
import edu.hu.ssbe.bean.Product;
import edu.hu.ssbe.utils.KafkaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ProductDao {

    private final static String KEYSPACE = "data";
    private final static String TABLE = "product";

    @Autowired
    private KafkaUtils kafkaUtils;

    @Autowired
    private CassandraOperations cassandraOperations;

    public List<Product> findAll() {
        List<Product> result = null;
        Select select = QueryBuilder.select()
                .all()
                .from(KEYSPACE, TABLE);
        List rows = cassandraOperations.select(select, Product.class);
        if (rows != null && !rows.isEmpty()) {
            result = rows;
        }
        return result;
    }

    public Product findOne(String id) {
        Select select = QueryBuilder.select()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .limit(1);
        Product product = cassandraOperations.selectOne(select, Product.class);
        return product;
    }

    public boolean saveOne(Product product) {
        Insert insert = QueryBuilder.insertInto(KEYSPACE, TABLE)
                .value("id", UUID.fromString(product.getId()))
                .value("providerid", UUID.fromString(product.getProviderid()))
                .value("createdon", product.getCreatedon())
                .value("lastupdate", product.getLastupdate())
                .value("name", product.getName())
                .value("description", product.getDescription())
                .value("number", product.getAmount())
                .value("tags", product.getTags())
                .value("pictures", product.getPictures())
                .value("status", product.getStatus());
        boolean result = cassandraOperations.getCqlOperations().execute(insert);
        kafkaUtils.sendToKafka("product", product, "insert");
        return result;
    }

    public boolean update(Product product) {
        Update update = QueryBuilder.update(KEYSPACE, TABLE);
        update.with(QueryBuilder.set("lastupdate", product.getLastupdate()))
                .and(QueryBuilder.set("name", product.getName()))
                .and(QueryBuilder.set("description", product.getDescription()))
                .and(QueryBuilder.set("number", product.getAmount()))
                .and(QueryBuilder.set("tags", product.getTags()))
                .and(QueryBuilder.set("pictures", product.getPictures()))
                .and(QueryBuilder.set("status", product.getStatus()))
                .where(QueryBuilder.eq("id", UUID.fromString(product.getId())));
        boolean result = cassandraOperations.getCqlOperations().execute(update);
        kafkaUtils.sendToKafka("product", product, "update");
        return result;
    }

    public boolean deleteOne(String id) {
        Delete delete = QueryBuilder.delete()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .ifExists();
        boolean result = cassandraOperations.getCqlOperations().execute(delete);
        kafkaUtils.sendToKafka("product", Product.builder().id(id).build(), "delete");
        return result;
    }
}
