package com.qmdeve.liquidglass.demo;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.qmdeve.liquidglass.demo.util.Utils;
import com.qmdeve.liquidglass.widget.LiquidGlassView;

public class TouchEffectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_touch_effect);
        Utils.transparentStatusBar(getWindow());
        Utils.transparentNavigationBar(getWindow());

        LiquidGlassView liquidGlassView = findViewById(R.id.liquid_glass_view);
        android.view.ViewGroup contentContainer = findViewById(R.id.content_container);
        
        // Bind the content to be blurred
        liquidGlassView.bind(contentContainer);
        
        // Enable the touch effect (iOS style animation)
        liquidGlassView.setTouchEffectEnabled(true);
        
        // Optional: Configure other properties for better look
        liquidGlassView.setCornerRadius(Utils.dp2px(getResources(), 40));
        liquidGlassView.setBlurRadius(15f);
    }
}