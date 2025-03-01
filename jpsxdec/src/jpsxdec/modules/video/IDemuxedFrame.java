/*
 * jPSXdec: PlayStation 1 Media Decoder/Converter in Java
 * Copyright (C) 2012-2020  Michael Sabin
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

package jpsxdec.modules.video;

import java.io.PrintStream;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import jpsxdec.cdreaders.DiscPatcher;
import jpsxdec.i18n.exception.LoggedFailure;
import jpsxdec.i18n.log.ILocalizedLogger;
import jpsxdec.modules.video.framenumber.FrameNumber;
import jpsxdec.modules.video.sectorbased.SectorBasedFrameAnalysis;
import jpsxdec.psxvideo.bitstreams.BitStreamAnalysis;
import jpsxdec.psxvideo.mdec.MdecInputStream;
import jpsxdec.util.Fraction;

/** Universal demuxed video frame. */
public interface IDemuxedFrame {

    public interface Listener {
        void frameComplete(@Nonnull IDemuxedFrame frame) throws LoggedFailure;
    }

    int getWidth();

    int getHeight();

    /** The frame number of the demuxed frame. */
    @Nonnull FrameNumber getFrame();

    int getStartSector();

    int getEndSector();

    /** The sector when the frame should be displayed.
     * This can be relative to 0, the start of the disc, or some other
     * consistent starting sector for the video this frame is in. */
    @Nonnull Fraction getPresentationSector();

    /** Size of the demuxed frame. */
    int getDemuxSize();

    /** Returns the contiguous demux copied into a buffer. */
    @Nonnull byte[] copyDemuxData();

    /** The demux data my not be able to be converted to an mdec stream on its
     * own. This can provide a direct mdec stream instead. */
    @CheckForNull MdecInputStream getCustomFrameMdecStream();

    void printSectors(@Nonnull PrintStream ps);

    void writeToSectors(@Nonnull SectorBasedFrameAnalysis existingFrame,
                        @Nonnull BitStreamAnalysis newFrame,
                        @Nonnull DiscPatcher patcher,
                        @Nonnull ILocalizedLogger log)
            throws LoggedFailure;
}
