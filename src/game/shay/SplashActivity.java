package game.shay;

import game.shay.charadaseenigmas.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	
	private Activity activity = this;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
        	View decorView = getWindow().getDecorView();
        	int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        	decorView.setSystemUiVisibility(uiOptions);
        	// Remember that you should never show the action bar if the
        	// status bar is hidden, so hide that too if necessary.
        	//ActionBar actionBar = getActionBar();
        	//actionBar.hide();
        }
        
        setContentView(R.layout.splash);
        
        new Thread(new Runnable() {
			public void run() {
				int segundos = 5;
				while(segundos != 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					segundos--;
				}
				
				Intent i = new Intent(activity, GameActivity.class);				
				activity.startActivityForResult(i, 0);
				
			}
		}).start();
	}
	
	@Override
	public void onBackPressed() {
		return;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK)
			finish();
	}
}
