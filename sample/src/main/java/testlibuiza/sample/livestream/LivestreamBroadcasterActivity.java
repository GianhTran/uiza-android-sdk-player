package testlibuiza.sample.livestream;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import testlibuiza.R;
import testlibuiza.app.LSApplication;
import vn.uiza.core.base.BaseActivity;
import vn.uiza.core.utilities.LLog;
import vn.uiza.core.utilities.LPopupMenu;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.AndroidViewFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.BasicDeformationFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.BeautyFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.BlurFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.BrightnessFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.CartoonFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.ColorFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.ContrastFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.DuotoneFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.EarlyBirdFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.EdgeDetectionFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.ExposureFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.FireFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.GammaFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.GreyScaleFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.HalftoneLinesFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.Image70sFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.LamoishFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.MoneyFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.NegativeFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.NoFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.PixelatedFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.PolygonizationFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.RGBSaturationFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.RainbowFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.RippleFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.RotationFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.SaturationFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.SepiaFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.SharpnessFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.SurfaceFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.TemperatureFilterRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.ZebraFilterRender;
import vn.uiza.libstream.uiza.encoder.utils.gl.TranslateTo;
import vn.uiza.restapi.uiza.model.v3.metadata.getdetailofmetadata.Data;
import vn.uiza.uzv3.view.rl.livestream.PresetLiveStreamingFeed;
import vn.uiza.uzv3.view.rl.livestream.UZLivestream;
import vn.uiza.views.LToast;

public class LivestreamBroadcasterActivity extends BaseActivity implements View.OnClickListener, UZLivestream.Callback {
    private UZLivestream uzLivestream;
    private Button bStartStop;
    private Button bStartStopStore;
    private Button btSwitchCamera;
    private Button btFilter;
    private TextView tvMainUrl;

    @Override
    protected boolean setFullScreen() {
        return true;
    }

    @Override
    protected String setTag() {
        return "TAG" + getClass().getSimpleName();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_livestream_video_broadcaster;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        //LActivityUtil.changeScreenLandscape(activity);

        uzLivestream = (UZLivestream) findViewById(R.id.uiza_livestream);
        uzLivestream.setCallback(this);
        bStartStop = findViewById(R.id.b_start_stop);
        bStartStopStore = findViewById(R.id.b_start_stop_store);
        btSwitchCamera = findViewById(R.id.b_switch_camera);
        btFilter = (Button) findViewById(R.id.b_filter);
        tvMainUrl = (TextView) findViewById(R.id.tv_main_url);

        bStartStop.setEnabled(false);
        bStartStopStore.setEnabled(false);
        btSwitchCamera.setEnabled(false);
        btFilter.setEnabled(false);

        bStartStop.setOnClickListener(this);
        bStartStopStore.setOnClickListener(this);
        btSwitchCamera.setOnClickListener(this);
        btFilter.setOnClickListener(this);

        uzLivestream.setId(LSApplication.entityIdDefaultLIVE_TRANSCODE);
        //uizaLivestream.setId(LSApplication.entityIdDefaultLIVE_NO_TRANSCODE);
    }

    private void handleFilterClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.e_d_fxaa:
                uzLivestream.enableAA(!uzLivestream.isAAEnabled());
                break;
            case R.id.no_filter:
                uzLivestream.setFilter(new NoFilterRender());
                break;
            case R.id.android_view:
                AndroidViewFilterRender androidViewFilterRender = new AndroidViewFilterRender();
                androidViewFilterRender.setView(findViewById(R.id.activity_example_rtmp));
                uzLivestream.setFilter(androidViewFilterRender);
                break;
            case R.id.basic_deformation:
                uzLivestream.setFilter(new BasicDeformationFilterRender());
                break;
            case R.id.beauty:
                uzLivestream.setFilter(new BeautyFilterRender());
                break;
            case R.id.blur:
                uzLivestream.setFilter(new BlurFilterRender());
                break;
            case R.id.brightness:
                uzLivestream.setFilter(new BrightnessFilterRender());
                break;
            case R.id.cartoon:
                uzLivestream.setFilter(new CartoonFilterRender());
                break;
            case R.id.color:
                uzLivestream.setFilter(new ColorFilterRender());
                break;
            case R.id.contrast:
                uzLivestream.setFilter(new ContrastFilterRender());
                break;
            case R.id.duotone:
                uzLivestream.setFilter(new DuotoneFilterRender());
                break;
            case R.id.early_bird:
                uzLivestream.setFilter(new EarlyBirdFilterRender());
                break;
            case R.id.edge_detection:
                uzLivestream.setFilter(new EdgeDetectionFilterRender());
                break;
            case R.id.exposure:
                uzLivestream.setFilter(new ExposureFilterRender());
                break;
            case R.id.fire:
                uzLivestream.setFilter(new FireFilterRender());
                break;
            case R.id.gamma:
                uzLivestream.setFilter(new GammaFilterRender());
                break;
            case R.id.gif:
                uzLivestream.setGifToStream(R.raw.banana, TranslateTo.BOTTOM);
                break;
            case R.id.grey_scale:
                uzLivestream.setFilter(new GreyScaleFilterRender());
                break;
            case R.id.halftone_lines:
                uzLivestream.setFilter(new HalftoneLinesFilterRender());
                break;
            case R.id.image:
                uzLivestream.setImageToStream(R.mipmap.ic_launcher, TranslateTo.RIGHT);
                break;
            case R.id.image_70s:
                uzLivestream.setFilter(new Image70sFilterRender());
                break;
            case R.id.lamoish:
                uzLivestream.setFilter(new LamoishFilterRender());
                break;
            case R.id.money:
                uzLivestream.setFilter(new MoneyFilterRender());
                break;
            case R.id.negative:
                uzLivestream.setFilter(new NegativeFilterRender());
                break;
            case R.id.pixelated:
                uzLivestream.setFilter(new PixelatedFilterRender());
                break;
            case R.id.polygonization:
                uzLivestream.setFilter(new PolygonizationFilterRender());
                break;
            case R.id.rainbow:
                uzLivestream.setFilter(new RainbowFilterRender());
                break;
            case R.id.rgb_saturate:
                RGBSaturationFilterRender rgbSaturationFilterRender = new RGBSaturationFilterRender();
                uzLivestream.setFilter(rgbSaturationFilterRender);
                //Reduce green and blue colors 20%. Red will predominate.
                rgbSaturationFilterRender.setRGBSaturation(1f, 0.8f, 0.8f);
                break;
            case R.id.ripple:
                uzLivestream.setFilter(new RippleFilterRender());
                break;
            case R.id.rotation:
                RotationFilterRender rotationFilterRender = new RotationFilterRender();
                uzLivestream.setFilter(rotationFilterRender);
                rotationFilterRender.setRotation(90);
                break;
            case R.id.saturation:
                uzLivestream.setFilter(new SaturationFilterRender());
                break;
            case R.id.sepia:
                uzLivestream.setFilter(new SepiaFilterRender());
                break;
            case R.id.sharpness:
                uzLivestream.setFilter(new SharpnessFilterRender());
                break;
            case R.id.surface_filter:
                //You can render this btFilter with other api that draw in a surface. for example you can use VLC
                SurfaceFilterRender surfaceFilterRender = new SurfaceFilterRender();
                uzLivestream.setFilter(surfaceFilterRender);
                MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.big_bunny_240p);
                mediaPlayer.setSurface(surfaceFilterRender.getSurface());
                mediaPlayer.start();
                //Video is 360x240 so select a percent to keep aspect ratio (50% x 33.3% screen)
                surfaceFilterRender.setScale(50f, 33.3f);
                surfaceFilterRender.setListeners(uzLivestream.getOpenGlView()); //Optional
                break;
            case R.id.temperature:
                uzLivestream.setFilter(new TemperatureFilterRender());
                break;
            case R.id.text:
                uzLivestream.setTextToStream("Hello Uiza", 40, Color.RED, TranslateTo.CENTER);
                break;
            case R.id.zebra:
                uzLivestream.setFilter(new ZebraFilterRender());
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_start_stop:
                if (!uzLivestream.isStreaming()) {
                    if (uzLivestream.prepareAudio() && uzLivestream.prepareVideoHD(false)) {
                        uzLivestream.startStream(uzLivestream.getMainStreamUrl());
                    } else {
                        LToast.show(activity, getString(R.string.err_dont_support));
                    }
                } else {
                    bStartStop.setText(R.string.start_button);
                    uzLivestream.stopStream();
                }
                if (uzLivestream.isStreaming()) {
                    bStartStop.setText("Stop streaming");
                    bStartStopStore.setEnabled(false);
                } else {
                    bStartStop.setText("Start streaming");
                    bStartStopStore.setEnabled(true);
                }
                break;
            case R.id.b_start_stop_store:
                if (!uzLivestream.isStreaming()) {
                    if (uzLivestream.prepareAudio() && uzLivestream.prepareVideoHD(false)) {
                        uzLivestream.startStream(uzLivestream.getMainStreamUrl(), true);
                    } else {
                        LToast.show(activity, "Cannot start");
                    }
                } else {
                    bStartStopStore.setText(R.string.start_button);
                    uzLivestream.stopStream();
                }
                if (uzLivestream.isStreaming()) {
                    bStartStopStore.setText("Stop streaming");
                    bStartStop.setEnabled(false);
                } else {
                    bStartStopStore.setText("Start stream and Store");
                    bStartStop.setEnabled(true);
                }
                break;
            case R.id.b_switch_camera:
                uzLivestream.switchCamera();
                break;
            case R.id.b_filter:
                LPopupMenu.show(activity, btFilter, R.menu.gl_menu, new LPopupMenu.CallBack() {
                    @Override
                    public void clickOnItem(MenuItem menuItem) {
                        handleFilterClick(menuItem);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LToast.show(activity, reason);
            }
        });
    }

    @Override
    public void onGetDataSuccess(Data d, String mainUrl, boolean isTranscode, PresetLiveStreamingFeed presetLiveStreamingFeed) {
        LLog.d(TAG, "onGetDataSuccess " + LSApplication.getInstance().getGson().toJson(presetLiveStreamingFeed));
        bStartStop.setEnabled(true);
        bStartStopStore.setEnabled(true);
        btSwitchCamera.setEnabled(true);
        btFilter.setEnabled(true);
        tvMainUrl.setText(mainUrl);
    }

    @Override
    public void onConnectionSuccessRtmp() {
    }

    @Override
    public void onConnectionFailedRtmp(String reason) {
    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bStartStop.setEnabled(true);
                bStartStopStore.setEnabled(true);
                btSwitchCamera.setEnabled(true);
                btFilter.setEnabled(true);
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
    }

    @Override
    public void onAuthSuccessRtmp() {
    }

    @Override
    public void surfaceCreated() {
    }

    @Override
    public void surfaceChanged(UZLivestream.StartPreview startPreview) {
        startPreview.onSizeStartPreview(1280, 720);
    }
}
