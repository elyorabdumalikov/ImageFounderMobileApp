package com.uz.exampletaxi.dialogs

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.uz.exampletaxi.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.uz.domain.models.ImageDbModel
import com.uz.exampletaxi.databinding.FavoriteItemDialogBinding
import com.uz.exampletaxi.utils.makeExpanded
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ShowFavoriteImageDialog(private val callBack:CallBack ,
                      private val model: ImageDbModel) : BottomSheetDialogFragment() {

    private lateinit var binding:FavoriteItemDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.setOnShowListener { it.makeExpanded() }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        binding = FavoriteItemDialogBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)

        binding.authorName.text = model.user
        binding.views.text = model.views.toString()

        setUpImages(model.largeImageURL,binding.largeImage)
        setUpImages(model.userImageURL,binding.userAvatarImage)
        setUpClicks()

        return dialog
    }


    private fun setUpImages(url:String,img:ImageView){
        Glide.with(requireContext())
            .load(url)
            .error(R.drawable.ic_baseline_person_24)
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
                                          isFirstResource: Boolean): Boolean {
                    Toast.makeText(requireContext(), R.string.no_connection, Toast.LENGTH_SHORT).show()

                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
                                             dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .into(img)
    }

    private fun setUpClicks(){
        binding.shareBtn.setOnClickListener { callBack.onShare(binding.largeImage) }
        binding.saveBtn.setOnClickListener { callBack.onSave(binding.largeImage) }
        binding.openBrowserBtn.setOnClickListener { callBack.onOpenBrowser(model) }
        binding.userAvatarCardView.setOnClickListener { callBack.onOpenUserPage(model) }
        binding.exitBtn.setOnClickListener { dismiss() }
        binding.removeFavoriteBtn.setOnClickListener {
            callBack.onClickRemoveFavorites(model)
            dismiss()
        }
        binding.setWallpaperBtn.setOnClickListener { callBack.onClickSetWallpaper(binding.largeImage) }
    }

    interface CallBack {
        fun onShare(imageView: ImageView)
        fun onSave(imageView: ImageView)
        fun onOpenBrowser(model: ImageDbModel)
        fun onOpenUserPage(model: ImageDbModel)
        fun onClickRetry()
        fun onClickRemoveFavorites(model: ImageDbModel)
        fun onClickSetWallpaper(imageView: ImageView)
    }
}