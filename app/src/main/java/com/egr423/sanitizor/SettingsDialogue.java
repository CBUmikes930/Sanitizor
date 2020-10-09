package com.egr423.sanitizor;

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

public class SettingsDialogue extends BottomSheetDialogFragment {


    private static final String PREFENCE_FILE = "app_settings";

    public static Boolean playGameAudio;
    public static Boolean useDarkMode;
    public static Integer controlScheme;
    public static boolean useGyroControls = false;

    private RadioGroup controlSchemeRadioGroup;
    private Button muteUnmuteButton;
    private Button darkModeLightModeButton;
    private Button clearLeaderboardButton;

    private static SharedPreferences sharedPref;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_layout, container, false);

        controlSchemeRadioGroup = view.findViewById(R.id.control_scheme);
        muteUnmuteButton = view.findViewById(R.id.mute_unmute_button);
        darkModeLightModeButton = view.findViewById(R.id.dark_mode_light_mode_button);
        clearLeaderboardButton = view.findViewById(R.id.clear_local_leaderboard_button);

        controlSchemeRadioGroup.setOnCheckedChangeListener(setOnCheckedChangeListener(controlSchemeRadioGroup.getId()));
        muteUnmuteButton.setOnClickListener(setOnClickListenerById(muteUnmuteButton.getId()));
        darkModeLightModeButton.setOnClickListener(setOnClickListenerById(darkModeLightModeButton.getId()));
        clearLeaderboardButton.setOnClickListener(setOnClickListenerById(clearLeaderboardButton.getId()));

        setDisplay(controlSchemeRadioGroup);
        setDisplay(muteUnmuteButton);
        setDisplay(darkModeLightModeButton);
        setDisplay(clearLeaderboardButton);

        return view;
    }

    private void setDisplay(View view) {
        switch (view.getId()) {
            case R.id.control_scheme:
                RadioGroup radioGroup = (RadioGroup) view;
                int check = sharedPref.getInt("controlScheme", R.id.gyro_controls);
                if (check != -1) {
                    radioGroup.check(check);
                } else {
                    radioGroup.clearCheck();
                }
                return;

            case R.id.mute_unmute_button:
                Button unMuteButton = (Button) view;
                String unMuteButtonText = "Mute";
                if (!playGameAudio) {
                    unMuteButtonText = "Unmute";
                }
                unMuteButton.setText(unMuteButtonText);
                return;

            case R.id.dark_mode_light_mode_button:
                Button darkModeButton = (Button) view;
                String darkModeButtonText = "Dark Mode";
                if (!useDarkMode) {
                    darkModeButtonText = "Light Mode";
                }
                darkModeButton.setText(darkModeButtonText);
                return;

            case R.id.clear_local_leaderboard_button:
                Button clearLeaderBoardButton = (Button) view;
                //todo dynamically set style?????????
                return;


            default:
                throw new IllegalArgumentException("id should be an id one of the associated buttons" +
                        "of the SettingsDialogue");

        }
    }

    private RadioGroup.OnCheckedChangeListener setOnCheckedChangeListener(int id) {
        switch (id) {
            case R.id.control_scheme:
                return new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        sharedPref.edit().putInt("controlScheme", checkedId).apply();
                    }

                };
            default:
                throw new IllegalArgumentException("id should be an id one of the associated " +
                        "RadioButtonGroup of the SettingsDialogue");
        }
    }

    private View.OnClickListener setOnClickListenerById(int id) {

        switch (id) {
            case R.id.mute_unmute_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playGameAudio = !playGameAudio;
                        onClickSettingsButton(v, playGameAudio, "playGameAudio",
                                "Game audio has been turned");
                        setDisplay(muteUnmuteButton);
                    }
                };

            case R.id.dark_mode_light_mode_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        useDarkMode = !useDarkMode;
                        onClickSettingsButton(v, useDarkMode, "useDarkMode",
                                "Dark Mode has been turned");
                        setDisplay(darkModeLightModeButton);
                    }
                };

            case R.id.clear_local_leaderboard_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendToast(v, "Local Database has been cleared.");
                        setDisplay(clearLeaderboardButton);
                    }
                };
            default:
                throw new IllegalArgumentException("id should be an id one of the associated buttons" +
                        "of the SettingsDialogue");
        }
    }


    private void onClickSettingsButton(View v, boolean booleanField, String key, String toastText) {
        sharedPref.edit().putBoolean(key, booleanField).apply();
        if (!booleanField) {
            toastText += " off.";
        } else {
            toastText += " on.";
        }
        sendToast(v, toastText);
        Log.d("SettingsDialogue",
                String.format("SettingsDialogue.%s: %s",
                        key, booleanField));
    }

    private void sendToast(View v, CharSequence toastText) {
        Context context = v.getContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, toastText, duration);
        toast.show();
    }

    public static Boolean getPlayGameAudio(Context context) {
        return playGameAudio;
    }


    public static void initialize(Context context){
        sharedPref = context.getSharedPreferences(PREFENCE_FILE, Context.MODE_PRIVATE);
        controlScheme = sharedPref.getInt("controlScheme", R.id.gyro_controls);
        playGameAudio = sharedPref.getBoolean("playGameAudio", true);
        useDarkMode = sharedPref.getBoolean("useDarkMode", true);
    }

}
