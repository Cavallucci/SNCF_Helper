import com.google.gson.annotations.SerializedName


data class Main (
    @SerializedName("feed_publishers") val feed_publishers : List<Feed_publishers>,
    @SerializedName("links") val links : List<Links>,
    @SerializedName("journeys") val journeys : List<Journeys>
)