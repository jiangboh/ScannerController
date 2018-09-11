package com.bravo.database;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.bravo.database.AdjacentCell;
import com.bravo.database.BcastHistory;
import com.bravo.database.FemtoList;
import com.bravo.database.SnifferHistory;
import com.bravo.database.TargetUser;
import com.bravo.database.User;

import com.bravo.database.AdjacentCellDao;
import com.bravo.database.BcastHistoryDao;
import com.bravo.database.FemtoListDao;
import com.bravo.database.SnifferHistoryDao;
import com.bravo.database.TargetUserDao;
import com.bravo.database.UserDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig adjacentCellDaoConfig;
    private final DaoConfig bcastHistoryDaoConfig;
    private final DaoConfig femtoListDaoConfig;
    private final DaoConfig snifferHistoryDaoConfig;
    private final DaoConfig targetUserDaoConfig;
    private final DaoConfig userDaoConfig;

    private final AdjacentCellDao adjacentCellDao;
    private final BcastHistoryDao bcastHistoryDao;
    private final FemtoListDao femtoListDao;
    private final SnifferHistoryDao snifferHistoryDao;
    private final TargetUserDao targetUserDao;
    private final UserDao userDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        adjacentCellDaoConfig = daoConfigMap.get(AdjacentCellDao.class).clone();
        adjacentCellDaoConfig.initIdentityScope(type);

        bcastHistoryDaoConfig = daoConfigMap.get(BcastHistoryDao.class).clone();
        bcastHistoryDaoConfig.initIdentityScope(type);

        femtoListDaoConfig = daoConfigMap.get(FemtoListDao.class).clone();
        femtoListDaoConfig.initIdentityScope(type);

        snifferHistoryDaoConfig = daoConfigMap.get(SnifferHistoryDao.class).clone();
        snifferHistoryDaoConfig.initIdentityScope(type);

        targetUserDaoConfig = daoConfigMap.get(TargetUserDao.class).clone();
        targetUserDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        adjacentCellDao = new AdjacentCellDao(adjacentCellDaoConfig, this);
        bcastHistoryDao = new BcastHistoryDao(bcastHistoryDaoConfig, this);
        femtoListDao = new FemtoListDao(femtoListDaoConfig, this);
        snifferHistoryDao = new SnifferHistoryDao(snifferHistoryDaoConfig, this);
        targetUserDao = new TargetUserDao(targetUserDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);

        registerDao(AdjacentCell.class, adjacentCellDao);
        registerDao(BcastHistory.class, bcastHistoryDao);
        registerDao(FemtoList.class, femtoListDao);
        registerDao(SnifferHistory.class, snifferHistoryDao);
        registerDao(TargetUser.class, targetUserDao);
        registerDao(User.class, userDao);
    }
    
    public void clear() {
        adjacentCellDaoConfig.clearIdentityScope();
        bcastHistoryDaoConfig.clearIdentityScope();
        femtoListDaoConfig.clearIdentityScope();
        snifferHistoryDaoConfig.clearIdentityScope();
        targetUserDaoConfig.clearIdentityScope();
        userDaoConfig.clearIdentityScope();
    }

    public AdjacentCellDao getAdjacentCellDao() {
        return adjacentCellDao;
    }

    public BcastHistoryDao getBcastHistoryDao() {
        return bcastHistoryDao;
    }

    public FemtoListDao getFemtoListDao() {
        return femtoListDao;
    }

    public SnifferHistoryDao getSnifferHistoryDao() {
        return snifferHistoryDao;
    }

    public TargetUserDao getTargetUserDao() {
        return targetUserDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

}
