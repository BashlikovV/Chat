package by.bashlikovv.messenger.data.remote

import by.bashlikovv.messenger.data.remote.base.BaseOkHttpSource
import by.bashlikovv.messenger.data.remote.base.OkHttpConfig
import by.bashlikovv.messenger.data.remote.contract.HttpContract
import by.bashlikovv.messenger.data.remote.model.ServerRoom
import by.bashlikovv.messenger.data.remote.request.AddRoomRequestBody
import by.bashlikovv.messenger.data.remote.request.DeleteRoomRequestBody
import by.bashlikovv.messenger.data.remote.request.GetRoomRequestBody
import by.bashlikovv.messenger.data.remote.request.GetRoomsRequestBody
import by.bashlikovv.messenger.data.remote.response.AddRoomResponseBody
import by.bashlikovv.messenger.data.remote.response.DeleteRoomResponseBody
import by.bashlikovv.messenger.data.remote.response.GetRoomResponseBody
import by.bashlikovv.messenger.data.remote.response.GetRoomsResponseBody
import okhttp3.Request

class OkHttpRoomsSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config) {

    suspend fun getRooms(user: String): List<ServerRoom> {
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

    suspend fun getRoom(user1: String, user2: String): ServerRoom {
        val getRoomRequestBody = GetRoomRequestBody(user1, user2)
        val request = Request.Builder()
            .post(getRoomRequestBody.toJsonRequestBody())
            .endpoint("/${HttpContract.UrlMethods.GET_ROOM}")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<GetRoomResponseBody>().room
    }
}