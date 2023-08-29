package by.bashlikovv.messenger.data.remote.request

data class UpdateUsernameRequestBody(
    val token: String,
    val newName: String
)