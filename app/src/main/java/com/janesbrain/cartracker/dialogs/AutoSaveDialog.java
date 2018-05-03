package com.janesbrain.cartracker.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.janesbrain.cartracker.R;


public class AutoSaveDialog extends DialogFragment {

    private PopupListener myHost;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            myHost = (PopupListener) activity;
        } catch (ClassCastException e) {
            // as the activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString());

        }
    }

    @Override
    public Dialog onCreateDialog( Bundle b) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.dialog_header);

        builder.setPositiveButton(R.string.saveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myHost.OnSaved(AutoSaveDialog.this);
            }
        });

        builder.setNegativeButton(R.string.editButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myHost.OnCancelled( new ManualSaveDialog());
            }
        });

        return builder.create();
    }

}
