package recipeconverter.org.recipeconverter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity {

    final static int DefaultSplashDuration = 3000;

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
        finish();
        Intent intent = new Intent(SplashScreenActivity.this, RecipeActivity.class);
        startActivity(intent);
    }

}
