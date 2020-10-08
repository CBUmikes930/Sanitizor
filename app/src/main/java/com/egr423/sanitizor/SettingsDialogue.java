package com.egr423.sanitizor;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SettingsDialogue extends BottomSheetDialogFragment {

    public static boolean PLAY_GAME_AUDIO = true;
    public static boolean USE_GYRO_CONTROLS = false;

    private SettingsDialogListener dialogListener;
    private Button notificationsButton;
    private Button muteUnmuteButton;
    private Button darkModeLightModeButton;
    private Button clearLeaderboardButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_layout, container, false);

        notificationsButton = v.findViewById(R.id.enable_disable_notifications);
        muteUnmuteButton = v.findViewById(R.id.mute_unmute_button);
        darkModeLightModeButton = v.findViewById(R.id.dark_mode_light_mode_button);
        clearLeaderboardButton = v.findViewById(R.id.clear_local_leaderboard_button);

        darkModeLightModeButton.setOnClickListener(setOnClickListenerById(notificationsButton.getId()));
        muteUnmuteButton.setOnClickListener(setOnClickListenerById(muteUnmuteButton.getId()));
        darkModeLightModeButton.setOnClickListener(setOnClickListenerById(darkModeLightModeButton.getId()));
        clearLeaderboardButton.setOnClickListener(setOnClickListenerById(clearLeaderboardButton.getId()));

        return v;
    }

    public interface SettingsDialogListener {
        void onButtonClicked();
    }

    private View.OnClickListener setOnClickListenerById(int id) {

        switch (id) {
            case R.id.enable_disable_notifications:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                };
            case R.id.mute_unmute_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PLAY_GAME_AUDIO = !PLAY_GAME_AUDIO;
                        String toastText = "Game audio has been turned on.";
                        String buttonText = "Mute";
                        if (!PLAY_GAME_AUDIO) {
                            toastText = "Game audio has been turned off.";
                            buttonText = "Unmute";
                        }
                        Context context = v.getContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, toastText, duration);
                        toast.show();
                        muteUnmuteButton.setText(buttonText);
                        Log.d("",
                                String.format("MainActivity.PLAY_GAME_AUDIO: %s",
                                        PLAY_GAME_AUDIO));
                    }
                };

            case R.id.dark_mode_light_mode_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                };

            case R.id.clear_local_leaderboard_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                };

            default:
                throw new IllegalArgumentException("id should be an id one of the associated buttons" +
                        "of the SettingsDialogue");

        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            dialogListener = (SettingsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement SettingsDialogListener");
        }
    }
}
