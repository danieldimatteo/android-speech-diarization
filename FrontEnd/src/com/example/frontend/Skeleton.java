package com.example.frontend;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioRecord;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.thesis.skeleton.R;

//imports for sphinx4 front end
import edu.cmu.sphinx.frontend.Data;
import edu.cmu.sphinx.frontend.DataEndSignal;
import edu.cmu.sphinx.frontend.DoubleData;
import edu.cmu.sphinx.frontend.FloatData;
import edu.cmu.sphinx.frontend.FrontEnd;
import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

//imports for LIUM_SpkDiarization
import fr.lium.spkDiarization.lib.DiarizationException;
import fr.lium.spkDiarization.programs.MClust;
import fr.lium.spkDiarization.programs.MSeg;


public class Skeleton extends Activity {
    private AudioRecord recorder;
    ProgressDialog progressDialog;
	ProgressDialog mProgressDialog;
	ProgressDialog dProgressDialog;
    //Flags, etc.
	static final int MFCC_DIALOG = 0;
    static final int DRZ_DIALOG = 1;
    static final int STREAM_MUSIC  = 0x00000003;
    //File Locations
    static final String AUDIO_FILE = "/sdcard/recordoutput.raw";
    static final String configFile = "/sdcard/config.xml";
    static final String outputMfccFile = "/sdcard/test.mfc";
    static final String outputUemFile = "/sdcard/test.uem.seg";    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        final Button startBtn = (Button) findViewById(R.id.bgnBtn);

        Button endBtn = (Button) findViewById(R.id.stpBtn);
        
        Button mfccBtn = (Button) findViewById(R.id.mfccBtn);
        
        Button drzBtn = (Button) findViewById(R.id.drzBtn);
        
        Button plyBtn = (Button) findViewById(R.id.plyBtn);

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
                	
                	new RecordAudio().execute(recorder);    
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
        if (recorder != null) {
            recorder.stop();
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
			MfccMaker Mfcc = new MfccMaker(configFile, AUDIO_FILE, outputMfccFile, outputUemFile); 
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
//    	    short[] buffer;
//
//    	    int minSize =AudioTrack.getMinBufferSize( 
//    	    		(int) 8000, 
//    	    		AudioFormat.CHANNEL_CONFIGURATION_MONO, 
//    	    		AudioFormat.ENCODING_PCM_16BIT );        
//    	    
//    	    AudioTrack recording = new AudioTrack( 
//    	    		AudioManager.STREAM_MUSIC, 
//    	    		(int) 8000, 
//    	            AudioFormat.CHANNEL_CONFIGURATION_MONO,
//    	            AudioFormat.ENCODING_PCM_16BIT, 
//    	            minSize, 
//    	            AudioTrack.MODE_STREAM);
//    	    
//    	    recording.play();
//    	    
//    	    //read PCM sample from recording
//    	    try {
//				FileInputStream audioStream = new FileInputStream(AUDIO_FILE);
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
    	    
    	    
    		return null;
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