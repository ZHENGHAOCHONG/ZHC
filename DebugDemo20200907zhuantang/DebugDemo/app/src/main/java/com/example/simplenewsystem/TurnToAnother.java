package com.example.simplenewsystem;
/**
 * author by Z
 * 20200909*/
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.lang.NullPointerException;

//目前改到这20201022 无法从手动到转塘跳转，有问题

public class TurnToAnother extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button ForwardAuto;  //正转自动按钮添加
    Button ForwardManual; //正转手动按钮添加
    Button ReverseAuto;   //反转自动按钮添加
    Button ReverseManual; //反转手动按钮添加
    Button JumpAuto;            //跳转到自动模式界面按钮
    Button HalfAuto;            //跳转到自动模式界面按钮
    CheckBox CheckBox;    //复选框

    TextView LeftDianJi;
    TextView RightDianJi;

//    SeekBar speedpwm;//滑动条
//    TextView speedtxt;//文本框

    //声明通信子线程
//    ClientThread clientThread;
    //声明主线程的Handler
//    Handler handler;

//    String cmdinit;

   byte [] cmd;                //28字节控制命令字节数组
//    String cmd;    20201030
    byte [] info;               //存放监测参数的28字节数组


    static boolean dangWei = false;				//判断标志，判断有没有选后退控制
    static int speedLeft = 0;					//把speedPaoPan静态化，使得函数内部也可以使用。speedPaoPan是权限选择的值
    static int speedRight = 0;				//把speedZhenDong静态化，使得函数内部也可以使用。speedZhenDong是权限选择的值



    byte x3;//半自动，x坐标的高位
    byte x4;//半自动，x坐标的低位
    byte y3;//半自动，y坐标的高位
    byte y4;//半自动，y坐标的低位

    private MapView mMapView1 =null;
    private BaiduMap mBaiduMap1;  //mBaiduMap绘制障碍点
    private MapView mMapView2 =null;
    private BaiduMap mBaiduMap2;  //mBaiduMap绘制障碍点

    static int Width;			//手机分辨率的宽度
    static int Height;			//手机分辨率的高度



    private Spinner spinner1;
    private Spinner spinner2;

    private List<String> data_list1;
    private List<String> data_list2;

    private ArrayAdapter<String> arr_adapter1;
    private ArrayAdapter<String> arr_adapter2;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this.getApplicationContext());
//		SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);


        setContentView(R.layout.turntoanother);
        //initViews();
        //WindowManager主要用来管理窗口的一些状态、属性、view增加、删除、更新、窗口顺序、消息收集和处理等。
        WindowManager wm = this.getWindowManager();//获取窗口管理器

        //设置RelativeLayout.LayoutParams参数
        RelativeLayout.LayoutParams pp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        //通过WindowManager中的Display获取屏幕大小
        pp.width  = wm.getDefaultDisplay().getWidth();
        pp.height = wm.getDefaultDisplay().getHeight();

        Width  = pp.width;
        Height = pp.height;


        init();

        //接收实时参数
        IntentFilter intentFilter = new IntentFilter();//创建IntentFilter
        intentFilter.addAction("action.Info");//指定BroadcastReceiver监听的Action
        //注册广播，接收System广播的实时参数
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
        //注册应用内广播接收器



        //抛盘电机
        //多种不同的模式
        data_list1 = new ArrayList<String>();//data_list是一个数组列表
        data_list1.add("0");
        data_list1.add("30");
        data_list1.add("60");
        data_list1.add("90");
        data_list1.add("100");

        //适配器
        arr_adapter1= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list1);
        //设置样式
        arr_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner1.setAdapter(arr_adapter1);
        //为Spinner设置选中事件监听器
        spinner1.setOnItemSelectedListener(TurnToAnother.this);


        //振动电机
        //多种不同的模式
        data_list2 = new ArrayList<String>();//data_list是一个数组列表
        data_list2.add("0");
        data_list2.add("30");
        data_list2.add("60");
        data_list2.add("90");
        data_list2.add("100");

        //适配器
        arr_adapter2= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list2);
        //设置样式
        arr_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner2.setAdapter(arr_adapter2);
        //为Spinner设置选中事件监听器
        spinner2.setOnItemSelectedListener(TurnToAnother.this);

/*
      speedpwm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
          int prostable;
          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              //当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
              //paotxt.setText("明轮电机转速:"+Integer.toString(progress));
              prostable=progress;
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {//滑动条开始接触显示
              Log.e("------------", "开始滑动！");
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {//滑动条停止接触显示
              cmd[2]= 0x02;

             // String tmp = Integer.toString(prostable);
              Intent intent = new Intent();
            //  intent.putExtra("cmd",("#a,1,2,m,"+tmp+",60,30,"));
              intent.putExtra("cmd",cmd);
              intent.setAction("action.move");
              sendBroadcast(intent);                                   //发送广播
              speedtxt.setText("明轮电机转速:"+Integer.toString(prostable)+"%");
          }
      });

*/

        mMapView1 = findViewById(R.id.map1);
        mMapView2 =findViewById(R.id.map2);

        mBaiduMap1 = mMapView1.getMap();
        mBaiduMap2 =mMapView2.getMap();
        /*mBaiduMap1 = mMapView.getMap();*/
        //普通地图 ,mBaiduMap是地图控制器对象
        mBaiduMap1.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mBaiduMap2.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        /*mBaiduMap1.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
         */
        //x,y是经纬度，f为缩放等级（4-20之间）    31.62320018, 119.64296511 金坛经纬度
        MapStatusUpdate update1 = MapStatusUpdateFactory.newLatLngZoom(new com.baidu.mapapi.model.LatLng(32.201699,119.528558),20);
        mBaiduMap1.animateMapStatus(update1);

        MapStatusUpdate update2 = MapStatusUpdateFactory.newLatLngZoom(new com.baidu.mapapi.model.LatLng(32.201699,119.528558),20);
        mBaiduMap2.animateMapStatus(update2);

        //水质监测仪
        //119.6313838   31.6191588
        //119.6316115   31.6189377

/*
		赤道周长（米）		度数（度）
		40076000			360
		111322.2222			1				100000m
		11132.22222			0.1				10000m
		1113.222222			0.01			1000m
		111.3222222			0.001			100m
		11.13222222			0.0001			10m
		1.113222222			0.00001			1m
		0.111322222			0.000001		1dm
		0.011132222			0.0000001		1cm
*/
        //这边只写了一个mBaiduMap1，如果需要mBaiduMap2的话再增加即可
        Bitmap bitmap = zoomImg(BitmapFactory.decodeResource(getResources(), R.drawable.forbid), 40, 40);
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        com.baidu.mapapi.model.LatLng point = new com.baidu.mapapi.model.LatLng(31.62320018, 119.64296511);//最东面的水质检测仪

        final OverlayOptions option = new MarkerOptions().position(point).icon(descriptor);

        com.baidu.mapapi.model.LatLng point1 = new LatLng(31.62340018, 119.64268211);//中间的水质检测仪

        final OverlayOptions option1 = new MarkerOptions().position(point1).icon(descriptor);

        mBaiduMap1.addOverlay(option);
        mBaiduMap1.addOverlay(option1);


        //底图标注
        mBaiduMap1.showMapPoi(false);//隐藏底图标注，这里隐藏的是地名和河流名
        /*mBaiduMap1.showMapPoi(false);//隐藏底图标注，这里隐藏的是地名和河流名*/



//CheckBox:复选框
        CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dangWei = true;//判断标志置为true，开启后退控制


                    cmd[3]  = 0x0F;                //控制命令字节数组第4个字节命令0x0F，控制手动停止
                    cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位
                    Intent intent = new Intent();
                    //将控制命令cmd的值添加到intent
                    intent.putExtra("cmd",cmd);
                    //设置intent的action值是action.cmd
                    intent.setAction("action.move");
                    sendBroadcast(intent);//开启广播

                    //UIUtils.showToast( Manual.this , "打开后退，进入后退模式:" +dangWei);
                }
                else{
                    dangWei = false;//判断标志置为false，关闭倒挡控制


                    cmd[3]  = 0x0F;                //控制命令字节数组第4个字节命令0x0F，控制手动停止
                    cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位
                    Intent intent = new Intent();
                    //将控制命令cmd的值添加到intent
                    intent.putExtra("cmd",cmd);
                    //设置intent的action值是action.cmd
                    intent.setAction("action.move");
                    sendBroadcast(intent);//开启广播

                    //UIUtils.showToast( Manual.this , "关闭陀螺仪，退出手动模式:" +dangWei);
                }
            }

        });





        ForwardAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"forward auto...",Toast.LENGTH_SHORT).show();
                UIUtils.showToast(TurnToAnother.this,"forward auto...");
                Log.e("收到", "咦，竟然收到了");

               //20201029new
//                String string ="5;";
//                StringAppend(cmd,string);
//                init();

//这里其实只要给左右明轮一个相同的占空比即可，可是暂时不知道cmd里面哪一位是左右明轮的值？？
//                cmd[1] = 0x01;
                cmd[1] = 0x02;
                cmd[2] = 0x00;
//                cmd[3] = 0x06;
                cmd[3] = 0x01;
                //这里把自动巡航的两位拿过来判断不知道有没有用？？
                cmd[8]=0x30;
                cmd[9]=0x34;
//              cmd[4] =(byte)(java.lang.Byte.decode("0x"+Integer.toHexString(x))+0x64); 左右明轮占空比还是要输入的，给定一个值就可以
//              cmd[5] = (byte)(java.lang.Byte.decode("0x"+Integer.toHexString(y))+0x64);

//                cmd[8] =0x00;
//                cmd[9] =0x00;
//                cmd[3]  = 0x0F;                //控制命令字节数组第4个字节命令0x0F，控制手动停止
//                cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位  20201029注销
                Intent intent = new Intent();
                //将控制命令cmd的值添加到intent
                intent.putExtra("cmd",cmd);
                //设置intent的action值是action.cmd
                intent.setAction("action.move");
                sendBroadcast(intent);//开启广播

                spinner1.setSelection(0,true);
                spinner2.setSelection(0,true);

            }
        });
//向前手动需要根据按压的时间，按下时设置左右占空比为一个固定值,设置一个长按事件
        ForwardManual.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(),"forward manual...",Toast.LENGTH_SHORT).show();

                cmd[2]=0x02;//这是旧版的，20190701 //控制命令字节数组第3个字节命令 0x02，控制手动前进 20201029注销
                //next question是把左右明轮占空比那个改成pwm滑动条的形式，20201022要改
                cmd[8]=0x30;
                cmd[9]=0x34;

                Intent intent = new Intent();
                //将控制命令cmd的值添加到intent
                intent.putExtra("cmd",cmd);
                //设置intent的action值是action.cmd
                intent.setAction("action.move");
                sendBroadcast(intent);//开启广播
                //这些只是暂时屏蔽了20201022，可以解开
                spinner1.setSelection(0,true);
                spinner2.setSelection(0,true);

                return false;
            }
        });
     /*     ForwardManual.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"forward manual...",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                //将控制命令cmd的值添加到intent
                intent.putExtra("cmd",cmd);
                //设置intent的action值是action.cmd
                intent.setAction("action.move");
                sendBroadcast(intent);//开启广播

                spinner1.setSelection(0,true);
                spinner2.setSelection(0,true);

            }
        });*/
        //反转自动，最好是先把倒档挂好，再自动运行/或者直接使用后退的cmd指令就可以了，找旧版的倒档cmd在哪20201019
        ReverseAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //20201029new
//                String string1 ="6";
//                StringAppend(cmd,string1);

                Intent intent = new Intent();
                //将控制命令cmd的值添加到intent
                intent.putExtra("cmd",cmd);
                //设置intent的action值是action.cmd
                intent.setAction("action.move");
                sendBroadcast(intent);//开启广播


                spinner1.setSelection(0,true);
                spinner2.setSelection(0,true);
//                 init(); //20201029new

            }
        });

        ReverseManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmd[2]=0x03;              //这是旧版的20190701 //控制命令字节数组第3个字节命令 0x03，控制手动后退

                cmd[8]=0x30;
                cmd[9]=0x34;
                Intent intent = new Intent();
                //将控制命令cmd的值添加到intent
                intent.putExtra("cmd",cmd);
                //设置intent的action值是action.cmd
                intent.setAction("action.move");
                sendBroadcast(intent);//开启广播

                spinner1.setSelection(0,true);
                spinner2.setSelection(0,true);

            }
        });

        //跳转到自动模式界面按钮
        JumpAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TurnToAnother.this,Auto.class);
                startActivity(intent);//开启广播
            }
        });



        //跳转到半自动模式界面按钮
        HalfAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TurnToAnother.this,HalfAuto.class);
                startActivity(intent);//开启广播
            }
        });

        //设置marker图标
        final Bitmap bitmap2 =zoomImg(BitmapFactory.decodeResource(getResources(),R.drawable.icon),40,40);
        final BitmapDescriptor descriptor2 =BitmapDescriptorFactory.fromBitmap(bitmap2);
        BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
            /**
             * 地图单击事件回调函数
             *
             * @param latLng 点击的地理坐标
             */
            @Override
            public void onMapClick(LatLng latLng) {
                //获取经纬度，例子：currentX:31.623101839488207     currentY:119.64263390372409
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;

                Log.e("T半自动--经纬度", "currentX:"+latitude+"currentY:"+longitude);//输出经纬度值

                //将经纬度转换为平面坐标，例子：currentX:3503132.978473543    currentY:2.075075058647018E7

                Point pa = BLtoxy(longitude,latitude);


                double x = pa.getX();		//3503092.3016042993
                double y = pa.getY();		//2.07507268389166E7
                Log.e("T半自动--高斯克吕哥公式，平面坐标", "currentX:"+x+"currentY:"+y);


                //注意，x,y进行转换，例子：currentX:207507268    currentY:35030923
                long x1 = Math.round(y*10);//横坐标，单位dm
                long y1 = Math.round(x*10);//纵坐标，单位dm

                Log.e("T半自动--换算成分米", "currentX:"+x1+"currentY:"+y1);


                //坐标转换，例子，currentX:9800      currentY:9618
                int x2 = (int)(x1-207497468);
                int y2 = (int)(y1-35021305);

                Log.e("T半自动--相对坐标", "currentX:"+x2+"currentY:"+y2);


                //2648		2592
                x3 = (byte)(((java.lang.Short.decode("0x"+Integer.toHexString(x2)))&0xff00)>>8);	//38
                x4 = (byte)((java.lang.Short.decode("0x"+Integer.toHexString(x2)))&0xff);			//72

                y3 = (byte)(((java.lang.Short.decode("0x"+Integer.toHexString(y2)))&0xff00)>>8);	//37
                y4 = (byte)((java.lang.Short.decode("0x"+Integer.toHexString(y2)))&0xff);			//-110

                Log.e("T半自动--相对坐标--十六进制", "currentX:"+"x3="+x3+"x4="+x4+"currentY:"+"y3="+y3+"y4="+y4);

                AlertDialog.Builder dialog = new AlertDialog.Builder(TurnToAnother.this);
                dialog.setTitle("提示");
                dialog.setMessage("确定要以该点作为终点吗？");
                dialog.setCancelable(false);

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UIUtils.showToast(TurnToAnother.this,"You clicked OK");
//                        20201029注销 下面的全部都暂时注销
                        cmd[3] = 0x02;
                        cmd[4] = x3;
                        cmd[5] = x4;
                        cmd[6] = y3;
                        cmd[7] = y4;
                        cmd[8] = (byte)(java.lang.Byte.decode(("0x"+Integer.toHexString(speedLeft)))+0x64);//抛盘电机
                        cmd[9] = (byte)(java.lang.Byte.decode(("0x"+Integer.toHexString(speedRight)))+0x64);//振动电机
//
                        cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位

                        Intent intent = new Intent();
                        //将控制命令cmd的值添加到intent
                        intent.putExtra("cmd",cmd);
                        //设置intent的action值是action.cmd
                        intent.setAction("action.move");
                        sendBroadcast(intent);//开启广播
                    }
                });

                dialog.setNegativeButton("换个坐标", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UIUtils.showToast(TurnToAnother.this,"You clicked No");

                    }
                });
                dialog.show();

                mBaiduMap1.clear();
//                mBaiduMap2.clear();

                // 定义Maker坐标点
                LatLng latLng1 = new LatLng(latitude, longitude);
                // 构建MarkerOption，用于在地图上添加Marker
                MarkerOptions options = new MarkerOptions().position(latLng1).icon(descriptor2);
                // 在地图上添加Marker，并显示
                mBaiduMap1.addOverlay(options);
                mBaiduMap1.addOverlay(option);//重新绘制水质检测仪障碍点
                mBaiduMap1.addOverlay(option1);//重新绘制水质检测仪障碍点

            }
            /**
             * 地图内 Poi 单击事件回调函数
             *
             * @param mapPoi 点击的 poi 信息
             */
            @Override
            public void onMapPoiClick(MapPoi mapPoi) {


            }
        };

        //设置地图单击事件监听
        mBaiduMap1.setOnMapClickListener(listener);
        //设置地图单击事件监听
//        mBaiduMap2.setOnMapClickListener(listener1);


    }
    //Android中所有与观察者模式有关的设计中，一旦涉及到register，必定在相应的时机需要unregister
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView1.onDestroy();

    }

//    //在每个的最外面加一个StringAppend函数，然后直接调用即可。20201029  不能用函数，会导致长度冗余，还是用case吧
//    public static  StringBuffer StringAppend(String cmd, String string) {
//        StringBuffer sb = new StringBuffer(cmd);
//        return sb.append(string);
//    }

    //广播接收器
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//获取intent中的action值
            if(action!=null&&action.equals("action.Info")){
                info = intent.getExtras().getByteArray("info");
                if(info!=null){

                }
            }
        }
    };



    private void init() {
        ForwardAuto =findViewById(R.id.ForwardAuto);
        ForwardManual = findViewById(R.id.ForwardManual);
        ReverseManual=findViewById(R.id.ReverseManual);
        ReverseAuto = findViewById(R.id.ReverseAuto);

        JumpAuto = findViewById(R.id.JumpAuto);
        HalfAuto = findViewById(R.id.HalfAuto);
        CheckBox = findViewById(R.id.CheckBox);

        LeftDianJi =(TextView) findViewById(R.id.leftdianji);
        RightDianJi =(TextView) findViewById(R.id.rightdianji);


        spinner1 =findViewById(R.id.spinner1);
        spinner2 =findViewById(R.id.spinner2);
//        speedpwm =findViewById(R.id.seekBar);
//        speedtxt =findViewById(R.id.speedpwm);



//        cmd = new String("AAA0001:1");  20201030注销
        cmd = new byte[28];
//        cmd = new byte[1032];
//        cmd[0] = 0x07;                  //命令字节数组第1个字节命令 0x07    20201029注销
//        cmd[1] = 0x02;                  //命令字节数组第2个字节命令 0x02		之前版本的手动控制协议   20201029注销
//        cmd[1] = 0x01;					//命令字节数组第2个字节命令 0x01
//        cmd[2] = 0x00;					//命令字节数组第3个字节命令 0x00

//        info = new byte[28];
        info = new byte[1032];
    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        //获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        //计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        //取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        //得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    public static Point BLtoxy(double lng,double lat) {
        Point pointxy=null;

        double x;
        double y;
        int  N   = (int)((lng+6)/6);   //带号

        lng = lng*3.1415926/180;
        lat = lat*3.1415926/180;

        double L = lng - (6*N-3)*3.1415926/180 ;

        double a = 6378137.0;
        double b = 6356752.3142;

        double e = Math.sqrt((Math.pow(a, 2)-Math.pow(b, 2))/Math.pow(a, 2));
        double ep= Math.sqrt((Math.pow(a, 2)-Math.pow(b, 2))/Math.pow(b, 2));

        double k0 = 1+3.0*Math.pow(e, 2)/4+45.0*Math.pow(e, 4)/64+175.0*Math.pow(e, 6)/256+11025.0*Math.pow(e, 8)/16384;

        double k2 = 1.0*(3.0*Math.pow(e, 2)/4+15.0*Math.pow(e, 4)/16+525.0*Math.pow(e, 6)/512+2205.0*Math.pow(e, 8)/2048)/2;

        double k4 = 1.0*(15.0*Math.pow(e, 4)/64+105.0*Math.pow(e, 6)/256+2205.0*Math.pow(e, 8)/4096)/4;

        double k6 = 1.0*(35.0*Math.pow(e, 6)/512+315.0*Math.pow(e, 8)/2048)/6;

        double k8 = 315.0*Math.pow(e, 8)/131072;

        //System.out.println(N);
        x = a*(1-Math.pow(e, 2))*(
                (k0*lat)-k2*Math.sin(2*lat)+k4*Math.sin(4*lat)-k6*Math.sin(6*lat)+k8*Math.sin(8*lat)
        )
                + a/(4*Math.sqrt(1-e*e*Math.pow(Math.sin(lat),2)))*Math.sin(2*lat)*L*L*
                (int)(1+1.0/12*L*L*Math.pow(Math.cos(lat), 2)*(5-Math.pow(Math.tan(lat), 2)+9*ep*ep*Math.pow(Math.cos(lat), 2)
                        +4*Math.pow(ep, 4)*Math.pow(Math.cos(lat), 4))
                        +1.0*Math.pow(L, 4)*Math.pow(Math.cos(lat), 4)*(61-58*Math.pow(Math.tan(lat), 2)+Math.pow(Math.tan(lat), 4))/360
                );

        y= a*Math.cos(lat)*L*(int)(1+1.0/6*L*L*Math.pow(Math.cos(lat), 2)*(1-Math.pow(Math.tan(lat), 2)+ep*ep*Math.pow(Math.cos(lat), 2))
                +1.0*Math.pow(L, 4)*Math.pow(Math.cos(lat), 4)*(5-18*Math.pow(Math.tan(lat), 2)+Math.pow(Math.tan(lat), 4)+14*ep*ep*Math.pow(Math.cos(lat), 2)
                -58*ep*ep*Math.pow(Math.sin(lat), 2))/120)/Math.sqrt(1-e*e*Math.pow(Math.sin(lat),2))+500000+1000000*N;

        if(y<0)y=(-y);

        pointxy = new Point(x,y);

        return pointxy;
        //119.516471,32.203445

    }

    @Override
    protected  void onResume(){
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView1.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView1.onPause();
    }


    //电机占空比
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        int number = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
        TextView tv;
        TextView tv1;
        switch (adapterView.getId()){
            case R.id.spinner1:
                speedLeft = number;			//left电机占空比
                tv1 = (TextView)view;
                tv1.setTextColor(getResources().getColor(R.color.white));//因为背景是黑色的，所以设置字体为白色的
                UIUtils.showToast(TurnToAnother.this,"当前左明轮电机占空比：" + speedLeft);
                break;

            case R.id.spinner2:
                speedRight = number;			//right电机占空比
                tv = (TextView)view;
                tv.setTextColor(getResources().getColor(R.color.white));//因为背景是黑色的，所以设置字体为白色的
                UIUtils.showToast(TurnToAnother.this,"当前右明轮电机占空比：" + speedRight);
                break;
            default:
                break;

        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}

