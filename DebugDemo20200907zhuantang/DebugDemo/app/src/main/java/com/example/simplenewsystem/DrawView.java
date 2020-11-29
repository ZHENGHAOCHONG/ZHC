package com.example.simplenewsystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import static android.content.ContentValues.TAG;
import static com.example.simplenewsystem.System.pxpy;



//Android的绘图应该继承View组件，并重写onDraw（）方法即可

public class DrawView extends View
{


	public static Class<?> service;
	// 定义cacheBitmap上的Canvas对象
	Canvas cacheCanvas = null;
	//paint代表canvas上的画笔
	public Paint paint = null;			//画笔，画虚线
	public Paint paint2 = null;			//画笔，画实线
	public Paint paint3 = null;			//画笔，画点
	public Paint paint4 = null;			//画笔，画实线

	private Path path;			//实路线		船的行走路径
	private Path path2;    		//虚路线		规划出来的路径
	private Path path3;			//实路线		绘制增氧管线路

	// 定义一个内存中的图片，该图片将作为缓冲区
	Bitmap cacheBitmap = null;
	Bitmap mBackground = null;
	Bitmap icon = null ;


	int i=0;

	float flat;					//平面纵坐标
	float flng;					//平面横坐标
	short [][] initpath;		//平面坐标数组

	//ArrayList<LatLng> latlngs = new ArrayList< >();
	//ArrayList<Pxpy> pxpy = new ArrayList< >();		//动态数组
	private double width;

	//构造函数
	public DrawView(Context context, int width , int height,short [][] revpoint)
	{
		super(context);


		//在画布上面添加位置图标并缩放至合适的大小

		//decodeResource(Resources res,int id)：根据给定的资源id从指定资源中解析、创建bitmap对象
		icon = BitmapFactory.decodeResource(getResources(),R.drawable.icon);
		int oldwidth = icon.getWidth();			//图标的宽度
		int oldheight = icon.getHeight();		//图标的高度
		int newWidth = 30;
		int newHeight = 50;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / oldwidth;
		float scaleHeight = ((float) newHeight) / oldheight;
		// 取得想要缩放的matrix(矩阵)参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		//createBitmap(Bitmap source,int x,int y,int width,int heigth,Matrix m,boolean filter)
		//从源位图icon的指定坐标点(0、0)开始，从中“挖取”宽width、高height的一块出来，创建新的bitmap对象，并按matrix指定的规则进行变换
		icon = Bitmap.createBitmap(icon, 0,0, oldwidth, oldheight, matrix, true);

		//根据给定的资源id从指定资源中解析、创建bitmap对象
		mBackground = BitmapFactory.decodeResource(getResources(),R.drawable.bg4);
		// 创建一个与该View相同大小的缓存区
		//createBitmap(width, height, Bitmap.Config config)：创建一个宽width，高height的新位图
		cacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		//canvas：画布，代表依附于指定View的画布
		cacheCanvas = new Canvas();

		//initpath = revpoint.clone();
		//initpath = new short[10][2];			//创建一个可以存放十个平面坐标点的二维数组
		initpath = new short[80][2];			//创建一个可以存放80个平面坐标点的二维数组

		//以下为学校鱼塘的十个实验点的平面横、纵坐标
		/*
		initpath[0][0]=TiaoZhengx((short)9908);
		initpath[0][1]=TiaoZhengy((short)9876);
		initpath[1][0]=TiaoZhengx((short)9593);
		initpath[1][1]=TiaoZhengy((short)9870);
		initpath[2][0]=TiaoZhengx((short)9560);
		initpath[2][1]=TiaoZhengy((short)10094);
		initpath[3][0]=TiaoZhengx((short)9900);
		initpath[3][1]=TiaoZhengy((short)10125);
		initpath[4][0]=TiaoZhengx((short)9905);
		initpath[4][1]=TiaoZhengy((short)9959);
		initpath[5][0]=TiaoZhengx((short)9582);
		initpath[5][1]=TiaoZhengy((short)9944);
		initpath[6][0]=TiaoZhengx((short)9550);
		initpath[6][1]=TiaoZhengy((short)10168);
		initpath[7][0]=TiaoZhengx((short)9898);
		initpath[7][1]=TiaoZhengy((short)10208);
		initpath[8][0]=TiaoZhengx((short)9903);
		initpath[8][1]=TiaoZhengy((short)10042);
		initpath[9][0]=TiaoZhengx((short)9571);
		initpath[9][1]=TiaoZhengy((short)10019);
		*/


		//以下为金坛蟹塘的平面坐标,全部的坐标
        /*
		initpath[0][0]=	TiaoZhengx((short)10000);
		initpath[0][1]=	TiaoZhengy((short)10000);
		initpath[1][0]=	TiaoZhengx((short)9671);
		initpath[1][1]=	TiaoZhengy((short)10085);
		initpath[2][0]=	TiaoZhengx((short)	9534	);
		initpath[2][1]=	TiaoZhengy((short)	9014	);
		initpath[3][0]=	TiaoZhengx((short)	9741	);
		initpath[3][1]=	TiaoZhengy((short)	8996	);
		initpath[4][0]=	TiaoZhengx((short)	9851	);
		initpath[4][1]=	TiaoZhengy((short)	10039	);
		initpath[5][0]=	TiaoZhengx((short)	9855	);
		initpath[5][1]=	TiaoZhengy((short)	10072	);
		initpath[6][0]=	TiaoZhengx((short)	9744	);
		initpath[6][1]=	TiaoZhengy((short)	10100	);
		initpath[7][0]=	TiaoZhengx((short)	9734	);
		initpath[7][1]=	TiaoZhengy((short)	10024	);
		initpath[8][0]=	TiaoZhengx((short)	9604	);
		initpath[8][1]=	TiaoZhengy((short)	9008	);
		initpath[9][0]=	TiaoZhengx((short)	9600	);
		initpath[9][1]=	TiaoZhengy((short)	8975	);
		initpath[10][0]=TiaoZhengx((short)	9741	);
		initpath[10][1]=TiaoZhengy((short)	8996	);
		initpath[11][0]=TiaoZhengx((short)	9810	);
		initpath[11][1]=TiaoZhengy((short)	8990	);
		initpath[12][0]=TiaoZhengx((short)	9920	);
		initpath[12][1]=TiaoZhengy((short)	10021	);
		initpath[13][0]=TiaoZhengx((short)	9923	);
		initpath[13][1]=TiaoZhengy((short)	10054	);
		initpath[14][0]=TiaoZhengx((short)	9812	);
		initpath[14][1]=TiaoZhengy((short)	10083	);
		initpath[15][0]=TiaoZhengx((short)	9803	);
		initpath[15][1]=TiaoZhengy((short)	10008	);
		initpath[16][0]=TiaoZhengx((short)	9674	);
		initpath[16][1]=TiaoZhengy((short)	9002	);
		initpath[17][0]=TiaoZhengx((short)	9670	);
		initpath[17][1]=TiaoZhengy((short)	8969	);
		initpath[18][0]=TiaoZhengx((short)	9810	);
		initpath[18][1]=TiaoZhengy((short)	8990	);
		initpath[19][0]=TiaoZhengx((short)	10730	);
		initpath[19][1]=TiaoZhengy((short)	8908	);
		initpath[20][0]=TiaoZhengx((short)	10968	);
		initpath[20][1]=TiaoZhengy((short)	9750	);
		initpath[21][0]=TiaoZhengx((short)	10811	);
		initpath[21][1]=TiaoZhengy((short)	9790	);
		initpath[22][0]=TiaoZhengx((short)	10566	);
		initpath[22][1]=TiaoZhengy((short)	8922	);
		initpath[23][0]=TiaoZhengx((short)	10557	);
		initpath[23][1]=TiaoZhengy((short)	8890	);
		initpath[24][0]=TiaoZhengx((short)	10650	);
		initpath[24][1]=TiaoZhengy((short)	8882	);
		initpath[25][0]=TiaoZhengx((short)	10674	);
		initpath[25][1]=TiaoZhengy((short)	8973	);
		initpath[26][0]=TiaoZhengx((short)	10900	);
		initpath[26][1]=TiaoZhengy((short)	9767	);
		initpath[27][0]=TiaoZhengx((short)	10909	);
		initpath[27][1]=TiaoZhengy((short)	9799	);
		initpath[28][0]=TiaoZhengx((short)	10811	);
		initpath[28][1]=TiaoZhengy((short)	9790	);
		initpath[29][0]=TiaoZhengx((short)	10744	);
		initpath[29][1]=TiaoZhengy((short)	9808	);
		initpath[30][0]=TiaoZhengx((short)	10473	);
		initpath[30][1]=TiaoZhengy((short)	8931	);
		initpath[31][0]=TiaoZhengx((short)	10463	);
		initpath[31][1]=TiaoZhengy((short)	8898	);
		initpath[32][0]=TiaoZhengx((short)	10579	);
		initpath[32][1]=TiaoZhengy((short)	8888	);
		initpath[33][0]=TiaoZhengx((short)	10603	);
		initpath[33][1]=TiaoZhengy((short)	8977	);
		initpath[34][0]=TiaoZhengx((short)	10832	);
		initpath[34][1]=TiaoZhengy((short)	9785	);
		initpath[35][0]=TiaoZhengx((short)	10841	);
		initpath[35][1]=TiaoZhengy((short)	9817	);
		initpath[36][0]=TiaoZhengx((short)	10744	);
		initpath[36][1]=TiaoZhengy((short)	9808	);
		initpath[37][0]=TiaoZhengx((short)	10141	);
		initpath[37][1]=TiaoZhengy((short)	9964	);
		initpath[38][0]=TiaoZhengx((short)	10108	);
		initpath[38][1]=TiaoZhengy((short)	9862	);
		initpath[39][0]=TiaoZhengx((short)	10031	);
		initpath[39][1]=TiaoZhengy((short)	9891	);
		initpath[40][0]=TiaoZhengx((short)	9941	);
		initpath[40][1]=TiaoZhengy((short)	9048	);
		initpath[41][0]=TiaoZhengx((short)	10374	);
		initpath[41][1]=TiaoZhengy((short)	9010	);
		initpath[42][0]=TiaoZhengx((short)	10609	);
		initpath[42][1]=TiaoZhengy((short)	9771	);
		initpath[43][0]=TiaoZhengx((short)	10119	);
		initpath[43][1]=TiaoZhengy((short)	9897	);
		initpath[44][0]=TiaoZhengx((short)	10099	);
		initpath[44][1]=TiaoZhengy((short)	9866	);
		initpath[45][0]=TiaoZhengx((short)	10018	);
		initpath[45][1]=TiaoZhengy((short)	9112	);
		initpath[46][0]=TiaoZhengx((short)	10324	);
		initpath[46][1]=TiaoZhengy((short)	9084	);
		initpath[47][0]=TiaoZhengx((short)	10520	);
		initpath[47][1]=TiaoZhengy((short)	9721	);
		initpath[48][0]=TiaoZhengx((short)	10163	);
		initpath[48][1]=TiaoZhengy((short)	9813	);
		initpath[49][0]=TiaoZhengx((short)	10096	);
		initpath[49][1]=TiaoZhengy((short)	9175	);
		initpath[50][0]=TiaoZhengx((short)	10273	);
		initpath[50][1]=TiaoZhengy((short)	9159	);
		initpath[51][0]=TiaoZhengx((short)	10432	);
		initpath[51][1]=TiaoZhengy((short)	9672	);
		initpath[52][0]=TiaoZhengx((short)	10224	);
		initpath[52][1]=TiaoZhengy((short)	9725	);
		initpath[53][0]=TiaoZhengx((short)	10173	);
		initpath[53][1]=TiaoZhengy((short)	9238	);
		initpath[54][0]=TiaoZhengx((short)	10223	);
		initpath[54][1]=TiaoZhengy((short)	9234	);
		initpath[55][0]=TiaoZhengx((short)	10343	);
		initpath[55][1]=TiaoZhengy((short)	9622	);
		initpath[56][0]=TiaoZhengx((short)	10285	);
		initpath[56][1]=TiaoZhengy((short)	9637	);
		initpath[57][0]=TiaoZhengx((short)	10198	);
		initpath[57][1]=TiaoZhengy((short)	9236	);
		initpath[58][0]=TiaoZhengx((short)	9841	);
		initpath[58][1]=TiaoZhengy((short)	9274	);
		initpath[59][0]=TiaoZhengx((short)	9923	);
		initpath[59][1]=TiaoZhengy((short)	10054	);
		initpath[60][0]=TiaoZhengx((short)	10000	);
		initpath[60][1]=TiaoZhengy((short)	10000	);


		//蟹塘顶点和障碍物坐标
		initpath[61][0]=TiaoZhengx((short)	9601	);	//蟹塘边界
		initpath[61][1]=TiaoZhengy((short)	10189	);
		initpath[62][0]=TiaoZhengx((short)	9441	);
		initpath[62][1]=TiaoZhengy((short)	8939	);
		initpath[63][0]=TiaoZhengx((short)	10791	);
		initpath[63][1]=TiaoZhengy((short)	8819	);
		initpath[64][0]=TiaoZhengx((short)	11071	);
		initpath[64][1]=TiaoZhengy((short)	9809	);
		initpath[65][0]=TiaoZhengx((short)	9901	);	//增氧管
		initpath[65][1]=TiaoZhengy((short)	10089	);
		initpath[66][0]=TiaoZhengx((short)	9781	);
		initpath[66][1]=TiaoZhengy((short)	8959	);
		initpath[67][0]=TiaoZhengx((short)	10481	);
		initpath[67][1]=TiaoZhengy((short)	8889	);
		initpath[68][0]=TiaoZhengx((short)	10771	);
		initpath[68][1]=TiaoZhengy((short)	9829	);
		initpath[69][0]=TiaoZhengx((short)	9761	);	//水质监测仪
		initpath[69][1]=TiaoZhengy((short)	9529	);
		initpath[70][0]=TiaoZhengx((short)	9991	);
		initpath[70][1]=TiaoZhengy((short)	9709	);
		initpath[71][0]=TiaoZhengx((short)	10221	);
		initpath[71][1]=TiaoZhengy((short)	9469	);
		initpath[72][0]=TiaoZhengx((short)	10071	);	//码头坐标
		initpath[72][1]=TiaoZhengy((short)	9939	);
		initpath[73][0]=TiaoZhengx((short)	9991	);
		initpath[73][1]=TiaoZhengy((short)	9969	);
		initpath[74][0]=TiaoZhengx((short)	10111	);
		initpath[74][1]=TiaoZhengy((short)	10059	);
		initpath[75][0]=TiaoZhengx((short)	10021	);
		initpath[75][1]=TiaoZhengy((short)	10069	);

        */

		//金坛2019-06-08新测的坐标点数据

/*
		initpath[0][0]=	TiaoZhengx((short)10050);
		initpath[0][1]=	TiaoZhengy((short)9985);
		initpath[1][0]=	TiaoZhengx((short)10561);
		initpath[1][1]=	TiaoZhengy((short)9832);
		initpath[2][0]=	TiaoZhengx((short)10274);
		initpath[2][1]=	TiaoZhengy((short)8876);
		initpath[3][0]=	TiaoZhengx((short)9688);
		initpath[3][1]=	TiaoZhengy((short)8917);
		initpath[4][0]=	TiaoZhengx((short)9794);
		initpath[4][1]=	TiaoZhengy((short)9887);
		initpath[5][0]=	TiaoZhengx((short)10403);
		initpath[5][1]=	TiaoZhengy((short)9725);
		initpath[6][0]=	TiaoZhengx((short)10168);
		initpath[6][1]=	TiaoZhengy((short)9025);
		initpath[7][0]=	TiaoZhengx((short)9854);
		initpath[7][1]=	TiaoZhengy((short)9089);
		initpath[8][0]=	TiaoZhengx((short)9937);
		initpath[8][1]=	TiaoZhengy((short)9749);
		initpath[9][0]=	TiaoZhengx((short)10259);
		initpath[9][1]=	TiaoZhengy((short)9643);
		initpath[10][0]=TiaoZhengx((short)10083);
		initpath[10][1]=TiaoZhengy((short)9182);
		initpath[11][0]=TiaoZhengx((short)9976);
		initpath[11][1]=TiaoZhengy((short)9274);
		initpath[12][0]=TiaoZhengx((short)10058);
		initpath[12][1]=TiaoZhengy((short)9639);



*/

/*2019-07-28
		initpath[0][0]=	TiaoZhengx((short)9996);
		initpath[0][1]=	TiaoZhengy((short)9926);
		initpath[1][0]=	TiaoZhengx((short)9879);
		initpath[1][1]=	TiaoZhengy((short)9070);
		initpath[2][0]=	TiaoZhengx((short)10066);
		initpath[2][1]=	TiaoZhengy((short)9051);
		initpath[3][0]=	TiaoZhengx((short)10230);
		initpath[3][1]=	TiaoZhengy((short)9850);

		initpath[4][0]=	TiaoZhengx((short)9996);
		initpath[4][1]=	TiaoZhengy((short)9926);
		initpath[5][0]=	TiaoZhengx((short)9879);
		initpath[5][1]=	TiaoZhengy((short)9070);
		initpath[6][0]=	TiaoZhengx((short)10066);
		initpath[6][1]=	TiaoZhengy((short)9051);
		initpath[7][0]=	TiaoZhengx((short)10230);
		initpath[7][1]=	TiaoZhengy((short)9850);

		initpath[8][0]=	TiaoZhengx((short)9996);
		initpath[8][1]=	TiaoZhengy((short)9926);
		initpath[9][0]=	TiaoZhengx((short)9879);
		initpath[9][1]=	TiaoZhengy((short)9070);
		initpath[10][0]=TiaoZhengx((short)10066);
		initpath[10][1]=TiaoZhengy((short)9051);
		initpath[11][0]=TiaoZhengx((short)10230);
		initpath[11][1]=TiaoZhengy((short)9850);
*/

/**/
//2019-07-28新测，会碰到水质检测仪
		initpath[0][0]=	TiaoZhengx((short)10050);
		initpath[0][1]=	TiaoZhengy((short)9902);
		initpath[1][0]=	TiaoZhengx((short)10532);
		initpath[1][1]=	TiaoZhengy((short)9758);
		initpath[2][0]=	TiaoZhengx((short)10259);
		initpath[2][1]=	TiaoZhengy((short)8871);



		initpath[3][0]=	TiaoZhengx((short)9696);
		initpath[3][1]=	TiaoZhengy((short)8922);

		initpath[4][0]=	TiaoZhengx((short)9819);
		initpath[4][1]=	TiaoZhengy((short)9887);
		initpath[5][0]=	TiaoZhengx((short)10444);
		initpath[5][1]=	TiaoZhengy((short)9711);
		initpath[6][0]=	TiaoZhengx((short)10208);
		initpath[6][1]=	TiaoZhengy((short)8966);
		initpath[7][0]=	TiaoZhengx((short)9818);
		initpath[7][1]=	TiaoZhengy((short)9004);

		initpath[8][0]=	TiaoZhengx((short)9905);
		initpath[8][1]=	TiaoZhengy((short)9797);
		initpath[9][0]=	TiaoZhengx((short)10357);
		initpath[9][1]=	TiaoZhengy((short)9664);
		initpath[10][0]=TiaoZhengx((short)10158);
		initpath[10][1]=TiaoZhengy((short)9021);
		initpath[11][0]=TiaoZhengx((short)9855);
		initpath[11][1]=TiaoZhengy((short)9048);

		initpath[12][0]=TiaoZhengx((short)9926);
		initpath[12][1]=TiaoZhengy((short)9710);
		initpath[13][0]=TiaoZhengx((short)10269);
		initpath[13][1]=TiaoZhengy((short)9617);
		initpath[14][0]=TiaoZhengx((short)10108);
		initpath[14][1]=TiaoZhengy((short)9096);
		initpath[15][0]=TiaoZhengx((short)9932);
		initpath[15][1]=TiaoZhengy((short)9111);
		initpath[16][0]=TiaoZhengx((short)9987);
		initpath[16][1]=TiaoZhengy((short)9623);
		initpath[17][0]=TiaoZhengx((short)10181);
		initpath[17][1]=TiaoZhengy((short)9570);
		initpath[18][0]=TiaoZhengx((short)10058);
		initpath[18][1]=TiaoZhengy((short)9170);
		initpath[19][0]=TiaoZhengx((short)9985);
		initpath[19][1]=TiaoZhengy((short)9180);




		initpath[21][0]=TiaoZhengx((short)10056);//画障碍点
		initpath[21][1]=TiaoZhengy((short)9397);
		initpath[22][0]=TiaoZhengx((short)9865);
		initpath[22][1]=TiaoZhengy((short)9765);
		initpath[23][0]=TiaoZhengx((short)9996);
		initpath[23][1]=TiaoZhengy((short)8945);




/*
//2019-08-02
		initpath[0][0]=	TiaoZhengx((short)10037);
		initpath[0][1]=	TiaoZhengy((short)9894);
		initpath[1][0]=	TiaoZhengx((short)10613);
		initpath[1][1]=	TiaoZhengy((short)9840);
		initpath[2][0]=	TiaoZhengx((short)10324);
		initpath[2][1]=	TiaoZhengy((short)8882);



		initpath[3][0]=	TiaoZhengx((short)9704);
		initpath[3][1]=	TiaoZhengy((short)8945);

		initpath[4][0]=	TiaoZhengx((short)9800);
		initpath[4][1]=	TiaoZhengy((short)9850);
		initpath[5][0]=	TiaoZhengx((short)10522);
		initpath[5][1]=	TiaoZhengy((short)9779);
		initpath[6][0]=	TiaoZhengx((short)10274);
		initpath[6][1]=	TiaoZhengy((short)8957);
		initpath[7][0]=	TiaoZhengx((short)9781);
		initpath[7][1]=	TiaoZhengy((short)9008);

		initpath[8][0]=	TiaoZhengx((short)9862);
		initpath[8][1]=	TiaoZhengy((short)9773);
		initpath[9][0]=	TiaoZhengx((short)10430);
		initpath[9][1]=	TiaoZhengy((short)9717);
		initpath[10][0]=TiaoZhengx((short)10224);
		initpath[10][1]=TiaoZhengy((short)9033);
		initpath[11][0]=TiaoZhengx((short)9858);
		initpath[11][1]=TiaoZhengy((short)9070);

		initpath[12][0]=TiaoZhengx((short)9925);
		initpath[12][1]=TiaoZhengy((short)9697);
		initpath[13][0]=TiaoZhengx((short)10339);
		initpath[13][1]=TiaoZhengy((short)9656);
		initpath[14][0]=TiaoZhengx((short)10173);
		initpath[14][1]=TiaoZhengy((short)9108);
		initpath[15][0]=TiaoZhengx((short)9935);
		initpath[15][1]=TiaoZhengy((short)9133);
		initpath[16][0]=TiaoZhengx((short)9987);
		initpath[16][1]=TiaoZhengy((short)9620);
		initpath[17][0]=TiaoZhengx((short)10247);
		initpath[17][1]=TiaoZhengy((short)9595);
		initpath[18][0]=TiaoZhengx((short)10123);
		initpath[18][1]=TiaoZhengy((short)9184);

		initpath[19][0]=TiaoZhengx((short)10012);
		initpath[19][1]=TiaoZhengy((short)9195);

		//绘制障碍点
		initpath[20][0]=TiaoZhengx((short)10050);
		initpath[20][1]=TiaoZhengy((short)8936);
		initpath[21][0]=TiaoZhengx((short)9881);
		initpath[21][1]=TiaoZhengy((short)9678);
		initpath[22][0]=TiaoZhengx((short)10103);
		initpath[22][1]=TiaoZhengy((short)9438);

*/





		//障碍点坐标

		/*
		initpath[14][	0	]=	TiaoZhengx((short)	10521	);
		initpath[14][	1	]=	TiaoZhengy((short)	9805	);
		initpath[15][	0	]=	TiaoZhengx((short)	10477	);
		initpath[15][	1	]=	TiaoZhengy((short)	9716	);
		initpath[16][	0	]=	TiaoZhengx((short)	10399	);
		initpath[16][	1	]=	TiaoZhengy((short)	9502	);
		initpath[17][	0	]=	TiaoZhengx((short)	10348	);
		initpath[17][	1	]=	TiaoZhengy((short)	9258	);
		initpath[18][	0	]=	TiaoZhengx((short)	10271	);
		initpath[18][	1	]=	TiaoZhengy((short)	9020	);
		initpath[19][	0	]=	TiaoZhengx((short)	10139	);
		initpath[19][	1	]=	TiaoZhengy((short)	8925	);
		initpath[20][	0	]=	TiaoZhengx((short)	10038	);
		initpath[20][	1	]=	TiaoZhengy((short)	8925	);
		initpath[21][	0	]=	TiaoZhengx((short)	9823	);
		initpath[21][	1	]=	TiaoZhengy((short)	8971	);
		initpath[22][	0	]=	TiaoZhengx((short)	9770	);
		initpath[22][	1	]=	TiaoZhengy((short)	9056	);
		initpath[23][	0	]=	TiaoZhengx((short)	9802	);
		initpath[23][	1	]=	TiaoZhengy((short)	9290	);
		initpath[24][	0	]=	TiaoZhengx((short)	9829	);
		initpath[24][	1	]=	TiaoZhengy((short)	9526	);
		initpath[25][	0	]=	TiaoZhengx((short)	9893	);
		initpath[25][	1	]=	TiaoZhengy((short)	9664	);
		initpath[26][	0	]=	TiaoZhengx((short)	9870	);
		initpath[26][	1	]=	TiaoZhengy((short)	9845	);
		initpath[27][	0	]=	TiaoZhengx((short)	10161	);
		initpath[27][	1	]=	TiaoZhengy((short)	9246	);
		*/




		path = new Path();		//实路线
		path2 = new Path();		//虚路线
		//path3 = new Path();		//实路线
		path2.moveTo((float) initpath[0][0],(float) initpath[0][1]);//虚路线，从手机的左上角顶点，走向第一个点
		//path2.moveTo((float) initpath[0][0],(float) initpath[0][1]);
		//path3.moveTo((float) initpath[65][0],(float) initpath[65][1]);//画增氧管那条直线
		//path3.lineTo((float)initpath[66][0],(float)initpath[66][1]);

		//path3.moveTo((float) initpath[67][0],(float) initpath[67][1]);//画增氧管那条直线
		//path3.lineTo((float)initpath[68][0],(float)initpath[68][1]);

		for(int num=1;num<20;num++){
			if((initpath[num][0]+initpath[num][1])!=0){
				path2.lineTo((float)initpath[num][0],(float)initpath[num][1]);
			}
		}


		path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//实线，从手机的左上角顶点，走向原点(10000,10000)


		// 设置cacheCanvas将会绘制到内存中的cacheBitmap上
		cacheCanvas.setBitmap(cacheBitmap);

		paint = InitPaint();	//初始化画笔，画虚线
		paint2 = InitPaint();	//初始化画笔，画实线
		paint3 = InitPaint();	//初始化画笔，画点
		paint4 = InitPaint();	//初始化画笔，画实线


		paint.setColor(Color.RED);
		paint2.setColor(Color.BLACK);
		paint3.setColor(Color.YELLOW);
		paint4.setColor(Color.GREEN);


		paint3.setStrokeWidth(35);//设置障碍点的画点尺寸

		// 虚线绘制的时候会不断的循环这个数组，0表示偏移量,DashPathEffect画虚线，{10,20,20,25}  10 实线，15虚线，20实线，25虚线
		//DashPathEffect作用是将Path的线段虚线化。
		paint.setPathEffect( new DashPathEffect( new float[]{10,10,10,10}, 0) );

		startTimer();


	}

	//初始化画笔
	private Paint InitPaint(){
		Paint paint;
		// 设置画笔的属性
		paint = new Paint(Paint.DITHER_FLAG);
		// 设置画笔填充风格
		paint.setStyle(Paint.Style.STROKE);//空心
		paint.setStrokeWidth(10);//设置画笔的笔触宽度
		//转弯的连接风格
		paint.setStrokeJoin(Paint.Join.BEVEL);
		// 反锯齿
		paint.setAntiAlias(true);
		paint.setDither(true);//设置防抖动
		return  paint;
	}

	//小米6   手机像素为1920*1080			OPPO R6007 像素为720*980
	//平面坐标x转换为像素坐标x
	private short TiaoZhengx(short x){


		x-=9704;
		//x-=9879;//
		//x-=9688;
		//PathShow.Width是手机分辨率的宽度，PathShow.Height是手机分辨率的高度
		double tmp = (double)x*PathShow.Width/890;

		Log.i("msg", "TiaoZhengx: "+String.valueOf(tmp));
		x = (short)tmp;
		x = (short)(x+20);
		//x = (short)(x+250);
		return x;
	}

	//平面坐标y转换为像素坐标y
	private short TiaoZhengy(short y){

		y-=8882;
		//y-=9051;//
		//PathShow.Width是手机分辨率的宽度，PathShow.Height是手机分辨率的高度
		double tmp = (double)y*PathShow.Width/890;

		Log.i("msg", "TiaoZhengy: "+String.valueOf(tmp));
		y = (short)tmp;
		y =(short)((PathShow.Width+PathShow.Height)/2-y);

		//y=(short)(y-300);//用于调整位置

		return  y;
	}
	@Override
	public void onDraw(Canvas canvas)
	{

		Paint bmpPaint = new Paint();
		// 将mBackground绘制到该View组件上
		canvas.drawBitmap(mBackground, 0, 0, bmpPaint);
		// 将cacheBitmap绘制到该View组件上
		canvas.drawBitmap(cacheBitmap, 0, 0, bmpPaint);
		//canvas.drawBitmap(icon,flat-10,flng-35,bmpPaint);

		/*
		//动态数组
		if(pxpy.size()!=0){
			//Log.i("msg", "onDraw: x:"+String.valueOf(TiaoZhengx(pxpy.get(i-1).y))+"y:"+String.valueOf(TiaoZhengy(pxpy.get(i-1).x)));
			//void drawBitmap(Bitmap bitmap, float left, float top, Paint paint)

				canvas.drawBitmap(icon,TiaoZhengx(pxpy.get(i-1).x)-15,TiaoZhengy(pxpy.get(i-1).y)-40,bmpPaint);
		}
		*/
		//需要更改！！！！！！
		//path.moveTo((float) initpath[1][0],(float) initpath[1][1]);//从第一个点走

		//07 01 00 28 22 22 AC 0F 37 01 00

		// 沿着path绘制

		canvas.drawPath(path2,paint);	//用paint画笔画path2虚线
		//canvas.drawPoint( initpath[69][0],initpath[69][1] ,paint3);		//用paint3画笔画点
		//canvas.drawPoint( initpath[70][0],initpath[70][1] ,paint3);
		//canvas.drawPoint( initpath[71][0],initpath[71][1] ,paint3);


		for(int num=21;num<24;num++){
			canvas.drawPoint( initpath[num][0],initpath[num][1] ,paint3);		//用paint3画笔画点，障碍点
		}
		/**/
		canvas.drawPath(path, paint2);	//用paint2画笔画path实线
		//canvas.drawPath(path3, paint4);	//用paint3画笔画path实线
		//canvas.scale(1,-1);

		if(pxpy.size()!=0) {

			canvas.drawBitmap(icon, TiaoZhengx(pxpy.get(pxpy.size() - 2)) - 15, TiaoZhengy(pxpy.get(pxpy.size() - 1)) - 40, bmpPaint);//进行适度偏移
		}



	}


	public void setArray(short x,short y){
		/*Log.i("msg", "DrawView.dlat: "+String.valueOf(x));//到了
		Log.i("msg", "DrawView.dlng: "+String.valueOf(y));
		pxpy.add(new Pxpy(x,y));//往动态数组pxpxy中添加新的横纵坐标点*/

		if(i<pxpy.size()-1&&pxpy.size()!=0){

			float x1 = pxpy.get(i);
			float y1 = pxpy.get(i+1);

			Log.i(TAG, "DrawView: float x1  "+x1);
			Log.i(TAG, "DrawView: float y1  "+y1);

			//Judgement(x1,y1);

			flat = (float)TiaoZhengx(pxpy.get(i));
			flng = (float)TiaoZhengy(pxpy.get(i+1));

			Log.i(TAG, "DrawView: float flat  "+flat);
			Log.i(TAG, "DrawView: float flng  "+flng);



			path.lineTo(flat,flng );		//实线 连接 点
			i=i+2;
		}
		cacheCanvas.drawPath(path,paint);
	}


	//判断点在哪条直线上？
	public void Judgement(float x1,float y1){


		//如果点在第1条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		if (y1+0.299*x1-12992.01231>0 &&  y1 - 3.33 * x1 + 25353.75482 >= 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			//没有执行进来
			Log.i(TAG, "setArray:pass1");
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[0][0], (float) initpath[0][1]);

			Log.i(TAG, "setArray:pass1");
		}

		//如果点在第2条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		/*if (y1 - 3.33 * x1 + 25353.75482 >= 0 && y1 - 3.33 * x1 + 25339.84718 <= 0 && y1 + 0.299 * x1 - 12992.01231 <0 ) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点

			//没有执行进来
			Log.i(TAG, "setArray:pass2");
			path.lineTo((float) initpath[1][0], (float) initpath[1][1]);
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[1][0], (float) initpath[1][1]);

			Log.i(TAG, "setArray:pass2");
		}*/

		//如果点在第3条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 + 0.07 * x1 - 9596.834246 <= 0 && y1 + 0.07 * x1 - 9592.824458 >= 0 && y1 - 3.33 * x1 + 25339.84718 > 0 && y1 - 9.151 * x1 + 79718.92867 <= 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			path.lineTo((float) initpath[1][0], (float) initpath[1][1]);
			path.lineTo((float) initpath[2][0], (float) initpath[2][1]);
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[2][0], (float) initpath[2][1]);

			Log.i(TAG, "setArray:pass3");

		}
		//如果点在第4条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 - 9.151 * x1 + 79755.75057 >= 0 && y1 - 9.151 * x1 + 79718.92867 <= 0 && y1 + 0.07 * x1 - 9596.834246 > 0 && y1 + 0.266 * x1 - 12494.37004 <= 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			for (int num = 1; num < 4; num++) {
				path.lineTo((float) initpath[num][0], (float) initpath[num][1]);
			}
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[3][0], (float) initpath[3][1]);

			Log.i(TAG, "setArray:pass4");
		}
		//如果点在第5条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 + 0.266 * x1 - 12494.37004 <= 0 && y1 + 0.266 * x1 - 12490.23094 >= 0 && y1 - 9.151 * x1 + 79755.75057 < 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			for (int num = 1; num < 5; num++) {
				path.lineTo((float) initpath[num][0], (float) initpath[num][1]);
			}
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[4][0], (float) initpath[4][1]);

			Log.i(TAG, "setArray:pass5");
		}
/*
		//如果点在第6条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 - 2.98 * x1 + 21268.94619 >= 0 && y1 - 2.98 * x1 + 21256.37295 <= 0 && y1 + 0.266 * x1 - 12490.23094 < 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			//没有执行进来
			Log.i(TAG, "setArray:pass6");
			for (int num = 1; num < 6; num++) {
					path.lineTo((float) initpath[num][0], (float) initpath[num][1]);
			}
					//path.lineTo(某点);//lineTo该点
					path.moveTo((float) initpath[5][0], (float) initpath[5][1]);

					Log.i(TAG, "setArray:pass6");
		}*/

		//如果点在第7条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 + 0.204 * x1 - 11099.49979 <= 0 && y1 + 0.204 * x1 - 11095.41741 >= 0 && y1 - 2.98 * x1 + 21256.37295 > 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			for (int num = 1; num < 7; num++) {
				path.lineTo((float) initpath[num][0], (float) initpath[num][1]);
			}
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[6][0], (float) initpath[6][1]);

			Log.i(TAG, "setArray:pass7");
		}
		//如果点在第8条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 - 7.952 * x1 + 69284.13769 >= 0 && y1 - 7.952 * x1 + 69252.07917 <= 0 && y1 + 0.204 * x1 - 11099.49979 > 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			for (int num = 1; num < 8; num++) {
				path.lineTo((float) initpath[num][0], (float) initpath[num][1]);
			}
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[7][0], (float) initpath[7][1]);

			Log.i(TAG, "setArray:pass8");
		}
		//如果点在第9条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 + 0.329 * x1 - 13022.2918 <= 0 && y1 + 0.329 * x1 - 13018.08088 >= 0 && y1 - 7.952 * x1 + 69284.13769 < 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			for (int num = 1; num < 9; num++) {
				path.lineTo((float) initpath[num][0], (float) initpath[num][1]);
			}
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[8][0], (float) initpath[8][1]);

			Log.i(TAG, "setArray:pass9");
		}
		//如果点在第10条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 - 2.619 * x1 + 17234.19207 >= 0 && y1 - 2.619 * x1 + 17222.97839 <= 0 && y1 + 0.329 * x1 - 13018.08088 < 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			for (int num = 1; num < 10; num++) {
				path.lineTo((float) initpath[num][0], (float) initpath[num][1]);
			}
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[9][0], (float) initpath[9][1]);

			Log.i(TAG, "setArray:pass10");
		}

		//如果点在第11条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 + 0.86 * x1 - 17854.13321 <= 0 && y1 + 0.86 * x1 - 17848.85745 >= 0 && y1 - 2.619 * x1 + 17222.97839 > 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			for (int num = 1; num < 11; num++) {
				path.lineTo((float) initpath[num][0], (float) initpath[num][1]);
			}
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[10][0], (float) initpath[10][1]);

			Log.i(TAG, "setArray:pass11");
		}
		//如果点在第12条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 - 4.451 * x1 + 35140.48975 >= 0 && y1 - 4.451 * x1 + 35122.24195 <= 0 && y1 + 0.86 * x1 - 17854.13321 > 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			for (int num = 1; num < 12; num++) {
				path.lineTo((float) initpath[num][0], (float) initpath[num][1]);
			}
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[11][0], (float) initpath[11][1]);

			Log.i(TAG, "setArray:pass12");
		}

		//如果点在第13条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 + 43.25 * x1 - 444734.0231 <= 0 && y1 + 43.25 * x1 - 444560.9769 >= 0) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			for (int num = 1; num < 13; num++) {
				path.lineTo((float) initpath[num][0], (float) initpath[num][1]);
			}
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[12][0], (float) initpath[12][1]);

			Log.i(TAG, "setArray:pass13");
		}
		//如果点在第2条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else if (y1 - 2.98 * x1 + 21278.37612 < 0 && y1 + 0.299 * x1 - 12992.01231 <0 ) {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点

			path.lineTo((float) initpath[1][0], (float) initpath[1][1]);
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[1][0], (float) initpath[1][1]);

			Log.i(TAG, "setArray:pass2");
		}

		//如果点在第6条直线的左右2m范围之内，则路线从起点连接到接收到的点。之后再进行绘制线路
		else  {
			path.moveTo((float) initpath[0][0],(float) initpath[0][1]);//从手机的左上角顶点，走向第一个点
			//没有执行进来

			for (int num = 1; num < 6; num++) {
				path.lineTo((float) initpath[num][0], (float) initpath[num][1]);
			}
			//path.lineTo(某点);//lineTo该点
			path.moveTo((float) initpath[5][0], (float) initpath[5][1]);

			Log.i(TAG, "setArray:pass6");
		}

	}





	//定时器任务刷新UI
	Timer mTimer;
	TimerTask mTimerTask;
	private void  startTimer(){
		if (mTimer == null) {
			mTimer = new Timer();                   //新建定时器
		}
		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {          //新建定时器任务
				@Override
				public void run() {
					//x+=20;
					//y+=20;
					postInvalidate();               //在非UI线程中，刷新UI
					do {
						try {
							Log.i(TAG, "sleep(1000)...");
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					} while (false);

				}
			};
		}


		if(mTimer != null && mTimerTask != null )
			//void     schedule(TimerTask task, long delay, long period)
			//    Schedules the specified task for repeated fixed-delay execution, beginning after the specified delay.
			//    为重复的固定延迟执行计划指定的任务，从指定的延迟开始。
			mTimer.schedule(mTimerTask, 500,1000);//500ms 1000ms
	}


}
