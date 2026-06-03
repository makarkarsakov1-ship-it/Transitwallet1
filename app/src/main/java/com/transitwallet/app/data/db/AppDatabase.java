package com.transitwallet.app.data.db;

import android.content.Context;
import androidx.room.*;
import com.transitwallet.app.data.model.TransitCard;

@Database(entities = {TransitCard.class}, version = 1,
          exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TransitCardDao transitCardDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "transit_wallet.db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
