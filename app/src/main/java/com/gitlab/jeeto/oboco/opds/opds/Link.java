package com.gitlab.jeeto.oboco.opds.opds;

import static com.gitlab.jeeto.oboco.opds.opds.Preconditions.checkState;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.namespace.QName;

public class Link {

    @XmlAttribute(name = "rel")
    private final String rel;
    @XmlAttribute(name = "type")
    private final String type;
    @XmlAttribute(name = "href", required = true)
    private final String href;
    @XmlAttribute(name = "title")
    private final String title;
    @XmlAnyAttribute
    private final Map<QName, String> additionalAttributes;

    @SuppressWarnings("unused") // jaxb
    private Link() {
        this(null, null, null, null, null);
    }

    private Link(String rel, String type, String href, String title, Map<QName, String> additionalAttributes) {
        this.rel = rel;
        this.type = type;
        this.href = href;
        this.title = title;
        this.additionalAttributes = additionalAttributes;
    }

    public static Builder builder(String href) {
        return new Builder(href);
    }

    public String getRel() {
        return rel;
    }

    public String getType() {
        return type;
    }

    public String getHref() {
        return href;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rel, type, href, title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Link other = (Link) obj;
        return Objects.equals(this.rel, other.rel) && Objects.equals(this.type, other.type) && Objects.equals(this.href, other.href) && Objects.equals(this.title, other.title);
    }

    @Override
    public String toString() {
        return "Link{" +
            "rel=" + rel +
            ", type='" + type + '\'' +
            ", href='" + href + '\'' +
            ", title='" + title + '\'' +
            '}';
    }

    public static class Builder {

        private final String href;
        private String rel;
        private String type;
        private String title;
        private Collection<Attribute> additionalAttributes = new LinkedHashSet<>();

        private Builder(String href) {
            this.href = href;
        }

        public Builder withRel(String rel) {
            this.rel = rel;
            return this;
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }
        
        public Builder addAttribute(Attribute attribute) {
            this.additionalAttributes.add(attribute);
            return this;
        }

        public Link build() {
            checkState(href != null, "href is mandatory");
            return new Link(rel, type, href, title, index(additionalAttributes));
        }
    }
    
    private static Map<QName, String> index(Collection<Attribute> additionalAttributes) {
        Map<QName, String> attributes = new HashMap<>(additionalAttributes.size());
        for (Attribute attribute : additionalAttributes) {
            Namespace namespace = attribute.getNamespace();
            QName name = new QName(namespace.uri(), attribute.getName(), namespace.prefix());
            attributes.put(name, attribute.getValue());
        }
        return attributes;
    }
}
