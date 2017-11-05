package com.example.fernandomdelima.referenciacruzada;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Fernando M. de Lima on 10/23/2017.
 */

public class Encounter {

    public String userMail;
    public String groupId;
    public String lat;
    public String lon;
    public String specie;
    public Timestamp timestamp;
    public String timeZone;
    public String timeZoneId;
    public String cityName;
    //public Map<String, Boolean> stars = new HashMap<>();

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Encounter() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        TimeZone tz = TimeZone.getDefault();
        timeZone = tz.getDisplayName(false, TimeZone.SHORT);
        timeZoneId = tz.getID();
    }

    public Encounter(String userMail, String groupId, String lat, String lon, String specie) {
        this.userMail = userMail;
        this.groupId = groupId;
        this.lat = lat;
        this.lon = lon;
        this.specie = specie;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userMail", userMail);
        result.put("groupId", groupId);
        result.put("lat", lat);
        result.put("lon", lon);
        result.put("specie", specie);
        result.put("timestamp", timestamp);
        result.put("timeZone", timeZone);
        result.put("timeZoneId", timeZoneId);
        result.put("cityName", cityName);
        return result;
    }

    @Override
    public String toString() {
        return "{\"cityName\":\"" + cityName + "\", \"timeZone\":\"" + timeZone + "\", \"timeZoneId\":\"" + timeZoneId + "\", \"timestamp\":\"" + timestamp.getTime() + "\", \"lon\":\"" + lon + "\", \"specie\":\"" + specie + "\", \"userMail\":\"" + userMail + "\", " + "\"groupId\":\"" + groupId + "\", \"lat\":\"" + lat + "\"}";
    }
}
