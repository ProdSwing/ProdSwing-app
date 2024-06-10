package com.c241ps447.prodswing.di

import android.content.Context
import com.c241ps447.prodswing.data.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        return UserRepository.getInstance(context)
    }
}