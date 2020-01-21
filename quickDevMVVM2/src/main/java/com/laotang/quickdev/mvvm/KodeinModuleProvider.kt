package com.laotang.quickdev.mvvm

import org.kodein.di.Kodein

interface KodeinModuleProvider {
    fun providerModule(): Kodein.Module
}