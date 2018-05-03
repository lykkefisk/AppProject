package com.janesbrain.cartracker.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.service.autofill.FillEventHistory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.janesbrain.cartracker.R;

// http://stacktips.com/tutorials/android/android-dialog-fragment-example
// https://developer.android.com/guide/topics/ui/dialogs

public class ManualSaveDialog extends DialogFragment {


    private String typedAddress = "";
    public String GetTypedAddess(){
            return typedAddress;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        final View root = inflater.inflate(R.layout.manual_address, null);
        Button ok = root.findViewById(R.id.saveManualButton);

        // TODO set up the rest of the views
        // i have no idea wwhih view is which id
        // took me 20 minutes to find the and correct the two buttons

         ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(),
                        "NOT DOING ANYTHING\r\nPRESS CANCEL", Toast.LENGTH_LONG).show();

               // EditText txt = root.findViewById(R.id.addressEditText);
               // typedAddress = txt.getText().toString();
                // TODO ... fix the ids in all the layouts
                // the id's are cross referenced.. BAD PUPPY!
            }
        });
        Button cancel = root.findViewById(R.id.cancelManualButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return root;
    }

}
