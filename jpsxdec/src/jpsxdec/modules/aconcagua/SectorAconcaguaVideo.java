/*
 * jPSXdec: PlayStation 1 Media Decoder/Converter in Java
 * Copyright (C) 2019-2020  Michael Sabin
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

package jpsxdec.modules.aconcagua;

import javax.annotation.Nonnull;
import jpsxdec.cdreaders.CdSector;
import jpsxdec.i18n.log.ILocalizedLogger;
import jpsxdec.modules.IdentifiedSector;
import jpsxdec.modules.video.sectorbased.ISelfDemuxingVideoSector;
import jpsxdec.modules.video.sectorbased.IVideoSectorWithFrameNumber;
import jpsxdec.modules.video.sectorbased.SectorBasedFrameAnalysis;
import jpsxdec.modules.video.sectorbased.SectorBasedFrameReplace;
import jpsxdec.psxvideo.bitstreams.BitStreamAnalysis;
import jpsxdec.util.DemuxedData;

/** Video sector of the Aconcagua opening FMV. The ending FMV is different. */
public class SectorAconcaguaVideo extends IdentifiedSector
        implements DemuxedData.Piece, ISelfDemuxingVideoSector,
                   SectorBasedFrameReplace.IReplaceableVideoSector,
                   IVideoSectorWithFrameNumber
{

    private final static long MAGIC_NUMBER_BE = 0x60010200;

    // Magic                           // 4 bytes  @0
    private int _iChunkNumber;         // 2 bytes  @4
    private int _iChunksInFrame;       // 2 bytes  @6
    private int _iFrameNumber;         // 4 bytes  @8
    private int _iCodeCount;           // 4 bytes  @12
    private int _iWidth;               // 2 bytes  @16
    private int _iHeight;              // 2 bytes  @18
    private int _iQuantizationScale;   // 4 bytes  @20
    // Zeroes                          // 8 bytes  @24

    public SectorAconcaguaVideo(CdSector cdSector) {
        super(cdSector);
        if (isSuperInvalidElseReset()) return;

        long lngMagic = cdSector.readUInt32BE(0);
        if (lngMagic != MAGIC_NUMBER_BE)
            return;

        _iChunkNumber = cdSector.readSInt16LE(4);
        if (_iChunkNumber < 0 || _iChunkNumber > 10)
            return;
        _iChunksInFrame = cdSector.readSInt16LE(6);
        if (_iChunksInFrame <= 0 || _iChunksInFrame > 10)
            return;
        _iFrameNumber = cdSector.readSInt32LE(8);
        if (_iFrameNumber < 0)
            return;
        _iCodeCount = cdSector.readSInt32LE(12);
        if (_iCodeCount < 1)
            return;
        _iWidth = cdSector.readSInt16LE(16);
        if (_iWidth < 16 || _iWidth > 400)
            return;
        _iHeight = cdSector.readSInt16LE(18);
        if (_iHeight < 16 || _iHeight > 400)
            return;
        _iQuantizationScale = cdSector.readSInt32LE(20);
        if (_iQuantizationScale < 1 || _iQuantizationScale > 64)
            return;
        for (int i = 24; i < 32; i++) {
            if (cdSector.readUserDataByte(i) != 0)
                return;
        }

        setProbability(100);
    }

    @Override
    public String getTypeName() {
        return "Aconcagua Video";
    }

    @Override
    public int getWidth() {
        return _iWidth;
    }

    @Override
    public int getHeight() {
        return _iHeight;
    }

    @Override
    public int getHeaderFrameNumber() {
        return _iFrameNumber;
    }

    @Override
    public int getChunksInFrame() {
        return _iChunksInFrame;
    }

    @Override
    public int getChunkNumber() {
        return _iChunkNumber;
    }

    public int getQuantizationScale() {
        return _iQuantizationScale;
    }

    @Override
    public int getVideoSectorHeaderSize() {
        return 32;
    }

    @Override
    public int getDemuxPieceSize() {
        return getCdSector().getCdUserDataSize() - getVideoSectorHeaderSize();
    }

    @Override
    public byte getDemuxPieceByte(int i) {
        return getCdSector().readUserDataByte(i);
    }

    @Override
    public void copyDemuxPieceData(@Nonnull byte[] abOut, int iOutPos) {
        getCdSector().getCdUserDataCopy(getVideoSectorHeaderSize(),
                                        abOut, iOutPos, getDemuxPieceSize());
    }

    @Override
    public @Nonnull AconcaguaDemuxer createDemuxer(@Nonnull ILocalizedLogger log) {
        return new AconcaguaDemuxer(this, log);
    }

    @Override
    public String toString() {
        return String.format("%s %s frame:%d chunk:%d/%d %dx%d codes:%d qscale=%d",
            getTypeName(),
            super.cdToString(),
            _iFrameNumber,
            _iChunkNumber,
            _iChunksInFrame,
            _iWidth,
            _iHeight,
            _iCodeCount,
            _iQuantizationScale);
    }

    @Override
    public void replaceVideoSectorHeader(SectorBasedFrameAnalysis existingFrame, BitStreamAnalysis newFrame, byte[] abCurrentVidSectorHeader) {
        throw new UnsupportedOperationException("Never going to support replacing Aconcagua video");
    }
}
