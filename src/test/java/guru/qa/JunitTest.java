package guru.qa;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class JunitTest {

    @ValueSource(strings = {"Java", "Python"})
    @ParameterizedTest(name = "Результаты поиска не пустые для запроса {0}")
    void searchTest(String testData) {
        open("https://duckduckgo.com/");
        $(".js-search-input").setValue(testData).pressEnter();
        $$(".results--main").shouldBe(CollectionCondition.sizeGreaterThan(0));
    }

    @CsvSource(value = {
            "Реестр,   О федеральных государственных информационных системах, обеспечивающих предоставление",
            "Акт,  Федеральный закон от 17 июля 2009 г. № 172-ФЗ",
    })
    @ParameterizedTest(name = "Результаты поиска содержат текст \"{1}\" для запроса: \"{0}\"")
    void searchTestWithParameters(String testData, String expectedResult) {
        open("https://digital.gov.ru/");
        $("#searchButton").click();
        $("#search").setValue(testData);
        $("button[type='submit']").click();
        $$("div.searchresults ").first().shouldHave(text(expectedResult));
    }

    static Stream<Arguments> changeLangTest() {
        return Stream.of(
                Arguments.of(Lang.Рус, List.of("Новости", "Объявления", "Пресс-служба", "Сайты МГУ", "Адреса", "Карта сайта", "Поиск")),
                Arguments.of(Lang.Eng, List.of("MSU Online", "Addresses", "Site map", "Search"))
        );
    }
    @MethodSource("changeLangTest")
    @ParameterizedTest(name = "Для языка {0} отображаются пункты меню {1}")
    void selenideSiteMenuTest(Lang lang, List<String> expectedButtons) {
        open("https://www.msu.ru/");
        $$(".lang-choose a").find(text(lang.name())).click();
        $$(".nav a").filter(visible).shouldHave(CollectionCondition.texts(expectedButtons));
    }
}
