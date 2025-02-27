package com.expresscart.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utilidad para capturar screenshots durante las pruebas
 */
public class ScreenshotUtils {
    
    /**
     * Captura una screenshot y la guarda en el directorio de capturas
     * @param driver WebDriver para capturar la screenshot
     * @param testName Nombre del test para identificar la captura
     * @return Ruta del archivo de captura guardado
     */
    public static String takeScreenshot(WebDriver driver, String testName) {
        // Crear directorio para screenshots si no existe
        File screenshotDir = new File("target/screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        // Generar nombre de archivo con timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        String filePath = "target/screenshots/" + fileName;

        // Tomar la captura
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File(filePath));
            System.out.println("Screenshot guardada en: " + filePath);
            return filePath;
        } catch (IOException e) {
            System.err.println("Error al guardar la screenshot: " + e.getMessage());
            return null;
        }
    }
} 