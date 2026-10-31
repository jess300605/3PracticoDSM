# Manual de Usuario — AprendeApp 📚📱
### Sistema de Gestión de Recursos de Aprendizaje para Ingeniería de Sistemas
**Universidad Don Bosco (UDB) — Facultad de Ingeniería**  
**Desarrollo de Software para Móviles (DSM) — Tercer Desafío Práctico**

---

## 📖 Introducción
**AprendeApp** es una aplicación Android para consultar, organizar y calificar recursos de aprendizaje de Ingeniería de Sistemas: **libros, videos, artículos y tutoriales**.

Este manual explica, paso a paso, cómo usar la aplicación según tu rol:

| Rol | ¿Para quién es? | ¿Qué puede hacer? |
| :--- | :--- | :--- |
| 🎓 **Estudiante** | Quien consulta y estudia los recursos | Ver, buscar, filtrar, ordenar, guardar favoritos, calificar, abrir y compartir recursos |
| 👨‍🏫 **Docente** | Quien administra el catálogo | Todo lo del Estudiante **más** crear, editar y eliminar recursos |

---

## 📑 Contenido
1. [Requisitos](#-requisitos)
2. [Crear una cuenta](#-1-crear-una-cuenta-registro)
3. [Iniciar y cerrar sesión](#-2-iniciar-y-cerrar-sesión)
4. [Conocer la pantalla principal](#-3-la-pantalla-principal)
5. [Explorar recursos](#-4-explorar-recursos)
6. [Favoritos](#️-5-favoritos)
7. [Calificar recursos](#-6-calificar-recursos)
8. [Gestión de recursos (Docente)](#-7-gestión-de-recursos-solo-docente)
9. [Mi Perfil](#-8-mi-perfil)
10. [Mensajes y alertas](#-9-mensajes-y-alertas)
11. [Solución de problemas](#-10-solución-de-problemas)

---

## ✅ Requisitos
- Teléfono o emulador con **Android 7.0 (API 24) o superior**.
- **Conexión a internet**: los recursos se descargan de una API en línea y los cambios del Docente se envían a ella.
- Tener una cuenta. Si no la tienes, puedes crearla desde la app (ver sección 1).

---

## 📝 1. Crear una cuenta (Registro)

1. En la pantalla de inicio de sesión toca **"¿No tienes cuenta? Regístrate aquí"**.
2. Completa el formulario **Crear Cuenta**:

| Campo | Qué escribir |
| :--- | :--- |
| **Nombre Completo** | Tu nombre, por ejemplo `Juan Pérez` |
| **Correo Institucional** | Un correo válido, por ejemplo `ejemplo@udb.edu.sv` |
| **Rol del Usuario** | Toca la tarjeta **Estudiante** o **Docente** |
| **Código de acceso docente** | *Solo aparece si eliges Docente* (ver abajo) |
| **Contraseña** | Una contraseña segura (ver requisitos) |
| **Confirmar Contraseña** | La misma contraseña otra vez |

3. Toca **Registrarse**. Si todo es correcto, entrarás directamente a la aplicación.

### 🔐 Requisitos de la contraseña
Mientras escribes, una **lista de requisitos** cambia de gris a verde al cumplirse cada uno. El botón **Registrarse** solo se activa cuando todos están en verde.

- ✅ Mínimo **12 caracteres**
- ✅ Al menos una **letra mayúscula** (A-Z)
- ✅ Al menos una **letra minúscula** (a-z)
- ✅ Al menos un **número** (0-9)
- ✅ Al menos un **carácter especial**: `! @ # $ % ^ & *`
- ✅ Las dos contraseñas **coinciden**

> 💡 **Ejemplo de contraseña válida:** `MiClave2026!Segura`

### 👨‍🏫 Registrarse como Docente
Para crear una cuenta de **Docente** debes escribir el **código de acceso docente**, que entrega la institución. Si el código es incorrecto, aparecerá una alerta y la cuenta no se creará.

> Las cuentas de **Estudiante** se crean libremente, sin código.

### ⚠️ Si tu correo ya está registrado
La app mostrará una **alerta** indicando que *ya existe una cuenta con ese correo*. Toca **Aceptar**, regresa al inicio de sesión (flecha atrás) e ingresa con esa cuenta, o usa otro correo.

---

## 🔑 2. Iniciar y cerrar sesión

### Iniciar sesión
1. Escribe tu **Correo Institucional** y tu **Contraseña**.
2. Toca el ícono del **ojo** para mostrar u ocultar la contraseña.
3. Toca **Ingresar**.

Si algo falla (correo no registrado o contraseña incorrecta), la app muestra una **alerta** explicando el motivo.

### Cuentas de prueba

| Rol | Correo | Contraseña |
| :--- | :--- | :--- |
| 👨‍🏫 Docente | `docente@udb.edu.sv` | `Docente2026!@#$` |
| 🎓 Estudiante | `estudiante@udb.edu.sv` | `Estudiante2026!@#$` |

### Cerrar sesión
Toca el ícono rojo de **salida** (esquina superior derecha) o el botón **Cerrar Sesión** en la pestaña **Mi Perfil**. Volverás a la pantalla de inicio de sesión. Cada cuenta conserva sus propios favoritos.

---

## 🏠 3. La pantalla principal

Al iniciar sesión verás:

```
┌───────────────────────────────────────────┐
│ AprendeApp [Rol]                    [Salir]│  ← Barra superior
│ Universidad Don Bosco • DSM                │
├───────────────────────────────────────────┤
│                                           │
│           Contenido de la pestaña          │
│                                           │
├───────────────────────────────────────────┤
│ Recursos │ Favoritos │ Gestión CRUD │ Perfil│  ← Navegación inferior
└───────────────────────────────────────────┘
```

- **Barra superior:** muestra el nombre de la app, una **etiqueta con tu rol** (Estudiante o Docente) y el botón de cerrar sesión.
- **Navegación inferior:** cambia de pestaña con un toque.

| Pestaña | Estudiante | Docente |
| :--- | :---: | :---: |
| **Recursos** | ✅ | ✅ |
| **Favoritos** | ✅ | ✅ |
| **Gestión CRUD** | ❌ no aparece | ✅ |
| **Mi Perfil** | ✅ | ✅ |

> El botón **Atrás** del teléfono, estando en otra pestaña, te regresa a **Recursos**.

---

## 🔎 4. Explorar recursos

La pestaña **Recursos** muestra el catálogo en **tarjetas**.

### 🃏 La tarjeta de un recurso
Cada tarjeta incluye:
- **Imagen** ilustrativa del recurso.
- Etiqueta de **tipo** (Libro, Video, Artículo o Tutorial) y su **ID** (`ID: #6`).
- **Título** y **descripción** corta.
- **Calificación promedio** (⭐ 4.5) o `N/A` si aún nadie lo ha calificado.

Y estos botones:

| Botón | Qué hace |
| :--- | :--- |
| ❤️ **Corazón** | Guarda o quita el recurso de tus Favoritos |
| ⭐ **Calificar** | Abre la ventana para calificar de 1 a 5 estrellas |
| 🔗 **Abrir** | Abre el enlace del recurso en tu navegador |
| 📤 **Compartir** | Comparte título, descripción y enlace por WhatsApp, correo, etc. |

*(El Docente verá además los botones de editar y eliminar; ver sección 7.)*

### 🔍 Buscar
Escribe en la barra **"Buscar por ID, título o tipo…"**. La lista se filtra mientras escribes. Puedes buscar por:
- **ID**: `6`
- **Título**: `clean`
- **Tipo**: `video`

### 🏷️ Filtrar por tipo
Debajo del buscador hay botones de filtro: **Todos · Libro · Video · Artículo · Tutorial**. Toca uno para ver solo ese tipo y **Todos** para quitar el filtro. El filtro y la búsqueda funcionan **juntos**.

### ↕️ Ordenar
Toca el ícono de **ordenar** (junto al buscador) y elige:
- **Título (A-Z)**
- **Título (Z-A)**
- **Mejor Calificados**
- **Más Recientes (ID)**

### 🔄 Actualizar
Toca el ícono de **refrescar** para volver a descargar el catálogo desde la API. Verás un indicador de carga mientras se actualiza.

Si no hay resultados, la app muestra *"No se encontraron recursos"* y te sugiere ajustar los filtros o la búsqueda.

---

## ❤️ 5. Favoritos

Guarda los recursos que quieres tener a mano.

**Agregar:** en cualquier tarjeta toca el **corazón** ♡. Se pone rojo ❤️ y aparece el aviso *"Agregado a tus favoritos"*.

**Quitar:** toca de nuevo el corazón rojo. Aparece *"Eliminado de favoritos"*.

**Ver tus favoritos:** abre la pestaña **Favoritos**. Verás **solo** los recursos que marcaste, con el contador *"N recursos guardados en tu dispositivo"*. La pestaña muestra una **marca** cuando tienes favoritos.

Si aún no tienes ninguno, verás el mensaje *"Aún no tienes favoritos"* con una guía.

> 📌 Los favoritos se guardan **en tu teléfono** y son **personales**: cada cuenta tiene los suyos y se conservan aunque cierres la aplicación o la sesión.

---

## ⭐ 6. Calificar recursos

1. En la tarjeta del recurso toca **Calificar**.
2. En la ventana **Calificar Recurso**, toca de **1 a 5 estrellas**. Abajo verás cuántas elegiste (por ejemplo, *"4 de 5 estrellas"*).
3. Toca **Enviar Calificación**.

La app envía tu voto a la API y **recalcula el promedio que ven todos los usuarios**.

**Reglas importantes:**
- Cada usuario tiene **un solo voto por recurso**.
- Si vuelves a calificar el mismo recurso, tu voto anterior **se reemplaza** (no se suma uno nuevo), así el promedio no se altera de forma injusta.
- Necesitas conexión a internet para calificar; si falla, verás un aviso y tu voto no se guarda.

---

## 🛠️ 7. Gestión de recursos (solo Docente)

La pestaña **Gestión CRUD** permite administrar el catálogo. Cada acción se envía a la API en línea:

| Acción | Operación |
| :--- | :--- |
| ➕ Crear | `POST` |
| ✏️ Editar | `PUT` |
| 🗑️ Eliminar | `DELETE` |

> Si entras con cuenta de **Estudiante**, la pestaña no aparece. Si algún estudiante intentara acceder, vería *"Acceso Exclusivo para Docentes"*.

### ➕ Agregar un recurso
1. Toca **Nuevo Recurso**.
2. Completa el formulario:

| Campo | Descripción | ¿Obligatorio? |
| :--- | :--- | :---: |
| **Título del Recurso** | Nombre del recurso | ✅ |
| **Tipo de Recurso** | Libro, Video, Artículo o Tutorial | ✅ |
| **Descripción Detallada** | De qué trata | ✅ |
| **Enlace / URL de Acceso** | Debe empezar con `http://` o `https://` | ✅ |
| **URL de Imagen Ilustrativa** | Imagen que se muestra en la tarjeta | Opcional |

3. Toca **Guardar en API**. Verás un indicador de carga y, al terminar, el aviso *"Recurso creado exitosamente (POST)"*.

Si falta un campo o el enlace no es válido, el formulario muestra el error en rojo y **no se cierra**, para que no pierdas lo escrito.

### ✏️ Editar un recurso
1. Busca el recurso con la barra de búsqueda de la pestaña (por ID, título o tipo).
2. Toca el ícono del **lápiz** de su tarjeta.
3. El formulario se abre **con los datos ya cargados**. Cambia lo que necesites.
4. Toca **Actualizar Recurso**. Verás *"Recurso actualizado exitosamente (PUT)"*.

### 🗑️ Eliminar un recurso
1. Toca el ícono de la **papelera** del recurso.
2. Aparece una **ventana de confirmación**: *"¿Eliminar recurso?"*, con el título y el ID.
3. Toca **Sí, eliminar** para confirmar o **Cancelar** para arrepentirte.
4. Verás *"Recurso eliminado exitosamente (DELETE)"*.

> ⚠️ Eliminar es **permanente** y lo ven todos los usuarios.

Si la API no responde, la app muestra un **error** y el cambio **no** se aplica: nunca verás un éxito falso.

---

## 👤 8. Mi Perfil

Muestra:
- Tu **nombre** y **correo**.
- Tu **rol** (etiqueta "Rol: Estudiante" o "Rol: Docente").
- **Privilegios del rol:** un resumen de lo que puedes hacer.
- **Conexión a API REST:** la dirección (endpoint) de la que se obtienen los datos.
- El botón **Cerrar Sesión**.

---

## 💬 9. Mensajes y alertas

La app te avisa de lo que ocurre de tres formas:

| Tipo | Cuándo aparece |
| :--- | :--- |
| 🟦 **Aviso inferior (Snackbar)** | Confirmaciones: favorito agregado/quitado, recurso creado/editado/eliminado, calificación enviada, sesión cerrada |
| ⚠️ **Alerta (ventana con *Aceptar*)** | Errores de inicio de sesión y de registro (cuenta existente, contraseña incorrecta, código docente inválido…) |
| ⏳ **Indicador de carga** | Mientras se descargan o se envían datos a la API |

**Alertas más comunes:**

| Mensaje | Qué significa | Qué hacer |
| :--- | :--- | :--- |
| *Ya existe una cuenta registrada con este correo* | Ese correo ya fue registrado | Inicia sesión o usa otro correo |
| *Usuario no encontrado con ese correo* | No hay cuenta con ese correo | Revisa el correo o regístrate |
| *Contraseña incorrecta* | La contraseña no coincide | Inténtalo de nuevo |
| *La contraseña no cumple con todos los requisitos* | Falta algún requisito | Revisa la lista de requisitos en verde |
| *El formato del correo es inválido* | Correo mal escrito | Corrígelo (ej. `nombre@udb.edu.sv`) |
| *El código de acceso docente es incorrecto* | Código erróneo al registrarte como Docente | Verifica el código con la institución |
| *No se pudo guardar / eliminar en la API* | Falló la conexión | Revisa tu internet e inténtalo de nuevo |

---

## 🧰 10. Solución de problemas

**❓ No aparece la pestaña "Gestión CRUD".**  
Esa pestaña es solo para **Docentes**. Verifica en la barra superior que tu etiqueta de rol diga *Docente*.

**❓ La lista de recursos está vacía o aparece una franja de "datos locales".**  
La app no pudo conectarse a la API y muestra los últimos datos descargados (modo lectura). Revisa tu conexión y toca **refrescar**. En ese modo no puedes crear, editar, eliminar ni calificar.

**❓ No puedo registrarme.**  
Comprueba que: el nombre y el correo no estén vacíos, el correo sea válido, **todos** los requisitos de contraseña estén en verde, las contraseñas coincidan y, si eres Docente, hayas escrito el código de acceso.

**❓ Mis favoritos no aparecen.**  
Los favoritos son por cuenta: si entras con otro correo verás los favoritos de esa cuenta, no los de la anterior.

**❓ El botón "Abrir" no hace nada.**  
El recurso puede no tener un enlace válido, o tu teléfono no tiene un navegador instalado.

**❓ Olvidé mi contraseña.**  
La aplicación no incluye recuperación de contraseña. Puedes crear una cuenta nueva con otro correo.

---

## 🔒 Resumen de permisos por rol

| Función | 🎓 Estudiante | 👨‍🏫 Docente |
| :--- | :---: | :---: |
| Ver recursos | ✅ | ✅ |
| Buscar, filtrar y ordenar | ✅ | ✅ |
| Abrir enlaces y compartir | ✅ | ✅ |
| Guardar favoritos | ✅ | ✅ |
| Calificar (1 a 5 estrellas) | ✅ | ✅ |
| Crear recursos (`POST`) | ❌ | ✅ |
| Editar recursos (`PUT`) | ❌ | ✅ |
| Eliminar recursos (`DELETE`) | ❌ | ✅ |

---

*AprendeApp — Universidad Don Bosco · Desarrollo de Software para Móviles (DSM)*
