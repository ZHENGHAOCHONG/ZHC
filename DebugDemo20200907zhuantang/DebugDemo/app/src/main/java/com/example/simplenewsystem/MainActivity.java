package com.example.simplenewsystem;

/*
*
* 给用户的产品
* */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;//Bundle：主要用于传递数据(一个简单的数据携带包)
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;//TextUtils类是系统自带的一个工具类
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//这里点击登陆开始就有问题了，和20200610进行对照，对照之后并没有发现有什么不同，明天来看20201022


//类继承了AppCompatActivity会显示标题栏。如果直接继承的是Activity不会出现标题栏
public class MainActivity extends AppCompatActivity {

    String userName;//用户名
    String passWord;//密码
    String SPpwd;
    String user;
    String pass;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bnlogin   = findViewById(R.id.bnlogin);          //登录按钮
        //Button bnregister = findViewById(R.id.register);        //注册按钮

/*

        bnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //备注：
					//Intent(Context packageContext, Class<?> cls)：为特定组件创建意图。
					//Context是第一个参数(之所以使用this是因为Activity类是Context的子类)；
					//应用组件的Class，系统应将Intent(在本例中为应启动的Activity)传递至该类。


                //创建需要启动的register对应的Intent
                Intent intent = new Intent(MainActivity.this,regsiter.class);
                //启动Intent对应的Activity
                Log.i("msg", "register" );
                startActivity(intent);              //开启regsiter页面
            }
        });
*/
        bnlogin.setOnClickListener(new View.OnClickListener() {  //注册按钮已注销
            @Override
            public void onClick(View v) {

//                Log.i("msg", "login" );
                //EditText username = findViewById(R.id.username);            //用户名输入框
				//1.去除密码
//1                EditText password = findViewById(R.id.password);            //密码输入框

                //userName = username.getText().toString().trim();            //获取输入的用户名，去除前后空格
				//1.去除密码
//1                passWord = password.getText().toString().trim();            //获取输入的密码，去除前后空格

				//定义一个用户名和密码，只有用户名和密码都一致的情况下，才可以使用
				//user = "54321";
				//1.去除密码
//1				pass = "54321";


				//1.去除密码
//1               SPpwd = readPsw(userName);          //读取输入的key：用户名，返回对应的value：密码

                //if (TextUtils.isEmpty(userName)) {
                    //this，是上下文参数，指当前页面显示;
                    //是你想要显示的内容;
                    //Toast.LENGTH_SHORT，提示消息显示的时间，大概2秒钟;
                    //show(),显示这个Toast消息提醒
                    //Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();//弃用
//				UIUtils.showToast( MainActivity.this , "请输入用户名" );
                //} else
				//1.去除密码
 //1               	if (TextUtils.isEmpty(passWord)) {
                    //Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();//弃用
				//1.去除密码
//1				 UIUtils.showToast( MainActivity.this , "请输入密码" );
                //} else if (userName.equals(user)&&passWord.equals(pass)) {
				//1.去除密码
	//1			} else if (passWord.equals(pass)) {
				//} else if (SPpwd.equals(passWord)) {

                	/*
                    //登录成功
                    //Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();//弃用
                    UIUtils.showToast( MainActivity.this , "登录成功" );

                    Intent intent2 = getIntent();
                    //直接通过Intent取出它所携带的Bundle数据包中的数据
                    User p = (User) intent2.getSerializableExtra("user");
                    //创建需要启动的System对应的Intent
                    Intent intent = new Intent(MainActivity.this, System.class);
                    //启动Intent对应的Activity
                    startActivity(intent);
                    finish();
					*/


					//登录成功
					//Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();//弃用
					UIUtils.showToast( MainActivity.this , "登录成功" );
					//创建需要启动的System对应的Intent
					Intent intent = new Intent(MainActivity.this, System.class);
					//启动Intent对应的Activity
					startActivity(intent);
					finish();


				//1.去除密码
   //1             } else  {
				//1.去除密码
	//1				Toast.makeText(MainActivity.this, "输入的密码不正确", Toast.LENGTH_SHORT).show();//弃用
				//UIUtils.showToast( MainActivity.this , "输入的密码不正确" );


                //} else if ((SPpwd != null && !TextUtils.isEmpty(SPpwd) && !password.equals(SPpwd))) {
                  //  Toast.makeText(MainActivity.this, "输入的用户名和密码不一致", Toast.LENGTH_SHORT).show();//弃用
				//UIUtils.showToast( MainActivity.this , "输入的用户名和密码不一致" );
				//
               }

			//1.去除密码
  //1.          }
        });
    }

    private String readPsw(String userName){

		/* */
    	//getSharedPreferences("loginInfo",MODE_PRIVATE);
        //"loginInfo",mode_private; MODE_PRIVATE表示可以继续写入
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        //sp.getString() userName, "";
        return sp.getString(userName , "");

    }




}
