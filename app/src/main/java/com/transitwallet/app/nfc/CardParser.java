package com.transitwallet.app.nfc;

import com.transitwallet.app.data.model.TransitCard;
import java.util.Map;

public class CardParser {

    public static TransitCard parse(byte[] uid, String[] techList,
                                    byte[] isoDepResponse,
                                    Map<String, byte[]> rawBytes) {
        String uidHex = toHex(uid);
        String cardType = detectType(uidHex, techList, isoDepResponse);
        double balance = parseBalance(rawBytes);
        int trips = parseTrips(rawBytes);
        String cardNumber = formatNumber(uidHex, cardType);
        long now = System.currentTimeMillis();

        return new TransitCard(
            uidHex, cardType, cardNumber,
            balance, trips, now, now, "{}"
        );
    }

    private static String detectType(String uid, String[] techs,
                                     byte[] isoResp) {
        if (isoResp != null) {
            String hex = toHex(isoResp).toUpperCase();
            if (hex.contains("D41000003") ||
                hex.contains("315449433")) {
                return "TROIKA";
            }
            if (hex.contains("315452455")) {
                return "STRELKA";
            }
        }
        for (String tech : techs) {
            if (tech.contains("MifareClassic")) return "TROIKA";
            if (tech.contains("IsoDep"))        return "STRELKA";
            if (tech.contains("MifareUltralight")) return "EDINIY";
        }
        return "UNKNOWN";
    }

    private static double parseBalance(Map<String, byte[]> raw) {
        byte[] block = raw.get("sector4block1");
        if (block != null && block.length >= 4) {
            int kopecks = ((block[3] & 0xFF) << 24)
                        | ((block[2] & 0xFF) << 16)
                        | ((block[1] & 0xFF) << 8)
                        |  (block[0] & 0xFF);
            if (kopecks >= 0 && kopecks <= 10_000_000) {
                return kopecks / 100.0;
            }
        }
        return -1.0;
    }

    private static int parseTrips(Map<String, byte[]> raw) {
        byte[] data = raw.get("tripsData");
        if (data != null && data.length >= 2) {
            return ((data[1] & 0xFF) << 8) | (data[0] & 0xFF);
        }
        return 0;
    }

    private static String formatNumber(String uid, String type) {
        switch (type) {
            case "TROIKA":
                return "Тройка " +
                    uid.substring(0, 4).toUpperCase() + "-" +
                    uid.substring(4, 8).toUpperCase();
            case "STRELKA":
                return "Стрелка " +
                    uid.substring(0, 8).toUpperCase();
            case "EDINIY":
                return "Единый " + uid.toUpperCase();
            default:
                return "Карта " + uid.toUpperCase();
        }
    }

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
