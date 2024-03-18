package io.hhplus.tdd.point;

import io.hhplus.tdd.Exceptions.MinusPointException;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PointControllerTest {

    private PointController pointController;
    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;

    @BeforeEach
    void beforeAll() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();
        pointController = new PointController(userPointTable, pointHistoryTable);
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

}