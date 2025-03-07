package pl.syntaxdevteam.cleanerx.api

import org.bukkit.plugin.java.JavaPlugin
import pl.syntaxdevteam.cleanerx.base.WordFilter

/**
 * Implementacja interfejsu [CleanerXAPI] pluginu CleanerX.
 *
 * Klasa ta wykorzystuje wewnętrzną logikę zdefiniowaną w klasie [WordFilter],
 * która odpowiada za ładowanie konfiguracji oraz filtrowanie wiadomości.
 * Dzięki tej implementacji metody [containsBannedWord] oraz [censorMessage] działają spójnie
 * z konfiguracją pluginu CleanerX, korzystając z listy słów niecenzuralnych (banned) i białych (whitelist).
 *
 * @constructor Tworzy instancję API korzystając z podanego obiektu [plugin] klasy [JavaPlugin].
 * W trakcie inicjalizacji tworzony jest obiekt [WordFilter], który automatycznie ładuje
 * konfigurację słów niecenzuralnych oraz słów dozwolonych z plików YAML.
 *
 * @param plugin Instancja głównego pluginu CleanerX, używana do pobierania konfiguracji,
 *               zapisywania zasobów oraz logowania zdarzeń.
 */
class CleanerXApiImpl(plugin: JavaPlugin) : CleanerXAPI {
    private val wordFilter = WordFilter(plugin)

    /**
     * Sprawdza, czy przekazana wiadomość zawiera niecenzuralne słowa.
     *
     * Metoda deleguje wywołanie do [WordFilter.containsBannedWord], która analizuje wiadomość
     * w kontekście załadowanych list słów niecenzuralnych i dozwolonych.
     *
     * @param message Wiadomość, która ma zostać sprawdzona.
     * @return `true` jeśli wiadomość zawiera niecenzuralne słowa, `false` w przeciwnym przypadku.
     */
    override fun containsBannedWord(message: String): Boolean {
        return wordFilter.containsBannedWord(message)
    }

    /**
     * Cenzuruje podaną wiadomość, zastępując wykryte niecenzuralne słowa znakami cenzury.
     *
     * Metoda deleguje wywołanie do [WordFilter.censorMessage], która przetwarza wiadomość
     * na podstawie konfiguracji pluginu CleanerX. W zależności od wartości parametru [fullCensorship],
     * metoda dokonuje pełnej lub częściowej cenzury wykrytych słów.
     *
     * @param message Wiadomość, która ma zostać poddana cenzurze.
     * @param fullCensorship Określa tryb cenzury:
     *        - `true` – pełna cenzura, całe słowo zostanie zastąpione,
     *        - `false` – tylko część słowa zostanie cenzurowana.
     * @return Zwraca wiadomość po modyfikacji, w której niecenzuralne słowa zostały zastąpione
     *         znakami cenzury.
     */
    override fun censorMessage(message: String, fullCensorship: Boolean): String {
        return wordFilter.censorMessage(message, fullCensorship)
    }
}
