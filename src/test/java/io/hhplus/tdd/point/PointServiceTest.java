package io.hhplus.tdd.point;

import io.hhplus.tdd.Exceptions.MinusPointException;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PointServiceTest {

    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;
    private PointService pointService;

    @BeforeEach
    void beforeAll() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();
        pointService = new PointService(userPointTable, pointHistoryTable);
    }


    @Test
    public void test_신규_유저_포인트_조회(){
        // given-when
        UserPoint usersPoint = pointService.getPointById(1);
        // then
        assertEquals(0L, usersPoint.point());
    }

    @Test
    public void test_유저가_충전한다(){
        // given-when
        UserPoint result = pointService.charge(1, 1000);
        // then
        assertEquals(1000L, result.point());
    }

    @Test
    public void test_충전_유저_포인트_조회(){
        // given
        pointService.charge(1, 1000);
        // when
        UserPoint usersPoint = pointService.getPointById(1);
        // then
        assertEquals(1000L, usersPoint.point());
    }

    @Test
    public void test_충전_유저_포인트_조회_2(){
        // given
        pointService.charge(1, 1000);
        pointService.charge(1, 2000);
        // when
        UserPoint usersPoint = pointService.getPointById(1);
        // then
        assertEquals(3000L, usersPoint.point());
    }

    @Test
    public void test_신규_유저가_포인트를_사용한다(){
        // given-when-then
        assertThrows(MinusPointException.class, () -> pointService.use(1, 1000));
    }

    @Test
    public void test_충전유저가_포인트를_사용한다(){
        // given
        pointService.charge(1, 1000);
        // when
        pointService.use(1, 300);
        // then
        UserPoint point = pointService.getPointById(1);
        assertEquals(700L, point.point());
    }

    @Test
    public void test_충전유저가_과도한_포인트를_사용한다(){
        // given
        pointService.charge(1, 1000);
        // when-then
        assertThrows(MinusPointException.class, () -> pointService.use(1, 1100));
    }


    @Test
    public void test_미사용유저의_히스토리를_가져온다(){
        // given-when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(1);

        // then
        assertTrue(pointHistories.isEmpty());
    }

    @Test
    public void test_사용유저의_히스토리를_가져온다() {
        // given
        pointService.charge(1, 1000);
        pointService.use(1, 700);
        // when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(1);
        // then
        assertEquals(TransactionType.CHARGE, pointHistories.get(0).type());
        assertEquals(1000L, pointHistories.get(0).amount());
        assertEquals(TransactionType.USE, pointHistories.get(1).type());
        assertEquals(700L, pointHistories.get(1).amount());
    }
}