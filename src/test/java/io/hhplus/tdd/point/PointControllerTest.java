package io.hhplus.tdd.point;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class PointControllerTest {
    @MockBean
    private PointService pointService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test_point() throws Exception {
        doReturn(
                new UserPoint(1, 1000, 0)
        ).when(pointService).getPointById(1);

        mockMvc.perform(get("/point/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1, \"point\": 1000, \"updateMillis\": 0}"));
    }

    @Test
    void history() throws Exception {

        List<PointHistory> pointHistories = new ArrayList<PointHistory>();
        PointHistory pointHistory = new PointHistory(1, 1, 1000, TransactionType.CHARGE, 0);
        pointHistories.add(pointHistory);
        doReturn( pointHistories ).when(pointService).getHistoryByUserId(1);

        mockMvc.perform(get("/point/1/histories"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                "[{\"id\": 1, \"userId\": 1, \"amount\": 1000, \"type\": \"CHARGE\", \"updateMillis\": 0}]"
                ));
    }

    @Test
    void charge() throws Exception {
        doReturn(
                new UserPoint(1, 1000, 0)
        ).when(pointService).getPointById(1);

        mockMvc.perform(patch("/point/1/charge"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1, \"point\": 1000, \"updateMillis\": 0}"));
    }

    @Test
    void use() throws Exception{
        doReturn(
                new UserPoint(1, 1000, 0)
        ).when(pointService).getPointById(1);

        mockMvc.perform(patch("/point/1/use"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1, \"point\": 1000, \"updateMillis\": 0}"));
    }
}