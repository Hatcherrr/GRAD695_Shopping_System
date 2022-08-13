package edu.hu.ssbe.dao;

import com.datastax.driver.core.querybuilder.*;
import edu.hu.ssbe.bean.Account;
import edu.hu.ssbe.utils.KafkaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AccountDao {

    private final static String KEYSPACE = "data";
    private final static String TABLE = "account";

    @Autowired
    private KafkaUtils kafkaUtils;

    @Autowired
    private CassandraOperations cassandraOperations;

    public List<Account> findAll() {
        List<Account> result = null;
        Select select = QueryBuilder.select()
                .all()
                .from(KEYSPACE, TABLE);
        List rows = cassandraOperations.select(select, Account.class);
        if (rows != null && !rows.isEmpty()) {
             result = rows;
        }
        return result;
    }

    public Account findOne(String id) {
        Select select = QueryBuilder.select()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .limit(1);
        Account account = cassandraOperations.selectOne(select, Account.class);
        return account;
    }

    public boolean saveOne(Account account) {
        Insert insert = QueryBuilder.insertInto(KEYSPACE, TABLE)
                .value("id", UUID.fromString(account.getId()))
                .value("kind", account.getKind())
                .value("username", account.getUsername())
                .value("credentials", account.getCredentials())
                .value("password", account.getPassword())
                .value("createdon", account.getCreatedon())
                .value("lastupdate", account.getLastupdate())
                .value("address1", account.getAddress1())
                .value("address2", account.getAddress2())
                .value("address3", account.getAddress3())
                .value("city", account.getCity())
                .value("state", account.getState())
                .value("country", account.getCountry())
                .value("zipcode", account.getZipcode())
                .value("email", account.getEmail())
                .value("phone", account.getPhone())
                .value("status", account.getStatus());
        boolean result = cassandraOperations.getCqlOperations().execute(insert);
        kafkaUtils.sendToKafka("account", account, "insert");
        return result;
    }

    public boolean update(Account account) {
        Update update = QueryBuilder.update(KEYSPACE, TABLE);
        update.with(QueryBuilder.set("kind", account.getKind()))
                .and(QueryBuilder.set("username", account.getUsername()))
                .and(QueryBuilder.set("password", account.getPassword()))
                .and(QueryBuilder.set("lastupdate", account.getLastupdate()))
                .and(QueryBuilder.set("address1", account.getAddress1()))
                .and(QueryBuilder.set("address2", account.getAddress2()))
                .and(QueryBuilder.set("address3", account.getAddress3()))
                .and(QueryBuilder.set("city", account.getCity()))
                .and(QueryBuilder.set("state", account.getState()))
                .and(QueryBuilder.set("country", account.getCountry()))
                .and(QueryBuilder.set("zipcode", account.getZipcode()))
                .and(QueryBuilder.set("email", account.getEmail()))
                .and(QueryBuilder.set("phone", account.getPhone()))
                .and(QueryBuilder.set("status", account.getStatus()))
                .where(QueryBuilder.eq("id", UUID.fromString(account.getId())));
        boolean result = cassandraOperations.getCqlOperations().execute(update);
        kafkaUtils.sendToKafka("account", account, "update");
        return result;
    }

    public boolean deleteOne(String id) {
        Delete delete = QueryBuilder.delete()
                .from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq("id", UUID.fromString(id)))
                .ifExists();
        boolean result = cassandraOperations.getCqlOperations().execute(delete);
        kafkaUtils.sendToKafka("account", Account.builder().id(id).build(), "delete");
        return result;
    }
}
