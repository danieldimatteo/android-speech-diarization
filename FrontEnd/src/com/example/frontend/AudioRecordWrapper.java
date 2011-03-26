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