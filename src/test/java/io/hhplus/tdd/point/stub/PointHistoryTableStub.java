package io.hhplus.tdd.point.stub;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class PointHistoryTableStub extends PointHistoryTable {
    private final List<PointHistory> table = new ArrayList<>();

    @Override
    public PointHistory insert(long userId, long amount, TransactionType type, long updateMillis) {
        PointHistory pointHistory = new PointHistory(1, userId, amount, type, updateMillis);
        table.add(pointHistory);
        return pointHistory;
    }

    @Override
    public List<PointHistory> selectAllByUserId(long userId) {
        return table.stream().filter(pointHistory -> pointHistory.userId() == userId).toList();
    }
}
