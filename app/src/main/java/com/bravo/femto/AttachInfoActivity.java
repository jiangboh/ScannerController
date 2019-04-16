package com.bravo.femto;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bravo.FemtoController.BaseActivity;
import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.database.TargetUser;
import com.bravo.database.TargetUserDao;
import com.bravo.database.User;
import com.bravo.database.UserDao;
import com.bravo.dialog.DialogSendSMS;
import com.bravo.dialog.DialogTargetRedirect;
import com.bravo.parse_generate_xml.ReleaseTarget;
import com.bravo.parse_generate_xml.Status;
import com.bravo.parse_generate_xml.TargetDetach;
import com.bravo.parse_generate_xml.send_sms.SendSmsReq;
import com.bravo.parse_generate_xml.send_sms.SendSmsRes;
import com.bravo.parse_generate_xml.silent_call.SilentCallReq;
import com.bravo.parse_generate_xml.silent_call.SilentCallRes;
import com.bravo.parse_generate_xml.target_position.TargetPosition;
import com.bravo.parse_generate_xml.target_redirect.TargetRedirectReq;
import com.bravo.parse_generate_xml.target_redirect.TargetRedirectRes;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.bravo.femto.BcastCommonApi.sendTcpMsg;

public class AttachInfoActivity extends BaseActivity {
    private TextView tvImsi;
    private TextView tvImei;
    private TextView tvConntime;
    private TextView tvAttachtime;
    private boolean bSilentFlag = false;
    private String strImsi, strImei;
    private String strTech;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvDistance;
    private int iMaxDistance = 100;
    private int iAuthTotal;
    private boolean bDetachFlag = false;
    private final int POINT_SIZE = 12;
    //chart
    private LineChart lineChart;
    private ArrayList<Entry> distances = new ArrayList<>();
    private String strCurIp;
    private ImageView rightBtn;
    private TextToSpeech tts;
    private CheckBox openOrCloseVoice;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_attach_info);

        findViewById(R.id.iv_activity_back).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                onBackPressed();
                super.recordOnClick(v, "Back Event " + TAG);
            }
        });
        ((TextView) findViewById(R.id.tv_activity_title)).setText("Target");
        tvImsi = (TextView) findViewById(R.id.imsi);
        tvImei = (TextView) findViewById(R.id.imei);
        tvConntime = (TextView) findViewById(R.id.conntime);
        tvAttachtime = (TextView) findViewById(R.id.attachtime);
        //gps
        tvLatitude = ((TextView) findViewById(R.id.latitude));
        tvLongitude = ((TextView) findViewById(R.id.longitude));
        //distance
        tvDistance = ((TextView) findViewById(R.id.distance));
        lineChart = (LineChart) findViewById(R.id.lineChart);
        rightBtn = (ImageView) findViewById(R.id.iv_activity_right);
        findViewById(R.id.unlock).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                ReleaseTarget releaseTarget = new ReleaseTarget();
                releaseTarget.setImsi(strImsi);
                try {
                    sendTcpMsg(mContext, ReleaseTarget.toXml(releaseTarget));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.recordOnClick(v, "Unlock Event");
            }
        });
        findViewById(R.id.silent).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                //builder.setMessage("Select mode");
                builder.setTitle("Silent Call Req");
                if (bSilentFlag == false) {
                    builder.setPositiveButton("START", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SilentCallReq silentCallReq = new SilentCallReq();
                            silentCallReq.setMode("START");
                            silentCallReq.setImsi(strImsi);
                            try {
                                sendTcpMsg(mContext, SilentCallReq.toXml(silentCallReq));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    });
                } else {
                    builder.setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SilentCallReq silentCallReq = new SilentCallReq();
                            silentCallReq.setMode("STOP");
                            silentCallReq.setImsi(strImsi);
                            try {
                                sendTcpMsg(mContext, SilentCallReq.toXml(silentCallReq));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    });
                }
                builder.create().show();
                super.recordOnClick(v, "Silent Event");
            }
        });

        findViewById(R.id.sms).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                final DialogSendSMS dialogSendSMS = new DialogSendSMS(mContext, R.style.dialog_style,
                        new DialogSendSMS.OnCustomDialogListener() {
                            @Override
                            public void DialogCallBack(SendSmsReq sendSmsReq) {
                                sendSmsReq.setImsi(strImsi);
                                try {
                                    sendTcpMsg(mContext, SendSmsReq.toXml(sendSmsReq));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                dialogSendSMS.show();
                super.recordOnClick(v, "SMS Event");
            }
        });

        findViewById(R.id.redirect).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                DialogTargetRedirect dialogTargetRedirect = new DialogTargetRedirect(mContext, R.style.dialog_style,
                        new DialogTargetRedirect.OnCustomDialogListener() {
                            @Override
                            public void DialogCallBack(TargetRedirectReq targetRedirectReq) {
                                targetRedirectReq.setImsi(strImsi);
                                try {
                                    sendTcpMsg(mContext, TargetRedirectReq.toXml(targetRedirectReq));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                dialogTargetRedirect.show();
                super.recordOnClick(v, "Redirect Event");
            }
        });
//        rightBtn.setVisibility(View.VISIBLE);
//        rightBtn.setImageResource(R.drawable.btn_start_selector);
//        rightBtn.setOnClickListener(new RecordOnClick() {
//            @Override
//            public void recordOnClick(View v, String strMsg) {
//                startTukTuk();
//                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
//                super.recordOnClick(v, "start tuktuk List Event");
//            }
//        });
        openOrCloseVoice = (CheckBox) findViewById(R.id.openOrCloseVoice);
        //初始化语音
        initTextToSpeech();
        initStatusView();
    }

    private void initTextToSpeech(){
        tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setSpeechRate(1.5f);
                tts.setLanguage(Locale.getDefault());
            }
        });
    }

    private void startTukTuk() {
        if(BcastCommonApi.isAvilible(mContext, "com.mobility.client.tuktuk")){
                    Intent intent = getPackageManager().getLaunchIntentForPackage(
                            //这个是另外一个应用程序的包名
                            "com.mobility.client.tuktuk");
                    startActivity(intent);
                } else {
//                    OneBtnHintDialog hintDialog = new OneBtnHintDialog(mContext, R.style.dialog_style);
//                    if (!hintDialog.isShowing()) {
//                        hintDialog.show();
//                        hintDialog.setBtnContent("OK");
//                        hintDialog.setTitle("Warning");
//                        hintDialog.setContent("Please Install TukTuk App");
//                    }
                final Application sApp;
                Application app = null;
                try {
                    app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
                    if (app == null)
                        throw new IllegalStateException("Static initialization of Applications must be on main thread.");
                } catch (final Exception e) {
                    Logs.d(TAG, "Failed to get current application from AppGlobals." + e.getMessage());
                    try {
                        app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
                    } catch (final Exception ex) {
                        Logs.d(TAG, "Failed to get current application from ActivityThread." + e.getMessage());
                    }
                } finally {
                    sApp = app;
                }
                Logs.d(TAG, "lmj=" + sApp.getExternalFilesDir("upgrade_apk"));
                if(BcastCommonApi.copyApkFromAssets(mContext, "TukTuk.apk", sApp.getExternalFilesDir("upgrade_apk")+"/TukTuk.apk")){
                    AlertDialog.Builder m = new AlertDialog.Builder(mContext)
                            .setIcon(R.drawable.icon_2g_default).setMessage("是否安装？")
                            .setIcon(R.drawable.icon_2g_default)
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String command = "chmod " + "777" + " " + sApp.getExternalFilesDir("upgrade_apk")+"/TukTuk.apk";
                                    Runtime runtime = Runtime.getRuntime();
                                    try {
                                        runtime.exec(command);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    if(Build.VERSION.SDK_INT>=24) {
                                        Uri apkUri = FileProvider.getUriForFile(mContext, "com.bravo.fileprovider",
                                                new File(sApp.getExternalFilesDir("upgrade_apk") + "/TukTuk.apk"));
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                    } else {
                                        intent.setDataAndType(Uri.fromFile(new File(sApp.getExternalFilesDir("upgrade_apk") + "/TukTuk.apk")),
                                                "application/vnd.android.package-archive");
                                    }
                                    mContext.startActivity(intent);
                                }
                            });
                    m.show();
                }
                }
    }
    @Override
    protected void initData(Bundle savedInstanceState) {
        strCurIp = ((ProxyApplication)getApplicationContext()).getCurSocketAddress();
        strTech = SharePreferenceUtils.getInstance(mContext).getString("status_notif_tech" +
                ((ProxyApplication)getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)getApplicationContext()).getiTcpPort(), "4G");
        strImsi = getIntent().getStringExtra("imsi");
        strImei = getIntent().getStringExtra("imei");
        LoadTarget();
        //auth total
        Long starttime = SharePreferenceUtils.getInstance(this).getLong("status_notif_starttime" +
                ((ProxyApplication)getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)getApplicationContext()).getiTcpPort(), System.currentTimeMillis());
        List<User> users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(SharePreferenceUtils.getInstance(mContext).getString("status_notif_unique" + ((ProxyApplication)getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)getApplicationContext()).getiTcpPort(), "")),
                UserDao.Properties.ConnTime.gt(starttime), UserDao.Properties.IAuth.eq(1)).build().list();
        iAuthTotal = users.size();
    }

    public void LoadTarget() {
        List<User> users =  ProxyApplication.getDaoSession().getUserDao().queryBuilder().orderDesc().
                where(UserDao.Properties.Unique.eq(SharePreferenceUtils.getInstance(this).
                                getString("status_notif_unique" + ((ProxyApplication)getApplicationContext()).getCurSocketAddress()
                                        + ((ProxyApplication)getApplicationContext()).getiTcpPort(), "")),
                        UserDao.Properties.SrtImsi.eq(strImsi == null?"":strImsi),
                        UserDao.Properties.StrImei.eq(strImei == null?"":strImei),
                        UserDao.Properties.IAuth.eq(1),
                        UserDao.Properties.ConnTime.gt(SharePreferenceUtils.getInstance(this).
                        getLong("status_notif_starttime" + ((ProxyApplication)getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)getApplicationContext()).getiTcpPort(),
                                System.currentTimeMillis()))).build().list();
        if (users.size() > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
            tvConntime.setText(formatter.format(new Date(users.get(0).getConnTime())));
            tvAttachtime.setText(formatter.format(new Date(users.get(0).getAttachTime())));
            bSilentFlag = users.get(0).getBSilent();
        }
        tvImei.setText(strImei);
        tvImsi.setText(strImsi);
    }
    public void initChart(LineChart lineChart){
        Description description = new Description();
        description.setText("");
        description.setTextColor(ContextCompat.getColor(mContext,R.color.colorChartMain));
        description.setTextSize(18);
        lineChart.setDescription(description);
        lineChart.setNoDataText("No data");
        lineChart.setNoDataTextColor(R.color.colorChartMain);
        lineChart.setDrawGridBackground(true);
        lineChart.setBackgroundColor(Color.parseColor("#f2f2f2"));

        lineChart.setTouchEnabled(false); // 设置是否可以触摸
        lineChart.setDragEnabled(false);// 是否可以拖拽
        lineChart.setScaleEnabled(false);// 是否可以缩放 x和y轴, 默认是true
        // lineChart.setScaleXEnabled(true); //是否可以缩放 仅x轴
        //lineChart.setScaleYEnabled(true); //是否可以缩放 仅y轴
        lineChart.setPinchZoom(false);  //设置x轴和y轴能否同时缩放。默认是否
        lineChart.setDoubleTapToZoomEnabled(true);//设置是否可以通过双击屏幕放大图表。默认是true
//        lineChart.setHighlightPerDragEnabled(false);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        //  lineChart.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        //lineChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。


        //获取此图表的x轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true);//设置轴启用或禁用 如果禁用以下的设置全部不生效
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        xAxis.setDrawGridLines(true);//设置x轴上每个点对应的线
        xAxis.setGridColor(Color.parseColor("#cfdae6"));
        xAxis.setDrawLabels(false);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        //xAxis.setTextSize(20f);//设置字体
        //xAxis.setTextColor(Color.BLACK);//设置字体颜色
        //设置竖线的显示样式为虚线
        //lineLength控制虚线段的长度
        //spaceLength控制线之间的空间
        //   xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMinimum(0f);//设置x轴的最小值
        xAxis.setAxisMaximum(12f);//设置最大值
//        xAxis.setAvoidFirstLastClipping(true);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
//        xAxis.setLabelRotationAngle(10f);//设置x轴标签的旋转角度
//        设置x轴显示标签数量  还有一个重载方法第二个参数为布尔值强制设置数量 如果启用会导致绘制点出现偏差
        xAxis.setLabelCount(POINT_SIZE);
//        xAxis.setTextColor(Color.BLUE);//设置轴标签的颜色
//        xAxis.setTextSize(24f);//设置轴标签的大小
//        xAxis.setGridLineWidth(10f);//设置竖线大小
//        xAxis.setGridColor(Color.RED);//设置竖线颜色
//        xAxis.setAxisLineColor(Color.GREEN);//设置x轴线颜色
//        xAxis.setAxisLineWidth(5f);//设置x轴线宽度
//        xAxis.setValueFormatter();//格式化x轴标签显示字符

        YAxis rYAxis = lineChart.getAxisRight();
        rYAxis.setEnabled(false);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setEnabled(true);
        yAxis.setDrawAxisLine(true);//是否绘制轴线
        yAxis.setDrawGridLines(true);
        yAxis.setGridColor(Color.parseColor("#cfdae6"));
//        yAxis.enableGridDashedLine(10f,10f,0f);
        yAxis.setDrawTopYLabelEntry(true);
        yAxis.setDrawZeroLine(false);
        yAxis.setTextSize(10);
        yAxis.setTextColor(Color.parseColor("#03304c"));
        yAxis.setLabelCount(10);
        yAxis.setAxisMaximum(iMaxDistance);
        yAxis.setAxisMinimum(0);
    }

    private LineData initData(ArrayList<Entry> values, LineChart lineChart){
        LineDataSet lineDataSet;
        if(lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0){
            lineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        }else{
            lineDataSet = new LineDataSet(values,"");
            lineDataSet.setColor(Color.parseColor("#7ac8f1"));
            lineDataSet.setCircleColor(Color.parseColor("#7ac8f1"));
            lineDataSet.setLineWidth(1f);//设置线宽
            lineDataSet.setCircleRadius(6f);//设置焦点圆心的大小
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
            lineDataSet.setHighlightLineWidth(2f);//设置点击交点后显示高亮线宽
            lineDataSet.setHighlightEnabled(true);//是否禁用点击高亮线
//            lineDataSet.setHighLightColor(Color.RED);//设置点击交点后显示交高亮线的颜色
            lineDataSet.setValueTextSize(9f);//设置显示值的文字大小
            lineDataSet.setDrawFilled(false);//设置禁用范围背景填充
            // lineDataSet.setFillColor(R.color.colorTest);//设置折线下方填充色
            lineDataSet.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int)value) + "m";
                }
            });
        }
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        return new LineData(dataSets);
    }

    private void addDistance(int iDistance) {
        if (iDistance > iMaxDistance) {
            iMaxDistance = iDistance;
        }
        int size = distances.size();
        if(size > POINT_SIZE){
            distances.remove(0);
            size--;
            for(Entry entry : distances){
                entry.setX(entry.getX()-1);
            }
        }
        distances.add(new Entry(size, iDistance));

        //播放语音
        if(openOrCloseVoice.isChecked()){
            DecimalFormat df = new DecimalFormat("#,###");
            String str = df.format(iDistance);
             tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private int iCounter = 0;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetPosition(TargetPosition tp) {
        if (strImsi.equals(tp.getImsi())){
            iCounter = 255;
            if (strTech.equals("2G")) {
                tvDistance.setText(tp.getSignalDistance() + "m |" + tp.getTechSpecific().getRssi() + "dBm");
            } else if (strTech.equals("3G")) {
                tvDistance.setText(tp.getSignalDistance() + "m |" + tp.getTechSpecific().getRscp() + "dBm");
            } else if (strTech.equals("4G")) {
                tvDistance.setText(tp.getSignalDistance() + "m |" + tp.getTechSpecific().getRsrp() + "dBm");
            }
            if (!TextUtils.isEmpty(tp.getGps())) {
                String gps[] = tp.getGps().split(",");
                if (gps.length == 2) {
                    tvLatitude.setText("Lat: " + gps[0]);
                    tvLongitude.setText("Lng: " + gps[1]);
                }
            }
            if (!TextUtils.isEmpty(tp.getSignalDistance())) {
                addDistance(Integer.parseInt(tp.getSignalDistance()));
                initChart(lineChart);
                if (distances.size() > 0) {
                    lineChart.setData(initData(distances, lineChart));
                    lineChart.invalidate();
                }
            }
        } else if (iCounter != 255){
            iCounter++;
            if (iCounter > iAuthTotal + 2) {
                iCounter = 255;
                /////
                String Unique = SharePreferenceUtils.getInstance(this).getString("status_notif_unique" + ((ProxyApplication)getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)getApplicationContext()).getiTcpPort(), "");
                List<User> users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().orderDesc().where(UserDao.Properties.Unique.eq(Unique), UserDao.Properties.SrtImsi.eq(strImsi),
                        UserDao.Properties.ConnTime.gt(SharePreferenceUtils.getInstance(this).getLong("status_notif_starttime" + ((ProxyApplication)getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)getApplicationContext()).getiTcpPort(), System.currentTimeMillis()))).build().list();
                int iAuth = 0;
                TargetUser targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImsi.eq(strImsi)).build().unique();
                if (targetUser !=  null) { iAuth = 2; }
                if (users.size() != 0) {
                    User updateData = users.get(0);
                    updateData.setIAuth(iAuth);
                    updateData.setDetachTime(System.currentTimeMillis());
                    ProxyApplication.getDaoSession().getUserDao().update(updateData);
                } else {
                    User insertData = new User(null, strImsi, null, iAuth, false, 1, null, null,  System.currentTimeMillis(), Unique);
                    ProxyApplication.getDaoSession().getUserDao().insert(insertData);
                }
                /////
                OneBtnHintDialog detachDialog = new OneBtnHintDialog(this, R.style.dialog_style);
                detachDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
                    @Override
                    public void onBtnClick(View v) {
                        onBackPressed();
                    }
                });
                detachDialog.setCanceledOnTouchOutside(false);
                detachDialog.show();
                detachDialog.setBtnContent("OK");
                detachDialog.setTitle("Warning");
                detachDialog.setContent(strImsi + " Detach");
                try {
                    BcastCommonApi.soundRing(mContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetDetach(TargetDetach targetDetach) {
        if (strImsi.equals(targetDetach.getImsi())) {
            bDetachFlag = true;
            OneBtnHintDialog detachDialog = new OneBtnHintDialog(this, R.style.dialog_style);
            detachDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
                @Override
                public void onBtnClick(View v) {
                    onBackPressed();
                }
            });
            detachDialog.setCanceledOnTouchOutside(false);
            detachDialog.show();
            detachDialog.setBtnContent("OK");
            detachDialog.setTitle("Warning");
            detachDialog.setContent(targetDetach.getImsi() + " Detach");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SilentCallRes(SilentCallRes scr) {
        Logs.d(TAG, "CurImsi=" + strImsi + ",scr.getImsi()=" + scr.getImsi());
        if (scr.getStatus().equals("SUCCESS") &&
                (TextUtils.isEmpty(scr.getImsi()) || (!TextUtils.isEmpty(scr.getImsi()) && scr.getImsi().equals(strImsi)))) {
            bSilentFlag = !bSilentFlag;
            User user =  ProxyApplication.getDaoSession().getUserDao().queryBuilder().orderDesc().where(UserDao.Properties.Unique.eq(SharePreferenceUtils.getInstance(this).getString("status_notif_unique" + ((ProxyApplication)getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)getApplicationContext()).getiTcpPort(), "")),
                    UserDao.Properties.SrtImsi.eq(strImsi),
                    UserDao.Properties.ConnTime.gt(SharePreferenceUtils.getInstance(this).getLong("status_notif_starttime" + ((ProxyApplication)getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)getApplicationContext()).getiTcpPort(), System.currentTimeMillis()))).build().unique();
            if (user != null) {
                user.setBSilent(bSilentFlag);
                ProxyApplication.getDaoSession().getUserDao().update(user);
            }
            CustomToast.showToast(mContext, "Silent call success");
        } else if (scr.getImsi().equals(strImsi) && scr.getStatus().equals("FAILURE")){
            CustomToast.showToast(mContext, "Silent call failure");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendSMSRes(SendSmsRes ssr) {
        if (ssr.getStatus().equals("SUCCESS")) {
            CustomToast.showToast(mContext, "Send SMS success");
        } else if (ssr.getStatus().equals("FAILURE")){
            CustomToast.showToast(mContext, "Send SMS failure");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void targetRedirRes(TargetRedirectRes trr) {
        if (trr.getStatus().equals("SUCCESS")) {
            CustomToast.showToast(mContext, "Target Redir success");
        } else if (trr.getStatus().equals("FAILURE")){
            CustomToast.showToast(mContext, trr.getImsi() + " Redir failure");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StatusNotif(Status s) {
        if (!s.getBtsState().equals("4") && !bDetachFlag) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!strCurIp.equals(((ProxyApplication)getApplicationContext()).getCurSocketAddress())) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { // 关闭TTS引擎
            tts.shutdown();
        }
        super.onDestroy();
    }
}
