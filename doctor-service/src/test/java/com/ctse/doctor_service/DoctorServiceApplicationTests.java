package com.ctse.doctor_service;

import com.ctse.doctor_service.dto.DoctorDto;
import com.ctse.doctor_service.model.Doctor;
import com.ctse.doctor_service.repository.DoctorRepository;
import com.ctse.doctor_service.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceApplicationTests {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor sampleDoctor;

    @BeforeEach
    void setUp() {
        sampleDoctor = new Doctor("Dr. Alice Smith", "Cardiology");
        sampleDoctor.setDoctorId("doctor-001");
    }

    @Test
    void testCreateDoctor_savesAndReturnsDto() {
        when(doctorRepository.save(any(Doctor.class))).thenReturn(sampleDoctor);

        DoctorDto result = doctorService.createDoctor(new DoctorDto(null, "Dr. Alice Smith", "Cardiology"));

        assertNotNull(result);
        assertEquals("Dr. Alice Smith", result.getName());
        assertEquals("Cardiology", result.getSpecialization());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void testGetAllDoctors_returnsListOfDtos() {
        Doctor doctor2 = new Doctor("Dr. Bob Jones", "Neurology");
        doctor2.setDoctorId("doctor-002");
        when(doctorRepository.findAll()).thenReturn(List.of(sampleDoctor, doctor2));

        List<DoctorDto> result = doctorService.getAllDoctors();

        assertEquals(2, result.size());
        assertEquals("Dr. Alice Smith", result.get(0).getName());
        assertEquals("Dr. Bob Jones", result.get(1).getName());
    }

    @Test
    void testGetDoctor_found_returnsDto() {
        when(doctorRepository.findById("doctor-001")).thenReturn(Optional.of(sampleDoctor));

        DoctorDto result = doctorService.getDoctor("doctor-001");

        assertNotNull(result);
        assertEquals("doctor-001", result.getDoctorId());
        assertEquals("Cardiology", result.getSpecialization());
    }

    @Test
    void testGetDoctor_notFound_throwsException() {
        when(doctorRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> doctorService.getDoctor("missing-id"));
    }

    @Test
    void testUpdateDoctor_updatesAndReturnsDto() {
        when(doctorRepository.findById("doctor-001")).thenReturn(Optional.of(sampleDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(sampleDoctor);

        DoctorDto updated = doctorService.updateDoctor("doctor-001", new DoctorDto(null, "Dr. Alice Updated", "Dermatology"));

        assertNotNull(updated);
        verify(doctorRepository, times(1)).save(sampleDoctor);
    }

    @Test
    void testDeleteDoctor_deletesSuccessfully() {
        when(doctorRepository.findById("doctor-001")).thenReturn(Optional.of(sampleDoctor));

        doctorService.deleteDoctor("doctor-001");

        verify(doctorRepository, times(1)).delete(sampleDoctor);
    }

    @Test
    void testDeleteDoctor_notFound_throwsException() {
        when(doctorRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> doctorService.deleteDoctor("bad-id"));
    }
}
