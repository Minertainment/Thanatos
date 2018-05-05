package com.minertainment.thanatos.commons.cluster;

import com.minertainment.athena.misc.GenericCallback;
import com.minertainment.thanatos.commons.slave.Slave;

public abstract class SlaveCallback implements GenericCallback<Slave> {

    @Override
    public abstract void onCallback(Slave slave);

}