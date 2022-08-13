package edu.hu.ssbe.config;

import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import edu.hu.ssbe.utils.ConfigurationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories
public class CassandraConfig {

    @Autowired
    private ConfigurationUtils configurationUtils;

    @Bean
    public CassandraClusterFactoryBean cluster() {
        final int timeoutMillis = 60 * 1000; // 60s
        SocketOptions socketOptions = new SocketOptions();
        socketOptions.setConnectTimeoutMillis(timeoutMillis);
        socketOptions.setReadTimeoutMillis(timeoutMillis);

        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(configurationUtils.getCassandraHost());
        cluster.setPort(configurationUtils.getCassandraPort());
        cluster.setSocketOptions(socketOptions);
        cluster.setLoadBalancingPolicy(new TokenAwarePolicy(
                DCAwareRoundRobinPolicy.builder().build()
                )
        );

        return cluster;
    }

    @Bean
    public CassandraMappingContext mappingContext() {
        return new CassandraMappingContext();
    }

    @Bean
    public CassandraConverter converter() {
        return new MappingCassandraConverter(mappingContext());
    }

    @Bean
    public CassandraSessionFactoryBean session() {
        CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
        session.setCluster(cluster().getObject());
        session.setKeyspaceName("data");
        session.setConverter(converter());
        session.setSchemaAction(SchemaAction.NONE);

        return session;
    }

    @Bean
    public CassandraOperations cassandraTemplate() {
        return new CassandraTemplate(session().getObject());
    }

}
