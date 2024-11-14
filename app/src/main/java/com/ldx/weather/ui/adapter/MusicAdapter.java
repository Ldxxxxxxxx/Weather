package com.ldx.weather.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ldx.weather.R;
import com.ldx.weather.db.bean.Music;
import java.util.List;

public class MusicAdapter extends BaseAdapter {
    Context context;
    List<Music> musicList;
    int playingPosition=-1;
    public MusicAdapter(Context context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    public void setPlayingPosition(int playingPosition) {
        this.playingPosition = playingPosition;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }
 
    @Override
    public Music getItem(int position) {
        return musicList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_music,null);
            viewHolder.number=convertView.findViewById(R.id.number);
           // viewHolder.artist=convertView.findViewById(R.id.artist);
            viewHolder.title=convertView.findViewById(R.id.place);
            viewHolder.duration=convertView.findViewById(R.id.duration);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Music music=musicList.get(position);
        viewHolder.title.setText(music.getTitle());
        viewHolder.duration.setText(music.getTime());
       // viewHolder.artist.setText(music.getArtist());
        viewHolder.number.setText(String.valueOf(position+1));
        if (playingPosition!=-1){
            if (position==playingPosition){
                viewHolder.title.setTextColor(context.getColor(R.color.colorAccent));
            //    viewHolder.artist.setTextColor(context.getColor(R.color.colorAccent));
                viewHolder.number.setTextColor(context.getColor(R.color.colorAccent));

            }
            else {
                viewHolder.title.setTextColor(context.getColor(R.color.black));
             //   viewHolder.artist.setTextColor(context.getColor(R.color.black));
                viewHolder.number.setTextColor(context.getColor(R.color.black));
            }
        }

        return convertView;
    }
 
    public static class ViewHolder {
        TextView number,duration,artist,title;
    }
}