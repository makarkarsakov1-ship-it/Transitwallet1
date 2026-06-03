package com.transitwallet.app.nfc;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import com.transitwallet.app.data.model.TransitCard;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NfcReader {

    public interface Callback {
        void onSuccess(TransitCard card);
        void onError(String message);
    }

    private static final ExecutorService executor =
        Executors.newSingleThreadExecutor();

    public static void readTag(Tag tag, Callback callback) {
        executor.execute(() -> {
            try {
                byte[] uid = tag.getId();
                String[] techList = tag.getTechList();
                Map<String, byte[]> rawBytes = new HashMap<>();
                byte[] isoDepData = null;

                if (hasTech(techList, "IsoDep")) {
                    IsoDep isoDep = IsoDep.get(tag);
                    if (isoDep != null) {
                        isoDep.connect();
                        isoDep.setTimeout(3000);
                        byte[] selectCmd = new byte[]{
                            0x00, (byte)0xA4, 0x04, 0x00,
                            0x07,
                            (byte)0xD4, 0x10, 0x00, 0x00,
                            0x30, 0x01, 0x00, 0x00
                        };
                        try {
                            isoDepData = isoDep.transceive(selectCmd);
                        } catch (Exception ignored) {}
                        isoDep.close();
                    }
                }

                if (hasTech(techList, "MifareClassic")) {
                    MifareClassic mifare = MifareClassic.get(tag);
                    if (mifare != null) {
                        mifare.connect();
                        try {
                            byte[] keyDefault =
                                MifareClassic.KEY_DEFAULT;
                            byte[] keyTroika = new byte[]{
                                (byte)0xA7, 0x3F, 0x5D,
                                (byte)0xC1, (byte)0xD3, 0x33
                            };
                            byte[][] keys = {keyDefault, keyTroika};
                            for (byte[] key : keys) {
                                try {
                                    if (mifare.authenticateSectorWithKeyA(
                                            4, key)) {
                                        int block =
                                            mifare.sectorToBlock(4);
                                        rawBytes.put("sector4block1",
                                            mifare.readBlock(block+1));
                                        rawBytes.put("tripsData",
                                            mifare.readBlock(block));
                                        break;
                                    }
                                } catch (Exception ignored) {}
                            }
                        } finally {
                            mifare.close();
                        }
                    }
                }

                TransitCard card = CardParser.parse(
                    uid, techList, isoDepData, rawBytes
                );
                callback.onSuccess(card);

            } catch (Exception e) {
                callback.onError(e.getMessage() != null
                    ? e.getMessage() : "Ошибка чтения");
            }
        });
    }

    private static boolean hasTech(String[] techs, String name) {
        for (String t : techs)
            if (t.contains(name)) return true;
        return false;
    }
}
