package com.udacity.android.popularmovies.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;
import com.udacity.android.popularmovies.model.*;

@Database(entities = {MovieRecord.class, FavMovies.class,
        MovieDetail.class, MovieSnapshot.class, MovieReviews.class, ReviewResult.class}, version = 1, exportSchema = false)
@TypeConverters({MovieTypeConverters.class, ReviewTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "moviedb";
    private static AppDatabase sInstance;

    public static AppDatabase getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new movie database");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
            }

        }
        Log.d(LOG_TAG, "Fetching the database instance");
        return sInstance;
    }

    public abstract MovieDao movieDao();

    public abstract FavDao favDao();

    public abstract MovieDetailDao movieDetailDao();

    public abstract MovieSnapshotDao movieSnapshotDao();

    public abstract ReviewsDao movieReviewsDao();

    public abstract MovieReviewDao reviewDao();
}
