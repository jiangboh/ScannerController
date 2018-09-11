package com.bravo.BlueTooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class tukTukService extends Service {
    private final String TAG = "tukTukService";
    private DeviceReceiver deviceReceiver;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice bluetoothDevice = null;
    private byte bByteUUID2[] = null;
    private Mutex mutex = new Mutex();
    //特征列表集
    private List<BluetoothGattCharacteristic > mCharacteristics = new ArrayList<>();
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (deviceReceiver == null) {
            deviceReceiver = new DeviceReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(deviceReceiver, filter);
        }
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Logs.d(TAG, "onStart tukTukService", "tt_log", true);
        mutex.unlock();
        if (mBluetoothGatt == null) {
            EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_STATUS_NOCONNECT));
        } else {
            EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_STATUS_ALREADY_CONNECT));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (deviceReceiver != null) {
            unregisterReceiver(deviceReceiver);
            deviceReceiver = null;
        }
        EventBus.getDefault().unregister(this);
        closeGatt();
        Logs.d(TAG, "close tukTukService", "tt_log", true);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void blueToothCtrl(EventBusTukTukMsg eventBusTukTukMsg) {
        switch (eventBusTukTukMsg.getAction()) {
            case EventBusTukTukMsg.BT_DISCONNECT_REQUEST:
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                }
                break;
            case EventBusTukTukMsg.BT_INIT_REQUEST:
                if (mCharacteristics.size() >= 4) {
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_INIT_SUCCESS));
                }
                break;
            case EventBusTukTukMsg.BT_CONNECT_REQUEST:
                Logs.d(TAG, "openGatt", "tt_log", true);
                openGatt(eventBusTukTukMsg.getBluetoothDevice());
                break;
            case EventBusTukTukMsg.BT_WRITE:
                mutex.lock();
                boolean bResult = writeCharacteristic(eventBusTukTukMsg.getBytes(), EventBusTukTukMsg.SERVICE_UUID, eventBusTukTukMsg.getUUID());
                Logs.d(TAG, "Write " + eventBusTukTukMsg.getUUID() + " result=" + bResult, "tt_log", true);
                if (bResult) {
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_WRITE_RESULT, eventBusTukTukMsg.getUUID(), EventBusTukTukMsg.BT_WRITE_SUCCESS));
                    switch (eventBusTukTukMsg.getUUID()) {
                        case EventBusTukTukMsg.CHARACTERISTIC_UUID2:
                        case EventBusTukTukMsg.CHARACTERISTIC_UUID6:
                            bByteUUID2 = eventBusTukTukMsg.getBytes();
                            break;
                        case EventBusTukTukMsg.CHARACTERISTIC_UUID4:
                        case EventBusTukTukMsg.CHARACTERISTIC_UUID7:
                            if (bByteUUID2 != null && mCharacteristics.size() < 6) {
                                EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_WRITE, EventBusTukTukMsg.CHARACTERISTIC_UUID2, bByteUUID2));
                            }
                            break;
                    }
                } else {
                    mutex.unlock();
                }
                if (eventBusTukTukMsg.getUUID() == EventBusTukTukMsg.CHARACTERISTIC_UUID2) {
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_READ, EventBusTukTukMsg.CHARACTERISTIC_UUID2));
                }
                break;
            case EventBusTukTukMsg.BT_READ:
                mutex.lock();
                bResult = readCharacteristic(eventBusTukTukMsg.SERVICE_UUID, eventBusTukTukMsg.getUUID());
                Logs.d(TAG, "Read " + eventBusTukTukMsg.getUUID() + " result=" + bResult, "tt_log", true);
                if (!bResult) {
                    mutex.unlock();
                }
                break;
            case EventBusTukTukMsg.BT_GET_DEVICE_REQUEST:
                EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_GET_DEVICE_RESPONSE, bluetoothDevice));
                break;
            default:
                break;
        }
    }

    //注册此广播，监听BluetoothDevice.ACTION_FOUND，以接收系统消息取得扫描结果
    private class DeviceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logs.d(TAG, "Bluetooth BroadcastReceiver=" + action, "tt_log", true);
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Logs.d(TAG, "BT Device Name=" + device.getName(), "tt_log", true);
                    if (!TextUtils.isEmpty(device.getName()) && device.getName().indexOf("TUK") != -1)
                        EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_SCAN_RESULT, device));
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_START_SCAN));
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_STOP_SCAN));
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Logs.d(TAG, "打开蓝牙", "tt_log", true);
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Logs.d(TAG, "蓝牙开启状态", "tt_log", true);
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Logs.d(TAG, "关闭蓝牙", "tt_log", true);
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Logs.d(TAG, "蓝牙关闭状态", "tt_log", true);
                            break;
                    }
                    break;
            }
        }
    }

    //mBluetoothGattCallback 为所有蓝牙数据回调的处理者，也是整个蓝牙操作当中最为核心的一部分
    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        //BluetoothGattCallback 里面有很多方法，但并非所有都需要在开发当中用到。
        //这里列出来只是作为部分解析，需要哪个方法，就重写哪个方法，不需要的，直接去掉。
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {//newState顾名思义，表示当前最新状态。status可以获取之前的状态。
                case BluetoothProfile.STATE_CONNECTED:
                    //这里表示已经成功连接，如果成功连接，我们就会执行discoverServices()方法去发现设备所包含的服务
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_CONNECT_SUCCESS));
                    gatt.discoverServices();
                    Logs.d(TAG, "Start to discover services.", "tt_log", true);
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_DISCONNECT_NOTIF));
                    //表示gatt连接已经断开。
                    Logs.d(TAG, "Connection is broken.", "tt_log", true);
                    gatt.close();
                    closeGatt();
                    break;
            }
        }
        //接下来，在执行discoverServices()后，外设就会告诉我们它能够为中心提供哪些服务
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //gatt.getServices()可以获得外设的所有服务。
                for (final BluetoothGattService service : gatt.getServices()) {//接下来遍历所有服务
                    if (EventBusTukTukMsg.SERVICE_UUID.equals(service.getUuid().toString())) {
                        //每发现一个服务，我们再次遍历服务当中所包含的特征，service.getCharacteristics()可以获得当前服务所包含的所有特征
                        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                            mCharacteristics.add(characteristic);//通常可以把所发现的特征放进一个列表当中以便后续操作。
                            Logs.d(TAG, "characteristic=" + characteristic.getUuid().toString() + ",isRead=" + ifCharacteristicReadable(characteristic) + ",isWrite="
                                    + ifCharacteristicWritable(characteristic) + ",isNotif=" + ifCharacteristicNotifiable(characteristic), "tt_log", true);//打印特征的UUID。
                            if (ifCharacteristicNotifiable(characteristic)) {
                                setCharacteristicNotification(characteristic, true);
                            }
                        }
                    }
                }
            }
            //当方法执行完后，我们就获取了设备所有的特征了。
            //如果你想知道每个特征都包含哪些描述符，很简单，再用一个循环去遍历每一个特征的getDescriptor()方法。
        }


        //当我们执行了readCharacteristic()方法后，结果会回调在此。
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Logs.d(TAG, "onCharacteristicRead=" + characteristic.getUuid().toString() + "=" + bytesToHexString(characteristic.getValue()), "tt_log", true);
            mutex.unlock();
            if(status == BluetoothGatt.GATT_SUCCESS) {
                //如果程序执行到这里，证明特征的读取已经完成，我们可以在回调当中取出特征的值。
                //特征所包含的值包含在一个byte数组内，我们可以定义一个临时变量来获取。

//                byte[] characteristicValueBytes = characteristic.getValue();
                //如果这个特征返回的是一串字符串，那么可以直接获得其值
//                String bytesToString = new String(characteristicValueBytes );

                //如果只需要取得其中的几个byte，可以直接指定获取特定的数组位置的byte值.
                //例如协议当中定义了这串数据当中前2个byte表示特定一个数值，那么获取这个值，可以直接写成
//                byte[] aValueBytes = new byte[]{characteristic.getValue()[0], characteristic.getValue()[2]};

                EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_RECEIVER, characteristic.getUuid().toString(), characteristic.getValue()));
                //至于这个值时表示什么，十进制数值？或是一个字符串？还是翻开协议慢慢找吧。
                //到这里为止，我们已经成功采用读的方式，获得了存在于特征当中的值。
                //characteristic还能为我们提供什么东西呢？属性，权限等是比较常用的。
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            //BluetoothGatt.GATT_SUCCESS=0
            Logs.d(TAG, "CharacteristicWrite=" + characteristic.getUuid().toString() +",Value="  + bytesToHexString(characteristic.getValue()) + ",Reault=" + status, "tt_log", true);
            mutex.unlock();
//            if (status == 0) {
//                EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_WRITE_RESULT, characteristic.getUuid().toString(), EventBusTukTukMsg.BT_RESULT_SUCCESS));
//            } else {
//                EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_WRITE_RESULT, characteristic.getUuid().toString(), EventBusTukTukMsg.BT_RESULT_FAILURE));
//            }
            //写入特征后回调到此处。
        }

        //当我们执行了gatt.setCharacteristicNotification或写入特征的时候，结果会回调在此
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Logs.d(TAG, "onCharacteristicChanged=" + characteristic.getUuid().toString() + "," + bytesToHexString(characteristic.getValue()), "tt_log", true);
            switch (characteristic.getUuid().toString()) {
                case EventBusTukTukMsg.CHARACTERISTIC_UUID6:
                case EventBusTukTukMsg.CHARACTERISTIC_UUID3:
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_RECEIVER, characteristic.getUuid().toString(), characteristic.getValue()));
                    break;
                default:
                    break;
            }

            //当我们决定用通知的方式获取外设特征值的时候，每当特征值发生变化，程序就会回调到此处。
            //在一个gatt链接当中，可以同时存在多个notify的回调，全部值都会回调到这里，那么我们如何区分这些值的来源？
            //这个时候，我们就需要去判断回调回来的特征的UUID，因为UUID是唯一的，所以我们可以用UUID
            //来确定，这些数据来自哪个特征。
            //假设我们已经在BleService当中定了多个我们想要使用的静态UUID，前面已经说过如何表达一个UUID
            //那么我们需要做的就是对比这些UUID，根据不同的UUID来分类这些数据，究竟应该交由哪个方法来处理

            //所以，这么一来我们便会发现其实上面的onCharacteristicRead也会出现这种情况，
            //因为我们不可能只读取一个特征，除非这个外设也只有这一个特征，
            //究竟是谁在读取，读取的值来自于哪个特征等，都需要进行判断。


            //我们会有更好的写法，我们应该抽象这些方法，这样一来我们可能会减少很多代码量。
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Logs.d(TAG, "onDescriptorRead=" + descriptor.getUuid().toString());
            //读取描述符后回调到此处。
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Logs.d(TAG, "onDescriptorWrite="+ descriptor.getUuid().toString());
            //写入描述符后回调到此处
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            Logs.d(TAG, "onReliableWriteCompleted");
            super.onReliableWriteCompleted(gatt, status);
            //暂时没有用过。
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Logs.d(TAG, "onReadRemoteRssi");
            //Rssi表示设备与中心的信号强度，发生变化时回调到此处。
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Logs.d(TAG, "onMtuChanged");
            //暂时没有用过。
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openGatt(BluetoothDevice mTargetDevice){
        if (mBluetoothGatt != null && bluetoothDevice != null && bluetoothDevice.getAddress().equals(mTargetDevice.getAddress())) {
            EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_CONNECT_SUCCESS));
            return;
        } else if (mBluetoothGatt != null){
            closeGatt();
        }
        bluetoothDevice = mTargetDevice;
        mBluetoothGatt = mTargetDevice.connectGatt(this, false, mBluetoothGattCallback);
        //通过之前扫描所得设备，打开Gatt链接。
        //连接可能需要等待。
        //第一个参数是传Context，这个很好理解。
        //第二个参数是控制是否自动链接，为true的时候，当设备进入中心范围，会进行自动连接，为false反之。
        //第三个参数就是上面那个GattCallback了。
        //此步骤执行之后，所有结果都会回调到GattCallback当中，接下来我们就需要对其进行操作了。
    }

    //断开连接
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void closeGatt(){
        if(mBluetoothGatt != null){
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();//不会进入onConnectionStateChange
            mBluetoothGatt = null;
        }
        bluetoothDevice = null;
        mCharacteristics.clear();
        mutex.unlock();
    }

    //判断特征可读
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean ifCharacteristicReadable(BluetoothGattCharacteristic characteristic){
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) > 0);
    }

    //判断特征可写
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean ifCharacteristicWritable(BluetoothGattCharacteristic characteristic){
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0 ||
                (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0);
    }

    //判断特征是否具备通知属性
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean ifCharacteristicNotifiable (BluetoothGattCharacteristic characteristic){
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0 ||
                (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0);
    }

    //设置通知，读取结果会回调到mGattCallback中的onCharacteristicChanged
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable){
        mBluetoothGatt.setCharacteristicNotification(characteristic, enable);
        //以下的几句代码有人问可不可省略，这里建议写上。
        //在明确知道当前特征的描述符前提下，可以直接使用描述符，不需要做判断，
        //但如果不知道此特征是否具有描述符的情况下，没有以下几行代码可能会导致设置通知失败的情况发生。
        List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
        if (descriptorList != null) {
            for (BluetoothGattDescriptor descriptor : descriptorList) {
                Logs.d(TAG, "descriptor=" + descriptor.getCharacteristic().getUuid().toString());
                byte[] value = enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                descriptor.setValue(value);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }
    //读取特征，相当简单，一句话带过，读取结果会回调到mGattCallback中的onCharacteristicRead
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean readCharacteristic(String serviceUUID, String characterisiticUUID){
        Logs.d(TAG, "Read Request Characteristic=" + characterisiticUUID, "tt_log", true);
        if (mBluetoothGatt != null) {
            if (characterisiticUUID.equals(EventBusTukTukMsg.CHARACTERISTIC_UUID2) && mCharacteristics.size() >= 5) {
                characterisiticUUID = EventBusTukTukMsg.CHARACTERISTIC_UUID6;
            }
            for (int i = 0;i < mCharacteristics.size(); i++) {
                if (mCharacteristics.get(i).getUuid().toString().equals(characterisiticUUID)) {
                    BluetoothGattService mSVC = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
                    BluetoothGattCharacteristic mCH = mSVC.getCharacteristic(UUID.fromString(characterisiticUUID));
                    return mBluetoothGatt.readCharacteristic(mCH);
                }
            }
        }
        return false;
    }
    //写入特征，也相当简单，一句话带过，读取结果会回调到mGattCallback中的onCharacteristicWrite
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean writeCharacteristic(byte[] data_to_write, String serviceUUID, String characterisiticUUID){
        Logs.d(TAG, "Write Request Characteristic=" + characterisiticUUID + ",Value=" +bytesToHexString(data_to_write), "tt_log", true);
        if (mBluetoothGatt != null) {
            if (characterisiticUUID.equals(EventBusTukTukMsg.CHARACTERISTIC_UUID2) && mCharacteristics.size() >= 5) {
                characterisiticUUID = EventBusTukTukMsg.CHARACTERISTIC_UUID6;
            }
            for (int i = 0;i < mCharacteristics.size(); i++) {
                if (mCharacteristics.get(i).getUuid().toString().equals(characterisiticUUID)) {
                    BluetoothGattService mSVC = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
                    BluetoothGattCharacteristic mCH = mSVC.getCharacteristic(UUID.fromString(characterisiticUUID));
                    mCH.setValue(data_to_write);
                    return mBluetoothGatt.writeCharacteristic(mCH);
                }
            }
        }
        EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_NONSUPPORT, characterisiticUUID));
        return false;
    }

    public String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
