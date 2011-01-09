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
import android.media.AudioRecord;
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

public class Skeleton extends Activity {
    private AudioRecord recorder;
    ProgressDialog progressDialog;
    static final int PROGRESS_DIALOG = 0;
    private static final String OUTPUT_FILE = "/sdcard/recordoutput.raw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        final Button startBtn = (Button) findViewById(R.id.bgnBtn);

        Button endBtn = (Button) findViewById(R.id.stpBtn);
        
        Button mfccBtn = (Button) findViewById(R.id.mfccBtn);
        
        Button drzBtn = (Button) findViewById(R.id.drzBtn);

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

    }
    
    @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case PROGRESS_DIALOG:
				ProgressDialog mProgressDialog;
				mProgressDialog = new ProgressDialog(this);
				mProgressDialog.setMessage("Computing MFCCs...");
				mProgressDialog.setCancelable(false);
				mProgressDialog.show();
				return mProgressDialog;
			default:
				return null;
		}
	}
    
    class RecordAudio extends AsyncTask<Void, Void, Void> {
    	@Override
		protected Void doInBackground(Void... params) {
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
			showDialog(PROGRESS_DIALOG);
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
			dismissDialog(PROGRESS_DIALOG);
		}
    }
    
    private class diarize extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
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