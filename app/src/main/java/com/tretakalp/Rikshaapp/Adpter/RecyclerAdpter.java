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
 * Created by Mac on 20/03/17.
 */
public class RecyclerAdpter extends RecyclerView.Adapter<RecyclerAdpter.ViewHolder> implements View.OnClickListener {

    String[] SubjectValues;
    Context context;
    View view1;
    ViewHolder viewHolder1;
    TextView textView;
    int lastPosition = -1;
    private ArrayList<Bean> arrayList;
    String type;
    int srId=0;

    public RecyclerAdpter(Context context1, String[] SubjectValues1){

        SubjectValues = SubjectValues1;
        context = context1;

    }

    public RecyclerAdpter(Context context, ArrayList<Bean> movieList, String type) {
        this.context = context;
        this.arrayList=movieList;
        this.type=type;

    }

    @Override
    public void onClick(View v) {
        //viewHolder1.textView.setTextColor(Color.BLUE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(type.equals("site")) {
            view1 = LayoutInflater.from(context).inflate(R.layout.trip_row, parent, false);
        }else {
           // view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_items, parent, false);
        }
        viewHolder1 = new ViewHolder( view1);
        view1.setTag(viewHolder1);


        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ///holder.textDate.setText(expenseList.get(position).getDate());
        srId=srId+1;



            holder.textDate.setText(arrayList.get(position).getTripDate()+"");
            //holder.textDate.setText(srId+"");
            holder.textType.setText(arrayList.get(position).getTripCount());
           // holder.textAmount.setText(Constant.formateDateToDDMMYY_HHMMSS(expenseList.get(position).getTimeStamp()));






    }

    @Override
    public int getItemCount(){

        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textDate,textType,textAmount;

        public ViewHolder(View v){

            super(v);

            textDate = (TextView)v.findViewById(R.id.txtDate1);
            textType = (TextView)v.findViewById(R.id.txt_type);
           // textAmount = (TextView)v.findViewById(R.id.txt_amount);
        }
    }
   /* @Override
    public int getItemViewType(int position)
    {
        return position;
    }*/
}
