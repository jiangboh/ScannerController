package com.bravo.log;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.FemtoController.BaseActivity;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.FileUtils;
import com.bravo.utils.Logs;
import com.bravo.utils.SimpleDateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class Local_Fragment extends RevealAnimationBaseFragment {
    private final static String TAG = "Local_Fragment";
    private final static int REQUEST_CODE = 20;
    private ListView lv;
    private ArrayAdapter adapter;
    private ArrayList<String> fileNames = new ArrayList<String>();
    private ArrayList<String> fileAbsoluteNames = new ArrayList<String>();
    private String dirName = null;
    private FileUtils fileUtils;
    private String zipPath = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileUtils = new FileUtils(context);
        setContentView(R.layout.fragment_local);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_clear_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if(TextUtils.isEmpty(dirName)){
                    fileUtils.deleteFile(new File(fileUtils.getLogCacheDir()));
                }else{
                    fileUtils.deleteFile(new File(fileUtils.getLogCacheDir() + File.separator + dirName + File.separator));
                }
                initFileName(dirName);
                super.recordOnClick(v, "Local Log Clear Event " + dirName);
            }
        });
        initFileName(dirName);
    }

    @Override
    public void initView() {
        lv = (ListView) contentView.findViewById(R.id.file_list);
        lv.setOnItemClickListener(new RecordOnItemClick() {
            @Override
            public void recordOnItemClick(AdapterView<?> parent, View view, int position, long id, String strMsg) {
                String fileName = ((TextView)view).getText().toString().trim();
                if(fileName.endsWith("Logs")){
                    dirName = fileName;
                    initFileName(fileName);
                }else if(fileName.endsWith(".txt")||fileName.endsWith(".log")){
                    Intent intent = new Intent(context,LocalLogDetailActivity.class);
                    intent.putExtra("dirName",dirName);
                    intent.putExtra("fileName",fileName);
                    ((BaseActivity)context).startActivityWithAnimation(intent);
                }
                super.recordOnItemClick(parent, view, position, id, "Local Log Item Click Event " + fileName);
            }
        });
        /*
        lv.setOnItemLongClickListener(new RecordOnItemLongClick() {
            @Override
            public void recordOnItemLongClick(AdapterView<?> parent, View view, int position, long id, String strMsg) {
                final String fileAbsPath = fileAbsoluteNames.get(position);
//                Logs.d("123456","长点击的FileName == " + fileName + "  绝对路径为：" + fileAbsPath);
                if(fileAbsPath.endsWith(".txt")||fileAbsPath.endsWith(".log")){
                    OpenOrDownLoadDialog openOrDownLoadDialog = new OpenOrDownLoadDialog(context,R.style.dialog_style);
                    openOrDownLoadDialog.setClickListener(new OpenOrDownLoadDialog.BtnClickListener() {
                        @Override
                        public void onOpenClick(View v) {
                            zipPath = null;
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + fileAbsPath));
                            intent.setType("text/plain");
                            intent.setType("message/rfc882");
                            Intent.createChooser(intent, "Choose Email Client");
                            startActivityForResult(intent,REQUEST_CODE);
                        }

                        @Override
                        public void onDownLoadClick(View v) {
                            if(fileAbsPath.endsWith(".txt")){
                                zipPath = fileAbsPath.replace(".txt",".zip");
                            }else{
                                zipPath = fileAbsPath.replace(".log",".zip");
                            }
                            try {
                                ZipUtils.zipFile(new File(fileAbsPath),new File(zipPath));
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + zipPath));
                                intent.setType("text/plain");
                                intent.setType("message/rfc882");
                                Intent.createChooser(intent, "Choose Email Client");
                                startActivityForResult(intent,REQUEST_CODE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    openOrDownLoadDialog.show();
                    openOrDownLoadDialog.setTitle("Send By Email");
                    openOrDownLoadDialog.setOpenContent("send the original");
                    openOrDownLoadDialog.setDownLoadContent("send compressed file(.ZIP)");
                }
                super.recordOnItemLongClick(parent, view, position, id, "Local Log Item Long Click Event");
            }
        });
        */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!TextUtils.isEmpty(zipPath)&&requestCode == REQUEST_CODE){
            new File(zipPath).delete();
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    private void initFileName(String path){
        fileNames.clear();
        fileAbsoluteNames.clear();
        File[] files = null;
        if(TextUtils.isEmpty(path)){
            files = new File(new FileUtils(context).getLogCacheDir()).listFiles();
        }else{
            files = new File(new FileUtils(context).getLogCacheDir() + File.separator + path + File.separator).listFiles();
        }
        if (files != null && files.length > 0) {
            boolean isDir = true;
            for (File f : files) {
                Logs.d(TAG,"file.getName() == " + f.getName());
                if(f.getName().endsWith(".txt")){
                    isDir = false;
                }
                fileNames.add(f.getName());
                fileAbsoluteNames.add(f.getAbsolutePath());
            }
            if(isDir){
                Collections.sort(fileNames,new Comparator<String>(){
                    @Override
                    public int compare(String o1, String o2) {
                        long time1 = SimpleDateUtils.parseDate("yyyy-MM-dd",o1.replace(" Logs",""));
                        long time2 = SimpleDateUtils.parseDate("yyyy-MM-dd",o2.replace(" Logs",""));
                        return (int)(time2 - time1);
                    }
                });
            }
        }
        adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,fileNames);
        lv.setAdapter(adapter);
    }

    public boolean onBackPressed(){
        if(TextUtils.isEmpty(dirName)){
            return true;
        }else{
            dirName = null;
            initFileName(dirName);
            return false;
        }
    }

}
