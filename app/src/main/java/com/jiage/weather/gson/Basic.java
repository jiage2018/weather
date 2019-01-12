package com.jiage.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {

    @SerializedName("cid")
    public String weatherId;
    @SerializedName("location")
    public String cityName;
    public String parent_city;
    public String admin_area;
    public String cnty;
    public String lat;
    public String lon;
    public String tz;
}
