package com.druger.aboutwork.fragments;


import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.bumptech.glide.Glide;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.interfaces.view.AccountView;
import com.druger.aboutwork.presenters.AccountPresenter;
import com.druger.aboutwork.utils.PreferencesHelper;
import com.druger.aboutwork.utils.Utils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.theartofdev.edmodo.cropper.CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE;
import static com.theartofdev.edmodo.cropper.CropImage.getPickImageChooserIntent;


public class AccountFragment extends BaseFragment implements View.OnClickListener, AccountView{

    @InjectPresenter
    AccountPresenter accountPresenter;
    @Inject
    PreferencesHelper preferencesHelper;

    private TextView tvName;
    private ImageView ivEditName;
    private TextView tvMyReviews;
    private TextView tvSettings;
    private TextView tvLogout;
    private CircleImageView civAvatar;
    private TextView tvEmail;

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
        setupToolbar();

        return rootView;
    }

    private void setupListeners() {
        tvSettings.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        ivEditName.setOnClickListener(this);
        tvMyReviews.setOnClickListener(this);
        civAvatar.setOnClickListener(this);
    }

    private void setupToolbar() {
        toolbar = bindView(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle(R.string.profile);
    }

    private void setupUI() {
        tvName = bindView(R.id.tvName);
        ivEditName = bindView(R.id.ivEditName);
        tvMyReviews = bindView(R.id.tvMyReviews);
        tvSettings =  bindView(R.id.tvSettings);
        tvLogout = bindView(R.id.tvLogout);
        civAvatar = bindView(R.id.civAvatar);
        tvEmail = bindView(R.id.tvEmail);
    }

    @Override
    public void onStop() {
        super.onStop();
        accountPresenter.removeListeners();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivEditName:
                accountPresenter.clickChangeName();
                break;
            case R.id.tvSettings:
                accountPresenter.clickOpenSettings();
                break;
            case R.id.tvLogout:
                accountPresenter.logout();
                break;
            case R.id.tvMyReviews:
                accountPresenter.clickOpenMyReviews();
                break;
            case R.id.civAvatar:
                showPhotoPicker();
                break;
            default:
                break;
        }
    }

    private void showPhotoPicker() {
        startActivityForResult(getPickImageChooserIntent(getActivity()), PICK_IMAGE_CHOOSER_REQUEST_CODE);
    }

    @Override
    public void openMyReviews(String userId) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, MyReviewsFragment.newInstance(userId));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void showEmail(String email) {
        tvEmail.setText(email);
    }

    @Override
    public void checkPermissionReadExternal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void startCropImageActivity(Uri imgUri) {
        CropImage.activity(imgUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
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
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .crossFade()
                .error(R.drawable.ic_account_circle_black)
                .into(civAvatar);
    }

    @Override
    public void showName(String name) {
        tvName.setText(name);
    }

    @Override
    public void openSettings() {
        SettingsFragment settings = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_container, settings)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void changeName(final String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_name, null);
        final EditText etName = view.findViewById(R.id.etUserName);
        etName.setText(tvName.getText());

        builder.setTitle(R.string.new_name);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            String userName = etName.getText().toString();
            if (!userName.trim().isEmpty()) {
                tvName.setText(userName);
                Utils.hideKeyboard(getActivity(), etName);
                accountPresenter.changeUserName(userName, userId);
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            Utils.hideKeyboard(getActivity(), etName);
            dialog.cancel();
        });
        builder.show();
        Utils.showKeyboard(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        accountPresenter.checkActivityResult(getActivity(), requestCode, resultCode, data);
    }
}
