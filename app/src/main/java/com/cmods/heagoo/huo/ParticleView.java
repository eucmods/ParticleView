/**
 * @Author 
 * @AIDE AIDE+
*/
package com.cmods.heagoo.huo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cmods.heagoo.huo.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.support.annotation.*;

public class ParticleView extends SurfaceView implements SurfaceHolder.Callback {

    private final List<Particle> particles = new ArrayList<>();
    private SurfaceViewThread surfaceViewThread;
    private boolean hasSurface = false;
    private boolean hasSetup = false;

    private final Path path = new Path();

    // Attribute Defaults
    private int _particleCount = 20;

    @Dimension
    private int _particleMinRadius = 5;

    @Dimension
    private int _particleMaxRadius = 10;

    @ColorInt
    private int _particlesBackgroundColor = Color.BLACK;

    @ColorInt
    private int _particleColor = Color.WHITE;

    @ColorInt
    private int _particleLineColor = Color.WHITE;

    private boolean _particleLinesEnabled = true;

    // Paints
    private final Paint paintParticles = new Paint();
    private final Paint paintLines = new Paint();

    public ParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainStyledAttributes(attrs, R.attr.ParticleViewStyle);
        getHolder().addCallback(this);
        hasSurface = false;

        paintParticles.setAntiAlias(true);
        paintParticles.setStyle(Paint.Style.FILL);
        paintParticles.setStrokeWidth(2F);

        paintLines.setAntiAlias(true);
        paintLines.setStyle(Paint.Style.FILL_AND_STROKE);
        paintLines.setStrokeWidth(2F);
    }

    private void obtainStyledAttributes(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ParticleView, defStyleAttr, 0);

        try {
            _particleCount = typedArray.getInt(R.styleable.ParticleView_particleCount, _particleCount);
            _particleMinRadius = typedArray.getInt(R.styleable.ParticleView_particleMinRadius, _particleMinRadius);
            _particleMaxRadius = typedArray.getInt(R.styleable.ParticleView_particleMaxRadius, _particleMaxRadius);
            _particlesBackgroundColor = typedArray.getColor(R.styleable.ParticleView_particlesBackgroundColor, _particlesBackgroundColor);
            _particleColor = typedArray.getColor(R.styleable.ParticleView_particleColor, _particleColor);
            _particleLineColor = typedArray.getColor(R.styleable.ParticleView_particleLineColor, _particleLineColor);
            _particleLinesEnabled = typedArray.getBoolean(R.styleable.ParticleView_particleLinesEnabled, _particleLinesEnabled);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        hasSurface = true;
        if (surfaceViewThread == null) {
            surfaceViewThread = new SurfaceViewThread();
        }
        surfaceViewThread.start();
    }

    public void resume() {
        if (surfaceViewThread == null) {
            surfaceViewThread = new SurfaceViewThread();
            if (hasSurface) {
                surfaceViewThread.start();
            }
        }
    }

    public void pause() {
        if (surfaceViewThread != null) {
            surfaceViewThread.requestExitAndWait();
            surfaceViewThread = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        if (surfaceViewThread != null) {
            surfaceViewThread.requestExitAndWait();
            surfaceViewThread = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Ignorado
    }

    private void setupParticles() {
        if (!hasSetup) {
            hasSetup = true;
            particles.clear();
            Random random = new Random();

            for (int i = 0; i < _particleCount; i++) {
                particles.add(new Particle(
								  random.nextInt(_particleMaxRadius - _particleMinRadius + 1) + _particleMinRadius,
								  random.nextInt(getWidth()),
								  random.nextInt(getHeight()),
								  random.nextInt(5) - 2,
								  random.nextInt(5) - 2,
								  random.nextInt(106) + 150
							  ));
            }
        }
    }

    private class SurfaceViewThread extends Thread {
        private boolean running = true;
        private Canvas canvas;

        @Override
        public void run() {
            setupParticles();

            while (running) {
                try {
                    canvas = getHolder().lockCanvas();
                    synchronized (getHolder()) {
                        if (canvas != null) {
                            canvas.drawColor(_particlesBackgroundColor, PorterDuff.Mode.SRC);

                            for (int i = 0; i < _particleCount; i++) {
                                Particle p = particles.get(i);
                                p.x += p.vx;
                                p.y += p.vy;

                                if (p.x < 0) {
                                    p.x = getWidth();
                                } else if (p.x > getWidth()) {
                                    p.x = 0;
                                }

                                if (p.y < 0) {
                                    p.y = getHeight();
                                } else if (p.y > getHeight()) {
                                    p.y = 0;
                                }

                                if (_particleLinesEnabled) {
                                    for (int j = 0; j < _particleCount; j++) {
                                        if (i != j) {
                                            linkParticles(canvas, p, particles.get(j));
                                        }
                                    }
                                }

                                paintParticles.setAlpha(p.alpha);
                                canvas.drawCircle(p.x, p.y, p.radius, paintParticles);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        getHolder().unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        public void requestExitAndWait() {
            running = false;
            try {
                join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    private float dx, dy, dist, distRatio;

    private void linkParticles(Canvas canvas, Particle p1, Particle p2) {
        dx = p1.x - p2.x;
        dy = p1.y - p2.y;
        dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist < 220) {
            path.moveTo(p1.x, p1.y);
            path.lineTo(p2.x, p2.y);
            distRatio = (220 - dist) / 220;

            paintLines.setAlpha((int) (Math.min(p1.alpha, p2.alpha) * distRatio / 2));
            canvas.drawPath(path, paintLines);
            path.reset();
        }
    }
}


