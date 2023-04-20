package by.bashlikovv.chat.sources.rooms

import by.bashlikovv.chat.sources.HttpContract
import by.bashlikovv.chat.sources.base.BaseOkHttpSource
import by.bashlikovv.chat.sources.base.OkHttpConfig
import by.bashlikovv.chat.sources.rooms.entities.*
import by.bashlikovv.chat.sources.structs.Room
import okhttp3.Request

class OkHttpRoomsSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config) {

    suspend fun getRooms(user: String): List<Room> {
        val getRoomsRequestBody = GetRoomsRequestBody(user = user)
        val request = Request.Builder()
            .post(getRoomsRequestBody.toJsonRequestBody())
            .endpoint("/${HttpContract.UrlMethods.GET_ROOMS}")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<GetRoomsResponseBody>().rooms
    }

    suspend fun addRoom(user1: String, user2: String): String {
        val getRoomsRequestBody = AddRoomRequestBody(user1, user2)
        val request = Request.Builder()
            .post(getRoomsRequestBody.toJsonRequestBody())
            .endpoint("/${HttpContract.UrlMethods.ADD_ROOM}")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<AddRoomResponseBody>().token
    }

    suspend fun deleteRoom(user1: String, user2: String): String {
        val deleteRoomRequestBody = DeleteRoomRequestBody(user1, user2)
        val request = Request.Builder()
            .post(deleteRoomRequestBody.toJsonRequestBody())
            .endpoint("/${HttpContract.UrlMethods.DELETE_ROOM}")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<DeleteRoomResponseBody>().result
    }

    suspend fun getRoom(user1: String, user2: String): Room {
        val getRoomRequestBody = GetRoomRequestBody(user1, user2)
        val request = Request.Builder()
            .post(getRoomRequestBody.toJsonRequestBody())
            .endpoint("/${HttpContract.UrlMethods.GET_ROOM}")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<GetRoomResponseBody>().room
    }
}