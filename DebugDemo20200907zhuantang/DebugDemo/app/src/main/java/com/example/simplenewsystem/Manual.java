package com.example.simplenewsystem;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.AlertDialog;

/**
 * Created by Administrator on 2019-04-02.
 */

//类继承了AppCompatActivity会显示标题栏。如果直接继承的是Activity不会出现标题栏
public class Manual extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{


//秦导的数据传输格式用的string
//没有相应的注解，代码杂糅，有空改注解
    Button stop;                //停止按钮
    Button AutoBack;            //自动返航按钮
	Button JumpAuto;            //跳转到自动模式界面按钮
	Button HalfAuto;            //跳转到自动模式界面按钮
	Button TurnToAnother;
	CheckBox CheckBox;			//复选框

//    ImageButton forward;        //图形按钮 前进
//    ImageButton backward;       //图形按钮 后退
//    ImageButton left;           //图形按钮 左转
//    ImageButton right;          //图形按钮 右转

//	TextView speedtxt;          //文本框 速度pwm值   //20201029注销

//	TextView paotxt;            //文本框 抛盘pwm值   //20201029注销

	TextView paopan;       //抛盘电机
	TextView zhendong;       //振动电机


	TextView Etric;         //剩余电量		手动里面的
	TextView Erl;           //剩余饵料		手动里面的
	TextView North;           //剩余饵料		手动里面的
	TextView East;           //剩余饵料		手动里面的

    byte [] cmd;                //28字节控制命令字节数组
	byte [] info;               //存放监测参数的28字节数组1032
//	byte [] temp;


//	float X_Sensor;			//X方向的陀螺仪数据
//	float Y_Sensor;			//Y方向的陀螺仪数据
//	float Z_Sensor;			//Z方向的陀螺仪数据

	static boolean dangWei = false;				//判断标志，判断有没有选后退控制
	static int speedPaoPan = 0;					//把speedPaoPan静态化，使得函数内部也可以使用。speedPaoPan是权限选择的值
	static int speedZhenDong = 0;				//把speedZhenDong静态化，使得函数内部也可以使用。speedZhenDong是权限选择的值

	static int Width;			//手机分辨率的宽度
	static int Height;			//手机分辨率的高度


	private Spinner spinner1;
	private Spinner spinner2;

	private List<String> data_list1;
	private List<String> data_list2;

	private ArrayAdapter<String> arr_adapter1;
	private ArrayAdapter<String> arr_adapter2;



//	SensorManager sm;  //用来引用【传感器管理员】
//	Sensor sr;         //用来引用【加速传感器对象】
//	TextView txv;      //用来引用画面中的文字组件
//	TextView textViewHelp;	//辅助文字

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoudong);
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
		spinner1.setOnItemSelectedListener(Manual.this);


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
		spinner2.setOnItemSelectedListener(Manual.this);



		// 获取布局文件中的LinearLayout容器，手动模式关键区域
		LinearLayout root = findViewById(R.id.root);
		// 创建DrawView组件
		final DrawView1 draw = new DrawView1(this);

		// 设置自定义组件的最小宽度、高度
		draw.setMinimumWidth(0);
		draw.setMinimumHeight(0);
		//为drawview组件绑定tounch事件
		draw.setOnTouchListener( new View.OnTouchListener() { //这里的手动方向要不要改回原来的上下左右呢？

			public boolean onTouch(View v, MotionEvent event) {
				Log.e("onTouch", "onTouch");
				draw.currentX=event.getX();
				draw.currentY=event.getY();
				Log.e("onTouch", "currentX:"+draw.currentX+"currentY:"+draw.currentY);

				//如果点在扇形的半径之上，则可以进行操控。
				//if(draw.currentX*draw.currentX+draw.currentY*draw.currentY<114244&&(!((draw.currentX*draw.currentX+draw.currentY*draw.currentY<114244)&&((313*draw .currentY+116*draw .currentX-168570>=0)&&(302*draw .currentY-116*draw .currentX-49140>=0))))){
				//if(!((313*draw .currentY+116*draw .currentX-168570>=0)&&(302*draw .currentY-116*draw .currentX-49140>=0))){

				if(dangWei==false){
					//前进情况
					if(draw.currentY<Height/8&&Math.pow(draw.currentX-Width/2,2)+Math.pow(draw.currentY-Height/8,2)<(Height/8)*(Height/8)){
						UIUtils.showToast( Manual.this , "执行到前进过程" );
						Log.e("收到", "咦，竟然收到了");
						//通知drawview组件重绘
						draw.invalidate();

						/*

						当y<245时，前进
						若点在x=496的左边，即x<496，则
							左明轮的最大占空比为0~75，右明轮的最大占空比为75，

							根据该点的坐标，确定右明轮的占空比是：
							Math.sqrt((draw.currentX-496)*(draw.currentX-496)+(draw.currentY-245)*(draw.currentY-245))/240*50+25

							根据该点的坐标，确定左明轮的占空比是：
							Math.arctan(Math.toDegrees((-draw.currentY+245)/Math.abs(-draw.currentX+496)))/90*75*Math.sqrt((draw.currentX-496)*(draw.currentX-496)+(draw.currentY-245)*(draw.currentY-245))/240



						当y<245时，前进
						若点在x=496的右边，即x>496，则
							左明轮的最大占空比为75，右明轮的最大占空比为0~75，

							根据该点的坐标，确定左明轮的占空比是：
							Math.sqrt((draw.currentX-496)*(draw.currentX-496)+(draw.currentY-245)*(draw.currentY-245))/240*50+25

							根据该点的坐标，确定右明轮的占空比是：
							Math.arctan((-draw.currentY+245)/(draw.currentX-496))/90*75*Math.sqrt((draw.currentX-496)*(draw.currentX-496)+(draw.currentY-245)*(draw.currentY-245))/240


						* */


						int x = 0;//左电机明轮占空比
						int y = 0;//右电机明轮占空比



						if(draw.currentX==Width/2){
							x = (int)((Height/8-draw.currentY)/(Height/8)*50+25);
							y =	(int)((Height/8-draw.currentY)/(Height/8)*50+25);
						} else if(draw.currentX<Width/2){
							//右轮
							y = (int)(Math.sqrt((draw.currentX-Width/2)*(draw.currentX-Width/2)+(draw.currentY-Height/8)*(draw.currentY-Height/8))/(Height/8)*50+25);
							//左轮
							x = (int)(Math.toDegrees(Math.atan((-draw.currentY+Height/8)/Math.abs(-draw.currentX+Width/2)))/90*75*Math.sqrt((draw.currentX-Width/2)*(draw.currentX-Width/2)+(draw.currentY-Height/8)*(draw.currentY-Height/8))/(Height/8));

						}else{
							//左轮
							x = (int)(Math.sqrt((draw.currentX-Width/2)*(draw.currentX-Width/2)+(draw.currentY-Height/8)*(draw.currentY-Height/8))/(Height/8)*50+25);
							//右轮
							y = (int)(Math.toDegrees(Math.atan((-draw.currentY+Height/8)/Math.abs(-draw.currentX+Width/2)))/90*75*Math.sqrt((draw.currentX-Width/2)*(draw.currentX-Width/2)+(draw.currentY-Height/8)*(draw.currentY-Height/8))/(Height/8));
						}


						//在这里求解占空比
                        //手动0x02


						cmd[3]  = 0x01;                //控制命令字节数组第4个字节命令0x01，控制手动行驶
						//cmd[4] = java.lang.Byte.decode(("0x"+Integer.toHexString(x+100)));
						cmd[4]  = (byte)(java.lang.Byte.decode(("0x"+Integer.toHexString(x)))+0x64);//左占空比		 char c = (char)(b & 0xff);// char c = (char) b;为有符号扩展

//						cmd[4]  = (byte)(x+100);//左占空比		 char c = (char)(b & 0xff);// char c = (char) b;为有符号扩展
						//cmd[4]  = (byte)x;//左占空比
						//cmd[5]  = (byte)y;//右占空比
//						cmd[5]  = (byte)((y+100)&0xff);//右占空比
//						cmd[5]  = (byte)(y+100);//右占空比
						cmd[5] =  (byte)(java.lang.Byte.decode(("0x"+Integer.toHexString(y)))+0x64);

//						cmd[8] = (byte)(java.lang.Byte.decode(("0x"+Integer.toHexString(speedPaoPan)))+0x64);
//						cmd[9] = (byte)(java.lang.Byte.decode(("0x"+Integer.toHexString(speedZhenDong)))+0x64);
                    /*    cmd[8] = 0x30;  //new 暂时不加20201022
                        cmd[9] = 0x32;
                        temp[0] = (byte) ((cmd[12]-0x30)*100+(cmd[13]-0x30)*10+(cmd[15]-0x30));
                        if(cmd[11] == '-'){
                        	temp[0] = (byte) (-1*temp[0]);
						}
                        temp[1] = (byte) ((cmd[18]-0x30)*100+(cmd[19]-0x30)*10+(cmd[21]-0x30));
                        if(cmd[17] =='-'){
                        	temp[1] = (byte)(-1*temp[1]);
						}
                        temp[2] = (byte) ((cmd[23]-0x30)*100+(cmd[24]-0x30)*10+(cmd[26]-0x30));
                        temp[3] = (byte) ((cmd[28]-0x30)*100+(cmd[29]-0x30)*10+(cmd[31]-0x30));
                        temp[4] = (byte) ((cmd[33]-0x30)*100+(cmd[34]-0x30)*10+(cmd[36]-0x30));
                        temp[5] = (byte) ((cmd[38]-0x30)*100+(cmd[39]-0x30)*10+(cmd[41]-0x30));

*/



//						cmd[8]  = (byte)(speedPaoPan+100);//抛盘占空比
//						cmd[9]  = (byte)(speedZhenDong+100);//振动占空比
//						cmd[27] = (byte)(0x08+cmd[3]);  //字节数组最后一位      校验位
//						cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位      校验位

						Intent intent = new Intent();
						//将控制命令cmd的值添加到intent
						intent.putExtra("cmd",cmd);
						//设置intent的action值是action.cmd
						intent.setAction("action.move");
						sendBroadcast(intent);//开启广播

					}
				}
				else{
					//后退情况
					if(Math.pow(draw.currentX-Width/2,2)+Math.pow(draw.currentY-Height/8,2)<(Height/8)*(Height/8)&&draw.currentY+draw.currentX-Width/2-Height/8>0&&draw.currentY-draw.currentX+Width/2-Height/8>0){
						UIUtils.showToast( Manual.this , "执行到后退过程" );
						Log.e("收到", "倒车请注意");
						//通知drawview组件重绘
						draw.invalidate();


						/*


						当y>245时，后退
						若点在x=496的左边，即x<496，则
							左明轮的最大占空比为0~75，右明轮的最大占空比为75，


							根据该点的坐标，确定右明轮的占空比是：
							Math.sqrt((draw.currentX-496)*(draw.currentX-496)+(draw.currentY-245)*(draw.currentY-245))/240*50+25

							根据该点的坐标，确定左明轮的占空比是：
							Math.arctan((draw.currentY-245)/(-draw.currentX+496))/90*75*Math.sqrt((draw.currentX-496)*(draw.currentX-496)+(draw.currentY-245)*(draw.currentY-245))/240


						当y>245时，后退
						若点在x=496的右边，即x>496，则
							左明轮的最大占空比为75，右明轮的最大占空比为0~75，


							根据该点的坐标，确定左明轮的占空比是：
							Math.sqrt((draw.currentX-496)*(draw.currentX-496)+(draw.currentY-245)*(draw.currentY-245))/240*50+25

							根据该点的坐标，确定右明轮的占空比是：
							Math.arctan((draw.currentY-245)/(draw.currentX-496))/90*75*Math.sqrt((draw.currentX-496)*(draw.currentX-496)+(draw.currentY-245)*(draw.currentY-245))/240

						* */

						int x = 0;//左电机明轮占空比
						int y = 0;//右电机明轮占空比


						if(draw.currentX==Width/2){
							x = (int)((draw.currentY-Height/8)/(Height/8)*50+25);
							y =	(int)((draw.currentY-Height/8)/(Height/8)*50+25);
						} else if(draw.currentX<Width/2){
							//右轮
							y = (int)(Math.sqrt((draw.currentX-Width/2)*(draw.currentX-Width/2)+(draw.currentY-Height/8)*(draw.currentY-Height/8))/(Height/8)*50+25);
							//左轮
							x = (int)(Math.toDegrees(Math.atan((draw.currentY-Height/8)/Math.abs(-draw.currentX+Width/2)))/90*75*Math.sqrt((draw.currentX-Width/2)*(draw.currentX-Width/2)+(draw.currentY-Height/8)*(draw.currentY-Height/8))/(Height/8));

						}else{
							//左轮
							x = (int)(Math.sqrt((draw.currentX-Width/2)*(draw.currentX-Width/2)+(draw.currentY-Height/8)*(draw.currentY-Height/8))/(Height/8)*50+25);
							//右轮
							y = (int)(Math.toDegrees(Math.atan((draw.currentY-Height/8)/Math.abs(-draw.currentX+Width/2)))/90*75*Math.sqrt((draw.currentX-Width/2)*(draw.currentX-Width/2)+(draw.currentY-Height/8)*(draw.currentY-Height/8))/(Height/8));
						}

						//在这里求解占空比

						cmd[3]  = 0x01;                //控制命令字节数组第4个字节命令0x01，控制手动行驶
//						cmd[4] = java.lang.Byte.decode(("0x"+Integer.toHexString(-x+100)));
						cmd[4] = (byte)(-1*java.lang.Byte.decode(("0x"+Integer.toHexString(x)))+0x64);
//						cmd[4]  = (byte)(-x+100);//左占空比
						//cmd[4]  = (byte)(-x);//左占空比
						//cmd[5]  = (byte)(-y);//右占空比
						cmd[5] = (byte)(-1*java.lang.Byte.decode(("0x"+Integer.toHexString(y)))+0x64);
//						cmd[5]  = (byte)(-y+100);//右占空比
//						cmd[8] = (byte)(java.lang.Byte.decode(("0x"+Integer.toHexString(speedPaoPan)))+0x64);
//						cmd[9] = (byte)(java.lang.Byte.decode(("0x"+Integer.toHexString(speedZhenDong)))+0x64);
//						cmd[8]  = (byte)(speedPaoPan+100);//抛盘占空比
//						cmd[9]  = (byte)(speedZhenDong+100);//振动占空比
//						cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位      校验位
/*                     这里new的不应该放在这里，要放在info[]里！！！！
						cmd[8] = 0x30;
						cmd[9] = 0x32;
						temp[0] = (byte) ((cmd[12]-0x30)*100+(cmd[13]-0x30)*10+(cmd[15]-0x30));
						if(cmd[11] == '-'){
							temp[0] = (byte) (-1*temp[0]);
						}
						temp[1] = (byte) ((cmd[18]-0x30)*100+(cmd[19]-0x30)*10+(cmd[21]-0x30));
						if(cmd[17] =='-'){
							temp[1] = (byte)(-1*temp[1]);
						}
						temp[2] = (byte) ((cmd[23]-0x30)*100+(cmd[24]-0x30)*10+(cmd[26]-0x30));
						temp[3] = (byte) ((cmd[28]-0x30)*100+(cmd[29]-0x30)*10+(cmd[31]-0x30));
						temp[4] = (byte) ((cmd[33]-0x30)*100+(cmd[34]-0x30)*10+(cmd[36]-0x30));
						temp[5] = (byte) ((cmd[38]-0x30)*100+(cmd[39]-0x30)*10+(cmd[41]-0x30));
*/
						Intent intent = new Intent();
						//将控制命令cmd的值添加到intent
						intent.putExtra("cmd",cmd);
						//设置intent的action值是action.cmd
						intent.setAction("action.move");
						sendBroadcast(intent);//开启广播*/

					}
				}
				//通知drawview组件重绘
				//draw.invalidate();

				//返回true表明算是方法已经处理该事件
				return true;

			}

		});
		root.addView(draw);

		//CheckBox:复选框
		CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					dangWei = true;//判断标志置为true，开启后退控制

					/*draw.currentX = 496;
					draw.currentY = 245;*/
					draw.currentX = Width/2;
					draw.currentY = Height/8;
					draw.invalidate();

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
					draw.currentX = Width/2;
					draw.currentY = Height/8;
					draw.invalidate();

//					cmd[8] =0x30; 要改
//					cmd[9] =0x31;
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



		//接收实时参数
		IntentFilter intentFilter = new IntentFilter();//创建IntentFilter
		intentFilter.addAction("action.Info");//指定BroadcastReceiver监听的Action
		//注册广播，接收System广播的实时参数
		registerReceiver(mRefreshBroadcastReceiver, intentFilter);
		//注册应用内广播接收器



        //手动停止按钮
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	cmd[3]=0x0F;
				cmd[8] =0x30;
				cmd[9] =0x31;
//                cmd[3]  = 0x0F;                //控制命令字节数组第4个字节命令0x0F，控制手动停止
				cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位
                Intent intent = new Intent();
                //将控制命令cmd的值添加到intent
                intent.putExtra("cmd",cmd);
                //设置intent的action值是action.cmd
                intent.setAction("action.move");
                sendBroadcast(intent);//开启广播

				draw.currentX = Width/2;
				draw.currentY = Height/8;
				draw.invalidate();

				spinner1.setSelection(0,true);
				spinner2.setSelection(0,true);

            }
        });

 /*       //自动返航按钮  暂时没有该功能
        AutoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                cmd[3]  = 0x08;                //控制命令字节数组第4个字节命令0x08，控制自动返航
//				cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位
                Intent intent = new Intent();
                //将控制命令cmd的值添加到intent
                intent.putExtra("cmd",cmd);
                //设置intent的action值是action.cmd
                intent.setAction("action.move");
                sendBroadcast(intent);//开启广播
            }
        });*/

		//跳转到自动模式界面按钮
		JumpAuto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Manual.this,Auto.class);
				startActivity(intent);//开启广播
			}
		});



		//跳转到半自动模式界面按钮
		HalfAuto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Manual.this,HalfAuto.class);
				startActivity(intent);//开启广播
			}
		});

		//跳转到转塘模式界面按钮
		TurnToAnother.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			Intent intent = new Intent(Manual.this,TurnToAnother.class);
			startActivity(intent);//开启广播
		}
	});


/*


		final Handler myHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				// 如果该消息是本程序所发送的
				if (msg.what == 0x1233)
				{
					if(flagTuoLuoYi==true) {
						//UIUtils.showToast(Manual.this, "X轴:" + X_Sensor + "Y轴:" + Y_Sensor + "Z轴:" + Z_Sensor);//显示X,Y,Z三个方向的陀螺仪参数
						int rate = analyseSensorDate(X_Sensor, Y_Sensor, Z_Sensor);
						cmd[3] = 0x01;//手动控制

						if (Math.abs(X_Sensor) < Math.abs(Y_Sensor)) {
							//前进
							if (Y_Sensor < -1.0 & Y_Sensor > -9.8) {

								cmd[4] = (byte)(rate+100);            //左明轮占空比
								cmd[5] = (byte)(rate+100);            //右明轮占空比
					 		    cmd[8] = (byte)(speedPaoPan+100);			//抛盘电机占空比
								cmd[9] = (byte)(speedZhenDong+100);			//振动电机占空比

								cmd[27] = (byte) (0x08 + cmd[3]);
								Intent intent = new Intent();
								intent.putExtra("cmd", cmd);
								intent.setAction("action.move");
								sendBroadcast(intent);//发送广播
								textViewHelp.setText("前进" );
							}

							//后退
							if (Y_Sensor > 1.0 & Y_Sensor < 9.8) {
								cmd[4] = (byte)(rate+100);            //左明轮占空比
								cmd[5] = (byte)(rate+100);            //右明轮占空比

								cmd[8] = (byte)(speedPaoPan+100);			//抛盘电机占空比
								cmd[9] = (byte)(speedZhenDong+100);			//振动电机占空比
								cmd[27] = (byte) (0x08 + cmd[3]);
								Intent intent = new Intent();
								intent.putExtra("cmd", cmd);
								intent.setAction("action.move");
								sendBroadcast(intent);//发送广播
								textViewHelp.setText("后退" );
							}
						} else {
							//右拐
							if (X_Sensor < -1.0 & X_Sensor > -9.8) {
				//修改				cmd[4] = (byte)(rate+100);            //左明轮占空比
				//修改				cmd[5] = (byte)(rate+100);            //右明轮占空比

								cmd[8] = (byte)(speedPaoPan+100);			//抛盘电机占空比
								cmd[9] = (byte)(speedZhenDong+100);			//振动电机占空比
								cmd[27] = (byte) (0x08 + cmd[3]);
								Intent intent = new Intent();
								intent.putExtra("cmd", cmd);
								intent.setAction("action.move");
								sendBroadcast(intent);//发送广播
								textViewHelp.setText("右拐" );
							}
							//左拐
							if (X_Sensor > 1.0 & X_Sensor < 9.8) {

				//修改				cmd[4] = (byte)(rate);            //左明轮占空比
				//修改				cmd[5] = (byte)(rate);            //右明轮占空比

								cmd[8] = (byte)(speedPaoPan+100);			//抛盘电机占空比
								cmd[9] = (byte)(speedZhenDong+100);			//振动电机占空比
								cmd[27] = (byte) (0x08 + cmd[3]);
								Intent intent = new Intent();
								intent.putExtra("cmd", cmd);
								intent.setAction("action.move");
								sendBroadcast(intent);//发送广播
								textViewHelp.setText("左拐" );
							}
						}

					}


				}
			}
		};

		// 定义一个计时器，让该计时器周期性地执行指定任务
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				// 发送空消息
				myHandler.sendEmptyMessage(0x1233);
			}
		}, 0, 100);



		//CheckBox:复选框
		CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){

					flagTuoLuoYi = true;//判断标志置为true，开启陀螺仪控制
					UIUtils.showToast( Manual.this , "打开陀螺仪，进入手动模式:" +flagTuoLuoYi);

					if(speedPaoPan==0&&speedZhenDong!=0){
						//弹出一个对话框
						flagTuoLuoYi = false;//判断标志置为false，关闭陀螺仪控制

						AlertDialog.Builder dialog = new AlertDialog.Builder(Manual.this);
						dialog.setTitle("警告");
						dialog.setMessage("请先设置抛盘电机转动速度");
						dialog.setCancelable(false);
						dialog.setPositiveButton("我知道了",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								UIUtils.showToast( Manual.this , "You clicked OK" );
							}
						});
						dialog.show();
						CheckBox.setChecked(false);//设为没有被选中
					}
				}
				else{
					flagTuoLuoYi = false;//判断标志置为false，关闭陀螺仪控制
					UIUtils.showToast( Manual.this , "关闭陀螺仪，退出手动模式:" +flagTuoLuoYi);
				}
			}

		});

*/



/*
        //手动前进按钮
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmd[2]  = 0x02 ;                //控制命令字节数组第3个字节命令 0x02，控制手动前进
                cmd[27] = (byte)(0x09+cmd[2]);
                Intent intent = new Intent();
                intent.putExtra("cmd",cmd);
                intent.setAction("action.move");
                sendBroadcast(intent);

            }
        });

        //手动后退按钮
        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmd[2]  = 0x03 ;                //控制命令字节数组第3个字节命令 0x03，控制手动后退
                cmd[27] = (byte)(0x09+cmd[2]);
                Intent intent = new Intent();
                intent.putExtra("cmd",cmd);
                intent.setAction("action.move");
                sendBroadcast(intent);//发送广播

            }
        });

        //手动右转按钮
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmd[2]  = 0x04 ;                //控制命令字节数组第3个字节命令 0x04，控制手动左转
                cmd[27] = (byte)(0x09+cmd[2]);
                Intent intent = new Intent();
                intent.putExtra("cmd",cmd);
                intent.setAction("action.move");
                sendBroadcast(intent);//发送广播

            }
        });

        //手动左转按钮
       left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmd[2]  = 0x05 ;                //控制命令字节数组第3个字节命令 0x05，控制手动右转
                cmd[27] = (byte)(0x09+cmd[2]);
                Intent intent = new Intent();
                intent.putExtra("cmd",cmd);
                intent.setAction("action.move");
                sendBroadcast(intent);//发送广播

            }
        });*/

//		sm = (SensorManager) getSystemService(SENSOR_SERVICE); //从系统服务获取传感器管理员
//		sr = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //获取加速传感器

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
				if(info!=null){//这里还要根据info[]排版的来进行更改！！！
					String lng = String.valueOf((float)(getShort(info,5)-10000)/10);//5,6两位的数据转换为平面横坐标
					String lat = String.valueOf((float)(-getShort(info,7)+10000)/10);//7,8两位的数据转换为平面纵坐标
					String etric = String.valueOf((float) (getShort(info,11))/10);//第11,12位的数据转换为剩余电量
					String erl = String.valueOf((float) (getShort(info,13))/100);//第13,14位的数据转换为剩余饵料,得到的数据是10g，需要除以100，转换为kg。

					East.setText(lng);
					North.setText(lat);
					Etric.setText(etric);
					Erl.setText(erl);


				}
			}
		}
	};
/*
	@Override
	public void onSensorChanged(SensorEvent event) {
		txv.setText(String.format("X轴: %1.2f, Y轴: %1.2f, Z轴: %1.2f", event.values[0], event.values[1], event.values[2]));

		X_Sensor = event.values[0];
		Y_Sensor = event.values[1];
		Z_Sensor = event.values[1];

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	private  int analyseSensorDate(double x_sensor, double y_sensor, double z_sensor) {
				int rate =0;//占空比
				if(Math.abs(x_sensor)>=Math.abs(y_sensor)) {//左右拐的陀螺仪加速度大于前进后退的陀螺仪加速度
					//依据X轴方向的陀螺仪示数来判断左右拐的速度大小
					if (Math.abs(x_sensor) > 1.0 & Math.abs(x_sensor) < 9.8) {
						rate = (int) ((0.08 * Math.abs(x_sensor) + 0.2) * 100);
					}
				}else {
					//依据Y轴方向的陀螺仪示数来判断前进后退的速度大小
					if (Math.abs(y_sensor) > 1.0 & Math.abs(y_sensor) < 9.8) {
						rate = (int) ((0.08 * Math.abs(y_sensor) + 0.2) * 100);
					}
				}
				return rate;

	}



	@Override
	protected void onResume() {
		super.onResume();
		sm.registerListener(this, sr, SensorManager.SENSOR_DELAY_NORMAL); //向加速传感器 (sr) 注册监听对象(this)
	}

	@Override
	protected void onPause() {
		super.onPause();
		sm.unregisterListener(this);  //取消监听对象(this) 的注册
	}
*/


    //抛盘电机占空比
	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

		int number = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
		TextView tv;
		TextView tv1;
		switch (adapterView.getId()){
			case R.id.spinner1:
				speedPaoPan = number;			//抛盘电机占空比
				tv1 = (TextView)view;
				tv1.setTextColor(getResources().getColor(R.color.white));//因为背景是黑色的，所以设置字体为白色的
//				UIUtils.showToast(Manual.this,"当前抛盘电机占空比：" + speedPaoPan);
				break;

			case R.id.spinner2:
				speedZhenDong = number;			//振动电机占空比
				//为了确保在振动电机开启的时候，抛盘电机是运行的，防止饵料堵塞出料口
				//即，抛盘电机先于振动电机先运行
				if(speedPaoPan ==0 && speedZhenDong != 0){
					speedPaoPan = speedZhenDong;
					//当振动电机开启的时候，没有手动开启抛盘电机的话，就自动给一个抛盘电机占空比
					spinner1.setSelection(2,true);//抛盘电机占空比60%
				}
				tv = (TextView)view;
				tv.setTextColor(getResources().getColor(R.color.white));//因为背景是黑色的，所以设置字体为白色的
//				UIUtils.showToast(Manual.this,"当前振动电机占空比：" + speedZhenDong);
				break;
			default:
				break;

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {

	}






	//将两字节16进制数转化为十进制数
	public static short getShort(byte[] arr, int index) {
		return (short) (0xff00 & arr[index] << 8 | (0xff & arr[index + 1]));
	}



	public void init(){


        stop = findViewById(R.id.stop);
        AutoBack = findViewById(R.id.AutoBack);
        JumpAuto = findViewById(R.id.JumpAuto);
		HalfAuto = findViewById(R.id.HalfAuto);
		CheckBox = findViewById(R.id.CheckBox);
		TurnToAnother = findViewById(R.id.TurnToAnother);


     //   forward  = findViewById(R.id.imageButton4);
     //   backward = findViewById(R.id.imageButton6);
     //   left     = findViewById(R.id.imageButton3);
     //   right    = findViewById(R.id.imageButton5);

//        speedtxt   = findViewById(R.id.speedpwm); //20201029注销
        //paopwm   = findViewById(R.id.seekBar2);
//        paotxt   = findViewById(R.id.paofpwm);  //20201029注销
		//ManualControl = findViewById(R.id.ManualControl);
		//Attention = findViewById(R.id.Attention);

		paopan   = (TextView) findViewById(R.id.paopan);
		zhendong = (TextView) findViewById(R.id.zhendong);

		//Spinner 下拉栏   https://www.cnblogs.com/tinyphp/p/3858920.html
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);


		Etric  = (TextView) findViewById(R.id.Etricrev);
		Erl    = (TextView) findViewById(R.id.Erlrev);
		North  = (TextView) findViewById(R.id.textView11);
		East   = (TextView) findViewById(R.id.textView12);

		//txv = (TextView) findViewById(R.id.textView);     // 获取TextView组件
		//textViewHelp = (TextView) findViewById(R.id.textViewHelp);     // 获取TextView组件


		cmd = new byte[28];
//		cmd= new byte[1032];
        cmd[0] = 0x07;                  //命令字节数组第1个字节命令 0x07
//        cmd[1] = 0x02;                  //命令字节数组第2个字节命令 0x02		之前版本的手动控制协议
		cmd[1] = 0x02;					//命令字节数组第2个字节命令 0x01
//		cmd[2] = 0x00;					//命令字节数组第3个字节命令 0x00

//		temp = new byte[1032];
//		info = new byte[28];
		info = new byte[1032];
    }


}
