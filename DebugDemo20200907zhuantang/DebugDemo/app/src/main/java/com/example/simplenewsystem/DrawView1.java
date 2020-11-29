package com.example.simplenewsystem;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;




public class DrawView1 extends View
{


//	public float currentX = 496;
	public float currentX = (float)(Manual.Width/2);
//	public float currentY = 245;
	public float currentY = (float)(Manual.Height/8);
	// 定义、并创建画笔
	Paint p = new Paint();
	Paint p1 = new Paint();
	Paint p2 = new Paint();




	public DrawView1(Context context)
	{
		super(context);
	}
	public DrawView1(Context context , AttributeSet set)
	{
		super(context,set);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		// 设置画笔的颜色
		p.setColor(Color.RED);

		p1.setColor(Color.BLACK);
		p1.setStyle(Paint.Style.STROKE);//空心矩形框
		p1.setStrokeWidth(15);

		p2.setColor(Color.BLACK);


		canvas.drawCircle((float)(Manual.Width/2), (float)(Manual.Height/8), 15,p2);
		// 绘制一个小圆（作为小球）
		canvas.drawCircle(currentX, currentY, 30, p);
		//canvas.drawRect(0,0,992,470, p1);			//绘制矩形

/*
		//绘制圆弧
		RectF rectf_head=new RectF(10, 10, 790, 660);//确定外切矩形范围
		rectf_head.offset(100, 20);//使rectf_head所确定的矩形向右偏移100像素，向下偏移20像素
		canvas.drawArc(rectf_head, 10, -200, true, p1);//绘制圆弧，含圆心
*/



	}





}
