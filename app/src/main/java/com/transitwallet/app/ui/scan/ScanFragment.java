package com.transitwallet.app.ui.scan;

import android.animation.*;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.transitwallet.app.R;
import com.transitwallet.app.audio.SoundManager;
import com.transitwallet.app.ui.cards.CardsViewModel;
import java.util.List;

public class ScanFragment extends Fragment {

    private CardsViewModel viewModel;
    private AnimatorSet pulseAnim;
    private TextView scanStatus;
    private TextView scanHint;
    private TextView activeCardName;
    private TextView activeCardNumber;
    private TextView activeCardBalance;
    private SoundManager soundManager;
    private boolean nfcStartPlayed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan,
            container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity())
            .get(CardsViewModel.class);

        soundManager = SoundManager.getInstance(
            requireContext());

        scanStatus     = view.findViewById(R.id.scan_status);
        scanHint       = view.findViewById(R.id.scan_hint);
        activeCardName = view.findViewById(R.id.active_card_name);
        activeCardNumber = view.findViewById(R.id.active_card_number);
        activeCardBalance = view.findViewById(R.id.active_card_balance);

        startPulse(view);

        // Показываем активную карту
        viewModel.cards.observe(getViewLifecycleOwner(), cards -> {
            if (cards != null && !cards.isEmpty()) {
                var card = cards.get(0);
                activeCardName.setText(card.getDisplayName());
                activeCardNumber.setText(card.cardNumber);
                if (card.balance >= 0) {
                    activeCardBalance.setText(
                        String.format("%.2f \u20bd", card.balance));
                }
            }
        });

        viewModel.scanState.observe(getViewLifecycleOwner(),
            state -> {
                switch (state) {
                    case 1: // Reading
                        if (!nfcStartPlayed) {
                            soundManager.playNfcStart();
                            nfcStartPlayed = true;
                        }
                        scanStatus.setText("Идёт чтение карты...");
                        scanStatus.setTextColor(0xFF4A90E2);
                        scanHint.setText(getString(R.string.reading_card));
                        break;
                    case 2: // Success
                        soundManager.playSuccess();
                        nfcStartPlayed = false;
                        scanStatus.setText("Карта прочитана!");
                        scanStatus.setTextColor(0xFF4CAF88);
                        scanHint.setText(getString(R.string.card_added));
                        break;
                    case 3: // Error
                        soundManager.playError();
                        nfcStartPlayed = false;
                        scanStatus.setText("Ошибка чтения");
                        scanStatus.setTextColor(0xFFF5A623);
                        scanHint.setText(getString(R.string.read_error));
                        break;
                    default:
                        nfcStartPlayed = false;
                        scanStatus.setText(
                            "Поднесите телефон\nк терминалу");
                        scanStatus.setTextColor(0xFFFFFFFF);
                        scanHint.setText("Идёт чтение карты...");
                }
            });
    }

    private void startPulse(View root) {
        View r1 = root.findViewById(R.id.pulse_ring_1);
        View r2 = root.findViewById(R.id.pulse_ring_2);
        View r3 = root.findViewById(R.id.pulse_ring_3);
        pulseAnim = new AnimatorSet();
        pulseAnim.playTogether(
            makePulse(r1, 0),
            makePulse(r2, 500),
            makePulse(r3, 1000)
        );
        pulseAnim.start();
    }

    private AnimatorSet makePulse(View v, long delay) {
        ObjectAnimator sx =
            ObjectAnimator.ofFloat(v, "scaleX", 0.8f, 1.3f);
        ObjectAnimator sy =
            ObjectAnimator.ofFloat(v, "scaleY", 0.8f, 1.3f);
        ObjectAnimator a =
            ObjectAnimator.ofFloat(v, "alpha", 0.9f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sx, sy, a);
        set.setDuration(2500);
        set.setStartDelay(delay);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                set.start();
            }
        });
        return set;
    }

    @Override
    public void onDestroyView() {
        if (pulseAnim != null) pulseAnim.cancel();
        super.onDestroyView();
    }
}
