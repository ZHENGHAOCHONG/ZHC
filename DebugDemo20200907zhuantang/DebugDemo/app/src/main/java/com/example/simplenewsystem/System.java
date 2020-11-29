package com.example.simplenewsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


//安卓第七天笔记--网络编程一
//https://blog.csdn.net/ko0491/article/details/50816330
//Activity



//类继承了AppCompatActivity会显示标题栏。如果直接继承的是Activity不会出现标题栏
public class System extends AppCompatActivity {

  //变量声明
  Button connnect;            //连接按钮
  ImageButton auto;           //自动图形按钮
  ImageButton manual;         //手动图形按钮
  ImageButton halfauto;         //手动图形按钮

  EditText ipaddress;
  ClientThread clientThread;  //客户端线程
  //定义向UI线程发送消息的Handler对象
  Handler handler;            //Handler主要用于异步消息的处理，更新UI



  TextView warnning;
  ImageView light;
  short [][] point;           //用于存储规划路径的平面坐标
  int num;                    //用于存储已经接受的目标节点数
  byte[] response;            //用于发送给服务器的响应
  byte [] info;				  //存放监测参数的28字节数组
  byte [] info1;			  //存放监测参数的28字节数组
 byte []GNGGAData;
//String cmdinit;

  static ArrayList<Short> pxpy = new ArrayList<Short>();


  TextView DTU;
  TextView Phone;


//  static int times = 0;	//用于Auto中


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.zhukong);
    initViews();
    /**下面这句话有问题，添加了之后就无法从初始界面跳转到System里，为什么？？？？20201022*/
    // GNGGAData[90] = Byte.parseByte("$GNGGA,033045.40,3211.6409468,N,11931.0349258,E,1,14,0.797,16.3392,M,6.2734,M,*79");
//    cmdinit = "AAA0001:00,00.0,00.0,00.0,00.0,00.0,00.0/n";

    //定义一个Handler处理来自子线程的消息
    handler = new Handler(){
      public void handleMessage(Message msg){
        // 如果消息来自于子线程
        if (msg.what == 0x123)      //与RevThread相联系
        {
          //获取传递过来的Message中的数据集合，从数据集合中获取key为＂info＂的值，这个值是一个字节数组
          byte []  info = msg.getData().getByteArray("info");
          if(info!=null&&info[0]=='A'&&info[1]=='A'&&info[2]=='A'){
            Intent intent = new Intent();
            //将info的值添加到intent
            intent.putExtra("info", info);
            //为Intent设置Action属性
            intent.setAction("action.Info");
            //发送广播
            sendBroadcast(intent);
          }
        }
        if(msg.what == 0x789){
          byte []  info = msg.getData().getByteArray("info");
          if(info!=null&&info[0]==0x07){ //要改
            //判断字节数组倒第二和倒第三个数是否为0(最后一位为校验位)，为0 则地图信息都发送完成，
            //船也接收到所有数据；如果不为0 ，则继续向船发送剩下的点坐标
            if((info[26]+info[25])==0x00){ //要改
              //SendPathToPathShow();
              Log.i("msg", "接收完成!");
              Exchange(info);//把十六进制的坐标转换为十进制的坐标
              SendPathToPathShow();
            }else
            {
              Exchange(info);//把十六进制的坐标转换为十进制的坐标
              //bundle数据包
              Bundle data = new Bundle();
              //将表示指定字节数组的字符串与此首选项节点中指定的键相关联。putByteArray(String key, byte[] value)

              //public void putByteArray (String key, byte[] value)
              //字节数组插入到这个包的映射值，给定键的替换任何现有的值。无论是键或值可能为空
              data.putByteArray("response",response);
              //创建消息
              Message msg2 = new Message();
              //消息标识
              msg2.what = 0x789;
              msg2.setData(data);
              //向clientThread线程中的revHandler发送消息
              clientThread.revHandler.sendMessage(msg2);

            }
          }
        }
      }
    };



    //接收Auto.activity获取边界顶点的命令
    IntentFilter intentFilter = new IntentFilter();//创建IntentFilter
    intentFilter.addAction("action.cmd");//指定BroadcastReceiver监听的Action
    //注册BroadcastReceiver
    registerReceiver(mRefreshBroadcastReceiver, intentFilter);      //发送命令


    //接收手动控制的命令
    IntentFilter intentFilter1 = new IntentFilter();//创建IntentFilter
    intentFilter1.addAction("action.move");//指定BroadcastReceiver监听的Action
    //注册BroadcastReceiver
    registerReceiver(mRefreshBroadcastReceiver, intentFilter1);      //发送命令


    //广播接收器,接收规划好的路径字符串
    IntentFilter intentFilter2 = new IntentFilter();//创建IntentFilter
    intentFilter2.addAction("action.path");//指定BroadcastReceiver监听的Action
    //注册BroadcastReceiver
    registerReceiver(mRefreshBroadcastReceiver, intentFilter2);

    //接收实时参数
	IntentFilter intentFilter3 = new IntentFilter();//创建IntentFilter
	intentFilter3.addAction("action.Info");//指定BroadcastReceiver监听的Action
	//注册广播，接收System广播的实时参数
	registerReceiver(mRefreshBroadcastReceiver, intentFilter3);
	//注册应用内广播接收器



	  //为每一个选项设置监听

    connnect.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // 新建线程类
        clientThread = new ClientThread(handler);
        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据
        new Thread(clientThread).start();
        //setImageResource更换图片，有可能对一个Activity的启动造成延迟。可以用setImageDrawable和setImageBitmap来代替。
        light.setImageResource(R.drawable.runlight);
        Log.i("msg", "connect to server " );
        //Toast.makeText(System.this, "正在尝试连接服务器", Toast.LENGTH_SHORT).show();//弃用
		UIUtils.showToast( System.this , "正在尝试连接服务器" );
        //将"注：未登陆服务器时，请勿进行其他操作！"转换为"提示：请选择自动模式或者遥控模式。"
        warnning.setText("提示：请选择自动模式或者遥控模式。");

      }
    });

    auto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //使用Intent从主控页面跳转到自动控制页面
        Intent intent = new Intent(System.this,Auto.class);
        //启动跳转的页面
        startActivity(intent);
        Log.i("msg", "select auto" );
      }
    });

    manual.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //使用Intent从主控页面跳转到手动控制页面
        Intent intent = new Intent(System.this,Manual.class);
        //启动跳转的页面
        startActivity(intent);
        Log.i("msg", "select manual" );
      }
    });

    halfauto.setOnClickListener(new View.OnClickListener() {
		  @Override
		  public void onClick(View v) {
		//使用Intent从主控页面跳转到手动控制页面
		Intent intent = new Intent(System.this,HalfAuto.class);
		//启动跳转的页面
		startActivity(intent);
		Log.i("msg", "select HalfAuto" );
	 }
	  });

  }

  //0x07 0x05    01 0xXH 0xXL 0xYH 0xYL  02 0xXH 0xXL 0xYH 0xYL   03 0xXH 0xXL 0xYH 0xYL...

  //把十六进制的坐标转换为十进制的坐标
  public void Exchange(byte[] p ){
    //0x07 0x05
    //_(点的序号) _ _(存放平面坐标x的值) _ _(存放平面坐标y的值)       2  3  4  5  6      5 个字节存放一个点的平面坐标
    //_(点的序号) _ _(存放平面坐标x的值) _ _(存放平面坐标y的值)       7  8  9 10 11
    for(int i=3;i<28;){ //要改
//    for(int i=3;i<1032;){
      //将两个十六进制数转换为一个十进制数，存放平面坐标x的值
      point[num][0]=getShort(p,i);
      //将两个十六进制数转换为一个十进制数，存放平面坐标y的值
      point[num++][1]=getShort(p,i+2);
      Log.e("Exchange message point:", "x:"+String.valueOf(point[num-1][0])+",y:"+String.valueOf(point[num-1][1]));
      i+=5;
    }
    Log.i("msg", " Exchange message Hex:" +bytesToHexString(p) );
    Log.i("msg", " Exchange message Hex:" +String.valueOf(num) );


  }

  //将两字节16进制数转化为十进制数
  public static short getShort(byte[] arr, int index) {
    return (short) (0xff00 & arr[index] << 8 | (0xff & arr[index + 1]));
  }


  //博客    https://blog.csdn.net/sunny05296/article/details/80691303
  //博客    https://www.cnblogs.com/yuyutianxia/p/7102425.html
  //byte数组转换成十六进制输出
  public static final String bytesToHexString(byte[] bArray) {
    //新建名为sb的StringBuffer对象，长度为字节数组bArray的长度
    StringBuffer sb = new StringBuffer(bArray.length);
    String sTemp;
    for (int i = 0; i < bArray.length; i++) {
      //Integer.toHexString的参数是int，如果不进行&0xff，那么当一个byte会转换成int时，对于负数，会作位扩展
      sTemp = Integer.toHexString(0xFF & bArray[i]);
      if (sTemp.length() < 2)
        sb.append(0);
      //toUpperCase() 方法用于把字符串转换为大写
      sb.append(sTemp.toUpperCase());
    }
    return sb.toString();
  }

  private void SendPathToPathShow(){
    Intent intent = new Intent();
    //创建一个二维数组
    TwoBean data = new TwoBean();
    //TwoBean类新建的对象(名为data)中的二维数组名为data
    data.data=this.point.clone();   //data.data     TwoBean类的对象名为data  里的二维数组名为data
    intent.putExtra("path",data );
    intent.setAction("action.Path");
    //发送广播
    sendBroadcast(intent);
  }

  private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      	//获取intent中的action值
      	String action = intent.getAction();
//		Log.e("手动绘图  System","action是否为空？？"+action+" ");	//到了，打印出来的是action.cmd  07 04 0x??
		if(action.equals("action.cmd")){
        Bundle data = intent.getExtras();//getExtras()返回的intent所附带的额外数据
        Message msg = new Message();
        msg.what = 0x345;
        msg.setData(data);
        //UI线程给将控制信号数据封装成message发送给子线程的handler
        //子线程负责将自己handler所携带的message发送给服务器
        //（即子线程将该信号数据发送给服务器）
        clientThread.revHandler.sendMessage(msg);//向clientThread线程中的revHandler发送消息
      }
		else if(action.equals("action.move"))
		{
        Bundle data = intent.getExtras();//getExtras()返回的是Bundle的对象
        Message msg = new Message();
        msg.what = 0x345;
        msg.setData(data);
        //UI线程给将控制信号数据封装成message发送给子线程的handler
        //子线程负责将自己handler所携带的message发送给服务器
        //（即子线程将该信号数据发送给服务器）
        clientThread.revHandler.sendMessage(msg);//向clientThread线程中的revHandler发送消息

      }
		else if(action!=null&&action.equals("action.Info"))
		{
	  	  info = intent.getExtras().getByteArray("info");

		  //判断DTU上线，eclipse编的服务器使用到了，也可以不添加这条指令，直接判断有没有接收到DTU发送的数据即可，即第二个判断
		  if(info!=null&&info[0]==0x07&&info[1]==0x01&&info[2]==0x06) { //要改
			  DTU.setText("DTU上线");
		  }
		  //判断phone上线
		  else if(info!=null&&info[0]==0x07&&info[1]==0x01&&info[2]==0x07){ //要改
			  Phone.setText("Phone上线");
		  }

		  //当DTU先上线，并且发送一些数据到服务器之后，手机才连接入服务器
		  else if(info!=null&&info[0]==0x07&&info[1]==0x01&&info[2]==0x00) { //要改
		  	 	 DTU.setText("DTU上线");

		  	 	 short dlat = Exchange1(info,5);//将5,6两个字节的十六进制数转换为十进制数
		 		 short dlng = Exchange1(info,7);//将7,8两个字节的十六进制数转换为十进制数
		  		 Log.i("msg", "PathShow.dlat: "+String.valueOf(dlat));//显示平面横坐标，注意和常识有区别
		 		 Log.i("msg", "PathShow.dlng: "+String.valueOf(dlng));//显示平面纵坐标，注意和常识有区别


			  	//可以接收到System.dlat和System.dlng
			 	pxpy.add(dlat);
			  	pxpy.add(dlng);
			  	Log.i("msg", "System....pxpy.size: "+pxpy.size());
		  }
		  //接收到DTU发送的确认信息，继续发送剩下的坐标点
		  else if(info!=null&&info[0]==0x07&&info[1]==0x04){ //要改
//			  times++;
			  Log.e("手动绘图", "手动绘图是否收到船载终端发送的消息");//没执行到
		  }
		 /* //接收到DTU发送的确认信息，继续发送剩下的坐标点
		  else if(info!=null&&info[0]==0x07&&info[1]==0x01&&info[2]==0x04){
//			  times++;
			  Log.e("手动绘图", "手动绘图是否收到船载终端发送的消息");//执行到
		  }*/
	  }
    }
  };


	//返回一个十进制数
	public short Exchange1(byte[] p ,int i){
		return getShort(p,i);
	}



	//关闭广播
  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mRefreshBroadcastReceiver);
  }

  private void initViews(){
    connnect  = findViewById(R.id.Connect);
    auto      = findViewById(R.id.imageButton);
    manual    = findViewById(R.id.imageButton2);
    halfauto  = findViewById(R.id.imageButton3);
    ipaddress = findViewById(R.id.iprev);
    warnning  = findViewById(R.id.warnning);
    light     = findViewById(R.id.serverlive);
    point     = new short[100][2];
    response  = new byte[28];//放平面坐标的字节数组
    response[0]=0x07;
    response[1]=0x05;
//	info  = new byte[28];//放平面坐标的字节数组
      info = new byte[1032];
//	info1  = new byte[28];//放平面坐标的字节数组
      info1 = new byte[1032];
	DTU = findViewById(R.id.DTU);
	Phone = findViewById(R.id.Phone);

	GNGGAData = new byte[90];

  }



/*
		通过添加AlertDialog对话框，可以确保用户不会因误操作而直接退出Activity
*/


	/** Called when the activity is first created. */

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK )
		{
			// 创建退出对话框
			AlertDialog isExit = new AlertDialog.Builder(this).create();

			// 设置对话框标题
			isExit.setTitle("系统提示");

			// 设置对话框消息
			isExit.setMessage("确定要退出吗?");

			// 添加选择按钮并注册监听
			isExit.setButton("确定", listener);
			isExit.setButton2("取消", listener);

			// 显示对话框
			isExit.show();
		}
		return false;
	}

	/**监听对话框里面的button点击事件*/
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
	{

		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
				case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
					finish();
					break;
				case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
					break;
				default:
					break;
			}
		}
	};






}
