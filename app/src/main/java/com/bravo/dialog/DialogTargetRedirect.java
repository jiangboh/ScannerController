package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.parse_generate_xml.target_redirect.TargetRedirectReq;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;
import static com.bravo.femto.BcastCommonApi.checkChannel;

/**
 * Created by Jack.liao on 2016/10/19.
 */

public class DialogTargetRedirect extends Dialog {
    private final String TAG = "DialogTargetRedirect";
    private Spinner spinner_tech;
    private Spinner spinner_band;
    private EditText edit_channel;
    private Spinner spinner_movecell;
    private Button BtnTargetRedirect;
    private OnCustomDialogListener customDialogListener;
    private boolean bUpdateState = false;
    private Context context;

    public interface OnCustomDialogListener{
        void DialogCallBack(TargetRedirectReq targetRedirectReq);
    }

    public DialogTargetRedirect(Context context, int theme, OnCustomDialogListener customListener) {
        super(context, theme);
        this.context = context;
        this.customDialogListener = customListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_target_redirect);

        spinner_tech = (Spinner) findViewById(R.id.tech);
        spinner_tech.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, Arrays.asList("GSM", "UMTS", "LTE")));
        spinner_tech.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Logs.d(TAG, "lmj spinner_tech.setOnItemSelectedListener");
                if (bUpdateState) {
                    changeBand(position);
                } else {
                    bUpdateState = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_band = (Spinner) findViewById(R.id.band);
        edit_channel = (EditText) findViewById(R.id.channel);
        //move cell
        spinner_movecell = (Spinner) findViewById(R.id.movecell);
        spinner_movecell.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int iPosition = -1;
                switch (position) {
                    case 0:
                        switch (SharePreferenceUtils.getInstance(context).getString("status_notif_tech" +
                                ((ProxyApplication) context.getApplicationContext()).getCurSocketAddress() +
                                ((ProxyApplication) context.getApplicationContext()).getiTcpPort(), "")) {
                            case "2G":
                                iPosition = 0;
                                break;
                            case "3G":
                                iPosition = 1;
                                break;
                            case "4G":
                                iPosition = 2;
                                break;
                            default:
                                break;
                        }
                        Logs.d(TAG, "lmj spinner_tech.getSelectedItemPosition()=" + spinner_tech.getSelectedItemPosition() + ",devicePositon=" + iPosition);
                        if (spinner_tech.getSelectedItemPosition() != iPosition) {
                            spinner_tech.setSelection(iPosition);
                            changeBand(iPosition);
                        }
                        findViewById(R.id.layout_tech).setVisibility(View.GONE);
                        break;
                    case 1:
                        findViewById(R.id.layout_tech).setVisibility(View.VISIBLE);
                        break;
                    default:
                        return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BtnTargetRedirect = (Button) findViewById(R.id.target_redirect);
        BtnTargetRedirect.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (!checkChannel(spinner_tech.getSelectedItemPosition() + 1,
                        Integer.parseInt(spinner_band.getSelectedItem().toString()),
                        edit_channel.getText().toString())) {
                    edit_channel.requestFocus();
                    edit_channel.setError("Invalid channel");
                } else {
                    TargetRedirectReq targetRedirectReq = new TargetRedirectReq();
                    if (spinner_movecell.getSelectedItemPosition() == 0) {
                        switch (SharePreferenceUtils.getInstance(context).getString("status_notif_tech" +
                                ((ProxyApplication) context.getApplicationContext()).getCurSocketAddress() +
                                ((ProxyApplication) context.getApplicationContext()).getiTcpPort(), "")) {
                            case "2G":
                                targetRedirectReq.setTech("GSM");
                                break;
                            case "3G":
                                targetRedirectReq.setTech("UMTS");
                                break;
                            case "4G":
                                targetRedirectReq.setTech("LTE");
                                break;
                            default:
                                targetRedirectReq.setTech("ERROR");
                                break;
                        }
                        //targetRedirectReq.setTech(SharePreferenceUtils.getInstance(context).getString("status_notif_tech_capability" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getCurSocketPort(), ""));
                    } else {
                        targetRedirectReq.setTech(spinner_tech.getSelectedItem().toString());
                    }
                    targetRedirectReq.setChannel(edit_channel.getText().toString());
                    targetRedirectReq.setBand(spinner_band.getSelectedItem().toString());
                    targetRedirectReq.setMoveCell(spinner_movecell.getSelectedItem().toString());
                    customDialogListener.DialogCallBack(targetRedirectReq);
                    dismiss();
                    super.recordOnClick(v, "Redirect Event " + targetRedirectReq.toString());
                }
            }
        });
        ((Button) findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        LoadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SaveData();
    }

    private void SaveData() {
        String FemtoSn = SharePreferenceUtils.getInstance(context).getString("status_notif_sn" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        String FemtoTech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        SharedPreferences preferences = getContext().getSharedPreferences("target_redirect",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("band" + FemtoSn + FemtoTech, spinner_band.getSelectedItemPosition());
        editor.putString("channel" + FemtoSn + FemtoTech, edit_channel.getText().toString());
        editor.putInt("tech" + FemtoSn + FemtoTech, spinner_tech.getSelectedItemPosition());
        editor.putInt("movecell" + FemtoSn + FemtoTech, spinner_movecell.getSelectedItemPosition());
        editor.commit();
    }

    private void LoadData() {
        String FemtoSn = SharePreferenceUtils.getInstance(context).getString("status_notif_sn" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        String FemtoTech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        SharedPreferences sp = getContext().getSharedPreferences("target_redirect", MODE_PRIVATE);
        int iPosition = -1;
        int iMove = sp.getInt("movecell" + FemtoSn + FemtoTech, 0);
        if (iMove == 0) {
            bUpdateState = true;
            switch (FemtoTech) {
                case "2G":
                    iPosition = 0;
                    break;
                case "3G":
                    iPosition = 1;
                    break;
                case "4G":
                    iPosition = 2;
                    break;
                default:
                    break;
            }
        } else {
            bUpdateState = false;
            spinner_movecell.setSelection(iMove);
            iPosition = sp.getInt("tech" + FemtoSn + FemtoTech, 0);
        }
        spinner_tech.setSelection(iPosition);
        changeBand(iPosition);
        if (sp.getInt("tech" + FemtoSn + FemtoTech, 0) == iPosition) {
            spinner_band.setSelection(sp.getInt("band" + FemtoSn + FemtoTech, 0));
            edit_channel.setText(sp.getString("channel" + FemtoSn + FemtoTech, ""));
        }
    }

    private void changeBand(int iPosition) {
        switch (iPosition) {
            case 0:
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        context, R.array.band_2g, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_band.setAdapter(adapter);
                break;
            case 1:
                adapter = ArrayAdapter.createFromResource(
                        context, R.array.band_3g, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_band.setAdapter(adapter);
                break;
            case 2:
                adapter = ArrayAdapter.createFromResource(
                        context, R.array.band_4g, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_band.setAdapter(adapter);
                break;
            default:
                break;
        }
    }
}
