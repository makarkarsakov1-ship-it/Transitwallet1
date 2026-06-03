package com.transitwallet.app.ui.cards;

import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.transitwallet.app.R;
import com.transitwallet.app.data.model.TransitCard;
import java.util.ArrayList;
import java.util.List;

public class CardsAdapter extends
    RecyclerView.Adapter<CardsAdapter.CardViewHolder> {

    public interface OnDeleteListener {
        void onDelete(TransitCard card);
    }

    private List<TransitCard> cards = new ArrayList<>();
    private final OnDeleteListener deleteListener;

    public CardsAdapter(OnDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void setCards(List<TransitCard> newCards) {
        cards = newCards != null ? newCards : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_transit_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull CardViewHolder h, int pos) {
        h.bind(cards.get(pos));
    }

    @Override
    public int getItemCount() { return cards.size(); }

    class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardName, cardTypeBig, balanceValue;
        View cardRoot;

        CardViewHolder(View v) {
            super(v);
            cardRoot    = v.findViewById(R.id.card_root);
            cardName    = v.findViewById(R.id.card_name);
            cardTypeBig = v.findViewById(R.id.card_type_big);
            balanceValue = v.findViewById(R.id.balance_value);
        }

        void bind(TransitCard card) {
            GradientDrawable grad = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                    card.getGradientStart(),
                    card.getGradientEnd()
                }
            );
            grad.setCornerRadius(60f);
            cardRoot.setBackground(grad);

            cardName.setText(card.cardNumber);
            cardTypeBig.setText(card.getDisplayName());

            if (card.balance >= 0) {
                balanceValue.setText(
                    String.format("%.2f \u20bd", card.balance));
            } else {
                balanceValue.setText("\u2014 \u20bd");
            }

            cardRoot.setOnLongClickListener(v -> {
                deleteListener.onDelete(card);
                return true;
            });
        }
    }
}
