package io.hhplus.tdd.point;

import io.hhplus.tdd.Exceptions.MinusPointException;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class PointControllerTest {

    private PointController pointController;
    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;
    private PointService pointService;

    @BeforeEach
    void beforeAll() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();
        pointService = new PointService(userPointTable, pointHistoryTable);
        pointController = new PointController(pointService);
    }


    @Test
    public void test_신규_유저_포인트_조회(){
        // given-when
        UserPoint usersPoint = pointController.point(1);
        // then
        assertEquals(0L, usersPoint.point());
    }

    @Test
    public void test_유저가_충전한다(){
        // given-when
        UserPoint result = pointController.charge(1, 1000);
        // then
        assertEquals(1000L, result.point());
    }

    @Test
    public void test_충전_유저_포인트_조회(){
        // given
        pointController.charge(1, 1000);
        // when
        UserPoint usersPoint = pointController.point(1);
        // then
        assertEquals(1000L, usersPoint.point());
    }

    @Test
    public void test_충전_유저_포인트_조회_2(){
        // given
        pointController.charge(1, 1000);
        pointController.charge(1, 2000);
        // when
        UserPoint usersPoint = pointController.point(1);
        // then
        assertEquals(3000L, usersPoint.point());
    }

    @Test
    public void test_신규_유저가_포인트를_사용한다(){
        // given-when-then
        assertThrows(MinusPointException.class, () -> pointController.use(1, 1000));
    }

    @Test
    public void test_충전유저가_포인트를_사용한다(){
        // given
        pointController.charge(1, 1000);
        // when
        pointController.use(1, 300);
        // then
        UserPoint point = pointController.point(1);
        assertEquals(700L, point.point());
    }

    @Test
    public void test_충전유저가_과도한_포인트를_사용한다(){
        // given
        pointController.charge(1, 1000);
        // when-then
        assertThrows(MinusPointException.class, () -> pointController.use(1, 1100));
    }


    @Test
    public void test_미사용유저의_히스토리를_가져온다(){
        // given-when
        List<PointHistory> pointHistories = pointController.history(1);

        // then
        assertTrue(pointHistories.isEmpty());
    }

    @Test
    public void test_사용유저의_히스토리를_가져온다() {
        // given
        pointController.charge(1, 1000);
        pointController.use(1, 700);
        // when
        List<PointHistory> pointHistories = pointController.history(1);
        // then
        assertEquals(TransactionType.CHARGE, pointHistories.get(0).type());
        assertEquals(1000L, pointHistories.get(0).amount());
        assertEquals(TransactionType.USE, pointHistories.get(1).type());
        assertEquals(700L, pointHistories.get(1).amount());
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
                pointController.charge(1, 100);
                doneSignal.countDown();
            });
        }
        doneSignal.await();
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(1);
        assertEquals(10, pointHistories.size());
        assertEquals(1000, userPointTable.selectById(1L).point());
    }

    @Test
    public void test_동시성환경에서_유저가_사용을_수행한다() throws InterruptedException {
        // given
        pointController.charge(1, 1000);

        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch doneSignal = new CountDownLatch(numThreads);

        // when
        for (int i=0; i<numThreads; i++) {
            executorService.execute(() -> {
                pointController.use(1, 100);
                doneSignal.countDown();
            });
        }
        doneSignal.await();

        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(1);
        assertEquals(11, pointHistories.size());
        assertEquals(0, userPointTable.selectById(1L).point());
    }


}