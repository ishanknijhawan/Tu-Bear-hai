package com.ishanknijhawan.tubearhai.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ishanknijhawan.tubearhai.R
import com.ishanknijhawan.tubearhai.adapter.BeerAdapter
import com.ishanknijhawan.tubearhai.adapter.LoadingBeerAdapter
import com.ishanknijhawan.tubearhai.data.Beer
import com.ishanknijhawan.tubearhai.databinding.BeerListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class BeerListFragment : Fragment(R.layout.beer_list_fragment),
    BeerAdapter.OnBeerItemClickListener {

    private val mViewModel by viewModels<BeerListViewModel>()
    private var _binding: BeerListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var bitmap: Bitmap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = BeerListFragmentBinding.bind(view)
        val adapter = BeerAdapter(this)

        binding.apply {
            rvBeers.setHasFixedSize(true)
            rvBeers.adapter = adapter.withLoadStateHeaderAndFooter(
                header = LoadingBeerAdapter { adapter.retry() },
                footer = LoadingBeerAdapter { adapter.retry() }
            )
        }

        mViewModel.beers.observe(viewLifecycleOwner, Observer {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick() {
        Toast.makeText(context, "Long press to view item details", Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClick(beer: Beer) {

    }

    override fun onClickShare(beer: Beer) {
        try {
            Glide.with(requireContext())
                .asBitmap()
                .load(beer.imageUrl)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        bitmap = resource
                        try {
                            val cachePath = File(requireContext().cacheDir, "images")
                            cachePath.mkdirs() // don't forget to make the directory
                            val stream =
                                FileOutputStream("$cachePath/image.png") // overwrites this image every time
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            stream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        val imagePath = File(requireContext().cacheDir, "images")
                        val newFile = File(imagePath, "image.png")
                        val contentUri =
                            FileProvider.getUriForFile(
                                requireContext(),
                                "${getString(R.string.appId)}.provider",
                                newFile
                            )
                        if (contentUri != null) {
                            val shareIntent = Intent()
                            shareIntent.action = Intent.ACTION_SEND
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            shareIntent.setDataAndType(
                                contentUri,
                                requireContext().contentResolver.getType(contentUri)
                            )
                            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                            startActivity(Intent.createChooser(shareIntent, "Choose an app"))
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        } catch (e: IOException) {
            Toast.makeText(context, "Error sharing the image", Toast.LENGTH_SHORT).show()
        }
    }

}