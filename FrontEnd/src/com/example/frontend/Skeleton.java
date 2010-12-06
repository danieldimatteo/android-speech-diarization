package com.example.frontend;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.thesis.skeleton.R;

//imports for sphinx4 front end shit
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
    private static final String OUTPUT_FILE = "/sdcard/recordoutput.raw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Button startBtn = (Button) findViewById(R.id.bgnBtn);

        Button endBtn = (Button) findViewById(R.id.stpBtn);
        
        Button mfccBtn = (Button) findViewById(R.id.mfccBtn);

        startBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        mfccBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                	runFeatureFileDumper();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
    
    private void runFeatureFileDumper() throws Exception {
        FrontEnd frontEnd = null;
        StreamDataSource audioSource = null;
        List<float[]> allFeatures;
        int featureLength = -1;
        String configFile = "/sdcard/config.xml";
        String inputAudioFile = "/sdcard/test.wav";
        String outputMfccFile = "/sdcard/test.mfc";
        
        ConfigurationManager cm = new ConfigurationManager(configFile);
    	
        try {
            frontEnd = (FrontEnd) cm.lookup("mfcFrontEnd");
            audioSource = (StreamDataSource) cm.lookup("streamDataSource");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //set source for streaming in audio
        audioSource.setInputStream(new FileInputStream(inputAudioFile), "audio");
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
        
        //write the features to binary file
        DataOutputStream outStream = new DataOutputStream(new FileOutputStream(
        		outputMfccFile));
        outStream.writeInt( allFeatures.size() * featureLength );

        for (float[] feature : allFeatures) {
            for (float val : feature) {
                outStream.writeFloat(val);
            }
        }

        outStream.close();
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