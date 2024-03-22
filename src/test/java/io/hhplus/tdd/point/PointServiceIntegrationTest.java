package io.hhplus.tdd.point;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PointServiceIntegrationTest {
    private PointService pointService;

    @Autowired
    public PointServiceIntegrationTest(PointService pointService) {
        this.pointService = pointService;
    }


    @Test
    public void test_동시성환경에서_유저가_충전을_수행한다() throws InterruptedException {
        // given
        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch doneSignal = new CountDownLatch(numThreads);

        // when
        for (int i=0; i<numThreads; i++) {
            executorService.execute(() -> {
                pointService.charge(1, 100);
                doneSignal.countDown();
            });
        }
        doneSignal.await();
        List<PointHistory> pointHistories = this.pointService.getHistories(1);
        assertEquals(10, pointHistories.size());
        assertEquals(1000, pointService.getPointById(1).point());
    }

    @Test
    public void test_동시성환경에서_유저가_사용을_수행한다() throws InterruptedException {
        // given
        pointService.charge(1, 1000);

        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch doneSignal = new CountDownLatch(numThreads);

        // when
        for (int i=0; i<numThreads; i++) {
            executorService.execute(() -> {
                pointService.use(1, 100);
                doneSignal.countDown();
            });
        }
        doneSignal.await();

        List<PointHistory> pointHistories = this.pointService.getHistories(1);
        assertEquals(11, pointHistories.size());
        assertEquals(0, pointService.getPointById(1).point());
    }
}