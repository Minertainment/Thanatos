package com.minertainment.thanatos.commons.profile;

import com.minertainment.athena.profile.ProfileManager;

import java.util.UUID;

public class ThanatosProfileManager extends ProfileManager<ThanatosProfile> {

    public ThanatosProfileManager() {
        super();
    }

    @Override
    public ThanatosProfile createProfile(UUID uuid) {
        return new ThanatosProfile(uuid);
    }

}