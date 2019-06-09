package com.example.myapplication.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myapplication.R;


/**
 * Created by Gurjot on 05-Apr-17.
 */
public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {
    private SeekBar mSeekBar;
    private TextView mSeekBarValue;
    private int mProgress;

    public SeekBarPreference(Context context) {
        this(context, null, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.seek);
    }

    public int getValue() {
        return mProgress;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mSeekBar = (SeekBar) view.findViewById(R.id.bubbleSeekbar);
        mSeekBarValue = (TextView) view.findViewById(R.id.bubbleSeekbarValue);
        mSeekBar.setFocusable(false);
        mSeekBar.setProgress(mProgress);
        mSeekBarValue.setText("" + mProgress);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser)
            return;

        setValue(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // not used
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
       // // System.out.println("Seek Release");
        // not used
        callChangeListener(mProgress);
    }
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(mProgress) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        if (shouldPersist()) {
            persistInt(value);
        }

        if (value != mProgress) {
            mProgress = value;
            if (mSeekBarValue != null)
                mSeekBarValue.setText("" + mProgress);


            callChangeListener(mProgress);
            //   notifyChanged();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }
}
