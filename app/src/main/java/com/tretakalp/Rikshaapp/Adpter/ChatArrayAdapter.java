package com.tretakalp.Rikshaapp.Adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mac on 12/10/18.
 */
public class ChatArrayAdapter extends ArrayAdapter<Bean> {

    private TextView chatText;
    private List<Bean> chatMessageList = new ArrayList<Bean>();
    private Context context;

    public ChatArrayAdapter(Context applicationContext, int right_chat_row, ArrayList<Bean> queryList) {
        super(applicationContext,right_chat_row);

    }

    @Override
    public void add(Bean object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public Bean getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Bean chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.right_chat_row, parent, false);
        }else{
            row = inflater.inflate(R.layout.left_chat_row, parent, false);
        }


        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setText(chatMessageObj.chat_message);
        return row;
    }
}