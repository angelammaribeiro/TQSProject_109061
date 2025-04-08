package ua.deti.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
    @JsonProperty("location")
    private Location location;

    @JsonProperty("current")
    private Current current;

    private String forecast;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        @JsonProperty("name")
        private String name;

        @JsonProperty("region")
        private String region;

        @JsonProperty("country")
        private String country;

        @JsonProperty("lat")
        private double lat;

        @JsonProperty("lon")
        private double lon;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        @JsonProperty("temp_c")
        private double tempC;

        @JsonProperty("temp_f")
        private double tempF;

        @JsonProperty("feelslike_c")
        private double feelsLikeC;

        @JsonProperty("feelslike_f")
        private double feelsLikeF;

        @JsonProperty("condition")
        private Condition condition;

        @JsonProperty("wind_kph")
        private double windKph;

        @JsonProperty("wind_degree")
        private int windDegree;

        @JsonProperty("humidity")
        private int humidity;

        public double getTempC() {
            return tempC;
        }

        public void setTempC(double tempC) {
            this.tempC = tempC;
        }

        public double getTempF() {
            return tempF;
        }

        public void setTempF(double tempF) {
            this.tempF = tempF;
        }

        public double getFeelsLikeC() {
            return feelsLikeC;
        }

        public void setFeelsLikeC(double feelsLikeC) {
            this.feelsLikeC = feelsLikeC;
        }

        public double getFeelsLikeF() {
            return feelsLikeF;
        }

        public void setFeelsLikeF(double feelsLikeF) {
            this.feelsLikeF = feelsLikeF;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

        public double getWindKph() {
            return windKph;
        }

        public void setWindKph(double windKph) {
            this.windKph = windKph;
        }

        public int getWindDegree() {
            return windDegree;
        }

        public void setWindDegree(int windDegree) {
            this.windDegree = windDegree;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Condition {
        @JsonProperty("text")
        private String text;

        @JsonProperty("icon")
        private String icon;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
} 