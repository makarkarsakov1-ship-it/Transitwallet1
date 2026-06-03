package com.transitwallet.app.ui.cards;

import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.transitwallet.app.R;

public class CardsFragment extends Fragment {

    private CardsViewModel viewModel;
    private CardsAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
            R.layout.fragment_cards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity())
            .get(CardsViewModel.class);

        recyclerView = view.findViewById(R.id.cards_recycler);
        emptyState   = view.findViewById(R.id.empty_state);

        adapter = new CardsAdapter(card ->
            new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.delete_card))
                .setMessage(getString(R.string.delete_confirm))
                .setPositiveButton(getString(R.string.delete),
                    (d, w) -> viewModel.deleteCard(card))
                .setNegativeButton(
                    getString(R.string.cancel), null)
                .show()
        );

        recyclerView.setLayoutManager(
            new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel.cards.observe(getViewLifecycleOwner(), cards -> {
            adapter.setCards(cards);
            boolean empty = cards == null || cards.isEmpty();
            emptyState.setVisibility(
                empty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(
                empty ? View.GONE : View.VISIBLE);
        });
    }
}
