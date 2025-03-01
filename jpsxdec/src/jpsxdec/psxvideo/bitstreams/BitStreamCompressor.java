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

package jpsxdec.psxvideo.bitstreams;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import jpsxdec.i18n.exception.LocalizedIncompatibleException;
import jpsxdec.i18n.log.ILocalizedLogger;
import jpsxdec.psxvideo.encode.MdecEncoder;
import jpsxdec.psxvideo.mdec.MdecException;
import jpsxdec.psxvideo.mdec.MdecInputStream;
import jpsxdec.util.IncompatibleException;

/** Interface for classes that can generate a binary bit-stream compressed MDEC frame. */
public interface BitStreamCompressor {

    /** Compresses the {@link MdecInputStream} and returns the result.  */
    @Nonnull byte[] compress(@Nonnull MdecInputStream inStream)
            throws IncompatibleException, MdecException.EndOfStream,
                   MdecException.ReadCorruption, MdecException.TooMuchEnergy;

    /** Returns null if unable to encode the frame small enough, otherwise the
     * returned {@code array.length} will be {@code <= iMaxSize}. */
    @CheckForNull byte[] compressFull(@Nonnull int iMaxSize,
                                      @Nonnull String sFrameDescription,
                                      @Nonnull MdecEncoder encoder,
                                      @Nonnull ILocalizedLogger log)
            throws MdecException.EndOfStream, MdecException.ReadCorruption;

    /** Returns null if unable to encode the frame small enough, otherwise the
     * returned {@code array.length} will be {@code <= abOriginonal.length}. */
    @CheckForNull byte[] compressPartial(@Nonnull byte[] abOriginal,
                                         @Nonnull String sFrameDescription,
                                         @Nonnull MdecEncoder encoder,
                                         @Nonnull ILocalizedLogger log)
            throws LocalizedIncompatibleException, MdecException.EndOfStream, MdecException.ReadCorruption;

    /**
     * @return count or -1 if n/a.
     */
    int getMdecCodesFromLastCompress();
}
