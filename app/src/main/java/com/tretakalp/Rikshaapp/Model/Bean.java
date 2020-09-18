package com.tretakalp.Rikshaapp.Model;

/**
 * Created by Mac on 06/09/18.
 */
public class Bean {
    String cityId;
    private String tripDate;
    private String source;
    private String destination;
    private String amount;
    private String time;
    private String driverId;
    private String tripType;
    private String IsPrepaid;

    public String getIsPrepaid() {
        return IsPrepaid;
    }

    public void setIsPrepaid(String isPrepaid) {
        IsPrepaid = isPrepaid;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    long expirationTime;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String getBookingId) {
        this.bookingId = getBookingId;
    }

    public String bookingId;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String query;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getPickupLocName() {
        return pickupLocName;
    }

    public void setPickupLocName(String pickupLocName) {
        this.pickupLocName = pickupLocName;
    }

    public String getEndLocationName() {
        return endLocationName;
    }

    public void setEndLocationName(String endLocationName) {
        this.endLocationName = endLocationName;
    }

    public String getCustMobileNo() {
        return custMobileNo;
    }

    public void setCustMobileNo(String custMobileNo) {
        this.custMobileNo = custMobileNo;
    }

    public String getStartLat() {
        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getStartLong() {
        return startLong;
    }

    public void setStartLong(String startLong) {
        this.startLong = startLong;
    }

    public String getDropLat() {
        return dropLat;
    }

    public void setDropLat(String dropLat) {
        this.dropLat = dropLat;
    }

    public String getDropLong() {
        return dropLong;
    }

    public void setDropLong(String dropLong) {
        this.dropLong = dropLong;
    }

    public String getEndLat() {
        return endLat;
    }

    public void setEndLat(String endLat) {
        this.endLat = endLat;
    }

    public String getEndLong() {
        return endLong;
    }

    public void setEndLong(String endLong) {
        this.endLong = endLong;
    }

    public String getDropLocName() {
        return dropLocName;
    }

    public void setDropLocName(String dropLocName) {
        this.dropLocName = dropLocName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getIsPaymentDone() {
        return isPaymentDone;
    }

    public void setIsPaymentDone(String isPaymentDone) {
        this.isPaymentDone = isPaymentDone;
    }

    public String getIsRideComplete() {
        return isRideComplete;
    }

    public void setIsRideComplete(String isRideComplete) {
        this.isRideComplete = isRideComplete;
    }

    private String sessionId;
    private String duration;
    private String netAmount;
    private String pickupLocName;
    private String endLocationName;
    private String custMobileNo;
    private String startLat;
    private String startLong;
    private String dropLat;
    private String check_tripType;

    public String getCheck_tripType() {
        return check_tripType;
    }

    public void setCheck_tripType(String check_tripType) {
        this.check_tripType = check_tripType;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    private String tripId;
    private String dropLong;
    private String endLat,endLong,dropLocName,totalAmount,totalDistance,isPaymentDone,isRideComplete;


   /* values1.put(DRIVER_ID,b.driverId);
    values1.put(SESSION_ID,longitude);
    values1.put(DURATION,source);
    values1.put(NET_AMOUNT,getDateTime());
    values1.put(TRIP_TYPE,accuracy);
    values1.put(PICKUP_LOCATION,driverid);
    values1.put(END_LOCATION,batterylevel);
    values1.put(CUST_MOBILE_NO,batterylevel);
    values1.put(START_LAT,batterylevel);
    values1.put(START_LONG,batterylevel);
    values1.put(DROP_LAT,batterylevel);
    values1.put(DROP_LONG,batterylevel);
    values1.put(END_LAT,batterylevel);
    values1.put(END_LONG,batterylevel);
    values1.put(DROP_LOCATION_NAME,batterylevel);
    values1.put(TOATAL_AMOUNT,batterylevel);
    values1.put(TOTAL_DISTANCE,batterylevel);
    values1.put(IS_PAYMENT_DONE,batterylevel);
    values1.put(IS_RIDECOMPLETE,batterylevel);*/

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getMinTime() {
        return minTime;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    private String fee;
    private String distance;
    private String minTime;

    public Bean() {

    }

    public Bean(String source, String destination, boolean isTripId, String amount, String time,String fee,String minTime,String distance) {
        this.source =source;
        this.destination =destination;
        this.isTripId =isTripId;
        this.amount=amount;
        this.time=time;
        this.fee=fee;
        this.distance=distance;
        this.minTime=minTime;

    }


    public boolean isTripId() {
        return isTripId;
    }

    public void setTripId(boolean tripId) {
        isTripId = tripId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private  boolean isTripId;


    public void setTripCount(String tripCount) {
        this.tripCount = tripCount;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    private String tripCount;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getRtoCode() {
        return rtoCode;
    }

    public void setRtoCode(String rtoCode) {
        this.rtoCode = rtoCode;
    }

    String cityName;
    String rtoCode;

    public String getPaymentMode() {
        return paymentMode;
    }

    String paymentMode;

    public String getStartRideTime() {
        return startRideTime;
    }

    public String getDropRideTime() {
        return dropRideTime;
    }

    String startRideTime;
    String dropRideTime;

    public String getTimeStamp() {
        return timeStamp;
    }

    String timeStamp;

    public String getTripDate() {


        return tripDate;
    }

    public String getTripCount() {
        return tripCount;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public void setDropRideTime(String dropRideTime) {
        this.dropRideTime = dropRideTime;
    }

    public void setStartRideTime(String startRideTime) {
        this.startRideTime = startRideTime;
    }

    String notificationTitle;

    public String getMessage() {
        return message;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    String message;
    public void setMessage(String message) {
        this.message = message;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public boolean left;
    public String chat_message;

    public Bean(boolean left, String message) {
        super();
        this.left = left;
        this.chat_message = message;
    }

    public Bean(boolean left, String message,String userType) {
        super();
        this.left = left;
        this.chat_message = message;
        this.userType = userType;
    }

    public int getSrId() {
        return srId;
    }

    int srId;
    String reasonId;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getResonId() {
        return reasonId;
    }

    String reason;

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    String notifyId;

    public String getDate_To() {
        return date_To;
    }

    public String getDate_From() {
        return date_From;
    }

    String date_To;
    String date_From;

    public String getImage() {
        return image;
    }

    String image;

    public String getQueryId() {
        return queryId;
    }

    String queryId;
    public void setSrId(int srId) {
        this.srId = srId;
    }

    public void setReasonId(String reasonId) {
        this.reasonId = reasonId;
    }


    public void setNotifyId() {
    }

    public void setDate_To(String date_To) {
        this.date_To = date_To;
    }

    public void setDate_From(String date_From) {
        this.date_From = date_From;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    String answer;

    public String getUserType() {
        return userType;
    }

    public String getAnswer() {
        return answer;
    }

    String userType;

    public String getRideCharge() {
        return rideCharge;
    }

    String rideCharge;

    public String getTripNumber() {
        return tripNumber;
    }

    String tripNumber;

    public String getPayment() {
        return payment;
    }

    String payment;
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setRideCharge(String rideCharge) {
        this.rideCharge = rideCharge;
    }

    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getViewId() {
        return viewId;
    }

    String viewId;
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public String getStatus() {
        return status;
    }

    String status;
    public void setStatus(String status) {
        this.status = status;
    }
}
