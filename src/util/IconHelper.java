package jtaskui.util;

import javax.swing.ImageIcon;
import java.awt.Image;
import javax.imageio.ImageIO;

public class IconHelper {

    /**
     * Helper method to fetch icons as ImageIcon objects.
     *
     * @param iconPath - String path to the icon file.
     * @return ImageIcon - ImageIcon object with specified icon file loaded.
     */
    // TODO: Fix where the icons come from. At the moment this expects all items to be relative to the class path
    public static ImageIcon getIcon(String iconPath) {
        Image img = null;
        try {
            img = ImageIO.read(IconHelper.class.getClassLoader().getSystemResource(iconPath));
        } catch (Exception ex) {
            System.out.println(ex);
        }

        // Check to see if the Image object is still null
        if(img != null) {
            // If the Image has been created, return a new ImageIcon
            return new ImageIcon(img);
        }
        // If the img is still null then there must have been an exception
        else {
            // Print some helpful messages to indicate what might have gone wrong
            System.err.println("ERROR: Cannot find/open icon at path " + iconPath);
            System.err.println("       If the icon is present and readable, check there is available free space in the tmp dir currently specified at " + System.getProperty("java.io.tmpdir"));
            // Return null, the icon will just be missing but everything should continue to work
            return null;
        }  
    }
}
