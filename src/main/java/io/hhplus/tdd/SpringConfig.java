package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {


    @Bean
    public UserPointTable pointTable(){
        return new UserPointTable();
    }

    @Bean
    public PointHistoryTable pointHistoryTable() { return new PointHistoryTable(); }

    @Bean
    public PointService pointService() { return new PointService(pointTable(), pointHistoryTable()); }
}
