package com.ldx.weather.manage;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ldx.weather.R;
import com.ldx.weather.db.MySQLiteOpenHelper;
import com.ldx.weather.ui.MainActivity;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
//日程中心主活动
public class DailyManager extends AppCompatActivity implements View.OnClickListener {
    private CalendarView calendarView;
    private EditText scheduleInput;
    private Context context;
    private Button addSchedule, checkAdd;
    private String dateToday;//用于记录今天的日期
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase myDatabase;
    private TextView mySchedule[] = new TextView[5];
    private static String username;
    private boolean isChecked;
    private Menu mMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_manager);
        setFullScreenImmersion();
        // 获取传递的Intent对象
        Intent intent = getIntent();
        username = intent.getStringExtra("userName"); // 修复了用户名未正确初始化的问题
        isChecked = intent.getBooleanExtra("isChecked", false); // 获取布尔类型的值，如果没有传递或者出错，使用默认值 false
        initView();

        //这里不这样的话一进去就设置当天的日程会报错
        Calendar time = Calendar.getInstance();
        int year = time.get(Calendar.YEAR);
        int month = time.get(Calendar.MONTH) + 1;//注意要+1，0表示1月份
        int day = time.get(Calendar.DAY_OF_MONTH);
        dateToday = year + "-" + month + "-" + day;
        //还要直接查询当天的日程，这个要放在initView的后面，不然会出问题
        queryByDateAndUserId(dateToday, username);
       // Log.d("SB",dateToday);
      //  Log.d("SB",username);


        //点击菜单栏打开菜单
        ImageView iconView=findViewById(R.id.menu);

       // 为图标添加点击事件监听器
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建PopupMenu对象，并关联到图标View
                PopupMenu popupMenu = new PopupMenu(DailyManager.this, iconView);

                // 加载菜单布局
                popupMenu.getMenuInflater().inflate(R.menu.menu_manager, popupMenu.getMenu());

                // 设置菜单项点击事件监听器
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        // 根据菜单项的ID执行相应的操作
                        if (id == R.id.item_login_register) {
                            // 执行登录注册操作
                            Intent intent1=new Intent(DailyManager.this,ManagerActivity.class);
                            startActivity(intent1);
                            return true;
                        }
                        return false;
                    }
                });

                // 显示PopupMenu
                popupMenu.show();
            }
        });



    }

    private void initView() {
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        myDatabase = mySQLiteOpenHelper.getWritableDatabase();

        context = this;
        addSchedule = findViewById(R.id.addSchedule);
        addSchedule.setOnClickListener(this);
        checkAdd = findViewById(R.id.checkAdd);
        checkAdd.setOnClickListener(this);
        calendarView = findViewById(R.id.calendar);
        ImageView exit = findViewById(R.id.exit3);
        calendarView.setOnDateChangeListener(mySelectDate);
        mySchedule[0] = findViewById(R.id.schedule1);
        mySchedule[1] = findViewById(R.id.schedule2);
        mySchedule[2] = findViewById(R.id.schedule3);
        mySchedule[3] = findViewById(R.id.schedule4);
        mySchedule[4] = findViewById(R.id.schedule5);
        for (TextView v : mySchedule) {
            v.setOnClickListener(this);
        }
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (isChecked) {
                    // 如果 isChecked 是 true，跳转到 MainActivity
                    intent = new Intent(DailyManager.this, MainActivity.class);
                    intent.putExtra("isChecked", isChecked);
                } else {
                    // 如果 isChecked 是 false，跳转到 ManagerActivity
                    intent = new Intent(DailyManager.this, ManagerActivity.class);
                }
                startActivity(intent);
            }
        });


    }

    private CalendarView.OnDateChangeListener mySelectDate = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            dateToday = year + "-" + (month + 1) + "-" + dayOfMonth;
            Toast.makeText(context, "你选择了:" + dateToday, Toast.LENGTH_SHORT).show();

            //得把用别的日期查出来的日程删除并将其隐藏
            for (TextView v : mySchedule) {
                v.setText("");
                v.setVisibility(View.GONE);
            }
            queryByDateAndUserId(dateToday, username);
        }
    };


    private void queryByDateAndUserId(String date, String username) {
        String selection = "time=? AND UserName=?";
        String[] selectionArgs = {date, username};

        Cursor cursor = myDatabase.query("schedules", null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int scheduleCount = 0;
            do {
                String title = cursor.getString(cursor.getColumnIndex("title")); // 修改字段名为 "title"
                mySchedule[scheduleCount].setText("日程" + (scheduleCount + 1) + "：" + title); // 显示标题
                mySchedule[scheduleCount].setVisibility(View.VISIBLE);
                scheduleCount++;
                if (scheduleCount >= 5)
                    break;
            } while (cursor.moveToNext());
        }
        cursor.close();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.addSchedule) {
            checkAddSchedule();
            //queryByDateAndUserId(dateToday, username);
        } else if (id == R.id.checkAdd) {
           // checkAddSchedule();
        } else if (id == R.id.schedule1 || id == R.id.schedule2 || id == R.id.schedule3 || id == R.id.schedule4 || id == R.id.schedule5) {
            editSchedule(v);
            queryByDateAndUserId(dateToday, username);
        }

    }

private void editSchedule(View v) {
    String title = ((TextView) v).getText().toString().split("：")[1];
    // 根据标题从数据库中获取日程信息
    String place = getPlaceFromDatabase(title);
    int notification = getNotificationFromDatabase(title);
    String priority = getPriorityFromDatabase(title);
    String description = getDescriptionFromDatabase(title);

    // 将日程信息传递到 EditScheduleActivity 中
    Intent intent = new Intent(DailyManager.this, EditScheduleActivity.class);
    intent.putExtra("title", title);
    intent.putExtra("place", place);
    intent.putExtra("notification", notification);
    intent.putExtra("priority", priority);
    intent.putExtra("description", description);
    intent.putExtra("time", dateToday);
    intent.putExtra("userName", username);
    startActivity(intent);
}


    private void checkAddSchedule() {
        //String inputText = scheduleInput.getText().toString().trim();
        // 启动 AddSchedule 活动，并传递日程标题
        Intent intent = new Intent(DailyManager.this, AddSchedule.class);
        intent.putExtra("time",  dateToday);
        intent.putExtra("UserName",username);
        startActivityForResult(intent, 1);
    }

  //屏幕渲染
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
    // 从数据库中获取日程信息的方法
    private String getPlaceFromDatabase(String title) {
        //Cursor 是 Android 开发中用于在数据库查询结果集上进行遍历的接口。
        // 它类似于数据库中的游标，可以逐行地检索查询结果，允许程序员访问和处理数据库中的数据。
        //根据给定的日程标题，在数据库中查询对应的地点信息，并将查询结果返回。
        String place = null;
        SQLiteDatabase db = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("schedules", new String[]{"place"}, "title=?", new String[]{title}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            place = cursor.getString(cursor.getColumnIndex("place"));
            cursor.close();
        }
        return place;
    }

    private int getNotificationFromDatabase(String title) {
        int notification = 0;
        SQLiteDatabase db = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("schedules", new String[]{"notition"}, "title=?", new String[]{title}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            notification = cursor.getInt(cursor.getColumnIndex("notition"));
            cursor.close();
        }
        return notification;
    }

    private String getPriorityFromDatabase(String title) {
        String priority = null;
        SQLiteDatabase db = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("schedules", new String[]{"priority"}, "title=?", new String[]{title}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            priority = cursor.getString(cursor.getColumnIndex("priority"));
            cursor.close();
        }
        return priority;
    }

    private String getDescriptionFromDatabase(String title) {
        String description = null;
        SQLiteDatabase db = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("schedules", new String[]{"description"}, "title=?", new String[]{title}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            description = cursor.getString(cursor.getColumnIndex("description"));
            cursor.close();
        }
        return description;
    }

}

