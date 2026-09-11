package com.dolarcop.app.data

data class Currency(
    val code: String,
    val name: String,
    val flag: String
)

/**
 * Listado de monedas soportadas. La API devuelve tasas para más de 160 monedas;
 * aquí seleccionamos las más comunes/solicitadas para una mejor experiencia de usuario.
 * Puedes ampliar esta lista libremente: solo el "code" debe existir en la respuesta de la API.
 */
object Currencies {
    val ALL: List<Currency> = listOf(
        Currency("USD", "Dólar estadounidense", "🇺🇸"),
        Currency("EUR", "Euro", "🇪🇺"),
        Currency("GBP", "Libra esterlina", "🇬🇧"),
        Currency("JPY", "Yen japonés", "🇯🇵"),
        Currency("CNY", "Yuan chino", "🇨🇳"),
        Currency("CAD", "Dólar canadiense", "🇨🇦"),
        Currency("AUD", "Dólar australiano", "🇦🇺"),
        Currency("CHF", "Franco suizo", "🇨🇭"),
        Currency("BRL", "Real brasileño", "🇧🇷"),
        Currency("MXN", "Peso mexicano", "🇲🇽"),
        Currency("ARS", "Peso argentino", "🇦🇷"),
        Currency("CLP", "Peso chileno", "🇨🇱"),
        Currency("PEN", "Sol peruano", "🇵🇪"),
        Currency("UYU", "Peso uruguayo", "🇺🇾"),
        Currency("BOB", "Boliviano", "🇧🇴"),
        Currency("PYG", "Guaraní paraguayo", "🇵🇾"),
        Currency("VES", "Bolívar venezolano", "🇻🇪"),
        Currency("KRW", "Won surcoreano", "🇰🇷"),
        Currency("INR", "Rupia india", "🇮🇳"),
        Currency("RUB", "Rublo ruso", "🇷🇺"),
        Currency("SEK", "Corona sueca", "🇸🇪"),
        Currency("NOK", "Corona noruega", "🇳🇴"),
        Currency("DKK", "Corona danesa", "🇩🇰"),
        Currency("NZD", "Dólar neozelandés", "🇳🇿"),
        Currency("ZAR", "Rand sudafricano", "🇿🇦"),
        Currency("SGD", "Dólar de Singapur", "🇸🇬"),
        Currency("HKD", "Dólar de Hong Kong", "🇭🇰"),
        Currency("AED", "Dirham de EAU", "🇦🇪"),
        Currency("TRY", "Lira turca", "🇹🇷"),
        Currency("PLN", "Zloty polaco", "🇵🇱")
    )
}
