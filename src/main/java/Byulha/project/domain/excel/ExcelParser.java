package Byulha.project.domain.excel;

import Byulha.project.domain.excel.model.dto.request.RequestExcelDto;
import Byulha.project.domain.perfume.model.ForGender;
import Byulha.project.domain.perfume.model.PriceValue;
import Byulha.project.domain.perfume.model.Sillage;
import Byulha.project.domain.perfume.model.entity.Perfume;
import Byulha.project.domain.perfume.repository.PerfumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelParser {

    private final PerfumeRepository perfumeRepository;

    public void parseExcelData(RequestExcelDto dto) throws Exception {
        OPCPackage opcPackage = OPCPackage.open(dto.getExcelFile().getInputStream());
        XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);

        XSSFSheet sheet = workbook.getSheetAt(0);

        for(int i=1;i <637; i++){
            XSSFRow row = sheet.getRow(i);

            String link = "";
            String name = "";
            String company = "";
            String for_gender = "";
            double rating = 0;
            String notes = "";
            String sillage = "";
            String price_value = "";
            String perfume_image = "";
            String thumbnail_image = "";

            // link (String)
            XSSFCell cell = row.getCell(0);
            if (null != cell) {
                link = cell.getStringCellValue();
            }

            // name (String)
            cell = row.getCell(1);
            if (null != cell && cell.getCellType() == CellType.NUMERIC) {
                name = String.valueOf(cell.getNumericCellValue());
            } else if(null != cell && cell.getCellType() == CellType.STRING) {
                name = cell.getStringCellValue();
            }

            // company (String)
            cell = row.getCell(2);
            if (null != cell) {
                company = cell.getStringCellValue();
            }

            // for_gender (String)
            cell = row.getCell(3);
            if (null != cell) {
                for_gender = cell.getStringCellValue();
            }

            // rating (double)
            cell = row.getCell(4);
            if (null != cell) {
                rating = cell.getNumericCellValue();
            }

            // main accords => notes (String)
            cell = row.getCell(5);
            if (null != cell) {
                String notesData = cell.getStringCellValue();
                HashMap<String, Double> notesMap = parseData(notesData);

                List<Map.Entry<String, Double>> sortedData = new ArrayList<>(notesMap.entrySet());

                sortedData.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

                StringBuilder resultNotes = new StringBuilder();
                for (int j=0; j<sortedData.size(); j++) {
                    resultNotes.append(sortedData.get(j).getKey()).append(":").append(sortedData.get(j).getValue());
                    if (j != sortedData.size() - 1) {
                        resultNotes.append(",");
                    }
                }
                notes = resultNotes.toString();
            }

            // sillage (String)
            cell = row.getCell(7);
            if (null != cell) {
                String sillageData = cell.getStringCellValue();
                HashMap<String, Double> sillageMap = parseData(sillageData);

                List<Map.Entry<String, Double>> sortedData = new ArrayList<>(sillageMap.entrySet());

                sortedData.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

                sillage = sortedData.get(0).getKey();
            }

            // price value (String)
            cell = row.getCell(8);
            if (null != cell) {
                String priceData = cell.getStringCellValue();
                HashMap<String, Double> priceMap = parseData(priceData);

                List<Map.Entry<String, Double>> sortedData = new ArrayList<>(priceMap.entrySet());

                sortedData.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

                price_value = sortedData.get(0).getKey();
            }

            // perfume image (String)
            cell = row.getCell(0);
            if (null != cell) {
                String perfumeUrl = cell.getStringCellValue();
                String perfumeNum = extractNumberFromUrl(perfumeUrl);
                perfume_image = "https://fimgs.net/mdimg/perfume/375x500." + perfumeNum + ".jpg";

                thumbnail_image = "https://fimgs.net/mdimg/perfume/m." + perfumeNum + ".jpg";
            }

            // thumbnail image (String)

            Perfume perfume = Perfume.builder()
                    .perfumeUrl(link)
                    .name(name)
                    .company(company)
                    .notes(notes)
                    .rating(rating)
                    .forGender(changeGender(for_gender))
                    .sillage(Sillage.valueOf(sillage.toUpperCase().replace(" ", "_")))
                    .priceValue(PriceValue.valueOf(price_value.toUpperCase().replace(" ", "_")))
                    .perfumeImage(perfume_image)
                    .thumbnailImage(thumbnail_image)
                    .build();

            perfumeRepository.save(perfume);
        }
    }

    private String extractNumberFromUrl(String perfumeUrl) {
        Pattern pattern = Pattern.compile(("-(\\d+)\\.html"));
        Matcher matcher = pattern.matcher(perfumeUrl);

        if (matcher.find()) {
            return String.valueOf(matcher.group(1));
        } else {
            throw new IllegalArgumentException("No number found in the URL");
        }
    }

    private ForGender changeGender(String forGender) {
        if (forGender.equals("for women and men")) {
            return ForGender.FOR_BOTH;
        }
        return ForGender.valueOf(forGender.toUpperCase().replace(" ", "_"));
    }

    private Map<String, Double> sortData(HashMap<String, Double> input) {
        return input.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));
    }

    private HashMap<String, Double> parseData(String input) {
        HashMap<String, Double> data = new HashMap<>();
        int startIndex = input.indexOf("{");
        int endIndex = input.indexOf("}");
        String dataString = input.substring(startIndex + 1, endIndex);

        String[] parts = input.split(", ");
        for (String part : parts) {
            String[] keyValue = part.split(": ");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("[{}']", "");
                double value = Double.parseDouble(keyValue[1].trim().replaceAll("[}']", ""));
                data.put(key, value);
            }
        }
        return data;
    }
}