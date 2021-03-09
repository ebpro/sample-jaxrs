@XmlSchema(
        xmlns = {
                @XmlNs(prefix="ebjax", namespaceURI="http://bruno.univ-tln.fr/sample-jaxrs"),
                @XmlNs(prefix="xsd", namespaceURI="http://www.w3.org/2001/XMLSchema")
        },
        namespace="http://bruno.univ-tln.fr/sample-jaxrs",
        elementFormDefault= XmlNsForm.UNQUALIFIED,
        attributeFormDefault=XmlNsForm.UNQUALIFIED
)
package fr.univtln.bruno.samples.jaxrs.model;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;