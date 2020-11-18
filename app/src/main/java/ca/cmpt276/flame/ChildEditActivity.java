package ca.cmpt276.flame;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ca.cmpt276.flame.model.BGMusicPlayer;
import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.ChildrenManager;

/**
 * ChildEditActivity:
 * Add a new child
 * Rename an existing child
 * Delete a child
 */
public class ChildEditActivity extends AppCompatActivity {
    private static final String EXTRA_CHILD_ID = "EXTRA_CHILD_ID";
    Bitmap childImage = null;
    private Child clickedChild;
    private String newName;
    private Child newChild;
    private final ChildrenManager childrenManager = ChildrenManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_edit);
        getDataFromIntent();
        setupToolbar();
        fillChildName();
        fillChildImage();
        setupSaveButton();

        setUpEditImageButton();
        if (clickedChild == null) {
            hideDeleteButton();
        } else {
            setupDeleteButton();
        }
    }

    private void getDataFromIntent() {
        long childId = getIntent().getLongExtra(EXTRA_CHILD_ID, Child.NONE);
        clickedChild = childrenManager.getChild(childId);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_edit);

        if(clickedChild == null) {
            toolbar.setTitle(R.string.add_child);
        } else {
            toolbar.setTitle(R.string.edit_child);
        }

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void fillChildName() {
        if(clickedChild != null) {
            EditText inputName = findViewById(R.id.childEdit_editTxtChildName);
            inputName.setText(clickedChild.getName());
        }
    }

    private void fillChildImage() {
        if(clickedChild != null) {
            loadChildImage(clickedChild.getImagePath());
        }
        else{
            loadChildImage(null);
        }
        ImageView imageView = findViewById(R.id.childEdit_child_image_view);
        imageView.setImageBitmap(childImage);
    }

    private void setupSaveButton() {
        Button btn = findViewById(R.id.childEdit_btnSave);

        btn.setOnClickListener(v -> {
            EditText inputName = findViewById(R.id.childEdit_editTxtChildName);
            newName = inputName.getText().toString();
            if(newName.isEmpty()) {
                Toast.makeText(this, getString(R.string.child_name_empty_error), Toast.LENGTH_SHORT).show();
                return;
            }

            if (clickedChild != null) {
                childrenManager.renameChild(clickedChild, newName);
                childrenManager.changeChildPic(clickedChild, saveChildImage(clickedChild));
            } else {
                newChild = childrenManager.addChild(newName);
                String imgPath = saveChildImage(newChild);
                childrenManager.changeChildPic(newChild, imgPath);
            }
            finish();
        });
    }

    private void setUpEditImageButton() {
        ImageButton inputImageBtn = findViewById(R.id.childEdit_changeImageBtn);
        inputImageBtn.setOnClickListener(v ->  {
                new AlertDialog.Builder(ChildEditActivity.this)
                        .setTitle(R.string.choose)
                        .setPositiveButton(R.string.gallery, ((dialogInterface, i) -> {
                            pickImageFromGallery();
                        }))
                        .setNegativeButton(R.string.remove, ((dialogInterface, i) ->{
                            if(clickedChild != null) {
                                deleteChildImgFromInternalStorage(clickedChild);
                            }
                            loadChildImage(null);
                            ImageView imageView = findViewById(R.id.childEdit_child_image_view);
                            imageView.setImageBitmap(childImage);
                        })).show();

            /*@Override
            public void onClick(View v) {
                v = LayoutInflater.from(getActivity())
                        .inflate(R.layout.edit_image_msg_layout, null);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();;
                    }
                };
                new AlertDialog.Builder(ChildEditActivity.this)
                        .setTitle("Choose Options")
                        .setView(v)
                        .setPositiveButton(R.string.cancel, listener)
                        .create();
                FragmentManager manager = getSupportFragmentManager();
                Fragment dialog = new Fragment();
                dialog.show(manager, "Choices");
               // pickImageFromGallery();*/

        });
    }

    private void pickImageFromGallery() {
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    //handle result of picked image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        //Code from : https://stackoverflow.com/questions/10165302/dialog-to-pick-image-from-gallery-or-from-camera
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Uri selectedImage;
        ImageView childImageView = findViewById(R.id.childEdit_child_image_view);
        switch (requestCode) {
            case 0:
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    childImageView.setImageURI(selectedImage);
                    try {
                        childImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    private String saveChildImage(Child child) {
        //Code from :https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
        //saves the image of child with name including the child id
        final int IMAGE_QUALITY = 100;
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("childImageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath;
        myPath = new File(directory, "" + child.getId() + "profile.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            childImage.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    private void loadChildImage(String path) {
        //Code from: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
        if(path == null){
            childImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_child);
        } else {
            File f = null;
            try {
                if (clickedChild != null) {
                    f = new File(path, "" + clickedChild.getId() + "profile.jpg");
                }
                childImage = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                childImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_child);
            }
        }
    }
    private void hideDeleteButton() {
        Button btn = findViewById(R.id.childEdit_btnDelete);
        btn.setVisibility(View.GONE);
    }

    private void setupDeleteButton() {
        Button btn = findViewById(R.id.childEdit_btnDelete);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(v -> {
            new AlertDialog.Builder(ChildEditActivity.this)
                    .setTitle(R.string.confirm)
                    .setMessage(R.string.childEditActivity_confirmDeleteMsg)
                    .setPositiveButton(R.string.delete, ((dialogInterface, i) -> {
                        deleteChildImgFromInternalStorage(clickedChild);
                        childrenManager.removeChild(clickedChild);
                        finish();
                    }))
                    .setNegativeButton(R.string.cancel, null).show();
        });
    }

    private void deleteChildImgFromInternalStorage(Child child) {
        String dir = child.getImagePath();
        File file = new File(dir, "" + child.getId() + "profile.jpg");
    }

    @Override
    protected void onResume() {
        super.onResume();
        BGMusicPlayer.resumeBgMusic();
    }

    protected static Intent makeIntent(Context context, Child child) {
        long childId = Child.NONE;
        if(child != null) {
            childId = child.getId();
        }

        Intent intent = new Intent(context, ChildEditActivity.class);
        intent.putExtra(EXTRA_CHILD_ID, childId);
        return intent;
    }
}