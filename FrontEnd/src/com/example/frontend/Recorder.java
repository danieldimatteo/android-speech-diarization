package com.example.frontend;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;

class RecordAudio extends AsyncTask<AudioRecord, Void , Void> {
    String AUDIO_FILE = "/sdcard/recordoutput.raw";

	@Override
	protected Void doInBackground(AudioRecord... recorder) {
		if (recorder[0] != null) {
            recorder[0].release();
        }
        
        File outFile = new File(AUDIO_FILE);

        if(outFile.exists())
        {
            outFile.delete();
        }

        
        int bufferSize = AudioRecord.getMinBufferSize(
				(int)8000, 
				AudioFormat.CHANNEL_IN_MONO, 
				AudioFormat.ENCODING_PCM_16BIT) * 4;
        

        short[] microphoneBuffer = new short[bufferSize];

        
        recorder[0] = new AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        FileOutputStream outFileStream = null;
		try {
			outFileStream = new FileOutputStream(outFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        DataOutputStream dataOutStream = new DataOutputStream(outFileStream);
        
        recorder[0].startRecording();
                	
        while(recorder[0].getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
	        
        	int numSamplesRead = recorder[0].read(microphoneBuffer, 0, bufferSize);
	        
	        if(numSamplesRead == AudioRecord.ERROR_INVALID_OPERATION) {
	            throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
	        }
	        else if(numSamplesRead == AudioRecord.ERROR_BAD_VALUE) {
	            throw new IllegalStateException("read() returned AudioRecord.ERROR_BAD_VALUE");
	        }
	        
	        for(int bufferIndex = 0; bufferIndex < numSamplesRead; bufferIndex++) {
	        	try {
					dataOutStream.writeShort(microphoneBuffer[bufferIndex]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	                
        }

        try {
			dataOutStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}