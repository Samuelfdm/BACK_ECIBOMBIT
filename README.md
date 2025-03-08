# Bombit Game

## Objetivo
El objetivo principal de este proyecto es desarrollar una versión moderna y optimizada del clásico juego Bomberman, enfocada en la jugabilidad en tiempo real para cuatro jugadores. Se busca mejorar la experiencia multijugador mediante tecnologías de redes eficientes, garantizando baja latencia y una experiencia de juego competitiva.
El proyecto implementa una arquitectura moderna basada en microservicios para proporcionar una experiencia de juego fluida, escalable y de alta disponibilidad.

## Infraestructura

### Cloud

El proyecto está completamente desplegado en **Azure**, aprovechando los siguientes servicios:

- **Azure App Service**: Alojamiento de la aplicación web del frontend
- **Azure Container Apps**: Orquestación de contenedores para los microservicios backend
- **Azure Blob Storage**: Almacenamiento de assets del juego (sprites, sonidos, etc.)
- **Azure Entra ID**: Autenticación y gestión de identidades
- **Azure Monitor**: Monitorización y logging centralizado
- **Azure Container Registry**: Almacenamiento de imágenes Docker

### Backend (Spring Boot)

La capa de backend está implementada como una serie de microservicios desarrollados con Spring Boot:

- **API Gateway (Spring Cloud Gateway)**: Punto de entrada único para todas las peticiones
- **Microservicios**:
  - User Service: Gestión de perfiles y progreso de los jugadores
  - Auth Service: Integración con Entra ID para autenticación
  - Game Engine Service: Lógica principal del juego
  - Map Service: Generación y gestión de mapas
- **Servicios en Tiempo Real**:
  - WebSocket Service: Comunicación bidireccional en tiempo real
  - Game State Service: Gestión del estado del juego

### Frontend (React JS)

La interfaz de usuario está desarrollada con React JS:

- Game Canvas: Renderizado del juego usando Pixi.js
- Redux/Context API: Gestión del estado global
- Authentication Module: Integración con Azure Entra ID

### Bases de Datos

- **MongoDB**: Base de datos principal para almacenar información de usuarios, partidas y mapas
- **Redis**: Caché en memoria para estado del juego en tiempo real y sesiones

### Comunicación

- **REST API**: Comunicación asíncrona entre servicios y cliente
- **WebSockets**: Comunicación en tiempo real para actualizaciones del juego
- **Kafka**: Sistema de mensajería para eventos entre microservicios

## Despliegue

### Prerequisitos

- Cuenta de Azure con permisos adecuados
- Azure CLI instalado
- Docker instalado localmente
- Node.js y npm para desarrollo frontend
- JDK y Maven para desarrollo backend

### Configuración de Azure Container Apps

1. **Crear un grupo de recursos**:
   ```bash
   az group create --name bombit-rg --location eastus
   ```

2. **Crear Azure Container Registry**:
   ```bash
   az acr create --resource-group bombit-rg --name bombitregistry --sku Basic
   ```

3. **Configurar Azure Container Apps Environment**:
   ```bash
   az containerapp env create --name bombit-env --resource-group bombit-rg --location eastus
   ```

### Pipeline de CI/CD

El proyecto utiliza Azure DevOps para CI/CD con los siguientes pasos:

1. **Build**: Compilación de microservicios y frontend
   ```yaml
   - stage: Build
     jobs:
     - job: BuildServices
       steps:
       - task: Docker@2
         inputs:
           command: 'buildAndPush'
           containerRegistry: 'bombitregistry'
           repository: '$(microserviceName)'
           tags: 'latest'
   ```

2. **Test**: Ejecución de pruebas unitarias e integración
   ```yaml
   - stage: Test
     jobs:
     - job: RunTests
       steps:
       - task: Maven@3
         inputs:
           goals: 'test'
           publishJUnitResults: true
   ```

3. **Deploy**: Despliegue a Azure Container Apps
   ```yaml
   - stage: Deploy
     jobs:
     - job: DeployContainerApps
       steps:
       - task: AzureCLI@2
         inputs:
           azureSubscription: '$(azureSubscription)'
           scriptType: 'bash'
           scriptLocation: 'inlineScript'
           inlineScript: 'az containerapp update --name $(microserviceName) --resource-group bombit-rg --image bombitregistry.azurecr.io/$(microserviceName):latest'
   ```

## Buenas Prácticas

### Desarrollo

- **Arquitectura Hexagonal**: Separación clara entre dominio, aplicación e infraestructura
- **API-First Design**: Definición de contratos API mediante OpenAPI/Swagger
- **Testing Automatizado**: Cobertura mínima del 80% con pruebas unitarias e integración
- **Clean Code**: Seguimiento de principios SOLID y convenciones de código

### Monitorización y Operaciones

- **Logging Centralizado**: Todos los logs se envían a Azure Monitor
- **Alertas Automáticas**: Configuración de alertas para anomalías y errores
- **Dashboards Operativos**: Visualización de métricas clave de rendimiento

## Próximos Pasos

- Implementación de un sistema de matchmaking avanzado
- Incorporación de logros y sistema de recompensas
- Desarrollo de editor de mapas para la comunidad
- Integración con análisis de telemetría para balance del juego
