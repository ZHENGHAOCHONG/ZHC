package com.example.simplenewsystem;


//经纬度坐标

public class Point {
	double x;
	double y;

	public Point(double x,double y){
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
}