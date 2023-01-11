package edu.miami.cs.vraj_patel.phlogger;
import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.app.ComponentActivity;

public class CustomFrag extends DialogFragment implements View.OnClickListener {
    View dialogView;
    ImageView theImage;
    // not much to see here, just showing a the user a bigger version of the picture they took/selected

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog, container);
        theImage = (ImageView) dialogView.findViewById(R.id.dialog_pic); //linking this ImageView to the one in the dialog XML
        ((Button) dialogView.findViewById(R.id.dismiss)).setOnClickListener(this);
        theImage.setImageURI(this.getArguments().getParcelable("show_image")); //getting the URI from the Main activity to display
        return (dialogView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dismiss:
                dismiss(); //dismissing the dialog
                break;

        }

    }
}
