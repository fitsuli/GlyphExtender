
package com.nothing.thirdparty;

interface IGlyphService {
    void setFrameColors(in int[] colors);
    void openSession();
    void closeSession();
    boolean register(String key);
    boolean registerSDK(String key, String device);
}