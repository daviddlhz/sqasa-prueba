package com.expresscart.tests;

import com.expresscart.utils.ScreenshotUtils;
import com.expresscart.utils.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Clase base para todos los tests, maneja la configuración y limpieza
 */
public class BaseTest {
    protected WebDriver driver;
    
    /**
     * Configura el WebDriver antes de cada método de prueba
     * @param browser Navegador a utilizar (chrome, firefox, edge)
     * @param headless Si se debe ejecutar en modo headless
     */
    @BeforeMethod
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("chrome") String browser, @Optional("false") String headless) {
        boolean isHeadless = Boolean.parseBoolean(headless);
        driver = WebDriverFactory.createDriver(browser, isHeadless);
    }
    
    /**
     * Limpia los recursos después de cada método de prueba
     * Si el test falla, toma una captura de pantalla
     * @param result Resultado del test
     */
    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            // Tomar captura de pantalla en caso de fallo
            ScreenshotUtils.takeScreenshot(driver, result.getName());
        }
        
        if (driver != null) {
            driver.quit();
        }
    }
} 