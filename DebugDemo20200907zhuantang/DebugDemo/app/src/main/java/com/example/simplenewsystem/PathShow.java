package com.example.simplenewsystem;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import static com.example.simplenewsystem.System.pxpy;

/**
 * Created by Administrator on 2019-04-03.
 */

//类继承了AppCompatActivity会显示标题栏。如果直接继承的是Activity不会出现标题栏
public class PathShow extends AppCompatActivity {

    short [][] revpoint;        //存放接收到的平面横纵坐标
    DrawView drawView;
    RelativeLayout relative;    //相对布局

	static int Width;			//手机分辨率的宽度
	static int Height;			//手机分辨率的高度

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //WindowManager主要用来管理窗口的一些状态、属性、view增加、删除、更新、窗口顺序、消息收集和处理等。
        WindowManager wm = this.getWindowManager();//获取窗口管理器

        Init();

        //1.创建一个相对布局relative并设置属性
        relative = new RelativeLayout(this);
        relative.setBackgroundColor(Color.YELLOW);
        //设置RelativeLayout.LayoutParams参数
        RelativeLayout.LayoutParams pp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //通过WindowManager中的Display获取屏幕大小
        pp.width  = wm.getDefaultDisplay().getWidth();
        pp.height = wm.getDefaultDisplay().getHeight();

        Log.i("像素", " pp.width: "+ pp.width);
		Log.i("像素", " pp.width: "+ pp.height);


		Width  = pp.width;
		Height = pp.height;

        relative.setLayoutParams(pp);

        //在RelativeLayout中添加TextView
        //2.创建一个TextView并设置属性，添加到RelativeLayout中
        TextView text = new TextView(this);
        //相对的控件如果是new出来的TextView text = new TextView(context);，需要调用setId()设置ID，text.setId(Integer.MAX_VALUE - 1000);，否则不生效。
        text.setId(Integer.MAX_VALUE - 1000);
        text.setTextSize(16);
        text.setText("经纬度位置：");
        text.setPadding(3,3,3,3);
        relative.addView(text);

        //3.创建一个自定义的DrawView类，设置属性并添加到RelativeLayout中
        drawView = new DrawView(this,100,300 ,revpoint);

        //LayoutParams 可以在代码中指定view 相对于父view的位置，相当于xml 里的layout_gravity
        RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //添加相应的规则
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        lp.addRule(RelativeLayout.BELOW, text.getId());
        drawView.setLayoutParams(lp);
        relative.addView(drawView);

        setContentView(relative);

        IntentFilter intentFilter2 = new IntentFilter();//创建IntentFilter
        intentFilter2.addAction("action.Info");//指定BroadcastReceiver监听的Action
        //注册广播
        registerReceiver(mRefreshBroadcastReceiver, intentFilter2);
    }

    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取intent中的action值
            String action = intent.getAction();
            if(action!= null&&action.equals("action.Info")){

         /*
                byte [] latlng = intent.getExtras().getByteArray("info");
                short dlat = Exchange(latlng,5);//将5,6两个字节的十六进制数转换为十进制数
                short dlng = Exchange(latlng,7);//将7,8两个字节的十六进制数转换为十进制数
                Log.i("msg", "PathShow.dlat: "+String.valueOf(dlat));//显示平面纵坐标
                Log.i("msg", "PathShow.dlng: "+String.valueOf(dlng));//显示平面横坐标
		*/
				Log.i("msg", "PathShow....pxpy.size: "+pxpy.size());
				for(int i = 0;i< pxpy.size()-1;){

					Log.i("msg", "PathShow.pxpy.x: "+pxpy.get(i));
					Log.i("msg", "PathShow.pxpy.y: "+pxpy.get(i+1));
					drawView.setArray(pxpy.get(i),pxpy.get(i+1));

					i=i+2;
					//pxpy.size=+2;

				}


				//调用DrawView里的setArray方法，进行轨迹绘画
				//drawView.setArray(dlat,dlng);
				Log.i("msg", "rev realtime");


            }
        }
    };

    //返回一个十进制数
    public short Exchange(byte[] p ,int i){
       return getShort(p,i);
    }

    //将两字节16进制数转化为十进制数
    public static short getShort(byte[] arr, int index) {
        return (short) (0xff00 & arr[index] << 8 | (0xff & arr[index + 1]));
    }

    private void Init(){
        //TwoBean twoBean = (TwoBean)this.getIntent().getExtras().get("path");

        //TwoBean类新建的对象(名为twoBean)中的二维数组名为data
        //revpoint = twoBean.data.clone();


		short[][] revpoint = {{9996,9926},{9879,9070},{10066,9051},{10230,9850},
				{9996,9926},{9879,9070},{10066,9051},{10230,9850},
				{9996,9926},{9879,9070},{10066,9051},{10230,9850},{10036,9886}};


		/*
        revpoint[0][0] = 9996;revpoint[0][1] = 9926;
        revpoint[1][0] = 9879;revpoint[1][1] = 9070;
        revpoint[2][0] = 10066;revpoint[2][1] = 9051;
        revpoint[3][0] = 10230;revpoint[3][1] = 9850;
        revpoint[4][0] = 9996;revpoint[4][1] = 9926;
        revpoint[5][0] = 9879;revpoint[5][1] = 9070;
        revpoint[6][0] = 10066;revpoint[6][1] = 9051;
        revpoint[7][0] = 10230;revpoint[7][1] = 9850;
        revpoint[8][0] = 9996;revpoint[8][1] = 9926;
        revpoint[9][0] = 9879;revpoint[9][1] = 9070;
        revpoint[10][0] = 10066;revpoint[10][1] = 9051;
        revpoint[11][0] = 10230;revpoint[11][1] = 9850;
        revpoint[12][0] = 10036;revpoint[12][1] = 9886;
		*/




		/*
        for(int i=0;i<34;) {
            //显示x: revpoint[i][0])   y: revpoint[i++][1]       x: revpoint[0][0]) y: revpoint[0][1]    x: revpoint[1][0]) y: revpoint[1][1]...
            Log.i("msg", "PathShow message point: x:" + String.valueOf(revpoint[i][0]) + ",y:" + String.valueOf(revpoint[i++][1]));
        }
        */
    }

    //没有用到
    public void DangerShow(){

        //加载布局文件
        TableLayout buchongerl = (TableLayout)getLayoutInflater().inflate(R.layout.shishilujing,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("路径已经收到")
                .setIcon(R.drawable.danger)
                .setView(buchongerl);

        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();

    }

    //关闭广播
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }
}
