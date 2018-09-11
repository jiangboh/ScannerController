package com.bravo.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "FEMTO_LIST".
*/
public class FemtoListDao extends AbstractDao<FemtoList, Long> {

    public static final String TABLENAME = "FEMTO_LIST";

    /**
     * Properties of entity FemtoList.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Ip = new Property(1, String.class, "ip", false, "IP");
        public final static Property Mac = new Property(2, String.class, "mac", false, "MAC");
        public final static Property Port = new Property(3, int.class, "port", false, "PORT");
        public final static Property UdpPort = new Property(4, int.class, "udpPort", false, "UDP_PORT");
        public final static Property SSID = new Property(5, String.class, "SSID", false, "SSID");
    }


    public FemtoListDao(DaoConfig config) {
        super(config);
    }
    
    public FemtoListDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FEMTO_LIST\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"IP\" TEXT," + // 1: ip
                "\"MAC\" TEXT," + // 2: mac
                "\"PORT\" INTEGER NOT NULL ," + // 3: port
                "\"UDP_PORT\" INTEGER NOT NULL ," + // 4: udpPort
                "\"SSID\" TEXT);"); // 5: SSID
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FEMTO_LIST\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, FemtoList entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String ip = entity.getIp();
        if (ip != null) {
            stmt.bindString(2, ip);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(3, mac);
        }
        stmt.bindLong(4, entity.getPort());
        stmt.bindLong(5, entity.getUdpPort());
 
        String SSID = entity.getSSID();
        if (SSID != null) {
            stmt.bindString(6, SSID);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, FemtoList entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String ip = entity.getIp();
        if (ip != null) {
            stmt.bindString(2, ip);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(3, mac);
        }
        stmt.bindLong(4, entity.getPort());
        stmt.bindLong(5, entity.getUdpPort());
 
        String SSID = entity.getSSID();
        if (SSID != null) {
            stmt.bindString(6, SSID);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public FemtoList readEntity(Cursor cursor, int offset) {
        FemtoList entity = new FemtoList( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // ip
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // mac
            cursor.getInt(offset + 3), // port
            cursor.getInt(offset + 4), // udpPort
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // SSID
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, FemtoList entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setIp(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMac(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPort(cursor.getInt(offset + 3));
        entity.setUdpPort(cursor.getInt(offset + 4));
        entity.setSSID(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(FemtoList entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(FemtoList entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(FemtoList entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
