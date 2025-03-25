package com.cmods.heagoo.huo;

import android.app.*;
import android.os.*;
import com.cmods.heagoo.huo.ParticleView;

public class MainActivity extends Activity 
{
	
	 ParticleView particleView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		particleView = findViewById(R.id.particleView);
    }

	
	@Override
    protected void onResume() {
        super.onResume();
        if (particleView != null) {
            particleView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (particleView != null) {
            particleView.pause();
        }
    }
}
