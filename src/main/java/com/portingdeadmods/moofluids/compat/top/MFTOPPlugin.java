package com.portingdeadmods.moofluids.compat.top;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;

public final class MFTOPPlugin {
    public static void registerCompatibility() {
        ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        oneProbe.registerEntityProvider(new MFTopProvider());
    }
}
