package com.example.serene;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class AvatarView extends FrameLayout {
    private ImageView base;
    private ImageView eyes;
    private ImageView mouth;
    private ImageView accessory;
    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        inflate(context, R.layout.view_avatar, this);

        base = findViewById(R.id.imgBase);
        eyes = findViewById(R.id.imgEyes);
        mouth = findViewById(R.id.imgMouth);
        accessory = findViewById(R.id.imgAccessory);
    }
    public void setAvatarConfig(String eyesType, String mouthType, String accessoryName) {
        base.setImageResource(R.drawable.avatar_base);
        switch (eyesType) {
            case "Happy":
                eyes.setImageResource(R.drawable.eyes_happy);
                break;
            case "Normal":
                eyes.setImageResource(R.drawable.eyes_normal);
                break;
            case "Sparkle":
                eyes.setImageResource(R.drawable.eyes_sparkle);
                break;
            case "Surprised":
                eyes.setImageResource(R.drawable.eyes_surprised);
                break;
            case "Wink":
                eyes.setImageResource(R.drawable.eyes_wink);
                break;
        }
        switch (mouthType) {
            case "Cute":
                mouth.setImageResource(R.drawable.mouth_cute);
                break;
            case "Neutral":
                mouth.setImageResource(R.drawable.mouth_neutral);
                break;
            case "Sad":
                mouth.setImageResource(R.drawable.mouth_sad);
                break;
            case "Surprised":
                mouth.setImageResource(R.drawable.mouth_surprised);
                break;
            case "Smile":
                mouth.setImageResource(R.drawable.mouth_smile);
                break;
        }
        switch (accessoryName) {
            case "Glasses":
                accessory.setImageResource(R.drawable.acc_glasses);
                break;
            case "Sunglasses":
                accessory.setImageResource(R.drawable.acc_sunglasses);
                break;
            case "Cap":
                accessory.setImageResource(R.drawable.acc_cap);
                break;
            case "Headband":
                accessory.setImageResource(R.drawable.acc_headband);
                break;
            case "Earrings":
                accessory.setImageResource(R.drawable.acc_earrings);
                break;
            case "Beanie":
                accessory.setImageResource(R.drawable.acc_beanie);
                break;
            case "Bow":
                accessory.setImageResource(R.drawable.acc_bow);
                break;
            default:
                accessory.setImageDrawable(null);
        }
    }
}