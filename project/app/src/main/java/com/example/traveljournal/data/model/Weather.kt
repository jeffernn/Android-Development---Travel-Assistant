package com.example.traveljournal.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val code: Int,
    val message: String,
    val data: WeatherData
)

@JsonClass(generateAdapter = true)
data class WeatherData(
    val location: Location,
    @Json(name = "hourly_forecast")
    val hourlyForecast: List<HourlyForecast>,
    @Json(name = "daily_forecast")
    val dailyForecast: List<DailyForecast>,
    @Json(name = "sunrise_sunset")
    val sunriseSunset: List<SunriseSunset>
)

@JsonClass(generateAdapter = true)
data class Location(
    val name: String,
    val province: String,
    val city: String,
    val county: String
)

@JsonClass(generateAdapter = true)
data class HourlyForecast(
    @Json(name = "datetime")
    val datetime: String,
    val temperature: Int,
    val condition: String,
    @Json(name = "condition_code")
    val conditionCode: String,
    @Json(name = "wind_direction")
    val windDirection: String,
    @Json(name = "wind_power")
    val windPower: String,
    @Json(name = "weather_icon")
    val weatherIcon: String
)

@JsonClass(generateAdapter = true)
data class DailyForecast(
    val date: String,
    @Json(name = "day_condition")
    val dayCondition: String,
    @Json(name = "day_condition_code")
    val dayConditionCode: String,
    @Json(name = "night_condition")
    val nightCondition: String,
    @Json(name = "night_condition_code")
    val nightConditionCode: String,
    @Json(name = "max_temperature")
    val maxTemperature: Int,
    @Json(name = "min_temperature")
    val minTemperature: Int,
    @Json(name = "day_wind_direction")
    val dayWindDirection: String,
    @Json(name = "day_wind_power")
    val dayWindPower: String,
    @Json(name = "night_wind_direction")
    val nightWindDirection: String,
    @Json(name = "night_wind_power")
    val nightWindPower: String,
    val aqi: Int,
    @Json(name = "aqi_level")
    val aqiLevel: Int,
    @Json(name = "air_quality")
    val airQuality: String,
    @Json(name = "day_weather_icon")
    val dayWeatherIcon: String,
    @Json(name = "night_weather_icon")
    val nightWeatherIcon: String
)

@JsonClass(generateAdapter = true)
data class SunriseSunset(
    val sunrise: String,
    @Json(name = "sunrise_at")
    val sunriseAt: Long,
    @Json(name = "sunrise_desc")
    val sunriseDesc: String,
    val sunset: String,
    @Json(name = "sunset_at")
    val sunsetAt: Long,
    @Json(name = "sunset_desc")
    val sunsetDesc: String
)