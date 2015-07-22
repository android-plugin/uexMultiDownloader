package org.zywx.wbpalmstar.plugin.uexmultidownloader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ylt on 15/7/8.
 */
public class DownLoadService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }



}
