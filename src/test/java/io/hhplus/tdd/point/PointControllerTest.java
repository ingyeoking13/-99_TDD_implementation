package io.hhplus.tdd.point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointControllerTest {

    private PointController pointController;

    @BeforeEach
    void beforeAll() {
        pointController = new PointController();
    }


    @Test
    public void test_신규_유저_포인트_조회(){
        // given-when
        UserPoint usersPoint = pointController.point(1);
        // then
        assertSame(0L, usersPoint.point());
    }

    @Test
    public void test_유저가_충전한다(){
        // given-when
        pointController.charge(1, 1000);
        // then
        assertSame(1000L, pointController.point(1));
    }

    @Test
    public void test_충전_유저_포인트_조회(){
        // given
        pointController.charge(1, 1000);
        // when
        UserPoint usersPoint = pointController.point(1);
        // then
        assertSame(1000L, usersPoint.point());
    }

}