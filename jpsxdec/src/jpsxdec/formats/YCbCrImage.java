/*
 * jPSXdec: PlayStation 1 Media Decoder/Converter in Java
 * Copyright (C) 2007-2020  Michael Sabin
 * All rights reserved.
 *
 * Redistribution and use of the jPSXdec code or any derivative works are
 * permitted provided that the following conditions are met:
 *
 *  * Redistributions may not be sold, nor may they be used in commercial
 *    or revenue-generating business activities.
 *
 *  * Redistributions that are modified from the original source must
 *    include the complete source code, including the source code for all
 *    components used by a binary built from the modified sources. However, as
 *    a special exception, the source code distributed need not include
 *    anything that is normally distributed (in either source or binary form)
 *    with the major components (compiler, kernel, and so on) of the operating
 *    system on which the executable runs, unless that component itself
 *    accompanies the executable.
 *
 *  * Redistributions must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jpsxdec.formats;

import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;

/** Basic YCbCr image format with 4:2:0 chroma subsampling.
 * That means there are 4 luma pixels for each chroma blue and chroma red pixel.
 *<p>
 * This class does not interpret the data, so it can hold either the Rec.601
 * "color space"...
 *<pre>
 * Y : 16 to 235
 * Cb: 16 to 240
 * Cr: 16 to 240
 *</pre>
 *<blockquote>
 *  "In each 8 bit luminance sample, the value 16 is used for black and
 *  235 for white, to allow for overshoot and undershoot. The values 0
 *  and 255 are used for sync encoding. The Cb and Cr samples use the
 *  value 128 to encode a zero value, as used when encoding a white,
 *  grey or black area."
 *</blockquote>
 *      -http://en.wikipedia.org/wiki/Rec._601
 * <p>
 * ...or it can handle the PSX "color space", or the JPEG/JFIF "color space":
 *<pre>
 * Y : 0 to 255
 * Cb: 0 to 255
 * Cr: 0 to 255
 *</pre>
 * Internally however, the values are stored as unsigned bytes, so shift
 * the values by -128.
 * <p>
 * This is intended as the final step in the decoding process, to store the
 * data before sending it to an AVI writer.
 */
public class YCbCrImage {

    private int _iLumaWidth;
    private int _iLumaHeight;
    private int _iChromaWidth;
    private int _iChromaHeight;

    /** Holds luminance values. */
    @Nonnull
    private final byte[] _abY;
    /** Holds chroma blue values with 4:2:0 subsampling. */
    @Nonnull
    private final byte[] _abCb;
    /** Holds chorma red values with 4:2:0 subsampling. */
    @Nonnull
    private final byte[] _abCr;

    /** Creates a new instance of Rec601YCbCrImage
     * @param iWidth   Width of image (in luma pixels)
     * @param iHeight  Height of image (in luma pixels) */
    public YCbCrImage(int iWidth, int iHeight) {
        if (iWidth < 2 || iHeight < 2 ||
               (iWidth % 2) != 0 ||
               (iHeight % 2) != 0) {
            throw new IllegalArgumentException("Dimensions must be even.");
        }
        _iLumaWidth  = iWidth;
        _iLumaHeight = iHeight;
        _iChromaWidth = iWidth / 2;
        _iChromaHeight = iHeight / 2;
        _abY  = new byte[_iLumaWidth * _iLumaHeight];
        _abCb = new byte[_iChromaWidth * _iChromaHeight];
        _abCr = new byte[_abCb.length];
    }

    /** Converts RGB to YCbCr as Rec.601. */
    public YCbCrImage(@Nonnull BufferedImage rgb) {
        this(rgb.getWidth(), rgb.getHeight());

        for (int x = 0; x < _iLumaWidth; x+=2) {
            for (int y = 0; y < _iLumaHeight; y+=2) {
                Rec601YCbCr ycc = new Rec601YCbCr(new RGB(rgb.getRGB(x  , y  )),
                                                  new RGB(rgb.getRGB(x+1, y  )),
                                                  new RGB(rgb.getRGB(x  , y+1)),
                                                  new RGB(rgb.getRGB(x+1, y+1))
                                                 );
                _abY[ (x  ) + (y  ) * _iLumaWidth ] = rc(ycc.y1);
                _abY[ (x+1) + (y  ) * _iLumaWidth ] = rc(ycc.y2);
                _abY[ (x  ) + (y+1) * _iLumaWidth ] = rc(ycc.y3);
                _abY[ (x+1) + (y+1) * _iLumaWidth ] = rc(ycc.y4);
                _abCb[x/2 + (y/2) * _iChromaWidth] = rc(ycc.cb);
                _abCr[x/2 + (y/2) * _iChromaWidth] = rc(ycc.cr);
            }
        }
    }

    /** Clamp & Round */
    private static byte rc(double dbl) {
        if (dbl < 0)
            return 0;
        else if (dbl > 255)
            return (byte)255;
        else
            return (byte)Math.round(dbl);
    }

    public int getWidth() {
        return _iLumaWidth;
    }

    public int getHeight() {
        return _iLumaHeight;
    }

    public @Nonnull byte[] getY() {
        return _abY;
    }
    public @Nonnull byte[] getCb() {
        return _abCb;
    }
    public @Nonnull byte[] getCr() {
        return _abCr;
    }

    /** Sets a luminance value.
     * @param iLumaX  X luma pixel to set.
     * @param iLumaY  Y luma pixel to set.
     * @param bY     New value.
     */
    public void setY(int iLumaX, int iLumaY, byte bY) {
        _abY[iLumaX + iLumaY * _iLumaWidth] = bY;
    }
    /** Sets chrominance blue value.
     * @param iChromaX  X chroma pixel (1/2 luma width)
     * @param iChromaY  Y chroma pixel (1/2 luma width)
     * @param bCb    New value.
     */
    public void setCb(int iChromaX, int iChromaY, byte bCb) {
        _abCb[iChromaX + iChromaY * _iChromaWidth] = bCb;
    }
    /** Sets chrominance red value.
     * @param iChromaX  X chroma pixel (1/2 luma width)
     * @param iChromaY  Y chroma pixel (1/2 luma width)
     * @param bCr    New value.
     */
    public void setCr(int iChromaX, int iChromaY, byte bCr) {
        _abCr[iChromaX + iChromaY * _iChromaWidth] = bCr;
    }

    /** Set a block of luma values
     * @param iDestX  Top left corner where block starts (in luma pixels)
     * @param iDestY  Top left corner where block starts (in luma pixels)
     * @param iSrcWidth   Width of block (in luma pixels)
     * @param abY  Array of block values with the color space -128 to +127.*/
    public void setY(int iDestX, int iDestY,
                     int iSrcOfs, int iSrcWidth,
                     int iCopyWidth, int iCopyHeight,
                     @Nonnull byte[] abY)
    {
        set(_abY, iDestX + iDestY * _iLumaWidth, _iLumaHeight,
            abY, iSrcOfs, iSrcWidth,
            iCopyWidth, iCopyHeight);
    }

    public void setCb(int iDestX, int iDestY,
                     int iSrcOfs, int iSrcWidth,
                     int iCopyWidth, int iCopyHeight,
                     @Nonnull byte[] abCb)
    {
        set(_abCb, iDestX + iDestY * _iChromaWidth, _iChromaWidth,
            abCb, iSrcOfs, iSrcWidth,
            iCopyWidth, iCopyHeight);
    }

    public void setCr(int iDestX, int iDestY,
                     int iSrcOfs, int iSrcWidth,
                     int iCopyWidth, int iCopyHeight,
                     @Nonnull byte[] abCr)
    {
        set(_abCr, iDestX + iDestY * _iChromaWidth, _iChromaWidth,
            abCr, iSrcOfs, iSrcWidth,
            iCopyWidth, iCopyHeight);
    }


    private void set(@Nonnull byte[] abDest, int iDestOfs, int iDestWidth,
                     @Nonnull byte[] abSrc, int iSrcOfs, int iSrcWidth,
                     int iCopyWidth, int iCopyHeight)
    {
        for (int iLine = 0; iLine < iCopyHeight;
             iLine++, iDestOfs += iDestWidth, iSrcOfs += iSrcWidth)
        {
            System.arraycopy(abSrc, iSrcOfs, abDest, iDestOfs, iCopyWidth);
        }
    }

}
