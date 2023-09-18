package org.example.jpa;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.datanucleus.store.rdbms.adapter.H2Adapter;
import org.h2.Driver;

import javax.sql.DataSource;
import java.util.Map;

public interface Setup {

    EntityManagerFactory configure();

    default DataSource buildDataSource() {
        final HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(10);
        ds.setDriverClassName(Driver.class.getSimpleName());
        ds.setJdbcUrl("jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1"); ;
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }
}
