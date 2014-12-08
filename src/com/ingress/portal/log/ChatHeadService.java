package com.ingress.portal.log;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class ChatHeadService extends Service {

	  private WindowManager windowManager;
	  private ImageView chatHead;

	  @Override public IBinder onBind(Intent intent) {
	    // Not used
	    return null;
	  }
	  
	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	    //TODO do something useful
	    return Service.START_NOT_STICKY;
	  }

	  @Override public void onCreate() {
	    super.onCreate();

	    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

	    chatHead = new ImageView(this);
	    chatHead.setImageResource(R.drawable.ic_launcher);

	    final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
	        WindowManager.LayoutParams.WRAP_CONTENT,
	        WindowManager.LayoutParams.WRAP_CONTENT,
	        WindowManager.LayoutParams.TYPE_PHONE,
	        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
	        PixelFormat.TRANSLUCENT);

	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	    windowManager.getDefaultDisplay().getMetrics(displayMetrics);

	    int width = displayMetrics.widthPixels;
	    
	    params.gravity = Gravity.TOP | Gravity.LEFT;
	    params.x = width - 50;
	    params.y = 120;
		    
	    windowManager.addView(chatHead, params);
	    
	    chatHead.setOnLongClickListener(new View.OnLongClickListener() {
			  @Override public boolean onLongClick(View v) {
				  Intent dialogIntent = new Intent(getBaseContext(), MainActivity.class);
				  dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				  getApplication().startActivity(dialogIntent);
				  return true;
			  }
			});
	    
	    chatHead.setOnClickListener(new View.OnClickListener() {
			  @Override public void onClick(View v) {
				  Intent dialogIntent = new Intent(getBaseContext(), MyDialog.class);
				  dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				  getApplication().startActivity(dialogIntent);
			  }
			});	    
	  }

	  @Override
	  public void onDestroy() {
	    super.onDestroy();
	    if (chatHead != null) windowManager.removeView(chatHead);
	  }
	}