package com.storefront.huxley.henri.storefrontar;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ARCore extends AppCompatActivity {

    private static final String TAG = ARCore.class.getName();

    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ViewRenderable viewRenderable;
    private ModelRenderable modelRenderable;
    private String file_name;
    private Boolean hasBeenAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file_name = getIntent().getExtras().getString("filename");

        // make sure OpenGL version is supported

        if (!checkIsSupportedDevice(this)) {
            String errorMessage =  "Sceneform requires OpenGL ES " + MIN_OPENGL_VERSION + " or later";
            Log.e(TAG, errorMessage);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            finish(); // finish the activity
            return;
        }

        // As soon as we call setContentView, they view will be inflated, including the ARFragment
        // This is why we need to check for OpenGL first
        setContentView(R.layout.arcore);

        setupArScene();
    }

    private void setupArScene() {
        // ARFragment is what is displaying our scene
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        // load the renderables
        build3dModel();

        // handle taps
        handleUserTaps();
    }

    private void handleUserTaps() {
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if(!hasBeenAdded) {
                hasBeenAdded = true;
                // viewRenderable must be loaded
                if (modelRenderable == null) {
                    return;
                }

                // create the an anchor on the scene
                AnchorNode anchorNode = createAnchorNode(hitResult);

                // add the view to the scene
                addRenderableToScene(anchorNode, modelRenderable);
            }
        });

    }

    private AnchorNode createAnchorNode(HitResult hitResult) {

        // create an anchor based off the the HitResult (what was tapped)
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);

        // attach this anchor to the scene
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        return anchorNode;
    }


    private Node addRenderableToScene(AnchorNode anchorNode, Renderable renderable) {
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());

        // anchor node knows where it fits into our world
        node.setParent(anchorNode);
        node.setRenderable(renderable);
        node.select();
        return node;
    }

    private void build3dModel() {

        ModelRenderable.builder()
                // helloWorld.sfb was added to the assets folder by the Sceneform plugin when
                // we imported the asset
                .setSource(this, Uri.parse(file_name))
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(throwable -> {
                    Toast.makeText(ARCore.this, "Unable to display model",
                            Toast.LENGTH_LONG).show();
                    return null;
                });
    }


    private boolean checkIsSupportedDevice(final Activity activity) {

        ActivityManager activityManager =
                (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager == null) {
            Log.e(TAG, "ActivityManager is null");
            return false;
        }

        String openGlVersion = activityManager.getDeviceConfigurationInfo().getGlEsVersion();

        return openGlVersion != null && Double.parseDouble(openGlVersion) >= MIN_OPENGL_VERSION;
    }

    public void btn_Return(View view) {
        onBackPressed();
    }

    public void btn_Reset(View view) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
