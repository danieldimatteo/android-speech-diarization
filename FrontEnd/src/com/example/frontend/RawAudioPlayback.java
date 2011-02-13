package com.example.frontend;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.media.AudioTrack;

public class RawAudioPlayback {
    String audioFile;
    int audioStream;
    int sampleRateInHz;
    int channelConfig;
    int audioFormat;
    int mode;

    AudioTrack track;
    short[] buffer;
    
    File audioSampleFile;
    FileInputStream fileStream;
    DataInputStream sampleStream;
    long numberSamples;
    long samplesRead;
	
    
    public RawAudioPlayback(String audio, int audioStrm, int sampleRt, int channelCfg, int audioFrmt, int playMode ){
    	audioFile = audio;
    	audioStream = audioStrm;
    	sampleRateInHz = sampleRt;
    	channelConfig = channelCfg;
    	audioFormat = audioFrmt; 
    	mode = playMode;
    	
        int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    	track = new AudioTrack(audioStream, sampleRateInHz, channelConfig, audioFormat, bufferSize, mode);
    	
    	buffer = new short[1024];
    	
    	audioSampleFile = new File(audio);
    	numberSamples = audioSampleFile.length() / 2;
    	try {
			fileStream = new FileInputStream(audioSampleFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sampleStream = new DataInputStream(fileStream);
    	
    }
    
    public void play() throws IOException {
    	track.play();
    	while (samplesRead < numberSamples){
    		this.fillBuffer();
    		track.write(buffer, 0, buffer.length);
    	}
    	
    	track.flush();
    	track.stop();
    	track.release();

    	return;
    }
    
    public void fillBuffer(){
    	int i;
    	for (i = 0; i<buffer.length; i++  ){
    		if (samplesRead < numberSamples){
	    		try {
					buffer[i] = sampleStream.readShort();
					this.samplesRead++;
	
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    
    public void stopPlayback(){
		if (track != null) {
			track.flush();
			track.stop();
            track.release();
        }
    }
    
    public void release(){
		if (track != null) {
            track.release();
        }
    }
    
}
