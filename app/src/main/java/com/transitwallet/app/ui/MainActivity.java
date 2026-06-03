package com.transitwallet.app.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.transitwallet.app.R;
import com.transitwallet.app.ui.cards.CardsViewModel;

public class MainActivity extends AppCompatActivity {

    private CardsViewModel viewModel;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this)
            .get(CardsViewModel.class);

        bottomNav = findViewById(R.id.bottom_nav);

        NavHostFragment navHost = (NavHostFragment)
            getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHost.getNavController();
        NavigationUI.setupWithNavController(bottomNav, navController);

        setupNfc();
        observeViewModel();
    }

    private void applyTheme() {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(this);
        int mode = prefs.getInt("theme_mode",
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private void setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this,
                getString(R.string.nfc_not_supported),
                Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, getClass())
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_MUTABLE);
    }

    private void observeViewModel() {
        viewModel.scanState.observe(this, state -> {
            if (state == 2) {
                String status = viewModel.scanStatus.getValue();
                String msg = "added".equals(status)
                    ? getString(R.string.card_added)
                    : getString(R.string.card_updated);
                Toast.makeText(this, msg,
                    Toast.LENGTH_SHORT).show();
                bottomNav.setSelectedItemId(R.id.cardsFragment);
                viewModel.resetState();
            } else if (state == 3) {
                Toast.makeText(this,
                    getString(R.string.read_error),
                    Toast.LENGTH_LONG).show();
                viewModel.resetState();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(this,
                    getString(R.string.nfc_disabled),
                    Toast.LENGTH_LONG).show();
            }
            nfcAdapter.enableForegroundDispatch(
                this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||
            NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(
                NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                bottomNav.setSelectedItemId(R.id.scanFragment);
                viewModel.processNfcTag(tag);
            }
        }
    }
}
