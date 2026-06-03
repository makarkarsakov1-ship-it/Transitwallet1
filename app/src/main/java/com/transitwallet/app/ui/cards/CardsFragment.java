package com.transitwallet.app.ui.cards;

import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.transitwallet.app.R;

public class CardsFragment extends Fragment {

    private CardsViewModel viewModel;
    private CardsAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView recentRecycler;
    private LinearLayout emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cards,
            container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity())
            .get(CardsViewModel.class);

        recyclerView  = view.findViewById(R.id.cards_recycler);
        recentRecycler = view.findViewById(R.id.recent_recycler);
        emptyState    = view.findViewById(R.id.empty_state);

        // Быстрые кнопки
        view.findViewById(R.id.btn_add).setOnClickListener(v ->
            Navigation.findNavController(v)
                .navigate(R.id.scanFragment));

        view.findViewById(R.id.btn_topup).setOnClickListener(v ->
            Navigation.findNavController(v)
                .navigate(R.id.scanFragment));

        view.findViewById(R.id.btn_pay_quick).setOnClickListener(v ->
            Navigation.findNavController(v)
                .navigate(R.id.scanFragment));

        view.findViewById(R.id.btn_history_quick).setOnClickListener(v ->
            Navigation.findNavController(v)
                .navigate(R.id.historyFragment));

        adapter = new CardsAdapter(card ->
            new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.delete_card))
                .setMessage(getString(R.string.delete_confirm))
                .setPositiveButton(getString(R.string.delete),
                    (d, w) -> viewModel.deleteCard(card))
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        );

        // Горизонтальный список карт
        androidx.recyclerview.widget.LinearLayoutManager horizontal =
            new androidx.recyclerview.widget.LinearLayoutManager(
                requireContext(),
                androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                false);

        recyclerView.setLayoutManager(horizontal);
        recyclerView.setAdapter(adapter);

        // Вертикальный список недавних
        com.transitwallet.app.ui.history.JourneyAdapter recentAdapter =
            new com.transitwallet.app.ui.history.JourneyAdapter();
        recentRecycler.setLayoutManager(
            new LinearLayoutManager(requireContext()));
        recentRecycler.setAdapter(recentAdapter);

        viewModel.cards.observe(getViewLifecycleOwner(), cards -> {
            adapter.setCards(cards);
            recentAdapter.loadFromCards(cards);
            boolean empty = cards == null || cards.isEmpty();
            emptyState.setVisibility(
                empty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(
                empty ? View.GONE : View.VISIBLE);
        });
    }
}
