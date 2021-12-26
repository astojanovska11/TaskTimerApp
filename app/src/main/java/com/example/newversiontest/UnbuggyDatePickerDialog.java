package com.example.newversiontest;

import android.app.DatePickerDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UnbuggyDatePickerDialog extends DatePickerDialog {
    public UnbuggyDatePickerDialog(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
    }

    @Override
    protected void onStop() {
        /* do nothing - do NOT call super method */
    }
}
