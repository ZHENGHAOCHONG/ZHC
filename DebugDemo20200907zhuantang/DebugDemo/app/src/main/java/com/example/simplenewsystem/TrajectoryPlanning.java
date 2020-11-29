package com.example.simplenewsystem;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import static android.content.Context.MODE_PRIVATE;

/**该java文件作用是自动轨迹规划点
 * Creadted by Ting on 2020-05-21 15:12
 */
public class TrajectoryPlanning {  //标点路径

	String s;
	String s1;

	int temp = 0;

	public String [][] point2 ;
	public static String pointNum="";
	public double [][] point1 ;
	public double [][] point ;      		//开辟存放100个点的内存空间
//	public double [][] LatLng ;      		//开辟存放100个点的内存空间


//	double a=6378137;					//a是WGS椭球体长半轴的长度,m,a=6378137
//	double b=6356752.3142;				//b是WGS椭球体短半轴的长度,m,b=6356752.3142

//	double e1=Math.sqrt((Math.pow(a,2)-Math.pow(b,2))/Math.pow(a,2));		//第一偏心率，0.081819191
//	double e2=Math.sqrt((Math.pow(a,2)-Math.pow(b,2))/Math.pow(b,2));		//第二偏心率，0.082094438



	//法向量
	public class NodeN{
		float p;			//对应的法向量的横坐标
		float q;
	}

	//方向向量
	public class NodeS{
		int i;
		float x;                //方向向量的横坐标
		float y;
		public float paof;
		public float speed;
		public float flow;

		public NodeN n = new NodeN();         //法向量
		public NodeS next;

		//这个没用到
		public void SetNext(NodeS next) {
			this.next = next;
		}

	}

	NodeS A;
	NodeS B;
	NodeS C;
	NodeS D;

	NodeS ab;
	NodeS ba;
	NodeS bc;
	NodeS cb;
	NodeS cd;
	NodeS dc;
	NodeS da;
	NodeS ad;



	//求解平移向量
	@SuppressWarnings("null")
	public NodeS GotoMobile(NodeS m,NodeS n,float d) {

		//定义变量

		NodeS tmp=new NodeS();  //返回平移向量

		NodeS r1 =new NodeS();  //单位向量1
		NodeS r2 =new NodeS();  //单位向量2
		float sino = 0;  //夹角的正弦
		float coso = 0;  //夹角的余弦
		float Mm   = 0;  //向量m的模
		float Mn   = 0;  //向量n的模

		//求模
		Mm = (float) Math.sqrt(Math.pow(m.x, 2)+Math.pow(m.y, 2));

		Mn = (float) Math.sqrt(Math.pow(n.x, 2)+Math.pow(n.y, 2));

		//求解正弦值
		//A·B = |A|·|B|cosΘ
		coso = (m.x*n.x+m.y*n.y)/(Mm*Mn);

		sino = (float) Math.sqrt(1-Math.pow(coso, 2));

		//求解单位向量
		r1.x = (d/sino)*(m.x/Mm);
		r1.y = (d/sino)*(m.y/Mm);

		r2.x = (d/sino)*(n.x/Mn);
		r2.y = (d/sino)*(n.y/Mn);

		//求解平移向量
		tmp.x = r1.x+r2.x;
		tmp.y = r1.y+r2.y;

		return tmp;
	}

	//获取TextField的边界数据


	//师兄的
	public void GetPoint() {
		point1 = new double[][]{{24, 8}, {13, 75}, {84, 82}, {100, 29}};

	/*	for(int i = 0 ;i<4;i++)
			for(int j=0;j<2;j++){
				//创建Scanner对象，接受从控制台输入
//				@SuppressWarnings("resource")

//				Scanner input=new Scanner(System.in);
				//接受String类型
//				double f =input.nextDouble();
				//输出结果
//				LatLng[i][j] = f;
//				point[i][j]= f;

			}*/





        //先获得第一圈的四个点坐标
		for(int i=0;i<4;i++) {
			point[i][0] = point1[i][0];
			point[i][1] = point1[i][1];

			/*System.out.print("GetPoint:第 "+i+"个点坐标： ");
				System.out.println("("+LatLng[i][0]+","+LatLng[i][1]+")");
			System.out.println("("+point[i][0]+","+point[i][1]+")");*/
			Log.e("Ting  点个数","GetPoint:第 "+i+"个点坐标： ");
			Log.e("Ting  点坐标","("+point[i][0]+","+point[i][1]+")");

		}

	}





	//坐标转换，经纬度转换为平面坐标系

	//由已知的经纬度坐标(L2,B)转换为高斯坐标(x1,y1)的高斯投影正算公式

	/*	void Convert() {

			for(int i=0;i<4;i++) {
				//System.out.print("第 "+i+"个点经纬度坐标： ");
				//System.out.println("("+LatLng[i][0]+","+LatLng[i][1]+")");

				double B=LatLng[i][0];		//B是纬度

				double L2=LatLng[i][1];		//L2是经度



			int N=(int)Math.floor((L2/Math.PI*180+3)/6+0.5);		//N是带号

			double k0=(1+3/4*Math.pow(e1,2)+45/64*Math.pow(e1,4)+175/256*Math.pow(e1,6)+11025/16384*Math.pow(e1,8));

			double k2=(1/2*(3/4*Math.pow(e1,2)+15/16*Math.pow(e1,4)+525/512*Math.pow(e1,6)+2205/2048*Math.pow(e1,8)));

			double k4=(1/4*(15/64*Math.pow(e1,4)+105/256*Math.pow(e1,6)+2205/4096*Math.pow(e1,8)));

			double k6=(1/6*(35/512*Math.pow(e1,6)+315/2048*Math.pow(e1,8)));

			double k8=(315/131072*Math.pow(e1,8));

			double L1=((6*N-3)/180*Math.PI);	//L1：中央子午线的经度

			double l=L2-L1;					//l是经差




			// x1、y1是相对于本带原点的高斯平面坐标
			double  x1=(a*(1-Math.pow(e1,2))*(k0*B-k2*Math.sin(2*B)+k4*Math.sin(4*B)-k6*Math.sin(6*B)+k8*Math.sin(8*B))+a/(4*Math.sqrt(1-Math.pow(e1,2)*Math.pow(Math.sin(B),2)))*
				Math.sin(2*B)*Math.pow(l,2)*
				(
					1+1/12*Math.pow(l,2)*Math.pow(Math.cos(B),2)*(5-Math.pow(Math.tan(B),2)+9*Math.pow(e2,2)*Math.pow(Math.cos(B),2)+4*Math.pow(e2,4)*Math.pow(Math.cos(B),4))+
					1/360*Math.pow(l,4)*Math.pow(Math.cos(B),4)*(61-58*Math.pow(Math.tan(B),2)+Math.pow(Math.tan(B),4))
				));

			double y1=(a/Math.sqrt(1-Math.pow(e1,2)*Math.pow(Math.sin(B),2))*Math.cos(B)*l*
			(
				1+1/6*Math.pow(l,2)*Math.pow(Math.cos(B),2)*(1-Math.pow(Math.tan(B),2)+Math.pow(e2,2)*Math.pow(Math.cos(B),2))+
				1/120*Math.pow(l,4)*Math.pow(Math.cos(B),4)*(5-18*Math.pow(Math.tan(B),2)+Math.pow(Math.tan(B),4)+14*Math.pow(e2,2)*Math.pow(Math.cos(B),2)-58*Math.pow(e2,2)*Math.pow(Math.sin(B),2))
			)+500000+1000000*N);


			//笛卡尔坐标(x,y)和高斯坐标(x1, y1)的对应关系

			double x=y1;
			double y=x1;


			point[i][0]=x;

			point[i][1]=y;



			System.out.print("第 "+i+"个点平面坐标： ");
			System.out.println("("+point[i][0]+","+point[i][1]+")");

			}


		}*/









	//求解方向向量

	public NodeS GetD (double m,double n,double p,double q) {


		NodeS tmp = new NodeS();

		tmp.x = (float)(m - p);
		tmp.y = (float)(n - q);
		return tmp;
	}

	public void  Init() {

		//实例化四个平移向量
		A = new NodeS();
		B = new NodeS();
		C = new NodeS();
		D = new NodeS();

		ab = new NodeS();
		ba = new NodeS();
		bc = new NodeS();
		cb = new NodeS();
		cd = new NodeS();
		dc = new NodeS();
		da = new NodeS();
		ad = new NodeS();


	/*	for(int i=0;i<4;i++) {
			System.out.print("Init：第 "+i+"个点坐标： ");
			System.out.println("("+point[i][0]+","+point[i][1]+")");
		}*/

		ab = GetD(point[1][0],point[1][1],point[0][0],point[0][1]);
		ba = GetD(point[0][0],point[0][1],point[1][0],point[1][1]);
		bc = GetD(point[2][0],point[2][1],point[1][0],point[1][1]);
		cb = GetD(point[1][0],point[1][1],point[2][0],point[2][1]);
		cd = GetD(point[3][0],point[3][1],point[2][0],point[2][1]);
		dc = GetD(point[2][0],point[2][1],point[3][0],point[3][1]);
		da = GetD(point[0][0],point[0][1],point[3][0],point[3][1]);
		ad = GetD(point[3][0],point[3][1],point[0][0],point[0][1]);

			/*A = GotoMobile(ab,ad,50);
			B = GotoMobile(bc,ba,50);
			C = GotoMobile(cb,cd,50);
			D = GotoMobile(da,dc,50);*/

		A = GotoMobile(ab,ad,7);
		B = GotoMobile(bc,ba,7);
		C = GotoMobile(cb,cd,7);
		D = GotoMobile(da,dc,7);


	}

	public void PathFunc(NodeS A,NodeS B,NodeS C,NodeS D) {

		float tmp = 0;
		int i = 4;
		float a,b,c,d;


		do{

			point[0+i*4-12][0] = point[0][0]+(i-3)*A.x;
			point[0+i*4-12][1] = point[0][1]+(i-3)*A.y;
			point[1+i*4-12][0] = point[1][0]+(i-3)*B.x;
			point[1+i*4-12][1] = point[1][1]+(i-3)*B.y;
			point[2+i*4-12][0] = point[2][0]+(i-3)*C.x;
			point[2+i*4-12][1] = point[2][1]+(i-3)*C.y;
			point[3+i*4-12][0] = point[3][0]+(i-3)*D.x;
			point[3+i*4-12][1] = point[3][1]+(i-3)*D.y;

			//求出四条边的长度
			//AB
			a = (float)Math.sqrt(Math.pow((point[1+i*4-12][1]-point[0+i*4-12][1]), 2)+Math.pow(point[1+i*4-12][0]-point[0+i*4-12][0], 2));
			//BC
			b = (float)Math.sqrt(Math.pow((point[2+i*4-12][1]-point[1+i*4-12][1]), 2)+Math.pow(point[2+i*4-12][0]-point[1+i*4-12][0], 2));
			//CD
			c = (float)Math.sqrt(Math.pow((point[3+i*4-12][1]-point[2+i*4-12][1]), 2)+Math.pow(point[3+i*4-12][0]-point[2+i*4-12][0], 2));
			//DA
			d = (float)Math.sqrt(Math.pow((point[0+i*4-12][1]-point[3+i*4-12][1]), 2)+Math.pow(point[0+i*4-12][0]-point[3+i*4-12][0], 2));

//			System.out.println(a);
//			System.out.println(b);
//			System.out.println(c);
//			System.out.println(d);
			i++;
			//求出4个值中的最小值
			tmp=BubbleSort(a,b,c,d);
//			System.out.println("mini is :"+tmp);

			Log.e("Ting  点坐标","mini is :"+tmp);

		}while(tmp>7);
//			}while(tmp>500);

		/*		轨迹点结果
2020-05-21 16:49:42.419 12722-12722/com.example.debugdemo E/Ting 第0个:(24.0: 8.0)
2020-05-21 16:49:42.419 12722-12722/com.example.debugdemo E/Ting 第1个:(13.0: 75.0)
2020-05-21 16:49:42.419 12722-12722/com.example.debugdemo E/Ting 第2个:(84.0: 82.0)
2020-05-21 16:49:42.419 12722-12722/com.example.debugdemo E/Ting 第3个:(100.0: 29.0)
2020-05-21 16:49:42.419 12722-12722/com.example.debugdemo E/Ting 第4个:(29.645294189453125: 16.822196006774902)
2020-05-21 16:49:42.419 12722-12722/com.example.debugdemo E/Ting 第5个:(21.117149353027344: 68.76634359359741)
2020-05-21 16:49:42.419 12722-12722/com.example.debugdemo E/Ting 第6个:(78.96139907836914: 74.46929740905762)
2020-05-21 16:49:42.419 12722-12722/com.example.debugdemo E/Ting 第7个:(91.22736263275146: 33.83829307556152)
2020-05-21 16:49:42.420 12722-12722/com.example.debugdemo E/Ting 第8个:(35.29058837890625: 25.644392013549805)
2020-05-21 16:49:42.420 12722-12722/com.example.debugdemo E/Ting 第9个:(29.234298706054688: 62.532687187194824)
2020-05-21 16:49:42.420 12722-12722/com.example.debugdemo E/Ting 第10个:(73.92279815673828: 66.93859481811523)
2020-05-21 16:49:42.420 12722-12722/com.example.debugdemo E/Ting 第11个:(82.45472526550293: 38.67658615112305)
2020-05-21 16:49:42.420 12722-12722/com.example.debugdemo E/Ting 第12个:(40.935882568359375: 34.46658706665039)
2020-05-21 16:49:42.420 12722-12722/com.example.debugdemo E/Ting 第13个:(37.35144805908203: 56.29903030395508)
2020-05-21 16:49:42.420 12722-12722/com.example.debugdemo E/Ting 第14个:(68.88419723510742: 59.40789222717285)
2020-05-21 16:49:42.420 12722-12722/com.example.debugdemo E/Ting 第15个:(73.68208694458008: 43.51487922668457)
2020-05-21 16:49:42.420 12722-12722/com.example.debugdemo E/Ting 第16个:(46.5811767578125: 43.28878402709961)
2020-05-21 16:49:42.420 12722-12722/com.example.debugdemo E/Ting 第17个:(45.468597412109375: 50.06537437438965)
2020-05-21 16:49:42.421 12722-12722/com.example.debugdemo E/Ting 第18个:(63.84559631347656: 51.87718963623047)
2020-05-21 16:49:42.421 12722-12722/com.example.debugdemo E/Ting 第19个:(64.90945053100586: 48.353172302246094)
2020-05-21 16:49:42.421 12722-12722/com.example.debugdemo E/Ting 第20个:(0.0: 0.0)
		* */


		int x = 0;


		if(point!=null) {
			for (x = 0; x < point.length; x++) {
				//取出有用的数据。
				if ((point[x][0] != 0 && point[x][1] != 0) && (point[x + 1][0] == 0 && point[x + 1][1] == 0))
					break;
			}
			temp = x;


			//到这里是对的(4 * i - 11)
			for (int j = 0; j < temp; j++) {


//			s = point[j][0] + "";
//			s1 = point[j][1] + "";

				s = String.format("%.1f", point[j][0]);
				s1 = String.format("%.1f", point[j][1]);

				point[j][0] = Double.parseDouble(s);
				point[j][1] = Double.parseDouble(s1);


//				Log.e("Ting 第" + j + "个:(" + point[j][0], point[j][1] + ")");

				point[j][0] = point[j][0]*10+10000;//平移之后的横坐标
				point[j][1] = point[j][1]*10+10000;//平移之后的纵坐标
//				Log.e("Ting 第" + j + "个:(" + point[j][0], point[j][1] + ")");				//浮点型数据
//				Log.e("Ting 第" + j + "个:(" + (int)point[j][0], (int)point[j][1] + ")");		//整型数据

				//Integer.toHexString((byte)point[j][0]);

				//十六进制整型数据
//				Log.e("Ting 第" + j + "个:(" + Integer.toHexString((int)point[j][0]), Integer.toHexString((int)point[j][1]) + ")");		//十六进制整型数据



				point2[j][0] = Integer.toHexString((int)point[j][0]);
				point2[j][1] = Integer.toHexString((int)point[j][1]);


//				Log.e("Ting String 类型  第" + j + "个:(" +point2[j][0] ,point2[j][1]+")");		//十六进制的String类型数据

				/*
2020-05-25 15:40:10.808 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760
2020-05-25 15:40:10.810 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe
2020-05-25 15:40:10.812 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a44
2020-05-25 15:40:10.815 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832
2020-05-25 15:40:10.817 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b8
2020-05-25 15:40:10.819 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c0
2020-05-25 15:40:10.821 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f9
2020-05-25 15:40:10.822 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa02862
2020-05-25 15:40:10.824 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa0286228712810
2020-05-25 15:40:10.826 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa028622871281028342981
2020-05-25 15:40:10.827 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa02862287128102834298129f329ad
2020-05-25 15:40:10.829 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa02862287128102834298129f329ad2a492893
2020-05-25 15:40:10.831 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa02862287128102834298129f329ad2a49289328a92869
2020-05-25 15:40:10.833 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa02862287128102834298129f329ad2a49289328a9286928862943
2020-05-25 15:40:10.834 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa02862287128102834298129f329ad2a49289328a928692886294329c12962
2020-05-25 15:40:10.836 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa02862287128102834298129f329ad2a49289328a928692886294329c1296229f128c3
2020-05-25 15:40:10.837 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa02862287128102834298129f329ad2a49289328a928692886294329c1296229f128c328e228c1
2020-05-25 15:40:10.839 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa02862287128102834298129f329ad2a49289328a928692886294329c1296229f128c328e228c128d72905
2020-05-25 15:40:10.840 23332-23332/com.example.debugdemo E/Ting String 类型: 28002760279229fe2a582a442af82832283827b827e329c02a2629f92aa02862287128102834298129f329ad2a49289328a928692886294329c1296229f128c328e228c128d72905298e2917
				*
				* */
				//加空格是为了方便对字符串进行拆分。
				pointNum = pointNum + point2[j][0] +" "+point2[j][1]+" ";
				Log.e("Ting String 类型",pointNum+"");

			/*  Byte aa  = (byte)((Byte.parseByte(Integer.toHexString((int)point[j][0]))>>8)&0xff);	//横坐标高八位
				Byte ab  = (byte)(Byte.parseByte(Integer.toHexString((int)point[j][0]))&0xff);		//横坐标低八位

				Byte ba  = (byte)((Byte.parseByte(Integer.toHexString((int)point[j][1]))>>8)&0xff);	//纵坐标高八位
				Byte bb  = (byte)(Byte.parseByte(Integer.toHexString((int)point[j][1]))&0xff);		//纵坐标低八位

				Log.e("Ting 第" + j + "个:(" + aa+""+ab +" ", ""+ba+""+bb+ ")");		//十六进制高低八位 整型数据
*/

                //这几位没用到
				byte aa  = (byte)((byte)(Integer.parseInt(Integer.toString((int)point[j][0]))>>8)&0xff);	//横坐标高八位			结果为二进制形式，有符号数据，会出现负号
				byte ab  = ((byte)(Integer.parseInt(Integer.toString((int)point[j][0]))&0xff));				//横坐标低八位			结果为二进制形式，有符号数据，会出现负号

				byte ba  = (byte)((byte)(Integer.parseInt(Integer.toString((int)point[j][1]))>>8)&0xff);	//纵坐标高八位			结果为二进制形式，有符号数据，会出现负号
				byte bb  = ((byte)(Integer.parseInt(Integer.toString((int)point[j][1]))&0xff));				//纵坐标低八位			结果为二进制形式，有符号数据，会出现负号


//				Log.e("Ting 第" + j + "个:(" + aa+"   "+ab +"   ", "   "+ba+"   "+bb+ ")");		//十六进制高低八位 整型数据

			}







		}


	}




	//反向坐标转换，平面坐标系转换为经纬度
	/*
		void DisConvert(int k) {


			for(int i=0;i<k;i++){


			double x=point[i][0];

			double y=point[i][1];

			double x1=y;
			double y1=x;



			double t=((1-b/a)/(1+b/a));

			double φ=(x1/a/(1-Math.pow(e1,2)/4-3*Math.pow(e1,4)/64-5*Math.pow(e1,6)/256));


			double B0=(φ+(3*t/2-27*Math.pow(t,3)/32)*Math.sin(2*φ)+(21*Math.pow(t,2)/16-55*Math.pow(t,4)/32)*Math.sin(4*φ)+151*Math.pow(t,3)/96*Math.sin(6*φ));

			double N0=(a/Math.sqrt(1-Math.pow(e1*Math.sin(B0),2)));


			double R0=(a*(1-Math.pow(e1,2))/Math.pow(1-Math.pow(e1*Math.sin(B0),2),3/2));



			int N=(int)Math.floor(y1/1000000);

			double T=500000+N*1000000;		//T为y坐标的偏移量，m

			double D=(y1-T)/N0;		//m



			double  L1=((6*(N-1)+3)*Math.PI/180);

			double B=(B0-N0*Math.tan(B0)/R0*(
					Math.pow(D,2)/2-Math.pow(D,4)/24*(5+3*Math.pow(Math.tan(B0),2)+Math.pow(e2,2)*Math.pow(Math.cos(B0),2)-9*Math.pow(e2,2)*Math.pow(Math.sin(B0),2))+
					Math.pow(D,6)/720*(61+90*Math.pow(Math.tan(B0),2)+45*Math.pow(Math.tan(B0),4))));

			double L2=(L1+1/Math.cos(B0)*(
					D-Math.pow(D,3)/6*(1+2*Math.pow(Math.tan(B0),2)+Math.pow(e2,2)*Math.pow(Math.cos(B0),2))+
					Math.pow(D,5)/120*(5+28*Math.pow(Math.tan(B0),2)+6*Math.pow(e2,2)*Math.pow(Math.cos(B0),2)+8*Math.pow(e2,2)*Math.pow(Math.sin(B0),2)+24*Math.pow(Math.tan(B0),4))));


			LatLng[i][0]=B;

			LatLng[i][1]=L2;



			if(i%4==0) {
				System.out.println("第 "+ i/4 +" 圈.......:");
			}
			System.out.println("纬度："+LatLng[i][0]+"  经度："+LatLng[i][1]);

			}
		}



		*/





	//求出最小值
	public float BubbleSort(float a,float b,float c,float d) {
		float tmp=0;
		float [] arr = new float [4];

		arr[0]=a;
		arr[1]=b;
		arr[2]=c;
		arr[3]=d;


		//将最小的一个数值，移位到数组第一位。求出最小值
		for(int i=0;i<arr.length-1;i++){
			for(int j=0;j<arr.length-i-1;j++){
				if(arr[j+1]<=arr[j]){
					tmp = arr[j];
					arr[j] = arr[j+1];
					arr[j+1] = tmp;
				}
			}
		}
		tmp = arr[0];

		return tmp;
	}




	public TrajectoryPlanning(Auto father,String initpoint,int length) {


		point = new double [500][2];//(1,1)(1,70)(100,70)(100,1)
		point2 = new String[500][2];


//			LatLng = new double[300][7];	//开辟存放100个经纬度点的内存空间

		GetPoint();


//			Convert();			//坐标转换，经纬度转换为平面坐标系


		Init();
		PathFunc(A,B,C,D);

//			DisConvert(12);		//反向坐标转换，平面坐标系转换为经纬度



//		p.Pointset(point);
	}




}

