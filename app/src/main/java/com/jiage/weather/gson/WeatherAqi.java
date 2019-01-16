package com.jiage.weather.gson;

import com.google.gson.annotations.SerializedName;

public class WeatherAqi {
    public Basic basic;
    public update update;
    public String status;
    @SerializedName("air_now_city")
    public Aqi weatheraqi;
}
/*
{
"HeWeather6":[
{
"basic":{"cid":"CN101200801","location":"荆州","parent_city":"荆州","admin_area":"湖北","cnty":"中国","lat":"30.32685661","lon":"112.23812866","tz":"+8.00"},
"update":{"loc":"2019-01-16 08:05","utc":"2019-01-16 00:05"},
"status":"ok",
"air_now_city":{"aqi":"87","qlty":"良","main":"PM10","pm25":"42","pm10":"123","no2":"45","so2":"14","co":"1.3","o3":"9","pub_time":"2019-01-16 08:00"},

"air_now_station":[
{"air_sta":"市图书馆","aqi":"84","asid":"CNA1844","co":"1.2","lat":"30.3175","lon":"112.2551","main":"PM10","no2":"41","o3":"12","pm10":"118","pm25":"36","pub_time":"2019-01-16 08:00","qlty":"良","so2":"16"},
{"air_sta":"市委党校","aqi":"89","asid":"CNA1845","co":"1.3","lat":"30.3515","lon":"112.2068","main":"PM10","no2":"49","o3":"5","pm10":"128","pm25":"47","pub_time":"2019-01-16 08:00","qlty":"良","so2":"12"}]}]}
 */