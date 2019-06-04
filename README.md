[![Release](https://jitpack.io/v/umjammer/vavi-image-sandbox.svg)](https://jitpack.io/#umjammer/vavi-image-sandbox)

# vavi-image-sandbox

Vavi Image API Sandbox

## formats

  * jpeg (simd) (WIP)
  * qrcode
  * barcode
  * heif

## filter

  * pixel operation

    * cropping
    * rotation
    * draw

  * quantizing

    * fixed color model
    * oct tree ([vs neural net](https://github.com/umjammer/vavi-image-sandbox/wiki/OctTree_vs_NeuralNet))
    * Imagemagick
    * neural net ([vs oct tree](https://github.com/umjammer/vavi-image-sandbox/wiki/OctTree_vs_NeuralNet))

  * resampling

    * HexeReinLanczos
    * ZhomxLanczos

  * enlargement

    * smilla
    * noids

  * [Pseudo Colorization](https://github.com/umjammer/vavi-apps-pseudocolorization/wiki) ([android version](https://github.com/umjammer/vavi-apps-pseudocolorization))
  * [Imagemagick](https://github.com/umjammer/vavi-image-sandbox/wiki/ImageMagickFilter)
  * flood filling

## Others

  * gif animation renderer

    * [JAnimationFrame](https://github.com/umjammer/vavi-image-sandbox/blob/master/src/test/java/JAnimationFrame.java)

as a desktop mascot

![JAnimationWindow](https://lh3.googleusercontent.com/d3wp6hzuILHq6MT7Ud_gUi_TpqYIK1UiT-m9C03rndcpPzFLwmPXpUkaEjLobQpb-vnXLR1l8eKdwHNUF0xJUjLXnEP5Fc9oOM1NoElCZ5u2AJoOKWLqsAoNEzHWBIPaSffQM1X11w=w2400)

  * [watermark removing](https://github.com/umjammer/vavi-image-sandbox/wiki/WatermarkRemoval)

  * jpeg analysis

  * packing

    * arevalo
    * binpack
