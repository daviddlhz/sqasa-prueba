# Framework de Automatización para expressCart

Este proyecto implementa un framework de pruebas automatizadas para la validación del carrito de compras de expressCart, una aplicación de e-commerce.

## Estructura del Proyecto

El proyecto sigue el patrón de diseño Page Object Model (POM) para facilitar el mantenimiento y la escalabilidad:

```
CartAutomation/
├── src/
│   └── test/
│       └── java/
│           └── com/
│               └── expresscart/
│                   ├── pages/        # Clases de Page Objects
│                   ├── tests/        # Clases de pruebas
│                   └── utils/        # Utilidades de soporte
├── pom.xml         # Configuración de Maven
├── testng.xml      # Configuración de TestNG para ejecución
└── README.md       # Este archivo
```

## Casos de Prueba Implementados

Se han implementado los siguientes casos de prueba para el carrito de compras:

1. **Funcionalidad Básica:**
   - Agregar productos al carrito y verificar que se actualizan correctamente.
   - Eliminar productos del carrito y validar que el total se actualiza adecuadamente.
   - Verificar los cálculos del total de la compra al agregar o quitar productos.
   - Probar el límite máximo de productos permitidos en el carrito (configurado como 10).

2. **Manejo de Errores:**
   - Simular un error del servidor (código 500) y verificar que la interfaz muestra un mensaje adecuado.
   - Probar el manejo de errores al intentar establecer cantidades inválidas.

## Ejecución de Pruebas

### Requisitos Previos

- Java JDK 11 o superior
- Maven 3.8.x o superior
- Navegador Chrome, Firefox o Edge instalado

### Comandos de Ejecución

Para ejecutar todas las pruebas:

```bash
mvn clean test
```

Para ejecutar solo las pruebas de funcionalidad básica:

```bash
mvn clean test -Dgroups=functionality
```

Para ejecutar solo las pruebas de manejo de errores:

```bash
mvn clean test -Dgroups=error-handling
```

### Configuración de Ejecución

Puedes personalizar la ejecución mediante parámetros:

- **Browser**: Navegador a utilizar (chrome, firefox, edge)
- **Headless**: Ejecutar en modo headless (true/false)

Ejemplo:

```bash
mvn clean test -Dbrowser=firefox -Dheadless=true
```

## Integración con CI/CD

Este framework está diseñado para integrarse con cualquier sistema CI/CD, especialmente Azure DevOps. Los informes de resultados son generados en formato estándar de TestNG, que puede ser interpretado por la mayoría de las herramientas de CI/CD.

## Capturas de Pantalla

En caso de fallos en las pruebas, se guardan capturas de pantalla automáticamente en el directorio `target/screenshots` con el formato `[nombre_test]_[timestamp].png`.

## Mantenimiento

Para añadir nuevos casos de prueba:

1. Crea nuevos métodos en las clases de test existentes o crea nuevas clases que extiendan de `BaseTest`.
2. Agrega los nuevos métodos con la anotación `@Test`.
3. Si es necesario, añade nuevos Page Objects en el paquete `pages`.

## Autor

David (Desarrollador Full Stack) 