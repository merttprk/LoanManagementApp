# Loan Management Application - Mimari Dokümantasyon

## Genel Mimari Bakış

Bu uygulama, Clean Architecture ve SOLID prensiplerini temel alan bir yapıda geliştirilmiştir. Uygulama, aşağıdaki katmanlardan oluşmaktadır:

![Clean Architecture](https://blog.cleancoder.com/uncle-bob/images/2012-08-13-the-clean-architecture/CleanArchitecture.jpg)

### 1. Sunum Katmanı (Presentation Layer)
- Kullanıcı arayüzü bileşenleri
- ViewModel sınıfları
- Fragment ve Activity sınıfları

### 2. Domain Katmanı (Domain Layer)
- Use Case sınıfları
- Entity modelleri
- Repository arayüzleri

### 3. Veri Katmanı (Data Layer)
- Repository implementasyonları
- Veri kaynakları (Yerel veritabanı, API, vb.)
- DTO (Data Transfer Object) sınıfları

## Uygulanan Tasarım Desenleri

### 1. Strateji Deseni (Strategy Pattern)

Strateji deseni, farklı kredi türleri için faiz hesaplama stratejilerini yönetmek amacıyla uygulanmıştır. Bu desen, farklı algoritmaları (faiz hesaplama yöntemleri) kapsülleyerek, çalışma zamanında değiştirilebilir hale getirmeyi sağlar.

#### Arayüz:
```kotlin
interface InterestStrategy {
    fun calculateInterest(loan: Loan, months: Int): Double
    fun getRecommendedTerm(): Int
    fun getBaseRate(): Double
}
```

#### Stratejiler:
- **PersonalLoanStrategy**: Basit faiz formülü kullanır
- **AutoLoanInterestStrategy**: Aylık bileşik faiz formülü kullanır
- **MortgageInterestStrategy**: Aylık bileşik faiz formülü kullanır
- **BusinessLoanInterestStrategy**: Üç aylık bileşik faiz formülü kullanır

#### Örnek Uygulama:
```kotlin
class BusinessLoanInterestStrategy : InterestStrategy {
    companion object {
        private const val BASE_RATE = 9.75 // İşletme kredileri için %9.75 temel faiz oranı
        private const val RECOMMENDED_TERM_MONTHS = 84 // 7 yıl
    }
    
    override fun calculateInterest(loan: Loan, months: Int): Double {
        val principal = loan.principalAmount
        val annualRate = loan.interestRate / 100
        val quarterlyRate = annualRate / 4
        val quarters = months / 3.0
        
        val totalAmount = principal * (1 + quarterlyRate).pow(quarters)
        val interest = totalAmount - principal
        
        return (interest * 100).roundToInt() / 100.0
    }
    
    override fun getRecommendedTerm(): Int = RECOMMENDED_TERM_MONTHS
    
    override fun getBaseRate(): Double = BASE_RATE
}
```

### 2. Durum Deseni (State Pattern)

Durum deseni, kredi durumlarını yönetmek ve karmaşık if-else yapılarını ortadan kaldırmak için uygulanmıştır. Bu desen, bir nesnenin iç durumu değiştiğinde davranışının da değişmesini sağlar.

#### Arayüz:
```kotlin
interface LoanState {
    fun getStateName(): String
    fun updateLoan(loan: Loan): Loan
    fun handleDueDate(loan: Loan, currentDate: Date): LoanState
}
```

#### Durumlar:
- **ActiveLoanState**: Düzenli ödemeli aktif krediler için
- **PaidLoanState**: Tamamen ödenmiş krediler için
- **OverdueLoanState**: Ödemesi gecikmiş krediler için
- **DefaultLoanState**: Yasal takipteki krediler için

#### Örnek Uygulama:
```kotlin
class OverdueLoanState : LoanState {
    override fun getStateName(): String = "Overdue"
    
    override fun updateLoan(loan: Loan): Loan {
        loan.status = "Overdue"
        // Gecikme faizi hesaplama ve diğer işlemler
        return loan
    }
    
    override fun handleDueDate(loan: Loan, currentDate: Date): LoanState {
        val daysSinceOverdue = calculateDaysBetween(loan.dueDate, currentDate)
        
        return if (loan.remainingAmount <= 0) {
            PaidLoanState()
        } else if (daysSinceOverdue > 90) {
            DefaultLoanState()
        } else {
            this
        }
    }
}
```

## MVVM Mimarisi

Model-View-ViewModel (MVVM) mimarisi, kullanıcı arayüzü mantığını iş mantığından ayırmak için uygulanmıştır.

### ViewModel Örneği:
```kotlin
class LoanDetailsViewModel @Inject constructor(
    private val getLoanUseCase: GetLoanUseCase,
    private val calculateInterestUseCase: CalculateInterestUseCase
) : ViewModel() {
    
    private val _loanState = MutableStateFlow<LoanDetailsState>(LoanDetailsState.Loading)
    val loanState: StateFlow<LoanDetailsState> = _loanState
    
    fun loadLoan(loanId: String) {
        viewModelScope.launch {
            try {
                val loan = getLoanUseCase(loanId)
                _loanState.value = LoanDetailsState.Success(loan)
            } catch (e: Exception) {
                _loanState.value = LoanDetailsState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun calculateInterest(loanId: String, months: Int) {
        viewModelScope.launch {
            try {
                val interest = calculateInterestUseCase(loanId, months)
                // İlgili state güncelleme
            } catch (e: Exception) {
                // Hata durumu
            }
        }
    }
}
```

## Bağımlılık Enjeksiyonu

Uygulama, bağımlılıkları yönetmek için Hilt kütüphanesini kullanmaktadır. Bu, test edilebilirliği artırır ve bağımlılıkların daha kolay yönetilmesini sağlar.

### Örnek Modül:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideLoanRepository(
        loanDao: LoanDao,
        loanApi: LoanApi
    ): LoanRepository {
        return LoanRepositoryImpl(loanDao, loanApi)
    }
    
    @Provides
    @Singleton
    fun provideInterestStrategyFactory(): InterestStrategyFactory {
        return InterestStrategyFactoryImpl()
    }
}
```

## Birim Testleri

Uygulamanın güvenilirliğini sağlamak için kapsamlı birim testleri uygulanmıştır.

### Örnek Test:
```kotlin
@RunWith(JUnit4::class)
class LoanInterestStrategyTest {
    
    @Test
    fun `test personal loan interest calculation`() {
        // Given
        val loan = TestUtil.createPersonalLoan(
            principalAmount = 10000.0,
            interestRate = 12.5
        )
        val strategy = PersonalLoanStrategy()
        
        // When
        val interest = strategy.calculateInterest(loan, 12)
        
        // Then
        assertEquals(1250.0, interest, 0.01)
    }
    
    @Test
    fun `test auto loan interest calculation`() {
        // Given
        val loan = TestUtil.createAutoLoan(
            principalAmount = 20000.0,
            interestRate = 7.5
        )
        val strategy = AutoLoanInterestStrategy()
        
        // When
        val interest = strategy.calculateInterest(loan, 12)
        
        // Then
        assertEquals(1545.41, interest, 0.01)
    }
}
```

## UI Bileşenleri

Uygulama, yeniden kullanılabilir UI bileşenleri kullanarak modüler bir yapıya sahiptir:

### CustomEditText
Özel giriş alanı bileşeni, farklı input tipleri için özelleştirilebilir.

### PrimaryButton
Uygulama genelinde tutarlı görünüm sağlayan standart buton bileşeni.

### ReusableListItem
Farklı liste öğeleri için kullanılabilen genel liste öğesi bileşeni.

### LoanCardView
Kredi detaylarını göstermek için özel tasarlanmış kart bileşeni.

## Sonuç

Bu mimari dokümantasyon, Loan Management Application'ın genel yapısını, kullanılan tasarım desenlerini ve uygulama prensiplerini açıklamaktadır. Uygulama, Clean Architecture, SOLID prensipleri ve modern tasarım desenlerini kullanarak modüler, test edilebilir ve sürdürülebilir bir yapıda geliştirilmiştir.
