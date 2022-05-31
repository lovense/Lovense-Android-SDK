package com.lovense.sdkdemo;

/**
 *  Created by Lovense on 2019/5/14
 *
 *  Copyright © 2019 Hytto. All rights reserved.
 */
public class ToyConnectEvent {
    private int connect;
    private String id;

    public ToyConnectEvent(int connect, String id) {
        this.connect = connect;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getConnect() {
        return connect;
    }

    public void setConnect(int connect) {
        this.connect = connect;
    }
}
