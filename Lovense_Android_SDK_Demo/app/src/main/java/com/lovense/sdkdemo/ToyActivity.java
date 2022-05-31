package com.lovense.sdkdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lovense.sdklibrary.Lovense;
import com.lovense.sdklibrary.LovenseToy;
import com.lovense.sdklibrary.callBack.LovenseError;
import com.lovense.sdklibrary.callBack.OnCallBackAidLightStatusListener;
import com.lovense.sdklibrary.callBack.OnCallBackBatteryListener;
import com.lovense.sdklibrary.callBack.OnCallBackDeviceTypListener;
import com.lovense.sdklibrary.callBack.OnCallBackLightStatusListener;
import com.lovense.sdklibrary.callBack.OnCallBackMoveListener;
import com.lovense.sdklibrary.callBack.OnCommandErrorListener;
import com.lovense.sdklibrary.callBack.OnCommandSuccessListener;
import com.lovense.sdklibrary.callBack.OnConnectListener;
import com.lovense.sdklibrary.callBack.OnSendCommandErrorListener;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Lovense on 2019/5/14
 * <p>
 * Copyright © 2019 Hytto. All rights reserved.
 */
public class ToyActivity extends AppCompatActivity implements View.OnClickListener {

    protected TextView tvType, tvAddress, tvVersion, tvBattery, tvMovement;

    private String toyId;
    private View back;
    private TextView stopConnect;
    private OnConnectListener onConnectListener;
    private EditText etProgram;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toy);
        initUI();
    }

    protected void initUI() {
        Intent intent = getIntent();

        back = findViewById(R.id.back);
        stopConnect = findViewById(R.id.stop_connect);

        tvType = findViewById(R.id.tv_type);
        tvAddress = findViewById(R.id.tv_address);
        tvVersion = findViewById(R.id.tv_version);
        tvBattery = findViewById(R.id.tv_battery);
        tvMovement = findViewById(R.id.tv_movement);
        toyId = intent.getStringExtra("toyId");

        if (!Lovense.getInstance(getApplication()).isConnected(toyId)) {
            onConnectListener = new OnConnectListener() {

                @Override
                public void onConnect(String toyId, String status) {
                    switch (status) {
                        case LovenseToy.STATE_CONNECTING:

                            break;
                        case LovenseToy.STATE_CONNECTED:
                            EventBus.getDefault().post(new ToyConnectEvent(1, toyId));
                            break;
                        case LovenseToy.STATE_FAILED:
                            EventBus.getDefault().post(new ToyConnectEvent(-1, toyId));
                            break;
                        case LovenseToy.SERVICE_DISCOVERED:
                            Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_GET_DEVICE_TYPE);
                            Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_GET_BATTERY);
                            stopConnect.setText("Disconnect");
                            break;
                    }

                }

                @Override
                public void onError(LovenseError error) {
                    try {
                        if (error.getCode().equals("29")) {
                            String msg = error.getMessage();
                            Toast.makeText(ToyActivity.this, msg, Toast.LENGTH_SHORT).show();
                            final AlertDialog.Builder normalDialog =
                                    new AlertDialog.Builder(ToyActivity.this);
                            normalDialog.setTitle("notice");
                            normalDialog.setMessage(msg);
                            normalDialog.setPositiveButton("back",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //...To-do
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                            normalDialog.setNegativeButton("CONNECT",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //...To-do
                                            Lovense.getInstance(getApplication()).connectToy(toyId);
                                            dialog.dismiss();
                                        }
                                    });
                            // 显示
                            normalDialog.show();
                            stopConnect.setText("connect");
                        } else {
                            String msg = error.getMessage();
                            Toast.makeText(ToyActivity.this, msg, Toast.LENGTH_SHORT).show();
                            final AlertDialog.Builder normalDialog =
                                    new AlertDialog.Builder(ToyActivity.this);
                            normalDialog.setTitle("notice");
                            normalDialog.setMessage(msg);
                            normalDialog.setPositiveButton("back",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //...To-do
                                            finish();
                                        }
                                    });
                            normalDialog.setNegativeButton("AGAIN",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //...To-do
                                            Lovense.getInstance(getApplication()).connectToy(toyId);
                                            dialog.dismiss();
                                        }
                                    });
                            // 显示
                            normalDialog.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            };
            Lovense.getInstance(getApplication()).connectToy(toyId, onConnectListener);
        } else {
            Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_GET_DEVICE_TYPE);
            Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_GET_BATTERY);
        }


        Lovense.getInstance(getApplication()).addListener(toyId, new OnSendCommandErrorListener() {
            @Override
            public void sendCommandError(String toyId, LovenseError error) {
                Toast.makeText(ToyActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Lovense.getInstance(getApplication()).addListener(toyId, new OnCallBackDeviceTypListener() {
            @Override
            public void deviceType(String toyId, LovenseToy lovenseToy) {
                tvType.setText("Device Info：" + lovenseToy.getType());
                tvAddress.setText("MAC Address：" + lovenseToy.getMacAddress());
                tvVersion.setText("Version：" + lovenseToy.getVersion());
            }
        });
        Lovense.getInstance(getApplication()).addListener(toyId, new OnCallBackBatteryListener() {
            @Override
            public void battery(String toyId, int battery) {
                tvBattery.setText("Battery：" + battery + "%");
            }
        });
        Lovense.getInstance(getApplication()).addListener(toyId, new OnCallBackLightStatusListener() {
            @Override
            public void lightStatus(String toyId, Integer status) {
                Toast.makeText(ToyActivity.this, status == 1 ? "Light on" : "Light off", Toast.LENGTH_SHORT).show();
            }
        });
        Lovense.getInstance(getApplication()).addListener(toyId, new OnCallBackAidLightStatusListener() {
            @Override
            public void aidLightStatus(String toyId, Integer status) {
                Toast.makeText(ToyActivity.this, status == 1 ? "AID Light on" : "AID Light off", Toast.LENGTH_SHORT).show();
            }
        });

        Lovense.getInstance(getApplication()).addListener(toyId, new OnCallBackMoveListener() {
            @Override
            public void moveWaggle(String level) {
                tvMovement.setText("Movement:" + level);
            }
        });

        Lovense.getInstance(getApplication()).addListener(toyId, new OnCommandErrorListener() {
            @Override
            public void commandError(String msg) {
                Toast.makeText(ToyActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        Lovense.getInstance(getApplication()).addListener(toyId, new OnCommandSuccessListener() {
            @Override
            public void commandSuccess(String msg) {
                Log.e("test", "commandSuccess: " + msg);
            }
        });


        if (Lovense.getInstance(getApplication()).isConnected(toyId)) {
            stopConnect.setText("Disconnect");
        } else {
            stopConnect.setText("connect");
        }
        stopConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Lovense.getInstance(getApplication()).isConnected(toyId)) {
                    Lovense.getInstance(getApplication()).disconnect(toyId);
                    EventBus.getDefault().post(new ToyConnectEvent(-1, toyId));
                    stopConnect.setText("connect");
                } else {
                    Lovense.getInstance(getApplication()).connectToy(toyId);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        SeekBar commVibrate = findViewById(R.id.comm_vibrate);
        commVibrate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_VIBRATE, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        findViewById(R.id.tv_flash).setOnClickListener(this);

        SeekBar presetLevel = findViewById(R.id.preset_level);
        presetLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_PRESET, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar noraRotate = findViewById(R.id.nora_rotate);
        noraRotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_ROTATE, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar noraRotateTrue = findViewById(R.id.nora_rotate_true);
        noraRotateTrue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_ROTATE_CLOCKWISE, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar noraRotateFalse = findViewById(R.id.nora_rotate_false);
        noraRotateFalse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_ROTATE_ANTI_CLOCKWISE, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        findViewById(R.id.tv_rotate_change).setOnClickListener(this);


        SeekBar edgeVibrate1 = findViewById(R.id.edge_vibrate1);
        edgeVibrate1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_VIBRATE1, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar edgeVibrate2 = findViewById(R.id.edge_vibrate2);
        edgeVibrate2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_VIBRATE2, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar maxAirIn = findViewById(R.id.max_air_in);
        maxAirIn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_AIR_IN, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar maxAirOut = findViewById(R.id.max_air_out);
        maxAirOut.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_AIR_OUT, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar maxAirLoop = findViewById(R.id.max_air_loop);
        maxAirLoop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_AIR_AUTO, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.tv_alight_off).setOnClickListener(this);
        findViewById(R.id.tv_alight_on).setOnClickListener(this);
        findViewById(R.id.tv_get_alight).setOnClickListener(this);

        findViewById(R.id.tv_light_off).setOnClickListener(this);
        findViewById(R.id.tv_light_on).setOnClickListener(this);


        findViewById(R.id.tv_start_move_waggle).setOnClickListener(this);
        findViewById(R.id.tv_stop_move_waggle).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_flash:
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_FLASH);
                break;
            case R.id.tv_rotate_change:
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_ROTATE_CHANGE);
                break;
            case R.id.tv_alight_off:
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_ALIGHT_OFF);
                break;
            case R.id.tv_alight_on:
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_ALIGHT_ON);
                break;
            case R.id.tv_get_alight:
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_GET_ALIGHT_STATUS);
                break;
            case R.id.tv_light_off:
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_LIGHT_OFF);
                break;
            case R.id.tv_light_on:
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_LIGHT_ON);
                break;
            case R.id.tv_start_move_waggle:
                tvMovement.setText("Movement:0");
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_START_MOVE);
                break;
            case R.id.tv_stop_move_waggle:
                tvMovement.setText("");
                Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_STOP_MOVE);
                break;
        }
    }

}
