package com.smhrd.deulmaru.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class NcpmsService {
    private final String API_KEY = "202527b8644da53e300b8e2584ea2839576d";
    private final String BASE_URL = "http://ncpms.rda.go.kr/npmsAPI/service";
    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ 병해충 검색
    public Map<String, Object> searchDisease(String query, String type) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String searchTypeParam = type.equals("sick") ? "sickNameKor" : "cropName";
            String url = BASE_URL + "/pestList?apiKey=" + API_KEY + "&" + searchTypeParam + "=" + encodedQuery;
            Document doc = getXmlResponse(url);

            List<Map<String, String>> diseaseList = new ArrayList<>();
            NodeList items = doc.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                Map<String, String> disease = new HashMap<>();
                disease.put("sickNameKor", getTagValue("sickNameKor", item));
                disease.put("cropName", getTagValue("cropName", item));
                disease.put("thumbImg", getTagValue("thumbImg", item));
                disease.put("sickKey", getTagValue("sickKey", item));
                diseaseList.add(disease);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("items", diseaseList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    // ✅ 병해충 상세 정보 가져오기
    public Map<String, Object> getDiseaseDetail(String sickKey) {
        try {
            String url = BASE_URL + "/pestDetail?apiKey=" + API_KEY + "&sickKey=" + sickKey;
            Document doc = getXmlResponse(url);

            Map<String, Object> detail = new HashMap<>();
            detail.put("sickNameKor", getTagValue("sickNameKor", doc.getDocumentElement()));
            detail.put("symptoms", getTagValue("symptoms", doc.getDocumentElement()));
            detail.put("preventionMethod", getTagValue("preventionMethod", doc.getDocumentElement()));
            detail.put("imageList", getTagValue("imageList", doc.getDocumentElement()));

            return detail;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    // ✅ 병해충 상담 검색
    public Map<String, Object> searchConsult(String query, int page) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = BASE_URL + "/consultList?apiKey=" + API_KEY + "&query=" + encodedQuery + "&page=" + page;
            Document doc = getXmlResponse(url);

            List<Map<String, String>> consultList = new ArrayList<>();
            NodeList items = doc.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                Map<String, String> consult = new HashMap<>();
                consult.put("title", getTagValue("dgnssReqSj", item));
                consult.put("consultId", getTagValue("dgnssReqNo", item));
                consult.put("requestDate", getTagValue("registDatetm", item));
                consultList.add(consult);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("items", consultList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    // ✅ 병해충 상담 상세보기
    public Map<String, Object> getConsultDetail(String consultId) {
        try {
            String url = BASE_URL + "/consultDetail?apiKey=" + API_KEY + "&consult_id=" + consultId;
            Document doc = getXmlResponse(url);

            Map<String, Object> detail = new HashMap<>();
            detail.put("title", getTagValue("dgnssReqSj", doc.getDocumentElement()));
            detail.put("requestContent", getTagValue("reqestCn", doc.getDocumentElement()));
            detail.put("opinion", getTagValue("dgnssOpin", doc.getDocumentElement()));

            return detail;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    // ✅ XML 응답 가져오기
    private Document getXmlResponse(String url) throws Exception {
        String xml = restTemplate.getForObject(url, String.class);
        return javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    // ✅ XML 태그 값 가져오기
    private String getTagValue(String tag, Element element) {
        NodeList list = element.getElementsByTagName(tag);
        return list.getLength() > 0 ? list.item(0).getTextContent() : "정보 없음";
    }
}
