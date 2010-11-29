package com.example.frontend;

import java.io.*;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FrontEnd extends Activity {
    private AudioRecord recorder;
    private static final String OUTPUT_FILE = "/sdcard/recordoutput.pcm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Button startBtn = (Button) findViewById(R.id.bgnBtn);

        Button endBtn = (Button) findViewById(R.id.stpBtn);

        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    beginRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        endBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    stopRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void beginRecording() throws Exception {
        // TODO: add exception catching and handling!
    	killAudioRecord();
        
        File outFile = new File(OUTPUT_FILE);

        if(outFile.exists())
        {
            outFile.delete();
        }

        
        int bufferSize = AudioRecord.getMinBufferSize(
				(int)16000, 
				AudioFormat.CHANNEL_IN_MONO, 
				AudioFormat.ENCODING_PCM_16BIT) * 2;
        

        short[] microphoneBuffer = new short[bufferSize];

        
        recorder = new AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        FileOutputStream outFileStream = new FileOutputStream(outFile);
        DataOutputStream dataOutStream = new DataOutputStream(outFileStream);
        
        recorder.startRecording();
                	
        while(recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
	        
        	int numSamplesRead = recorder.read(microphoneBuffer, 0, bufferSize);
	        
	        if(numSamplesRead == AudioRecord.ERROR_INVALID_OPERATION) {
	            throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
	        }
	        else if(numSamplesRead == AudioRecord.ERROR_BAD_VALUE) {
	            throw new IllegalStateException("read() returned AudioRecord.ERROR_BAD_VALUE");
	        }
	        
	        for(int bufferIndex = 0; bufferIndex < numSamplesRead; bufferIndex++) {
	        	dataOutStream.writeShort(microphoneBuffer[bufferIndex]);
	        }
	                
        }

        dataOutStream.close();

    }

    private void stopRecording() throws Exception {
        if (recorder != null) {
            recorder.stop();
        }
    }

    private void killAudioRecord() {
        if (recorder != null) {
            recorder.release();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        killAudioRecord();
    }

}