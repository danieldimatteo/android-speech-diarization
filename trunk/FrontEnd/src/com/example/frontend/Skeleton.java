package com.example.frontend;

import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.thesis.skeleton.R;

//imports for LIUM_SpkDiarization
import fr.lium.spkDiarization.lib.DiarizationException;
import fr.lium.spkDiarization.programs.MClust;
import fr.lium.spkDiarization.programs.MSeg;


public class Skeleton extends Activity {
    public AudioRecordWrapper recorderWrapper;
    public RawAudioPlayback audioPlayer;
    ProgressDialog progressDialog;
	ProgressDialog mProgressDialog;
	ProgressDialog dProgressDialog;
    //Dialog aliases
	public static final int MFCC_DIALOG = 0;
    public static final int DRZ_DIALOG = 1;
    //File Locations
    public static final String AUDIO_FILE = "/sdcard/recordoutput.raw";
    public static final String CONFIG_FILE = "/sdcard/config.xml";
    public static final String MFCC_FILE = "/sdcard/test.mfc";
    public static final String UEM_FILE = "/sdcard/test.uem.seg";    
    //Audio Settings
    public static final int AUDIO_SOURCE = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    public static final int SAMPLE_RATE = 16000;
    public static final int CHANNELS_IN = AudioFormat.CHANNEL_IN_MONO;
    public static final int CHANNELS_OUT = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int AUDIO_STREAM = AudioManager.STREAM_MUSIC;
    public static final int PLAYBACK_MODE = AudioTrack.MODE_STREAM;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        final Button startBtn = (Button) findViewById(R.id.bgnBtn);

        Button endBtn = (Button) findViewById(R.id.stpBtn);
        
        Button mfccBtn = (Button) findViewById(R.id.mfccBtn);
        
        Button drzBtn = (Button) findViewById(R.id.drzBtn);
        
        Button plyBtn = (Button) findViewById(R.id.plyBtn);
        
        Button stopPlyBtn = (Button) findViewById(R.id.stopPlyBtn);

        startBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                	Context context = getApplicationContext();
                	CharSequence text = "Now recording";
                	int duration = Toast.LENGTH_SHORT;
                	Toast.makeText(context, text, duration).show();
                    
                	startBtn.setBackgroundColor(Color.RED);
                	startBtn.setText("Recording");
                	
                	new recordConvo().execute();    
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
                    
                	startBtn.setBackgroundResource(android.R.drawable.btn_default);
                	startBtn.setText("Begin Recording");
                    
                	Context context = getApplicationContext();
                	CharSequence text = "Finished recording";
                	int duration = Toast.LENGTH_SHORT;
                	Toast.makeText(context, text, duration).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        mfccBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	new makeMFCC().execute();
            }
        });
        
        drzBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	new diarize().execute();
            }
        });
        
        plyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	new playbackRecording().execute();
            }
        });
        
        stopPlyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	audioPlayer.stopPlayback();
            }
        });

    }
    
    @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case MFCC_DIALOG:
				mProgressDialog = new ProgressDialog(this);
				mProgressDialog.setMessage("Computing MFCCs...");
				mProgressDialog.setCancelable(false);
				mProgressDialog.show();
				return mProgressDialog;
			
			case DRZ_DIALOG:
				dProgressDialog = new ProgressDialog(this);
				dProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				dProgressDialog.setMessage("Performing Diarization...");
				dProgressDialog.setCancelable(false);
				dProgressDialog.show();
				return dProgressDialog;
			
			default:
				return null;
		}
	}
    

    private void stopRecording() throws Exception {
        if (recorderWrapper != null) {
            recorderWrapper.stop();
        }
    }
    
    private class recordConvo extends AsyncTask<Void, Void, Void> {
    	
		@Override
		protected Void doInBackground(Void... params) {
			recorderWrapper = new AudioRecordWrapper(AUDIO_FILE, AUDIO_SOURCE, SAMPLE_RATE, CHANNELS_IN, AUDIO_FORMAT);
			recorderWrapper.record();
			return null;
		}

    }
    
    private class makeMFCC extends AsyncTask<Void, Void, Void> {
    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(MFCC_DIALOG);
		}
    	
		@Override
		protected Void doInBackground(Void... params) {
			MfccMaker Mfcc = new MfccMaker(CONFIG_FILE, AUDIO_FILE, MFCC_FILE, UEM_FILE); 
			Mfcc.produceFeatures();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			dismissDialog(MFCC_DIALOG);
		}
    }
    
    private class diarize extends AsyncTask<Void, Integer, Void> {
    	int DONE_LINEARSEG = 50;
    	int DONE_LINEARCLUST = 100;
    	String[] linearSegParams = {"--trace", "--help", "--kind=FULL", "--sMethod=GLR", "--fInputMask=/sdcard/test.mfc", "--fInputDesc=sphinx,1:1:0:0:0:0,13,0:0:0", "--sInputMask=/sdcard/test.uem.seg", "--sOutputMask=/sdcard/test.s.seg", "test"};
    	String[] linearClustParams = {"--trace", "--help", "--fInputMask=/sdcard/test.mfc", "--fInputDesc=sphinx,1:1:0:0:0:0,13,0:0:0", "--sInputMask=/sdcard/test.s.seg", "--sOutputMask=/sdcard/test.l.seg", "--cMethod=l", "--cThr=2", "test"};
    	@Override
    	
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DRZ_DIALOG);
		}
    	
		@Override
		protected Void doInBackground(Void... params) {
			try {
				MSeg.main(linearSegParams);
			} catch (DiarizationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setProgress(DONE_LINEARSEG);


			try {
				MClust.main(linearClustParams);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setProgress(DONE_LINEARCLUST);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			dismissDialog(DRZ_DIALOG);
		}
		
		@Override
		protected void onProgressUpdate(Integer... value) {
			dProgressDialog.setProgress(value[0]);
		}
    	
    	
    }
    
    private class playbackRecording extends AsyncTask<Void, Void, Void> {
    	@Override
    	protected Void doInBackground(Void... params){
    	    
    		audioPlayer = new RawAudioPlayback(AUDIO_FILE, AUDIO_STREAM, SAMPLE_RATE, CHANNELS_OUT, AUDIO_FORMAT, PLAYBACK_MODE );
    		try {
				audioPlayer.play();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    
    		return null;
    	}
    	
    }
    
    private void killAudioRecord() {
        if (recorderWrapper != null) {
            recorderWrapper.release();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        killAudioRecord();
        audioPlayer.stopPlayback();
    }

}