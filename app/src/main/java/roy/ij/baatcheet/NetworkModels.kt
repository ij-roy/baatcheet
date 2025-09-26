package roy.ij.baatcheet

// NetworkModels.kt
import com.google.gson.annotations.SerializedName

// This class represents the data we send TO the server
data class WriterRequest(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("initial_prompt")
    val initialPrompt: String,

    @SerializedName("target_model")
    val targetModel: String,

    @SerializedName("task_type")
    val taskType: String
)

// This class represents the data we receive FROM the server
data class WriterResponse(
    @SerializedName("rewritten_prompt")
    val rewrittenPrompt: String,

    @SerializedName("applied_techniques")
    val appliedTechniques: List<String>,

    @SerializedName("request_id")
    val requestId: String
)