package com.ldx.weather.db.bean;
import java.io.Serializable;

//歌曲实体类

/**
 *  Music 类被标记为 Serializable，
 * 意味着该类的对象可以被转换为字节流，然后可以存储在文件中、通过网络发送，或者以其他方式序列化，
 * 然后在需要时反序列化以重建原始对象。
 */
public class Music implements Serializable {
    private String title,artist,data, time;
    private int duration;//音频长度
    public String getTitle() {
        return title;
    }
 
    public void setTitle(String title) {
        this.title = title;
    }
 
    public String getArtist() {
        return artist;
    }
 
    public void setArtist(String artist) {
        this.artist = artist;
    }
 
    public String getData() {
        return data;
    }
 
    public void setData(String data) {
        this.data = data;
    }
 
    public String getTime() {
        return time;
    }
 
    public void setTime(String time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Music(String title, String artist, String data, String time, int duration) {
        this.title = title;
        this.artist = artist;
        this.data = data;
        this.time = time;
        this.duration = duration;
    }
}