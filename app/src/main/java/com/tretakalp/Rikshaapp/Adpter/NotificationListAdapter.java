package com.tretakalp.Rikshaapp.Adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tretakalp.Rikshaapp.Activity.LoginActivity;
import com.tretakalp.Rikshaapp.Database.DatabaseHelper;
import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mac on 19/12/17.
 */

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private static final String TAG = "notificationlistAdper";
    private String[] mData = new String[0];
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private StopFuncListener mstopClickListener;
    private ArrayList<Bean> arraylist = new ArrayList<>();
    private List<ViewHolder> lstHolders;

    View view;
    private Context mcontext;
    String companyString = "";
    DatabaseHelper db;
    private boolean onBind;
    //private boolean isPlayingBeep;
    //ToneGenerator toneGen1;
    ViewHolder holder;
    int posss;

    public NotificationListAdapter(Context context, ArrayList<Bean> listLabel, ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.arraylist = listLabel;
        this.mcontext = context;
        this.mClickListener = itemClickListener;
        this.db = DatabaseHelper.getInstance(context);
        this.lstHolders = new ArrayList<>();
        Log.d(TAG, "Constructor----->>>>>");
        //stopCountDown();


    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = mInflater.inflate(R.layout.trip_noti_row, parent, false);
        // lstHolders.add(new ViewHolder(view));// if write here this line then when on row finish then all will finish
        //lstHolders.add(holder);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //holder=viewholder;
        //holder.holderPosition=position;
        // stopCountDown();
        // cancelTimer(holder);
        if (position == 0) {
            // stopCountDown();
            lstHolders = new ArrayList<>();
        }

        //stopCountDown();

        synchronized (lstHolders) {

            lstHolders.add(holder);

        }




        Log.d(TAG, "onBindViewHolder--->>>listSizeHolder" + lstHolders.size());
        holder.txtSource.setText(arraylist.get(position).getPickupLocName() + "");
        holder.txtDest.setText(arraylist.get(position).getDropLocName() + "");
        holder.txtKm.setText(arraylist.get(position).getDistance() + "");
        holder.txtRate.setText(arraylist.get(position).getNetAmount() + "");
        holder.txtsrId.setText(arraylist.get(position).getSrId() + "");
        Log.d(TAG, "listSize: " + arraylist.size());

        Log.d("mytag","TripType Adapter: "+arraylist.get(position).getIsPrepaid());

        if (arraylist.get(position).getIsPrepaid().equals("prepaid")){
            Log.d("mytag","Condition found ");
            cancelTimer(holder);
            // holder.timer.onFinish();

            stopCountDown();

            if (mClickListener != null)
                mClickListener.onItemClick1(view, position);

        }


        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //toneGen1.stopTone();
                Log.d(TAG, "btnCancel----->>>" + holder.txtDest.getText());
                //stopBeep(holder);
                cancelTimer(holder);
                //stopCountDown();
                //lstHolders.remove(position);

                Log.d(TAG, "DropLocation----->>>" + arraylist.get(position).getDropLocName() + "");
                Log.d(TAG, "New_listSizeHolder" + lstHolders.size());

                holder.timer.onFinish();

                /*if (mClickListener != null)
                    mClickListener.onItemDelete(view, position,arraylist);
                notifyDataSetChanged();*/
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // stopBeep(holder);
                cancelTimer(holder);
                // holder.timer.onFinish();

                stopCountDown();
                notifyDataSetChanged();

                if (mClickListener != null)
                    mClickListener.onItemClick1(view, position);



            }
        });

        cancelTimer(holder);

        /** Set And Start Timer*/
        setCountDownTimer(holder, position);


    }

    private void setCountDownTimer(ViewHolder holder, int position) {


        String str_date = arraylist.get(position).getTimeStamp();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = (Date) formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long timer = date.getTime();
        Date today = new Date();
        final long currentTime = today.getTime();
        long expiryTime = 0;


        if (currentTime - timer <= 30000) {
            long diff = 30000 - (currentTime - timer);
            expiryTime = diff;

            //set progressbar
            holder.progressBarCircle.setMax((int) diff / 1000);
            holder.progressBarCircle.setProgress((int) diff / 1000);

            startCountDownTimer(holder, expiryTime, position);

        }

    }

    private void startCountDownTimer(final ViewHolder holder, long expiryTime, final int position) {
        //this.isPlayingBeep=true;

        holder.timer = new CountDownTimer(expiryTime, 1000) {


            public void onTick(long millisUntilFinished) {

                long seconds = millisUntilFinished / 1000;

                String time = "" + seconds % 60;
                if (time.length() < 2) {
                    time = "0" + time;
                }

                holder.txtCounter.setText(time);
                holder.progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

                playBeepSound(holder);


            }

            public void onFinish() {
                Log.d(TAG, "OnFinish----->>>");
               // stopBeep(holder);
                holder.txtCounter.setText("Time up!");
                Log.d("NotificationListAdapter", "Position----->>>" + position + " listSize:" + arraylist.size()+" "+holder.holderPosition);
                //Log.d("NotificationListAdapter","Position----->>>"+holder.getPosition()+" destName: "+ holder.txtDest.getText());
                if (!arraylist.isEmpty()) {
                    //Log.d("NotificationListAdapter","FinishPosition----->>>"+ position+" "+arraylist.get(position).getDropLocName());

                    db.deleteNotiRecord(arraylist.get(position).getSrId());
                    arraylist.remove(position);

                    //lstHolders.get(position).timer.cancel();
                    //lstHolders.remove(position);
                    stopCountDown();
                    notifyDataSetChanged();
                }


                if (arraylist.isEmpty()) {

                    stopCountDown();
                    db.deleteAllNotificationData();
                    stopCountDown();
                    notifyDataSetChanged();
                    mcontext.startActivity(new Intent(mcontext, LoginActivity.class));
                    ((Activity) mcontext).finish();
                }

                Log.d(TAG, "New_listSizeHolder" + lstHolders.size());


            }
        }.start();

    }

    public void stopBeep(ViewHolder holder) {

        if (holder.mp.isPlaying()) {
            holder.mp.stop();
            holder.mp.release();
            holder.mp = MediaPlayer.create(mcontext, R.raw.beep_old);
        }

    }

    public void setBooleanStopBeep() {

        //this.isPlayingBeep=false;

    }

    public void stopCountDown() {
        Log.d(TAG,"StopCountDown_listSizeHolderNumber-->>: "+lstHolders.size());

        for(int i=0;i<lstHolders.size();i++){
            if(lstHolders.get(i).timer!=null) {
                Log.d(TAG,"listSizeHolderNumber-->>: "+i);
                    lstHolders.get(i).timer.cancel();
                    //lstHolders.get(i).timer.cle;
                     stopBeep(lstHolders.get(i));
                    //lstHolders.get(i).mp.stop();
                lstHolders.get(i).timer=null;
               // lstHolders.get(i).mp=null;

            }
        }
        Log.d(TAG,"listSizeHolderNumber-->>: Finish Loop");

    }





    private void cancelTimer(ViewHolder holder) {

        if (holder.timer != null) {
            holder.timer.cancel();
        }
    }


    private void playBeepSound(final ViewHolder holder) {


        try {

            if (holder.mp.isPlaying()) {
                holder.mp.stop();
                holder.mp.release();
                holder.mp = MediaPlayer.create(mcontext, R.raw.beep_long);
            }


            holder.mp.start();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {

        return arraylist.size();
    }


    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public void setStopallFuncOfAdpter(StopFuncListener onClickListener) {
        this.mstopClickListener = onClickListener;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtSource, txtDest, txtKm, txtRate, txtCounter, txtsrId;
        Button btnCancel, btnAccept;
        CountDownTimer timer;
        ProgressBar progressBarCircle;
        MediaPlayer mp;
        int holderPosition;



        ViewHolder(View itemView) {
            super(itemView);

            mp = MediaPlayer.create(mcontext, R.raw.beep_old);
            txtSource = itemView.findViewById(R.id.txtsource);
            txtDest = itemView.findViewById(R.id.txtDestination);
            txtKm = itemView.findViewById(R.id.txtKm);
            txtRate = itemView.findViewById(R.id.txtRate);
            txtCounter = itemView.findViewById(R.id.txtCounter);
            txtsrId = itemView.findViewById(R.id.txtsrId);
            progressBarCircle = itemView.findViewById(R.id.progressBarCircle);

            btnCancel = itemView.findViewById(R.id.btn_cancel);
            btnAccept = itemView.findViewById(R.id.btn_accept);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //if (mClickListener != null) mClickListener.onItemClick1(view, getAdapterPosition());
        }


    }


    // convenience method for getting data at click position
    String getItem(int id) {
        return mData[id];
    }

    // allows clicks events to be caught
    public void setClickListener(Context itemClickListener) {
        this.mClickListener = (ItemClickListener) itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick1(View view, int position);

        void onItemDelete(View view, int position, ArrayList<Bean> arrayList1);
    }


    public interface StopFuncListener {

        void onstopListener(View view, int position);
    }


}