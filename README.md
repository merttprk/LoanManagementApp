# LoanManagementApp

Modern bir finansal teknoloji kredi yönetim uygulaması. Bu uygulama, kişisel krediler, otomobil kredileri, konut kredileri ve işletme kredileri gibi çeşitli finansal ürünleri yönetmeyi sağlar.

## Mimari Yapı

Bu uygulama, Clean Architecture ve MVVM (Model-View-ViewModel) prensiplerine göre tasarlanmıştır. Uygulama, aşağıdaki katmanlardan oluşmaktadır:

### 1. Sunum Katmanı (Presentation Layer)
- **View**: Kullanıcı arayüzü bileşenleri ve ekranlar
- **ViewModel**: Görünüm durumunu yönetir ve iş mantığını Use Case'ler aracılığıyla çağırır
- **State**: UI durumunu temsil eden veri sınıfları

### 2. Domain Katmanı (Domain Layer)
- **Use Cases**: İş mantığı operasyonları
- **Entities**: İş mantığı modelleri
- **Repository Interfaces**: Veri katmanı ile iletişim için arayüzler

### 3. Veri Katmanı (Data Layer)
- **Repository Implementations**: Domain katmanındaki arayüzlerin uygulamaları
- **Data Sources**: Yerel veritabanı, API veya diğer veri kaynakları


## Tasarım Desenleri

### 1. Strateji Deseni (Strategy Pattern)
Farklı kredi türleri için faiz hesaplama stratejileri uygulanmıştır:

- **InterestStrategy**: Faiz hesaplama stratejisi için arayüz
  - `calculateInterest(loan, months)`: Belirli bir kredi ve süre için faiz hesaplar
  - `getRecommendedTerm()`: Önerilen kredi süresini döndürür
  - `getBaseRate()`: Temel faiz oranını döndürür

- **Concrete Strategies**:
  - `PersonalLoanStrategy`: Kişisel krediler için basit faiz hesaplama (%12.5 temel oran, 36 ay süre)
  - `AutoLoanInterestStrategy`: Taşıt kredileri için aylık bileşik faiz (%7.5 temel oran, 60 ay süre)
  - `MortgageInterestStrategy`: Konut kredileri için aylık bileşik faiz (%5.25 temel oran, 360 ay süre)
  - `BusinessLoanInterestStrategy`: İşletme kredileri için üç aylık bileşik faiz (%9.75 temel oran, 84 ay süre)

### 2. Durum Deseni (State Pattern)
Kredi durumlarını yönetmek için durum deseni uygulanmıştır:

- **LoanState**: Kredi durumu için arayüz
  - `getStateName()`: Durum adını döndürür
  - `updateLoan()`: Duruma göre krediyi günceller
  - `handleDueDate()`: Vade tarihi geçtiğinde sonraki durumu belirler

- **Concrete States**:
  - `ActiveLoanState`: Düzenli ödemeli aktif krediler için
  - `PaidLoanState`: Tamamen ödenmiş krediler için
  - `OverdueLoanState`: Ödemesi gecikmiş krediler için
  - `DefaultLoanState`: Yasal takipteki krediler için

## Bileşen Tabanlı UI Mimarisi

Uygulama, yeniden kullanılabilir UI bileşenleri kullanarak modüler bir yapıya sahiptir:

- **CustomEditText**: Genel amaçlı giriş alanı
- **PrimaryButton**: Standart buton bileşeni
- **ReusableListItem**: Yeniden kullanılabilir liste öğesi
- **LoanCardView**: Kredi detaylarını göstermek için özel bileşen

## Kullanıcı Kimlik Doğrulama

Kullanıcı kimlik doğrulama sistemi aşağıdaki özelliklere sahiptir:

- Güvenli kimlik bilgisi depolama
- Oturum yönetimi
- Çıkış yapma işlevselliği

## Birim Testleri

Uygulamanın güvenilirliğini sağlamak için kapsamlı birim testleri uygulanmıştır:

- **LoanInterestStrategyTest**: Farklı kredi türleri için faiz hesaplama stratejilerini test eder
- **LoanStateTest**: Kredi durumu geçişlerini ve davranışlarını test eder
- **LoanRepositoryTest**: Repository fonksiyonlarını test eder

## Kurulum ve Çalıştırma

1. Projeyi klonlayın:
```
git clone https://github.com/username/LoanManagementApp.git
```

2. Android Studio'da projeyi açın

3. Gradle senkronizasyonunu bekleyin

4. Uygulamayı bir emülatör veya gerçek cihazda çalıştırın

## Teknolojiler ve Kütüphaneler

- **Dil**: Kotlin
- **UI**:Jetpack Compose
- **Asenkron İşlemler**: Coroutines, Flow
- **Bağımlılık Enjeksiyonu**: Hilt
- **Veritabanı**: Room
- **Ağ İstekleri**: Retrofit
- **Test**: JUnit, Mockito

## Katkıda Bulunma

1. Bu repo'yu fork edin
2. Yeni bir branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## Lisans

Bu proje [MIT Lisansı](LICENSE) altında lisanslanmıştır.
