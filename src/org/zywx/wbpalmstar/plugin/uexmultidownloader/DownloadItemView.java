package org.zywx.wbpalmstar.plugin.uexmultidownloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.bizs.DBManager;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.bizs.DLManager;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.interfaces.DLTaskListener;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.vo.DownloadItemVO;

import java.io.File;

/**
 * Created by ylt on 15/7/9.
 */
public class DownloadItemView extends LinearLayout{

    private Context context;
    private ImageView fileTypeImg;
    private TextView fileNameTv;
    private ProgressBar progressBar;
    private TextView downloadedSizeTv;
    private TextView netSpeedTv;
    private ImageView startImg;
    private ImageView pauseImg;
    private OnDownloadListener downloadListener;
    private boolean isFinished=false;
    private DownloadItemVO infoVO;

    private boolean started=false;


    public DownloadItemView(Context context) {
        super(context);
        this.context=context;
        LayoutInflater.from(context).inflate(EUExUtil.getResLayoutID("plugin_uexmulti_download_list_item"),this,true);
        initView();
    }


    private void initView(){
        fileTypeImg= (ImageView) findViewById(EUExUtil.getResIdID("item_icon"));
        fileNameTv= (TextView) findViewById(EUExUtil.getResIdID("tv_file_name"));
        progressBar= (ProgressBar) findViewById(EUExUtil.getResIdID("progress"));
        downloadedSizeTv= (TextView) findViewById(EUExUtil.getResIdID("download_size_tv"));
        netSpeedTv= (TextView) findViewById(EUExUtil.getResIdID("net_speed_tv"));
        startImg= (ImageView) findViewById(EUExUtil.getResIdID("btn_start"));
        pauseImg= (ImageView) findViewById(EUExUtil.getResIdID("btn_pause"));
    }

    /**
     * 为View绑定
     * @param infoVO
     */
    public void setData(final DownloadItemVO infoVO){
        started=false;
        this.infoVO=infoVO;
        fileNameTv.setText(infoVO.getName());
        if (infoVO.getState()== DownloadItemVO.State.FINISH){
            //已下载完成
            progressBar.setVisibility(View.GONE);
            startImg.setVisibility(View.GONE);
            pauseImg.setVisibility(View.GONE);
            downloadedSizeTv.setVisibility(View.GONE);
            netSpeedTv.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(infoVO.getProgress());
            if (infoVO.getTotalLength()!=0){
                setDownloadedText(infoVO.getDownloadLength(),infoVO.getTotalLength());
            }
            if (infoVO.getState()== DownloadItemVO.State.LOADING){
                //下载中
                netSpeedTv.setVisibility(View.VISIBLE);
                pauseImg.setVisibility(View.VISIBLE);
                startImg.setVisibility(View.GONE);
                netSpeedTv.setText(getNetSpeedText(infoVO.getSpeed()));
                setDownloadingState();
            }else {
                //暂停
                netSpeedTv.setVisibility(View.GONE);
                startImg.setVisibility(View.VISIBLE);
                pauseImg.setVisibility(View.GONE);
            }
        }

        startImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setDownloadingState();
            }
        });
        pauseImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                infoVO.setState(DownloadItemVO.State.PAUSE);
                netSpeedTv.setVisibility(View.GONE);
                startImg.setVisibility(View.VISIBLE);
                pauseImg.setVisibility(View.GONE);
                DLManager.getInstance(context).dlStop(infoVO.getUrl());
            }

        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPop();
                return true;
            }
        });
    }

    private void setDownloadingState(){
        if (started){
            return;
        }
        started=true;
        infoVO.setState(DownloadItemVO.State.LOADING);
        netSpeedTv.setVisibility(View.VISIBLE);
        pauseImg.setVisibility(View.VISIBLE);
        startImg.setVisibility(View.GONE);
        downloadedSizeTv.setVisibility(View.VISIBLE);
        DLManager.getInstance(context).dlStart(infoVO.getUrl(),infoVO.getSavePath(),new DLTaskListener(){

            @Override
            public void onFinish(File file) {
                super.onFinish(file);
                Log.i("appcan", "onFinish" + file.getAbsolutePath());
                infoVO.setSpeed(0);
                infoVO.setState(DownloadItemVO.State.FINISH);
                if (context!=null){
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setDownloadFinished();
                        }
                    });
                }
            }

            @Override
            public void onNetSpeed(final int speed) {
                infoVO.setSpeed(speed);
                if (netSpeedTv!=null){
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            netSpeedTv.setText(getNetSpeedText(speed));
                        }
                    });
                }
            }

            @Override
            public void onStart(String fileName, String url) {
                super.onStart(fileName, url);
            }

            @Override
            public void onProgress(final int progress, final int totalLength, final int downloadedLength) {

                Log.i("appcan", "progress: " + progress + " total: " + totalLength + " downloaded: " + downloadedLength);
                if (context==null){
                    Log.e("appcan", "context is null...");
                    return;
                }
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoVO.setProgress(progress);
                        infoVO.setTotalLength(totalLength);
                        infoVO.setDownloadLength(downloadedLength);
                        if (progressBar != null) {
                            progressBar.setProgress(progress);
                        }
                        if (downloadedSizeTv != null) {
                            setDownloadedText(downloadedLength, totalLength);
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                super.onError(error);
                Log.i("appcan", error);
            }

            @Override
            public boolean onConnect(int type, String msg) {
                return super.onConnect(type, msg);
            }

        });
    }


    public String getFileDir(String filePath){
        return filePath.substring(0,filePath.lastIndexOf(File.separator));
    }

    public String getNetSpeedText(int speed){
        return speed + "k/s";
    }

    private void setDownloadedText(int downloadedLength,int totalLength){
        downloadedSizeTv.setText(getSizeText(downloadedLength)+"/"+getSizeText(totalLength));
    }

    private String getSizeText(int length){
        if (length<=1024*1024){
            return String.format("%.1f", length/1024.0)+"kB";
        }else if (length<=1024*1024*1024){
            return String.format("%.1f", length/1024.0/1024.0)+"MB";
        }else{
            return String.format("%.1f", length/1024.0/1024.0/1024.0)+"GB";
        }
    }

    public void setNetSpeed(int speed){
        netSpeedTv.setText(speed + "k/s");
    }

    /**
     * 设置view为下载完成的状态
     */
    private synchronized void setDownloadFinished(){
        if (isFinished){
            return;
        }
        isFinished=true;
        netSpeedTv.setVisibility(View.INVISIBLE);
        pauseImg.setVisibility(View.GONE);
        startImg.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        if (downloadListener!=null){
            downloadListener.onDownloadFinished();
        }

    }

    public void setOnDownloadListener(OnDownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }


    public interface OnDownloadListener{
        void onDownloadFinished();
        void onDelete();
        void onReDownload();
        void onTaskDetail();
    }

    public void showPop(){
        LinearLayout pv = (LinearLayout) LayoutInflater.from(
                context).inflate(
                EUExUtil.getResLayoutID("plugin_uexmulti_download_item_pop"),
                null);
        final PopupWindow pw = new PopupWindow(context);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setContentView(pv);
        pw.setWidth(LayoutParams.WRAP_CONTENT);
        pw.setHeight(LayoutParams.WRAP_CONTENT);

        pw.setOutsideTouchable(true);
        pw.setFocusable(true);


        LinearLayout contentlinearlayout = (LinearLayout) this
                .findViewById(EUExUtil.getResIdID("contentlinearlayout"));
        int [] location=new int[2];
        contentlinearlayout.getLocationOnScreen(location);
        pw.showAtLocation(
                contentlinearlayout,
                Gravity.RIGHT | Gravity.TOP,
                ((Activity)context).getWindowManager().getDefaultDisplay().getWidth() / 4, location[1]);
        TextView downloadTextView = (TextView) pv.findViewById(EUExUtil
                .getResIdID("downloadTextView"));
        downloadTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (downloadListener!=null){
                    downloadListener.onReDownload();
                }
                pw.dismiss();
//                startDownload(parent, position);
            }
        });
        TextView downloadDetailTextView = (TextView) pv.findViewById(EUExUtil
                .getResIdID("downloadDetailTextView"));
        downloadDetailTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (downloadListener!=null){
                    downloadListener.onTaskDetail();
                }
                pw.dismiss();

            }
        });
        TextView downloadDeleteTextView = (TextView) pv.findViewById(EUExUtil
                .getResIdID("downloadDeleteTextView"));
        downloadDeleteTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (downloadListener!=null){
                    downloadListener.onDelete();
                }
                pw.dismiss();
            }
        });


    }

}
