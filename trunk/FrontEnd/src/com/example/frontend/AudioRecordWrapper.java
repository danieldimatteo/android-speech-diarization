package com.example.frontend;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.media.AudioRecord;

public class AudioRecordWrapper {
    String audioFile;
    int audioSource;
    int sampleRateInHz;
    int channelConfig;
    int audioFormat;
    AudioRecord recorder;
	
    public AudioRecordWrapper(String audio, int audioSrc, int sampleRt, int channelCfg, int audioFrmt){
    	audioFile = audio;
    	audioSource = audioSrc;
    	sampleRateInHz = sampleRt;
    	channelConfig = channelCfg;
    	audioFormat = audioFrmt;   	
    }
    
	public void record() {
		
		if (recorder != null) {
            recorder.release();
        }
        
        File outFile = new File(audioFile);

        if(outFile.exists())
        {
            outFile.delete();
        }

        int bufferSize = AudioRecord.getMinBufferSize(
				sampleRateInHz, 
				channelConfig, 
				audioFormat) * 4;
        
        short[] microphoneBuffer = new short[bufferSize];
        
        recorder = new AudioRecord(
                audioSource,
                sampleRateInHz,
                channelConfig,
                audioFormat,
                bufferSize);

        FileOutputStream outFileStream = null;
		try {
			outFileStream = new FileOutputStream(outFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		return;
	}

	public void stop() {
		recorder.stop();
	}

	public void release() {
		recorder.release();
	}
}