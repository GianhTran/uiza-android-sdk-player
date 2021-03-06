package vn.uiza.libstream.uiza.rtplibrary.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Surface;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import vn.uiza.libstream.uiza.encoder.input.gl.SurfaceManager;
import vn.uiza.libstream.uiza.encoder.input.gl.render.ManagerRender;
import vn.uiza.libstream.uiza.encoder.input.gl.render.filters.BaseFilterRender;
import vn.uiza.libstream.uiza.encoder.utils.gl.GlUtil;

/**
 * Created by pedro on 4/03/18.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class OffScreenGlThread
        implements GlInterface, Runnable, SurfaceTexture.OnFrameAvailableListener {

    private final Context context;
    private final Semaphore semaphore = new Semaphore(0);
    private final BlockingQueue<Filter> filterQueue = new LinkedBlockingQueue<>();
    private final Object sync = new Object();
    private Thread thread = null;
    private boolean frameAvailable = false;
    private boolean running = true;
    private boolean initialized = false;
    private SurfaceManager surfaceManager = null;
    private SurfaceManager surfaceManagerEncoder = null;
    private ManagerRender textureManager = null;
    private int encoderWidth, encoderHeight;
    private boolean loadAA = false;

    private boolean AAEnabled = false;
    private int waitTime = 10;
    //used with camera
    private boolean isCamera2Landscape = false;
    private boolean onChangeFace = false;
    private boolean isFrontCamera = false;
    private TakePhotoCallback takePhotoCallback;

    public OffScreenGlThread(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        if (!initialized) textureManager = new ManagerRender();
        initialized = true;
    }

    @Override
    public void setEncoderSize(int width, int height) {
        this.encoderWidth = width;
        this.encoderHeight = height;
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return textureManager.getSurfaceTexture();
    }

    @Override
    public Surface getSurface() {
        return textureManager.getSurface();
    }

    @Override
    public void addMediaCodecSurface(Surface surface) {
        synchronized (sync) {
            surfaceManagerEncoder = new SurfaceManager(surface, surfaceManager);
        }
    }

    @Override
    public void removeMediaCodecSurface() {
        synchronized (sync) {
            if (surfaceManagerEncoder != null) {
                surfaceManagerEncoder.release();
                surfaceManagerEncoder = null;
            }
        }
    }

    @Override
    public void takePhoto(TakePhotoCallback takePhotoCallback) {
        this.takePhotoCallback = takePhotoCallback;
    }

    @Override
    public void setFilter(int filterPosition, BaseFilterRender baseFilterRender) {
        filterQueue.add(new Filter(filterPosition, baseFilterRender));
    }

    @Override
    public void setFilter(BaseFilterRender baseFilterRender) {
        setFilter(0, baseFilterRender);
    }

    @Override
    public void enableAA(boolean AAEnabled) {
        this.AAEnabled = AAEnabled;
        loadAA = true;
    }

    @Override
    public void setCameraFace(boolean frontCamera) {
        onChangeFace = true;
        isFrontCamera = frontCamera;
    }

    @Override
    public boolean isAAEnabled() {
        return textureManager != null && textureManager.isAAEnabled();
    }

    @Override
    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    @Override
    public void start(boolean isCamera2Landscape) {
        this.isCamera2Landscape = isCamera2Landscape;
        thread = new Thread(this);
        running = true;
        thread.start();
        semaphore.acquireUninterruptibly();
    }

    @Override
    public void stop() {
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join(1000);
            } catch (InterruptedException e) {
                thread.interrupt();
            }
            thread = null;
        }
        running = false;
    }

    @Override
    public void run() {
        surfaceManager = new SurfaceManager();
        surfaceManager.makeCurrent();
        textureManager.setStreamSize(encoderWidth, encoderHeight);
        textureManager.initGl(encoderWidth, encoderHeight, isCamera2Landscape, context);
        textureManager.getSurfaceTexture().setOnFrameAvailableListener(this);
        semaphore.release();
        try {
            while (running) {
                synchronized (sync) {
                    sync.wait(waitTime);
                    if (frameAvailable) {
                        frameAvailable = false;
                        surfaceManager.makeCurrent();
                        textureManager.updateFrame();
                        textureManager.drawOffScreen();
                        textureManager.drawScreen(encoderWidth, encoderHeight, false, false);
                        surfaceManager.swapBuffer();
                        if (takePhotoCallback != null) {
                            takePhotoCallback.onTakePhoto(
                                    GlUtil.getBitmap(encoderWidth, encoderHeight, encoderWidth, encoderHeight));
                            takePhotoCallback = null;
                        }

                        synchronized (sync) {
                            if (surfaceManagerEncoder != null) {
                                surfaceManagerEncoder.makeCurrent();
                                textureManager.drawScreen(encoderWidth, encoderHeight, false, false);
                                long ts = textureManager.getSurfaceTexture().getTimestamp();
                                surfaceManagerEncoder.setPresentationTime(ts);
                                surfaceManagerEncoder.swapBuffer();
                            }
                        }
                    }
                    if (!filterQueue.isEmpty()) {
                        Filter filter = filterQueue.poll();
                        textureManager.setFilter(filter.getPosition(), filter.getBaseFilterRender());
                    } else if (loadAA) {
                        textureManager.enableAA(AAEnabled);
                        loadAA = false;
                    } else if (onChangeFace) {
                        textureManager.faceChanged(isFrontCamera);
                        onChangeFace = false;
                    }
                }
            }
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        } finally {
            surfaceManager.release();
            textureManager.release();
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (sync) {
            frameAvailable = true;
            sync.notifyAll();
        }
    }
}
