package com.bravo.test;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bravo.R;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.fragments.SerializableHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * A simple {@link Fragment} subclass.
 */
public class Terminal_Fragmen extends RevealAnimationBaseFragment {
    private final static String TAG = "Terminal_Fragment";
    private View view;
    private List<String> list = new ArrayList<String>();
    private Spinner spinner_terminal;
    private ArrayAdapter<String> adapter;
    private Button BtnConnect;
    private EditText terminal_log;
    private Timer timer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_terminal);
    }

    @Override
    public void initView() {
//        list.add("Telnet");
//        list.add("SSH");
//        spinner_terminal = (Spinner) view.findViewById(R.id.spinner_terminal);
//        BtnConnect = (Button) view.findViewById(R.id.terminal_connect);
//        terminal_log = (EditText) view.findViewById(R.id.terminal_log);
//
//        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner_terminal.setAdapter(adapter);
//        spinner_terminal.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        spinner_terminal.setOnTouchListener(new Spinner.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//
//        spinner_terminal.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//
//            }
//        });
//
//        BtnConnect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (timer != null) {
//                    timer.cancel();
//                    timer = null;
//                }
//            }
//        });
//
//        terminal_log.setKeyListener(null);
//        terminal_log.setSelection(terminal_log.getText().length());
//
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.sendMessage(handler.obtainMessage());
//            }
//        }, 10, 100);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }
    static int i = 0;
    private android.os.Handler handler = new SerializableHandler() {
        @Override
        //定时更新图表
        public void handleMessage(Message msg) {
            terminal_log.getText().insert(terminal_log.getSelectionStart(), i+"");
            i++;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
