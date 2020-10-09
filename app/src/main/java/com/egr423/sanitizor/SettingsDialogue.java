package com.egr423.sanitizor;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class SettingsDialogue extends BottomSheetDialogFragment {


    private View view;

    public static boolean PLAY_GAME_AUDIO = true;
    public static boolean USE_DARK_MODE = true;
    public static boolean USE_GYRO_CONTROLS = true;

    private RadioGroup controlSchemeRadioGroup;
    private Button muteUnmuteButton;
    private Button darkModeLightModeButton;
    private Button clearLeaderboardButton;

    private static SharedPreferences sharedPref;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_layout, container, false);

        Context context = getActivity();
        sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);

        controlSchemeRadioGroup = view.findViewById(R.id.control_scheme);
        muteUnmuteButton = view.findViewById(R.id.mute_unmute_button);
        darkModeLightModeButton = view.findViewById(R.id.dark_mode_light_mode_button);
        clearLeaderboardButton = view.findViewById(R.id.clear_local_leaderboard_button);

        darkModeLightModeButton.setOnClickListener(setOnClickListenerById(controlSchemeRadioGroup.getId()));
        muteUnmuteButton.setOnClickListener(setOnClickListenerById(muteUnmuteButton.getId()));
        darkModeLightModeButton.setOnClickListener(setOnClickListenerById(darkModeLightModeButton.getId()));
        clearLeaderboardButton.setOnClickListener(setOnClickListenerById(clearLeaderboardButton.getId()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.show();
    }

    private View.OnClickListener setOnClickListenerById(int id) {

        switch (id) {
            case R.id.control_scheme:
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
                        Log.d("SettingsDialogue",
                                String.format("SettingsDialogue.PLAY_GAME_AUDIO: %s",
                                        PLAY_GAME_AUDIO));
                        sharedPref.edit().putBoolean("PLAY_GAME_AUDIO", PLAY_GAME_AUDIO).apply();
                    }
                };

            case R.id.dark_mode_light_mode_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("SettingsDialogue",
                                String.format("SettingsDialogue.USE_DARK_MODE: %s",
                                        USE_DARK_MODE));
                        sharedPref.edit().putBoolean("USE_DARK_MODE", USE_DARK_MODE).apply();
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

}
