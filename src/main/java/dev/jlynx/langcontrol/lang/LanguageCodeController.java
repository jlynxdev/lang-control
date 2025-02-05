package dev.jlynx.langcontrol.lang;

import dev.jlynx.langcontrol.lang.dto.LanguageCodeOverview;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * A REST controller for providing information about language codes.
 * <p>
 * This controller handles requests related to retrieving the list of available language codes
 * defined in the {@link LanguageCode} enumeration.
 * </p>
 */
@RestController
@RequestMapping("${apiPref}/lang")
public class LanguageCodeController {

    /**
     * Retrieves a list of all available language codes and their corresponding details.
     *
     * @return a {@link ResponseEntity} containing a {@link List} of {@link LanguageCodeOverview}
     * representing all available language codes.
     */
    @GetMapping
    public ResponseEntity<List<LanguageCodeOverview>> getAllAvailableLanguageCodes() {
        List<LanguageCodeOverview> langCodeOverviewList = Arrays.stream(LanguageCode.values())
                .map(LanguageCodeOverview::fromLanguageCode)
                .toList();
        return ResponseEntity.ok(langCodeOverviewList);
    }
}
