package com.bravo.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * 模拟接到推送通知
 * @author jiangkang
 * @date 2017/10/18
 */

public class VoiceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final List<String> list = new VoiceTemplate()
                .prefix("success")
                .numString("15.00")
                .suffix("yuan")
                .gen();

        VoiceSpeaker.getInstance().startSpeak(context,list);
    }


}
