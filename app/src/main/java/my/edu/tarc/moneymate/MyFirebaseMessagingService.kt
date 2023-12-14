package my.edu.tarc.moneymate

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val userRef = FirebaseFirestore.getInstance().collection("users").document(it)
            userRef.update("fcmToken", token)
                .addOnSuccessListener {
                    Log.d("FCM Token", "Token updated in Firestore")
                }
                .addOnFailureListener {
                    Log.e("FCM Token", "Error updating token", it)
                }
        }
    }
}