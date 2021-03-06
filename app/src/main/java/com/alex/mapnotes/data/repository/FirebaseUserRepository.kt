package com.alex.mapnotes.data.repository

import com.alex.mapnotes.AppExecutors
import com.alex.mapnotes.data.Result
import com.alex.mapnotes.data.exception.AccountNotCreatedException
import com.alex.mapnotes.data.exception.UserNotAuthenticatedException
import com.alex.mapnotes.model.AuthUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume

class FirebaseUserRepository(private val appExecutors: AppExecutors) : UserRepository {
    private val usersPath = "users"
    private val nameKey = "name"
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override suspend fun signIn(email: String, password: String): Result<AuthUser> = withContext(appExecutors.networkContext) {
        suspendCoroutine<Result<AuthUser>> {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { authResultTask ->
                if (authResultTask.isSuccessful) {
                    it.resume(Result.Success(AuthUser(authResultTask.result?.user?.uid!!)))
                } else {
                    it.resume(Result.Error(UserNotAuthenticatedException()))
                }
            }
        }
    }

    override suspend fun signUp(email: String, password: String): Result<AuthUser> = withContext(appExecutors.networkContext) {
        suspendCoroutine<Result<AuthUser>> {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { authResultTask ->
                if (authResultTask.isSuccessful) {
                    it.resume(Result.Success(AuthUser(authResultTask.result?.user?.uid!!)))
                } else {
                    it.resume(Result.Error(AccountNotCreatedException()))
                }
            }
        }
    }

    override suspend fun signOut() = withContext(appExecutors.networkContext) {
        auth.signOut()
    }

    override suspend fun getCurrentUser(): Result<AuthUser> = withContext(appExecutors.networkContext) {
        val user = auth.currentUser
        if (user != null) {
            return@withContext Result.Success(AuthUser(user.uid))
        } else {
            return@withContext Result.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun changeUserName(user: AuthUser, name: String) {
        withContext(appExecutors.networkContext) {
            val usersRef = database.getReference(usersPath)
            usersRef.child(user.uid).setValue(hashMapOf(nameKey to name))
        }
    }

    override suspend fun getHumanReadableName(userId: String): Result<String> = withContext(appExecutors.networkContext) {
        suspendCoroutine<Result<String>> {
            database.getReference(usersPath).child(userId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        it.resume(Result.Success(dataSnapshot.children.first().value.toString()))
                    }
                }
            })
        }
    }

    override suspend fun getUserIdFromHumanReadableName(userName: String): Result<String> = withContext(appExecutors.networkContext) {
        suspendCoroutine<Result<String>> { continuation ->
            database.getReference(usersPath)
                    .orderByChild(nameKey)
                    .equalTo(userName)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                continuation.resume(Result.Success(dataSnapshot.children.first().key.toString()))
                            }
                        }
                    })
        }
    }
}