package com.jiage.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String cond_code_d;

    public String cond_code_n;

    @SerializedName("cond_txt_d")
    public String weatherday;

    @SerializedName("cond_txt_n")
    public String weathernight;

    public String date;

    //相对湿度
    public String hum;

    //月升时间
    public String mr;

    //月落时间
    public String ms;

    //降水量
    public String pcpn;

    //降水概率
    public String pop;

    //大气压强
    public String pres;

    //日出时间
    public String sr;

    //日落时间
    public String ss;

    //最高温度
    public String tmp_max;

    //最低温度
    public String tmp_min;

    //紫外线强度指数
    public String uv_index;

    //能见度，单位：公里
    public String vis;

    //风向360角度
    public String wind_deg;

    //风向
    public String wind_dir;

    //风力
    public String wind_sc;

    //风速
    public String wind_spd;
}
