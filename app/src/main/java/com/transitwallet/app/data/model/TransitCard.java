package com.transitwallet.app.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "transit_cards")
public class TransitCard {

    @PrimaryKey
    @NonNull
    public String uid;
    public String cardType;
    public String cardNumber;
    public double balance;
    public int tripsCount;
    public long lastUsed;
    public long addedAt;
    public String rawData;

    public TransitCard(@NonNull String uid, String cardType,
                       String cardNumber, double balance,
                       int tripsCount, long lastUsed,
                       long addedAt, String rawData) {
        this.uid = uid;
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.balance = balance;
        this.tripsCount = tripsCount;
        this.lastUsed = lastUsed;
        this.addedAt = addedAt;
        this.rawData = rawData;
    }

    public String getDisplayName() {
        switch (cardType) {
            case "TROIKA":  return "Тройка";
            case "STRELKA": return "Стрелка";
            case "EDINIY":  return "Единый";
            default:        return "Транспортная карта";
        }
    }

    public int getGradientStart() {
        switch (cardType) {
            case "TROIKA":  return 0xFF1A237E;
            case "STRELKA": return 0xFF1B5E20;
            case "EDINIY":  return 0xFFB71C1C;
            default:        return 0xFF37474F;
        }
    }

    public int getGradientEnd() {
        switch (cardType) {
            case "TROIKA":  return 0xFF283593;
            case "STRELKA": return 0xFF2E7D32;
            case "EDINIY":  return 0xFFC62828;
            default:        return 0xFF455A64;
        }
    }
}
