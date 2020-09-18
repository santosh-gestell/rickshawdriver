package com.tretakalp.Rikshaapp.Adpter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Mac on 19/12/17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private String[] mData = new String[0];
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ArrayList<Bean> arraylist=new ArrayList<>();

    View view;
    private Context mcontext;
    String companyString="";
    String encodedImage="";


    public NotificationAdapter(Context context, ArrayList<Bean> listLabel, ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.arraylist=listLabel;
        this.mcontext=context;
        this.mClickListener =itemClickListener;

    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = mInflater.inflate(R.layout.incentive_notification_row, parent, false);
        //View view = mInflater.inflate(R.layout.table_listrow, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

       holder.txtTitle.setText(arraylist.get(position).getNotificationTitle());
      // holder.txtTitle.setText("545454");
       holder.txtNoti.setText(arraylist.get(position).getMessage());
        Log.d("NotificaitionIncentive","ImageUrl:"+arraylist.get(position).getImage()+"");
        Glide.with(mcontext)
                .asBitmap()
                .load(arraylist.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)

                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.img_noti.setImageBitmap(resource);
                        encodeBitmapImage(resource);
                    }

                });

    }

    private void encodeBitmapImage(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byte_arr = stream.toByteArray();

        /** Encode Image to String.. to send server */
        encodedImage = Base64.encodeToString(byte_arr, 0);

    }



    @Override
    public int getItemCount(){

        return arraylist.size();
    }


    @Override
    public int getItemViewType(int position) {

        return position;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTitle,txtNoti;
        ImageView img_noti;


        ViewHolder(View itemView) {
            super(itemView);


            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtNoti=  itemView.findViewById(R.id.txtNofication);
            img_noti=  itemView.findViewById(R.id.img_noti);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick1(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData[id];
    }

    // allows clicks events to be caught
   /* public void setClickListener(Context itemClickListener) {
        this.mClickListener = (ItemClickListener) itemClickListener;
    }*/

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick1(View view, int position);
    }
}