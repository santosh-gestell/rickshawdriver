package com.tretakalp.Rikshaapp.Adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.R;

import java.util.ArrayList;

/**
 * Created by Mac on 19/12/17.
 */

public class ReportIssueAdapter extends RecyclerView.Adapter<ReportIssueAdapter.ViewHolder> {

    private String[] mData = new String[0];
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ArrayList<Bean> arraylist=new ArrayList<>();

    View view;
    private Context mcontext;
    String companyString="";


    public ReportIssueAdapter(Context context, ArrayList<Bean> listLabel,ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.arraylist=listLabel;
        this.mcontext=context;
        this.mClickListener =itemClickListener;

    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

           view = mInflater.inflate(R.layout.query_row, parent, false);


        //View view = mInflater.inflate(R.layout.table_listrow, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

                holder.txtQuery.setText(arraylist.get(position).getQuery());
                holder.txtQueryID.setText(mcontext.getResources().getString(R.string.queryNo)+""+arraylist.get(position).getQueryId());
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
        TextView txtQuery;
        TextView txtQueryID;

        ViewHolder(View itemView) {
            super(itemView);

           // myTextView = (TextView) itemView.findViewById(R.id.txtsrid);
            txtQuery = (TextView) itemView.findViewById(R.id.txt_query);
            txtQueryID = (TextView) itemView.findViewById(R.id.txt_queryId);

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