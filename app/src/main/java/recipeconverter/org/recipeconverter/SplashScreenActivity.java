package recipeconverter.org.recipeconverter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity {

    final static int DefaultSplashDuration = 3000;

    private boolean fired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeActivity();
            }
        }, DefaultSplashDuration);
    }

    private void changeActivity() {
        //finish();
        fired = true;
        Intent intent = new Intent(SplashScreenActivity.this, RecipeActivity.class);
        finish();
        startActivity(intent);
    }

    protected void onResume() {
        super.onResume();
        if (fired)
            changeActivity();
    }
}
