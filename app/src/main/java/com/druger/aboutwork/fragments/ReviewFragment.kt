package com.druger.aboutwork.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.druger.aboutwork.R
import com.druger.aboutwork.adapters.PhotoAdapter
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

open class ReviewFragment : BaseSupportFragment() {

    protected lateinit var uriPhotoAdapter: PhotoAdapter<Uri>
    protected var isFullScreenShown = false
    private var currentPhotoPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uriPhotoAdapter = PhotoAdapter()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            currentPhotoPosition = savedInstanceState.getInt(CURRENT_PHOTO_POSITION)
            isFullScreenShown = savedInstanceState.getBoolean(FULL_SCREEN)
        }
        if (isFullScreenShown) uriPhotoAdapter.showFullScreen(requireContext(), currentPhotoPosition, null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CURRENT_PHOTO_POSITION, uriPhotoAdapter.currentPosition)
        outState.putBoolean(FULL_SCREEN, uriPhotoAdapter.isFullScreen)
        super.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_FILES)
    protected fun checkPermission() {
        val perms = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireContext(), *perms)) {
            choosePhotos()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permision_read_files), RC_FILES, *perms)
        }
    }

    private fun choosePhotos() {
        val intent = Intent().apply {
            type = MIME_TYPE
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_photo)), RC_PICK_IMAGE)
    }

    companion object {
        private const val RC_FILES = 1111
        const val RC_PICK_IMAGE = 1
        private const val MIME_TYPE = "image/*"
        const val CURRENT_PHOTO_POSITION = "current_photo_position"
        const val FULL_SCREEN = "full_screen"
    }
}