package com.udacity.android.popularmovies.database;

import android.arch.persistence.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.udacity.android.popularmovies.model.ReviewResult;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ReviewTypeConverter {

    static Gson gson = new Gson();

    @TypeConverter
    public static List<ReviewResult> stringToReviewList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<ReviewResult>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ReviewListToString(List<ReviewResult> resultList) {
        return gson.toJson(resultList);
    }
}

