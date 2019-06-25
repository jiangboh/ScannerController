package com.bravo.FileSlelect;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bravo.R;
import com.bravo.config.FragmentImportImsi;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SelectTxtFileActivity extends Activity implements
        OnItemClickListener {

    private String tableName = FragmentImportImsi.TABLE_NAME;
    private String keyName = FragmentImportImsi.ImportPath;

    private ImageView ivBack;
    private TextView tvSettingTitle;
    private ListView mlistview;
    private TextView fullPath;
    private Button btnCancel, btnComfirm;
    //根节点路径
    private static final String rootDirectory = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    List<FolderInfo> folderlist;
    //默认打开路径
    String defultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String path="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_folder);
        initView();
        refreshListItems(path);
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_setting_back);
        tvSettingTitle = (TextView) findViewById(R.id.tv_setting_title);
        mlistview = (ListView) findViewById(R.id.folder_list);
        fullPath = (TextView) findViewById(R.id.tv_path_select);
        btnCancel = (Button) findViewById(R.id.btn_folder_cancel);
        btnComfirm = (Button) findViewById(R.id.btn_folder_comfirm);
        tvSettingTitle.setText("请选择要导入的文件");
        ivBack.setOnClickListener(backListener);
        btnCancel.setOnClickListener(cancelListener);

        btnComfirm.setVisibility(View.GONE);
        btnComfirm.setOnClickListener(comfirmListener);

        //Intent传递的路径
        SharedPreferences sp = getSharedPreferences(tableName, MODE_PRIVATE);
        String rPath = sp.getString(keyName,"");
        if (rPath == null || rPath.trim().isEmpty())
        {
            path = defultPath;
        } else {
            //File file = new File(rPath);
            //path = file.getAbsolutePath();
            int lastDot = rPath.lastIndexOf("/");
            if (lastDot >= 0) {
                path = rPath.substring(0,lastDot);
            } else {
                path = defultPath;
            }
        }
    }

    private OnClickListener comfirmListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            SharedPreferences preferences = getSharedPreferences(tableName, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(keyName,(String) fullPath.getText());
            editor.commit();

            SelectTxtFileActivity.this.finish();
        }
    };

    private OnClickListener cancelListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            SelectTxtFileActivity.this.finish();
        }
    };

    private OnClickListener backListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            SelectTxtFileActivity.this.finish();
        }
    };

    /* 根据path更新路径列表 */
    private void refreshListItems(String path) {
        fullPath.setText(path);
        folderlist = getFolderList(path);
        FolderAdapter folderAdapter = new FolderAdapter(folderlist,
                SelectTxtFileActivity.this);
        mlistview.setAdapter(folderAdapter);
        mlistview.setOnItemClickListener(this);
        mlistview.setSelection(0);
    }

    /* 根据路径生成一个包含路径的列表 */
    private List<FolderInfo> getFolderList(String path) {
        File[] files = new File(path).listFiles();
        List<FolderInfo> list = new ArrayList<FolderInfo>();
        if (!rootDirectory.equals(path)) {
            FolderInfo fi = new FolderInfo();
            //fi.setFolderIcon(R.drawable.swf_folder);
            fi.setFolderIcon(R.drawable.back_folder);
            fi.setFolderName("...");
            fi.setFolderPath("");
            fi.setFolderToTal("父目录");
            list.add(fi);
        }
        List<File> fileList = Arrays.asList(files);
        //文件排序--按照名称排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }

        });
        for (File file : files) {
            //判断文件是否是文件还是文件夹&&隐藏首字母名称为“.”
            if (file.isDirectory() && file.getName().indexOf(".") != 0) {
                FolderInfo fol = new FolderInfo();
                fol.setFolderIcon(R.drawable.swf_folder);
                fol.setFolderName(file.getName());
                fol.setFolderPath(file.getPath());
                fol.setFolderTime(longDateToString(file.lastModified()));
                fol.setFolderToTal(getSubfolder(file.getPath()));
                list.add(fol);
            } else {
                //判断是否txt文件
                if (checkTxtFile(file.getPath())) {
                    FolderInfo fol = new FolderInfo();
                    fol.setFolderIcon(R.drawable.txt_log);
                    fol.setFolderName(file.getName());
                    fol.setFolderPath(file.getPath());
                    fol.setFolderTime(longDateToString(file.lastModified()));
                    fol.setFolderToTal("大小:"+ file.length());
                    //+ FileSizeUtil.FormetFileSize(file.length()));
                    list.add(fol);
                }
            }
        }
        return list;
    }

    /* 跳转到上一层 */
    private void goToParent() {
        if (!rootDirectory.equals(path)) {
            File file = new File(path);
            File str_pa = file.getParentFile();
            if (str_pa.equals(rootDirectory)) {
                Toast.makeText(SelectTxtFileActivity.this, "已经是根目录",
                        Toast.LENGTH_SHORT).show();
                refreshListItems(path);
            } else {
                path = str_pa.getAbsolutePath();
                refreshListItems(path);
            }
        } else {
            Toast.makeText(SelectTxtFileActivity.this, "已经是根目录",
                    Toast.LENGTH_SHORT).show();
            refreshListItems(path);
        }
    }

    private String getSubfolder(String path) {
        int i = 0;
        File[] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isDirectory() && file.getName().indexOf(".") != 0) {
                i++;
            } else {
                if (MediaFileUtil.isImageFileType(file.getPath())
                        || MediaFileUtil.isVideoFileType(file.getPath())) {
                    i++;
                }
            }
        }
        return "共" + i + "项";
    }

    private String longDateToString(long time) {
        try {
            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd");
            String dateTime = df.format(new Date(time));
            return dateTime;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if (folderlist.get(position).getFolderPath().isEmpty())
            goToParent();
        else {
            path = folderlist.get(position).getFolderPath();
            File file = new File(path);
            if (file.isDirectory()) {
                refreshListItems(path);
            } else {
                if (checkTxtFile(path)) {
                    //refreshListItems(path);
                    fullPath.setText(path);
                    SaveSelectPath();
                }
            }
        }
    }


    public void SaveSelectPath() {
        SharedPreferences preferences = getSharedPreferences(tableName, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keyName,(String) fullPath.getText());
        editor.commit();

        SelectTxtFileActivity.this.finish();
    }

    private boolean checkTxtFile(String file) {
        int lastDot = file.lastIndexOf(".");
        if (lastDot >= 0) {
            if ("TXT".equals(file.substring(lastDot + 1).toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}


