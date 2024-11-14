package com.ldx.weather.manage;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.ldx.weather.R;
import com.ldx.weather.db.UserSQLiteOpenHelper;
import com.ldx.weather.ui.MainActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//实现日程中心用户登录功能
public class ManagerActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private UserSQLiteOpenHelper dbHelper;
    private CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        setFullScreenImmersion();
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginIn);
        Button signUpButton = findViewById(R.id.signUp);
        ImageView exit1 = findViewById(R.id.exit1);
        checkBox = findViewById(R.id.checkBox);

        dbHelper = new UserSQLiteOpenHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        exit1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT UserId, UserName FROM users WHERE UserName = ? AND PassWord = ?", new String[]{username, password});
        boolean isChecked = checkBox.isChecked(); // 获取CheckBox的状态
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndex("UserId"));
            cursor.close();
            db.close();

            // 登录成功，跳转到 DailyManager 并传递用户ID和用户名
            Intent intent = new Intent(ManagerActivity.this, DailyManager.class);
            intent.putExtra("isChecked", isChecked);
            intent.putExtra("userId", userId);
            intent.putExtra("userName", username);
            startActivity(intent);
        } else {
            // 用户名或密码错误，显示提示框
            cursor.close();
            db.close();
            showAlertDialog("用户名或密码错误");
        }
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
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
