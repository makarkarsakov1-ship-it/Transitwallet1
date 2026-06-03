package com.transitwallet.app.ui.history;

import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.transitwallet.app.R;
import com.transitwallet.app.ui.cards.CardsViewModel;

public class HistoryFragment extends Fragment {

    private CardsViewModel viewModel;
    private RecyclerView recyclerView;
    private LinearLayout emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history,
            container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity())
            .get(CardsViewModel.class);

        recyclerView = view.findViewById(R.id.history_recycler);
        emptyState = view.findViewById(R.id.empty_state);

        JourneyAdapter adapter = new JourneyAdapter();
        recyclerView.setLayoutManager(
            new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel.cards.observe(getViewLifecycleOwner(), cards -> {
            if (cards == null || cards.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.loadFromCards(cards);
            }
        });
    }
}
