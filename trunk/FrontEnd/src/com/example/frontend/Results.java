/**
 * Android Speech Diarization - Calculates the amount of time spent speaking
 * 								by each speaker in a conversation
 * 
 * Copyright (C) 2011  Daniel Di Matteo
 *
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.example.frontend;

import edu.thesis.skeleton.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
//import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//public class Results extends Activity {
//    /** Called when the activity is first created. */
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.results);
//
//        Button doneBtn = (Button) findViewById(R.id.doneBtn);
//        
//        doneBtn.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//
//        });
//       
//
//    }
//    
//    public void onStart() {
//    	super.onStart();
//    	TextView results = (TextView) findViewById(R.id.results);
//        
//    	Conversation convo = new Conversation("/sdcard/test.l.seg");
//    	
//    	results.append("\n\nNumber of Speakers: " + Integer.toString(convo.numSpeakers) + "\n");
//    	
//    	for (int i = 0; i < convo.numSpeakers; i++ ) {
//    		results.append("\nSpeaker: " + convo.turns.get(i).speaker );
//    		results.append("\nStart: " + Integer.toString(convo.turns.get(i).start) );
//    		results.append("\nLength: " + Integer.toString(convo.turns.get(i).length) );
//    		results.append("\nEnd: " + Integer.toString(convo.turns.get(i).end) );
//    		results.append("\nPercent Speaking: " + Integer.toString(convo.turns.get(i).percentSpeaking) + "%\n" );
//    	}
//    	
//    	results.invalidate();
//
//    }
//}

public class Results extends Activity {
	
	List<PieChartItem> PieData = new ArrayList<PieChartItem>(0);
	Conversation convo;
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        
        // Get statistics of the conversation
        convo = new Conversation("/sdcard/test.l.seg");

        // Set up button to exit this view
        Button doneBtn = (Button) findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        });
    }
    
    public void onStart() {
    	super.onStart();

    	PieChartItem Item;
        Random mNumGen  = new Random();
        int MaxPieItems = convo.numSpeakers;
        int MaxCount  = 0;
        int ItemCount = 0;

        // Populate chart with data from Conversation
        for (int i = 0; i < MaxPieItems ; i++) {
        	ItemCount  = convo.turns.get(i).length;
        	Item       = new PieChartItem();
        	Item.count = ItemCount;
        	Item.label = "Speaker " + (i+1);
        	Item.colour = 0xff000000 + 256*256*mNumGen.nextInt(256) + 256*mNumGen.nextInt(256) + mNumGen.nextInt(256);
        	PieData.add(Item);
        	MaxCount += ItemCount;
        }

        // Size => Pie size
        int Size = 1000;

        // BgColor  => The background Pie Colour
        int BgColor = 0x00000000;

        // mBackgroundImage  => Temporary image will be drawn with the content of pie view
        Bitmap mBackgroundImage = Bitmap.createBitmap(Size, Size, Bitmap.Config.RGB_565);
        
        // Generating Pie view
        PieChartView PieChart = new PieChartView( this );
        PieChart.setLayoutParams(new LayoutParams(Size, Size));
        PieChart.setGeometry(Size, Size, 2, 2, 2, 2);
        PieChart.setSkinParams(BgColor);
        PieChart.setData(PieData, MaxCount);
        PieChart.invalidate();

        // Draw PieChart View on Bitmap canvas
        PieChart.draw(new Canvas(mBackgroundImage));
        PieChart = null;

        // Create a new ImageView to add to main layout
        ImageView mImageView = new ImageView(this);
	    mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    mImageView.setBackgroundColor(BgColor);
	    mImageView.setImageBitmap( mBackgroundImage );
	    
        // Add Image View to target view
        LinearLayout TargetPieView =  (LinearLayout) findViewById(R.id.pieContainer);
	    TargetPieView.addView(mImageView);
    }
}
