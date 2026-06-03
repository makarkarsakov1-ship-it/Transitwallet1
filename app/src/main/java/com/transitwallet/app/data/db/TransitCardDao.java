package com.transitwallet.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.transitwallet.app.data.model.TransitCard;
import java.util.List;

@Dao
public interface TransitCardDao {

    @Query("SELECT * FROM transit_cards ORDER BY addedAt DESC")
    LiveData<List<TransitCard>> getAllCards();

    @Query("SELECT * FROM transit_cards WHERE uid = :uid LIMIT 1")
    TransitCard getCardByUid(String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(TransitCard card);

    @Delete
    void delete(TransitCard card);
}
