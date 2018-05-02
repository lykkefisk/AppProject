package com.janesbrain.cartracker.dialogs;

import android.app.DialogFragment;

public interface PopupListener {
    public void OnSaved(DialogFragment dialog);
    public void OnCancelled(DialogFragment dialog);
}
