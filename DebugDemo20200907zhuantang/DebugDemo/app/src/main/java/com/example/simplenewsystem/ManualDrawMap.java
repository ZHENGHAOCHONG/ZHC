package com.example.simplenewsystem;

		import android.app.AlertDialog;
		import android.content.BroadcastReceiver;
		import android.content.Context;
		import android.content.DialogInterface;
		import android.content.Intent;
		import android.content.IntentFilter;
		import android.graphics.Bitmap;
		import android.graphics.BitmapFactory;
		import android.graphics.Color;
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
		import com.baidu.mapapi.map.Overlay;
		import com.baidu.mapapi.map.OverlayOptions;
		import com.baidu.mapapi.map.PolylineOptions;
		import com.baidu.mapapi.model.LatLng;

		import java.io.FileInputStream;
		import java.io.FileOutputStream;
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


//手动绘制地图
public class ManualDrawMap extends AppCompatActivity /*implements AdapterView.OnItemSelectedListener*/{

	private static final String SPACE_SEPARATOR = " ";

	Button btnAddPoint;			//添加障碍点
	Button btnRefresh;			//刷新  暂时不用
	Button btnDeleteData;		//清除数据
	Button btnAddLine;   		//手绘路线
	Button btnSendPoint;   		//发送拐点坐标


	byte[] cmd;
	byte[] info;



	byte x3;//半自动，x坐标的高位
	byte x4;//半自动，x坐标的低位
	byte y3;//半自动，y坐标的高位
	byte y4;//半自动，y坐标的低位



	private MapView mMapView = null;
	private BaiduMap mBaiduMap;//mBaiduMap绘制障碍点

	List<LatLng> points = new ArrayList<LatLng>();

	OverlayOptions mOverlayOptions;  //定义的是折线

	//文件名称
	String fileName1 = "ManualDrawLine.txt";		//文件名		轨迹拐点
	String fileName2 = "ManualDrawPoint.txt";		//文件名		障碍点

	public static String ManualLinePoint="";

	String [][] str_arr2 ;
	int touchTimes = 0;   //触摸时间touchTimes
	int t1;

	byte [] revpath;			//将轨迹规划的点存到字节数组中
	byte [] revPoint;

	String s1 = "";
	int revPointLength;


	byte[] a1a1 ; //这里的alal定义的是什么？？？
	byte[] a1b1;  //x的低位字节，x的高位字节，y的低位字节，y的高位字节
	byte[] b1a1;
	byte[] b1b1;

	int length;

	byte [] tmp;
//	int num  = 0; 					//用于记录revpath数组已经发送到DTU的字节数

	int times1 = 0;


	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
		SDKInitializer.initialize(this.getApplicationContext());
//		SDKInitializer.initialize(this);
		//自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
		//包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
		SDKInitializer.setCoordType(CoordType.BD09LL);


		setContentView(R.layout.manualdrawmap);
		init();

		/*locationManager = (LocationManager) getSystemService(Context
				.LOCATION_SERVICE);
		mapView = (MapView) findViewById(R.id.map);
		// 必须回调MapView的onCreate()方法
		mapView.onCreate(savedInstanceState);
		init();*/



		//获取地图控件引用
		mMapView = (MapView) findViewById(R.id.map);



		mBaiduMap = mMapView.getMap();
		/*mBaiduMap1 = mMapView.getMap();*/
		//普通地图 ,mBaiduMap是地图控制器对象
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		/*mBaiduMap1.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		 */

		/*地图类型或图层		显示层级
				2D地图		4-21
				3D地图		19-21
				卫星图		4-20
			  路况交通图		11-21
			百度城市热力图	11-21
				室内图		17-22
		*/
        /**这里改经纬度可以改变地图坐标*/
		//x,y是经纬度，f为缩放等级（4-20之间）		20为10米   31.62320018, 119.64296511金坛经纬度
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

        /**坐标改回鱼塘之后，水质监测仪也没显示了，改或者不改意义不大*/
        /**既然我把坐标改回鱼塘了，那么这里的水质监测仪是不是可以注销*/
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


/*
		//接收实时参数
		IntentFilter intentFilter = new IntentFilter();//创建IntentFilter
		intentFilter.addAction("action.Info");//指定BroadcastReceiver监听的Action
		//注册广播，接收System广播的实时参数
		registerReceiver(mRefreshBroadcastReceiver, intentFilter);
		//注册应用内广播接收器

		IntentFilter intentFilter2 = new IntentFilter();//创建IntentFilter
		intentFilter2.addAction("action.cmd");//指定BroadcastReceiver监听的Action
		//注册BroadcastReceiver
		registerReceiver(mRefreshBroadcastReceiver, intentFilter2);      //发送命令*/


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






		//添加障碍点，点击该按钮可以进行点的标注，并且将点的坐标保存到手机内存单独的一个文件中。
		btnAddPoint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				btnAddPoint.setBackgroundColor(getResources().getColor(R.color.colorAccent));
				btnAddPoint.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonbg3));
				addPoint();  //这里的addPoint还没实现

			}
		});

		//手动绘制轨迹线，点击该按钮可以进行路线的绘制，并且将拐点的坐标保存到手机内存单独的一个文件中。
		btnAddLine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				BaiduMap.OnMapClickListener listener2 = new BaiduMap.OnMapClickListener() {
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

						Log.e("手动绘图--经纬度", "currentX:"+latitude+"currentY:"+longitude);//输出经纬度值

						points.add(latLng);
						touchTimes++;

//						//将经纬度转换为平面坐标，例子：currentX:3503132.978473543    currentY:2.075075058647018E7
//						将经纬度转换为平面坐标，例子：
						Point pa = BLtoxy(longitude,latitude);
                                                   /**之前改了地图位置之后无法进行标点，需要改这里*/
						double x = pa.getX();		//这里的横纵坐标改变之后才能进行标点
						double y = pa.getY();		//
						Log.e("手动绘图--高斯克吕哥公式，平面坐标", "currentX:"+x+"currentY:"+y);



						//注意，x,y进行转换，例子：currentX:207507268    currentY:35030923
						long x1 = Math.round(y*10);//横坐标，单位dm
						long y1 = Math.round(x*10);//纵坐标，单位dm

						Log.e("手动绘图--换算成分米", "currentX:"+x1+"currentY:"+y1);

                        //这里的偏移量也要改，码头偏移量
						//坐标转换，例子，currentX:9800      currentY:9618
						int x2 = (int)(x1-207497468);
						int y2 = (int)(y1-35021305);

						Log.e("手动绘图--相对坐标", "currentX:"+x2+"currentY:"+y2);



						ManualLinePoint = ManualLinePoint+Integer.toHexString(x2)+" "+Integer.toHexString(y2)+" ";

						//2648		2592
						x3 = (byte)(((java.lang.Short.decode("0x"+Integer.toHexString(x2)))&0xff00)>>8);	//38
						x4 = (byte)((java.lang.Short.decode("0x"+Integer.toHexString(x2)))&0xff);			//72

						y3 = (byte)(((java.lang.Short.decode("0x"+Integer.toHexString(y2)))&0xff00)>>8);	//37
						y4 = (byte)((java.lang.Short.decode("0x"+Integer.toHexString(y2)))&0xff);			//-110

						Log.e("手动绘图--相对坐标--十六进制", "currentX:"+"x3="+x3+"x4="+x4+"currentY:"+"y3="+y3+"y4="+y4);


//						ManualLinePoint = ManualLinePoint+latitude+" "+longitude+" ";
//						Log.e("手动绘图 String 类型",ManualLinePoint+"");
						writeFileData(fileName1,ManualLinePoint);		//写入文件，正确


//						Setrevpath2(touchTimes);

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


				//设置折线的属性,点的个数必须大于2		http://lbsyun.baidu.com/index.php?title=androidsdk/guide/render-map/ployline
				if(points.size()>2) {
					mOverlayOptions = new PolylineOptions()
							.width(10)
							.color(0xAAFF0000)
							.points(points);
					//在地图上绘制折线
					//mPloyline 折线对象
					mBaiduMap.addOverlay(mOverlayOptions);
					//Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);  me写的
				}else{
					UIUtils.showToast(ManualDrawMap.this,"手动绘制点的个数应该超过一个");
				}
//设置地图单击事件监听
				mBaiduMap.setOnMapClickListener(listener2);


			}
		});


//先屏蔽  刷新功能
/*
		//刷新按钮，点击该按钮可以将内存中障碍点文件和手动绘制路线文件中的点绘制在地图上。
		btnRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {	//刷新功能就是将内存中的点和线再一次在地图上绘制出来

				String result = readFileData(fileName1); 		// 读取轨迹拐点文件		result结果正确
				String[] result11 = Auto.stringToArray(result);		// 读取文件		result1结果正确
				String[][] result12 = stringToArray2(result11);	// 读取文件		result2结果正确   38个数据

				mOverlayOptions = new PolylineOptions()
						.width(10)
						.color(0xAAFF0000)
						.points(points);

				//在地图上绘制折线
				//mPloyline 折线对象
				mBaiduMap.addOverlay(mOverlayOptions);





				String result2 = readFileData(fileName2); 		// 读取障碍点文件		result结果正确

			}
		});
*/

		//每次需要重新绘制地图的时候，需要清除一下数据
		//清除数据，点击该按钮可以将内存中障碍点文件和手动绘制路线文件中的点清除，方便用户重新绘制障碍点和轨迹拐点。
		btnDeleteData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mBaiduMap.clear();  //清空mBaiduMap
				deleteFile(fileName1);		//清除手绘拐点的数据
				deleteFile(fileName2);		//清除障碍点的数据
			}
		});

		//发送地图拐点到船载终端
		btnSendPoint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String result = readFileData(fileName1); 		// 读取文件		result结果正确
				String[] result1 = stringToArray(result);		// 读取文件		result1结果正确
				String[][] result2 = stringToArray2(result1);	// 读取文件		result2结果正确   38个数据

				Log.e("手动绘图","result1 length的值"+result1.length);	//16
				Log.e("手动绘图","result2 length的值"+result2.length);	//50


				length = result1.length/2;
				Log.e("手动绘图","length的值"+length);	//8

				for (int j = 0;j<result2.length;j++) {

					try {
						String aa1 = result2[j][0].substring(0, 2);
						String ab1 = result2[j][0].substring(2, 4);
						String ba1 = result2[j][1].substring(0, 2);
						String bb1 = result2[j][1].substring(2, 4);

						//输出的子字符串是正确的
						Log.e(" 手动绘图 在Auto 中 第" + j + "个  子字符串  ", "(" + aa1 + "   " + ab1 + "   " + "   " + ba1 + "   " + bb1 + ")");//可以到达

						a1a1[j] = (byte) Integer.parseInt(aa1, 16);
						a1b1[j] = (byte) Integer.parseInt(ab1, 16);
						b1a1[j] = (byte) Integer.parseInt(ba1, 16);
						b1b1[j] = (byte) Integer.parseInt(bb1, 16);

						Log.e(" 手动绘图 在Auto 中 第" + j + "个  数组", " (" + a1a1[j] + "   " + a1b1[j] + "   " + "   " + b1a1[j] + "   " + b1b1[j] + ")");//可以到达

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				Setrevpath2(touchTimes);

				for(int p = 0;p<revPointLength;p++){
//					Log.e("Ting 在xzdt 中的revPoint数据"," "+p+" "+revPoint[p]);		//执行到了这一步		√√√
				}

				Log.e("手动绘图 执行到这一步了吗","到了");
				SendPath(times1);
				Log.e("手动绘图 执行到这一步了吗","到了");

				cmd[1]  = tmp[1];
//				cmd[1] =  Byte.decode("0x"+Integer.toHexString(tmp[1]));

//				Log.e("Ting Send Point cmd",""+cmd[1]);
				for(int i=2;i<27;i++) {
					cmd[i] = tmp[i];
//					cmd[i] =  Byte.decode("0x"+Integer.toHexString(tmp[i]));
//					Log.e("Ting Send Point cmd",""+cmd[i]);
				}
				cmd[27] = tmp[27];  //字节数组最后一位      校验位
//				cmd[27] =  Byte.decode("0x"+Integer.toHexString(tmp[27]));
//				Log.e("Ting Send Point cmd",""+cmd[27]);
				Intent intent = new Intent();
				intent.putExtra("cmd",cmd);
////                intent.setAction("action.cmd");
				//设置intent的action值是action.move
				intent.setAction("action.cmd");
				sendBroadcast(intent);


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

//				Log.e("手动绘图--经纬度", "currentX:"+latitude+"currentY:"+longitude);//输出经纬度值

				//将经纬度转换为平面坐标，例子：currentX:3503132.978473543    currentY:2.075075058647018E7

				Point pa = BLtoxy(longitude,latitude);


				double x = pa.getX();		//3503092.3016042993
				double y = pa.getY();		//2.07507268389166E7
//				Log.e("手动绘图--高斯克吕哥公式，平面坐标", "currentX:"+x+"currentY:"+y);


				//注意，x,y进行转换，例子：currentX:207507268    currentY:35030923
				long x1 = Math.round(y*10);//横坐标，单位dm
				long y1 = Math.round(x*10);//纵坐标，单位dm

//				Log.e("手动绘图--换算成分米", "currentX:"+x1+"currentY:"+y1);


				//坐标转换，例子，currentX:9800      currentY:9618
				int x2 = (int)(x1-207497468);
				int y2 = (int)(y1-35021305);

//				Log.e("手动绘图-相对坐标", "currentX:"+x2+"currentY:"+y2);


				//2648		2592
				x3 = (byte)(((java.lang.Short.decode("0x"+Integer.toHexString(x2)))&0xff00)>>8);	//38
				x4 = (byte)((java.lang.Short.decode("0x"+Integer.toHexString(x2)))&0xff);			//72

				y3 = (byte)(((java.lang.Short.decode("0x"+Integer.toHexString(y2)))&0xff00)>>8);	//37
				y4 = (byte)((java.lang.Short.decode("0x"+Integer.toHexString(y2)))&0xff);			//-110

//				Log.e("手动绘图--相对坐标--十六进制", "currentX:"+"x3="+x3+"x4="+x4+"currentY:"+"y3="+y3+"y4="+y4);


			/*	//弹出一个对话框
				AlertDialog.Builder dialog = new AlertDialog.Builder(HalfAuto.this);
				dialog.setTitle("提示");
				dialog.setMessage("确定要以该点作为终点吗？");
				dialog.setCancelable(false);


				dialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						UIUtils.showToast( HalfAuto.this , "You clicked OK" );

						cmd[3] = 0x02;
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
*/

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
				Log.e("手动绘图 Send Point",i+"   "+tmp[i]);			//到达
			}
		}
		/*tmp[27]=(byte)(revPoint[0]+revPoint[1]+revPoint[2]+revPoint[3]+revPoint[4]+revPoint[5]+revPoint[6]+revPoint[7]+revPoint[8]+revPoint[9]+
				revPoint[10]+revPoint[11]+revPoi2nt[12]+revPoint[13]+revPoint[14]+revPoint[15]+revPoint[16]+revPoint[17]+revPoint[18]+revPoint[19]+
				revPoint[20]+revPoint[21]+revPoint[22]+revPoint[23]+revPoint[24]+revPoint[25]+revPoint[26]+revPoint[27]	);*/
		tmp[27]=(byte)(tmp[0]+tmp[1]+tmp[2]+tmp[3]+tmp[4]+tmp[5]+tmp[6]+tmp[7]+tmp[8]+tmp[9]+
				tmp[10]+tmp[11]+tmp[12]+tmp[13]+tmp[14]+tmp[15]+tmp[16]+tmp[17]+tmp[18]+tmp[19]+
				tmp[20]+tmp[21]+tmp[22]+tmp[23]+tmp[24]+tmp[25]+tmp[26]);
	}




	public void Setrevpath2(int touchTimes) {

//		StringBuilder sb = new StringBuilder();

//		Log.e("Ting","Ting");			//进入到这一步
		Log.e("手动绘图","touchTimes的值"+touchTimes);		//

		//一包数据有三个点  当传输的点数少于6个时，传输类型1，2，2，也就是2、3包中点会重复
		//touchTimes为8时，3个包。touchTimes为9时，3个包
		for(int i = 0;(touchTimes%3!=0 ? i<=touchTimes/3:i<touchTimes/3);i++){
//		for(int i = 0;i<=(touchTimes/3);i++){
			//可以通过数组的长度来进行判断。if(length%3==2)			//if(length%3==1)
			if((touchTimes-i*3)/3==0){		//最后一包数据
				if(touchTimes%3==1){		//最后一包数据只有一个点
					t1 = touchTimes / 3;
					revpath = new byte[]{(byte)(3 * t1),a1a1[3 * t1],a1b1[3 * t1],b1a1[3 * t1],b1b1[3 * t1],0x0F,0x37,0x01, 0x00,
							(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
							(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,0x00,0x00,0x00,

							/*0x07,0x04,(byte)(3 * t),a1a1[3 * t],a1b1[3 * t],b1a1[3 * t],b1b1[3 * t],0x0F,0x37,0x01, 0x00,
							(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
							(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,0x00,0x00,0x00,
							(byte)(0x07+0x04+(byte)(3*i)+a1a1[3*i]+a1b1[3*i]+b1a1[3*i]+b1b1[3*i]+0x0F+0x37+0x01+0x00+
									(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+
									(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+(byte)0xFF+0x00+0x00+0x00)*/
					};
				}else if(touchTimes%3==2) {
					t1 = touchTimes / 3;	//最后一包数据有两个点
					revpath = new byte[]{(byte) (3 * t1), a1a1[3 * t1], a1b1[3 * t1], b1a1[3 * t1], b1b1[3 * t1], 0x0F, 0x37, 0x01, 0x00,
							a1a1[3 * t1 + 1], a1b1[3 * t1 + 1], b1a1[3 * t1 + 1], b1b1[3 * t1 + 1], 0x0F, 0x37, 0x01, 0x00,
							(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00,

							/*0x07, 0x04, (byte) (3 * t), a1a1[3 * t], a1b1[3 * t], b1a1[3 * t], b1b1[3 * t], 0x0F, 0x37, 0x01, 0x00,
							a1a1[3 * t + 1], a1b1[3 * t + 1], b1a1[3 * t + 1], b1b1[3 * t + 1], 0x0F, 0x37, 0x01, 0x00,
							(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00,
							(byte) (0x07 + 0x04 + (byte) (3 * i) + a1a1[3 * i] + a1b1[3 * i] + b1a1[3 * i] + b1b1[3 * i] + 0x0F + 0x37 + 0x01 + 0x00 +
									a1a1[3 * i + 1] + a1b1[3 * i + 1] + b1a1[3 * i + 1] + b1b1[3 * i + 1] + 0x0F + 0x37 + 0x01 + 0x00 +
									(byte) 0xFF + (byte) 0xFF + (byte) 0xFF + (byte) 0xFF + (byte) 0xFF + 0x00 + 0x00 + 0x00)*/
					};
				} else if(touchTimes%3==0) {
					t1 = touchTimes / 3;	//最后一包数据有三个点
					revpath = new byte[]{(byte) (3 * t1), a1a1[3 * t1], a1b1[3 * t1], b1a1[3 * t1], b1b1[3 * t1], 0x0F, 0x37, 0x01, 0x00,
							a1a1[3 * t1 + 1], a1b1[3 * t1 + 1], b1a1[3 * t1 + 1], b1b1[3 * t1 + 1], 0x0F, 0x37, 0x01, 0x00,
							a1a1[3 * t1 + 2], a1b1[3 * t1 + 2], b1a1[3 * t1 + 2], b1b1[3 * t1 + 2], 0x0F, 0x37, 0x01, 0x00,

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
				Log.e("手动绘图 在Auto中的revpath数据"," "+x+" "+revpath[x]);//执行到了这一步
//				revPoint[];
				s1 = s1 + revpath[x]+" ";

//				sb.append(revpath[x]+" ");
			}

/*			for(int x = 0;x<28;x++){
//				Log.e("Ting 在Auto中的revpath数据"," "+x+" "+revpath[x]);//执行到了这一步
//				revPoint[];
				s = s + revpath[x]+" ";

//				sb.append(revpath[x]+" ");
			}*/

		}


		String[] Point = stringToArray(s1);

		for(int p = 0;p<Point.length;p++){
			revPointLength = Point.length;
			revPoint[p] = (byte)(Integer.parseInt(Point[p]));							//得出revPoint字节数组的值
			Log.e("手动绘图中的revPoint数据"," "+p+" "+revPoint[p]);		//执行到了这一步		√√
		}

	}







	//将字符串数组变成二维字符串数组
	public String[][] stringToArray2(String[] numStr) {
		for (int i = 0,j =0;i<numStr.length;i++,j=i/2){
			if(i%2==0)
			{
				str_arr2[j][0] = numStr[i];
				Log.e("手动绘图","get File stringToArray result2 x "+str_arr2[j][0]);	//不可以到达
			} else{
				str_arr2[j][1] = numStr[i];
				Log.e("手动绘图","get File stringToArray result2 y "+str_arr2[j][1]);	//不可以到达
			}
		}
		return str_arr2;
	}

	//将字符串变成字符串数组
	public static String[] stringToArray(String numStr) {
		String [] str_arr = numStr.split(SPACE_SEPARATOR);
		for (int i = 0;i<str_arr.length;i++){
			Log.e("手动绘图","get File stringToArray result1 "+str_arr[i]);	//可以到达
		}
		return str_arr;
	}




	//打开指定文件，读取其数据，返回字符串对象
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



	//是错误的
	//向指定的文件中写入指定的数据
	public void writeFileData(String filename, String content) {
		try {
			//不存在会自动创建
			FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);//获得FileOutputStream
			//将要写入的字符串转换为byte数组
			byte[]  bytes = content.getBytes();
			fos.write(bytes);//将byte数组写入文件
			Log.e("手动绘图 Manual Draw","get File content");	//可以到达
			fos.close();//关闭文件输出流
			Log.e("手动绘图 Manual Draw","get File content ok");	//可以到达
		} catch (Exception e) {
			e.printStackTrace();
		}
	}





	private void addPoint() {   //这里的addpoint（）没实现



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
        //这里的k0到k8是什么？？？
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
			//07 01 04满足条件，以下都可以到达。为什么07 04不是action.info，而是action.cmd。
			if(action!=null){
				Log.e("手动绘图","action是否为空？？"+action+" "+cmd[0]+" "+cmd[1]+" "+cmd[2]);	//到了，打印出来的是action.cmd  07 04 0x??
				//info为空
				if(action.equals("action.Info")) {
					info = intent.getExtras().getByteArray("info");//info为空
					Log.e("手动绘图", "get Point info！！！！");    //没有到
					//这是收到地图数据，船返回的数据。区别于手动时船返回的数据
					if (info != null) {
						Log.e("手动绘图", "info是否为空？？？");    //此处没有到，说明info为空
						if (info[0] == 0x07 && info[1] == 0x04/* && info[2] == 0x04*/) {
//						if (info[0] == 0x07 && info[1] == 0x01 && info[2] == 0x04) {
								times1++;
								Log.e("手动绘图", " " + times1);    //到达
								SendPath(times1);
								Log.e("手动绘图", "get Point");    //到达
								cmd[1] = tmp[1];
								//				cmd[1] =  Byte.decode("0x"+Integer.toHexString(tmp[1]));
								Log.e("Ting Send Point cmd", "" + cmd[1]);
								for (int i = 2; i < 27; i++) {
									cmd[i] = tmp[i];
									//					cmd[i] =  Byte.decode("0x"+Integer.toHexString(tmp[i]));
									Log.e("Ting Send Point cmd", "" + cmd[i]);
								}
								cmd[27] = tmp[27];  //字节数组最后一位      校验位
								//				cmd[27] =  Byte.decode("0x"+Integer.toHexString(tmp[27]));
								Log.e("Ting Send Point cmd", "" + cmd[27]);
								Intent intent2 = new Intent();
								intent2.putExtra("cmd", cmd);
								////                intent.setAction("action.cmd");
								//设置intent的action值是action.move
								intent2.setAction("action.cmd");
								sendBroadcast(intent2);
						}
					}
				}
			}
		}
	};


	//将两字节16进制数转化为十进制数  （未用到）
	public static short getShort(byte[] arr, int index) {
		return (short) (0xff00 & arr[index] << 8 | (0xff & arr[index + 1]));
	}


/*

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

*/



	public void init() {


		btnAddPoint   = findViewById(R.id.btnAddPoint);			//添加障碍点
		btnAddLine 	  = findViewById(R.id.btnAddLine);			//手绘路线
//		btnRefresh 	  = findViewById(R.id.btnRefresh);			//刷新
		btnDeleteData = findViewById(R.id.btnDeleteData);		//清除数据
		btnSendPoint  = findViewById(R.id.btnSendPoint);		//发送拐点


		//Spinner 下拉栏   https://www.cnblogs.com/tinyphp/p/3858920.html
//		spinner1 = (Spinner) findViewById(R.id.spinner1);
//		spinner2 = (Spinner) findViewById(R.id.spinner2);

		cmd = new byte[28];
		cmd[0] = 0x07;                  //命令字节数组第1个字节命令 0x07
//        cmd[1] = 0x02;                  //命令字节数组第2个字节命令 0x02		之前版本的手动控制协议
		cmd[1] = 0x01;					//命令字节数组第2个字节命令 0x01
		cmd[2] = 0x00;					//命令字节数组第3个字节命令 0x00


		info = new byte[28];
		str_arr2= new String[50][2];
		revPoint = new byte[500];
		a1a1 = new byte[50];
		a1b1 = new byte[50];
		b1a1 = new byte[50];
		b1b1 = new byte[50];
		revpath = new byte[28];

		tmp = new byte[28];		//0x07 0x04 ...




	}




}