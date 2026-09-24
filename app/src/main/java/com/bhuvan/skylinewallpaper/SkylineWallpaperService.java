package
com.bhuvan.skylinewallpaper;

import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.graphics.Canvas;
import android.graphics.Color;

public class SkylineWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new SkylineEngine();
    }

    private class SkylineEngine extends Engine {

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                drawWallpaper();
            }
        }

        @Override
        public void onSurfaceChanged(
                SurfaceHolder holder,
                int format,
                int width,
                int height) {
            drawWallpaper();
        }

        private void drawWallpaper() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();

                if (canvas != null) {
                    canvas.drawColor(Color.BLACK);
                }

            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
                  }
