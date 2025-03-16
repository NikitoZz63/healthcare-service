package service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

public class TestMedicalServiceImpl {

    static Stream<Arguments> testSetForCheckBloodPressure() {
        return Stream.of(
                Arguments.of(1, "bf4ce936-3e91-430f-921d-b07c583fb544", new BloodPressure(200, 100), new PatientInfo("bf4ce936-3e91-430f-921d-b07c583fb544", "Иван", "Петров", LocalDate.of(1980, 11, 26),
                        new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)))),
                Arguments.of(0, "bf4ce936-3e91-430f-921d-b07c583fb544", new BloodPressure(120, 80), new PatientInfo("bf4ce936-3e91-430f-921d-b07c583fb544", "Иван", "Петров", LocalDate.of(1980, 11, 26),
                        new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80))))
        );
    }

    static Stream<Arguments> testSetForCheckTemperature() {
        return Stream.of(
                Arguments.of(1, "bf4ce936-3e91-430f-921d-b07c583fb544", new BigDecimal("33.65"), new PatientInfo("bf4ce936-3e91-430f-921d-b07c583fb544", "Иван", "Петров", LocalDate.of(1980, 11, 26),
                        new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)))),
                Arguments.of(0, "bf4ce936-3e91-430f-921d-b07c583fb544", new BigDecimal("36.65"), new PatientInfo("bf4ce936-3e91-430f-921d-b07c583fb544", "Иван", "Петров", LocalDate.of(1980, 11, 26),
                        new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80))))
        );
    }


    @ParameterizedTest
    @MethodSource("testSetForCheckBloodPressure")
    public void testCheckBloodPressure(int mockitoTimes, String id, BloodPressure bloodPressure, PatientInfo patientInfo) {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(id)).thenReturn(patientInfo);

        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

        medicalService.checkBloodPressure(id, bloodPressure);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(sendAlertService, Mockito.times(mockitoTimes)).send(Mockito.anyString());

    }

    @ParameterizedTest
    @MethodSource("testSetForCheckTemperature")
    public void testCheckTemperature(int mockitoTimes, String id, BigDecimal temperature, PatientInfo patientInfo) {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(id)).thenReturn(patientInfo);

        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

        medicalService.checkTemperature(id, temperature);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(sendAlertService, Mockito.times(mockitoTimes)).send(Mockito.anyString());


    }

    @Test
    public void testGetPatientInfo() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("bf4ce936-3e91-430f-921d-b07c583fb544")).thenReturn(new PatientInfo("bf4ce936-3e91-430f-921d-b07c583fb544", "Иван", "Петров", LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80))));

        PatientInfo actual = patientInfoRepository.getById("bf4ce936-3e91-430f-921d-b07c583fb544");

        PatientInfo expected = new PatientInfo("bf4ce936-3e91-430f-921d-b07c583fb544", "Иван", "Петров", LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetPatientInfoThrow() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("bf4ce936-3e91-430f-921d-b07c583fb534")).thenReturn(null);

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException("Patient not found");
        });

        Assertions.assertEquals("Patient not found", exception.getMessage());
    }

}
