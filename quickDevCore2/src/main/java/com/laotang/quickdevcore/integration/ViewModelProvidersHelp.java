package com.laotang.quickdevcore.integration;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class ViewModelProvidersHelp {

    @Deprecated
    public ViewModelProvidersHelp() {
    }

    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull ViewModelStoreOwner viewModelStoreOwner) {
        return of(viewModelStoreOwner, null);
    }

    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull ViewModelStoreOwner viewModelStoreOwner, @Nullable ViewModelProvider.Factory factory) {
        if (factory == null) {
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(AppManager.Companion.getInstance().getApplication());
        }
        return new ViewModelProvider(viewModelStoreOwner.getViewModelStore(), factory);
    }
}
