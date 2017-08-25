package com.echodev.echoalpha.util;

import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import com.echodev.echoalpha.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioHelper {

    private static MediaRecorder mRecorder;
    private static boolean isRecording = false;

    public static boolean startRecording(String audioPath) {
        boolean startSuccess = false;

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
//        mRecorder.setAudioChannels(2);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(64000);
        mRecorder.setOutputFile(audioPath);

        try {
            mRecorder.prepare();
            startSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();

        isRecording = true;

        return startSuccess;
    }

    public static boolean stopRecording() {
        boolean stopSuccess = false;

        if (isRecording && mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;

            isRecording = false;
            stopSuccess = true;
        }

        return stopSuccess;
    }

    public static void playAudioLocal(String audioPath) {
        MediaPlayer mPlayer = new MediaPlayer();
        File appFile = new File(audioPath);
        if (appFile.exists()) {
            try {
                mPlayer.setDataSource(audioPath);
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.start();
//            mPlayer.release();
        }
    }

    public static void playAudioOnline(String audioUrl) {
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(audioUrl);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();
    }

    public static File createAudioFile(String userUuid) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioName = userUuid+ "_" + timeStamp + ".m4a";
        String audioPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        audioPath += File.separator + "Echowall" + File.separator + "audios" + File.separator + audioName;

        return new File(audioPath);
    }
}
