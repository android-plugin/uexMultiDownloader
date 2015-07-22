package org.zywx.wbpalmstar.plugin.uexmultidownloader;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.zywx.wbpalmstar.engine.DataHelper;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.bizs.DBManager;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.bizs.DLManager;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.entities.TaskInfo;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.utils.FileUtil;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.view.FloatingGroupExpandableListView;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.view.WrapperExpandableListAdapter;
import org.zywx.wbpalmstar.plugin.uexmultidownloader.vo.DownloadItemVO;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ylt on 15/7/9.
 */
public class MultiDownloadView extends LinearLayout {

    private FloatingGroupExpandableListView listView;
    private List<DownloadItemVO> downloadingVOs;
    private List<DownloadItemVO> downloadedVOs;
    private BaseExpandableListAdapter baseAdapter;
    private WrapperExpandableListAdapter adapter;
    private OnDownloadOperateListener operateListener;
    private Context context;

    public MultiDownloadView(Context context) {
        super(context);
        downloadingVOs=new ArrayList<DownloadItemVO>();
        downloadedVOs=new ArrayList<DownloadItemVO>();
        this.context=context;
        initView(context);
     }

    public void initView(Context context){
        LayoutInflater.from(context).inflate(EUExUtil.getResLayoutID("plugin_uexmulti_download_layout"),this,true);
        listView= (FloatingGroupExpandableListView) findViewById(EUExUtil.getResIdID("download_list"));
        baseAdapter=new DownloadAdapter();
        adapter=new WrapperExpandableListAdapter(baseAdapter);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        listView.setGroupIndicator(null);
        listView.expandGroup(0);
        listView.expandGroup(1);
        loadHistory();
    }

    /**
     * 加载历史记录
     */
    public void loadHistory(){
        List<String> urls= DBManager.getInstance(context.getApplicationContext()).queryDownLoadUrls();
        if (urls!=null&&!urls.isEmpty()){
            for (int i=0;i<urls.size();i++){
                TaskInfo taskInfo=DBManager.getInstance(context.getApplicationContext()).queryTaskInfoByUrl(urls.get(i));
                DownloadItemVO itemVO=new DownloadItemVO();
                itemVO.setUrl(taskInfo.baseUrl);
                itemVO.setProgress(100 * taskInfo.progress / taskInfo.length);
                itemVO.setName(taskInfo.dlLocalFile.getName());
                itemVO.setTotalLength(taskInfo.length);
                itemVO.setSavePath(taskInfo.dlLocalFile.getAbsolutePath());
                if (taskInfo.progress==taskInfo.length) {
                    itemVO.setState(DownloadItemVO.State.FINISH);
                    downloadedVOs.add(itemVO);
                }else{
                    itemVO.setState(DownloadItemVO.State.PAUSE);
                    downloadingVOs.add(itemVO);
                }

            }
            baseAdapter.notifyDataSetChanged();
        }
    }

    public boolean addTask(DownloadItemVO itemVO){
        if (isTaskExsists(itemVO.getUrl())){
            return false;
        }
        downloadingVOs.add(itemVO);
//        adapter.notifyDataSetChanged();
        baseAdapter.notifyDataSetChanged();
        return true;
    }

    public boolean isTaskExsists(String url){
        if (downloadedVOs!=null){
            for (int i = 0; i < downloadedVOs.size(); i++) {
                if (url.equals(downloadedVOs.get(i).getUrl())){
                    return true;
                }
            }
        }
        if (downloadingVOs!=null){
            for (int i = 0; i < downloadingVOs.size(); i++) {
                if (url.equals(downloadingVOs.get(i).getUrl())){
                    return true;
                }
            }
        }
        return false;
    }

    public void setOperateListener(OnDownloadOperateListener operateListener) {
        this.operateListener = operateListener;
    }

    private class DownloadAdapter extends BaseExpandableListAdapter{

        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition==0){
                return downloadingVOs.size();
            }else if (groupPosition==1){
                return downloadedVOs.size();
            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public DownloadItemVO getChild(int groupPosition, int childPosition) {
            if (groupPosition==0){
                return downloadingVOs.get(childPosition);
            }else if (groupPosition==1){
                return downloadedVOs.get(childPosition);
            }
            return null;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            LinearLayout headerLayout= (LinearLayout) LayoutInflater.from(context).inflate(EUExUtil.getResLayoutID("plugin_uexmulti_download_title_layout"),null);
            ImageView arrowImg= (ImageView) headerLayout.findViewById(EUExUtil.getResIdID("up_arrow_img"));
            TextView title= (TextView) headerLayout.findViewById(EUExUtil.getResIdID("titleTv"));
            if (isExpanded){
                arrowImg.setBackgroundResource(EUExUtil.getResDrawableID("plugin_uexmultidownloader_uparrow"));
            }else{
                arrowImg.setBackgroundResource(EUExUtil.getResDrawableID("plugin_uexmultidownloader_downarrow"));
            }
            if (groupPosition==0){
                //正在下载
                title.setText("正在下载("+downloadingVOs.size()+")");
            }else if (groupPosition==1){
                //已下载
                title.setText("已下载("+downloadedVOs.size()+")");
            }

            return headerLayout;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final DownloadItemVO downloadItemVO=getChild(groupPosition, childPosition);
            DownloadItemView itemView;
            if (convertView==null||convertView instanceof DownloadItemView){
                convertView=new DownloadItemView(context);
            }
            itemView= (DownloadItemView) convertView;
            itemView.setData(downloadItemVO);
            itemView.setOnDownloadListener(new DownloadItemView.OnDownloadListener() {
                @Override
                public void onDownloadFinished() {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadedVOs.contains(downloadItemVO)){
                                return;
                            }
                            downloadingVOs.remove(downloadItemVO);
                            downloadedVOs.add(0, downloadItemVO);
                            baseAdapter.notifyDataSetChanged();
                            Log.i("appcan","getChildView onDownloadFinished"+downloadItemVO.getUrl());
                        }
                    });
                 }

                @Override
                public void onDelete() {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deleteDownload(downloadItemVO);
                            baseAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onReDownload() {
                    //重新下载
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deleteDownload(downloadItemVO);
                            FileUtil.deleteFile(downloadItemVO.getSavePath());
                            DownloadItemVO newItemVO=new DownloadItemVO();
                            newItemVO.setName(downloadItemVO.getName());
                            newItemVO.setProgress(0);
                            newItemVO.setTotalLength(downloadItemVO.getTotalLength());
                            newItemVO.setState(DownloadItemVO.State.LOADING);
                            newItemVO.setUrl(downloadItemVO.getUrl());
                            newItemVO.setSpeed(0);
                            newItemVO.setSavePath(downloadItemVO.getSavePath());
                            addTask(newItemVO);
                        }
                    });
                }

                @Override
                public void onTaskDetail() {
                    if (operateListener!=null){
                        operateListener.onTaskDetail(downloadItemVO);
                    }
                }
            });
            return itemView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        /**
         * 删除下载任务
         * @param downloadItemVO
         */
        private void deleteDownload(DownloadItemVO downloadItemVO){
            DLManager.getInstance(context.getApplicationContext()).dlCancel(downloadItemVO.getUrl());
            if (downloadItemVO.getState()== DownloadItemVO.State.FINISH){
                //已完成的任务
                downloadedVOs.remove(downloadItemVO);
            }else{
                //未完成的任务
                downloadingVOs.remove(downloadItemVO);
            }
        }

    }


    public interface OnDownloadOperateListener{
        void onTaskDetail(DownloadItemVO itemVO);
    }

}
