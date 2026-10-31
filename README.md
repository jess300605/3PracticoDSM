# AprendeApp 📚🚀
### Sistema de Gestión de Recursos de Aprendizaje para Ingeniería de Sistemas
**Universidad Don Bosco (UDB) — Facultad de Ingeniería**  
**Desarrollo de Software para Móviles (DSM) — Tercer Desafío Práctico**

---

## 📖 Descripción General
**AprendeApp** es una solución móvil desarrollada en **Kotlin** y **Jetpack Compose** (Material Design 3) diseñada para gestionar y explorar recursos educativos especializados en Ingeniería de Sistemas (libros de Clean Architecture, tutoriales de patrones de diseño, videos de microservicios, artículos de bases de datos y seguridad).

La aplicación consume una **API REST dinámica** externa (compatible con [MockAPI.io](https://mockapi.io) y [Railway](https://railway.com)) implementando el ciclo completo de operaciones **CRUD (Create, Read, Update, Delete)**, control de acceso basado en roles (**Estudiante** y **Docente**), validación criptográfica y de complejidad de contraseñas, y persistencia local de usuario mediante **Room Database**.

---

## 🏛️ Arquitectura del Sistema
El proyecto implementa la arquitectura **MVVM (Model - View - ViewModel)** / **MVC**, garantizando separación de responsabilidades y flujo unidireccional de datos (UDF):

```
┌────────────────────────────────────────────────────────┐
│                   UI / VIEW LAYER                      │
│  Jetpack Compose Screens & Components (Material 3)    │
│  - LoginScreen, RegisterScreen                         │
│  - ResourceListScreen, TeacherCrudScreen               │
│  - FavoritesScreen, ProfileScreen                      │
│  - Dialogs: ResourceFormDialog, RatingDialog, ApiConfig│
└───────────────────────────▲────────────────────────────┘
                            │ StateFlow / Events
┌───────────────────────────▼────────────────────────────┐
│                  VIEWMODEL LAYER                       │
│  MainViewModel (State Management, Flows, Logic)       │
└───────────────────────────▲────────────────────────────┘
                            │ Coroutines
┌───────────────────────────▼────────────────────────────┐
│                 REPOSITORY LAYER                       │
│  - AuthRepository (Login, Register, Hashing, Roles)    │
│  - ResourceRepository (CRUD, Rating Sync, Cache)       │
└──────────────┬──────────────────────────┬──────────────┘
               │                          │
┌──────────────▼─────────────┐   ┌────────▼──────────────┐
│       REMOTE DATA (API)    │   │   LOCAL DATA (ROOM)   │
│ Retrofit 2 + Moshi + OkHttp│   │ AppDatabase           │
│ MockAPI / Railway REST API │   │ - UserDao             │
│ GET / POST / PUT / DELETE  │   │ - FavoriteDao         │
└────────────────────────────┘   │ - ResourceDao         │
                                 └───────────────────────┘
```

---

## 🌟 Funcionalidades Implementadas

### 1. 🔐 Registro y Login con Autenticación por Roles
- **Registro de usuarios**: Formulario con selección de rol (**Estudiante** o **Docente**).
- **Validación Estricta de Contraseña (Requisito DSM)**:
  - ✅ Mínimo 12 caracteres.
  - ✅ Al menos una letra mayúscula (`A-Z`).
  - ✅ Al menos una letra minúscula (`a-z`).
  - ✅ Al menos un número (`0-9`).
  - ✅ Al menos un carácter especial (`!@#$%^&*`).
  - *Checklist visual en tiempo real* con indicadores que cambian de gris a verde al cumplirse cada condición.
- **Roles y Permisos**:
  - 🎓 **Estudiante**: Explora la biblioteca, realiza búsquedas avanzadas, filtra por categoría, guarda en favoritos y califica recursos (1 a 5 estrellas).
  - 👨‍🏫 **Docente**: Acceso total al catálogo y a la **Pantalla Especial de Gestión CRUD** para crear (`POST`), modificar (`PUT`) y eliminar (`DELETE`) recursos en la API externa.
- **Registro de Docentes protegido**: para crear una cuenta con rol Docente se exige un código de acceso institucional (ver sección de credenciales).

### 2. ⚡ Consumo de API REST Dinámica (CRUD Completo)
- **Visualización**: Tarjetas informativas con carátula (Coil), título, descripción corta, badge de tipo (#ID) y calificación promedio.
- **Búsqueda Avanzada**: Búsqueda en tiempo real por **ID**, **Título** o **Tipo**.
- **Filtros por Tipo**: Libros, Videos, Artículos, Tutoriales o Todos.
- **Ordenamiento**:
  - Alfabético (A-Z y Z-A)
  - Mejor Calificados (estrellas)
  - Más Recientes (por ID)
- **Operaciones CRUD (Docente)**:
  - ➕ **Crear (`POST`)**: Formulario con validación de campos obligatorios y URL.
  - ✏️ **Modificar (`PUT`)**: Formulario prellenado con datos del recurso a editar.
  - 🗑️ **Eliminar (`DELETE`)**: Diálogo de confirmación antes de eliminar el recurso en el servidor.
- **Caché local (Room)**: si la API no responde, se muestran en modo lectura los últimos datos descargados. Las operaciones de escritura (`POST`/`PUT`/`DELETE`) requieren conexión y muestran un error si fallan.
- **URL de la API**: se define en `ApiClient.DEFAULT_API_URL` (MockAPI, recurso `recursos`).

### 3. ⭐ Persistencia de Usuario e Interacción
- **Lista de Favoritos**:
  - Persistencia local en Room (`FavoriteEntity`) asociada a la cuenta del usuario.
  - Pestaña exclusiva de Favoritos con contador en badge.
- **Sistema de Calificación (Rating 1 a 5 Estrellas)**:
  - Modal interactivo con selector de estrellas.
  - Recálculo dinámico del promedio:
    $$\text{Nuevo Promedio} = \frac{(\text{Promedio Actual} \times \text{Votos}) + \text{Nueva Calificación}}{\text{Votos} + 1}$$
  - Envío automático de la nueva calificación a la API externa mediante petición `PUT`.

### 4. 🎨 UI/UX & Feedback Visual
- Diseño moderno adaptativo con **Material Design 3**.
- Textos **100% centralizados** en `res/values/strings.xml` conforme a la especificación.
- Feedback inmediato mediante `Snackbar`, alertas de confirmación (`AlertDialog`) y loaders (`CircularProgressIndicator`).
- Apertura directa de enlaces educativos en el navegador (`Intent.ACTION_VIEW`) y opción para compartir recursos.

---

## 🔑 Credenciales Pre-configuradas y Código Docente

| Rol | Correo Electrónico | Contraseña | Privilegios |
| :--- | :--- | :--- | :--- |
| **Docente** | `docente@udb.edu.sv` | `Docente2026!@#$` | Ver, Buscar, Favoritos, Calificar + **CRUD API (POST/PUT/DELETE)** |
| **Estudiante** | `estudiante@udb.edu.sv` | `Estudiante2026!@#$` | Ver, Buscar, Favoritos, Calificar |
| **Estudiante** | `SS233210@udb.sv` | `Jes30060526&` | Ver, Buscar, Favoritos, Calificar |

Para registrar una **nueva cuenta Docente** desde la app, se debe ingresar el código de acceso docente: `UDB-DOCENTE-2026`. Las cuentas Estudiante se registran libremente.

*(También es posible crear nuevas cuentas desde la pantalla de Registro cumpliendo las políticas de seguridad). Si el correo ya existe, la app muestra una alerta.*

---

## 📡 Estructura del JSON de la API
```json
{
  "id": "1",
  "titulo": "Clean Architecture: A Craftsman's Guide",
  "descripcion": "Guía fundamental sobre principios de arquitectura de software desacoplada y testeable.",
  "tipo": "Libro",
  "enlace": "https://www.oreilly.com/library/view/clean-architecture-a/9780134494272/",
  "imagen": "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c",
  "calificacion": 4.8,
  "totalVotos": 18
}
```

---

## 🛠️ Tecnologías y Librerías Utilizadas
- **Lenguaje:** Kotlin 2.2
- **UI Framework:** Jetpack Compose (BOM 2024.09.00) con Material Design 3
- **Networking:** Retrofit 2.12.0 + OkHttp 4.10.0 + Logging Interceptor
- **JSON Serialization:** Moshi 1.15.2 + Codegen
- **Persistencia Local:** Room Database 2.7.0 (KSP)
- **Carga de Imágenes:** Coil Compose 2.7.0
- **Asincronía:** Kotlin Coroutines & StateFlow
- **Arquitectura:** MVVM / MVC con AndroidViewModel
