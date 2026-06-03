package com.transitwallet.app.ui.history;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.transitwallet.app.R;
import com.transitwallet.app.data.model.TransitCard;
import java.text.SimpleDateFormat;
import java.util.*;

public class JourneyAdapter extends
    RecyclerView.Adapter<JourneyAdapter.JourneyViewHolder> {

    private List<JourneyItem> items = new ArrayList<>();

    public static class JourneyItem {
        public String type;
        public String date;
        public String amount;

        public JourneyItem(String type, String date, String amount) {
            this.type = type;
            this.date = date;
            this.amount = amount;
        }
    }

    public void loadFromCards(List<TransitCard> cards) {
        items.clear();
        SimpleDateFormat sdf = new SimpleDateFormat(
            "dd.MM.yyyy HH:mm", Locale.getDefault());

        for (TransitCard card : cards) {
            String date = sdf.format(new Date(card.lastUsed));
            String name = card.getDisplayName();

            items.add(new JourneyItem(
                name + " — последнее сканирование",
                date,
                card.balance >= 0
                    ? String.format("%.2f \u20bd", card.balance)
                    : "— \u20bd"
            ));

            if (card.tripsCount > 0) {
                items.add(new JourneyItem(
                    "Всего поездок на карте",
                    name,
                    String.valueOf(card.tripsCount)
                ));
            }
        }

        notifyDataSetChanged();

        if (items.isEmpty()) {
            items.add(new JourneyItem(
                "Нет данных",
                "Приложи карту для обновления",
                ""
            ));
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public JourneyViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_journey, parent, false);
        return new JourneyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull JourneyViewHolder h, int pos) {
        JourneyItem item = items.get(pos);
        h.type.setText(item.type);
        h.date.setText(item.date);
        h.amount.setText(item.amount);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class JourneyViewHolder extends RecyclerView.ViewHolder {
        TextView type, date, amount;

        JourneyViewHolder(View v) {
            super(v);
            type   = v.findViewById(R.id.journey_type);
            date   = v.findViewById(R.id.journey_date);
            amount = v.findViewById(R.id.journey_amount);
        }
    }
}
