package com.wul.hlt.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


import com.wul.hlt.R;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/8/25.
 */

public class SoundPoolUtil {
    SoundPool mSoundPool;
    HashMap<Integer, Integer> soundPoolMap = new HashMap();
    Context mContext;

    public SoundPoolUtil(Context context) {
        mContext = context;
        init();
    }

    public void init() {
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap();
        soundPoolMap.put(1, mSoundPool.load(mContext, R.raw.order_music, 1));
    }

    public void playSucess() {
        AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        mSoundPool.play(soundPoolMap.get(1), streamVolumeCurrent, streamVolumeCurrent, 1, 0, 1f);
    }

    public void playFail() {
        AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        mSoundPool.play(soundPoolMap.get(2), streamVolumeCurrent, streamVolumeCurrent, 1, 0, 1f);
    }

}
