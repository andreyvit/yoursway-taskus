package com.mkalugin.corchy.internal.images;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.mkalugin.corchy.ui.core.CorchyUIPlugin;

public class CorchyImages {

	private static final String IMAGES_PATH = "/images"; 

	public static final ManagedImage ICN_SYNC = new LazyImage("sync.png"); 	

	private static ImageRegistry imageRegistry = null;

	private static ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry();
		}
		return imageRegistry;
	}

	private static class LazyImage implements ManagedImage {

		private final String relativePath;

		public LazyImage(String relativePath) {
			if (relativePath == null)
				throw new NullPointerException("relativePath is null");
			this.relativePath = relativePath;
		}

		public Image get() {
			ImageRegistry registry = getImageRegistry();
			Image image = registry.get(relativePath);
			if (image == null) {
				URL entry = CorchyUIPlugin.instance().getBundle().getEntry(
						new File(IMAGES_PATH, relativePath).getPath());
				InputStream stream;
				try {
					stream = entry.openStream();
					try {
						image = new Image(Display.getCurrent(), stream);
					} finally {
						stream.close();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				if (image != null)
					registry.put(relativePath, image);
				else 
					throw new AssertionError("Failed to load application image: " + relativePath);
			}
			return image;
		}
	}

}
