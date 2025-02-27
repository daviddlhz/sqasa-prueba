package com.expresscart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object para la página de inicio de expressCart
 */
public class HomePage extends BasePage {

    @FindBy(className = "product-item")
    private List<WebElement> productCards;

    @FindBy(className = "navbar-brand")
    private WebElement logo;

    @FindBy(xpath = "//*[contains(@class, 'cart-count')]")
    private WebElement cartCount;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navega a la página de inicio
     * @return La instancia de HomePage para encadenamiento
     */
    public HomePage navigate() {
        navigateTo("/");
        waitForVisibility(logo); // Espera a que cargue la página
        return this;
    }

    /**
     * Selecciona un producto por su nombre
     * @param productName Nombre del producto a seleccionar
     * @return La instancia de ProductPage para encadenamiento
     */
    public ProductPage selectProduct(String productName) {
        System.out.println("Buscando producto: " + productName);
        
        try {
            // 1. Intentar con XPath directo por el título del producto
            String cleanProductName = productName.replace("ó", "o").replace("á", "a").replace("é", "e").replace("í", "i").replace("ú", "u");
            System.out.println("Buscando producto sin acentos: " + cleanProductName);
            
            try {
                // Intento 1: Buscar directamente con el nombre exacto
                WebElement productLink = driver.findElement(
                    By.xpath("//h3[contains(@class, 'product-title')]/ancestor::a"));
                String productTitle = productLink.findElement(By.tagName("h3")).getText().trim();
                System.out.println("Producto encontrado: " + productTitle);
                
                if (productTitle.equalsIgnoreCase(productName) || 
                    productTitle.equalsIgnoreCase(cleanProductName)) {
                    clickElement(productLink);
                    return new ProductPage(driver);
                }
            } catch (Exception e) {
                System.out.println("No se encontró con nombre exacto: " + e.getMessage());
            }
            
            // Intento 2: Buscar todos los títulos de productos
            List<WebElement> productCards = driver.findElements(By.className("product-wrapper"));
            System.out.println("Número de tarjetas de producto encontradas: " + productCards.size());
            
            for (WebElement card : productCards) {
                try {
                    WebElement title = card.findElement(By.className("product-title"));
                    String titleText = title.getText().trim();
                    System.out.println("Comparando con: " + titleText);
                    
                    if (titleText.equalsIgnoreCase(productName) || 
                        titleText.equalsIgnoreCase(cleanProductName)) {
                        System.out.println("Producto encontrado por título: " + titleText);
                        WebElement link = card.findElement(By.tagName("a"));
                        clickElement(link);
                        return new ProductPage(driver);
                    }
                } catch (Exception e) {
                    System.out.println("Error al procesar tarjeta de producto: " + e.getMessage());
                }
            }
            
            // Intento 3: Buscar directamente en todos los enlaces con títulos de producto
            List<WebElement> productLinks = driver.findElements(By.xpath("//h3[contains(@class, 'product-title')]/ancestor::a"));
            System.out.println("Enlaces de producto encontrados: " + productLinks.size());
            
            for (WebElement link : productLinks) {
                try {
                    String titleText = link.findElement(By.className("product-title")).getText().trim();
                    System.out.println("Verificando enlace: " + titleText);
                    
                    if (titleText.equalsIgnoreCase(productName) || 
                        titleText.equalsIgnoreCase(cleanProductName)) {
                        System.out.println("Producto encontrado en enlace: " + titleText);
                        clickElement(link);
                        return new ProductPage(driver);
                    }
                } catch (Exception e) {
                    System.out.println("Error al procesar enlace: " + e.getMessage());
                }
            }
            
            // Intento final: Buscar con href parcial como último recurso
            String productNameInUrl = productName.toLowerCase()
                .replace(" ", "-")
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");
            
            System.out.println("Buscando producto por URL: " + productNameInUrl);
            WebElement productLinkByUrl = driver.findElement(
                By.xpath("//a[contains(@href, '/product/" + productNameInUrl + "')]"));
            
            if (productLinkByUrl != null) {
                System.out.println("Producto encontrado por URL!");
                clickElement(productLinkByUrl);
                return new ProductPage(driver);
            }
            
        } catch (Exception e) {
            System.out.println("Error general al buscar producto: " + e.getMessage());
            throw new RuntimeException("No se encontró el producto: " + productName, e);
        }
        
        throw new RuntimeException("No se encontró el producto: " + productName);
    }

    /**
     * Obtiene el número de productos en el carrito desde el indicador de la página
     * @return Número de productos en el carrito
     */
    public int getCartCount() {
        try {
            String countText = getText(cartCount).trim();
            if (countText.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(countText);
        } catch (Exception e) {
            // Si no hay contador visible o está vacío, el carrito está vacío
            return 0;
        }
    }

    /**
     * Navega directamente al carrito de compras
     * @return Instancia de CartPage
     */
    public CartPage goToCart() {
        navigateTo("/cart");
        return new CartPage(driver);
    }
} 