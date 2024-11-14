package com.ldx.weather.manage;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import com.ldx.weather.R;
import com.ldx.weather.db.MySQLiteOpenHelper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditScheduleActivity extends AppCompatActivity  {

    private String schedule;
    private Button editBtn, deleteBtn;
    private SQLiteDatabase myDatabase;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private EditText editTextTitle, editTextLocation, editTextDescrep;
    private Switch switch1;
    private RadioGroup radioGroup;
    private TextView editTextNotition, editTextPrority;
    private static int switchvalue;
    private static String prority;
    private static String dateToday;
    private static String username;
    private static String titlevalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);
        setFullScreenImmersion();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextDescrep = findViewById(R.id.editTextDescrep);
        switch1 = findViewById(R.id.switch1);
        radioGroup = findViewById(R.id.radioGroup);
        editTextNotition = findViewById(R.id.editTextNotition);
        editTextPrority = findViewById(R.id.editTextPrority);
        Intent intent = getIntent();
        schedule = intent.getStringExtra("schedule");
        initView();
        
        // 获取传递过来的日程信息
        Intent intent2 = getIntent();
        titlevalue = intent.getStringExtra("title");
        String place = intent.getStringExtra("place");
        int notification = intent.getIntExtra("notification", 0);
        String priority = intent.getStringExtra("priority");
        String description = intent.getStringExtra("description");
        dateToday= intent.getStringExtra("time");
        username = intent.getStringExtra("userName");

        // 将日程信息设置到相应的控件中
        editTextTitle.setText(titlevalue);
        editTextLocation.setText(place);
        editTextDescrep.setText(description);
        switch1.setChecked(notification == 1);
        // 根据优先级设置 RadioButton
       if (priority.equals("重要")) {
            radioGroup.check(R.id.radioButton1);
           editTextPrority.setText("重要");
       } else if (priority.equals("普通")) {
            radioGroup.check(R.id.radioButton2);
           editTextPrority.setText("普通");
        } else if (priority.equals("忽略")) {
            radioGroup.check(R.id.radioButton3);
           editTextPrority.setText("忽略");
        }
        if (notification == 1) {
            switch1.setChecked(true); // 设置 Switch 为选中状态
            editTextNotition.setText("需要提醒");
        } else {
            switch1.setChecked(false); // 设置 Switch 为未选中状态
            editTextNotition.setText("不提醒");
        }

    }


    private void initView() {
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        myDatabase = mySQLiteOpenHelper.getWritableDatabase();
        Button button1=findViewById(R.id.button);
        Button buttin2=findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSchedule();
                Intent intent1=new Intent(EditScheduleActivity.this,DailyManager.class);
                startActivity(intent1);
            }
        });
        buttin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMySchedule();
                Intent intent1=new Intent(EditScheduleActivity.this,DailyManager.class);
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

    private void editSchedule() {
        String title1 = editTextTitle.getText().toString();
        String location = editTextLocation.getText().toString();
        String descrip = editTextDescrep.getText().toString();
        // 在这里保存数据到数据库，这里只是示例，你可以根据需求修改
        ContentValues values = new ContentValues();
        // 检查 prority 是否为空
        if (prority != null) {
            values.put("priority", prority); // 使用 prority 变量获取用户选择的优先级
        } else {
            // 如果 prority 为空，则设置默认值或者其他处理逻辑
            values.put("priority", "default"); // 设置默认值为 "default"
        }

        values.put("title", title1);
        values.put("place", location);
        values.put("notition", switchvalue);
        values.put("priority",  prority);
        values.put("description", descrip);
        values.put("time", dateToday);
        values.put("UserName", username);

        Log.d("HB",dateToday);
        Log.d("HB",username);
      //  myDatabase.update("schedules", values, "title=?", new String[]{schedule});
    //    int rowsAffected = myDatabase.update("schedules", values, "title=?", new String[]{schedule});
        int rowsAffected = myDatabase.update("schedules", values, "title=?", new String[]{titlevalue});
        if (rowsAffected > 0) {
            // 更新成功
            showAlertDialog("日程修改成功!");
        } else {
            // 更新失败
            showAlertDialog("日程修改失败!");
        }

    }


    private void deleteMySchedule() {
        int rowsAffected =myDatabase.delete("schedules", "title=?", new String[]{titlevalue});
        //myDatabase.delete("schedules", "title=?",new String[]{titlevalue});
        if (rowsAffected > 0) {
            // 更新成功
            showAlertDialog("日程删除成功!");
        } else {
            // 更新失败
            showAlertDialog("日程删除失败!");
        }
        // 清空输入字段
        editTextTitle.setText("");
        editTextLocation.setText("");
        editTextDescrep.setText("");
        editTextNotition.setText("");
        editTextPrority.setText("");
        switch1.setChecked(false);
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

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
}
