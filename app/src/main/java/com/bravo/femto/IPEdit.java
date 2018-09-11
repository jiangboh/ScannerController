package com.bravo.femto;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bravo.R;


public class IPEdit extends LinearLayout {
    private final String TAG = "IPEdit";
    private EditText firstIPEdit;
    private EditText secondIPEdit;
    private EditText thirdIPEdit;
    private EditText fourthIPEdit;

    private String firstIP = "";
    private String secondIP = "";
    private String thirdIP = "";
    private String fourthIP = "";

    public IPEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        View view = LayoutInflater.from(context).inflate(R.layout.ip_edit, this);
        firstIPEdit = (EditText) view.findViewById(R.id.firstIPfield);
        secondIPEdit = (EditText) view.findViewById(R.id.secondIPfield);
        thirdIPEdit = (EditText) view.findViewById(R.id.thirdIPfield);
        fourthIPEdit = (EditText) view.findViewById(R.id.fourthIPfield);

        setIPEditTextListener(context);
    }

    public void setIPEditTextListener(final Context context){
        //设置第一个字段的事件监听
        firstIPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(!TextUtils.isEmpty(s)){
                    if(s.toString().trim().contains(".")) {
                        firstIP = s.toString().replace(".","");
                        firstIPEdit.setText(firstIP);
                        secondIPEdit.setFocusable(true);
                        secondIPEdit.requestFocus();
                        return;
                    } else {
                        firstIP = s.toString().trim();
                        if (Integer.parseInt(firstIP) > 255) {
                            Toast.makeText(context, "Please input a valid ip address", Toast.LENGTH_LONG).show();
                            firstIPEdit.setText("");
                            return;
                        } else if (firstIP.length() == 3) {
                            secondIPEdit.setFocusable(true);
                            secondIPEdit.requestFocus();
                        }
                    }
                } else {
                    firstIP = s.toString().trim();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        //设置第二个IP字段的事件监听
        secondIPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(!TextUtils.isEmpty(s)){
                    if(s.toString().trim().contains(".")) {
                        secondIP = s.toString().replace(".","");
                        secondIPEdit.setText(secondIP);
                        thirdIPEdit.setFocusable(true);
                        thirdIPEdit.requestFocus();
                        return;
                    } else {
                        secondIP = s.toString().trim();
                        if (Integer.parseInt(secondIP) > 255) {
                            Toast.makeText(context, "Please input a valid ip address", Toast.LENGTH_LONG).show();
                            secondIPEdit.setText("");
                            return;
                        } else if (secondIP.length() == 3) {
                            thirdIPEdit.setFocusable(true);
                            thirdIPEdit.requestFocus();
                        }
                    }
                } else {
                    secondIP = s.toString().trim();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub;
            }
        });
        //设置第三个IP字段的事件监听
        thirdIPEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(!TextUtils.isEmpty(s)){
                    if(s.toString().trim().contains(".")) {
                        thirdIP = s.toString().replace(".","");
                        thirdIPEdit.setText(thirdIP);
                        fourthIPEdit.setFocusable(true);
                        fourthIPEdit.requestFocus();
                        return;
                    } else {
                        thirdIP = s.toString().trim();
                        if (Integer.parseInt(thirdIP) > 255) {
                            Toast.makeText(context, "Please input a valid ip address", Toast.LENGTH_LONG).show();
                            thirdIPEdit.setText("");
                            return;
                        } else if (thirdIP.length() == 3) {
                            fourthIPEdit.setFocusable(true);
                            fourthIPEdit.requestFocus();
                        }
                    }
                } else {
                    thirdIP = s.toString().trim();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
//                thirdIPEdit.removeTextChangedListener(this);
//                thirdIPEdit.setText(thirdIP);
//                thirdIPEdit.setSelection(thirdIP.length());
//                thirdIPEdit.addTextChangedListener(this);

            }
        });
        //设置第四个IP字段的事件监听
        fourthIPEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(!TextUtils.isEmpty(s)){
                    fourthIP = s.toString().trim();
                    if (Integer.parseInt(fourthIP) > 255) {
                        Toast.makeText(context, "Please input a valid address", Toast.LENGTH_LONG).show();
                        fourthIPEdit.setText("");
                    }
                } else {
                    fourthIP = s.toString().trim();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    public String getIPAddtrss() {
        if (TextUtils.isEmpty(firstIP) || TextUtils.isEmpty(secondIP)
                || TextUtils.isEmpty(thirdIP) || TextUtils.isEmpty(fourthIP)) {
            return "";
        }
        return firstIP + "." + secondIP + "." + thirdIP + "." + fourthIP;
    }

    public boolean getValid() {
        if (TextUtils.isEmpty(firstIP) && TextUtils.isEmpty(secondIP)
                && TextUtils.isEmpty(thirdIP) && TextUtils.isEmpty(fourthIP)) {
        } else if (TextUtils.isEmpty(firstIP) || TextUtils.isEmpty(secondIP)
                || TextUtils.isEmpty(thirdIP) || TextUtils.isEmpty(fourthIP)) {
           return false;
        }
        return true;
    }

    public void initIpAddress(String strIP) {
        if (!TextUtils.isEmpty(strIP)) {
            String[] strIPArray = strIP.split("\\.");
            for (int i = 0; i < strIPArray.length; i++) {
                switch (i) {
                    case 0:
                        this.firstIP = strIPArray[i];
                        break;
                    case 1:
                        this.secondIP = strIPArray[i];
                        break;
                    case 2:
                        this.thirdIP = strIPArray[i];
                        break;
                    case 3:
                        this.fourthIP = strIPArray[i];
                        break;
                    default:
                        break;
                }
            }
            firstIPEdit.setText(this.firstIP);
            secondIPEdit.setText(this.secondIP);
            thirdIPEdit.setText(this.thirdIP);
            fourthIPEdit.setText(this.fourthIP);
        }
    }
}