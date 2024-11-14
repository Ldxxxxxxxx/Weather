package com.ldx.weather.db.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.room.TypeConverter;

/**
 * 数据转换器
 * CityConverter 用来将 List<Province.City> 类型转换为 String 类型以存储在数据库中，
 * 以及将存储的 String 类型转换回 List<Province.City> 类型的转换器。
 */
public class CityConverter {

    @TypeConverter
    public List<Province.City> stringToObject(String value) {
        // 使用 Gson 库将将从数据库中检索到的 String 类型数据转换为 List<Province.City> 类型。
        Type userListType = new TypeToken<ArrayList<Province.City>>() {}.getType();
        return new Gson().fromJson(value, userListType);
    }

    @TypeConverter
    public String objectToString(List<Province.City> list) {
        //将 List<Province.City> 类型转换为 String 类型
        return new Gson().toJson(list);
    }
}
