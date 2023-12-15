/* 
Copyright (c) 2023 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */
import com.google.gson.annotations.SerializedName


data class Journeys (

	@SerializedName("duration") val duration : Int,
	@SerializedName("nb_transfers") val nb_transfers : Int,
	@SerializedName("departure_date_time") val departure_date_time : String,
	@SerializedName("arrival_date_time") val arrival_date_time : String,
	@SerializedName("requested_date_time") val requested_date_time : String,
	@SerializedName("type") val type : String,
	@SerializedName("status") val status : String,
	@SerializedName("tags") val tags : List<String>,
	@SerializedName("co2_emission") val co2_emission : Co2_emission,
	@SerializedName("air_pollutants") val air_pollutants : Air_pollutants,
	@SerializedName("durations") val durations : Durations,
	@SerializedName("distances") val distances : Distances,
	@SerializedName("fare") val fare : Fare,
	@SerializedName("calendars") val calendars : List<Calendars>,
	@SerializedName("sections") val sections : List<Sections>,
	@SerializedName("links") val links : List<Links>
)