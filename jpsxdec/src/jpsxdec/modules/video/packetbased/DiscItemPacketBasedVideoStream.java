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

package jpsxdec.modules.video.packetbased;

import java.util.Date;
import javax.annotation.Nonnull;
import jpsxdec.cdreaders.ICdSectorReader;
import jpsxdec.discitems.Dimensions;
import jpsxdec.discitems.SerializedDiscItem;
import jpsxdec.i18n.I;
import jpsxdec.i18n.ILocalizedMessage;
import jpsxdec.i18n.exception.LocalizedDeserializationFail;
import jpsxdec.modules.player.MediaPlayer;
import jpsxdec.modules.video.DiscItemVideoStream;
import jpsxdec.modules.video.ISectorClaimToDemuxedFrame;
import jpsxdec.modules.video.framenumber.IndexSectorFrameNumber;
import jpsxdec.util.Fraction;
import jpsxdec.util.Misc;
import jpsxdec.util.player.PlayController;


public abstract class DiscItemPacketBasedVideoStream extends DiscItemVideoStream {

    private static final String SOUND_UNIT_COUNT_KEY = "Sound unit count";
    private final int _iSoundUnitCount;

    public DiscItemPacketBasedVideoStream(@Nonnull ICdSectorReader cd,
                                          int iStartSector, int iEndSector,
                                          @Nonnull Dimensions dim,
                                          @Nonnull IndexSectorFrameNumber.Format sectorIndexFrameNumberFormat,
                                          int iSoundUnitCount)
    {
        super(cd, iStartSector, iEndSector, dim, sectorIndexFrameNumberFormat);
        _iSoundUnitCount = iSoundUnitCount;
    }

    public DiscItemPacketBasedVideoStream(@Nonnull ICdSectorReader cd, @Nonnull SerializedDiscItem fields)
            throws LocalizedDeserializationFail
    {
        super(cd, fields);
        _iSoundUnitCount = fields.getInt(SOUND_UNIT_COUNT_KEY);
    }

    @Override
    public @Nonnull SerializedDiscItem serialize() {
        SerializedDiscItem serial = super.serialize();
        serial.addNumber(SOUND_UNIT_COUNT_KEY, _iSoundUnitCount);
        return serial;
    }

    final public boolean hasAudio() {
        return _iSoundUnitCount > 0;
    }

    @Override
    final public @Nonnull ILocalizedMessage getInterestingDescription() {
        int iFrames = getFrameCount();
        double dblFps = getPacketBasedFpsInterestingDescription();
        Date secs = Misc.dateFromSeconds(Math.max(iFrames / (int)Math.round(dblFps), 1));
        if (hasAudio())
            return I.GUI_PACKET_BASED_VID_DETAILS_WITH_AUDIO(getWidth() ,getHeight(), iFrames, dblFps, secs, getAudioSampleFramesPerSecond());
        else
            return I.GUI_PACKET_BASED_VID_DETAILS(getWidth() ,getHeight(), iFrames, dblFps, secs);
    }

    abstract protected double getPacketBasedFpsInterestingDescription();

    abstract public @Nonnull Fraction getFramesPerSecond();

    abstract public @Nonnull SectorClaimToAudioAndFrame makeAudioVideoDemuxer(double dblVolume);
    abstract public int getAudioSampleFramesPerSecond();

    @Override
    final public ISectorClaimToDemuxedFrame makeDemuxer() {
        return makeAudioVideoDemuxer(1.0);
    }

    @Override
    final public @Nonnull PacketBasedVideoSaverBuilder makeSaverBuilder() {
        return new PacketBasedVideoSaverBuilder(this);
    }

    @Override
    final public @Nonnull PlayController makePlayController() {
        SectorClaimToAudioAndFrame demuxer = makeAudioVideoDemuxer(1.0);
        MediaPlayer mp;
        if (hasAudio())
            mp = new MediaPlayer(this, demuxer, demuxer, getStartSector(), getEndSector(), 150);
        else
            mp = new MediaPlayer(this, demuxer, getStartSector(), getEndSector(), 150);
        return mp.getPlayController();
    }


}
