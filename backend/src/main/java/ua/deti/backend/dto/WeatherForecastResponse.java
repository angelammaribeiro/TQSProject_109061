package ua.deti.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherForecastResponse {
    private Forecast forecast;

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Forecast {
        private List<ForecastDay> forecastday;

        public List<ForecastDay> getForecastday() {
            return forecastday;
        }

        public void setForecastday(List<ForecastDay> forecastday) {
            this.forecastday = forecastday;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastDay {
        private Day day;

        public Day getDay() {
            return day;
        }

        public void setDay(Day day) {
            this.day = day;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Day {
        private double maxtemp_c;
        private double mintemp_c;
        private double avghumidity;
        private double daily_chance_of_rain;

        public double getMaxtemp_c() {
            return maxtemp_c;
        }

        public void setMaxtemp_c(double maxtemp_c) {
            this.maxtemp_c = maxtemp_c;
        }

        public double getMintemp_c() {
            return mintemp_c;
        }

        public void setMintemp_c(double mintemp_c) {
            this.mintemp_c = mintemp_c;
        }

        public double getAvghumidity() {
            return avghumidity;
        }

        public void setAvghumidity(double avghumidity) {
            this.avghumidity = avghumidity;
        }

        public double getDaily_chance_of_rain() {
            return daily_chance_of_rain;
        }

        public void setDaily_chance_of_rain(double daily_chance_of_rain) {
            this.daily_chance_of_rain = daily_chance_of_rain;
        }
    }
} 