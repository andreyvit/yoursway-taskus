package com.mkalugin.corchy.internal.ui.images;

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
    
    public static final ManagedImage IMG_SYNC = new LazyImage("syncarrow.png");
    public static final ManagedImage IMG_LEFT_ARROW = new LazyImage("left.png");
    public static final ManagedImage IMG_RIGHT_ARROW = new LazyImage("right.png");
    public static final ManagedImage IMG_KEYCHAIN = new LazyImage("keychain.png");
    public static final ManagedImage IMG_BOTTOM_BAR_BG = new LazyImage("bbbg.png");
    
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
                String path = new File(IMAGES_PATH, relativePath).getPath();
                path = path.replace('\\', '/');
                URL entry = CorchyUIPlugin.instance().getBundle().getEntry(path);
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
