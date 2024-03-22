package io.hhplus.tdd.point.stub;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;

import java.util.HashMap;
import java.util.Map;

public class UserPointTableStub extends UserPointTable {
    private final Map<Long, UserPoint> userPointMap = new HashMap<>();

    @Override
    public UserPoint selectById(Long id) {
        return this.userPointMap.getOrDefault(id, UserPoint.empty(id));
    }

    @Override
    public UserPoint insertOrUpdate(long id, long amount) {
        UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
        userPointMap.put(id, userPoint);
        return userPoint;
    }
}
