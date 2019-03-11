package com.bravo.scanner;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.audio.VoiceSpeaker;
import com.bravo.audio.VoiceTemplate;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.DeviceFragmentStruct;
import com.bravo.data_ben.PositionDataStruct;
import com.bravo.data_ben.SavePositionData;
import com.bravo.dialog.DialogCustomBuilder;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.Logs;
import com.bravo.xml.HandleRecvXmlMsg;
import com.bravo.xml.LTE_GeneralPara;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;
import static com.bravo.R.drawable.btn_sound_open;
import static com.bravo.R.id.imsi;
import static com.bravo.data_ben.DeviceFragmentStruct.getDevice;

/**
 * Created by admin on 2018-12-11.
 */

public class FragmentpPositionListen extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentpPositionListen";

    public static boolean isOpen = false; //界面是否打开

    private  static ArrayList<String> imsiList = new ArrayList<String>();
    private static ArrayAdapter<String> adapter;

    private SimpleDateFormat  formatter = new SimpleDateFormat("HH:mm:ss");
    private Boolean attOpen = false;
    private Boolean soundOpen = false;
    private Boolean sync_status = false;
    private static Boolean openOffset = true;
    private String currImsi = "";
    private int iRxGain = -8;

    private PowerManager.WakeLock mWakeLock;

    private TextView posTime;
    private TextView tRssi;
    private Spinner sImsi;
    private Button bAtt;
    private Button bSound;
    private Button bSync;

    private TextView dSn;
    private TextView dFullname;
    private TextView dMode;
    private TextView dIp;
    private TextView dPort;

    private static List<SavePositionData> allSaveData = new ArrayList<SavePositionData>();
    private List<PositionDataStruct> dataList = new ArrayList<PositionDataStruct>();

    private LineChart lineChart;
    private XAxis xAxis;                //X轴
    private YAxis leftYAxis;            //左侧Y轴
    private YAxis rightYaxis;           //右侧Y轴
    private Legend legend;              //图例
    private LimitLine limitLine;        //限制线
//  private MyMarkerView markerView;    //标记视图 即点击xy轴交点时弹出展示信息的View 需自定义

    public static void setOpenOffset(boolean flag) {
        openOffset = flag;
    }

    public static void addPositionData(PositionDataStruct data) {
        boolean flag = false;
        for(int i=0;i<allSaveData.size();i++) {
            if (allSaveData.get(i).getImsi().equals(data.getImsi())) {
                flag = true;
                List<PositionDataStruct> list = allSaveData.get(i).getDataList();
                list.add(data);
                while (list.size() > 50) {
                    list.remove(0);
                }
                break;
            }
        }
        if (!flag) { //增加新项
            List<PositionDataStruct> tmp = new ArrayList<PositionDataStruct>();
            tmp.add(data);
            allSaveData.add(new SavePositionData(data.getImsi(),tmp));
            imsiList.add(data.getImsi());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.GONE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_end_normal);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {

            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_scanner_black_imsi);

        loadData();

        //屏幕不休眠
        PowerManager pManager = ((PowerManager) context.getSystemService(POWER_SERVICE));
        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, TAG);
        mWakeLock.acquire();
    }

    @Override
    public void initView() {
        posTime = (TextView)contentView.findViewById(R.id.positionTime);
        tRssi = (TextView)contentView.findViewById(R.id.rssi);
        tRssi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Logs.d(TAG,"改变后的值：" + tRssi.getText().toString());
                if (soundOpen) {
                    onPlayMultiSounds(Integer.parseInt(tRssi.getText().toString()));
                }
                //onPlaySingleSoundClicked();
            }
        });

        dSn = (TextView)contentView.findViewById(R.id.sn);
        dFullname = (TextView)contentView.findViewById(R.id.fullname);
        dMode = (TextView)contentView.findViewById(R.id.mode);
        dIp = (TextView)contentView.findViewById(R.id.ip);
        dPort = (TextView)contentView.findViewById(R.id.port);

        lineChart = (LineChart)contentView.findViewById(R.id.lineChart);
        initChart(lineChart);

        sImsi = (Spinner)contentView.findViewById(imsi);

        adapter = new ArrayAdapter<String>(context,R.layout.my_spinner ,imsiList);
        sImsi.setAdapter(adapter);
        //添加事件Spinner事件监听
        sImsi.setOnItemSelectedListener(new SpinnerSelectedListener());

        bAtt = (Button) contentView.findViewById(R.id.att);
        if (attOpen) {
            bAtt.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_att_open));
        } else {
            bAtt.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_att_close));
        }
        bAtt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DeviceDataStruct dds = getDevice(dSn.getText().toString());
                if (dds == null) {
                    CustomToast.showToast(context, "没有设备信息");
                    return;
                }
                if ((!dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)) &&
                        (!dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD))) {
                    CustomToast.showToast(context, "目前仅LTE产品支持上行衰减功能");
                    return;
                }
                if (sync_status) {
                    LTE_GeneralPara para = (LTE_GeneralPara)dds.getGeneralPara();
                    if (para.getSource() == 1) {
                        DialogCustomBuilder dialog = new DialogCustomBuilder(
                                context,"下发停止同步命令","目前为空口同步，请先停止同步后再进行衰减\n是否立即停止同步?");
                        dialog.setOkListener(new DialogCustomBuilder.OkBtnClickListener() {
                            @Override
                            public void onBtnClick(DialogInterface arg0, int arg1) {
                                DeviceDataStruct d =  DeviceFragmentStruct.getDevice(dSn.getText().toString());
                                new HandleRecvXmlMsg(context,d).SetCnmSyncStatus(true);
                            }
                        });
                        dialog.setCancelListener(new DialogCustomBuilder.CancelBtnClickListener() {
                            @Override
                            public void onBtnClick(DialogInterface arg0, int arg1) {
                            }
                        });
                        dialog.show();

                        return;
                    }
                }

                if (attOpen) {
                    attOpen = false;
                    CustomToast.showToast(context, "已关闭上行衰减功能");
                    bAtt.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_att_close));
                    if (dds != null)
                        new HandleRecvXmlMsg(context, dds).SetGeneralParaRequest("CFG_RF_UPLINK_GAIN", "0");
                } else {
                    attOpen = true;
                    CustomToast.showToast(context, "已打开上行衰减功能");
                    bAtt.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_att_open));
                    if (dds != null)
                        new HandleRecvXmlMsg(context, dds).SetGeneralParaRequest("CFG_RF_UPLINK_GAIN", String.valueOf(iRxGain));

                }
                saveData();

            }
        });

        bSound = (Button) contentView.findViewById(R.id.sound);

        if (soundOpen) {
            bSound.setBackgroundDrawable(context.getResources().getDrawable(btn_sound_open));
        } else {
            bSound.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_sound_close));
        }
        bSound.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //CustomToast.showToast(context,"语音播报功能暂未实现");
                if (soundOpen) {
                    soundOpen = false;
                    onStopPlay();
                    bSound.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_sound_close));
                } else {
                    soundOpen = true;
                    bSound.setBackgroundDrawable(context.getResources().getDrawable(btn_sound_open));
                }
                saveData();
            }
        });

        bSync = (Button) contentView.findViewById(R.id.sync);
        SetSyncStatus();
        bSync.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DeviceDataStruct dev =  DeviceFragmentStruct.getDevice(dSn.getText().toString());
                if (dev == null) {
                    CustomToast.showToast(context, "没有设备信息");
                    return;
                }
                if (((LTE_GeneralPara)dev.getGeneralPara()).getSource() != 1) {
                    CustomToast.showToast(context, "非CNM同步，不能作此操作");
                    return;
                }

                if (sync_status) {
                    new HandleRecvXmlMsg(context, dev).SetCnmSyncStatus(true);
                    CustomToast.showToast(context, "停止CNM同步命令已下发，请等待状态变化");
                } else {
                    new HandleRecvXmlMsg(context, dev).SetCnmSyncStatus(false);
                    CustomToast.showToast(context, "开启CNM同步命令已下发，请等待状态变化");
                }
            }
        });

    }

    private void SetSyncStatus() {
        String sn = dSn.getText().toString();

        if (sn.length() <= 0)  {
            sync_status = false;
        } else {
            DeviceDataStruct device = getDevice(sn);

            if (device == null) {
                sync_status = false;
            } else {
                sync_status = device.isStatus_sync();
            }
        }

        if (sync_status) {
            bSync.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_sync_ok));
        } else {
            bSync.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_sync_fail));
        }
    }


    @Override
    public void initData(Bundle savedInstanceState) {

    }

    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Logs.d(TAG, "您选择了:" + imsiList.get(position));
            initChart(lineChart);
            currImsi = imsiList.get(position);

            //更新数据列表
            dataList.clear();
            for(int i=0;i<allSaveData.size();i++) {
                if (currImsi.equals(allSaveData.get(i).getImsi())) {
                    for(int j =0;j<allSaveData.get(i).getDataList().size();j++) {
                        dataList.add(allSaveData.get(i).getDataList().get(j));
                    }
                    break;
                }
            }

            showLineChart(dataList,currImsi, 0xFF6FA9E1);

            PositionDataStruct data = new PositionDataStruct("",currImsi,"0000/00/00-00:00:00",0);
            if (dataList.size() > 0) {
                data = dataList.get(dataList.size() - 1);
            }
            setRssiAndTime(data);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private void saveData() {
        SharedPreferences preferences = context.getSharedPreferences("FragmentpPositionListen", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("attOpen", attOpen);
        editor.putBoolean("soundOpen", soundOpen);
        editor.commit();
    }

    private void loadData() {
        SharedPreferences sp = context.getSharedPreferences("FragmentpPositionListen", MODE_PRIVATE);
        attOpen = sp.getBoolean("attOpen",false);
        soundOpen = sp.getBoolean("soundOpen",false);

        SharedPreferences sp1 = context.getSharedPreferences(FragmentScannerConfig.TABLE_NAME, MODE_PRIVATE);
        iRxGain = sp1.getInt(FragmentScannerConfig.tn_RxGain,FragmentScannerConfig.DefultRxGain);
        Log.v("上行衰减初始值：", String.valueOf(iRxGain));
        openOffset = sp1.getBoolean(FragmentScannerConfig.tn_OpenOffset,FragmentScannerConfig.DefultOpenOffset);
        Log.v("补偿上行衰减值：", String.valueOf(openOffset));

    }

    @Override
    public void onPause() {
        Logs.d(TAG,"onPause",true);
        saveData();
        isOpen = false;
        super.onPause();
    }

    @Override
    public void onStop() {
        Logs.d(TAG,"onStop",true);
        onStopPlay();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Logs.d(TAG,"onDestroy",true);
        super.onDestroy();

    }


    /**
     * 设置线条填充背景颜色
     *
     * @param drawable
     */
    public void setChartFillDrawable(Drawable drawable) {
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            LineDataSet lineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            //避免在 initLineDataSet()方法中 设置了 lineDataSet.setDrawFilled(false); 而无法实现效果
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillDrawable(drawable);
            lineChart.invalidate();
        }

    }


    /**
     * 初始化图表
     */
    private void initChart(LineChart lineChart) {
        /***图表设置***/

        //是否展示网格线
        lineChart.setDrawGridBackground(false);

        //是否显示边界
        lineChart.setDrawBorders(false);

        lineChart.setBackgroundColor(Color.WHITE);

        //是否可以拖动
        lineChart.setDragEnabled(false);

        //是否有触摸事件
        lineChart.setTouchEnabled(true);

        //设置XY轴动画效果
        lineChart.animateY(2500);
        lineChart.animateX(1500);

        /***XY轴的设置***/
        xAxis = lineChart.getXAxis();
        leftYAxis = lineChart.getAxisLeft();
        rightYaxis = lineChart.getAxisRight();

        xAxis.setDrawGridLines(false);
        rightYaxis.setDrawGridLines(false);
        leftYAxis.setDrawGridLines(true);

        //leftYAxis.setAxisMinimum(-128f); // start at zero
        //leftYAxis.setAxisMaximum(128f); // the axis maximum is 100
        leftYAxis.enableGridDashedLine(10f, 10f, 0f);

        rightYaxis.setEnabled(false);

        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);


        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value <=0) value=0;
                String tradeDate = dataList.get((int) value % dataList.size()).getTradeDate();
                return formatDate(tradeDate);
            }
        });

        leftYAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ((int) (value-128)) + "dBm";
            }
        });
        leftYAxis.setLabelCount(8);

        //保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisMinimum(0f);
        rightYaxis.setAxisMinimum(0f);

        /***折线图例 标签 设置***/
        legend = lineChart.getLegend();

        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);

        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        //是否绘制在图表里面
        legend.setDrawInside(false);

        Description description = new Description();
        //description.setText("需要展示的内容");
        description.setEnabled(false);
        lineChart.setDescription(description);

    }



    private static String formatDate(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        SimpleDateFormat sf2 = new SimpleDateFormat("HH:mm");
        String formatStr = "";
        try {
            formatStr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatStr;
    }

    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    private void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(1f);

        lineDataSet.setCircleRadius(3f);

        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);

        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);

        //设置折线图填充
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);

        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }


    }


    /**
     * 展示曲线
     *
     * @param dataList 数据集合
     * @param name     曲线名称
     * @param color    曲线颜色
     */
    public void showLineChart(List<PositionDataStruct> dataList, String name, int color) {
        //重新更新x轴的起始值
        lineChart.setScaleMinima(1.0f, 1.0f);
        lineChart.getViewPortHandler().refresh(new Matrix(), lineChart, true);

        Drawable drawable = getResources().getDrawable(R.drawable.fade_blue);
        setChartFillDrawable(drawable);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            PositionDataStruct data = dataList.get(i);
            /**
             * 在此可查看 Entry构造方法，可发现 可传入数值 Entry(float x, float y)
             * 也可传入Drawable， Entry(float x, float y, Drawable icon) 可在XY轴交点 设置Drawable图像展示
             */
            Entry entry;
            if (openOffset) {
                entry = new Entry(i, (int) data.getValue() - (int)data.getRxGain() + 128);
            } else {
                entry = new Entry(i, (int) data.getValue() + 128);
            }
            entries.add(entry);
        }

        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.LINEAR);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void onStopPlay() {
        VoiceSpeaker.getInstance().stopSpeak();
    }

    public void onPlayMultiSounds(int rssi) {
        //context.sendBroadcast(new Intent(context, VoiceBroadcastReceiver.class));
        List<String> list = new VoiceTemplate()
                //.prefix("success")
                //.numString("15.00")
                //.suffix("yuan")
                .numString(String.valueOf(Math.abs(rssi)))
                .gen();

        VoiceSpeaker.getInstance().startSpeak(context,list);
    }

    private void setRssiAndTime(PositionDataStruct data)
    {
        Logs.d(TAG,"补偿开关：" + String.valueOf(openOffset));
        if (openOffset) {
            tRssi.setText(String.valueOf(data.getValue() - data.getRxGain()));
        } else {
            tRssi.setText(String.valueOf(data.getValue()));
        }

        posTime.setText(data.getTradeDate().substring(11));

        dSn.setText(data.getSn());
        SetSyncStatus();
        DeviceDataStruct device = getDevice(data.getSn());
        if (device != null) {
            dFullname.setText(device.getFullName());
            dMode.setText(device.getMode());
            dIp.setText(device.getIp());
            dPort.setText(String.valueOf(device.getPort()));

            int att = ((LTE_GeneralPara)device.getGeneralPara()).getRfTxGain();
            if (att < 0) {
                attOpen = true;
            } else {
                attOpen = false;
            }
            if (attOpen) {
                bAtt.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_att_open));
            } else {
                bAtt.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_att_close));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetPosition(PositionDataStruct data) {
        boolean flag = false;
        String rImsi = data.getImsi().toString();
        List<PositionDataStruct> currList = new ArrayList<PositionDataStruct>();

        Logs.d(TAG,"接收到定位消息：" + rImsi + ":" + data.getTradeDate() +
                "(" + data.getValue() + ")衰减:" + data.getRxGain());

        //更新数据列表
        for(int i=0;i<allSaveData.size();i++) {
            if (rImsi.equals(allSaveData.get(i).getImsi())) {
                for(int j =0;j<allSaveData.get(i).getDataList().size();j++) {
                    currList.add(allSaveData.get(i).getDataList().get(j));
                }
                flag = true;
                break;
            }
        }

        if (currImsi.length() == 0) {
            sImsi.setSelection(0);
            //initChart(lineChart);
            //currImsi = data.getImsi();

            //showLineChart(dataList,currImsi, 0xFF6FA9E1);
        }

        if (currImsi.equals(data.getImsi())) { //更新界面

            setRssiAndTime(data);

            dataList.clear();
            for(int j=0;j<currList.size();j++) {
                dataList.add(currList.get(j));
            }

            showLineChart(dataList,currImsi, 0xFF6FA9E1);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LteParaChangesEvens(LTE_GeneralPara para) {
        DeviceDataStruct dds = getDevice(dSn.getText().toString());
        if (para.getSn().equals(dds.getSN())) {
            Logs.d(TAG, "接收到LTE参数改变改变事件", true, true);
            SetSyncStatus();
        }
    }
}