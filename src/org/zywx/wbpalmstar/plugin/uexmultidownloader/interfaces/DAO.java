package org.zywx.wbpalmstar.plugin.uexmultidownloader.interfaces;

import android.content.Context;

import org.zywx.wbpalmstar.plugin.uexmultidownloader.daos.DBOpenHelper;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.entities.DLInfo;


/**
 * DAO抽象类
 * Abstract class of DAO.
 *
 * @author AigeStudio 2015-05-16
 */
public abstract class DAO {
    protected DBOpenHelper dbHelper;

    public DAO(Context context) {
        dbHelper = new DBOpenHelper(context);
    }

    public abstract void insertInfo(DLInfo info);

    public abstract void deleteInfo(String url);

    public abstract void updateInfo(DLInfo info);

    public abstract DLInfo queryInfo(String str);

    public void close() {
        dbHelper.close();
    }
}
