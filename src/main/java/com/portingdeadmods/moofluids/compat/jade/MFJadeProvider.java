package com.portingdeadmods.moofluids.compat.jade;

import com.portingdeadmods.moofluids.entity.FluidCow;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin("moofluids")
public class MFJadeProvider implements IWailaPlugin {
    static IWailaClientRegistration client;

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(MFJadePlugin.INSTANCE, FluidCow.class);
    }
}
