package com.expresscart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Page Object para la página del carrito de compras
 */
public class CartPage extends BasePage {

    @FindBy(xpath = "//div[@class='cart-product']//a")
    private List<WebElement> productTitles;

    @FindBy(xpath = "//div[@class='cart-product']//input[@type='number']")
    private List<WebElement> productQuantities;

    @FindBy(xpath = "//div[@class='cart-product']//div[contains(@class, 'col-md-4')]//strong")
    private List<WebElement> productTotals;

    @FindBy(id = "total-cart-amount")
    private WebElement cartTotalAmount;

    @FindBy(xpath = "//button[@class='btn btn-danger btn-delete-from-cart']")
    private List<WebElement> deleteButtons;

    @FindBy(xpath = "//a[contains(@href, '/checkout/information')]")
    private WebElement checkoutButton;

    @FindBy(id = "empty-cart")
    private WebElement emptyCartButton;

    @FindBy(className = "cart-empty-message")
    private WebElement emptyCartMessage;

    @FindBy(className = "error-message")
    private WebElement errorMessage;

    public CartPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navega a la página del carrito
     * @return La instancia de CartPage para encadenamiento
     */
    public CartPage navigate() {
        navigateTo("/cart");
        return this;
    }

    /**
     * Obtiene la cantidad de un producto específico en el carrito
     * @param productName Nombre del producto
     * @return Cantidad del producto en el carrito
     */
    public int getProductQuantity(String productName) {
        try {
            System.out.println("Buscando cantidad para producto: " + productName);
            int index = getProductIndex(productName);
            if (index != -1) {
                String value = productQuantities.get(index).getAttribute("value");
                System.out.println("Valor obtenido para cantidad: " + value);
                return Integer.parseInt(value);
            }
            
            // Si no encontramos por índice, intentamos buscar directamente
            List<WebElement> products = driver.findElements(By.xpath("//div[@class='cart-product']"));
            for (WebElement product : products) {
                String title = product.findElement(By.tagName("a")).getText().trim();
                System.out.println("Producto en carrito: " + title);
                if (title.equalsIgnoreCase(productName.trim())) {
                    String qty = product.findElement(By.xpath(".//input[@type='number']")).getAttribute("value");
                    System.out.println("Cantidad encontrada: " + qty);
                    return Integer.parseInt(qty);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener cantidad: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Obtiene el precio unitario de un producto en el carrito
     * @param productName Nombre del producto
     * @return Precio unitario del producto
     */
    public double getProductPrice(String productName) {
        int index = getProductIndex(productName);
        if (index != -1) {
            String priceText = getText(productTotals.get(index));
            return parsePrice(priceText);
        }
        return 0.0;
    }

    /**
     * Obtiene el precio total de un producto en el carrito (precio * cantidad)
     * @param productName Nombre del producto
     * @return Precio total del producto
     */
    public double getProductTotal(String productName) {
        try {
            int index = getProductIndex(productName);
            if (index != -1) {
                String totalText = getText(productTotals.get(index));
                return parsePrice(totalText);
            }
            
            // Búsqueda alternativa
            List<WebElement> products = driver.findElements(By.xpath("//div[@class='cart-product']"));
            for (WebElement product : products) {
                String title = product.findElement(By.tagName("a")).getText().trim();
                if (title.equalsIgnoreCase(productName.trim())) {
                    String total = product.findElement(By.xpath(".//strong[contains(@class, 'my-auto')]")).getText();
                    return parsePrice(total);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener total del producto: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Elimina un producto del carrito
     * @param productName Nombre del producto a eliminar
     * @return La instancia de CartPage para encadenamiento
     */
    public CartPage removeProduct(String productName) {
        try {
            List<WebElement> products = driver.findElements(By.xpath("//div[@class='cart-product']"));
            for (WebElement product : products) {
                String title = product.findElement(By.tagName("a")).getText().trim();
                if (title.equalsIgnoreCase(productName.trim())) {
                    WebElement deleteButton = product.findElement(By.xpath(".//button[contains(@class, 'btn-delete-from-cart')]"));
                    clickElement(deleteButton);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return this;
                }
            }
            throw new NoSuchElementException("No se encontró el producto: " + productName);
        } catch (Exception e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            throw new RuntimeException("Error al eliminar producto: " + productName, e);
        }
    }

    /**
     * Actualiza la cantidad de un producto en el carrito
     * @param productName Nombre del producto
     * @param quantity Nueva cantidad
     * @return La instancia de CartPage para encadenamiento
     */
    public CartPage updateProductQuantity(String productName, int quantity) {
        try {
            List<WebElement> products = driver.findElements(By.xpath("//div[@class='cart-product']"));
            for (WebElement product : products) {
                String title = product.findElement(By.tagName("a")).getText().trim();
                if (title.equalsIgnoreCase(productName.trim())) {
                    WebElement quantityInput = product.findElement(By.xpath(".//input[@type='number']"));
                    enterText(quantityInput, String.valueOf(quantity));
                    
                    // Enviar Enter para confirmar el cambio
                    quantityInput.submit();
                    
                    // Esperar a que se actualice el carrito
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return this;
                }
            }
            throw new NoSuchElementException("No se encontró el producto: " + productName);
        } catch (Exception e) {
            System.out.println("Error al actualizar cantidad: " + e.getMessage());
            throw new RuntimeException("Error al actualizar cantidad para: " + productName, e);
        }
    }

    /**
     * Obtiene el monto total del carrito
     * @return Monto total del carrito
     */
    public double getCartTotal() {
        try {
            return parsePrice(getText(cartTotalAmount));
        } catch (Exception e) {
            System.out.println("Error al obtener total del carrito: " + e.getMessage());
            
            // Intento alternativo con JavaScript
            try {
                String total = (String) js.executeScript(
                    "return document.getElementById('total-cart-amount').textContent");
                return parsePrice(total);
            } catch (Exception ex) {
                System.out.println("Error con JS al obtener total: " + ex.getMessage());
                return 0.0;
            }
        }
    }

    /**
     * Verifica si el carrito está vacío
     * @return true si el carrito está vacío
     */
    public boolean isCartEmpty() {
        try {
            // Verificar si hay productos en el carrito
            List<WebElement> products = driver.findElements(By.xpath("//div[@class='cart-product']"));
            if (!products.isEmpty()) {
                System.out.println("Carrito NO está vacío. Productos encontrados: " + products.size());
                return false;
            }
            
            // Verificar el contador del carrito
            WebElement cartCount = driver.findElement(By.id("cart-count"));
            String count = cartCount.getText().trim();
            System.out.println("Contador del carrito: " + count);
            if (!"0".equals(count)) {
                return false;
            }
            
            System.out.println("Carrito está vacío.");
            return true;
        } catch (Exception e) {
            System.out.println("Error al verificar si el carrito está vacío: " + e.getMessage());
            // Asumir que si hay error es porque no hay elementos
            return true;
        }
    }

    /**
     * Verifica si se muestra un mensaje de error
     * @return true si se muestra un mensaje de error
     */
    public boolean isErrorMessageDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene el texto del mensaje de error
     * @return Texto del mensaje de error
     */
    public String getErrorMessage() {
        if (isErrorMessageDisplayed()) {
            return getText(errorMessage);
        }
        return "";
    }

    /**
     * Obtiene el número de productos diferentes en el carrito
     * @return Número de productos diferentes
     */
    public int getNumberOfProducts() {
        try {
            List<WebElement> products = driver.findElements(By.xpath("//div[@class='cart-product']"));
            return products.size();
        } catch (Exception e) {
            System.out.println("Error al contar productos: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Método privado para obtener el índice de un producto en las listas
     * @param productName Nombre del producto
     * @return Índice del producto o -1 si no se encuentra
     */
    private int getProductIndex(String productName) {
        try {
            System.out.println("Buscando índice para producto: " + productName);
            for (int i = 0; i < productTitles.size(); i++) {
                String title = getText(productTitles.get(i)).trim();
                System.out.println("Comparando con: " + title);
                if (title.equalsIgnoreCase(productName.trim())) {
                    return i;
                }
            }
            
            // Buscar de forma alternativa
            List<WebElement> allLinks = driver.findElements(By.xpath("//div[@class='cart-product']//a"));
            for (int i = 0; i < allLinks.size(); i++) {
                String title = getText(allLinks.get(i)).trim();
                System.out.println("Alternativa - Comparando con: " + title);
                if (title.equalsIgnoreCase(productName.trim())) {
                    return i;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar índice: " + e.getMessage());
        }
        return -1;
    }
} 