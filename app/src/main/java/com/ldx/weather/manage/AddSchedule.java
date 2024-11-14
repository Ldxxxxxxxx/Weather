package com.ldx.weather.manage;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.ldx.weather.R;
import com.ldx.weather.db.MySQLiteOpenHelper;

import androidx.appcompat.app.AppCompatActivity;

public class AddSchedule extends AppCompatActivity {

    private EditText editTextTitle, editTextLocation, editTextDescrep;
    private Switch switch1;
    private RadioGroup radioGroup;
    private TextView editTextNotition, editTextPrority;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase myDatabase;
    private static int switchvalue;
    private static String prority;
    private static String dateToday;
    private static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        setFullScreenImmersion();
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        myDatabase = mySQLiteOpenHelper.getWritableDatabase();

        // 获取传递的意图
        Intent intent = getIntent();
        // 检查意图是否为空
        if (intent != null) {
            // 从意图中获取日期和用户名信息
            dateToday = intent.getStringExtra("time");
            username = intent.getStringExtra("UserName");

        }

        // 初始化控件
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextDescrep = findViewById(R.id.editTextDescrep);
        switch1 = findViewById(R.id.switch1);
        radioGroup = findViewById(R.id.radioGroup);
        editTextNotition = findViewById(R.id.editTextNotition);
        editTextPrority = findViewById(R.id.editTextPrority);
        ImageView save=findViewById(R.id.save);
        ImageView exit=findViewById(R.id.exit6);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave(v);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(AddSchedule.this,DailyManager.class);
                startActivity(intent1);
            }
        });

        // 设置 switch1 的监听器
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch1.isChecked()) {
                    editTextNotition.setText("需要通知");
                    switchvalue=1;
                } else {
                    editTextNotition.setText("不通知");
                    switchvalue=0;
                }
            }
        });

        // 设置 radioGroup 的监听器
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                prority = radioButton.getText().toString();
                editTextPrority.setText( prority);
            }
        });
    }


    // 保存按钮点击事件
    public void onClickSave(View view) {
        // 获取输入框的值
        String title = editTextTitle.getText().toString();
        String location = editTextLocation.getText().toString();
        String descrip = editTextDescrep.getText().toString();
        ContentValues values = new ContentValues();

        // 检查 prority 是否为空
        if (prority != null) {
            values.put("priority", prority); // 使用 prority 变量获取用户选择的优先级
        } else {
            // 如果 prority 为空，则设置默认值或者其他处理逻辑
            values.put("priority", "default"); // 设置默认值为 "default"
        }

        values.put("title", title);
        values.put("place", location);
        values.put("notition", switchvalue);
        values.put("priority",  prority);
        values.put("description",
                descrip);
        values.put("time", dateToday);
        values.put("UserName", username);
        myDatabase.insert("schedules", null, values);
        Intent intent = new Intent(AddSchedule.this, DailyManager.class);
        startActivity(intent);

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
