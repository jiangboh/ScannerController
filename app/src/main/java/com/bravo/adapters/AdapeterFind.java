package com.bravo.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.config.Fragment_SystemConfig;
import com.bravo.custom_view.CustomToast;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.socket_service.EventBusMsgSendUDPMsg;
import com.bravo.utils.Logs;
import com.bravo.xml.Msg_Body_Struct;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.bravo.utils.Utils.getWifiIp;
import static com.bravo.xml.XmlCodec.EncodeApXmlMessage;

/**
 * Created by admin on 2018-9-17.
 */

public class AdapeterFind  extends BaseAdapter {
    private final static String TAG = "AdapeterFind";
    private final ArrayList<DeviceDataStruct> deviceDataStructs = new ArrayList<>();
    private Context mContext;
    //private int iCurPosition;
    private String strCurTech;
    private int iCurFindTotal = 0;
    private boolean findList = true;
    private TextView tvTotal;
    private int udpPort;

    public AdapeterFind(Context context,boolean findList) {
        this.findList = findList;
        this.mContext = context;
        SharedPreferences sp1 = context.getSharedPreferences(Fragment_SystemConfig.TABLE_NAME, MODE_PRIVATE);
        udpPort = sp1.getInt(Fragment_SystemConfig.tn_LisenPort,Fragment_SystemConfig.DefultPort);
        //iCurPosition = -1;
    }

    public AdapeterFind(Context context, TextView txView) {
        this.mContext = context;
        SharedPreferences sp1 = context.getSharedPreferences(Fragment_SystemConfig.TABLE_NAME, MODE_PRIVATE);
        udpPort = sp1.getInt(Fragment_SystemConfig.tn_LisenPort,Fragment_SystemConfig.DefultPort);
        //iCurPosition = -1;
    }

    public void updateCurTech(String strCurTech) {
        this.strCurTech = strCurTech;
    }

    @Override
    public int getCount() {
        return deviceDataStructs.size();
    }

    @Override
    public DeviceDataStruct getItem(int position) {
        return deviceDataStructs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<DeviceDataStruct> getList() {
        return deviceDataStructs;
    }


    public void removeTarget(int iPosition) {
        deviceDataStructs.remove(iPosition);
        notifyDataSetChanged();
    }

    public void RemoveAll() {
        deviceDataStructs.clear();
        iCurFindTotal = 0;

        notifyDataSetChanged();
    }


    public int getAuthTotal() {
        return iCurFindTotal;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_find_list, null);
            holder.addImage = (TextView) convertView.findViewById(R.id.addImageView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.fullname_layout = (LinearLayout)convertView.findViewById(R.id.fullname_layout);

            holder.lastTime = (LinearLayout)convertView.findViewById(R.id.layout_LastTime);
            holder.SN = (TextView) convertView.findViewById(R.id.sn);
            holder.FullName = (TextView) convertView.findViewById(R.id.fullname);
            holder.Mode = (TextView) convertView.findViewById(R.id.mode);
            holder.AddStatus = (TextView) convertView.findViewById(R.id.addStatus);
            holder.viewS = (View) convertView.findViewById(R.id.viewStatus);
            holder.Name_AddStatus = (TextView) convertView.findViewById(R.id.name_addStatus);
            holder.Ip = (TextView) convertView.findViewById(R.id.ip);
            holder.Port = (TextView) convertView.findViewById(R.id.port);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lastTime.setVisibility(View.GONE);
        holder.SN.setText(deviceDataStructs.get(position).getSN());
        holder.FullName.setText(deviceDataStructs.get(position).getFullName());
        holder.Mode.setText(deviceDataStructs.get(position).getMode());
        holder.AddStatus.setText(DeviceDataStruct.sAddStatus[deviceDataStructs.get(position).getAddStatus()]);
        holder.Ip.setText(deviceDataStructs.get(position).getIp());
        holder.Port.setText(String.valueOf(deviceDataStructs.get(position).getPort()));

        holder.AddStatus.setVisibility(View.INVISIBLE);
        holder.Name_AddStatus.setVisibility(View.INVISIBLE);
        holder.viewS.setVisibility(View.INVISIBLE);

        if (findList) {
            holder.imageView.setVisibility(View.GONE);
            holder.addImage.setVisibility(View.VISIBLE);

            holder.addImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logs.d(TAG, "点击添加设备按钮",true);
                    Logs.d(TAG, "ip=" + deviceDataStructs.get(position).getIp() + ";" + deviceDataStructs.get(position).getPort(),true);

                    Msg_Body_Struct text = new Msg_Body_Struct(0, Msg_Body_Struct.SetUDPServerIp);
                    text.dic.put("ip", getWifiIp(mContext));
                    text.dic.put("port", udpPort);
                    String sendText = EncodeApXmlMessage(text);

                    EventBusMsgSendUDPMsg msg = new EventBusMsgSendUDPMsg(
                            deviceDataStructs.get(position).getIp(),
                            deviceDataStructs.get(position).getPort(),
                            sendText);

                    EventBus.getDefault().post(msg);

                    CustomToast.showToast(mContext, "点击添加设备按钮");
                }
            });

            if (deviceDataStructs.get(position).getiState() == DeviceDataStruct.ON_LINE) {
                holder.addImage.setVisibility(View.INVISIBLE);
            } else {
                holder.addImage.setVisibility(View.VISIBLE);
            }
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.addImage.setVisibility(View.GONE);
        }

        return convertView;

        /* String mode = deviceDataStructs.get(position).getBootmode();
            if (mode.equalsIgnoreCase("LTE")) {
                holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_4g_default));
            } else if (mode.equalsIgnoreCase("WCDMA")) {
                holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_3g_default));
            } else if (mode.equalsIgnoreCase("GSM")) {
                holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_2g_default));
            } else {
                holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_4g_default));
            }*/
    }

    private class ViewHolder {
        TextView addImage;
        ImageView imageView;
        TextView SN;
        TextView FullName;
        TextView Mode;
        TextView Name_AddStatus;
        TextView AddStatus;
        TextView Ip;
        TextView Port;
        LinearLayout fullname_layout;
        LinearLayout lastTime;
        View viewS;
    }

    public void DeviceListTarget(DeviceDataStruct deviceDataStruct) {
        boolean newDevice = true;

        for (int i = 0; i < iCurFindTotal; i++) {
            if (deviceDataStruct.getSN().equals(deviceDataStructs.get(i).getSN())) {
                deviceDataStructs.get(i).setiState(deviceDataStruct.getiState());

                newDevice = false;
                break;
            }
        }

        if (newDevice) {
            deviceDataStruct.setiState(DeviceDataStruct.ON_LINE);
            deviceDataStructs.add(deviceDataStruct);
            iCurFindTotal++;
        }

        notifyDataSetChanged();
    }

    public void DeviceListChange(DeviceDataStruct deviceDataStruct) {
        for (int i = 0; i < iCurFindTotal; i++) {
            if (deviceDataStruct.getSN().equals(deviceDataStructs.get(i).getSN())) {
                deviceDataStructs.get(i).setiState(deviceDataStruct.getiState());

                break;
            }
        }

        notifyDataSetChanged();
    }
}
