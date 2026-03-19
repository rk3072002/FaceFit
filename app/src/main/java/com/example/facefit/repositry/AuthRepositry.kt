package com.example.facefit.repositry

import com.example.facefit.model.AuthState
import com.example.facefit.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//Data source
class AuthRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
    fun loginUser( email: String, password: String, callback: (AuthState) -> Unit ) {
        callback(AuthState.Loading)
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                callback(AuthState.Success)
            } .addOnFailureListener {
                exception -> callback(AuthState.Error(exception.message ?: "Login Failed"))
            }
    }

    fun registerUser(name: String, email: String, password: String, callback: (AuthState) -> Unit) {

        callback(AuthState.Loading)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val user = User( name = name, email = email)
                firestore.collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener {
                        callback(AuthState.Success)
                    }
            } .addOnFailureListener {
                callback(AuthState.Error("User created but data save failed"))
            }
            .addOnFailureListener { exception ->
                callback(AuthState.Error(exception.message ?: "Signup Failed"))
            }
    }
    fun logout() {
        firebaseAuth.signOut()
    }
}