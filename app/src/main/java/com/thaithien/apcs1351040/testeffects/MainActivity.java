package com.thaithien.apcs1351040.testeffects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer {
    private GLSurfaceView mEffectView;
    private int[] mTextures = new int[2];
    private EffectContext mEffectContext;
    private Effect mEffect;
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mInitialized = false;

    //
    int mCurrentEffect;
    int EFFECT_CONTRAST = 1;
    int EFFECT_BRIGHTNESS = 2;
    int EFFECT_FISHEYE = 3;


    //seekbar
    private SeekBar mSeekBar;
    private SeekBar constractBar;
    private SeekBar brightnessBar;
    private SeekBar fillLight;
    private SeekBar fishEye;

    public void setCurrentEffect(int effect) {
        mCurrentEffect = effect;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * Initialize the renderer and tell it to only render when
         * explicity requested with the RENDERMODE_WHEN_DIRTY option
         */
        mEffectView = (GLSurfaceView) findViewById(R.id.effectsview);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCurrentEffect = R.id.none;
        setCurrentEffect( EFFECT_CONTRAST);
        initButton();
        initSeekBar();

    }

    private void initButton(){
        Button fish = (Button) findViewById(R.id.fisheye);
        Button contrast = (Button) findViewById(R.id.contrast);
        Button bright = (Button) findViewById(R.id.bright);
        fish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentEffect(EFFECT_FISHEYE);
            }
        });
        contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentEffect(EFFECT_CONTRAST);
            }
        });

        bright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentEffect(EFFECT_BRIGHTNESS);
            }
        });
    }
    
    //init seekbar, add listener
    private void initSeekBar(){
        
        mSeekBar = (SeekBar) findViewById(R.id.mSeekBar_id);
        mSeekBar.setProgress(100);
        mSeekBar.setMax(200);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                float start_num = 0f;
                float num = start_num + (float)progress*0.01f ;

                initEffect(num);
                mEffectView.requestRender();
            }
        });
/*
        if (mCurrentEffect == EFFECT_CONTRAST)
        {
            constractBar = (SeekBar) findViewById(R.id.mSeekBar_id);
            constractBar.setProgress(100);
            constractBar.setMax(200);
            constractBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    float start_constract_scale = 0f;
                    float constract_scale = start_constract_scale + (float)progress*0.01f ;
                    Log.i("scale seekbar","scale = "+constract_scale);
                    initEffect(constract_scale);
                    mEffectView.requestRender();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress();
                    float start_constract_scale = 0f;
                    float constract_scale = start_constract_scale + (float)progress*0.01f ;
                    Log.i("scale seekbar","scale = "+constract_scale);
                    initEffect(constract_scale);
                    mEffectView.requestRender();
                }
            });
        }

        if (mCurrentEffect == EFFECT_BRIGHTNESS){
            brightnessBar = (SeekBar) findViewById(R.id.mSeekBar_id);
            brightnessBar.setProgress(100);
            brightnessBar.setMax(200);
            brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    float start = 0f;
                    float scale = start + (float)progress*0.01f ;
                    Log.i("scale seekbar","scale = "+scale);
                    initEffect(scale);
                    mEffectView.requestRender();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        if (mCurrentEffect == EFFECT_FISHEYE){
            fishEye = (SeekBar) findViewById(R.id.mSeekBar_id);
            fishEye.setProgress(100);
            fishEye.setMax(200);
            fishEye.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    float start = 0f;
                    float scale = start + (float)progress*0.01f ;
                    Log.i("scale seekbar","scale = "+scale);
                    initEffect(scale);
                    mEffectView.requestRender();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEffectView.requestRender();
    }

    private void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(2, mTextures, 0);

        // Load input bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.puppy);
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

        // Upload to texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Set texture parameters
        GLToolbox.initTexParams();
    }

    //will create effect depend onc current effect
    private void initEffect(float arg) {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffect != null) {
            mEffect.release();
        }

        if (mCurrentEffect == EFFECT_BRIGHTNESS) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_BRIGHTNESS);
            mEffect.setParameter("brightness", arg);
            Log.i("Effect", "init = " + arg);
        }

        if (mCurrentEffect ==  EFFECT_CONTRAST){
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_CONTRAST);
            mEffect.setParameter("contrast", arg);
            Log.i("Effect", "init = " + arg);
        }

        if (mCurrentEffect ==  EFFECT_FISHEYE){
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_FISHEYE);
            mEffect.setParameter("scale", arg);
            Log.i("Effect", "init = " + arg);
        }
    }

    private void applyEffect() {
        mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
        Log.i("Effect", "apply");
    }

    private void renderResult() {


            mTexRenderer.renderTexture(mTextures[1]);

    }



    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
            initEffect(1.0f);
        }


           // initEffect();
            applyEffect();

        renderResult();
        Log.i("Effect", "draw");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mTexRenderer != null) {
            mTexRenderer.updateViewSize(width, height);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
       // inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setCurrentEffect(item.getItemId());
        mEffectView.requestRender();
        return true;
    }

}
