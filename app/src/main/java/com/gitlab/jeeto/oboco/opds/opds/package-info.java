@XmlSchema(namespace = "http://www.w3.org/2005/Atom",
    xmlns = { 
    	@XmlNs(prefix = "", namespaceURI = "http://www.w3.org/2005/Atom"),
    	@XmlNs(prefix = "opds", namespaceURI = "http://opds-spec.org/2010/catalog")
    }, 
    elementFormDefault = QUALIFIED
)
@XmlAccessorType(XmlAccessType.NONE) // explicit FTW ;)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(value = DateAdapter.class, type = Date.class),
    @XmlJavaTypeAdapter(value = ExtensionElementAdapter.class, type = ExtensionElement.class),
    @XmlJavaTypeAdapter(value = ExtensionElementAdapter.class, type = SimpleElement.class),
    @XmlJavaTypeAdapter(value = ExtensionElementAdapter.class, type = StructuredElement.class)
})
package com.gitlab.jeeto.oboco.opds.opds;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.util.Date;

import static javax.xml.bind.annotation.XmlNsForm.QUALIFIED;
