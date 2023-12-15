package com.example.journeys

data class Journeys(
    val feedPublishers: List<FeedPublisher>,
    val links: List<Link>,
    val journeys: List<Journey>,
    val tickets: List<Any>,
    val disruptions: List<Any>,
    val terminus: List<Terminus>,
    val context: Context,
    val notes: List<Any>,
    val exceptions: List<Any>
)

data class FeedPublisher(
    val id: String,
    val name: String,
    val url: String,
    val license: String
)

data class Link(
    val href: String,
    val templated: Boolean,
    val rel: String,
    val type: String
)

data class Calendar(
    val weekPattern: WeekPattern,
    val activePeriods: List<ActivePeriod>
)

data class WeekPattern(
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean
)

data class ActivePeriod(
    val begin: String,
    val end: String
)

data class Section(
    val id: String,
    val duration: Int,
    val co2Emission: Co2Emission,
    val departureDateTime: String,
    val arrivalDateTime: String,
    val to: To,
    val from: From,
    val additionalInformations: List<String>,
    val geojson: Geojson,
    val type: String,
    val displayInformations: DisplayInformations,
    val links: List<Link>,
    val stopDateTimes: List<StopDateTime>
)

data class Co2Emission(
    val value: Double,
    val unit: String
)

data class To(
    val id: String,
    val name: String,
    val quality: Int,
    val stopPoint: StopPoint,
    val embeddedType: String
)

data class From(
    val id: String,
    val name: String,
    val quality: Int,
    val stopPoint: StopPoint,
    val embeddedType: String
)

data class StopPoint(
    val id: String,
    val name: String,
    val label: String,
    val coord: Coord,
    val links: List<Any>,
    val equipments: List<Any>
)

data class Geojson(
    val type: String,
    val coordinates: List<List<Double>>,
    val properties: List<Property>
)

data class Property(
    val length: Int
)

data class DisplayInformations(
    val commercialMode: String,
    val network: String,
    val direction: String,
    val label: String,
    val color: String,
    val code: String,
    val name: String,
    val links: List<Link>,
    val textColor: String,
    val description: String,
    val physicalMode: String,
    val equipments: List<Any>,
    val headsign: String,
    val tripShortName: String
)

data class StopDateTime(
    val departureDateTime: String,
    val baseDepartureDateTime: String,
    val arrivalDateTime: String,
    val baseArrivalDateTime: String,
    val stopPoint: StopPoint,
    val additionalInformations: List<Any>,
    val links: List<Any>
)

data class Journey(
    val duration: Int,
    val nbTransfers: Int,
    val departureDateTime: String,
    val arrivalDateTime: String,
    val requestedDateTime: String,
    val type: String,
    val status: String,
    val tags: List<String>,
    val co2Emission: Co2Emission,
    val airPollutants: AirPollutants,
    val durations: Durations,
    val distances: Distances,
    val fare: Fare,
    val calendars: List<Calendar>,
    val sections: List<Section>,
    val links: List<Link>
)

data class AirPollutants(
    val unit: String,
    val values: Values
)

data class Values(
    val nox: Double,
    val pm: Double
)

data class Durations(
    val total: Int,
    val walking: Int,
    val bike: Int,
    val car: Int,
    val ridesharing: Int,
    val taxi: Int
)

data class Distances(
    val walking: Int,
    val bike: Int,
    val car: Int,
    val ridesharing: Int,
    val taxi: Int
)

data class Fare(
    val found: Boolean,
    val total: Total,
    val links: List<Any>
)

data class Total(
    val value: String
)

data class PropertyX(
    val length: Int
)

data class LinkX(
    val href: String,
    val templated: Boolean,
    val rel: String,
    val type: String
)

data class VehicleJourney(
    val id: String,
    val type: String
)

data class PhysicalMode(
    val id: String,
    val type: String
)

data class StopPoints(
    val id: String,
    val type: String
)

data class Routes(
    val id: String,
    val type: String
)

data class CommercialMode(
    val id: String,
    val type: String
)

data class Networks(
    val id: String,
    val type: String
)

data class StopAreas(
    val id: String,
    val type: String
)

data class AirPollutantsX(
    val unit: String,
    val values: ValuesX
)

data class ValuesX(
    val nox: Double,
    val pm: Double
)

data class ValuesXX(
    val nox: Double,
    val pm: Double
)

data class Terminus(
    val id: String,
    val name: String,
    val codes: List<Code>,
    val timezone: String,
    val label: String,
    val coord: Coord,
    val links: List<Any>
)

data class Code(
    val type: String,
    val value: String
)

data class Coord(
    val lon: String,
    val lat: String
)

data class Context(
    val carDirectPath: CarDirectPath,
    val currentDatetime: String,
    val timezone: String
)

data class CarDirectPath(
    val co2Emission: Co2EmissionX,
    val airPollutants: AirPollutantsXX
)

data class Co2EmissionX(
    val value: Double,
    val unit: String
)

data class AirPollutantsXX(
    val unit: String,
    val values: ValuesXX
)