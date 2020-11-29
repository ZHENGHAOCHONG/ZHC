package com.example.simplenewsystem;

import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2019-04-22.
 */

public class pathorg {
    Auto father;
    String initpoint;

    public double [][] point ;      		//开辟存放100个点的内存空间
    private int length;

    //方向向量
    public class NodeS{
        double x;                //方向向量的横坐标
        double y;
    }

    private NodeS A;
    private NodeS B;
    private NodeS C;
    private NodeS D;


    //求解平移向量
    @SuppressWarnings("null")
    private  NodeS GotoMobile(NodeS m,NodeS n,double d) {
        //定义变量
        NodeS tmp=new NodeS();  //返回平移向量

        NodeS r1 =new NodeS();  //单位向量1
        NodeS r2 =new NodeS();  //单位向量2
        double sino ;  //夹角的正弦
        double coso ;  //夹角的余弦
        double Mm   ;  //向量m的模
        double Mn   ;  //向量n的模
        //求模
        Mm =  Math.sqrt(Math.pow(m.x, 2)+Math.pow(m.y, 2));

        Mn = Math.sqrt(Math.pow(n.x, 2)+Math.pow(n.y, 2));

        //求解正弦值
        coso = (m.x*n.x+m.y*n.y)/(Mm*Mn);

        sino =  Math.sqrt(1-Math.pow(coso, 2));

        Log.e("GotoMobile:coso= ",String.valueOf(coso) );
        Log.e("GotoMobile:sino= ",String.valueOf(sino) );

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


    //求解方向向量
    private NodeS GetD (double m,double n,double p,double q) {
        NodeS tmp = new NodeS();
        tmp.x = m - p;
        tmp.y = n - q;
        return tmp;
    }

    private void  Init() {
        NodeS ab ;
        NodeS ba;
        NodeS bc;
        NodeS cb;
        NodeS cd;
        NodeS dc;
        NodeS da;
        NodeS ad;

        //实例化四个平移向量
        A = new NodeS();
        B = new NodeS();
        C = new NodeS();
        D = new NodeS();

        //八个方向向量
        ab = GetD(point[1][0],point[1][1],point[0][0],point[0][1]);
        ba = GetD(point[0][0],point[0][1],point[1][0],point[1][1]);
        bc = GetD(point[2][0],point[2][1],point[1][0],point[1][1]);
        cb = GetD(point[1][0],point[1][1],point[2][0],point[2][1]);
        cd = GetD(point[3][0],point[3][1],point[2][0],point[2][1]);
        dc = GetD(point[2][0],point[2][1],point[3][0],point[3][1]);
        da = GetD(point[0][0],point[0][1],point[3][0],point[3][1]);
        ad = GetD(point[3][0],point[3][1],point[0][0],point[0][1]);

        A = GotoMobile(ab,ad,length);
        B = GotoMobile(bc,ba,length);
        C = GotoMobile(cb,cd,length);
        D = GotoMobile(da,dc,length);

        Log.e( "a.x:"+String.valueOf(A.x), "a.y:"+String.valueOf(A.y));
        Log.e( "a.x:"+String.valueOf(B.x), "a.y:"+String.valueOf(B.y));
        Log.e( "a.x:"+String.valueOf(C.x), "a.y:"+String.valueOf(C.y));
        Log.e( "a.x:"+String.valueOf(D.x), "a.y:"+String.valueOf(D.y));

    }

    private void PathFunc(NodeS A,NodeS B,NodeS C,NodeS D) {

        float tmp ;
        int i = 4;
        float a,b,c,d;
        String data = "data show!";

        do{
            Log.e("length", String.valueOf(length) );
            point[i*4-12][0] = point[0][0]+(i-3)*A.x;
            point[i*4-12][1] = point[0][1]+(i-3)*A.y;
            point[1+i*4-12][0] = point[1][0]+(i-3)*B.x;
            point[1+i*4-12][1] = point[1][1]+(i-3)*B.y;
            point[2+i*4-12][0] = point[2][0]+(i-3)*C.x;
            point[2+i*4-12][1] = point[2][1]+(i-3)*C.y;
            point[3+i*4-12][0] = point[3][0]+(i-3)*D.x;
            point[3+i*4-12][1] = point[3][1]+(i-3)*D.y;



            //AB
            a = (float)Math.sqrt(Math.pow((point[1+i*4-12][1]-point[i*4-12][1]), 2)+Math.pow(point[1+i*4-12][0]-point[i*4-12][0], 2));
            Log.e("PathFunc: a =",String.valueOf(a) );
            //BC
            b = (float)Math.sqrt(Math.pow((point[2+i*4-12][1]-point[1+i*4-12][1]), 2)+Math.pow(point[2+i*4-12][0]-point[1+i*4-12][0], 2));
            Log.e("PathFunc: b =",String.valueOf(b) );
            //CD
            c = (float)Math.sqrt(Math.pow((point[3+i*4-12][1]-point[2+i*4-12][1]), 2)+Math.pow(point[3+i*4-12][0]-point[2+i*4-12][0], 2));
            Log.e("PathFunc: c =",String.valueOf(c) );
            //DA
            d = (float)Math.sqrt(Math.pow((point[i*4-12][1]-point[3+i*4-12][1]), 2)+Math.pow(point[i*4-12][0]-point[3+i*4-12][0], 2));
            Log.e("PathFunc: d =",String.valueOf(d) );
            i++;
            tmp=BubbleSort(a,b,c,d);
            Log.e("mini is", String.valueOf(tmp) );
        }while(tmp>length);

        /******************
         for(int j=0;j<(4*i-11);j++) {
         point[j][0] = father.xmin+point[j][0];
         point[j][1] = father.ymin+point[j][1];
         System.out.println("第"+j+"个:"+"("+point[j][0]+","+point[j][1]+")");
         }
         ****************/
        for(int j =0;j<point.length;j++)
            if(point[j][0]+point[j][1]!=0)
                data=Concat(data,"\r\n","（",Double.toString(point[j][0]),",",Double.toString(point[j][1]),")");;

    }

    private String Concat(String s1,String s2,String s3,String s4,String s5,String s6,String s7) {
        return s1+s2+s3+s4+s5+s6+s7;
    }

    private float BubbleSort(float a,float b,float c,float d) {
        float tmp;
        float [] arr = new float [4];

        arr[0]=a;
        arr[1]=b;
        arr[2]=c;
        arr[3]=d;

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

    private void Getpoint(String s){

        point[0][0]=Double.parseDouble(s.substring(3,14))*Math.pow(10,5)-3219760;
        point[0][1]=Double.parseDouble(s.substring(14,26))*Math.pow(10,5)-11951688;
        point[1][0]=Double.parseDouble(s.substring(26,37))*Math.pow(10,5)-3219760;
        point[1][1]=Double.parseDouble(s.substring(37,49))*Math.pow(10,5)-11951688;
        point[2][0]=Double.parseDouble(s.substring(49,60))*Math.pow(10,5)-3219760;
        point[2][1]=Double.parseDouble(s.substring(60,72))*Math.pow(10,5)-11951688;
        point[3][0]=Double.parseDouble(s.substring(72,83))*Math.pow(10,5)-3219760;
        point[3][1]=Double.parseDouble(s.substring(83,95))*Math.pow(10,5)-11951688;

        Log.e(".....1........",String.valueOf(point[0][0]) );
        Log.e("......1.......",String.valueOf(point[0][1]) );
        Log.e(".......2......",String.valueOf(point[1][0]) );
        Log.e("........2.....",String.valueOf(point[1][1]) );
        Log.e(".........3....",String.valueOf(point[2][0]) );
        Log.e("..........3...",String.valueOf(point[2][1]) );
        Log.e("...........4..",String.valueOf(point[3][0]) );
        Log.e("............4.",String.valueOf(point[3][1]) );

    }

    public pathorg(Auto father,String initpoint,int length){
        this.father=father;
        this.initpoint=initpoint;
        this.length = length;

        this.point = new double [100][2];

        Getpoint(initpoint);
        father.setPoint(this.point);
        Init();
        PathFunc(A,B,C,D);

    }
}
