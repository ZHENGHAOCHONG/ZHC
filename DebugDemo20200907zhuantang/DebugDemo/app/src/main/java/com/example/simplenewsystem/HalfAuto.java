package com.example.simplenewsystem;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;


/*
 ----------------- 鉴权错误信息 ------------
    sha1;package:C8:AA:55:FD:32:04:88:33:21:90:F0:8E:98:DC:37:6F:E4:11:69:B9;com.example.debugdemo
    key:3vFyWlovxNUfNw6t34EQGQoCZUTG4bZU
    errorcode: 230 uid: -1 appid -1 msg: APP Mcode码校验失败
    请仔细核查 SHA1、package与key申请信息是否对应，key是否删除，平台是否匹配
    errorcode为230时，请参考论坛链接：
    http://bbs.lbsyun.baidu.com/forum.php?mod=viewthread&tid=106461

* */





public class HalfAuto extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

	Button JumpAuto;
	Button JumpManual;
	Button Stop;



	private Spinner spinner1;
	private Spinner spinner2;

	private List<String> data_list1;
	private List<String> data_list2;

	private ArrayAdapter<String> arr_adapter1;
	private ArrayAdapter<String> arr_adapter2;


	byte[] temp;
	byte[] cmd;
	byte[] info;

	static int speedPaoPan = 0;					//把speedPaoPan静态化，使得函数内部也可以使用。speedPaoPan是权限选择的值
	static int speedZhenDong = 0;				//把speedZhenDong静态化，使得函数内部也可以使用。speedZhenDong是权限选择的值


	byte x3;//半自动，x坐标的高位
	byte x4;//半自动，x坐标的低位
	byte y3;//半自动，y坐标的高位
	byte y4;//半自动，y坐标的低位



	private MapView mMapView = null;
	private BaiduMap mBaiduMap;//mBaiduMap绘制障碍点



	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
		SDKInitializer.initialize(this.getApplicationContext());
//		SDKInitializer.initialize(this);
		//自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
		//包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
		SDKInitializer.setCoordType(CoordType.BD09LL);


		setContentView(R.layout.banzidong);
		init();

		/*locationManager = (LocationManager) getSystemService(Context
				.LOCATION_SERVICE);
		mapView = (MapView) findViewById(R.id.map);
		// 必须回调MapView的onCreate()方法
		mapView.onCreate(savedInstanceState);
		init();*/


        //new20201013  这段代码放在这里就可以，放在下面就不可？？20201022改
		temp[0] = (byte) (((cmd[12] - 0x30) * 1000) + ((cmd[13] - 0x30) * 100) + ((cmd[14] - 0x30) * 10) + (cmd[16] - 0x30));
		if (cmd[11]=='E') temp[0]= (byte) (10000+temp[0]);
		if (cmd[11]=='W') temp[0]= (byte) (10000-temp[0]);
		temp[1] = (byte) ((cmd[19]-0x30)*1000+(cmd[20]-0x30)*100+(cmd[21]-0x30)*10+(cmd[23]-0x30));
		if (cmd[18]=='N') temp[1] = (byte) (10000+temp[0]);
		if (cmd[18]=='S') temp[1] = (byte) (10000-temp[1]);
		temp[2] = (byte) ((cmd[25]-0x30)*100+(cmd[26]-0x30)*10+(cmd[28]-0x30));
		temp[3] = (byte) ((cmd[30]-0x30)*100+(cmd[31]-0x30)*10+(cmd[33]-0x30));
		temp[4] = (byte) ((cmd[35]-0x30)*100+(cmd[36]-0x30)*10+(cmd[38]-0x30));
		temp[5] = (byte) ((cmd[40]-0x30)*100+(cmd[41]-0x30)*10+(cmd[43]-0x30));


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
		spinner1.setOnItemSelectedListener(HalfAuto.this);


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
		spinner2.setOnItemSelectedListener(HalfAuto.this);



		//获取地图控件引用
		mMapView = (MapView) findViewById(R.id.map);



		mBaiduMap = mMapView.getMap();
		/*mBaiduMap1 = mMapView.getMap();*/
		//普通地图 ,mBaiduMap是地图控制器对象
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		/*mBaiduMap1.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
*/

		//x,y是经纬度，f为缩放等级（4-20之间）    31.62320018, 119.64296511 金坛经纬度
		MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(new com.baidu.mapapi.model.LatLng(32.201699, 119.528558), 20);////31.61938459，119.63149220,
		mBaiduMap.animateMapStatus(update);
		/*mBaiduMap1.animateMapStatus(update);*/


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



		Bitmap bitmap = zoomImg(BitmapFactory.decodeResource(getResources(), R.drawable.forbid), 40, 40);
		BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
		com.baidu.mapapi.model.LatLng point = new com.baidu.mapapi.model.LatLng(31.62320018, 119.64296511);//最东面的水质检测仪

		final OverlayOptions option = new MarkerOptions().position(point).icon(descriptor);

		com.baidu.mapapi.model.LatLng point1 = new LatLng(31.62340018, 119.64268211);//中间的水质检测仪

		final OverlayOptions option1 = new MarkerOptions().position(point1).icon(descriptor);

		mBaiduMap.addOverlay(option);
		mBaiduMap.addOverlay(option1);



		//底图标注
		mBaiduMap.showMapPoi(false);//隐藏底图标注，这里隐藏的是地名和河流名
		/*mBaiduMap1.showMapPoi(false);//隐藏底图标注，这里隐藏的是地名和河流名*/



		//接收实时参数
		IntentFilter intentFilter = new IntentFilter();//创建IntentFilter
		intentFilter.addAction("action.Info");//指定BroadcastReceiver监听的Action
		//注册广播，接收System广播的实时参数
		registerReceiver(mRefreshBroadcastReceiver, intentFilter);
		//注册应用内广播接收器


		//跳转到自动模式界面按钮
		JumpAuto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HalfAuto.this,Auto.class);
				startActivity(intent);//开启广播
			}
		});



		//跳转到手动模式界面按钮
		JumpManual.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HalfAuto.this,Manual.class);
				startActivity(intent);//开启广播
			}
		});


		//手动停止按钮
		Stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cmd[8]= 0x30;
				cmd[9]= 0x31;
//				cmd[3]  = 0x0F;                //控制命令字节数组第4个字节命令0x0F，控制手动停止
//				cmd[27] = (byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]+cmd[7]+cmd[8]+cmd[9]);  //字节数组最后一位
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


/*
		//此处和手动模式很像，只是得将手点的位置和实际的位置的平面坐标进行对应

		// 获取布局文件中的LinearLayout容器，手动模式关键区域
		LinearLayout root = findViewById(R.id.root);
		// 创建DrawView组件
		final DrawView1 draw = new DrawView1(this);

		// 设置自定义组件的最小宽度、高度
		draw.setMinimumWidth(0);
		draw.setMinimumHeight(0);
		//为drawview组件绑定tounch事件
		draw.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				draw.currentX=event.getX();
				draw.currentY=event.getY();
				Log.e("半自动", "currentX:"+draw.currentX+"currentY:"+draw.currentY);



				return true;
			}
		});

		root.addView(draw);*/



/*
		//定义Maker坐标点
		LatLng point = new LatLng(39.963175, 116.400244);
		//构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
        		.fromResource(R.drawable.icon_marka);
		//构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions()
        						.position(point)
        						.icon(bitmap);
		//在地图上添加Marker，并显示
		mBaiduMap.addOverlay(option);




		//点击Marker时会回调BaiduMap.OnMarkerClickListener，监听器的实现方式示例如下：
		mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
			//marker被点击时回调的方法
			//若响应点击事件，返回true，否则返回false
			//默认返回false
			@Override
			public boolean onMarkerClick(Marker marker) {
				return true;
			}
		});*/


		// 设置marker图标
		final Bitmap bitmap1 = zoomImg(BitmapFactory.decodeResource(getResources(), R.drawable.icon_gcoding), 40, 40);
		final BitmapDescriptor descriptor1 = BitmapDescriptorFactory.fromBitmap(bitmap1);
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

				Log.e("半自动--经纬度", "currentX:"+latitude+"currentY:"+longitude);//输出经纬度值

				//将经纬度转换为平面坐标，例子：currentX:3503132.978473543    currentY:2.075075058647018E7

				Point pa = BLtoxy(longitude,latitude);


				double x = pa.getX();		//3503092.3016042993
				double y = pa.getY();		//2.07507268389166E7
				Log.e("半自动--高斯克吕哥公式，平面坐标", "currentX:"+x+"currentY:"+y);


				//注意，x,y进行转换，例子：currentX:207507268    currentY:35030923
				long x1 = Math.round(y*10);//横坐标，单位dm
				long y1 = Math.round(x*10);//纵坐标，单位dm

				Log.e("半自动--换算成分米", "currentX:"+x1+"currentY:"+y1);


				//坐标转换，例子，currentX:9800      currentY:9618
				int x2 = (int)(x1-207497468);
				int y2 = (int)(y1-35021305);

				Log.e("半自动--相对坐标", "currentX:"+x2+"currentY:"+y2);


				//2648		2592
				x3 = (byte)(((java.lang.Short.decode("0x"+Integer.toHexString(x2)))&0xff00)>>8);	//38
				x4 = (byte)((java.lang.Short.decode("0x"+Integer.toHexString(x2)))&0xff);			//72

				y3 = (byte)(((java.lang.Short.decode("0x"+Integer.toHexString(y2)))&0xff00)>>8);	//37
				y4 = (byte)((java.lang.Short.decode("0x"+Integer.toHexString(y2)))&0xff);			//-110

				Log.e("半自动--相对坐标--十六进制", "currentX:"+"x3="+x3+"x4="+x4+"currentY:"+"y3="+y3+"y4="+y4);


				//弹出一个对话框
				AlertDialog.Builder dialog = new AlertDialog.Builder(HalfAuto.this);
				dialog.setTitle("提示");
				dialog.setMessage("确定要以该点作为终点吗？");
				dialog.setCancelable(false);


				dialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						UIUtils.showToast( HalfAuto.this , "You clicked OK" );

						cmd[3] = 0x02;  //这里的都要改一下
						cmd[4] = x3;
						cmd[5] = x4;
						cmd[6] = y3;
						cmd[7] = y4;
						cmd[8] = (byte)(java.lang.Byte.decode(("0x"+Integer.toHexString(speedPaoPan)))+0x64);//抛盘电机
						cmd[9] = (byte)(java.lang.Byte.decode(("0x"+Integer.toHexString(speedZhenDong)))+0x64);//振动电机

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
					public void onClick(DialogInterface dialogInterface, int i) {
						UIUtils.showToast( HalfAuto.this , "You clicked No" );
					}
				});
				dialog.show();


				//先清除图层，清空图层
				mBaiduMap.clear();


				// 定义Maker坐标点
				LatLng latLng1 = new LatLng(latitude, longitude);
				// 构建MarkerOption，用于在地图上添加Marker
				MarkerOptions options = new MarkerOptions().position(latLng1).icon(descriptor1);
				// 在地图上添加Marker，并显示
				mBaiduMap.addOverlay(options);
				mBaiduMap.addOverlay(option);//重新绘制水质检测仪障碍点
				mBaiduMap.addOverlay(option1);//重新绘制水质检测仪障碍点

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
		mBaiduMap.setOnMapClickListener(listener);




	/*	//此处和手动模式很像，只是得将手点的位置和实际的位置的平面坐标进行对应

		// 获取布局文件中的LinearLayout容器，手动模式关键区域
		LinearLayout root = findViewById(R.id.root);
		// 创建DrawView组件
		final DrawView1 draw = new DrawView1(this);

		// 设置自定义组件的最小宽度、高度
		draw.setMinimumWidth(0);
		draw.setMinimumHeight(0);
		//为drawview组件绑定tounch事件
		draw.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				draw.currentX=event.getX();
				draw.currentY=event.getY();
				Log.e("半自动", "currentX:"+draw.currentX+"currentY:"+draw.currentY);

				//draw.invalidate();


				return true;
			}
		});

		root.addView(draw);
*/


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
	protected void onResume() {
		super.onResume();
		//在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
		//在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}





	//Android中所有与观察者模式有关的设计中，一旦涉及到register，必定在相应的时机需要unregister
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mRefreshBroadcastReceiver);


		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();

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




	//广播接收器
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();//获取intent中的action值
			if(action!=null&&action.equals("action.Info")){
				info = intent.getExtras().getByteArray("info");
				if(info!=null){
					String lng = String.valueOf((float)(getShort(info,5)-10000)/10);//3,4两位的数据转换为平面横坐标
					String lat = String.valueOf((float)(-getShort(info,7)+10000)/10);//5,6两位的数据转换为平面纵坐标
					String etric = String.valueOf((float) (getShort(info,11))/10);//第8位的数据转换为剩余电量
					String erl = String.valueOf((float) (getShort(info,13)));//第9位的数据转换为剩余饵料


				}
			}
		}
	};


	//将两字节16进制数转化为十进制数
	public static short getShort(byte[] arr, int index) {
		return (short) (0xff00 & arr[index] << 8 | (0xff & arr[index + 1]));
	}



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
//				UIUtils.showToast(HalfAuto.this,"当前抛盘电机占空比：" + speedPaoPan);
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
//				UIUtils.showToast(HalfAuto.this,"当前振动电机占空比：" + speedZhenDong);
				break;
			default:
				break;

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {

	}




	public void init() {
		JumpAuto = findViewById(R.id.JumpAuto);
		JumpManual = findViewById(R.id.JumpManual);
		Stop = findViewById(R.id.Stop);


		//Spinner 下拉栏   https://www.cnblogs.com/tinyphp/p/3858920.html
		spinner1 =  findViewById(R.id.spinner1);
		spinner2 =  findViewById(R.id.spinner2);

//		cmd = new byte[28];
		cmd = new byte[1032];
		cmd[0] = 0x07;                  //命令字节数组第1个字节命令 0x07
//        cmd[1] = 0x02;                  //命令字节数组第2个字节命令 0x02		之前版本的手动控制协议
		cmd[1] = 0x01;					//命令字节数组第2个字节命令 0x01
		cmd[2] = 0x00;					//命令字节数组第3个字节命令 0x00

		/**这一段写了代码无法实现从System到HalfAuto的跳转，为什么？？？20201022（写在别的地方试试，放在oncreate里）*/
		/*
		//new20201013
		temp[0] = (byte) (((cmd[12] - 0x30) * 1000) + ((cmd[13] - 0x30) * 100) + ((cmd[14] - 0x30) * 10) + (cmd[16] - 0x30));
		if (cmd[11]=='E') temp[0]= (byte) (10000+temp[0]);
		if (cmd[11]=='W') temp[0]= (byte) (10000-temp[0]);
		temp[1] = (byte) ((cmd[19]-0x30)*1000+(cmd[20]-0x30)*100+(cmd[21]-0x30)*10+(cmd[23]-0x30));
		if (cmd[18]=='N') temp[1] = (byte) (10000+temp[0]);
		if (cmd[18]=='S') temp[1] = (byte) (10000-temp[1]);
		temp[2] = (byte) ((cmd[25]-0x30)*100+(cmd[26]-0x30)*10+(cmd[28]-0x30));
		temp[3] = (byte) ((cmd[30]-0x30)*100+(cmd[31]-0x30)*10+(cmd[33]-0x30));
		temp[4] = (byte) ((cmd[35]-0x30)*100+(cmd[36]-0x30)*10+(cmd[38]-0x30));
		temp[5] = (byte) ((cmd[40]-0x30)*100+(cmd[41]-0x30)*10+(cmd[43]-0x30));
*/

		temp = new byte[1032];
//		info = new byte[28];
		info = new byte[1032];




	}




	}