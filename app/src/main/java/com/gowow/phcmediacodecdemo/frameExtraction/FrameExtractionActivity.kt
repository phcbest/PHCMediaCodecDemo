package com.gowow.phcmediacodecdemo.frameExtraction

import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UriUtil
import androidx.media3.exoplayer.ExoPlayer
import com.blankj.utilcode.util.UriUtils
import com.gowow.phcmediacodecdemo.base.BaseActivity
import com.gowow.phcmediacodecdemo.databinding.ActivityFrameExtractionBinding
import com.gowow.pmediacodelib.VideoEdit

class FrameExtractionActivity :

    BaseActivity<ActivityFrameExtractionBinding>(ActivityFrameExtractionBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectVideo()
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                binding.player.useController = false
                binding.player.player = ExoPlayer.Builder(this).build().also {
                    it.setMediaItem(MediaItem.fromUri(uri))
                    it.prepare()
                    it.play()
                }
                uri.encodedPath?.let { VideoEdit.getVideoInfo(UriUtils.uri2File(uri).path) }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private fun selectVideo() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))

    }
}