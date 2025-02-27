# Framework de Automatización para ExpressCart

Este proyecto implementa un framework de pruebas automatizadas para validar la funcionalidad del carrito de compras de ExpressCart, una aplicación de e-commerce. Utiliza Selenium WebDriver, TestNG y Java para crear tests robustos y mantenibles siguiendo el patrón Page Object Model.

## Estructura del Proyecto

El proyecto está estructurado siguiendo las mejores prácticas para frameworks de automatización, con una clara separación de responsabilidades:

```
CartAutomation/
├── src/
│   └── test/
│       └── java/
│           └── com/
│               └── expresscart/
│                   ├── pages/        # Implementaciones de Page Objects 
│                   │   ├── BasePage.java          # Clase base con funciones comunes
│                   │   ├── HomePage.java          # Página principal y navegación
│                   │   ├── ProductPage.java       # Página de detalle de producto
│                   │   └── CartPage.java          # Página del carrito de compras
│                   ├── tests/        # Clases de pruebas
│                   │   ├── BaseTest.java          # Configuración base para todos los tests
│                   │   ├── CartFunctionalityTest.java    # Pruebas de funcionalidad básica
│                   │   └── CartErrorHandlingTest.java    # Pruebas de manejo de errores
│                   └── utils/        # Utilidades y helpers
│                       ├── WebDriverFactory.java  # Creación y configuración de WebDriver
│                       └── ScreenshotUtils.java   # Utilidades para capturas de pantalla
├── target/          # Resultados de la compilación y ejecución
│   ├── screenshots/  # Capturas de pantalla de fallos
│   └── surefire-reports/  # Informes de resultados de pruebas
├── pom.xml          # Configuración de Maven y dependencias
├── testng.xml       # Configuración TestNG para paralelismo y agrupación
├── azure-pipelines.yml  # Definición del pipeline CI/CD
└── README.md        # Este archivo
```

### Aspectos Destacados de la Implementación

#### Page Objects
- **BasePage**: Contiene métodos comunes para todas las páginas (clics, esperas, etc.)
- **HomePage**: Maneja la navegación principal y la selección de productos con múltiples estrategias para manejar nombres con acentos
- **ProductPage**: Implementa acciones sobre productos y validaciones de mensajes
- **CartPage**: Gestiona todas las operaciones del carrito de compras

#### Clases de Test
- **BaseTest**: Configura el entorno de pruebas, gestiona WebDriver y captura screenshots en caso de fallo
- **CartFunctionalityTest**: Prueba funcionalidades core del carrito (añadir, eliminar, etc.)
- **CartErrorHandlingTest**: Prueba específicamente escenarios de error (servidor error 500, etc.)

#### Utilidades
- **WebDriverFactory**: Implementa el patrón Factory para crear instancias de WebDriver según la configuración
- **ScreenshotUtils**: Maneja la captura y almacenamiento de screenshots para reportes

## Requisitos Previos

Para ejecutar este framework de automatización, necesitas:

1. **Java Development Kit (JDK) 11+**
   - Asegúrate de que JAVA_HOME está correctamente configurado

2. **Maven 3.8.x+**
   - Verifica la instalación con `mvn -version`

3. **Navegadores**
   - Chrome, Firefox o Edge instalados
   - Los drivers se descargan automáticamente gracias a WebDriverManager

4. **Aplicación ExpressCart**
   - Instancia de ExpressCart ejecutándose en http://localhost:1111
   - Base de datos MongoDB con datos de productos cargados

## Configuración de ExpressCart con Docker

Para asegurar la consistencia en el entorno de pruebas, hemos configurado ExpressCart utilizando Docker, lo que proporciona un entorno aislado y reproducible que facilita tanto el desarrollo como las pruebas automatizadas.

### Estructura de ExpressCart

ExpressCart es una plataforma de e-commerce basada en Node.js con las siguientes características:

```
expressCart/
├── bin/                # Scripts de inicio
├── config/             # Archivos de configuración
│   └── settings.json   # Configuración principal de la aplicación
├── lib/                # Bibliotecas y funciones core
├── public/             # Recursos estáticos (CSS, JS, imágenes)
├── routes/             # Controladores de rutas API
├── views/              # Plantillas Handlebars para renderizado
├── app.js              # Archivo principal de la aplicación
├── docker-compose.yml  # Configuración de contenedores Docker
└── Dockerfile          # Instrucciones para construcción de imagen Docker
```

### Configuración con Docker

Utilizamos Docker Compose para gestionar tanto la aplicación ExpressCart como su base de datos MongoDB. A continuación se detalla nuestra configuración en `docker-compose.yml`:

```yaml
version: '3'
services:
  # Servicio para MongoDB
  mongodb:
    image: mongo:latest
    container_name: mongodb
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    networks:
      - expresscart-network

  # Servicio para ExpressCart
  expresscart:
    build: .
    container_name: expresscart
    ports:
      - "1111:1111"
    environment:
      - NODE_ENV=development
      - MONGODB_URL=mongodb://mongodb:27017/expresscart
    depends_on:
      - mongodb
    networks:
      - expresscart-network

volumes:
  mongodb_data:

networks:
  expresscart-network:
    driver: bridge
```

### Proceso de Configuración y Ejecución

Para configurar y ejecutar ExpressCart con Docker:

1. **Instalar Docker y Docker Compose**
   - Asegúrate de tener Docker Desktop instalado (Windows/Mac) o Docker Engine y Docker Compose (Linux)

2. **Clonar el repositorio**
   ```bash
   git clone https://github.com/mrvautin/expressCart.git
   cd expressCart
   ```

3. **Configurar la base de datos**
   - Modificamos `config/settings.json` para usar la URL de MongoDB en Docker:
   ```json
   "databaseConnectionString": "mongodb://mongodb:27017/expresscart"
   ```

4. **Iniciar los contenedores**
   ```bash
   docker-compose up -d
   ```

5. **Cargar datos iniciales**
   - Para tener productos de prueba, implementamos un script que carga datos de ejemplo en MongoDB:
   ```bash
   docker exec expresscart node lib/testdata.js
   ```

### Datos de Prueba

Para asegurar la consistencia en las pruebas automatizadas, creamos un conjunto de datos de prueba que incluyen:

1. **Productos**:
   - "Camiseta Básica" (ID: "camiseta-basica", precio: $19.99)
   - "Pantalón Casual" (ID: "pantalon-casual", precio: $39.99)
   - Y otros productos con variantes y opciones

2. **Configuración**:
   - Límite máximo de cantidad: 10 unidades por producto
   - Moneda: USD
   - Gastos de envío: $5.00 para pedidos nacionales

### Integración con Pruebas Automatizadas

Nuestros tests automatizados asumen que ExpressCart está ejecutándose en `http://localhost:1111` con los datos de prueba cargados. Para facilitar la ejecución de pruebas en entornos de CI/CD:

1. **Script de Inicialización**:
   Creamos un script que verifica y configura el entorno antes de las pruebas:
   ```bash
   #!/bin/bash
   # check-environment.sh
   
   # Verificar si los contenedores están corriendo
   if ! docker ps | grep -q expresscart; then
     echo "Iniciando ExpressCart..."
     docker-compose up -d
     sleep 10 # Esperar a que la aplicación inicie completamente
   fi
   
   # Verificar si los datos de prueba están cargados
   if ! docker exec mongodb mongo expresscart --eval "db.products.count()" | grep -q "^[1-9]"; then
     echo "Cargando datos de prueba..."
     docker exec expresscart node lib/testdata.js
   fi
   ```

2. **Configuración en Pipeline**:
   En nuestro pipeline de Azure DevOps, incluimos pasos para gestionar el entorno Docker:
   ```yaml
   # Fragmento de azure-pipelines.yml
   - script: |
       ./check-environment.sh
     displayName: 'Configurar entorno ExpressCart'
     condition: succeededOrFailed()
   ```

Esta configuración garantiza que nuestras pruebas automatizadas se ejecuten contra una instancia consistente y controlada de ExpressCart, aumentando la fiabilidad y reproducibilidad de los resultados.

## Ejecución de Pruebas Localmente

### Ejecución Básica

Para ejecutar todas las pruebas:

```bash
mvn clean test
```

Este comando ejecutará automáticamente:
1. Limpieza de compilaciones anteriores
2. Compilación del código fuente
3. Ejecución de pruebas según la configuración en testng.xml
4. Generación de informes en formato JUnit XML

### Ejecución con Parámetros

Puedes personalizar la ejecución mediante varios parámetros:

**Ejecutar un conjunto específico de pruebas:**
```bash
mvn clean test -Dtest=com.expresscart.tests.CartFunctionalityTest
```

**Cambiar el navegador:**
```bash
mvn clean test -Dbrowser=firefox
```

**Ejecutar en modo headless:**
```bash
mvn clean test -Dheadless=true
```

**Combinación de parámetros:**
```bash
mvn clean test -Dbrowser=chrome -Dheadless=true -Dtest=com.expresscart.tests.CartErrorHandlingTest
```

### Ejecución por Suite XML

El proyecto incluye configuración TestNG para ejecutar pruebas en paralelo:

```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

## CI/CD en Azure DevOps

### Configuración del Pipeline

El proyecto incluye un archivo `azure-pipelines.yml` que define la configuración del pipeline CI/CD. Para configurarlo en Azure DevOps:

1. **Crear un nuevo proyecto en Azure DevOps**
   - Accede a https://dev.azure.com/ y crea un nuevo proyecto (o usa uno existente)

2. **Configurar el repositorio**
   - Conecta tu repositorio Git (GitHub, Azure Repos, etc.)
   - Asegúrate de que el código incluya el archivo `azure-pipelines.yml`

3. **Crear un nuevo pipeline**
   - Ve a Pipelines > New Pipeline
   - Selecciona tu repositorio
   - Elige "Existing Azure Pipelines YAML file"
   - Selecciona el archivo `azure-pipelines.yml`
   - Revisa la configuración y haz clic en "Run"

### Estructura del Pipeline

El pipeline está estructurado en dos etapas principales:

1. **Testing Stage:**
   - Ejecuta dos jobs en paralelo:
     - **Functional Tests**: Ejecuta pruebas de funcionalidad básica
     - **Error Handling Tests**: Ejecuta pruebas de manejo de errores
   - Cada job:
     - Configura JDK 11
     - Aprovecha caché de dependencias Maven
     - Ejecuta pruebas con Maven
     - Publica resultados de pruebas
     - Guarda capturas de pantalla como artefactos en caso de fallo

2. **Reporting Stage:**
   - Combina y publica todos los resultados de pruebas
   - Genera un informe resumido
   - Almacena los resultados como artefactos persistentes

### Variables del Pipeline

El pipeline utiliza varias variables clave:

- `$(Pipeline.Workspace)`: Directorio de trabajo del pipeline
- `$(Agent.OS)`: Sistema operativo del agente (usado para caché)
- `$(MAVEN_CACHE_FOLDER)`: Ubicación personalizada para el repositorio Maven
- `$(Agent.JobStatus)`: Estado de finalización del job para informes

### Activación del Pipeline

El pipeline se activa automáticamente cuando:

```yaml
trigger:
- main
```

Esto significa que cada commit a la rama principal iniciará una ejecución.

## Ejecución en Paralelo

### Configuración del Paralelismo

El framework implementa dos niveles de paralelismo:

#### 1. Paralelismo a nivel de TestNG

En el archivo `testng.xml`:

```xml
<suite name="ExpressCart Shopping Cart Test Suite" parallel="tests" thread-count="2">
    <test name="Shopping Cart Functionality Tests">
        <classes>
            <class name="com.expresscart.tests.CartFunctionalityTest" />
        </classes>
    </test>
    <test name="Shopping Cart Error Handling Tests">
        <classes>
            <class name="com.expresscart.tests.CartErrorHandlingTest" />
        </classes>
    </test>
</suite>
```

Esto configura:
- Ejecución paralela a nivel de `<test>` (no métodos ni clases)
- 2 threads simultáneos máximos
- Cada `<test>` agrupa pruebas relacionadas lógicamente

#### 2. Paralelismo a nivel de Pipeline

En `azure-pipelines.yml`, ejecutamos dos jobs en paralelo:

```yaml
jobs:
- job: FunctionalTests
  displayName: 'Pruebas de Funcionalidad'
  ...
  
- job: ErrorHandlingTests
  displayName: 'Pruebas de Manejo de Errores'
  ...
```

### Consideraciones para el Paralelismo

Para asegurar una ejecución correcta en paralelo:

1. **Independencia de Pruebas**: Cada test es autónomo y no depende del estado de otros tests
2. **Setup/Teardown Robustos**: Cada test inicializa y limpia su propio entorno
3. **Timeouts Configurados**: Se establecen tiempos de espera razonables para evitar bloqueos
4. **Manejo de Recursos**: Se gestionan apropiadamente los recursos compartidos como WebDriver

### Habilitación de Paralelismo en Azure DevOps

Para cuentas gratuitas de Azure DevOps, el paralelismo necesita configuración adicional:

1. Ve a Organization Settings > Parallel Jobs
2. Para Microsoft-hosted agents, solicita capacidad adicional (2 jobs en paralelo)
3. Esta solicitud puede requerir aprobación, pero generalmente se concede para proyectos pequeños/medianos

## Manejo y Registro de Errores

### Estrategia de Detección y Manejo de Errores

El framework implementa un enfoque de múltiples capas para el manejo de errores:

#### 1. A nivel de Page Object

```java
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
```

- Métodos con múltiples estrategias de interacción
- Captura y manejo de excepciones específicas de Selenium
- Intentos alternativos automáticos cuando falla la estrategia inicial

#### 2. A nivel de Test

```java
@Test
public void testAddProductToCart() {
    try {
        // Código de prueba
        // ...
    } catch (Exception e) {
        System.out.println("Error en testAddProductToCart: " + e.getMessage());
        e.printStackTrace();
        Assert.fail("La prueba falló: " + e.getMessage());
    }
}
```

- Captura de excepciones generales para evitar fallos silenciosos
- Registro detallado del error para facilitar el diagnóstico
- Fallo explícito de la prueba con mensaje informativo

#### 3. Capturas de Pantalla Automáticas

```java
@AfterMethod
public void tearDown(ITestResult result) {
    if (result.getStatus() == ITestResult.FAILURE) {
        // Tomar captura de pantalla en caso de fallo
        ScreenshotUtils.takeScreenshot(driver, result.getName());
    }
    // Limpieza
}
```

- Captura automática en cada fallo de prueba
- Nombramiento con timestamp para evitar sobreescrituras
- Almacenamiento en formato para análisis posterior

### Manejo Específico del Error 500

El framework incluye pruebas específicas para simular y verificar el comportamiento ante errores del servidor:

```java
@Test(description = "Probar manejo de error del servidor (código 500)")
public void testServerErrorHandling() {
    // ...
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "const errorDiv = document.createElement('div');" +
        "errorDiv.className = 'error-message';" + 
        "errorDiv.textContent = 'Error del servidor: 500 Internal Server Error';" +
        "errorDiv.style.display = 'block';" +
        "document.body.appendChild(errorDiv);"
    );
    
    // Verificación del manejo del error
    // ...
}
```

- Simulación controlada de errores del servidor
- Verificación de mensajes de error apropiados en la UI
- Validación de la experiencia del usuario durante fallos

### Registro de Errores en el Pipeline

En el pipeline de CI/CD, los errores se gestionan mediante:

1. **Publicación de Resultados de Pruebas**:
   ```yaml
   - task: PublishTestResults@2
     inputs:
       testResultsFormat: 'JUnit'
       testResultsFiles: '**/surefire-reports/TEST-*.xml'
   ```

2. **Publicación de Artefactos en Fallo**:
   ```yaml
   - task: PublishBuildArtifacts@1
     inputs:
       PathtoPublish: 'CartAutomation/target/screenshots'
       ArtifactName: 'test-screenshots'
     condition: failed()
   ```

3. **Informe Resumido**:
   ```yaml
   - script: |
       echo "Generando informe de resumen de pruebas..."
       echo "Fecha de ejecución: $(date)" > test-summary.txt
       echo "Resultado: $(Agent.JobStatus)" >> test-summary.txt
   ```

### Análisis Post-Ejecución

El framework genera múltiples tipos de artefactos para facilitar el diagnóstico:

1. **Informes JUnit XML**: Contienen detalles técnicos de cada error
2. **Capturas de Pantalla**: Muestran el estado de la UI en el momento del fallo
3. **Logs de Consola**: Incluyen mensajes de diagnóstico personalizados
4. **Informe Resumido**: Proporciona una visión general de la ejecución

## Mejores Prácticas Implementadas

1. **Robustez**:
   - Múltiples estrategias de localización de elementos
   - Manejo de condiciones de carrera en páginas dinámicas
   - Normalización de textos para manejar acentos y caracteres especiales

2. **Mantenibilidad**:
   - Page Object Model para separar lógica de prueba de interacción con UI
   - Abstracción de operaciones comunes en la clase BasePage
   - Comentarios detallados en cada método público

3. **Rendimiento**:
   - Paralelismo configurado para optimizar tiempos de ejecución
   - Caché de dependencias Maven para despliegues más rápidos
   - Modos headless para entornos CI/CD

4. **Reportes**:
   - Múltiples niveles de detalle en informes
   - Capturas de pantalla automáticas en fallos
   - Integración con el panel de Azure DevOps

## Autor

David (Desarrollador Full Stack)
