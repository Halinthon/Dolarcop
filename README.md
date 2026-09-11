# Dólar COP 🇨🇴💵

App Android nativa (Kotlin + Jetpack Compose) que calcula el valor en **pesos colombianos (COP)**
de cualquier moneda del mundo, y muestra un **widget de escritorio** con el valor actual del dólar
(USD → COP), actualizado automáticamente en segundo plano.

## ⚠️ Nota importante sobre el "ícono con el valor del dólar"

Android **no permite** que el ícono de lanzador de una app muestre texto/valores dinámicos —
los íconos son imágenes estáticas. Por eso esta app resuelve tu necesidad real (ver fácilmente
el valor del dólar desde el escritorio) con un **widget** (que sí se actualiza automáticamente
y se puede colocar junto a los demás íconos), más un ícono de app fijo con el símbolo `$`.

## Funcionalidades

- **Widget de escritorio** "USD → COP" que se actualiza cada 6 horas automáticamente (WorkManager).
- **Calculadora** dentro de la app: elige cualquier moneda del mundo (con buscador), ingresa un
  monto y obtén al instante su equivalente en pesos colombianos.
- **Funciona sin internet**: guarda en caché local (Room) la última tasa conocida.
- **API gratuita, sin necesidad de registro ni API Key**: [open.er-api.com](https://www.exchangerate-api.com/docs/free)
  (actualiza tasas aprox. cada 24h en su capa gratuita). Si más adelante quieres actualizaciones
  más frecuentes, puedes migrar a una API con key (ver sección "Mejoras futuras").

## Arquitectura

```
UI (Jetpack Compose) → ViewModel → Repository → Retrofit (API) + Room (caché local)
                                             ↳ SharedPreferences (lectura rápida para el widget)
WorkManager → refresca tasas cada 6h → actualiza el AppWidgetProvider
```

## Estructura del proyecto

```
DolarCOP/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/dolarcop/app/
│       │   ├── MainActivity.kt
│       │   ├── data/            (API, Room, Repositorio, lista de monedas)
│       │   ├── viewmodel/       (lógica de la calculadora)
│       │   ├── ui/              (pantallas Compose + tema)
│       │   └── widget/          (widget de escritorio + tareas en segundo plano)
│       └── res/                 (layouts, strings, íconos, config del widget)
├── .github/workflows/build-apk.yml   (compila el APK automáticamente en GitHub Actions)
├── build.gradle.kts / settings.gradle.kts / gradle.properties
└── README.md
```

## 🚀 Cómo obtener el APK

### Opción A — Compilar automáticamente con GitHub Actions (recomendada)

1. Crea un repositorio nuevo en GitHub (público o privado).
2. Sube este proyecto tal cual:
   ```bash
   cd DolarCOP
   git init
   git add .
   git commit -m "Proyecto inicial DolarCOP"
   git branch -M main
   git remote add origin https://github.com/TU_USUARIO/TU_REPO.git
   git push -u origin main
   ```
3. Ve a la pestaña **Actions** de tu repositorio en GitHub. El workflow **"Build APK"** se
   ejecutará automáticamente (también puedes lanzarlo manualmente con el botón "Run workflow").
4. Cuando termine (2-4 minutos), entra a la ejecución y descarga el artefacto
   **`DolarCOP-debug-apk`** — ahí está tu `app-debug.apk`, listo para instalar en cualquier Android.

### Opción B — Compilar en Android Studio

1. Abre Android Studio → **Open** → selecciona la carpeta `DolarCOP`.
2. Espera a que Android Studio sincronice Gradle (te ofrecerá crear el *Gradle Wrapper*
   automáticamente si no existe; acepta).
3. Menú **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
4. El APK quedará en `app/build/outputs/apk/debug/app-debug.apk`.

## 📲 Instalar el APK en tu celular

1. Copia el `.apk` a tu teléfono (o descárgalo directo desde GitHub Actions en el navegador del celular).
2. Actívalo la primera vez en **Ajustes → Seguridad → Instalar apps de fuentes desconocidas**.
3. Abre el archivo `.apk` para instalarlo.
4. Para agregar el widget: mantén presionado el escritorio → **Widgets** → busca **"Dólar COP"** →
   arrástralo a la pantalla.

## Posibles mejoras futuras

- Migrar a una API con key (ej. exchangerate-api.com plan gratuito con registro) para
  actualizaciones cada hora en vez de cada 24h.
- Historial/gráfico de la tasa USD-COP en el tiempo.
- Notificación cuando el dólar suba/baje de un umbral definido por el usuario.
- Publicación firmada en Google Play (requiere generar un keystore de release).

---
Generado como proyecto base — libre de modificar y extender.
