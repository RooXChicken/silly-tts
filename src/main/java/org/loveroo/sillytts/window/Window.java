package org.loveroo.sillytts.window;

public enum Window {
    
    TTS_INPUT(460, 160);

    private final int width;
    private final int height;

    Window(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
