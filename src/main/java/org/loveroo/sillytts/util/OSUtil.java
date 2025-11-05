package org.loveroo.sillytts.util;

public class OSUtil {
    
    private static final OSType osType;

    static {
        var osName = System.getProperty("os.name").toLowerCase();

        if(osName.contains("win")) {
            osType = OSType.WINDOWS;
        }
        else if(osName.contains("mac")) {
            osType = OSType.MACOS;
        }
        else if(osName.contains("nux") || osName.contains("nix")) {
            osType = OSType.LINUX;
        }
        else {
            osType = OSType.OTHER;
        }
    }

    public static OSType getOs() {
        return osType;
    }

    public static enum OSType {
        WINDOWS,
        MACOS,
        LINUX,
        OTHER,
    }

}
