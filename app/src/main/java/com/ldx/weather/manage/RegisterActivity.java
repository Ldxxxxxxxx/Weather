package com.ldx.weather.manage;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.ldx.weather.R;
import com.ldx.weather.db.UserSQLiteOpenHelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

//日程中心用户注册
public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText1, passwordEditText2;
    private UserSQLiteOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setFullScreenImmersion();

        usernameEditText = findViewById(R.id.registerUsernameEditText);
        passwordEditText1 = findViewById(R.id.registerPasswordEditText1);
        passwordEditText2 = findViewById(R.id.registerPasswordEditText2);
        Button registerButton = findViewById(R.id.registerButton);

        dbHelper = new UserSQLiteOpenHelper(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        // 返回按钮
        ImageView backButton = findViewById(R.id.exit2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void register() {
        String username = usernameEditText.getText().toString();
        String password1 = passwordEditText1.getText().toString();
        String password2 = passwordEditText2.getText().toString();

        // 检查密码是否一致
        if (!password1.equals(password2)) {
            showAlertDialog("密码不一致，请重新输入");
            return;
        }

        // 检查用户名是否已存在
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT UserId FROM users WHERE UserName = ?", new String[]{username});

        if (cursor.moveToFirst()) {
            // 用户名已存在
            cursor.close();
            db.close();
            showAlertDialog("该用户名已被注册，请重新输入");
        } else {
            // 用户名不存在，可以注册
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("UserName", username);
            values.put("PassWord", password1);

            // 插入新用户信息
            long newRowId = db.insert("users", null, values);

            // 注册成功，返回登录页面
            cursor.close();
            db.close();
            Intent intent = new Intent(RegisterActivity.this, ManagerActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
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
}
