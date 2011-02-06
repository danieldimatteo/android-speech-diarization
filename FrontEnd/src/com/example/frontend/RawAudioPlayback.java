package com.example.frontend;

import android.media.AudioTrack;

public class RawAudioPlayback {
    String audioFile;
    int audioStream;
    int sampleRateInHz;
    int channelConfig;
    int audioFormat;
    int mode;
    AudioTrack track;
	
    public RawAudioPlayback(String audio, int audioStrm, int sampleRt, int channelCfg, int audioFrmt, int playMode ){
    	audioFile = audio;
    	audioStream = audioStrm;
    	sampleRateInHz = sampleRt;
    	channelConfig = channelCfg;
    	audioFormat = audioFrmt; 
    	mode = playMode;
    	
        int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    	
    	track = new AudioTrack(audioStream, sampleRateInHz, channelConfig, audioFormat, bufferSize, mode);
    }
    
    public void play() {

		
		
    	return;
    }
}
