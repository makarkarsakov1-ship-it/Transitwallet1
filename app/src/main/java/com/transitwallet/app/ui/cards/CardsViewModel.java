package com.transitwallet.app.ui.cards;

import android.app.Application;
import android.nfc.Tag;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.transitwallet.app.data.db.AppDatabase;
import com.transitwallet.app.data.db.TransitCardDao;
import com.transitwallet.app.data.model.TransitCard;
import com.transitwallet.app.nfc.NfcReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardsViewModel extends AndroidViewModel {

    private final TransitCardDao dao;
    private final ExecutorService executor =
        Executors.newSingleThreadExecutor();

    public final LiveData<List<TransitCard>> cards;

    private final MutableLiveData<String> _scanStatus =
        new MutableLiveData<>();
    public final LiveData<String> scanStatus = _scanStatus;

    private final MutableLiveData<Integer> _scanState =
        new MutableLiveData<>(0);
    public final LiveData<Integer> scanState = _scanState;

    public CardsViewModel(@NonNull Application application) {
        super(application);
        dao = AppDatabase.getInstance(application).transitCardDao();
        cards = dao.getAllCards();
    }

    public void processNfcTag(Tag tag) {
        _scanState.postValue(1);
        NfcReader.readTag(tag, new NfcReader.Callback() {
            @Override
            public void onSuccess(TransitCard card) {
                executor.execute(() -> {
                    TransitCard existing =
                        dao.getCardByUid(card.uid);
                    if (existing != null) {
                        card.addedAt = existing.addedAt;
                    }
                    dao.insertOrUpdate(card);
                    _scanStatus.postValue(
                        existing == null ? "added" : "updated"
                    );
                    _scanState.postValue(2);
                });
            }

            @Override
            public void onError(String message) {
                _scanStatus.postValue(message);
                _scanState.postValue(3);
            }
        });
    }

    public void deleteCard(TransitCard card) {
        executor.execute(() -> dao.delete(card));
    }

    public void resetState() {
        _scanState.postValue(0);
    }
}
