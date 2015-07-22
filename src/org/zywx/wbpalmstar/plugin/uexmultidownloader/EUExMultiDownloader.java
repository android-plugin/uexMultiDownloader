package org.zywx.wbpalmstar.plugin.uexmultidownloader;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.vo.DownloadItemVO;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.vo.OpenInputVO;

public class EUExMultiDownloader extends EUExBase {

    private static final String BUNDLE_DATA = "data";
    private static final int MSG_OPEN = 1;
    private static final int MSG_OPEN_MANAGER_VIEW = 2;
    private static final int MSG_CLOSE_MANAGER_VIEW = 3;
    private static final int MSG_ENQUEUE = 4;
    private static final int MSG_CLOSE = 5;
    private static final int MSG_OPEN_VIEW = 6;

    private Gson gson;

    private MultiDownloadView multiDownloadView;

    public EUExMultiDownloader(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
        gson=new Gson();
    }

    @Override
    protected boolean clean() {
        return false;
    }

    public void openManagerView(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_OPEN_MANAGER_VIEW;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void openManagerViewMsg(String[] params) {
        String json = params[0];
    }

    public void closeManagerView(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CLOSE_MANAGER_VIEW;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void closeManagerViewMsg(String[] params) {
        String json = params[0];
    }

    public void enqueue(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_ENQUEUE;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void enqueueMsg(String[] params) {
        String json = params[0];
        if (multiDownloadView!=null){
            DownloadItemVO inputVO=gson.fromJson(json,DownloadItemVO.class);
            inputVO.setState(DownloadItemVO.State.LOADING);
            boolean result=multiDownloadView.addTask(inputVO);
            JSONObject jsonResult = new JSONObject();
            try {
                jsonResult.put("result", result);
            } catch (JSONException e) {
            }
            callBackPluginJs(JsConst.CALLBACK_ENQUEUE, jsonResult.toString());
        }else{

        }


    }

    public void close(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CLOSE;
        mHandler.sendMessage(msg);
    }

    private void closeMsg(String[] params) {
//        String json = params[0];
        removeViewFromCurrentWindow(multiDownloadView);
    }

    public void open(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_OPEN_VIEW;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void openMsg(String[] params) {
        String json = params[0];
        OpenInputVO openInputVO=gson.fromJson(json,OpenInputVO.class);
        if (multiDownloadView==null) {
            multiDownloadView = new MultiDownloadView(mContext);
        }
        multiDownloadView.setOperateListener(new MultiDownloadView.OnDownloadOperateListener() {
            @Override
            public void onTaskDetail(DownloadItemVO itemVO) {

                callBackPluginJs(JsConst.ON_TASK_DETAIL,gson.toJson(itemVO));
            }

        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                openInputVO.getW(), openInputVO.getH());
        lp.topMargin = openInputVO.getY();
        lp.leftMargin = openInputVO.getX();
        addViewToCurrentWindow(multiDownloadView, lp);
    }

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {

            case MSG_OPEN:
                openMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_OPEN_MANAGER_VIEW:
                openManagerViewMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CLOSE_MANAGER_VIEW:
                closeManagerViewMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_ENQUEUE:
                enqueueMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CLOSE:
                closeMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_OPEN_VIEW:
                openMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            default:
                super.onHandleMessage(message);
        }
    }

    private void callBackPluginJs(String methodName, String jsonData){
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "('" + jsonData + "');}";
        onCallback(js);
    }

}
