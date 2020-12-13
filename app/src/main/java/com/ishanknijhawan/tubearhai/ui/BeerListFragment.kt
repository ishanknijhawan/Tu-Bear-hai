package com.ishanknijhawan.tubearhai.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ishanknijhawan.tubearhai.R
import com.ishanknijhawan.tubearhai.adapter.BeerAdapter
import com.ishanknijhawan.tubearhai.adapter.LoadingBeerAdapter
import com.ishanknijhawan.tubearhai.data.Beer
import com.ishanknijhawan.tubearhai.databinding.BeerListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.beer_list_fragment.*
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
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = BeerListFragmentBinding.bind(view)
        val adapter = BeerAdapter(this)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        binding.apply {
            rvBeers.setHasFixedSize(true)
            rvBeers.adapter = adapter.withLoadStateHeaderAndFooter(
                header = LoadingBeerAdapter { adapter.retry() },
                footer = LoadingBeerAdapter { adapter.retry() }
            )
        }

        mViewModel.beers.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                tv_no_results.visibility = View.VISIBLE
            }
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemLongClick(beer: Beer) {
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            25,
                            VibrationEffect.EFFECT_CLICK
                        )
                    )
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            25,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                }
                else -> {
                    vibrator.vibrate(25)
                }
            }
        }
        val detailText = setDetailText(beer)
        tv_details.text = detailText
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onClickShare(beer: Beer) {

        val detailText = setDetailText(beer)

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
                            cachePath.mkdirs()
                            val stream =
                                FileOutputStream("$cachePath/image.png")
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            stream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        val imagePath = File(requireContext().cacheDir, "images")
                        val newFile = File(imagePath, "image.png")
                        val imageUri =
                            FileProvider.getUriForFile(
                                requireContext(),
                                "${getString(R.string.appId)}.provider",
                                newFile
                            )
                        if (imageUri != null) {
                            val intent = Intent()
                            intent.action = Intent.ACTION_SEND
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.setDataAndType(
                                imageUri,
                                requireContext().contentResolver.getType(imageUri)
                            )
                            intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                            intent.putExtra(Intent.EXTRA_TEXT, detailText)
                            intent.type = "*/*"
                            startActivity(Intent.createChooser(intent, "Choose an app"))
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        } catch (e: IOException) {
            Toast.makeText(context, "Error sharing the image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setDetailText(beer: Beer): String {
        var maltString = ""
        beer.ingredients.malt.map { malt -> maltString += "${malt.name} (${malt.amount.value} ${malt.amount.unit}), " }

        var hopsString = ""
        beer.ingredients.hops.map { hops -> hopsString += "${hops.name} (${hops.amount.value} ${hops.amount.unit}), " }

        var foodPairing = ""
        beer.foodPairing.map { food -> foodPairing += "$food, " }

        return """
            ${beer.name}: ${beer.description}
            
            ● Food Pairs:
            $foodPairing
            ● pH: ${beer.ph} 
            ● attenuation level: ${beer.attenuationLevel}
            ● Boil Volume: ${beer.boilVolume.value} ${beer.boilVolume.unit}
            ● Ingredients: 
                ● Malt
                $maltString
                ● Hops
                $hopsString
                ● Yeast
                ${beer.ingredients.yeast}
            ● Brewer Tips: ${beer.brewersTips}
            ● Contributed By: ${beer.contributedBy}
        """.trimIndent()
    }

}