package com.jiage.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public Basic basic;
    public update update;
    public String status;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
