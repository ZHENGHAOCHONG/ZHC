package com.example.simplenewsystem;

import android.content.Context;
import android.widget.Toast;

//Toast类，用于解决小米MIUI中Toast中出现APP名问题



public class UIUtils {

	private static Toast toast;

	public static void showToast(Context context, String msg) {
		if (toast == null) {
			toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
			toast.setText(msg);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}
}
