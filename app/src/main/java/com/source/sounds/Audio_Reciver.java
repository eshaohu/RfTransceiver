package com.source.sounds;


import android.util.Log;

import com.rftransceiver.datasets.MyDataQueue;
import com.rftransceiver.datasets.AudioData;
import com.rftransceiver.util.PoolThreadUtil;

import butterknife.InjectView;

public class Audio_Reciver implements Runnable
{
    private volatile boolean isReceiving = false;
    private MyDataQueue dataQueue = null;

    private static Audio_Reciver instance;

    private PlaySoundsListener listener;

    private boolean autoStop = false;

    private Audio_Reciver() {
        /**
         * 初始化缓冲区
         */
        dataQueue = MyDataQueue.getInstance(MyDataQueue.DataType.Sound_Receiver);
    }

    public static Audio_Reciver getInstance() {
        if(instance == null) {
            instance = new Audio_Reciver();
        }
        return instance;
    }

    public void setListener(PlaySoundsListener listener) {
        this.listener = listener;
    }

	 public void startReceiver()
	 {
         if(!isReceiving()) {
             PoolThreadUtil.getInstance().addTask(this);
         }
	 }

    @Override
    public void run() {
        //先启动解码器
        Audio_Decoder decoder = Audio_Decoder.getInstance();
        decoder.startDecoding();
        if(listener != null) listener.playingStart();
        setReceiving(true);
        while (!decoder.getIsDecoding()) {
            try {
                Thread.sleep(50);
            }catch (InterruptedException e) {

            }
        }
        while(isReceiving()) {

            AudioData data = (AudioData)dataQueue.get();
            if(data == null) {
                //此处可以增加一个延时
            }else {
                //将数据添加至解码器
                decoder.addData(data);
            }
            if(autoStop) {
                if(dataQueue.getSize() == 0) {
                    autoStop = false;
                    stopReceiver();
                }
            }
        }
        //停止解码器
        decoder.stopDecoding();
        if(listener != null) {
            listener.playingStop();
        }
    }

    public void stopReceiver() {
        //stop receive
        setReceiving(false);
    }

    public boolean isReceiving() {
        synchronized (this) {
            return isReceiving;
        }
    }

    public void setReceiving(boolean receiving) {
        synchronized (this) {
            this.isReceiving = receiving;
        }
    }

    /**
     * add sounds data to dataQueue to play
     * @param data
     * @param size
     */
    public void cacheData(byte[] data,int size) {
        AudioData receviceData = new AudioData();
        receviceData.setSize(size);
        receviceData.setencodeData(data);
        dataQueue.add(receviceData);
     }

    public void clear() {
        dataQueue.clear();
    }

    /**
     * play sounds listener
     * can konwn the start and end of playing
     */
    public interface PlaySoundsListener{
        void playingStart();
        void playingStop();
    }
}
