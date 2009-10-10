
#include <jni.h>
#include "vavix_awt_image_quantize_NeuralNetQuantizerOP.h"

/**
 * Class:     vavix_awt_image_quantize_NeuralNetQuantizerOP
 * Method:    init
 * Signature: (Ljava/awt/image/BufferedImage;III)V
 */
JNIEXPORT void JNICALL Java_vavix_awt_image_quantize_NeuralNetQuantizerOP_init
  (JNIEnv *env, jobject obj, jintArray image, jint width, jint height, jint colors) {

    initnet(pic, 3 * width * height, samplefac);
    learn();
    unbiasnet();
    inxbuild();
}

/**
 * Class:     vavix_awt_image_quantize_NeuralNetQuantizerOP
 * Method:    convert
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_vavix_awt_image_quantize_NeuralNetQuantizerOP_convert
  (JNIEnv *env, jobject obj, jint rgb) {

    return inxsearch(b, g, r);
}

/* */
