package com.example.simplenewsystem;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


//当此Activity实例化时，会动态将mBroadcastReceiver注册到系统中。当此Activity销毁时，动态注册的MyBroadcastReceiver将不再接收到相应的广播。

//广播发送及广播类型
//定义广播的定义过程，实际就是相应广播”意图“的定义过程，然后通过广播发送者将此”意图“发送出去。被相应的BroadcastReceiver接收后将会回调onReceive()函数。


//类继承了AppCompatActivity会显示标题栏。如果直接继承的是Activity不会出现标题栏
public class JianCe extends AppCompatActivity {
    TextView jingdu;        //经度
    TextView weidu;         //纬度
    TextView etric;         //剩余电量
    TextView erl;           //剩余饵料
    TextView paof;          //抛幅
    TextView speed;         //船速
    TextView flow;         //船速

    byte []  info;          //存放监测参数的28字节数组
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zidongjiance);
        init();

        //接收实时参数
        IntentFilter intentFilter = new IntentFilter();//创建IntentFilter
        intentFilter.addAction("action.Info");//指定BroadcastReceiver监听的Action
        //注册广播，接收System广播的实时参数
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
        //注册应用内广播接收器
    }

    //Android中所有与观察者模式有关的设计中，一旦涉及到register，必定在相应的时机需要unregister
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }
    //广播接收器
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//获取intent中的action值
            if(action!=null&&action.equals("action.Info")){
                info = intent.getExtras().getByteArray("info");
                if(info!=null){
                    String lng = String.valueOf((float)(getShort(info,5)-10000)/10);//5,6两位的数据转换为平面横坐标
                    String lat = String.valueOf((float)(-getShort(info,7)+10000)/10);//7,8两位的数据转换为平面纵坐标
					String Speed = String.valueOf(((float) (getShort(info,9))-10000)/1000);//第9,10两位的数据转换为船速
					String Etric = String.valueOf((float) (getShort(info,11))/10);//第11,12两位的数据转换为剩余电量
                    String Erl = String.valueOf((float) ((getShort(info,13))/100));//第13,14两位的数据转换为剩余饵料，得到的数据是10g，需要除以100转换为kg。
					//String Paof = String.valueOf((float) (getShort(info,15)));//第15,16两位的数据转换为抛幅
					String Flow = String.valueOf((float) (getShort(info,17)));//第17,18两位的数据转换为流量


                    jingdu.setText(lng);
                    weidu.setText(lat);
                    etric.setText(Etric);
                    speed.setText(Speed);
                    //paof.setText(Paof);
                    erl.setText(Erl);
                    flow.setText(Flow);

					Log.i("msg", "info[]={"+jingdu+","+weidu+","+etric+","+speed+","+paof+","+erl+","+flow+"}");

                }
            }
        }
    };


    //将两字节16进制数转化为十进制数
    public static short getShort(byte[] arr, int index) {
        return (short) (0xff00 & arr[index] << 8 | (0xff & arr[index + 1]));
    }

    private void init(){
        jingdu = findViewById(R.id.jingdu);
        weidu  = findViewById(R.id.weidu);
        etric  = findViewById(R.id.textView4);
        erl    = findViewById(R.id.textView6);
        paof   = findViewById(R.id.textView8);
        speed  = findViewById(R.id.textspeed);
        flow   = findViewById(R.id.textliuliang);

//        info = new byte[28];
        info = new byte[1032];
    }
}
