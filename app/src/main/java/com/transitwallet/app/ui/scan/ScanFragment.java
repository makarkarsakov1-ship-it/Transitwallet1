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
import com.transitwallet.app.ui.cards.CardsViewModel;

public class ScanFragment extends Fragment {

    private CardsViewModel viewModel;
    private AnimatorSet pulseAnim;
    private TextView scanStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
            R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity())
            .get(CardsViewModel.class);

        scanStatus = view.findViewById(R.id.scan_status);
        startPulse(view);

        viewModel.scanState.observe(getViewLifecycleOwner(),
            state -> {
                switch (state) {
                    case 1:
                        scanStatus.setText(
                            getString(R.string.reading_card));
                        scanStatus.setTextColor(0xFF4A90E2);
                        break;
                    case 2:
                        scanStatus.setText(
                            getString(R.string.card_added));
                        scanStatus.setTextColor(0xFF4CAF88);
                        break;
                    case 3:
                        scanStatus.setText(
                            getString(R.string.read_error));
                        scanStatus.setTextColor(0xFFF5A623);
                        break;
                    default:
                        scanStatus.setText(
                            getString(R.string.scan_prompt));
                        scanStatus.setTextColor(0xFF8A9BB8);
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
            makePulse(r2, 400),
            makePulse(r3, 800)
        );
        pulseAnim.start();
    }

    private AnimatorSet makePulse(View v, long delay) {
        ObjectAnimator sx =
            ObjectAnimator.ofFloat(v, "scaleX", 0.8f, 1.2f);
        ObjectAnimator sy =
            ObjectAnimator.ofFloat(v, "scaleY", 0.8f, 1.2f);
        ObjectAnimator a =
            ObjectAnimator.ofFloat(v, "alpha", 0.8f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sx, sy, a);
        set.setDuration(2000);
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
