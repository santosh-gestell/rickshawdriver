package com.tretakalp.Rikshaapp.Adpter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ExpandableCustAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<Bean> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<Bean, List<Bean>> _listDataChild;
	boolean flag=false;

	public ExpandableCustAdapter(Context context, List<Bean> listDataHeader,
								 HashMap<Bean, List<Bean>> listChildData) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		//final String childText = (String) getChild(groupPosition, childPosition);
		final Bean childText = (Bean) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.trip_list_item, null);
		}

		TextView txtsource = (TextView) convertView.findViewById(R.id.txtsource);
		TextView txtDest = (TextView) convertView.findViewById(R.id.txtDestination);
		TextView txtTime = (TextView) convertView.findViewById(R.id.txtTime);
		TextView txtAmount = (TextView) convertView.findViewById(R.id.txtRate);
		TextView txtTripId = (TextView) convertView.findViewById(R.id.txtTripId);
		ImageView img=convertView.findViewById(R.id.img_tripId);

		txtsource.setText(childText.getPickupLocName());
		if(childText.getPickupLocName().length()>20){
			txtsource.setTextSize(10f);
		}
		txtDest.setText(childText.getDropLocName());
		if(childText.getDropLocName().length()>20){
			txtDest.setTextSize(10f);
		}
		txtTime.setText(childText.getTime());
		txtAmount.setText("\u20B9 "+childText.getNetAmount());
		txtTripId.setText(childText.getTripId()+"");

		//getTotalTime(childText.getStartRideTime(),childText.getDropRideTime(),txtTime);
		//txtTime.setText(childText.getDuration());
		/*if(childText.isTripId()){
			img.setImageDrawable(ContextCompat.getDrawable(_context,R.drawable.trip_check));
		}else {
			img.setImageDrawable(ContextCompat.getDrawable(_context,R.drawable.trip_cancel));
		}*/

		return convertView;
	}

	private void getTotalTime(String startTime, String endTime, TextView txtTime) {

		//DateUtils obj = new DateUtils();
		//SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);

		try {
			Date date1 = simpleDateFormat.parse(startTime);
			Date date2 = simpleDateFormat.parse(endTime);

			printDifference(date1, date2,txtTime);

		} catch (ParseException e) {
			e.printStackTrace();
		}

//1 minute = 60 seconds
//1 hour = 60 x 60 = 3600
//1 day = 3600 x 24 = 86400


	}


	public void printDifference(Date startDate, Date endDate, TextView txtTime) {
		//milliseconds
		long different = endDate.getTime() - startDate.getTime();

		System.out.println("startDate : " + startDate);
		System.out.println("endDate : "+ endDate);
		System.out.println("different : " + different);

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;
		if(elapsedHours==0) {
			txtTime.setText(elapsedMinutes + " min " + elapsedSeconds + "sec");
		}else {
			txtTime.setText(elapsedHours + " hour " + elapsedMinutes + " min");
		}

		System.out.printf(
				"%d days, %d hours, %d minutes, %d seconds%n",
				elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		Bean headerTitle = (Bean) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.trip_row, null);
		}

		TextView lblListHeader = (TextView) convertView.findViewById(R.id.txt_type);
		TextView lblListHeader1 = (TextView) convertView.findViewById(R.id.txtDate1);
		TextView lbltxtMonth = (TextView) convertView.findViewById(R.id.txtMonth);
		final ImageView img = convertView.findViewById(R.id.img_arrow);
		//lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle.getTripCount());

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date date=null;
		try {
			 date = dateFormat.parse(headerTitle.getTripDate());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Log.d("dateEnglish",date+"");
		String day  = (String) DateFormat.format("dd",  date); // 20
		Log.d("dayEnglish",day+"");
		String monthString  = (String) DateFormat.format("MMM",  date); // Jun
		lblListHeader1.setText(day+"");
		lbltxtMonth.setText(monthString+"");


		/*convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//if()
				//img.setImageDrawable(ContextCompat.getDrawable(_context,R.mipmap.spinner_up));

			}
		});*/

		if (!isExpanded) {
			img.setImageDrawable(ContextCompat.getDrawable(_context,R.mipmap.spinner_arrow));
		} else {
			img.setImageDrawable(ContextCompat.getDrawable(_context,R.mipmap.spinner_up));
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
