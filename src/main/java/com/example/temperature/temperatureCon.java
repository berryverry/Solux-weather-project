package com.example.temperature;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.text.SimpleDateFormat;

//초단기실황
@Controller
public class temperatureCon {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Calendar c1 = Calendar.getInstance();
    String strToday = sdf.format(c1.getTime());
    private String nx = "60"; // x좌표
    private String ny = "126";// y좌표
    private String baseDate = strToday; // 조회하고 싶은 날짜
    private LocalDateTime now = LocalDateTime.now(); // 현재 시각을 조회 시각에.
    int hour = now.getHour()-1;
    private String baseTime = hour + "00";
    private String type = "xml"; // 조회하고 싶은 type

    @GetMapping("/temperature")
    public String getTemperature(Model model) {
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";
        String serviceKey = "HMt2MxiIwh55s2oGGTEavk%2FDgYwDuz%2Bk7EbMPN%2Fv2JyRdmPda93c1dGIDDfvnqHsDHJhTyyjq7G4ykbH8mviGA%3D%3D"; // 홈페이지에서 받은 키

        try {
            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/xml");
            System.out.println("Response code: " + conn.getResponseCode());

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            String result = sb.toString();
            System.out.println(result);

            // XML 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());

            // XML에서 필요한 데이터 추출
            NodeList itemList = document.getElementsByTagName("item");
            StringBuilder weatherInfo = new StringBuilder();
            String temperature = "";

            /*for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                String category = item.getElementsByTagName("category").item(0).getTextContent();
                String fcstValue = item.getElementsByTagName("obsrValue").item(0).getTextContent();
                String baseDate = item.getElementsByTagName("baseDate").item(0).getTextContent();
                String baseTime = item.getElementsByTagName("baseTime").item(0).getTextContent();

                weatherInfo.append("\tcategory: ").append(category).append(", obsrValue: ").append(fcstValue).append("\n");
            }*/

            Element item = (Element) itemList.item(3);
            String category = item.getElementsByTagName("category").item(0).getTextContent();
            String fcstValue = item.getElementsByTagName("obsrValue").item(0).getTextContent();
            String baseDate = item.getElementsByTagName("baseDate").item(0).getTextContent();
            String baseTime = item.getElementsByTagName("baseTime").item(0).getTextContent();

            weatherInfo.append("지금은 ").append(fcstValue).append("℃, ");

            double tem1 = Double.parseDouble(fcstValue);
            if(tem1 <= 15) weatherInfo.append("\nCOLD");
            else if(tem1 > 15 && tem1 <= 25) weatherInfo.append("\nWARM");
            else if(tem1 > 25) weatherInfo.append("\nHOT");

            model.addAttribute("weatherInfo", weatherInfo.toString());
            model.addAttribute("temperature", temperature);
            return "weather";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error occurred while fetching weather data.");
            return "error";
        }
    }

    /*Ncst category 순서
    PYT 강수형태
    REH 습도
    RN1 1시간 강수량
    T1H 기온
    UUU 풍속(동서)
    VEC 풍향
    VVV 풍속(남북)
    WSD 풍속*/
}
