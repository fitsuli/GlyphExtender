package com.nothing.ketchum

import com.nothing.ketchum.Glyph.Code_20111
import com.nothing.ketchum.Glyph.Code_22111
import com.nothing.ketchum.Glyph.Code_23111

/* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/ketchum/GlyphFrame.class */
class GlyphFrame private constructor(builder: Builder) {
    private var mChannel = mutableListOf<Int?>()
    var period: Int = 0
    var cycles: Int = 1
    var interval: Int = 0

    init {
        this.period = builder.period
        this.cycles = builder.cycles
        this.interval = builder.interval
        this.mChannel = builder.channel
    }

    val channel: IntArray = IntArray(mChannel.size) { i ->
        mChannel[i] ?: 0 // fill with data from mChannel or 0 if null
    }

    /* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/ketchum/GlyphFrame$Builder.class */
    class Builder {
        var period: Int
        var cycles: Int
        var interval: Int
        var channel: MutableList<Int?>
        private var mDevice: String?

        constructor() {
            this.period = 0
            this.cycles = 1
            this.interval = 0
            this.mDevice = Glyph.DEVICE_22111
            this.channel =
                MutableList(size = Common.getTargetDeviceGlyphChannelSize(this.mDevice)) { null }
        }

        constructor(targetDevice: String?) {
            val targetDevice = targetDevice ?: Glyph.DEVICE_22111
            this.period = 0
            this.cycles = 1
            this.interval = 0
            this.mDevice = targetDevice
            this.channel =
                MutableList(size = Common.getTargetDeviceGlyphChannelSize(targetDevice)) { null }
        }

        fun buildPeriod(period: Int): Builder {
            this.period = period
            return this
        }

        fun buildCycles(cycles: Int): Builder {
            this.cycles = cycles
            return this
        }

        fun buildInterval(interval: Int): Builder {
            this.interval = interval
            return this
        }

        fun buildChannel(channel: Int): Builder {
            this.channel[channel] = DEFAULT_LIGHT
            return this
        }

        fun buildChannel(channel: Int, light: Int): Builder {
            this.channel[channel] = light
            return this
        }

        fun buildChannelA(): Builder {
            if (Common.isTargetDevice20111(this.mDevice)) {
                channel[Code_20111.A1] = DEFAULT_LIGHT
            } else if (Common.isTargetDevice22111(this.mDevice)) {
                channel[Code_22111.A1] = DEFAULT_LIGHT
                channel[Code_22111.A2] = DEFAULT_LIGHT
            } else if (Common.isTargetDevice23111(this.mDevice)) {
                channel[Code_23111.A] = DEFAULT_LIGHT
            }
            return this
        }

        fun buildChannelB(): Builder {
            if (Common.isTargetDevice20111(this.mDevice)) {
                channel[Code_20111.B1] = DEFAULT_LIGHT
            } else if (Common.isTargetDevice22111(this.mDevice)) {
                channel[Code_22111.B1] = DEFAULT_LIGHT
            } else if (Common.isTargetDevice23111(this.mDevice)) {
                channel[Code_23111.B] = DEFAULT_LIGHT
            }
            return this
        }

        fun buildChannelC(): Builder {
            if (Common.isTargetDevice20111(this.mDevice)) {
                channel[Code_20111.C1] = DEFAULT_LIGHT
                channel[Code_20111.C2] = DEFAULT_LIGHT
                channel[Code_20111.C3] = DEFAULT_LIGHT
                channel[Code_20111.C4] = DEFAULT_LIGHT
            } else if (Common.isTargetDevice22111(this.mDevice)) {
                channel[Code_22111.C1_1] = DEFAULT_LIGHT
                channel[Code_22111.C1_2] = DEFAULT_LIGHT
                channel[Code_22111.C1_3] = DEFAULT_LIGHT
                channel[Code_22111.C1_4] = DEFAULT_LIGHT
                channel[Code_22111.C1_5] = DEFAULT_LIGHT
                channel[Code_22111.C1_6] = DEFAULT_LIGHT
                channel[Code_22111.C1_7] = DEFAULT_LIGHT
                channel[Code_22111.C1_8] = DEFAULT_LIGHT
                channel[Code_22111.C1_9] = DEFAULT_LIGHT
                channel[Code_22111.C1_10] = DEFAULT_LIGHT
                channel[Code_22111.C1_11] = DEFAULT_LIGHT
                channel[Code_22111.C1_12] = DEFAULT_LIGHT
                channel[Code_22111.C1_13] = DEFAULT_LIGHT
                channel[Code_22111.C1_14] = DEFAULT_LIGHT
                channel[Code_22111.C1_15] = DEFAULT_LIGHT
                channel[Code_22111.C1_16] = DEFAULT_LIGHT
                channel[Code_22111.C2] = DEFAULT_LIGHT
                channel[Code_22111.C3] = DEFAULT_LIGHT
                channel[Code_22111.C4] = DEFAULT_LIGHT
                channel[Code_22111.C5] = DEFAULT_LIGHT
                channel[Code_22111.C6] = DEFAULT_LIGHT
            } else if (Common.isTargetDevice23111(this.mDevice)) {
                channel[Code_23111.C_1] = DEFAULT_LIGHT
                channel[Code_23111.C_2] = DEFAULT_LIGHT
                channel[Code_23111.C_3] = DEFAULT_LIGHT
                channel[Code_23111.C_4] = DEFAULT_LIGHT
                channel[Code_23111.C_5] = DEFAULT_LIGHT
                channel[Code_23111.C_6] = DEFAULT_LIGHT
                channel[Code_23111.C_7] = DEFAULT_LIGHT
                channel[Code_23111.C_8] = DEFAULT_LIGHT
                channel[Code_23111.C_9] = DEFAULT_LIGHT
                channel[Code_23111.C_10] = DEFAULT_LIGHT
                channel[Code_23111.C_11] = DEFAULT_LIGHT
                channel[Code_23111.C_12] = DEFAULT_LIGHT
                channel[Code_23111.C_13] = DEFAULT_LIGHT
                channel[Code_23111.C_14] = DEFAULT_LIGHT
                channel[Code_23111.C_15] = DEFAULT_LIGHT
                channel[Code_23111.C_16] = DEFAULT_LIGHT
                channel[Code_23111.C_17] = DEFAULT_LIGHT
                channel[Code_23111.C_18] = DEFAULT_LIGHT
                channel[Code_23111.C_19] = DEFAULT_LIGHT
                channel[Code_23111.C_20] = DEFAULT_LIGHT
                channel[Code_23111.C_21] = DEFAULT_LIGHT
                channel[Code_23111.C_22] = DEFAULT_LIGHT
                channel[Code_23111.C_23] = DEFAULT_LIGHT
                channel[Code_23111.C_24] = DEFAULT_LIGHT
            }
            return this
        }

        fun buildChannelD(): Builder {
            if (Common.isTargetDevice20111(this.mDevice)) {
                channel[Code_20111.D1_1] = DEFAULT_LIGHT
                channel[Code_20111.D1_2] = DEFAULT_LIGHT
                channel[Code_20111.D1_3] = DEFAULT_LIGHT
                channel[Code_20111.D1_4] = DEFAULT_LIGHT
                channel[Code_20111.D1_5] = DEFAULT_LIGHT
                channel[Code_20111.D1_6] = DEFAULT_LIGHT
                channel[Code_20111.D1_7] = DEFAULT_LIGHT
                channel[Code_20111.D1_8] = DEFAULT_LIGHT
            } else if (Common.isTargetDevice22111(this.mDevice)) {
                channel[Code_22111.D1_1] = DEFAULT_LIGHT
                channel[Code_22111.D1_2] = DEFAULT_LIGHT
                channel[Code_22111.D1_3] = DEFAULT_LIGHT
                channel[Code_22111.D1_4] = DEFAULT_LIGHT
                channel[Code_22111.D1_5] = DEFAULT_LIGHT
                channel[Code_22111.D1_6] = DEFAULT_LIGHT
                channel[Code_22111.D1_7] = DEFAULT_LIGHT
                channel[Code_22111.D1_8] = DEFAULT_LIGHT
            }
            return this
        }

        fun buildChannelE(): Builder {
            if (Common.isTargetDevice20111(this.mDevice)) {
                channel[Code_20111.E1] = DEFAULT_LIGHT
            } else if (Common.isTargetDevice22111(this.mDevice)) {
                channel[Code_22111.E1] = DEFAULT_LIGHT
            }
            return this
        }

        fun build(): GlyphFrame {
            return GlyphFrame(this)
        }
    }

    companion object {
        private const val DEFAULT_LIGHT = 4000
    }
}
