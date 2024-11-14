package com.ldx.weather.ui;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.ldx.weather.Constant;
import com.ldx.weather.R;
import com.ldx.weather.WeatherApp;
import com.ldx.weather.db.bean.Music;
import com.ldx.weather.service.MusicService;
import com.ldx.weather.ui.view.CircleImageView;
import androidx.appcompat.app.AppCompatActivity;


public class MusicActivity extends AppCompatActivity {

    private ImageView btn_play, btn_prev, btn_next, btn_status, btn_quick, btn_back;
    private CircleImageView covertest;
    private SeekBar seekBar;
    private Handler handler;
    private Runnable runnable;
    private ObjectAnimator rotationAnimator;
    private TextView start, end, title;
    private WeatherApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        //沉浸式
        setFullScreenImmersion();

        // 获取控件
        btn_play = findViewById(R.id.btn_play);
        btn_prev = findViewById(R.id.btn_prev);
        btn_next = findViewById(R.id.btn_next);
        btn_status = findViewById(R.id.btn_status);
        btn_quick = findViewById(R.id.progress_right);
        btn_back = findViewById(R.id.back_left);
        covertest = findViewById(R.id.cover);
        seekBar = findViewById(R.id.seekBar);
        start = findViewById(R.id.start);
        end = findViewById(R.id.end);
        title = findViewById(R.id.place);

        // 设置动画
        rotationAnimator = ObjectAnimator.ofFloat(covertest, Constant.ROTATION, 0f, 360f);
        rotationAnimator.setDuration(5000);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        handler = new Handler();
        updateSeekBar();

        // 返回
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
            }
        });

        // 设置音乐
        app.getService().setOnStatusChangeListener(new MusicService.OnStatusChangeListener() {
            @Override
            public void onChange(Music music) {
                setMusic(music);
            }
        });

        // 播放按钮点击事件
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.getService().isPlaying()) {
                    rotationAnimator.pause();
                    btn_play.setImageResource(R.drawable.ic_play_btn_play);
                } else {
                    if (rotationAnimator.isStarted()) {
                        rotationAnimator.resume();
                    } else {
                        rotationAnimator.start();
                    }
                    btn_play.setImageResource(R.drawable.ic_play_btn_pause);
                }
                app.getService().playOrPause();
            }
        });

        // 上一首按钮点击事件
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getService().pre();
            }
        });

        // 下一首按钮点击事件
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getService().next();
            }
        });

        // 快进按钮点击事件
        btn_quick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getService().quick(1000); // 快进1000毫秒
            }
        });

        // 后退按钮点击事件
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getService().back(1000); // 后退1000毫秒
            }
        });

        // 改变状态按钮点击事件
        btn_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = (app.getService().getStatus() + 1) % 3;
                app.getService().setStatus(status);
                if (status == 0) {
                    btn_status.setImageResource(R.drawable.ic_play_btn_loop);
                    Toast.makeText(MusicActivity.this, R.string.list_loops, Toast.LENGTH_SHORT).show();
                } else if (status == 1) {
                    btn_status.setImageResource(R.drawable.ic_play_btn_shuffle);
                    Toast.makeText(MusicActivity.this, R.string.shuffle, Toast.LENGTH_SHORT).show();
                } else if (status == 2) {
                    btn_status.setImageResource(R.drawable.ic_play_btn_one);
                    Toast.makeText(MusicActivity.this, R.string.single_loop, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_play.setImageResource(R.drawable.ic_play_btn_pause);
        Music music =app.getService().getMusic(app.getService().getPosition());
        if (app.getService().isPlaying()) {
            if (music != null && music != app.getService().getNowPlay()) {
                app.getService().setData(app.getService().getPosition());
                app.getService().playOrPause();
                rotationAnimator.start();
            }
        } else {
            app.getService().setData(app.getService().getPosition());
            app.getService().playOrPause();
            rotationAnimator.start();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void setMusic(Music music) {
        if (music == null) {
            Log.e("MusicActivity", "Received null music");
            return;
        }

        title.setText(music.getTitle());
        covertest.setImageResource(R.drawable.music);
        end.setText(music.getTime());
        seekBar.setMax(music.getDuration() / 1000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    app.getService().seekTo(progress * 1000);
                    start.setText(String.format(getString(R.string.s_s), format(progress / 60), format(progress % 60)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    private void updateSeekBar() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (app.getService() != null && app.getService().isPlaying()) {
                    rotationAnimator.resume();
                    btn_play.setImageResource(R.drawable.ic_play_btn_pause);
                    int currentPosition = app.getService().getNow();
                    seekBar.setProgress(currentPosition / 1000);
                    start.setText(String.format(getString(R.string.s_s), format(currentPosition / 1000 / 60), format(currentPosition / 1000 % 60)));
                } else {
                    rotationAnimator.pause();
                    btn_play.setImageResource(R.drawable.ic_play_btn_play);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    private String format(int day) {
        if (day < 10) {
            return getString(R.string._0) + day;
        }
        return String.valueOf(day);
    }
    protected void setFullScreenImmersion() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int option = window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        window.getDecorView().setSystemUiVisibility(option);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }
}
