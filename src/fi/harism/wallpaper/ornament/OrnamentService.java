package fi.harism.wallpaper.ornament;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.service.wallpaper.WallpaperService.Engine;
import android.view.SurfaceHolder;

public class OrnamentService extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
		return new WallpaperEngine();
	}
	
	/**
	 * Private wallpaper engine implementation.
	 */
	private final class WallpaperEngine extends Engine  {

		// Slightly modified GLSurfaceView.
		private WallpaperGLSurfaceView mGLSurfaceView;
		private OrnamentRenderer mRenderer;

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {

			// Uncomment for debugging.
			// android.os.Debug.waitForDebugger();

			super.onCreate(surfaceHolder);
			mRenderer = new OrnamentRenderer(OrnamentService.this);
			mGLSurfaceView = new WallpaperGLSurfaceView(OrnamentService.this);
			mGLSurfaceView.setEGLContextClientVersion(2);
			mGLSurfaceView
					.setEGLConfigChooser(new OrnamentEGLConfigChooser(false));
			mGLSurfaceView.setRenderer(mRenderer);
			mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mGLSurfaceView.onDestroy();
			mGLSurfaceView = null;
			mRenderer = null;
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
			mRenderer.setOffset(xOffset, yOffset);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if (visible) {
				mGLSurfaceView.onResume();
			} else {
				mGLSurfaceView.onPause();
			}
		}

		/**
		 * Lazy as I am, I din't bother using GLWallpaperService (found on
		 * GitHub) project for wrapping OpenGL functionality into my wallpaper
		 * service. Instead am using GLSurfaceView and trick it into hooking
		 * into Engine provided SurfaceHolder instead of SurfaceView provided
		 * one GLSurfaceView extends.
		 */
		private final class WallpaperGLSurfaceView extends GLSurfaceView {
			public WallpaperGLSurfaceView(Context context) {
				super(context);
			}

			@Override
			public SurfaceHolder getHolder() {
				return WallpaperEngine.this.getSurfaceHolder();
			}

			/**
			 * Should be called once underlying Engine is destroyed. Calling
			 * onDetachedFromWindow() will stop rendering thread which is lost
			 * otherwise.
			 */
			public void onDestroy() {
				super.onDetachedFromWindow();
			}
		}
	}
	
	
}