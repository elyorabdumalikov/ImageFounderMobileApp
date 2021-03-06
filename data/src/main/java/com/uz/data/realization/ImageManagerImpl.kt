package com.uz.data.realization

import android.content.Context
import android.graphics.Bitmap
import com.uz.data.cloud.abstraction.ImageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import android.provider.MediaStore
import android.widget.Toast
import com.uz.data.utils.DataConstants
import java.io.ByteArrayOutputStream
import android.app.WallpaperManager
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.readystatesoftware.chuck.internal.ui.MainActivity
import com.uz.data.R
import java.io.FileNotFoundException

class ImageManagerImpl
@Inject constructor(@ApplicationContext private val context: Context) : ImageManager {

    override suspend fun saveImage(imageView: ImageView) {
        var outFile: File? = null

        kotlin.runCatching {

            val bitmapDrawable = imageView.drawable as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap

            var outStream: FileOutputStream? = null
            val path: File = Environment.getExternalStorageDirectory()
            val dir = File(path.absolutePath.toString())
            val fileName = String.format("%d.jpg", System.currentTimeMillis())
            outFile = File(dir, fileName)
            outStream = FileOutputStream(outFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.flush()
            outStream.close()

        }.onFailure {
            if (it is NullPointerException) Toast.makeText(context, R.string.wait, Toast.LENGTH_SHORT).show()
            if (it is FileNotFoundException) openPermissionIntent()
            else Toast.makeText(context, R.string.wait, Toast.LENGTH_SHORT).show()

        }.onSuccess {
            Toast.makeText(context,context.getString(R.string.success_save).plus(" ${outFile?.absolutePath}"), Toast.LENGTH_SHORT).show()
            outFile?.let { it1 -> notifyDataChanged(it1) }

        }
    }


    private fun openPermissionIntent() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    override fun shareImage(imageView: ImageView) {
        try {
            val bitmapDrawable = imageView.drawable as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap

            val share = Intent(Intent.ACTION_SEND)
            share.type = DataConstants.IMAGE_TYPE
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap, (System.currentTimeMillis() / 1000).toString(), null
            )
            val imageUri = Uri.parse(path)
            share.addFlags(FLAG_ACTIVITY_NEW_TASK)
            share.putExtra(Intent.EXTRA_STREAM, imageUri)
            context.startActivity(
                Intent.createChooser(
                    share,
                    context.resources.getString(R.string.select_app)
                )
            )
        } catch (e: Exception) {
            Toast.makeText(context, R.string.an_error, Toast.LENGTH_SHORT).show()
        }
    }

    override suspend fun setWallpaper(imageView: ImageView) {
        kotlin.runCatching {
            val bitmapDrawable = imageView.drawable as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap
            val manager = WallpaperManager.getInstance(context)
            manager.setBitmap(bitmap)
        }.onSuccess {
            Toast.makeText(context, R.string.success_apply, Toast.LENGTH_SHORT).show()
        }.onFailure {
            Toast.makeText(context, R.string.wait, Toast.LENGTH_SHORT).show()
        }
    }

    private fun notifyDataChanged(file: File) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(file)
        context.sendBroadcast(intent)
    }
}