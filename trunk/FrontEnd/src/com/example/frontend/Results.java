/**
 * Android Speech Diarization - Calculates the amount of time spent speaking by each speaker
 * 								in a conversation
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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Results extends Activity {
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

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
    	TextView results = (TextView) findViewById(R.id.results);
        
    	Conversation convo = new Conversation("/sdcard/test.l.seg");
    	
    	results.append("\n\nNumber of Speakers: " + Integer.toString(convo.numSpeakers) + "\n");
    	
    	for (int i = 0; i < convo.numSpeakers; i++ ) {
    		results.append("\nSpeaker: " + convo.turns.get(i).speaker );
    		results.append("\nStart: " + Integer.toString(convo.turns.get(i).start) );
    		results.append("\nLength: " + Integer.toString(convo.turns.get(i).length) );
    		results.append("\nEnd: " + Integer.toString(convo.turns.get(i).end) );
    		results.append("\nPercent Speaking: " + Integer.toString(convo.turns.get(i).percentSpeaking) + "%\n" );
    	}
    	
    	results.invalidate();

    }
}
