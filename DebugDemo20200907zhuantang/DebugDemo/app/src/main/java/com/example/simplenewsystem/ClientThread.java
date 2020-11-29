package com.example.simplenewsystem;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;

//安卓第七天笔记--网络编程一
//https://blog.csdn.net/ko0491/article/details/50816330
//子线程

//疯狂Android讲义  p571 多线程

public class ClientThread implements Runnable
{

  // 定义向UI线程发送消息的Handler对象
  private Handler handler;
  // 定义接收UI线程的消息的Handler对象
  public Handler revHandler;
  // 该线程所处理的Socket所对应的输入流
  BufferedReader br = null;
  OutputStream os = null;
  InputStream is = null;

  //同步Queue，属于线程安全的BlockingQueue的一种，此队列设计的理念类似于“单工模式”，对于每个put/offer操作，必须等待一个take/poll操作，类似于我们的现实生活中的“火把传递”：一个火把传递地他人，需要2个人“触手可及”才行。
  SynchronousQueue<byte []> queue;        //同步队列  SynchronousQueue是一个没有数据缓冲的BlockingQueue


  private SocketStatus status = null;

  public ClientThread(Handler handler)
  {

    this.handler = handler;
    queue = new SynchronousQueue< >();
    status = new SocketStatus();

  }

  @Override

  public void run() {
    while(true){

      try{

        //使用域名连接
 /*
        String socketAddress ="n2399532o6.qicp.vip";           //n2399532o6.qicp.vip    域名
        InetAddress netAddress = InetAddress.getByName(socketAddress);//产生IP地址

        //建立连接到远程服务器的Socket
       Socket s = new Socket(netAddress,30000);//手机是30000
 */



                //使用固定ip连接
                //建立连接到远程服务器的Socket
                //Socket s = new Socket("218.93.179.90",30000);//金坛固定ip

	  Socket s = new Socket("119.45.26.207",10000);//工作室变化ip
           //     Socket s = new Socket("101.132.153.75",5851);//阿里云ip,手机端口号5851


        boolean flag = true;

        //将Socket对应的输入流包装成BuferedReader
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        os = s.getOutputStream();   //返回该Socket对象对应的输出流，让程序通过该输出流向Socket中输出数据
        is = s.getInputStream();    //返回该Socket对象对应的输入流，让程序通过该输入流从Socket中取出数据


        SendThread send = new SendThread(os,this);      //新建一个SendThread类对象send
        RevThread  rev  = new RevThread(s,handler);              //新建一个RevThread类对象rev

        new Thread(send).start();       //启动一条线程，用于发送
        new Thread(rev).start();        //启动一条线程，用于接收

        // 为当前线程初始化Looper
        Looper.prepare();

        // 创建revHandler对象
        revHandler = new Handler()
        {
          @Override
          public void handleMessage(Message msg)
          {

            // 接收到UI线程中用户输入的控制信号
            if (msg.what == 0x345)
            {
              // 将用户在UI线程内输入的控制信号（数据）写入网络即发送给服务器（重要）
              try
              {
                //往同步队列中添加cmd字节数组
                //msg.getData().getByteArray("response")
                // 获取传递过来的Message中的数据集合，从数据集合中获取key为＂cmd＂的值，这个值是一个字节数组
                queue.put(msg.getData().getByteArray("cmd"));
              }
              catch (Exception e)
              {
                e.printStackTrace();
              }
            }
            if(msg.what==0x789){
              try
              {
                //往同步队列中添加response字节数组
                //msg.getData().getByteArray("response")
                // 获取传递过来的Message中的数据集合，从数据集合中获取key为＂response＂的值，这个值是一个字节数组
                queue.put(msg.getData().getByteArray("response"));
              }
              catch (Exception e)
              {
                e.printStackTrace();
              }
            }
          }
        };
        // 启动Looper
        Looper.loop();



        while(flag) {
          try {
            s.sendUrgentData(0);        //发送心跳包
            status.setSocket(s);
            Thread.sleep(1000);
          }
          catch(Exception e) {

            s.shutdownInput();          //关闭输入
            s.shutdownOutput();         //关闭输出
            s.close();
            status.setSocket(null);
            flag = false;
          }
        }

      }catch (Exception e){
        e.printStackTrace();
      }


    }
  }
}
