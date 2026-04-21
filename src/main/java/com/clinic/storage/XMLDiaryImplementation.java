package com.clinic.storage;

import com.clinic.model.Appointment;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores appointments in an XML file.
 */
public class XMLDiaryImplementation implements DiaryImplementation {

    private final String filePath;

    public XMLDiaryImplementation(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    private void ensureFileExists() {
        File f = new File(filePath);
        if (!f.exists()) {
            try {
                f.getParentFile().mkdirs();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                Document doc = dbf.newDocumentBuilder().newDocument();
                doc.appendChild(doc.createElement("appointments"));
                writeDocument(doc);
            } catch (Exception e) {
                System.err.println("XML init error: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean save(Appointment a) {
        try {
            Document doc = loadDocument();
            Element root = doc.getDocumentElement();

            // Remove existing node with same id
            NodeList nodes = root.getElementsByTagName("appointment");
            for (int i = nodes.getLength() - 1; i >= 0; i--) {
                Element el = (Element) nodes.item(i);
                if (el.getAttribute("id").equals(a.getId())) {
                    root.removeChild(el);
                }
            }

            Element el = doc.createElement("appointment");
            el.setAttribute("id", a.getId());
            addChild(doc, el, "patientName", a.getPatientName());
            addChild(doc, el, "doctorName", a.getDoctorName());
            addChild(doc, el, "date", a.getDate().toString());
            addChild(doc, el, "time", a.getTime().toString());
            addChild(doc, el, "notes", a.getNotes());
            addChild(doc, el, "email", a.getEmail());
            addChild(doc, el, "phone", a.getPhone());
            root.appendChild(el);
            return writeDocument(doc);
        } catch (Exception e) {
            System.err.println("XML save error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            Document doc = loadDocument();
            Element root = doc.getDocumentElement();
            NodeList nodes = root.getElementsByTagName("appointment");
            for (int i = nodes.getLength() - 1; i >= 0; i--) {
                Element el = (Element) nodes.item(i);
                if (el.getAttribute("id").equals(id)) {
                    root.removeChild(el);
                    return writeDocument(doc);
                }
            }
        } catch (Exception e) {
            System.err.println("XML delete error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Appointment find(String id) {
        return findAll().stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Appointment> findAll() {
        List<Appointment> list = new ArrayList<>();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName("appointment");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                String id = el.getAttribute("id");
                Appointment a = new Appointment(
                    id,
                    getText(el, "patientName"),
                    getText(el, "doctorName"),
                    LocalDate.parse(getText(el, "date")),
                    LocalTime.parse(getText(el, "time"))
                );
                a.setNotes(getText(el, "notes"));
                a.setEmail(getText(el, "email"));
                a.setPhone(getText(el, "phone"));
                list.add(a);
            }
        } catch (Exception e) {
            System.err.println("XML findAll error: " + e.getMessage());
        }
        return list;
    }

    private Document loadDocument() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        return dbf.newDocumentBuilder().parse(new File(filePath));
    }

    private boolean writeDocument(Document doc) {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.transform(new DOMSource(doc), new StreamResult(new File(filePath)));
            return true;
        } catch (Exception e) {
            System.err.println("XML write error: " + e.getMessage());
            return false;
        }
    }

    private void addChild(Document doc, Element parent, String tag, String value) {
        Element child = doc.createElement(tag);
        child.setTextContent(value);
        parent.appendChild(child);
    }

    private String getText(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        return nl.getLength() > 0 ? nl.item(0).getTextContent() : "";
    }

    @Override
    public String toString() { return "XML Storage (" + filePath + ")"; }
}
