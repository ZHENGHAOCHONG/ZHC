package com.example.simplenewsystem;



import android.util.Log;
import java.io.OutputStream;
public class SendThread implements Runnable {
	private OutputStream out;
	private ClientThread father;
	SendThread(OutputStream out,ClientThread father){
		super();
		this.out = out;
		this.father=father;
	}
	@Override
	public void run() {
		while(true){
			try {
				byte [] cmd = father.queue.take();			//提取ClientThread父类的queue队列信息
				Log.i("msg", "queue take is successful");
				out.write(cmd);					//写出cmd命令
			} catch (Exception e){
				break;
			}
		}	
	}

}
