package com.laotang.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.laotang.quickdevcore.integration.imageloader.ImageLoaderStrategy
import java.io.File

class GlideImageLoaderStrategy: ImageLoaderStrategy<GlideImageConfig>,GlideAppliesOptions {

    override fun loadImage(view: View, config: GlideImageConfig) {
        loadImage(view.context,config,view)
    }

    override fun loadImage(context: Context, config: GlideImageConfig) {
        loadImage(context,config,null)
    }

    override fun clear(view: View, config: GlideImageConfig?) {
        GlideQuick.get(view.context).requestManagerRetriever.get(view.context).clear(view)
    }


    override fun applyGlideOptions(context: Context, builder: GlideBuilder) {

    }

    private fun loadImage(context: Context, config: GlideImageConfig, view: View? ){
        if(view !=null){
            val requests = if(config.path.isNotEmpty() && File(config.path).exists()){
                GlideQuick.with(context).load(File(config.path))
            }else{
                GlideQuick.with(context).load(config.url)
            }
            requests.apply {
                loadImageConfig(config)
                if(view is ImageView){
                    into(view)
                }else{
                    into(object :ViewTarget<View,Drawable>(view){
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            getView().setBackgroundDrawable(resource)
                        }
                    })
                }
            }
        }else{
            val requests = GlideQuick.with(context)
            config.loaderCallBack?.let {
                requests.asBitmap().apply {
                    load(config.url)
                    loadImageConfig(config)
                    into(object : SimpleTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            it.onBitmapLoaded(resource)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            errorDrawable?.apply {
                                it.onBitmapFailed(this)
                            }
                        }
                    })
                }
            }
        }
    }

    private inline fun <reified T> GlideRequest<T>.loadImageConfig(config: GlideImageConfig){
        when (config.cacheStrategy) {
            //缓存策略
            0 -> diskCacheStrategy(DiskCacheStrategy.ALL)
            1 -> diskCacheStrategy(DiskCacheStrategy.NONE)
            2 -> diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            3 -> diskCacheStrategy(DiskCacheStrategy.DATA)
            4 -> diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            else -> diskCacheStrategy(DiskCacheStrategy.ALL)
        }

        if (config.isCenterCrop) {
            centerCrop()
        }

        if (config.isCircle) {
            circleCrop()
        }

        if (config.isImageRadius()) {
            transform(RoundedCorners(config.imageRadius))
        }

        if (config.placeholder > 0) {
            placeholder(config.placeholder)
        }

        if (config.errorPic > 0) {
            error(config.errorPic)
        }

        if (config.targetWidth > 0 && config.targetHeight > 0) {
            override(config.targetWidth, config.targetHeight)
        }
    }
}