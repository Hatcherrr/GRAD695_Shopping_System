package edu.hu.ssbe.service;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.hu.ssbe.bean.Account;
import edu.hu.ssbe.bean.KafkaMessageBean;
import edu.hu.ssbe.dao.AccountDao;
import edu.hu.ssbe.utils.ConfigurationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private static final Logger LOGGER = LogManager.getLogger(AccountService.class);

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ConfigurationUtils configurationUtils;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public List<Account> getAccounts() {
        return accountDao.findAll();
    }

    public Account getAccount(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return accountDao.findOne(id);
    }

    public Account addAccount(Account account) {
        account.setId(UUID.randomUUID().toString());
        Date now = new Date();
        account.setCreatedon(now);
        account.setLastupdate(now);
        if (StringUtils.isEmpty(account.getStatus())) {
            account.setStatus("active");
        }
        if (accountDao.saveOne(account)) {
            return account;
        }
        return null;
    }

    public Account editAccount(String id, Account account) {
        Account oldAccount = getAccount(id);
        if (oldAccount != null) {
            account.setId(id);
            account.setLastupdate(new Date());
            if (accountDao.update(account)) {
                return account;
            }
        }
        return null;
    }

    public boolean deleteAccount(String id) {
        if (StringUtils.isNotEmpty(id)) {
            return accountDao.deleteOne(id);
        }
        return false;
    }

}
