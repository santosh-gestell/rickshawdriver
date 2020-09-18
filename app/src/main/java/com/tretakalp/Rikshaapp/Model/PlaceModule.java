package com.tretakalp.Rikshaapp.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceModule {

    private String id;
    private String Lat;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String Long;
    private String placeName;



    public PlaceModule(JSONObject object2) {
        try {
            this.id = object2.getString("id");
            this.Lat = object2.getString("lat");
            this.Long = object2.getString("long");
            this.placeName = object2.getString("loc_name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
