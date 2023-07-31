package com.example.temperature;

import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.text.SimpleDateFormat;

//초단기예보
@Controller
public class DayTemperature {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Calendar c1 = Calendar.getInstance();
    String strToday = sdf.format(c1.getTime());
    private String nx = "60"; // x좌표
    private String ny = "126"; // y좌표
    private String baseDate = strToday; // 조회하고 싶은 날짜
    private LocalDateTime now = LocalDateTime.now(); // 현재 시각을 조회 시각에.
    int hour = now.getHour()-1;
    private String baseTime = hour + "00";
    private String fTime = hour+1 + "00";
    private String type = "xml"; // 조회하고 싶은 type
    private String numOfRows = "250";

    @GetMapping("/dayTem")
    public String getTemperature(Model model) {
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";
        String serviceKey = "HMt2MxiIwh55s2oGGTEavk%2FDgYwDuz%2Bk7EbMPN%2Fv2JyRdmPda93c1dGIDDfvnqHsDHJhTyyjq7G4ykbH8mviGA%3D%3D"; // 홈페이지에서 받은 키

        try {
            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8"));	/* 한 페이지 결과 수 */

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

            // XML 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());

            // XML에서 필요한 데이터 추출
            NodeList itemList = document.getElementsByTagName("item");
            StringBuilder weatherInfo = new StringBuilder();
            StringBuilder skyInfo = new StringBuilder();

            double[] TemArray = new double[10];
            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                Object category = item.getElementsByTagName("category").item(0).getTextContent();
                String fcstValue = item.getElementsByTagName("fcstValue").item(0).getTextContent();
                String fcstTime = item.getElementsByTagName("fcstTime").item(0).getTextContent();
                String baseDate = item.getElementsByTagName("baseDate").item(0).getTextContent();
                String baseTime = item.getElementsByTagName("baseTime").item(0).getTextContent();


                if(category.equals("SKY")&&fcstTime.equals(fTime)){
                    skyInfo.append("지금의 날씨는 ");
                    if(fcstValue.equals("1")) {
                        skyInfo.append("맑음!\n");
                    }else if(fcstValue.equals("2")) {
                        skyInfo.append("비!\n");
                    }else if(fcstValue.equals("3")) {
                        skyInfo.append("구름!\n");
                    }else if(fcstValue.equals("4")) {
                        skyInfo.append("흐림!\n");
                    }
                }
                if(category.equals("T1H")){
                    weatherInfo.append(fcstTime).append("시: ").append(fcstValue).append("℃\n");

                }
            }

            /*int[] tems = new int[6];
            for(int j=0; j<6; j++) {
                tems[j] = Integer.parseInt(fcstValue);
            }
            int max = -1000;
            for(int x=0; x<6; x++) {
                if(max<tems[x]) max = tems[x];
            }
            weatherInfo.append("최고기온은: ").append(max);*/

            model.addAttribute("weatherInfo", weatherInfo);
            model.addAttribute("skyInfo", skyInfo);
            return "day";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error occurred while fetching weather data.");
            return "error";
        }
    }
}
