package org.zywx.wbpalmstar.plugin.uexmultidownloader.daos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.zywx.wbpalmstar.plugin.uexmultidownloader.cons.PublicCons;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.entities.DLInfo;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.entities.TaskInfo;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.interfaces.DAO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 下载任务的DAO实现
 * DAO for download task.
 *
 * @author AigeStudio 2015-05-09
 * @author AigeStudio 2015-05-29
 *         根据域名重定向问题进行逻辑修改
 */
public class TaskDAO extends DAO {
    public TaskDAO(Context context) {
        super(context);
    }

    @Override
    public void insertInfo(DLInfo info) {
        TaskInfo i = (TaskInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO " + PublicCons.DBCons.TB_TASK + "(" +
                        PublicCons.DBCons.TB_TASK_URL_BASE + ", " +
                        PublicCons.DBCons.TB_TASK_URL_REAL + ", " +
                        PublicCons.DBCons.TB_TASK_FILE_PATH + ", " +
                        PublicCons.DBCons.TB_TASK_PROGRESS + ", " +
                        PublicCons.DBCons.TB_TASK_FILE_LENGTH + ") values (?,?,?,?,?)",
                new Object[]{i.baseUrl, i.realUrl, i.dlLocalFile.getAbsolutePath(), i.progress,
                        i.length});
        db.close();
    }

    @Override
    public void deleteInfo(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + PublicCons.DBCons.TB_TASK + " WHERE " +
                PublicCons.DBCons.TB_TASK_URL_BASE + "=?", new String[]{url});
        db.close();
    }

    @Override
    public void updateInfo(DLInfo info) {
        TaskInfo i = (TaskInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + PublicCons.DBCons.TB_TASK + " SET " +
                PublicCons.DBCons.TB_TASK_PROGRESS + "=? WHERE " +
                PublicCons.DBCons.TB_TASK_URL_BASE + "=?", new Object[]{i.progress, i.baseUrl});
        db.close();
    }

    @Override
    public DLInfo queryInfo(String url) {
        TaskInfo info = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                PublicCons.DBCons.TB_TASK_URL_BASE + ", " +
                PublicCons.DBCons.TB_TASK_URL_REAL + ", " +
                PublicCons.DBCons.TB_TASK_FILE_PATH + ", " +
                PublicCons.DBCons.TB_TASK_PROGRESS + ", " +
                PublicCons.DBCons.TB_TASK_FILE_LENGTH + " FROM " +
                PublicCons.DBCons.TB_TASK + " WHERE " +
                PublicCons.DBCons.TB_TASK_URL_BASE + "=?", new String[]{url});
        if (c.moveToFirst()) {
            info = new TaskInfo(new File(c.getString(2)), c.getString(0),c.getString(1),
                    c.getInt(3), c.getInt(4));
        }
        c.close();
        db.close();
        return info;
    }

    public List<String> queryUrls() {
        List<String> urls=new ArrayList<>();
        TaskInfo info = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT " +
                PublicCons.DBCons.TB_TASK_URL_BASE + " FROM " +
                PublicCons.DBCons.TB_TASK, null);
        while (c.moveToNext()) {
            urls.add(c.getString(0));
        }
        c.close();
        db.close();
        return urls;
    }
}
