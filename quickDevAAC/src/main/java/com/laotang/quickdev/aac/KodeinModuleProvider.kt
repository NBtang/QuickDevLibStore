package com.laotang.quickdev.aac

import org.kodein.di.Kodein

interface KodeinModuleProvider {
    fun providerModule(): Kodein.Module
}