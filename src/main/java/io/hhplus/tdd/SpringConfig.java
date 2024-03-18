package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {
    private DataSource datasource;

    @Autowired
    public SpringConfig(DataSource datasource) {
        this.datasource = datasource;
    }

    @Autowired
    public PointController pointController(){
        return new PointController(pointTable(), pointHistoryTable());
    }

    @Bean
    public UserPointTable pointTable(){
        return new UserPointTable();
    }

    @Bean
    public PointHistoryTable pointHistoryTable() { return new PointHistoryTable(); }
}
