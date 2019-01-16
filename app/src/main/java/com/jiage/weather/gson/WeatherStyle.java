package com.jiage.weather.gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherStyle {
    public Basic basic;
    public update update;
    public String status;
    @SerializedName("lifestyle")
    public List<Lifestyle> Weatherlifestyle;
}
