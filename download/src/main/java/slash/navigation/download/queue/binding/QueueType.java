//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.08.06 at 11:30:49 AM CEST 
//


package slash.navigation.download.queue.binding;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 a queue contains queued downloads and a last sync time
 *             
 * 
 * <p>Java class for queueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="queueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="download" type="{http://api.routeconverter.com/v1/schemas/download-queue}downloadType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "queueType", propOrder = {
    "download"
})
public class QueueType {

    protected List<DownloadType> download;

    /**
     * Gets the value of the download property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the download property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDownload().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DownloadType }
     * 
     * 
     */
    public List<DownloadType> getDownload() {
        if (download == null) {
            download = new ArrayList<>();
        }
        return this.download;
    }

}