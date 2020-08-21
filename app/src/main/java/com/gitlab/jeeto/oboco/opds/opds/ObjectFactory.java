package com.gitlab.jeeto.oboco.opds.opds;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public Feed createFeed() {
        return Feed.builder().build();
    }

}
