package com.example.simpleshop;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;


// Используем класс ProductParser для того чтобы спарсить данные из xml файла
public class ProductParser {

    private static ArrayList<Product> products; // Приватная переменная для хранения полученых данных в виде ArrayList
    private static ArrayList<String> sections;


    public ProductParser() {
        if (products == null) {
            products = new ArrayList<>();
        }
        if (sections == null) {
            sections = new ArrayList<>();
        }
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public ArrayList<String> getSections() {
        return sections;
    }

    public Product findById(int id) {
        for (Product prod : products) {
            if (prod.getId() == id ) {
                return prod;
            }
        }
        return null;
    }

    // Функция для парсинга данных из файла xml, заполняет приватную переменную products
    public boolean parse(String filePath) {
        int id = 0;
        boolean status = true;
        Product currentProduct = null;
        boolean inEntry = false; // переменная флаг, показывает был ли открыт целивой тег, используется для поиска закрывающего целевога тега
        String textValue = "";
        int entryLevel = 0;
        String productSection = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();


            xpp.setInput(new StringReader(fileData(filePath)));

            int eventType = xpp.getEventType();
            // Цикл будет продолжатся пока не дойдём до конца документа
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // Нашли открывающий тег
                        if (entryLevel == 2) {
                            if (!sections.contains(tagName)) {
                                sections.add(tagName);
                                productSection = tagName;
                            }
                        }
                        entryLevel++;
                        if(productSection.equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentProduct = new Product();
                            currentProduct.setId(id);
                            id++;
                            currentProduct.setSection(productSection);
                        }
                        break;
                    case XmlPullParser.TEXT: // Находим текст внутри тега
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG: // Нашли закрывающий тег
                        // Если открыт целивой тег
                        entryLevel--;
                        if(inEntry) {
                            // Если нашли закрывающий целивой тег
                            if(productSection.equalsIgnoreCase(tagName)) {
                                products.add(currentProduct); // добавляем новый объект в products
                                inEntry = false;
                            } else if ("name".equalsIgnoreCase(tagName)) { // Если нашли тег name
                                currentProduct.setName(textValue);
                            } else if ("price".equalsIgnoreCase(tagName)) { // Если нашли тег cost
                                currentProduct.setPrice(Integer.parseInt(textValue));
                            } else if ("description".equalsIgnoreCase(tagName)) { // Если нашли тег description
                                currentProduct.setDescription(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)) { // Если нашли тег image
                                currentProduct.setImageName(textValue);
                            }
                        }
                        break;
                    default:
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }

    // Функция для извлечения данных из фала в виде одной переменной String
    private String fileData(String filePath) throws IOException {
        StringBuilder xmlResult = new StringBuilder();
        BufferedReader reader = null;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(new File(filePath));
            reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line=reader.readLine()) != null) {
                xmlResult.append(line);
            }
            return xmlResult.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }
}
