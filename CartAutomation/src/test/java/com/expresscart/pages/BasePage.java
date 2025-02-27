package com.expresscart.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Clase base para todos los Page Objects
 * Proporciona funcionalidades comunes para todas las páginas
 */
public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;
    
    // URL base de la aplicación
    private static final String BASE_URL = "http://localhost:1111";

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * Navega a una URL relativa
     * @param path Ruta relativa a la URL base
     */
    protected void navigateTo(String path) {
        driver.get(BASE_URL + path);
    }

    /**
     * Espera a que un elemento sea visible y clickable antes de hacer click
     * @param element Elemento web a clickear
     */
    protected void clickElement(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (StaleElementReferenceException e) {
            // Si el elemento se vuelve obsoleto, intentamos encontrarlo de nuevo
            WebElement refreshedElement = wait.until(ExpectedConditions.refreshed(
                    ExpectedConditions.elementToBeClickable(element)));
            refreshedElement.click();
        } catch (ElementClickInterceptedException e) {
            // Si el elemento está interceptado, usamos JavaScript para clickear
            js.executeScript("arguments[0].click();", element);
        }
    }

    /**
     * Espera a que un elemento sea visible antes de introducir texto
     * @param element Elemento web donde escribir
     * @param text Texto a escribir
     */
    protected void enterText(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Obtiene el texto de un elemento, esperando primero a que sea visible
     * @param element Elemento web del que extraer texto
     * @return Texto del elemento
     */
    protected String getText(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getText();
    }

    /**
     * Espera a que un elemento sea visible en la página
     * @param element Elemento web a esperar
     * @return El elemento web una vez visible
     */
    protected WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Verifica si un elemento está presente en el DOM
     * @param locator Localizador del elemento
     * @return true si el elemento existe, false en caso contrario
     */
    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Convierte un precio en formato texto (con símbolo de moneda) a valor numérico
     * @param priceText Texto del precio (ej: "$19.99")
     * @return Valor numérico del precio
     */
    protected double parsePrice(String priceText) {
        return Double.parseDouble(priceText.replaceAll("[^\\d.]", ""));
    }
} 