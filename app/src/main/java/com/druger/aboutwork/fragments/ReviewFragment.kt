package com.druger.aboutwork.fragments

import android.content.Intent
import com.druger.aboutwork.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

open class ReviewFragment : BaseSupportFragment() {

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
    }
}