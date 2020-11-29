package com.example.simplenewsystem;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.*;
import java.net.Socket;



public class RevThread implements Runnable {

	private Handler handler;			//消息分发对象，工作主要包含发送和接收过程，就是解决线程和线程之间的通信的。
	private Socket s ;
	private byte [] info;				//28字节info数组


	public RevThread(Socket s, Handler handler) {
		super();
		this.s = s;
		this.handler = handler;
	}

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
	
	@Override
	public void run() {
		while(true){
			try {
//				info = new byte[28];
				info = new byte[1032];

				//接收服务器数据
				DataInputStream in = new DataInputStream(s.getInputStream());
				int i =in.read(info);//read()是in的方法，每次读取一个字节的二进制数据
				if(info != null&&info[0]=='A'&&info[1]=='A'&&info[0]=='A'){
					switch((info[8]-0x30)*16+(info[9]-0x30)){
						case 0x00://空闲指令
							info[8]=0x30;
							info[9]=0x30;
						case 0x01://停车指令
						case 0x02://手动控制
						case 0x03://半自动
						case 0x04://投饵自动巡航
						case 0x05://投饵自动续航
						case 0x06://施药自动巡航
						case 0x07://施药自动续航
						case 0x08://巡航暂停控制
						case 0x09://返回控制
						case 0x10://原点校正
						case 0x11://地磁校准
						case 0x12: //地图下载
							Log.i("msg", "rev :实时参数" );
								Bundle data = new Bundle();//新建数据包data
								//将表示指定字节数组的字符串与此首选项节点中指定的键相关联
								data.putByteArray("info",info);//将info字节数组与键info相关联，放入data数据包中
								Message msg = new Message();
								msg.what = 0x123;
								msg.setData(data);
								handler.sendMessage(msg);		//handler把msg发送出去
								this.info=null;
								break;
	/*					case 0x04: //地图下载
								Log.i("msg","规划好的数据包:" + bytesToHexString(info));
								Bundle data2 = new Bundle();
								data2.putByteArray("info",info);
								Message msg2 = new Message();
								msg2.what = 0x789;
								msg2.setData(data2);
								handler.sendMessage(msg2);
								this.info=null;
								break;*/
					}

				}
			} catch (IOException e) {
				break;
			}
		}

	}

}
