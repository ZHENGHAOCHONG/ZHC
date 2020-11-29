package com.example.simplenewsystem;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import java.text.DecimalFormat;
import java.time.Month;
import java.time.MonthDay;

import static com.baidu.location.e.k.X;

public class test extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
//    String cmd ="AAA0001:0";  //用string肯定不对，用string后面所有的都要动了，依旧用byte来找位置
    String cmd;
    String[] info;
    String[] CommandCode;

    int xx,yy;

    Button Button1;
    private Object MonthDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        init();
        StringAppend(cmd,string);
    }

    public void panduan(){
        double dl;
        int[] notex = new int[100];  //这里不知道notex的大小，暂时定为100  20201104
        int[] notey = new int[100];  //这里的notey和notex一个意思，回头再找指向什么
        String[] CommandCode = new String[28];
        CommandCode[0]= String.valueOf(4);
        CommandCode[1]= String.valueOf(5);
        CommandCode[2]= String.valueOf(6);
        CommandCode[3]= String.valueOf(7);
        CommandCode[4]= String.valueOf(8);
        CommandCode[5]= String.valueOf(9);
        CommandCode[6]= String.valueOf(0);
        CommandCode[7]= String.valueOf(1);
        int index = Integer.parseInt(null);//这里有问题20201102
        switch (index){
            case 0:
//              cmd = "AAA0001:0"+CommandCode[index].toString();
                cmd = "AAA0001:0"+CommandCode[index]+"\r\n";
            case 1:
                cmd = "AAA0001:0"+CommandCode[index]+"\r\n";
            case 2:
                cmd = "AAA0001:0"+CommandCode[index]+"\r\n";
            case 3:
                cmd = "AAA0001:0"+CommandCode[index]+"\r\n";
            case 4:
                cmd = "AAA0001:0"+CommandCode[index]+"\r\n";
            case 5:
                cmd = "AAA0001:0"+CommandCode[index]+"\r\n";
            case 6:
                cmd = "AAA0001:1"+CommandCode[index]+"\r\n";
            case 7:
                cmd = "AAA0001:1"+CommandCode[index]+"\r\n";
            case 8:
                cmd = "AAA0001:12"+ MonthDay+";"; //这里暂时写下MonthDay，后面再改20201102
                for(int i =1;i<notex.length;i++){
                    dl = notex[i]/100;
                    if (dl>=0){
                        cmd = cmd + "E";
                    }else {
                        cmd = cmd + "W";
                    }
                    dl = Math.abs(dl);
                    cmd = cmd + ChangeDoubletoString(dl/1000)+ChangeDoubletoString((dl%1000)/100)+ChangeDoubletoString((dl%100)/10)+"."+ChangeDoubletoString(dl%10)+",";
                    dl = notey[i]/100;
                    if(dl>=0){
                        cmd = cmd + "S";
                    }else{
                        cmd = cmd + "N";
                    }
                    dl = Math.abs(dl);
                    cmd = cmd +ChangeDoubletoString(dl/1000)+ChangeDoubletoString((dl%1000)/100)+ChangeDoubletoString((dl%100)/10)+"."+ChangeDoubletoString(dl%10)+",";
                    //这里没有写出来，对于MapParal1函数不知道意思是什么20201104
//                    dl = MapParal1:cmd = cmd + ChangeDoubletoString(dl/10)+"."+ChangeDoubletoString(dl%10)+",";
//                    dl = MapParal2:cmd = cmd + ChangeDoubletoString(dl/10)+"."+ChangeDoubletoString(dl%10)+";";
                    cmd = cmd + "\r\n";
                }
            case 9:
                cmd = "AAA0001:13"+"\r\n";
            case 10:
                break;

        }

    }

    public void panduan2(){
        String[] CommandCode = new String[28];
        CommandCode[0] = String.valueOf(4);
        CommandCode[1] = String.valueOf(5);
        CommandCode[2] = String.valueOf(6);
        CommandCode[3] = String.valueOf(7);
        CommandCode[4] = String.valueOf(8);
        CommandCode[5] = String.valueOf(9);
        CommandCode[6] = String.valueOf(0);
        CommandCode[7] = String.valueOf(1);
        int index = Integer.parseInt(null);
        switch (index){
            case 0:
//                clrMap
//                readMap
            case 1:
//                clrMap
//                readMap
            case 2:
                cmd = "AAA0001:15"+"\r\n";  //正向转塘
            case 3:
                cmd = "AAA0001:16"+"\r\n";  //反向转塘
            case 4:
                cmd = "AAA0001:17"+"\r\n";  //下锚
            case 5:
                cmd = "AAA0001:18"+"\r\n";  //起锚
            case 6:
                break;

        }
    }

    public void banzidong(){

        double a1,a2,a3,a4,a5;
//        Log.i("前往",xx);
        cmd = "AAA0001:03,";
        if(xx>0){
            cmd = cmd+"E"+ChangeInttoString((xx/1000))+ChangeInttoString((xx%1000)/100)+ChangeInttoString((xx%100)/10)+"."+ChangeInttoString(xx%10);
        }else{
            cmd = cmd+"W"+ChangeInttoString((-xx)/1000)+ChangeInttoString(((-xx)%1000)/100)+ChangeInttoString(((-xx)%100)/10)+"."+ChangeInttoString((-xx)%10);
        }
        if(yy>0){
            cmd = cmd+",N"+ChangeInttoString(yy/1000)+ChangeInttoString((yy%1000)/100)+ChangeInttoString((yy%100)/10)+"."+ChangeInttoString(yy%10);
        }else{
            cmd = cmd+",S"+ChangeInttoString((-yy)/1000)+ChangeInttoString(((-yy)%1000)/100)+ChangeInttoString(((-yy)%100)/10)+"."+ChangeInttoString((-yy)%10);
        }
        cmd = cmd+";00.0,00.0,00.0,00.0"+"\r\n";
        //下面还有一段不会写 shpTargate 20201105
        /**
         * shpTargate.Top = Y - shpTargate.Width/2;
         * shpTargate.Left = X - shpTargate.Width/2;
         * MCtrlm = 0        ???
         * ball.Top = 0-ball.Width/2;
         * ball.Left = 0-ball.Width/2;
         *
         *  lnToXY(0).X1 = X;
         *  lnToXY(0).Y1 = Y;
         *  lnToXY(0).X2 = shpNow.Left + shpNow.Width / 2;
         *  lnToXY(0).Y2 = shpNow.Top + shpNow.Height / 2;
         *
         * a1 = X;
         * a2 = Y;                 //航迹起点
         * a3 = lnToXY(0).X2;
         * a4 = lnToXY(0).Y2;      //航迹终点
         * If (((a3 - a1) < 0.0001) && ((a3 - a1) > -0.0001)){
         *   a3 = a1 + 0.0001;
         * }
         * a5 = -1 * Atn((a4 - a2) / (a3 - a1));
         *
         * HJWidth = 500
         * lnToXY(1).X1 = X + HJWidth * Sin(a5);
         * lnToXY(1).Y1 = Y + HJWidth * Cos(a5);
         *
         * lnToXY(1).X2 = lnToXY(0).X2 + HJWidth * Sin(a5);
         * lnToXY(1).Y2 = lnToXY(0).Y2 + HJWidth * Cos(a5);
         * lnToXY(2).X1 = X - HJWidth * Sin(a5);
         * lnToXY(2).Y1 = Y - HJWidth * Cos(a5);
         * lnToXY(2).X2 = lnToXY(0).X2 - HJWidth * Sin(a5);
         * lnToXY(2).Y2 = lnToXY(0).Y2 - HJWidth * Cos(a5);
         * */
    }

    public void shoudong(){ //20201105
//        ball.Top = 0 - ball.Width / 2;  中间手控的球的最高点=负的球半径
//        ball.Left = 0 - ball.Width / 2;  手控的球的左边的点=负的球半径
//        MCtrlWork = 0;
//        PWM(0) = 0; PWM(1) = 0; PWM(2) = 0; PWM(3) = 0; 就空闲状态
        cmd = "AAA0001:01"+"\r\n";
        for (int i=0;i<=3;i++){
//        shpT(i).Top = pctCtrl.ScaleTop + pctCtrl.ScaleHeight - 20 - PWM(i);
//        shpT(i).Left = pctCtrl.ScaleLeft + i * 3 * pctCtrl.ScaleWidth / 11;
//        shpT(i).Width = pctCtrl.ScaleWidth * 2 / 11;
//        shpT(i).Height = 20 + PWM(i);
        }
    }

    public void zuoyekongzhi(){
        int KeyAscii;
        if (KeyAscii == 32){  //通过键盘给定的输入，ascii码=32就代表空格，空闲状态
//        MCtrlWork = 0；
//        PWM(0) = 0；
//        PWM(1) = 0；
//        PWM(2) = 0；
//        PWM(3) = 0；
        } else if (KeyAscii ==97 || KeyAscii ==65){  //按键输入为A或a字母,PWM(0) 抛盘
//             MCtrlWork = 1;
//             PWM(0) = PWM(0) + 10;
//             if (PWM(0) > 70) { PWM(0) = 70; }
            }else if (KeyAscii ==115 ||KeyAscii ==83){ //按键输入为S或s字母,PWM(1) 下料
//            MCtrlWork = 1;
//            PWM(1) = PWM(1) + 10;
//            if (PWM(1) > 70) { PWM(1) = 70; }
        }else if(KeyAscii ==100 ||KeyAscii ==68){ //按键输入为D或d字母,PWM(2) 泵
//            MCtrlWork = 1;
//            PWM(2) = PWM(2) + 10;
//            if (PWM(2) > 70) { PWM(2) = 70; }
        }else if(KeyAscii ==102 ||KeyAscii ==70){ //按键输入为F或f字母,PWM(3) 阀
//            MCtrlWork = 1;
//            PWM(3) = PWM(3) + 5;
//            if (PWM(3) > 35) { PWM(3) = 40; }
        }
        for (int i =0;i<=3;i++){
//            shpT(i).Top = pctCtrl.ScaleTop + pctCtrl.ScaleHeight - 20 - PWM(i);
//            shpT(i).Left = pctCtrl.ScaleLeft + i * 3 * pctCtrl.ScaleWidth / 11;
//            shpT(i).Width = pctCtrl.ScaleWidth * 2 / 11;
//            shpT(i).Height = 20 + PWM(i);
        }
    }

    public void xingshikongzhi(){ //20201109
//        if (MCtrlm ==1){  //前进状态
//            if (Y>0){ Y=0; }
//            if (Y < pctCtrl.ScaleTop){Y = pctCtrl.ScaleTop; }
//            if (X > (pctCtrl.ScaleLeft + pctCtrl.ScaleWidth)){X = pctCtrl.ScaleLeft + pctCtrl.ScaleWidth; }
//            if (X < pctCtrl.ScaleLeft) {X = pctCtrl.ScaleLeft;}
//            ball.Top = Y - ball.Width / 2;
//            ball.Left = X - ball.Width / 2;
//        }else if (MCtrlm ==2){ //后退状态
//            if(Y<0){Y=0}
//            if (Y > (pctCtrl.ScaleTop + pctCtrl.ScaleHeight)){Y = pctCtrl.ScaleTop + pctCtrl.ScaleHeight; }
//            if (X > (Y * Line6.X2 / Line6.Y2)){X = Y * Line6.X2 / Line6.Y2;}
//            if (X < (-1 * Y * Line6.X2 / Line6.Y2)){X = -1 * Y * Line6.X2 / Line6.Y2;}
//            ball.Top = Y - ball.Width / 2;
//            ball.Left = X - ball.Width / 2;
//        }else  if(MCtrlm ==0){ //当前空闲
//            if (Y<0){MCtrlm ==1;}
//            if (Y>0){MCtrlm ==2;}
//        }

    }

    public void dingshishuaxin(){ //20201109
        double a,b;
        long X,Y;
        String aaa;
        int DirtyCounter;
        if(Me.ActiveControl.Name ="pctCtrl"){  //失去焦点
            DirtyCounter=0;
        }else if (DirtyCounter <20){
            DirtyCounter =DirtyCounter+1;
        }
        if ((ball.Left <=(-1*ball.Width/2+30)) && (ball.Left >=(-1*ball.Width/2-30)) && (ball.Top <=(-1*ball.Width/2+30)) && (ball.Top >=(-1*ball.Width/2-30))){
//            MCtrlm =0;
//            ball.Left =-1*ball.Width/2;
//            ball.Top =-1*ball.Width/2;
            cmd = "AAA0001:02;+00.0,+00.0,";
        }else {
            X = (int)(ball.Left+ball.Width/2);
            Y = (int)(ball.Top+ball.Width/2);
            a=Math.sqrt(X*X +Y*Y)/10;
            if (a < 10){ a=0;}
            b =0;
            if (X<>0){
                b =Math.abs(Math.atan(Y/X)*2/3.1415926);
            }
            if (Y <=0){
                if (a>(-1*pctCtrl.ScaleTop/10)){
                    a= -1*pctCtrl.ScaleTop/10;
                }
            }
            if (X>0){
                PWML = (int)a;
                PWMR = (int)(a*b);
            }else if (X <0){
                PWMR = (int)a;
                PWML = (int)(a*b);
            }else {
                PWML = (int)a;
                PWMR = (int)a;
            }
            cmd = "AAA0001:02;+"+ChangeInttoString(PWML/10)+ChangeInttoString(PWML%10)+".0,"+"+"+ChangeInttoString(PWMR/10)+ChangeInttoString(PWMR%10)+".0,";
        }
        if (){

        }
        else{ //20201109做到这

        }



//        Else                                        '后退
//        If (a > (pctCtrl.ScaleTop + pctCtrl.ScaleHeight) / 10) Then a = (pctCtrl.ScaleTop + pctCtrl.ScaleHeight) / 10       '反转PWM限幅
//        If (X > 0) Then                         '偏左
//        PWMR = -1 * Int(a): PWML = -1 * Int(a * b)
//        ElseIf (X < 0) Then                     '偏右
//        PWML = -1 * Int(a): PWMR = -1 * Int(a * b)
//        Else                                    '正后
//        PWML = -1 * Int(a): PWMR = -1 * Int(a)
//        End If
//        pctCtrl.BackColor = &HC0FFC0
//                MCtrlCmd = "AAA0001:02,-" & CStr(Abs(PWML) \ 10) & CStr(Abs(PWML) Mod 10) & ".0," _
//                & "-" & CStr(Abs(PWMR) \ 10) & CStr(Abs(PWMR) Mod 10) & ".0,"
//        End If



    }
    public static int DirtyCounter;


    String string ="4";

    //在每个的最外面加一个StringAppend函数，然后直接调用即可。不能用函数，会导致长度冗余，还是用case吧
    public static  StringBuffer StringAppend(String cmd, String string) {
        StringBuffer sb = new StringBuffer(cmd);
        return sb.append(string);

//        String cmd = "AAA0001:0";

    }



        //将目标点数组转化为字符串（保留两位）发送给服务器
    public String  ChangeInttoString(int p){
        String tmp = "#p";
        //格式化类DecimalFormat    #：代表数字，该位不存在就不显示  0：代表数字，该位不存在就显示0
        DecimalFormat df = new DecimalFormat("#.00");
        int i = 0 ;
//        for(;i<p.length();i++){
//            if((p[i][0]+p[i][1])!=0)
//            {
//                tmp += df.format(p[i][0])+","+df.format(p[i][1])+"%";
//            }
//        }

        return tmp;

    }

    //将目标点数组转化为字符串（保留两位）发送给服务器
    public String  ChangeDoubletoString(double p){
        String tmp = "#p";
        //格式化类DecimalFormat    #：代表数字，该位不存在就不显示  0：代表数字，该位不存在就显示0
        DecimalFormat df = new DecimalFormat("#.00");
        int i = 0 ;
//        for(;i<p.length();i++){
//            if((p[i][0]+p[i][1])!=0)
//            {
//                tmp += df.format(p[i][0])+","+df.format(p[i][1])+"%";
//            }
//        }

        return tmp;

    }

    private void init() {
        cmd = new String();

        CommandCode[0]= String.valueOf(4);
        CommandCode[1]= String.valueOf(5);
        CommandCode[2]= String.valueOf(6);
        CommandCode[3]= String.valueOf(7);
        CommandCode[4]= String.valueOf(8);
        CommandCode[5]= String.valueOf(9);
        CommandCode[6]= String.valueOf(0);
        CommandCode[7]= String.valueOf(1);

        Button1 = findViewById(R.id.button1);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
