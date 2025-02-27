package com.expresscart.tests;

import com.expresscart.pages.CartPage;
import com.expresscart.pages.HomePage;
import com.expresscart.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Pruebas de funcionalidad del carrito de compras
 */
public class CartFunctionalityTest extends BaseTest {

    /**
     * Prueba para agregar un producto al carrito
     */
    @Test
    public void testAddProductToCart() {
        // Navegar a la página de inicio
        HomePage homePage = new HomePage(driver);
        homePage.navigate();
        
        try {
            // Seleccionar un producto (usar nombre con tilde como aparece en la página)
            ProductPage productPage = homePage.selectProduct("Camiseta Basica");
            
            // Añadir el producto al carrito
            productPage.addToCart();
            
            // Verificar que el mensaje de éxito aparece
            boolean isMessageDisplayed = productPage.isAddToCartSuccessMessageDisplayed();
            Assert.assertTrue(isMessageDisplayed, "El mensaje de éxito debería mostrarse");
            
            // Ir al carrito
            CartPage cartPage = productPage.goToCart();
            
            // Verificar que el carrito no está vacío
            boolean isCartEmpty = cartPage.isCartEmpty();
            Assert.assertFalse(isCartEmpty, "El carrito no debería estar vacío");
            
            // Verificar que el producto está en el carrito
            int quantity = cartPage.getProductQuantity("Camiseta Basica");
            Assert.assertEquals(quantity, 1, "La cantidad del producto debería ser 1");
        } catch (Exception e) {
            System.out.println("Error en testAddProductToCart: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("La prueba falló: " + e.getMessage());
        }
    }
    
    /**
     * Prueba para remover un producto del carrito
     */
    @Test
    public void testRemoveProductFromCart() {
        // Navegar a la página de inicio
        HomePage homePage = new HomePage(driver);
        homePage.navigate();
        
        try {
            // Añadir primer producto al carrito
            ProductPage productPage = homePage.selectProduct("Camiseta Basica");
            productPage.addToCart();
            Assert.assertTrue(productPage.isAddToCartSuccessMessageDisplayed(), 
                             "El mensaje de éxito debería mostrarse para el primer producto");
            
            // Volver a la página de inicio
            homePage.navigate();
            
            // Añadir segundo producto
            ProductPage secondProductPage = homePage.selectProduct("Pantalón Casual");
            secondProductPage.addToCart();
            Assert.assertTrue(secondProductPage.isAddToCartSuccessMessageDisplayed(), 
                              "El mensaje de éxito debería mostrarse para el segundo producto");
            
            // Ir al carrito
            CartPage cartPage = secondProductPage.goToCart();
            
            // Verificar que hay dos productos en el carrito
            int numberOfProducts = cartPage.getNumberOfProducts();
            Assert.assertEquals(numberOfProducts, 2, "El carrito debería tener 2 productos");
            
            // Remover el primer producto
            cartPage.removeProduct("Camiseta Basica");
            
            // Verificar que queda un producto
            int numberOfProductsAfterRemoval = cartPage.getNumberOfProducts();
            Assert.assertEquals(numberOfProductsAfterRemoval, 1, "El carrito debería tener 1 producto después de remover");
            
            // Verificar que el producto que queda es el segundo
            int quantity = cartPage.getProductQuantity("Pantalón Casual");
            Assert.assertEquals(quantity, 1, "La cantidad del segundo producto debería ser 1");
        } catch (Exception e) {
            System.out.println("Error en testRemoveProductFromCart: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("La prueba falló: " + e.getMessage());
        }
    }
    
    /**
     * Prueba para calcular el total del carrito
     */
    @Test
    public void testCartTotalCalculation() {
        // Navegar a la página de inicio
        HomePage homePage = new HomePage(driver);
        homePage.navigate();
        
        try {
            // Seleccionar un producto
            ProductPage productPage = homePage.selectProduct("Camiseta Basica");
            
            // Obtener el precio del producto
            double productPrice = productPage.getProductPriceValue();
            
            // Añadir 2 unidades al carrito
            productPage.setQuantity(2).addToCart();
            
            // Verificar que el mensaje de éxito aparece
            boolean isMessageDisplayed = productPage.isAddToCartSuccessMessageDisplayed();
            Assert.assertTrue(isMessageDisplayed, "El mensaje de éxito debería mostrarse");
            
            // Ir al carrito
            CartPage cartPage = productPage.goToCart();
            
            // Verificar que la cantidad es 2
            int quantity = cartPage.getProductQuantity("Camiseta Basica");
            Assert.assertEquals(quantity, 2, "La cantidad del producto debería ser 2");
            
            // Verificar el total del producto
            double expectedProductTotal = productPrice * 2;
            double actualProductTotal = cartPage.getProductTotal("Camiseta Basica");
            Assert.assertEquals(actualProductTotal, expectedProductTotal, 0.01, "El total del producto debería ser precio x cantidad");
            
            // Obtener el total del carrito (incluye envío)
            double cartTotal = cartPage.getCartTotal();
            
            // Verificar que el total del carrito es mayor que el total del producto (por el envío)
            Assert.assertTrue(cartTotal > actualProductTotal, "El total del carrito debería incluir gastos de envío");
        } catch (Exception e) {
            System.out.println("Error en testCartTotalCalculation: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("La prueba falló: " + e.getMessage());
        }
    }
    
    /**
     * Prueba para verificar el límite de cantidad máxima
     */
    @Test
    public void testMaxQuantityLimit() {
        // Navegar a la página de inicio
        HomePage homePage = new HomePage(driver);
        homePage.navigate();
        
        try {
            // Seleccionar un producto
            ProductPage productPage = homePage.selectProduct("Camiseta Basica");
            
            // Intentar añadir más de la cantidad máxima permitida (probamos con 20)
            productPage.setQuantity(20).addToCart();
            
            // Ir al carrito
            CartPage cartPage = productPage.goToCart();
            
            // Verificar que la cantidad se limitó al máximo permitido (normalmente 10)
            int quantity = cartPage.getProductQuantity("Camiseta Basica");
            Assert.assertEquals(quantity, 10, "La cantidad del producto debería limitarse al máximo configurado (10)");
        } catch (Exception e) {
            System.out.println("Error en testMaxQuantityLimit: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("La prueba falló: " + e.getMessage());
        }
    }
} 