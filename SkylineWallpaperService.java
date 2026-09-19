package com.bhuvan.skylinewallpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.util.Calendar;
import java.util.Random;

public class SkylineWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new SkylineEngine();
    }

    private class SkylineEngine extends Engine {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        private Bitmap bitmap;
        private boolean visible;
        private float xOffset = 0.5f;

        private final Runnable drawRunnable = new Runnable() {
            @Override public void run() {
                drawFrame();
            }
        };

        SkylineEngine() {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.skyline_wallpaper);
        }

        @Override
        public void onVisibilityChanged(boolean v) {
            visible = v;
            if (v) drawFrame();
            else handler.removeCallbacks(drawRunnable);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            drawFrame();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            visible = false;
            handler.removeCallbacks(drawRunnable);
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xStep, float yStep,
                                     int xPixels, int yPixels) {
            this.xOffset = xOffset;
            drawFrame();
        }

        private void drawFrame() {
            if (!visible) return;
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas == null) return;

                int w = canvas.getWidth();
                int h = canvas.getHeight();

                // Center-crop the original 2:3 image to the phone's tall 20:9-ish portrait screen.
                float scale = Math.max((float) w / bitmap.getWidth(),
                                       (float) h / bitmap.getHeight());
                float dw = bitmap.getWidth() * scale;
                float dh = bitmap.getHeight() * scale;
                float left = (w - dw) * 0.5f + (xOffset - 0.5f) * Math.min(80f, Math.abs(dw - w));
                float top = (h - dh) * 0.5f;

                paint.setAlpha(255);
                paint.setShader(null);
                canvas.drawColor(Color.BLACK);
                canvas.drawBitmap(bitmap, null,
                        new android.graphics.RectF(left, top, left + dw, top + dh), paint);

                Calendar now = Calendar.getInstance();
                float minutes = now.get(Calendar.HOUR_OF_DAY) * 60f
                        + now.get(Calendar.MINUTE)
                        + now.get(Calendar.SECOND) / 60f;

                drawAtmosphere(canvas, w, h, minutes);

            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas);
            }

            // Update once per minute; the transitions themselves are continuous based on the clock.
            handler.removeCallbacks(drawRunnable);
            handler.postDelayed(drawRunnable, 60_000L);
        }

        private void drawAtmosphere(Canvas c, int w, int h, float m) {
            // Local-time schedule. These are intentionally configurable constants.
            final float dawn = 5 * 60f;
            final float sunriseEnd = 7 * 60f;
            final float sunsetStart = 16.5f * 60f;
            final float sunsetEnd = 19 * 60f;
            final float nightEnd = 5 * 60f;

            if (m >= sunriseEnd && m < sunsetStart) {
                // Bright daytime: subtle blue atmospheric wash.
                paint.setShader(new LinearGradient(0, 0, 0, h * 0.7f,
                        Color.argb(12, 80, 150, 255),
                        Color.TRANSPARENT, Shader.TileMode.CLAMP));
                c.drawRect(0, 0, w, h, paint);
                paint.setShader(null);
                return;
            }

            if (m >= sunsetStart && m < sunsetEnd) {
                float t = (m - sunsetStart) / (sunsetEnd - sunsetStart);
                // Fade the warm scene into a cooler dusk.
                int alpha = (int)(20 + 100 * t);
                paint.setColor(Color.argb(alpha, 255, 100, 20));
                c.drawRect(0, 0, w, h, paint);
                return;
            }

            // Night / pre-dawn.
            float nightAmount;
            if (m >= sunsetEnd) {
                nightAmount = Math.min(1f, (m - sunsetEnd) / 120f);
            } else {
                nightAmount = Math.min(1f, (dawn - m + 24 * 60f) / 120f);
            }
            if (m >= dawn && m < sunriseEnd) {
                nightAmount = Math.max(0f, 1f - (m - dawn) / (sunriseEnd - dawn));
            }

            int alpha = (int)(110 + 80 * nightAmount);
            paint.setColor(Color.argb(alpha, 4, 12, 38));
            c.drawRect(0, 0, w, h, paint);

            // Subtle blue-violet sky gradient.
            paint.setShader(new LinearGradient(0, 0, 0, h * 0.72f,
                    Color.argb((int)(95 * nightAmount), 20, 35, 110),
                    Color.argb((int)(20 * nightAmount), 5, 8, 25),
                    Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, paint);
            paint.setShader(null);

            drawStars(c, w, h, nightAmount);
            drawMoon(c, w, h, m, nightAmount);
        }

        private void drawStars(Canvas c, int w, int h, float amount) {
            if (amount < 0.15f) return;
            Random r = new Random(20260919L);
            paint.setColor(Color.WHITE);
            for (int i = 0; i < 70; i++) {
                float x = r.nextFloat() * w;
                float y = r.nextFloat() * h * 0.43f;
                float radius = 0.6f + r.nextFloat() * 1.5f;
                paint.setAlpha((int)(amount * (80 + r.nextInt(140))));
                c.drawCircle(x, y, radius, paint);
            }
            paint.setAlpha(255);
        }

        private void drawMoon(Canvas c, int w, int h, float m, float amount) {
            if (amount < 0.1f) return;

            // Moon travels across the upper sky from right after moonrise to left before moonset.
            float t;
            if (m >= 19 * 60f) t = (m - 19 * 60f) / (10 * 60f);
            else t = (m + 5 * 60f) / (14 * 60f);
            t = Math.max(0f, Math.min(1f, t));

            float x = w * (0.84f - 0.68f * t);
            float y = h * (0.20f + 0.10f * (float)Math.sin(Math.PI * t));

            paint.setColor(Color.argb((int)(235 * amount), 245, 245, 235));
            c.drawCircle(x, y, Math.max(18f, w * 0.022f), paint);

            // Soft halo.
            paint.setShader(new android.graphics.RadialGradient(
                    x, y, w * 0.09f,
                    new int[]{Color.argb((int)(55 * amount), 220, 230, 255),
                              Color.TRANSPARENT},
                    null, Shader.TileMode.CLAMP));
            c.drawCircle(x, y, w * 0.09f, paint);
            paint.setShader(null);
            paint.setAlpha(255);
        }
    }
}
