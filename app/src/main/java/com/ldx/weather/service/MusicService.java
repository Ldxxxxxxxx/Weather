package com.ldx.weather.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.ldx.weather.db.bean.Music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener{
    private final IBinder binder = new MusicBinder();
    private MediaPlayer mediaPlayer;
    private List<Music> musicList=new ArrayList<>();
    private int position;
    private int status=0;
    private Music music;
    private Random random = new Random();
    private int lowerBound = 0; // 最小值
    private int upperBound = 0; // 最大值
    private OnStatusChangeListener onStatusChangeListener;

   public MusicService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer==null){
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    //判断是否正在播放
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    //设置音乐
    public void setMusic(Music music){
        if (music!=null){
            this.music=music;
            if (onStatusChangeListener!=null){
                onStatusChangeListener.onChange(music);
            }
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(music.getData());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public Music getMusic(int position){
        if (position>=0&&position<musicList.size()){
            return musicList.get(position);
        }
        return null;
    }

    public int getPosition() {

       return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setData(int position){
        this.position = position;
        if (position>=0&&position<musicList.size()){
            setMusic(musicList.get(position));
        }
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
        upperBound=musicList.size()-1;
    }

    public void pre(){
        if (status==1){
            randomPlay();
        }
        else {
            position=(position-1) % musicList.size();
            if (position<0){
                position=musicList.size()-1;
            }
            Music music = musicList.get(position);
            if (music!=null){
                setMusic(music);
                playOrPause();
            }
        }
    }
    //快进
    public void quick(int milliseconds){
        int currentPosition = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        int newPosition = currentPosition + milliseconds;
        if (newPosition > duration) {
            newPosition = duration;
        }
        mediaPlayer.seekTo(newPosition);
    }
    //后退
    public void back(int milliseconds) {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int newPosition = currentPosition - milliseconds;
        if (newPosition < 0) {
            newPosition = 0;
        }
        mediaPlayer.seekTo(newPosition);
    }

    //顺序播放下一首
    public void next(){
        if (status==1){
            randomPlay();
        }
        else {
            position=(position+1) % (musicList.size());
            Music music = musicList.get(position);
            if (music!=null){
                setMusic(music);
                playOrPause();
            }
        }

    }
    //随机播放
    public void randomPlay(){
        position=random.nextInt(upperBound - lowerBound + 1) + lowerBound;
        setData(position);
        playOrPause();
    }
    //单曲循环
    public void loop(){
        setData(position);
        playOrPause();
    }
    //设置当前播放器状态
    public void setStatus(int status){
        this.status=status;
    }
   //获取当前播放器状态
    public int getStatus() {
        return status;
    }

    public Music getNowPlay(){
        return music;
    }

    //播放或者暂停
    public void playOrPause(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        else {
            mediaPlayer.start();
        }
    }
    //进度改变
    public void seekTo(int i){
        mediaPlayer.seekTo(i);
    }

    //获取当前的播放进度
    public int getNow(){
        if (mediaPlayer.isPlaying()){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (status==0){
            next();//状态嘛为0，顺序播放下一首
        }
        else if (status==1){
            randomPlay();//状态码为1，随机播放
        }
        else if (status==2){
            loop();//状态码为2，循环播放
        }
    }

    public void setOnStatusChangeListener(OnStatusChangeListener onStatusChangeListener) {
        this.onStatusChangeListener = onStatusChangeListener;
    }

    public class MusicBinder extends Binder{
        //返回Service对象
        public MusicService getService(){
            return MusicService.this;
        }
    }

    public interface OnStatusChangeListener{
        void onChange(Music music);
    }

/*    public void seekTo(int progress) {
        mediaPlayer.seekTo(progress);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }*/
}