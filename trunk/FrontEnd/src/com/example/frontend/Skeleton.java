package com.example.frontend;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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
    static final int MFCC_DIALOG = 0;
    static final int DRZ_DIALOG = 1;
    static final int STREAM_MUSIC  = 0x00000003;
    private static final String AUDIO_FILE = "/sdcard/recordoutput.raw";

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
                	
                	new RecordAudio().execute();    
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
    
    class RecordAudio extends AsyncTask<Void, Void, Void> {
    	@Override
		protected Void doInBackground(Void... params) {
        	killAudioRecord();
            
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

            
            recorder = new AudioRecord(
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
			FrontEnd frontEnd = null;
	        StreamDataSource audioSource = null;
	        List<float[]> allFeatures;
	        int featureLength = -1;
	        String configFile = "/sdcard/config.xml";
	        String inputAudioFile = "/sdcard/recordoutput.raw";
	        String outputMfccFile = "/sdcard/test.mfc";
	        String outputUemFile = "/sdcard/test.uem.seg";
	        String uemSegment;
	        FileWriter uemWriter;
	        
	        ConfigurationManager cm = new ConfigurationManager(configFile);
	        
	        try {
	            frontEnd = (FrontEnd) cm.lookup("mfcFrontEnd");
	            audioSource = (StreamDataSource) cm.lookup("streamDataSource");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        //set source for streaming in audio
	        try {
				audioSource.setInputStream(new FileInputStream(inputAudioFile), "audio");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        allFeatures = new LinkedList<float[]>();
	        
	        //get features from audio
	        try {
	            assert (allFeatures != null);
	            Data feature = frontEnd.getData();
	            while (!(feature instanceof DataEndSignal)) {
	                if (feature instanceof DoubleData) {
	                    double[] featureData = ((DoubleData) feature).getValues();
	                    if (featureLength < 0) {
	                        featureLength = featureData.length;
	                        //logger.info("Feature length: " + featureLength);
	                    }
	                    float[] convertedData = new float[featureData.length];
	                    for (int i = 0; i < featureData.length; i++) {
	                        convertedData[i] = (float) featureData[i];
	                    }
	                    allFeatures.add(convertedData);
	                } else if (feature instanceof FloatData) {
	                    float[] featureData = ((FloatData) feature).getValues();
	                    if (featureLength < 0) {
	                        featureLength = featureData.length;
	                        //logger.info("Feature length: " + featureLength);
	                    }
	                    allFeatures.add(featureData);
	                }
	                feature = frontEnd.getData();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        //write the MFCC features to binary file
	        DataOutputStream outStream = null;
			try {
				outStream = new DataOutputStream(new FileOutputStream(outputMfccFile));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				outStream.writeInt( allFeatures.size() * featureLength );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        for (float[] feature : allFeatures) {
	            for (float val : feature) {
	                try {
						outStream.writeFloat(val);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        }

	        try {
				outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//write initial segmentation file for LIUM_SpkDiarization
			uemSegment = String.format( "test 1 0 %d U U U S0", allFeatures.size() );
			try {
				uemWriter = new FileWriter( outputUemFile );
				uemWriter.write(uemSegment);
				uemWriter.flush();
				uemWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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