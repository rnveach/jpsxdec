/*
 * jPSXdec: PlayStation 1 Media Decoder/Converter in Java
 * Copyright (C) 2017-2020  Michael Sabin
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

package jpsxdec.modules.video.framenumber;

import javax.annotation.Nonnull;

/** Result of comparing {@link FrameLookup} with {@link FrameNumber}. */
public enum FrameCompareIs {
    EQUAL { public boolean is(FrameCompareIs x) {
        return x == EQUAL || x == GREATERTHANEQUAL || x == LESSTHANEQUAL;
    }},

    LESSTHAN { public boolean is(FrameCompareIs x) {
        return x == LESSTHAN;
    }},

    LESSTHANEQUAL { public boolean is(FrameCompareIs x) {
        return x == LESSTHANEQUAL;
    }},

    GREATERTHAN { public boolean is(FrameCompareIs x) {
        return x == GREATERTHAN;
    }},

    GREATERTHANEQUAL { public boolean is(FrameCompareIs x) {
        return x == GREATERTHANEQUAL;
    }},

    /** When trying to compare header frame number with a frame without
     * a header frame number. */
    INVALID { public boolean is(FrameCompareIs x) {
        return false;
    }};

    /** Since {@code ==} can only check less-than, equal, or greater-than,
     * this also allows for less-than-or-equal and greater-than-or-equal. */
    abstract public boolean is(@Nonnull FrameCompareIs x);
}
