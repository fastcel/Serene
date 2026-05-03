package com.example.serene;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FocusFragment extends Fragment {

    private TextView tvTimer, tvModeLabel, tvSessionSummary;
    private ImageView btnPlayPause;
    private TextView btnReset, btnSkip, btnNewSession;
    private boolean hasShownDialog = false;
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private long remainingMillis;
    private int sessionWorkMin  = 25;
    private int sessionBreakMin = 5;
    private boolean useShortBreak = true;
    private String currentMode = "work";
    private boolean sessionConfigured = false;
    public FocusFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_focus, container, false);
        tvTimer          = view.findViewById(R.id.tvTimer);
        tvModeLabel      = view.findViewById(R.id.tvModeLabel);
        tvSessionSummary = view.findViewById(R.id.tvSessionSummary);
        btnPlayPause     = view.findViewById(R.id.btnPlayPause);
        btnReset         = view.findViewById(R.id.btnReset);
        btnSkip          = view.findViewById(R.id.btnSkip);
        btnNewSession    = view.findViewById(R.id.btnNewSession);
        remainingMillis = 25 * 60 * 1000L;
        updateTimerDisplay(remainingMillis);
        btnPlayPause.setImageResource(R.drawable.play);
        btnPlayPause.setOnClickListener(v -> {
            if (!sessionConfigured) {
                showSessionDialog();
                return;
            }
            if (isRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });
        btnReset.setOnClickListener(v -> {
            cancelTimer();
            sessionConfigured = false;
            isRunning = false;
            currentMode = "work";
            remainingMillis = sessionWorkMin * 60 * 1000L;
            updateTimerDisplay(remainingMillis);
            tvModeLabel.setText("work session");
            tvSessionSummary.setText("No session started");
            btnPlayPause.setImageResource(R.drawable.play);
        });
        btnSkip.setOnClickListener(v -> {
            if (!sessionConfigured) return;
            cancelTimer();
            isRunning = false;
            autoSwitchMode();
        });
        btnNewSession.setOnClickListener(v -> {
            cancelTimer();
            isRunning = false;
            sessionConfigured = false;
            btnPlayPause.setImageResource(R.drawable.play);
            showSessionDialog();
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!hasShownDialog) {
            showSessionDialog();
            hasShownDialog = true;
        }
    }
    private void showSessionDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_session_setup, null);
        TextView opt25      = dialogView.findViewById(R.id.optWork25);
        TextView opt45      = dialogView.findViewById(R.id.optWork45);
        TextView opt60      = dialogView.findViewById(R.id.optWork60);
        TextView optShort   = dialogView.findViewById(R.id.optBreakShort);
        TextView optLong    = dialogView.findViewById(R.id.optBreakLong);
        View layoutShortOpts = dialogView.findViewById(R.id.layoutShortOpts);
        View layoutLongOpts  = dialogView.findViewById(R.id.layoutLongOpts);
        TextView optShort5  = dialogView.findViewById(R.id.optShort5);
        TextView optShort10 = dialogView.findViewById(R.id.optShort10);
        TextView optShort15 = dialogView.findViewById(R.id.optShort15);
        TextView optLong20  = dialogView.findViewById(R.id.optLong20);
        TextView optLong35  = dialogView.findViewById(R.id.optLong35);
        TextView optLong50  = dialogView.findViewById(R.id.optLong50);
        final int[]     pickedWorkMin  = {25};
        final int[]     pickedBreakMin = {5};
        final boolean[] pickedShort    = {true};
        Runnable refreshWork = () -> {
            int[] opts = {25, 45, 60};
            TextView[] views = {opt25, opt45, opt60};
            for (int i = 0; i < 3; i++) {
                boolean sel = pickedWorkMin[0] == opts[i];
                views[i].setBackground(requireContext().getDrawable(sel ? R.drawable.chip_selected : R.drawable.chip_unselected));
                views[i].setTextColor(getResources().getColor(sel ? android.R.color.white : android.R.color.black));
            }
        };
        Runnable refreshBreakType = () -> {
            boolean s = pickedShort[0];
            optShort.setBackground(requireContext().getDrawable(s ? R.drawable.chip_selected : R.drawable.chip_unselected));
            optLong.setBackground(requireContext().getDrawable(!s ? R.drawable.chip_selected : R.drawable.chip_unselected));
            optShort.setTextColor(getResources().getColor(s ? android.R.color.white : android.R.color.black));
            optLong.setTextColor(getResources().getColor(!s ? android.R.color.white : android.R.color.black));
            layoutShortOpts.setVisibility(s ? View.VISIBLE : View.GONE);
            layoutLongOpts.setVisibility(!s ? View.VISIBLE : View.GONE);
        };
        Runnable refreshShortOpts = () -> {
            int[] opts = {5, 10, 15};
            TextView[] views = {optShort5, optShort10, optShort15};
            for (int i = 0; i < 3; i++) {
                boolean sel = pickedBreakMin[0] == opts[i];
                views[i].setBackground(requireContext().getDrawable(sel ? R.drawable.chip_selected : R.drawable.chip_unselected));
                views[i].setTextColor(getResources().getColor(sel ? android.R.color.white : android.R.color.black));
            }
        };
        Runnable refreshLongOpts = () -> {
            int[] opts = {20, 35, 50};
            TextView[] views = {optLong20, optLong35, optLong50};
            for (int i = 0; i < 3; i++) {
                boolean sel = pickedBreakMin[0] == opts[i];
                views[i].setBackground(requireContext().getDrawable(sel ? R.drawable.chip_selected : R.drawable.chip_unselected));
                views[i].setTextColor(getResources().getColor(sel ? android.R.color.white : android.R.color.black));
            }
        };
        refreshWork.run();
        refreshBreakType.run();
        refreshShortOpts.run();
        refreshLongOpts.run();
        opt25.setOnClickListener(v -> { pickedWorkMin[0] = 25; refreshWork.run(); });
        opt45.setOnClickListener(v -> { pickedWorkMin[0] = 45; refreshWork.run(); });
        opt60.setOnClickListener(v -> { pickedWorkMin[0] = 60; refreshWork.run(); });
        optShort.setOnClickListener(v -> { pickedShort[0] = true;  pickedBreakMin[0] = 5;  refreshBreakType.run(); refreshShortOpts.run(); });
        optLong.setOnClickListener(v  -> { pickedShort[0] = false; pickedBreakMin[0] = 20; refreshBreakType.run(); refreshLongOpts.run(); });
        optShort5.setOnClickListener(v  -> { pickedBreakMin[0] = 5;  refreshShortOpts.run(); });
        optShort10.setOnClickListener(v -> { pickedBreakMin[0] = 10; refreshShortOpts.run(); });
        optShort15.setOnClickListener(v -> { pickedBreakMin[0] = 15; refreshShortOpts.run(); });
        optLong20.setOnClickListener(v  -> { pickedBreakMin[0] = 20; refreshLongOpts.run(); });
        optLong35.setOnClickListener(v  -> { pickedBreakMin[0] = 35; refreshLongOpts.run(); });
        optLong50.setOnClickListener(v  -> { pickedBreakMin[0] = 50; refreshLongOpts.run(); });
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();
        dialogView.findViewById(R.id.btnStartSession).setOnClickListener(v -> {
            sessionWorkMin  = pickedWorkMin[0];
            sessionBreakMin = pickedBreakMin[0];
            useShortBreak   = pickedShort[0];
            sessionConfigured = true;
            currentMode = "work";
            remainingMillis = sessionWorkMin * 60 * 1000L;
            updateTimerDisplay(remainingMillis);
            tvModeLabel.setText("work session");
            updateSessionSummary();
            startTimer();
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.btnCancelSession).setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(remainingMillis, 1000) {
            @Override public void onTick(long ms) {
                remainingMillis = ms;
                updateTimerDisplay(ms);
            }
            @Override public void onFinish() {
                isRunning = false;
                autoSwitchMode();
            }
        }.start();
        isRunning = true;
        btnPlayPause.setImageResource(R.drawable.pause);
    }
    private void pauseTimer() {
        cancelTimer();
        isRunning = false;
        btnPlayPause.setImageResource(R.drawable.play); // FIXED
    }
    private void cancelTimer() {
        if (countDownTimer != null) { countDownTimer.cancel(); countDownTimer = null; }
    }
    private void autoSwitchMode() {
        currentMode = currentMode.equals("work") ? "break" : "work";
        remainingMillis = currentMode.equals("work")
                ? sessionWorkMin * 60 * 1000L
                : sessionBreakMin * 60 * 1000L;
        updateTimerDisplay(remainingMillis);
        tvModeLabel.setText(currentMode.equals("work") ? "work session"
                : (useShortBreak ? "short break" : "long break"));
        startTimer();
    }
    private void updateTimerDisplay(long millis) {
        long s = millis / 1000;
        tvTimer.setText(String.format("%02d:%02d", s / 60, s % 60));
    }

    private void updateSessionSummary() {
        String breakType = useShortBreak ? "short" : "long";
        tvSessionSummary.setText(sessionWorkMin + " min work  ·  "
                + sessionBreakMin + " min " + breakType + " break");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelTimer();
    }
}