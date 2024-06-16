package com.dicoding.prodswing.di

import android.content.Context
import com.dicoding.prodswing.data.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        return UserRepository.getInstance(context)
    }
}