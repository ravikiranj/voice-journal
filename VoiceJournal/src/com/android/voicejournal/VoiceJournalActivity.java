package com.android.voicejournal;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VoiceJournalActivity extends Activity {
	private static final String LOG_TAG = "VoiceJournalActivity";
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton mPlayButton = null;
    private MediaPlayer mPlayer = null;

    private TextView mStatusText = null;

    private enum RECORDSTATE {
        START,
        STOPPED
    };

    private enum PLAYSTATE {
        START,
        STOPPED
    };

    public VoiceJournalActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.m4a";
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		// Layout
		RelativeLayout rl = new RelativeLayout(this);
		// Layout Params
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
		    ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT
		);
		rl.setLayoutParams(rlParams);

		// Record button
		RelativeLayout.LayoutParams recordBtnParams = new RelativeLayout.LayoutParams(
		    ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
		);
        recordBtnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        recordBtnParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        recordBtnParams.topMargin = 100;
        mRecordButton = new RecordButton(this);
        Resources resources = getResources();
        mRecordButton.setBackground(resources.getDrawable(R.drawable.media_record_128));
        rl.addView(mRecordButton, recordBtnParams);

        // Play button
		RelativeLayout.LayoutParams playBtnParams = new RelativeLayout.LayoutParams(
		    ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
		);
		playBtnParams.addRule(RelativeLayout.BELOW, mRecordButton.getId());
        playBtnParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        playBtnParams.topMargin = 450;
        mPlayButton = new PlayButton(this);
        mPlayButton.setBackground(resources.getDrawable(R.drawable.media_play_128));
        rl.addView(mPlayButton, playBtnParams);

        // Status text
		RelativeLayout.LayoutParams statusTextParams = new RelativeLayout.LayoutParams(
		    ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
		);
		statusTextParams.addRule(RelativeLayout.BELOW, mPlayButton.getId());
        statusTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        statusTextParams.topMargin = 800;
        mStatusText = new TextView(this);
        mStatusText.setText("Idle");
        mStatusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float)25);
        rl.addView(mStatusText, statusTextParams);

        // Set Content View
        setContentView(rl);
	}

	@Override
    public void onPause() {
		super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voice_journal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    private void _startPlaying()
    {
    	try
    	{
    		mPlayer = new MediaPlayer();
    		mPlayer.setDataSource(mFileName);
    		mPlayer.prepare();
    		mPlayer.start();
    	}
    	catch (IOException e)
    	{
    		Log.e(LOG_TAG, "Failed to start playing", e);
    	}
    }

    private void _stopPlaying()
    {
    	mPlayer.release();
    	mPlayer = null;
    }

    private void _startRecording()
    {
    	try
    	{
    		mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    		mRecorder.prepare();
    		mRecorder.start();
    	}
    	catch (IOException e)
    	{
    		Log.e(LOG_TAG, "Failed to start recording", e);
    	}
    }

    private void _stopRecording()
    {
    	mRecorder.stop();
    	mRecorder.release();
    	mRecorder = null;
    }

	class RecordButton extends Button
	{
		RECORDSTATE currState = RECORDSTATE.STOPPED;
		OnClickListener recordClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (currState == RECORDSTATE.STOPPED)
				{
					_startRecording();
					mStatusText.setText("Recording started");
					currState = RECORDSTATE.START;
				}
				else
				{
					_stopRecording();
					mStatusText.setText("Recording stopped");
					currState = RECORDSTATE.STOPPED;
				}
			}
		};
		public RecordButton(Context context) {
			super(context);
			setOnClickListener(recordClickListener);
		}
	}

	class PlayButton extends Button
	{

		PLAYSTATE currState = PLAYSTATE.STOPPED;
		OnClickListener playClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (currState == PLAYSTATE.STOPPED)
				{
					_startPlaying();
					mStatusText.setText("Play started");
					currState = PLAYSTATE.START;
				}
				else
				{
					_stopPlaying();
					mStatusText.setText("Play stopped");
					currState = PLAYSTATE.STOPPED;
				}
			}
		};
		public PlayButton(Context context) {
			super(context);
			setOnClickListener(playClickListener);
		}
	}
}