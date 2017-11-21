package xiaohu.facetest.greendaodemo.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import xiahohu.facetest.model.WhiteList;

import xiaohu.facetest.greendaodemo.greendao.gen.WhiteListDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig whiteListDaoConfig;

    private final WhiteListDao whiteListDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        whiteListDaoConfig = daoConfigMap.get(WhiteListDao.class).clone();
        whiteListDaoConfig.initIdentityScope(type);

        whiteListDao = new WhiteListDao(whiteListDaoConfig, this);

        registerDao(WhiteList.class, whiteListDao);
    }
    
    public void clear() {
        whiteListDaoConfig.clearIdentityScope();
    }

    public WhiteListDao getWhiteListDao() {
        return whiteListDao;
    }

}
