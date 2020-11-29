package com.example.simplenewsystem;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;

//import static com.example.simplenewsystem.System.times;
import static com.example.simplenewsystem.TrajectoryPlanning.pointNum;

/**
 * Created by Administrator on 2019-04-02.
 */

//类继承了AppCompatActivity会显示标题栏。如果直接继承的是Activity不会出现标题栏
public class Auto extends AppCompatActivity implements Serializable {

	private static final String SPACE_SEPARATOR = " ";

	ImageButton jiance;         //监测参数图形按钮
    ImageButton lujing;         //实时路径图形按钮
    double[][] point;
//    Button setp;                //设置原点按钮
//    Button conp;                //确认原点按钮
    Button jzp;                 //原点校正按钮
    Button gjgh;                //轨迹规划按钮
    Button xzdt;                //下载地图按钮
    Button kqzy;                //开启作业按钮
	Button JumpManual;          //跳转到手动模式界面按钮
	Button JumpHalfAuto;        //跳转到半自动模式界面按钮
	Button ManualStop;          //手动停止按钮
	Button pauseCruise;			//启动巡航按钮
	Button manualDrawMap;		//手动绘制地图按钮


    EditText Length;
    ProgressDialog pd1;         //进度对话框
    byte [] cmd;                //控制命令字节数组
    TwoBean path;               //二维数组path

	String initpoint=null;

	byte [] revpath;			//将轨迹规划的点存到字节数组中

	//文件名称
	String fileName = "TrajectoryPlanning.txt";		//文件名

	//写入和读出的数据信息
	String content = "1234";

	String [][] str_arr2 ;

	byte[] a1a1 ;
	byte[] a1b1;
	byte[] b1a1;
	byte[] b1b1;

	int length;

	int t;

	byte[] revPoint;

	String s = "";

	byte [] tmp;

	byte [] info;

	int revPointLength;

//	int num  = 0; 					//用于记录revpath数组已经发送到DTU的字节数

	int times = 0;

	@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zidong);
        initviews();

//		writeFileData(fileName, content); // 写入文件

//		String result = readFileData(fileName); // 读取文件

        //两个广播接收器，一个接收实时参数，一个接收实时路径

        IntentFilter intentFilter = new IntentFilter();//创建IntentFilter
        intentFilter.addAction("action.Info");//指定BroadcastReceiver监听的Action
        //注册广播，接收System广播的实时参数
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);

        //广播接收器,接收规划好的路径
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("action.Path");
        registerReceiver(mRefreshBroadcastReceiver, intentFilter1);


        //监测参数
        jiance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //使用Intent从自动控制页面跳转到监测页面
                Intent intent = new Intent(Auto.this,JianCe.class);
                //启动监测页面
                startActivity(intent);
            }
        });


        //实时路径
        lujing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //使用Intent从自动控制页面跳转到实时路径页面
                Intent intent = new Intent(Auto.this,PathShow.class);
                //将path的值添加到intent
                intent.putExtra("path",path);
                //启动实时路径页面
                startActivity(intent);
            }
        });


        //原点校正  0x07 0x01 0x00 0x21 ……
        jzp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	cmd[8]=0x31;
            	cmd[9]=0x30;
//            	cmd[1]  = 0x01;
//                cmd[2]  = 0x00;
//                cmd[3]  = 0x21;
//				cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位
                Intent intent = new Intent();
                intent.putExtra("cmd",cmd);
                intent.setAction("action.cmd");
                sendBroadcast(intent);
            }
        });
        //轨迹规划  0x07 0x03 0x02				只是让手机自我规划路径。不需要经过服务器程序
        gjgh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*cmd[1]  = 0x03;
				cmd[2]  = 0x01;
				cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位
				Intent intent = new Intent();
				intent.putExtra("cmd",cmd);
				intent.setAction("action.cmd");
				sendBroadcast(intent);*/
				cal(initpoint);
				writeFileData(fileName, pointNum); // 写入文件

			}
        });
        //下载地图  0x07 0x04				 要改 有下载地图的新的通信协议
        xzdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				String result = readFileData(fileName); 		// 读取文件		result结果正确
				String[] result1 = stringToArray(result);		// 读取文件		result1结果正确
				String[][] result2 = stringToArray2(result1);	// 读取文件		result2结果正确   38个数据

				Log.e("Ting","result1 length的值"+result1.length);	//38
				Log.e("Ting","result2 length的值"+result2.length);	//50			？？？为啥发生变化

				length = result1.length/2;
				Log.e("Ting","length的值"+length);	//19

				for (int j = 0;j<result2.length;j++){

					//解决出现s == null 异常
					try {

//						byte aa = Byte.parseByte(result2[j][0].substring(0,2), System.Globalization.NumberStyles.HexNumber);	//c#



//						2020-05-29 11:09:05.881 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第0个  子字符串   :(28   00:    27   60)
//						2020-05-29 11:09:05.881 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第1个  子字符串   :(27   92:    29   fe)
//						2020-05-29 11:09:05.881 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第2个  子字符串   :(2a   58:    2a   44)
//						2020-05-29 11:09:05.881 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第3个  子字符串   :(2a   f8:    28   32)
//						2020-05-29 11:09:05.881 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第4个  子字符串   :(28   38:    27   b8)
//						2020-05-29 11:09:05.882 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第5个  子字符串   :(27   e3:    29   c0)
//						2020-05-29 11:09:05.882 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第6个  子字符串   :(2a   26:    29   f9)
//						2020-05-29 11:09:05.882 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第7个  子字符串   :(2a   a0:    28   62)
//						2020-05-29 11:09:05.882 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第8个  子字符串   :(28   71:    28   10)
//						2020-05-29 11:09:05.883 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第9个  子字符串   :(28   34:    29   81)
//						2020-05-29 11:09:05.883 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第10个  子字符串   :(29   f3:    29   ad)
//						2020-05-29 11:09:05.883 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第11个  子字符串   :(2a   49:    28   93)
//						2020-05-29 11:09:05.883 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第12个  子字符串   :(28   a9:    28   69)
//						2020-05-29 11:09:05.883 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第13个  子字符串   :(28   86:    29   43)
//						2020-05-29 11:09:05.884 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第14个  子字符串   :(29   c1:    29   62)
//						2020-05-29 11:09:05.884 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第15个  子字符串   :(29   f1:    28   c3)
//						2020-05-29 11:09:05.884 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第16个  子字符串   :(28   e2:    28   c1)
//						2020-05-29 11:09:05.884 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第17个  子字符串   :(28   d7:    29   05)
//						2020-05-29 11:09:05.885 10921-10921/com.example.debugdemo E/ Ting 在Auto 中 第18个  子字符串   :(29   8e:    29   17)


						String aa1 = result2[j][0].substring(0,2);
						String ab1 = result2[j][0].substring(2,4);
						String ba1 = result2[j][1].substring(0,2);
						String bb1 = result2[j][1].substring(2,4);


						//输出的子字符串是正确的
						Log.e(" Ting 在Auto 中 第" + j + "个  子字符串  ", "("+aa1+"   "+ab1 +"   "+"   "+ba1+"   "+bb1+ ")");//可以到达



//						byte aa = (byte)Integer.parseInt(aa1,16);
//						byte ab = (byte)Integer.parseInt(ab1,16);
//						byte ba = (byte)Integer.parseInt(ba1,16);
//						byte bb = (byte)Integer.parseInt(bb1,16);



//						byte aa = Byte.parseByte("0x"+result2[j][0].substring(0,2));		//截取高位字节
//						byte aa = Byte.decode(/*"0x"+*/result2[j][0].substring(0,2));		//截取高位字节
//						byte ab = Byte.parseByte("0x"+result2[j][0].substring(2,4));		//截取低位字节
//						byte ba = Byte.parseByte("0x"+result2[j][1].substring(0,2));		//截取高位字节
//						byte bb = Byte.parseByte("0x"+result2[j][1].substring(2,4));		//截取低位字节

/*
						byte aa  = (byte)((Integer.parseInt(result2[j][0],16)>>8)&0xff);		//横坐标高八位			结果为二进制形式，有符号数据，会出现负号
						byte ab  = (byte)((Integer.parseInt(result2[j][0],16))&0xff);			//横坐标低八位			结果为二进制形式，有符号数据，会出现负号
						byte ba  = (byte)((Integer.parseInt(result2[j][1],16)>>8)&0xff);		//纵坐标高八位			结果为二进制形式，有符号数据，会出现负号
						byte bb  = (byte)((Integer.parseInt(result2[j][1],16))&0xff);			//纵坐标低八位			结果为二进制形式，有符号数据，会出现负号
*/
//						Log.e(" Ting在Auto 中 第" + j + "个字节:(" ,aa1+"   "+ab1 +"   "+"   "+ba1+"   "+bb1+ ")");		//可以到达

//						cmd[4]  = (byte)(java.lang.Byte.decode(("0x"+Integer.toHexString(x)))+0x64);//左占空比		 char c = (char)(b & 0xff);// char c = (char) b;为有符号扩展


//						byte result = (byte) Integer.parseInt("FE", 16);
//						byte result = Integer.valueOf("FE", 16).byteValue();

						a1a1[j] = (byte)Integer.parseInt(aa1,16);
						a1b1[j] = (byte)Integer.parseInt(ab1,16);
						b1a1[j] = (byte)Integer.parseInt(ba1,16);
						b1b1[j] = (byte)Integer.parseInt(bb1,16);

						Log.e(" Ting 在Auto 中 第" + j + "个  数组",   " ("+a1a1[j]+"   "+a1b1[j] +"   "+"   "+b1a1[j]+"   "+b1b1[j]+ ")");//可以到达



					}catch(Exception e){

						e.printStackTrace();

					}



//					byte aa  = java.lang.Byte.decode("0x"+((byte)(Integer.parseInt(result2[j][0])>>8)&0xff));
//					byte ab  = java.lang.Byte.decode("0x"+((byte)(Integer.parseInt(result2[j][0]))&0xff));
//					byte ba  = java.lang.Byte.decode("0x"+((byte)(Integer.parseInt(result2[j][1])>>8)&0xff));
//					byte bb  = java.lang.Byte.decode("0x"+((byte)(Integer.parseInt(result2[j][1]))&0xff));
//						Log.e(" Ting在Auto 中 第" + j + "个:(" + aa+"   "+ab +"   ", "   "+ba+"   "+bb+ ")");

//					byte aa  = (byte)((byte)(Integer.parseInt(result2[j][0])>>8)&0xff);	//横坐标高八位			结果为二进制形式，有符号数据，会出现负号
//					byte ab  = ((byte)((Integer.parseInt(result2[j][0]))&0xff));		//横坐标低八位			结果为二进制形式，有符号数据，会出现负号
//
//					byte ba  = (byte)((byte)(Integer.parseInt(result2[j][1])>>8)&0xff);	//纵坐标高八位			结果为二进制形式，有符号数据，会出现负号
//					byte bb  = ((byte)((Integer.parseInt(result2[j][1]))&0xff));		//纵坐标低八位
//					Log.e(" Ting在Auto 中 第" + j + "个:(" + aa+"   "+ab +"   ", "   "+ba+"   "+bb+ ")");		//十六进制高低八位 整型数据

				}


				Setrevpath();

				for(int p = 0;p<revPointLength;p++){
//					Log.e("Ting 在xzdt 中的revPoint数据"," "+p+" "+revPoint[p]);		//执行到了这一步		√√√
				}

				Log.e("Ting 执行到这一步了吗","到了");

				SendPath(times);
				Log.e("Ting 执行到这一步了吗","到了");

            	cmd[1]  = tmp[1]; //要改
//				cmd[1] =  Byte.decode("0x"+Integer.toHexString(tmp[1]));

            	Log.e("Ting Send Point cmd",""+cmd[1]);
				for(int i=2;i<27;i++) {
					cmd[i] = tmp[i]; //要改
//					cmd[i] =  Byte.decode("0x"+Integer.toHexString(tmp[i]));
					Log.e("Ting Send Point cmd",""+cmd[i]);
				}
				cmd[27] = tmp[27];  //字节数组最后一位      校验位 //要改
//				cmd[27] =  Byte.decode("0x"+Integer.toHexString(tmp[27]));
				Log.e("Ting Send Point cmd",""+cmd[27]);
                Intent intent = new Intent();
                intent.putExtra("cmd",cmd);
////                intent.setAction("action.cmd");
				//设置intent的action值是action.move
				intent.setAction("action.cmd");
                sendBroadcast(intent);
            }
        });
        //新增的manualDrawMap
		manualDrawMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Auto.this,ManualDrawMap.class);
				startActivity(intent);
			}
		});



        //开启作业,启动巡航 0x04
        kqzy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	cmd[8]=0x30;
            	cmd[9]=0x34;
//				cmd[1]  = 0x01;
//				cmd[2]  = 0x00;
//    			cmd[3]  = 0x06 ;                //控制命令字节数组第4个字节命令 0x06，启动巡航
//				cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位      校验位
				Intent intent = new Intent();
				//将控制命令cmd的值添加到intent
				intent.putExtra("cmd",cmd);
				//设置intent的action值是action.cmd
				intent.setAction("action.move");
				sendBroadcast(intent);//开启广播
            }
        });

		//暂停巡航按钮 0x08
		pauseCruise.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cmd[8]=0x30;
				cmd[9]=0x38;
//				cmd[1]  = 0x01;
//				cmd[2]  = 0x00;
//				cmd[3]  = 0x07 ;                //控制命令字节数组第4个字节命令 0x07，启动巡航
//				cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位
				Intent intent = new Intent();
				//将控制命令cmd的值添加到intent
				intent.putExtra("cmd",cmd);
				//设置intent的action值是action.move
				intent.setAction("action.move");
				sendBroadcast(intent);//开启广播
			}
		});


		//跳转到手动模式界面按钮
		JumpManual.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Auto.this,Manual.class);
				startActivity(intent);//开启广播
			}
		});

		//跳转到手动模式界面按钮
		JumpHalfAuto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Auto.this,HalfAuto.class);
				startActivity(intent);//开启广播
			}
		});




		//手动停止按钮
		ManualStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cmd[8]=0x30;
				cmd[9]=0x31;
//				cmd[1]  = 0x01;
//				cmd[2]  = 0x00;                //控制命令字节数组第3个字节命令 0x11，控制手动停止
//				cmd[3]  = 0x0F;
//				cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位
				Intent intent = new Intent();
				//将控制命令cmd的值添加到intent
				intent.putExtra("cmd",cmd);
				//设置intent的action值是action.cmd
				intent.setAction("action.move");
				sendBroadcast(intent);//开启广播
			}
		});

    }

    //舍弃
    //将目标点数组转化为字符串（保留两位）发送给服务器
    public String  ChangetoString(double [][] p){
        String tmp = "#p";
        //格式化类DecimalFormat    #：代表数字，该位不存在就不显示  0：代表数字，该位不存在就显示0
        DecimalFormat df = new DecimalFormat("#.00");
        int i = 0 ;
        for(;i<p.length;i++){
            if((p[i][0]+p[i][1])!=0)
            {
                tmp += df.format(p[i][0])+","+df.format(p[i][1])+"%";
            }
        }

        return tmp;

    }

    //提取规划好的目标点数组
    public void setPoint(double[][] s){
        if(s!=null){
            this.point = s.clone();
        }
    }


/*    //传参并开始路径规划
    private  void cal(String s){new pathorg(this,s,7);}*/

	//传参并开始路径规划  新
	private  void cal(String s){
		new TrajectoryPlanning(this,s,7);
	}

	//发送地图
	public void SendPath(int times){
//		byte [] tmp = new byte[28];		//0x07 0x04 ...
		tmp[0]=0x07;
		tmp[1]=0x04;
		int num = 0;
		if(revpath!=null) {
			for (int i = 2; i < 27; i++) {
//				tmp[i] = revPoint[num];
//				num++;

				tmp[i] = revPoint[num+(times*25)];
				num++;
//				Log.e("Ting Send Point",""+tmp[i]);			//到达
			}
		}
		/*tmp[27]=(byte)(revPoint[0]+revPoint[1]+revPoint[2]+revPoint[3]+revPoint[4]+revPoint[5]+revPoint[6]+revPoint[7]+revPoint[8]+revPoint[9]+
				revPoint[10]+revPoint[11]+revPoi2nt[12]+revPoint[13]+revPoint[14]+revPoint[15]+revPoint[16]+revPoint[17]+revPoint[18]+revPoint[19]+
				revPoint[20]+revPoint[21]+revPoint[22]+revPoint[23]+revPoint[24]+revPoint[25]+revPoint[26]+revPoint[27]	);*/
		tmp[27]=(byte)(tmp[0]+tmp[1]+tmp[2]+tmp[3]+tmp[4]+tmp[5]+tmp[6]+tmp[7]+tmp[8]+tmp[9]+
				tmp[10]+tmp[11]+tmp[12]+tmp[13]+tmp[14]+tmp[15]+tmp[16]+tmp[17]+tmp[18]+tmp[19]+
				tmp[20]+tmp[21]+tmp[22]+tmp[23]+tmp[24]+tmp[25]+tmp[26]);
	}

	public void Setrevpath() {

//		StringBuilder sb = new StringBuilder();

//		Log.e("Ting","Ting");			//进入到这一步
		Log.e("Ting","length的值  2  "+length);		//19

//		for(int i = 0;(touchTimes%3!=0 ? i<=touchTimes/3:i<touchTimes/3);i++){		//对
//		for(int i = 0;i<=(touchTimes/3);i++){										//错


		//一包数据有三个点
		//length为8时，3个包。length为9时，3个包
		for(int i = 0;(length%3!=0 ? i<=length/3:i<length/3);i++){
//		for(int i = 0;i<=(length/3);i++){
			//可以通过数组的长度来进行判断。if(length%3==2)			//if(length%3==1)
			if((length-i*3)/3==0){			//最后一包数据
				if(length%3==1){			//最后一包数据只有一个点
						t = length / 3;
						revpath = new byte[]{(byte)(3 * t),a1a1[3 * t],a1b1[3 * t],b1a1[3 * t],b1b1[3 * t],0x0F,0x37,0x01, 0x00,
								(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
								(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,0x00,0x00,0x00,

								/*0x07,0x04,(byte)(3 * t),a1a1[3 * t],a1b1[3 * t],b1a1[3 * t],b1b1[3 * t],0x0F,0x37,0x01, 0x00,
								(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
								(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,0x00,0x00,0x00,
								(byte)(0x07+0x04+(byte)(3*i)+a1a1[3*i]+a1b1[3*i]+b1a1[3*i]+b1b1[3*i]+0x0F+0x37+0x01+0x00+
										(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+
										(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+0x00+0x00+0x00)*/
						};
				}else if(length%3==2) {
						t = length / 3;
						revpath = new byte[]{(byte) (3 * t), a1a1[3 * t], a1b1[3 * t], b1a1[3 * t], b1b1[3 * t], 0x0F, 0x37, 0x01, 0x00,
								a1a1[3 * t + 1], a1b1[3 * t + 1], b1a1[3 * t + 1], b1b1[3 * t + 1], 0x0F, 0x37, 0x01, 0x00,
								(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00,

								/*0x07, 0x04, (byte) (3 * t), a1a1[3 * t], a1b1[3 * t], b1a1[3 * t], b1b1[3 * t], 0x0F, 0x37, 0x01, 0x00,
								a1a1[3 * t + 1], a1b1[3 * t + 1], b1a1[3 * t + 1], b1b1[3 * t + 1], 0x0F, 0x37, 0x01, 0x00,
								(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00,
								(byte) (0x07 + 0x04 + (byte) (3 * i) + a1a1[3 * i] + a1b1[3 * i] + b1a1[3 * i] + b1b1[3 * i] + 0x0F + 0x37 + 0x01 + 0x00 +
										a1a1[3 * i + 1] + a1b1[3 * i + 1] + b1a1[3 * i + 1] + b1b1[3 * i + 1] + 0x0F + 0x37 + 0x01 + 0x00 +
										(byte) 0xFF + (byte) 0xFF + (byte) 0xFF + (byte) 0xFF + (byte) 0xFF + 0x00 + 0x00 + 0x00)*/
						};
				}else if(length%3==0) {
					t = length / 3;
					revpath = new byte[]{(byte) (3 * t), a1a1[3 * t], a1b1[3 * t], b1a1[3 * t], b1b1[3 * t], 0x0F, 0x37, 0x01, 0x00,
							a1a1[3 * t + 1], a1b1[3 * t + 1], b1a1[3 * t + 1], b1b1[3 * t + 1], 0x0F, 0x37, 0x01, 0x00,
							a1a1[3 * t + 2], a1b1[3 * t + 2], b1a1[3 * t + 2], b1b1[3 * t + 2], 0x0F, 0x37, 0x01, 0x00,

							/*0x07, 0x04, (byte) (3 * t), a1a1[3 * t], a1b1[3 * t], b1a1[3 * t], b1b1[3 * t], 0x0F, 0x37, 0x01, 0x00,
							a1a1[3 * t + 1], a1b1[3 * t + 1], b1a1[3 * t + 1], b1b1[3 * t + 1], 0x0F, 0x37, 0x01, 0x00,
							(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00,
							(byte) (0x07 + 0x04 + (byte) (3 * i) + a1a1[3 * i] + a1b1[3 * i] + b1a1[3 * i] + b1b1[3 * i] + 0x0F + 0x37 + 0x01 + 0x00 +
									a1a1[3 * i + 1] + a1b1[3 * i + 1] + b1a1[3 * i + 1] + b1b1[3 * i + 1] + 0x0F + 0x37 + 0x01 + 0x00 +
									(byte) 0xFF + (byte) 0xFF + (byte) 0xFF + (byte) 0xFF + (byte) 0xFF + 0x00 + 0x00 + 0x00)*/
					};
				}
			} else {
				revpath = new byte[]{(byte)(3*i),a1a1[3*i],a1b1[3*i],b1a1[3*i],b1b1[3*i],0x0F,0x37,0x01,0x00,
						a1a1[3*i+1],a1b1[3*i+1],b1a1[3*i+1],b1b1[3*i+1],0x0F,0x37,0x01,0x00,
						a1a1[3*i+2],a1b1[3*i+2],b1a1[3*i+2],b1b1[3*i+2],0x0F,0x37,0x01,0x00,

						/*0x07,0x04,(byte)(3*i),a1a1[3*i],a1b1[3*i],b1a1[3*i],b1b1[3*i],0x0F,0x37,0x01,0x00,
						a1a1[3*i+1],a1b1[3*i+1],b1a1[3*i+1],b1b1[3*i+1],0x0F,0x37,0x01,0x00,
						a1a1[3*i+2],a1b1[3*i+2],b1a1[3*i+2],b1b1[3*i+2],0x0F,0x37,0x01,0x00,
						(byte)(0x07+0x04+(byte)(3*i)+a1a1[3*i]+a1b1[3*i]+b1a1[3*i]+b1b1[3*i]+0x0F+0x37+0x01+0x00+
								a1a1[3*i+1]+a1b1[3*i+1]+b1a1[3*i+1]+b1b1[3*i+1]+0x0F+0x37+0x01+0x00+
								a1a1[3*i+2]+a1b1[3*i+2]+b1a1[3*i+2]+b1b1[3*i+2]+0x0F+0x37+0x01+0x00)*/
				};
			}

			for(int x = 0;x<25;x++){
//				Log.e("Ting 在Auto中的revpath数据"," "+x+" "+revpath[x]);//执行到了这一步
//				revPoint[];
				s = s + revpath[x]+" ";

//				sb.append(revpath[x]+" ");
			}

/*			for(int x = 0;x<28;x++){
//				Log.e("Ting 在Auto中的revpath数据"," "+x+" "+revpath[x]);//执行到了这一步
//				revPoint[];
				s = s + revpath[x]+" ";

//				sb.append(revpath[x]+" ");
			}*/

		}


		String[] Point = stringToArray(s);

		for(int p = 0;p<Point.length;p++){
			revPointLength = Point.length;
			revPoint[p] = (byte)(Integer.parseInt(Point[p]));							//得出revPoint字节数组的值
			Log.e("Ting 在Auto中的revPoint数据"," "+p+" "+revPoint[p]);		//执行到了这一步		√√
		}

	}




	//将字符串数组变成二维字符串数组
	public String[][] stringToArray2(String[] numStr) {
		for (int i = 0,j =0;i<numStr.length;i++,j=i/2){
			if(i%2==0)
			{
				str_arr2[j][0] = numStr[i];
				Log.e("Ting Auto readFile ","get File stringToArray result2 x "+str_arr2[j][0]);	//可以到达
			} else{
				str_arr2[j][1] = numStr[i];
				Log.e("Ting Auto readFile ","get File stringToArray result2 y "+str_arr2[j][1]);	//可以到达
			}
		}
		return str_arr2;
	}


	//将字符串变成字符串数组
	public static String[] stringToArray(String numStr) {
		String [] str_arr = numStr.split(SPACE_SEPARATOR);
		for (int i = 0;i<str_arr.length;i++){
//			Log.e("Ting Auto readFile ","get File stringToArray result1 "+str_arr[i]);	//可以到达
		}
		return str_arr;
	}



	//打开指定文件，读取其数据，返回字符串对象 new
	public String readFileData(String fileName){
		String result="";
		try{
			FileInputStream fis = openFileInput(fileName);
			//获取文件长度
			int lenght = fis.available();
			byte[] buffer = new byte[lenght];
			fis.read(buffer);
			//将byte数组转换成指定格式的字符串
			result = new String(buffer, "UTF-8");
//			Log.e("Ting Auto readFile Mess","get File content "+result);	//可以到达

		} catch (Exception e) {
			e.printStackTrace();
		}
		return  result;
	}

	//向指定的文件中写入指定的数据 new
	public void writeFileData(String filename, String content) {
		try {
			//不存在会自动创建
			FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);//获得FileOutputStream
			//将要写入的字符串转换为byte数组
			byte[]  bytes = content.getBytes();
			fos.write(bytes);//将byte数组写入文件
			Log.e("Ting Auto Message","get File content");	//可以到达
			fos.close();//关闭文件输出流
			Log.e("Ting Auto Message","get File content ok");	//可以到达
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	//07 01 00 26 97 23 6E 0F 37 01 00
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//获取intent中的action值
			String action = intent.getAction();
			if(action!=null&&action.equals("action.Info")){
//				initpoint = intent.getExtras().getString("info");
				info = intent.getExtras().getByteArray("info");
				Log.e("Ting Auto Send Point","get Point info");	//已经到达
				//这是收到地图数据，船返回的数据。先注释掉，以免和手动绘制路线的发生冲突		ManualDrawMap

				if(info!=null&&info[0]==0x07&&info[1]==0x04/*&&info[2]==0x03*/) { //要改
					times++;
					SendPath(times);
					Log.e("Ting Auto Send Point","get Point");	//没有到达
//需要解开这个注释
					cmd[1]  = tmp[1];  //要改
//				cmd[1] =  Byte.decode("0x"+Integer.toHexString(tmp[1]));

					Log.e("Ting Send Point cmd",""+cmd[1]);
					for(int i=2;i<27;i++) {
						cmd[i] = tmp[i]; //要改
//					cmd[i] =  Byte.decode("0x"+Integer.toHexString(tmp[i]));
						Log.e("Ting Send Point cmd",""+cmd[i]);
					}
					cmd[27] = tmp[27];  //字节数组最后一位      校验位//要改
//				cmd[27] =  Byte.decode("0x"+Integer.toHexString(tmp[27]));
					Log.e("Ting Send Point cmd",""+cmd[27]);
					Intent intent2 = new Intent();
					intent2.putExtra("cmd",cmd);
////                intent.setAction("action.cmd");
					//设置intent的action值是action.move
					intent2.setAction("action.cmd");
					sendBroadcast(intent2);

				}

			}else if(action.equals("action.Path")){
				//提取数据包中的数据并转化为平面坐标
				path = (TwoBean) intent.getExtras().get("path");
				short [][] revpoint = path.data.clone();
				for(int i=0;i<34;) {
					Log.i("msg", "x:" + String.valueOf(revpoint[i][0]) + ",y:" + String.valueOf(revpoint[i++][1]));
				}
			}
		}
	};





    //事无巨细，报错信息一定要解决
    //关闭广播
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }

    //ProgressDialog pd1;       进度对话框           疯狂Android讲义    p159
    //显示对话框里的进度条不显示进度值      没有用到
    public void showIndeterminate(View source)
    {
        pd1 = new ProgressDialog(this);
        // 设置对话框的标题
        pd1.setTitle("任务正在执行中");
        // 设置对话框显示的内容
        pd1.setMessage("任务正在执行中，敬请等待...");
        // 设置对话框能用“取消”按钮关闭
        pd1.setCancelable(true);
        // 设置对话框的进度条风格
        pd1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置对话框的进度条是否显示进度
        pd1.setIndeterminate(true);
        pd1.show();
    }

    //显示环形进度条  no use
    public void showSpinner(View source)
    {
        // 调用静态方法显示环形进度条
        ProgressDialog.show(this, "上传数据"
                , "轨迹数据更新中，请等待", false, true);
    }

    //显示对话框  no use
    private void ShowDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("信息提示")
                .setMessage("获取成功！");

        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }



    public void initviews(){
        jiance = findViewById(R.id.imageButton7);
        lujing = findViewById(R.id.imageButton8);
        //setp  = findViewById(R.id.setprim);         		//设置原点
        //conp = findViewById(R.id.confprim);         		//确认原点
        jzp   = findViewById(R.id.jzprim);          		//原点校正
        gjgh = findViewById(R.id.gjgh);             		//轨迹规划
        xzdt   = findViewById(R.id.dtxz);           		//下载地图
        kqzy = findViewById(R.id.startwork);        		//开启作业
		JumpManual = findViewById(R.id.JumpManual);        	//跳转到手动模式界面
		JumpHalfAuto = findViewById(R.id.JumpHalfAuto);     //跳转到手动模式界面
		ManualStop = findViewById(R.id.ManualStop);        	//手动停止
		pauseCruise = findViewById(R.id.pauseCruise);		//巡航暂停
		manualDrawMap = findViewById(R.id.manualDrawMap);	//手动绘制地图

//        cmd = new byte[28];
        cmd = new byte[1032];
        cmd[0]=0x07;

//        revpath = new byte[28];
		revpath = new byte[1032];

		str_arr2= new String[50][2];

		a1a1 = new byte[50];
		a1b1 = new byte[50];
		b1a1 = new byte[50];
		b1b1 = new byte[50];

		revPoint = new byte[500];

//		tmp = new byte[28];		//0x07 0x04 ...
        tmp = new byte[1032];
//		info  = new byte[28];//放平面坐标的字节数组
        info = new byte[1032];
    }
}
