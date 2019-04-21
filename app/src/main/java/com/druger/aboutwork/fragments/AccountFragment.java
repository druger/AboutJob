package com.druger.aboutwork.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.interfaces.view.AccountView;
import com.druger.aboutwork.presenters.AccountPresenter;
import com.druger.aboutwork.utils.PreferencesHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
import static com.theartofdev.edmodo.cropper.CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE;
import static com.theartofdev.edmodo.cropper.CropImage.getPickImageChooserIntent;


public class AccountFragment extends BaseSupportFragment implements AccountView{

    @InjectPresenter
    AccountPresenter accountPresenter;
    @Inject
    PreferencesHelper preferencesHelper;

    private TextView tvName;
    private TextView tvHeaderName;
    private ImageView civAvatar;
    private CardView cvLogout;
    private CardView cvEmail;
    private CardView cvName;
    private CardView cvPassword;
    private CardView cvRemoveAccount;
    private TextView tvEmail;
    private FrameLayout ltAuthAccount;
    private ConstraintLayout content;
    private Button btnLogin;
    private TextView tvAuth;

    private Uri selectedImgUri;

    @ProvidePresenter
    AccountPresenter getAccountPresenter() {
        return App.Companion.getAppComponent().getAccountPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.Companion.getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_account, container, false);

        setupUI();
        accountPresenter.getUserInfo();
        setupListeners();

        return rootView;
    }

    private void setupListeners() {
        cvLogout.setOnClickListener(v -> showLogoutDialog());
        civAvatar.setOnClickListener(v -> showPhotoPicker());
        cvEmail.setOnClickListener(v -> showChangeEmail());
        cvName.setOnClickListener(v -> showChangeName());
        cvPassword.setOnClickListener(v -> showChangePassword());
        cvRemoveAccount.setOnClickListener(v -> showRemoveDialog());
        btnLogin.setOnClickListener(v -> showLogin());
    }

    private void showLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void setupUI() {
        tvName = bindView(R.id.tvName);
        tvHeaderName = bindView(R.id.tvHeaderName);
        civAvatar = bindView(R.id.ivAvatar);
        cvLogout = bindView(R.id.cvLogout);
        cvEmail = bindView(R.id.cvEmail);
        tvEmail = bindView(R.id.tvEmail);
        cvName = bindView(R.id.cvName);
        cvRemoveAccount = bindView(R.id.cvRemoveAcc);
        cvPassword = bindView(R.id.cvPassword);
        ltAuthAccount = bindView(R.id.ltAuthAccount);
        content = bindView(R.id.content);
        btnLogin = bindView(R.id.btnLogin);
        tvAuth = bindView(R.id.tvAuth);
    }

    private void showRemoveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
        builder.setTitle(R.string.remove_account_ask);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            accountPresenter.removeAccount();
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showChangePassword() {
        ChangePasswordFragment passwordFragment = new ChangePasswordFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.main_container, passwordFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showChangeName() {
        ChangeNameFragment nameFragment = new ChangeNameFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.main_container, nameFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showChangeEmail() {
        String email = tvEmail.getText().toString().trim();
        ChangeEmailFragment changeEmail = ChangeEmailFragment.newInstance(email);
        getFragmentManager().beginTransaction()
                .replace(R.id.main_container, changeEmail)
                .addToBackStack(null)
                .commit();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
        builder.setTitle(R.string.log_out);
        builder.setMessage(R.string.message_log_out);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            AuthUI.getInstance()
                    .signOut(getActivity())
                    .addOnCompleteListener(task -> {
                        Log.d("Log out", "result: " + task.isSuccessful());
                        accountPresenter.logout();
                    });
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showPhotoPicker() {
        startActivityForResult(getPickImageChooserIntent(getActivity()), PICK_IMAGE_CHOOSER_REQUEST_CODE);
    }

    private void checkPermissionReadExternal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    private void startCropImageActivity(Uri imgUri) {
        CropImage.activity(imgUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ?
                        CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
                .setActivityTitle(getString(R.string.photo_crop_name))
                .setScaleType(CropImageView.ScaleType.CENTER)
                .setAspectRatio(1, 1)
                .start(getActivity(), this);
    }

    @Override
    public void setupPhoto(Uri imgUri) {
        civAvatar.setImageURI(imgUri);
    }

    @Override
    public void showPhoto(StorageReference storageRef) {
        Glide.with(getActivity())
                .load(storageRef)
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.ic_account_circle_black)
                .into(civAvatar);
    }

    @Override
    public void showHeaderName(String name) {
        tvHeaderName.setText(name);
    }

    @Override
    public void onStop() {
        super.onStop();
        accountPresenter.removeListeners();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            pickImage(data);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            cropImage(resultCode, data);
        }
    }

    private void pickImage(Intent data) {
        Uri imgUri = CropImage.getPickImageResultUri(getActivity(), data);
        if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imgUri)) {
            selectedImgUri = imgUri;
            checkPermissionReadExternal();
        } else {
            Log.d(TAG, "pickImage: startCropImageActivity");
            startCropImageActivity(imgUri);
        }
    }

    public void cropImage(int resultCode, Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            selectedImgUri = result.getUri();
            accountPresenter.savePhoto(selectedImgUri);
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Log.e(TAG, "Cropping failed: " + result.getError().getMessage());
        }
    }

    @Override
    public void showToast(@StringRes int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    @Override
    public void showName(@NotNull String name) {
        tvName.setText(name);
    }

    @Override
    public void showEmail(@NotNull String email) {
        tvEmail.setText(email);
    }

    @Override
    public void showAuthAccess() {
        content.setVisibility(View.INVISIBLE);
        ltAuthAccount.setVisibility(View.VISIBLE);
        tvAuth.setText(R.string.account_login);
    }
}
