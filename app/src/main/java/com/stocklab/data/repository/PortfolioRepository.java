package com.stocklab.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.stocklab.StockLabApp;
import com.stocklab.data.local.AppDatabase;
import com.stocklab.data.local.dao.HoldingDao;
import com.stocklab.data.local.dao.ProfileDao;
import com.stocklab.data.local.dao.TransactionDao;
import com.stocklab.data.local.entity.HoldingEntity;
import com.stocklab.data.local.entity.ProfileEntity;
import com.stocklab.data.local.entity.TransactionEntity;
import com.stocklab.model.Holding;
import com.stocklab.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for portfolio holdings and transactions.
 */
public class PortfolioRepository {

    private final HoldingDao holdingDao;
    private final TransactionDao transactionDao;
    private final ProfileDao profileDao;

    public PortfolioRepository(Context context) {
        AppDatabase db = StockLabApp.getInstance().getDatabase();
        this.holdingDao = db.holdingDao();
        this.transactionDao = db.transactionDao();
        this.profileDao = db.profileDao();
    }

    public LiveData<List<Holding>> getHoldings(long profileId) {
        return Transformations.map(holdingDao.getHoldingsByProfile(profileId), this::toHoldings);
    }

    public List<Holding> getHoldingsSync(long profileId) {
        return toHoldings(holdingDao.getHoldingsByProfileSync(profileId));
    }

    public LiveData<List<Transaction>> getTransactions(long profileId) {
        return Transformations.map(transactionDao.getTransactionsByProfile(profileId), this::toTransactions);
    }

    public List<Transaction> getTransactionsSync(long profileId) {
        return toTransactions(transactionDao.getTransactionsByProfileSync(profileId));
    }

    /** Simulated buy: deduct cash, add holding or update quantity, record transaction. */
    public boolean buy(long profileId, String symbol, String name, double quantity, double pricePerShare) {
        if (profileId <= 0 || quantity <= 0 || pricePerShare <= 0) return false;

        long costCents = (long) Math.round(quantity * pricePerShare * 100);

        ProfileEntity profile = profileDao.getProfileByIdSync(profileId);
        if (profile == null || profile.currentCash < costCents) return false;

        HoldingEntity existing = holdingDao.getHoldingSync(profileId, symbol);
        if (existing != null) {
            double newQty = existing.quantity + quantity;
            double newAvg = (existing.averageCostPerShare * existing.quantity + pricePerShare * quantity) / newQty;
            existing.quantity = newQty;
            existing.averageCostPerShare = newAvg;
            holdingDao.update(existing);
        } else {
            HoldingEntity h = new HoldingEntity();
            h.profileId = profileId;
            h.symbol = symbol;
            h.name = name;
            h.quantity = quantity;
            h.averageCostPerShare = pricePerShare;
            holdingDao.insert(h);
        }

        profile.currentCash -= costCents;
        profileDao.update(profile);

        TransactionEntity tx = new TransactionEntity();
        tx.profileId = profileId;
        tx.type = "buy";
        tx.symbol = symbol;
        tx.name = name;
        tx.quantity = quantity;
        tx.pricePerShare = pricePerShare;
        tx.timestamp = System.currentTimeMillis();
        transactionDao.insert(tx);

        return true;
    }

    /** Simulated sell: add cash, reduce holding, record transaction. */
    public boolean sell(long profileId, String symbol, String name, double quantity, double pricePerShare) {
        if (profileId <= 0 || quantity <= 0 || pricePerShare <= 0) return false;

        HoldingEntity existing = holdingDao.getHoldingSync(profileId, symbol);
        if (existing == null || existing.quantity < quantity) return false;

        long proceedsCents = (long) (quantity * pricePerShare * 100);

        if (existing.quantity == quantity) {
            holdingDao.delete(existing);
        } else {
            existing.quantity -= quantity;
            holdingDao.update(existing);
        }

        ProfileEntity profile = profileDao.getProfileByIdSync(profileId);
        if (profile != null) {
            profile.currentCash += proceedsCents;
            profileDao.update(profile);
        }

        TransactionEntity tx = new TransactionEntity();
        tx.profileId = profileId;
        tx.type = "sell";
        tx.symbol = symbol;
        tx.name = name;
        tx.quantity = quantity;
        tx.pricePerShare = pricePerShare;
        tx.timestamp = System.currentTimeMillis();
        transactionDao.insert(tx);

        return true;
    }

    private List<Holding> toHoldings(List<HoldingEntity> entities) {
        List<Holding> list = new ArrayList<>();
        if (entities != null) {
            for (HoldingEntity e : entities) {
                list.add(new Holding(e.id, e.symbol, e.name, e.quantity, e.averageCostPerShare, 0));
            }
        }
        return list;
    }

    private List<Transaction> toTransactions(List<TransactionEntity> entities) {
        List<Transaction> list = new ArrayList<>();
        if (entities != null) {
            for (TransactionEntity e : entities) {
                list.add(new Transaction(e.id, e.type, e.symbol, e.name, e.quantity, e.pricePerShare, e.timestamp));
            }
        }
        return list;
    }
}
