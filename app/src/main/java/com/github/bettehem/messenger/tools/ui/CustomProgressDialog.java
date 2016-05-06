package com.github.bettehem.messenger.tools.ui;

import android.app.ProgressDialog;
import android.content.Context;

public class CustomProgressDialog extends ProgressDialog {
    private ProgressDialog progressDialog;

    public CustomProgressDialog(Context context, String title, String message, boolean cancelable){
        super(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(cancelable);
    }

    public CustomProgressDialog(Context context, String title, String message, int maxProgress, boolean cancelable){
        super(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(maxProgress);
        progressDialog.setCancelable(cancelable);
    }

    @Override
    public void show(){
        if (progressDialog.isShowing()){
            dismiss();
        }
        if (progressDialog != null){
            progressDialog.show();
        }
    }

    @Override
    public void dismiss(){
        if (progressDialog.isShowing()){
            if (progressDialog != null){
                progressDialog.dismiss();
            }
        }
    }

    public void setMessage(String message){
        progressDialog.setMessage(message);
    }

    public void addProgress(int incrementBy, boolean dismissIfMax){
        if (progressDialog != null){
            if (dismissIfMax){
                if ((incrementBy + progressDialog.getProgress()) >= progressDialog.getMax()){
                    dismiss();
                }else{
                    progressDialog.incrementProgressBy(incrementBy);
                }
            }else{
                if ((incrementBy + progressDialog.getProgress()) >= progressDialog.getMax()){
                    progressDialog.setProgress(progressDialog.getMax());
                }else{
                    progressDialog.incrementProgressBy(incrementBy);
                }
            }
        }
    }

    public void addProgress(int incrementBy, boolean dismissIfMax, String message){
        if (progressDialog != null){
            progressDialog.setMessage(message);
            if (dismissIfMax){
                if ((incrementBy + progressDialog.getProgress()) >= progressDialog.getMax()){
                    dismiss();
                }else{
                    progressDialog.incrementProgressBy(incrementBy);
                }
            }else{
                if ((incrementBy + progressDialog.getProgress()) >= progressDialog.getMax()){
                    progressDialog.setProgress(progressDialog.getMax());
                }else{
                    progressDialog.incrementProgressBy(incrementBy);
                }
            }
        }
    }

    public void setProgress(int progress){
        if (progressDialog != null && progress < progressDialog.getMax()){
            progressDialog.setProgress(progress);
        }
    }

}
