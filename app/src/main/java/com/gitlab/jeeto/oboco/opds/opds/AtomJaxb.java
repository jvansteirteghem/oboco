package com.gitlab.jeeto.oboco.opds.opds;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class AtomJaxb {

    public static JAXBContext newContext() throws JAXBException {
        return JAXBContext.newInstance(Feed.class.getPackage().getName(), AtomJaxb.class.getClassLoader());
    }

    private AtomJaxb() {
    }
}
