package fr.fms.spring_things.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.logging.Level;
import static org.assertj.core.api.Assertions.assertThat;

class HelpersTest {

  /**
    * Teste si isNullOrEmpty renvoie les bonnes valeurs si null ou empty
   */
  @Test
  void isNullOrEmptyReturnsTrueForNullEmptyAndBlank() {
    assertThat(Helpers.isNullOrEmpty(null)).isTrue();
    assertThat(Helpers.isNullOrEmpty("")).isTrue();
    assertThat(Helpers.isNullOrEmpty("   ")).isTrue();
  }

  /**
   * Teste si isNullOrEmpty renvoie false pour du texte
   */
  @Test
  void isNullOrEmptyReturnsFalseForText() {
    assertThat(Helpers.isNullOrEmpty("Samsung")).isFalse();
  }

  /**
   * Teste si format money formate bien les prix
   */
  @Test
  void formatMoneyFormatsWithTwoDecimals() {
    assertThat(Helpers.formatMoney(new BigDecimal("12.5"))).isEqualTo("12,50");
    assertThat(Helpers.formatMoney(null)).isEqualTo("0,00");
  }

  /**
   * Teste si le formatage du texte en couleur fonctionne
   */
  @Test
  void formatWithLevelColorsReturnsOriginalTextWhenLevelIsNull() {
    String text = "Hello";

    String result = Helpers.formatWithLevelColors(String.valueOf((Level) null), text);

    assertThat(result).isEqualTo("Hello");
  }


  /**
   * Teste si le formatage du texte avec un niveau de log info
   */
  @Test
  void formatWithLevelColorsFormatInfoLevel(){
    String text = "Hello";
    String levelName ="INFO";
    String result = Helpers.formatWithLevelColors(levelName,text);
    assertThat(result).isEqualTo("\u001B[36mHello\u001B[0m");
  }

  void uiErrorFormatting(){

  }
}
