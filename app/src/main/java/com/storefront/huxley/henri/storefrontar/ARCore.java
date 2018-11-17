package com.storefront.huxley.henri.storefrontar;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            file_name = getIntent().getExtras().getString("filename");
            Toast.makeText(this, file_name, Toast.LENGTH_LONG).show();
        }catch(Exception E) {
            Log.e("ARCORE_ONCREATE", "Needs File Name Intent.");
        }

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
        //buildAndroidWidgetModel();
        build3dModel();

        // handle taps
        handleUserTaps();
    }

    private void handleUserTaps() {
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            // viewRenderable must be loaded
            if (modelRenderable ==  null) {
                return;
            }

            // create the an anchor on the scene
            AnchorNode anchorNode = createAnchorNode(hitResult);

            // add the view to the scene
            addRenderableToScene(anchorNode, modelRenderable);

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

    /**
     * Creates a {@link ViewRenderable} which will inflate the hello_world_view.
     *
     * Note: The resources get loaded on the background thread automagically.
     *       'thenAccept' is the callback for completion.

    private void buildAndroidWidgetModel() {

        ViewRenderable.builder()
                .setView(this, R.layout.hello_world_view)
                .build()
                .thenAccept(renderable -> {
                    viewRenderable = renderable;

                    if (viewRenderable != null) {
                        // get the view from the renderable
                        View androidView = viewRenderable.getView();

                        //Button btnToast = androidView.findViewById(R.id.button_toast);
                        //btnToast.setOnClickListener(view -> {
                         //   Toast.makeText(MainActivity.this, "Hello World",
                          //          Toast.LENGTH_LONG).show();
                        //});
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(ARCore.this, "Unable to display Hello World",
                            Toast.LENGTH_LONG).show();

                    return null;
                });
    }
     */

    /**
     * Creates a {@link ModelRenderable} which will load the helloWorld.sfb from the assets folder.
     *
     * Note: The resources get loaded on the background thread automagically.
     *       'thenAccept' is the callback for completion.
     *
     *       helloWorld.sfb was added to the assets folder by the Sceneform plugin when we imported
     *       the asset
     */
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


    /**
     * Checks to see if Sceneform can run on the device OpenGL version.
     *
     * @param activity
     *      - ${@link Activity} from which to fetch the system services.
     *
     * @return true if Sceneform can run; false otherwise.
     */
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

}
