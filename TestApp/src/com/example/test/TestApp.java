package com.example.test;

import android.app.Activity;
import android.os.Bundle;
import fr.lium.spkDiarization.programs.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestApp extends Activity {
    private Button button;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        this.button = (Button) this.findViewById(R.id.button);
        this.button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              finish();
            }
        });

    }
}