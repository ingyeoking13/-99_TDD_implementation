package io.hhplus.tdd.point;

import io.hhplus.tdd.Exceptions.MinusPointException;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PointService {

    @Autowired private final UserPointTable userPointTable;
    @Autowired private final PointHistoryTable pointHistoryTable;


    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public synchronized UserPoint getPointById(long id) {
        return this.userPointTable.selectById(id);
    }

    public synchronized List<PointHistory> getHistoryByUserId(long id) {
        return this.pointHistoryTable.selectAllByUserId(id);
    }

    public synchronized UserPoint charge(long id, long amount) {
        UserPoint result = this.userPointTable.insertOrUpdate(id,
                amount + this.userPointTable.selectById(id).point()
        );
        this.pointHistoryTable.insert(id, amount, TransactionType.CHARGE, result.updateMillis());
        return result;
    }

    public synchronized UserPoint use(long id, long amount) {
        UserPoint currentPoint = this.userPointTable.selectById(id);
        if (currentPoint.point() < amount) {
            throw new MinusPointException();
        }
        long newAmount = currentPoint.point() - amount;

        UserPoint result = this.userPointTable.insertOrUpdate(id, newAmount);
        this.pointHistoryTable.insert(id, amount, TransactionType.USE, result.updateMillis());

        return this.userPointTable.selectById(id);
    }

    public List<PointHistory> getHistories(long id) {
        return this.pointHistoryTable.selectAllByUserId(id);
    }

}
