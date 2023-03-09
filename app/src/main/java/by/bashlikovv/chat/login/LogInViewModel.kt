package by.bashlikovv.chat.login

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import by.bashlikovv.chat.model.LogInUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class LogInViewModel : ViewModel() {

    private val _logInUiState = MutableStateFlow(LogInUiState())
    val logInUiState = _logInUiState.asStateFlow()

    private lateinit var messengerDatabase: SQLiteDatabase

    fun applyMessengerDatabase(database: SQLiteDatabase) {
        messengerDatabase = database
        messengerDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS user (name VARCHAR(200), email VARCHAR(200), picFileName VARCHAR(100), picData VARBINARY)"
        )
    }

//    fun saveUserToDatabase(userName: String, email: String, picFileName: String, picData: Bitmap) {
//        messengerDatabase
//    }

    fun onIdentifierChange(newValue: String) {
        _logInUiState.update { it.copy(identifier = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _logInUiState.update { it.copy(password = newValue) }
    }

    fun onUserNameChange(newValue: String) {
        _logInUiState.update { it.copy(userName = newValue) }
    }

    fun applySuccess() {
        _logInUiState.update { it.copy(isSuccess = true) }
    }

    fun onCheckInput(context: Context, haveAccount: Boolean) {
        val email = _logInUiState.value.identifier
        val password = _logInUiState.value.password
        try {
            if (haveAccount) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _logInUiState.update {
                                it.copy(isPasswordCorrect = true, isIdentifierCorrect = true, isSuccess = true)
                            }
                        } else {
                            Toast
                                .makeText(context, task.exception?.message ?: "Authentication error.", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            _logInUiState.update { it.copy(isPasswordCorrect = false, isIdentifierCorrect = false) }
                            return@addOnCompleteListener
                        }
                        val user = FirebaseAuth.getInstance().currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                        profileUpdates.setDisplayName(_logInUiState.value.userName)
                        profileUpdates.setPhotoUri(_logInUiState.value.userImageBitmap.userImageUri)
                        user!!.updateProfile(profileUpdates.build())
                        //
                        saveUserToDatabase(_logInUiState.value.userImageBitmap.userImageUrl)
                        //
                        Toast
                            .makeText(context, "Success. New user: ${task.result.user?.displayName}", Toast.LENGTH_LONG)
                            .show()
                        _logInUiState.update {
                            it.copy(isPasswordCorrect = true, isIdentifierCorrect = true, isSuccess = true)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                    }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Please, fill this fields", Toast.LENGTH_SHORT).show()
            _logInUiState.update { it.copy(isPasswordCorrect = false, isIdentifierCorrect = false) }
        }
    }

    fun onButtonClk(newValue: Boolean) {
        _logInUiState.update { it.copy(isHaveAccount = newValue) }
    }

    fun selectImage(activity: ComponentActivity) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        activity.startActivityForResult(intent, 0)
    }

    fun applyUserImageUri(newValue: Uri) {
        val value = _logInUiState.value.userImageBitmap.copy(userImageUri = newValue)
        _logInUiState.update { it.copy(userImageBitmap = value) }
    }

    fun applyUserBitmapImage(bitmap: ImageBitmap) {
        val fileName = UUID.randomUUID()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")
        val value = _logInUiState.value.userImageBitmap.copy(
            userImageBitmap = bitmap.asAndroidBitmap(), userImageUrl = ref.downloadUrl.toString()
        )
        ref.putFile(_logInUiState.value.userImageBitmap.userImageUri)
        _logInUiState.update { it.copy(userImageBitmap = value) }
    }

    private fun saveUserToDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(
            email = _logInUiState.value.identifier,
            userName = _logInUiState.value.userName,
            imageUri = profileImageUrl,
            ref = ref
        )

//        ref.setValue(user)
    }

    fun setProgressBarVisibility(newValue: Boolean) {
        _logInUiState.update { it.copy(progressBarVisibility = newValue) }
    }
}

class User(
    val email: String,
    val imageUri: String,
    val userName: String,
    val ref: DatabaseReference
)