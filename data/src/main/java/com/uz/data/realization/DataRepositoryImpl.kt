package com.uz.data.realization

import android.widget.ImageView
import androidx.paging.PagingData
import com.uz.data.cloud.abstraction.ImageDataSource
import com.uz.data.cloud.abstraction.ImageManager
import com.uz.domain.models.ImageDomainModel
import com.uz.domain.abstraction.DataRepository
import com.uz.domain.models.ImageDbModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataRepositoryImpl

@Inject constructor(
    private val imageDataSource: ImageDataSource,
    private val imageManager: ImageManager) : DataRepository {

    override fun fetchImages(query: String): Flow<PagingData<ImageDomainModel>> {
        return imageDataSource.fetchImages(query)
    }

    override suspend fun saveImage(imageView: ImageView) {
        imageManager.saveImage(imageView)
    }

    override suspend fun shareImage(imageView: ImageView) {
        imageManager.shareImage(imageView)
    }

    override suspend fun setWallpaper(imageView: ImageView) {
        imageManager.setWallpaper(imageView)
    }

    override suspend fun fetchCacheImagesAsFlow():Flow<List<ImageDbModel>> {
        return imageDataSource.fetchCacheImagesAsFlow()
    }

    override fun fetchCacheImages():List<ImageDbModel> {
        return imageDataSource.fetchCacheImages()
    }

    override suspend fun saveCacheImage(imageDbModel: ImageDbModel) {
        imageDataSource.saveCacheImage(imageDbModel)
    }

    override fun removeCacheImage(id:Int) {
        imageDataSource.removeCacheImage(id)
    }
}