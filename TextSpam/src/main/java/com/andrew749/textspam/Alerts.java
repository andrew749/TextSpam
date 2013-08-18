package com.andrew749.textspam;

import android.app.AlertDialog;

import java.util.ArrayList;

/**
 * Created by andrew on 18/08/13.
 */
public class Alerts {
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    public Alerts(AlertDialog.Builder alertDialogBuilder,AlertDialog alertDialog){
        this.alertDialogBuilder=alertDialogBuilder;
        this.alertDialog = alertDialog;
    }
     public void clearAlerts(ArrayList<Custom> item){

     }
}
