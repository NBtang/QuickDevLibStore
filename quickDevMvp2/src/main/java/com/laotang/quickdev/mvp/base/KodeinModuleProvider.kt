package com.laotang.quickdev.mvp.base

import org.kodein.di.Kodein

interface KodeinModuleProvider {
    fun providerModule(): Kodein.Module
}