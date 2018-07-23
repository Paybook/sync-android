package com.paybook.sync.entities.paybook;

import com.paybook.sync.entities.Currency;
import java.math.BigDecimal;
import java.util.List;

public class PaybookBuilder {

  private String imageUrl;
  private String title;
  private BigDecimal balance;
  private String userImageUrl;
  private String id;
  private String icon;
  private String paybookThemeId;
  private String paybookThemeTypeId;
  private List<Paybook> children;
  private Currency currency;
  private PaybookTheme theme;

  public static PaybookBuilder builder() {
    return new PaybookBuilder();
  }

  public PaybookBuilder setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
    return this;
  }

  public PaybookBuilder setTitle(String title) {
    this.title = title;
    return this;
  }

  public PaybookBuilder setBalance(BigDecimal balance) {
    this.balance = balance;
    return this;
  }

  public PaybookBuilder setUserImageUrl(String userImageUrl) {
    this.userImageUrl = userImageUrl;
    return this;
  }

  public PaybookBuilder setId(String id) {
    this.id = id;
    return this;
  }

  public PaybookBuilder setIcon(String icon) {
    this.icon = icon;
    return this;
  }

  public PaybookBuilder setPaybookThemeId(String paybookThemeId) {
    this.paybookThemeId = paybookThemeId;
    return this;
  }

  public PaybookBuilder setPaybookThemeTypeId(String paybookThemeTypeId) {
    this.paybookThemeTypeId = paybookThemeTypeId;
    return this;
  }

  public PaybookBuilder setChildren(List<Paybook> children) {
    this.children = children;
    return this;
  }

  public PaybookBuilder setCurrency(Currency currency) {
    this.currency = currency;
    return this;
  }

  public PaybookBuilder setTheme(PaybookTheme theme) {
    this.theme = theme;
    return this;
  }

  public Paybook build() {
    return new Paybook(id, paybookThemeId, paybookThemeTypeId, title, imageUrl, userImageUrl,
        balance, icon, currency, children, theme);
  }
}