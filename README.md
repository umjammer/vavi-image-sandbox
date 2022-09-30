[![Release](https://jitpack.io/v/umjammer/vavi-image-sandbox.svg)](https://jitpack.io/#umjammer/vavi-image-sandbox)
[![Java CI](https://github.com/umjammer/vavi-image-sandbox/workflows/Java%20CI/badge.svg)](https://github.com/umjammer/vavi-image-sandbox/actions)
[![CodeQL](https://github.com/umjammer/vavi-image-sandbox/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-image-sandbox/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-8-b07219)

# vavi-image-sandbox

🎨 Imaging the world more!

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
[© oscd.jp](https://www.oscd.jp/)

  * [watermark removing](https://github.com/umjammer/vavi-image-sandbox/wiki/WatermarkRemoval)

  * jpeg analysis

  * packing

    * arevalo
    * binpack

  * image quality

    * https://github.com/google/butteraugli

## TODO

 * https://github.com/haraldk/TwelveMonkeys
 * https://github.com/eug/imageio-pnm
 * http://thorntonzone.com/manuals/Compression/Fax,%20IBM%20MMR/MMSC/mmsc/uk/co/mmscomputing/imageio/ppm/index.html
 * https://github.com/sejda-pdf/webp-imageio
 * [CIFilter](https://developer.apple.com/documentation/coreimage/cifilter)
 * https://github.com/sksamuel/scrimage (filter)
