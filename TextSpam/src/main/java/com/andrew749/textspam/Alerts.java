package com.andrew749.textspam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by andrew on 18/08/13.
 */
public class Alerts {
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    boolean decision = false;
    Context context;

    public Alerts(Context context) {
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialog = new AlertDialog.Builder(context).create();
        this.context = context;
    }

    /**
     * alert displayed when list is cleared
     *
     * @param item
     */
    public void clearAlerts(ArrayList<Custom> item) {
        item.clear();
        alertDialogBuilder = new AlertDialog.Builder(context);


        // Setting Dialog Title
        alertDialogBuilder.setTitle("Notice");

        // Setting Dialog Message
        alertDialogBuilder.setMessage("If you want to only remove one item just long click on the item.");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("Success", "cleared all entries");
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void changedAlert() {
        alertDialogBuilder.setTitle("Changes");
        // Setting Dialog Title
        alertDialogBuilder.setTitle("What's Changed");
        // Setting Dialog Message
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.notification, new LinearLayout(context));
        alertDialogBuilder.setView(dialoglayout);
        alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public boolean warningAlert() {
        decision = false;
        alertDialogBuilder = new AlertDialog.Builder(context);


        // Setting Dialog Title
        alertDialogBuilder.setTitle("Warning");

        // Setting Dialog Message
        alertDialogBuilder.setMessage("NOTE: Most Android phones cannot handle more than 30 messages sent at " +
                "one time, Android has a built in mechanism to limit the number of texts.");

        // Setting OK Button
        alertDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //need to fix
                decision = true;
                alertDialog.dismiss();


            }
        });
        alertDialogBuilder.setNegativeButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return decision;
    }

    public void contactAlert() {
        alertDialog.dismiss();


        // Setting Dialog Title
        alertDialogBuilder.setTitle("Warning");

        // Setting Dialog Message
        alertDialogBuilder.setMessage("The built in contact picker will currently only return results properly for " +
                "North American numbers, please adjust the number accordingly for the time being if you are texting " +
                "any other region" +
                ".");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();

            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
